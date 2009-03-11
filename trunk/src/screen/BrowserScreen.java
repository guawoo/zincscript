package screen;

import java.io.IOException;
import java.io.InputStream;
import parser.wmlparser.SNSParser;
import zincscript.core.ZSException;
import zincscript.core.ZincScript;
import com.mediawoz.akebono.components.CSimpleImageBox;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.ui.UComponent;
import com.mediawoz.akebono.ui.UScreen;
import component.AbstractSNSComponent;
import component.ComponentFactory;
import component.SNSBodyComponent;
import component.menu.MCascadeMenuItem;
import component.menu.MenuContainer;
import config.Config;
import data.IDOMChangeListener;
import data.UnitBuffer;
import data.Unit;
import dom.AbstractDOM;
import dom.MenuItemDOM;

public class BrowserScreen extends UScreen implements EComponentEventListener,
		IDOMChangeListener {

	private static BrowserScreen instance = null;
	private SNSBodyComponent body = null;
	private SNSParser parse = null;
	private ZincScript zinc = null;
	private UnitBuffer buffer = null;
	private MenuContainer menu = null;

	public static BrowserScreen getInstance() {
		if (instance == null)
			instance = new BrowserScreen();
		return instance;
	}

	private BrowserScreen() {
		super(50);
		setBgColor(0xA1CF6D);
		CRImage bgImage = CRImage.loadFromResource("/img/butterfly.png");
		setBgImage(bgImage, BGPOSMODE_CENTER);
		bgImage = null;
		bgImage = CRImage.loadFromResource("/img/bg.png");
		CSimpleImageBox bg = new CSimpleImageBox(20, bgImage);
		bgImage = null;
		addComponent(bg);
		bg = null;
		initMenu();
		buffer = UnitBuffer.getInstance();
		buffer.setDomChangeListener(this);
		parse = SNSParser.getSNSParser();
		zinc = ZincScript.getZincScript();
		loadUnit("/widgets/diary/diary.xml");
	}

	private void initMenu() {
		// 添加背景图片组件
		CRImage imgTL = CRImage.loadFromResource("/menu/tl.png");
		CRImage imgBL = CRImage.loadFromResource("/menu/bl.png");
		CRImage imgTR = CRImage.loadFromResource("/menu/tr.png");
		CRImage imgBR = CRImage.loadFromResource("/menu/br.png");
		CRImage upTrig = CRImage.loadFromResource("/menu/up.png");
		CRImage downTrig = CRImage.loadFromResource("/menu/down.png");
		menu = new MenuContainer(0, 0, getWidth(), getHeight(), null, this);
		menu.setMenuImg(imgTL, imgBL, imgTR, imgBR, upTrig, downTrig);
		this.addComponent(menu);
		imgTL = null;
		imgBL = null;
		imgTR = null;
		imgBR = null;
		upTrig = null;
		downTrig = null;
	}

	public void loadUnit(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		try {
			parse.setInput(is, "UTF-8");
			parse.parse();
			UnitBuffer.getInstance().addBuffer(parse.getResult());
		} catch (Exception e) {
			System.out.println("parser exception");
			e.printStackTrace();
		} finally {
			parse.release();
			try {
				is.close();
			} catch (IOException e) {
			} finally {
				is = null;
			}
		}
		// Unit currentUnit = UnitBuffer.getInstance().getCurrentBuffer();
		// if (body != null) {
		// body.removeAllComponents();
		// this.removeComponent(body);
		// body = null;
		// }
		// body = (SNSBodyComponent) DOMTree2ViewTree(currentUnit.getDomTree());
		// body.doLayout(0, 0);
		// this.addComponent(body);
		// for (int i = 0; i < body.getComponentCount(); i++) {
		// UComponent c = body.componentAt(i);
		// System.out.println(c.iX + "  " + c.iY);
		// c = null;
		// }
	}

	private final AbstractSNSComponent DOMTree2ViewTree(AbstractDOM rootDOM) {
		if (rootDOM == null)
			return null;
		AbstractSNSComponent rootComponent = rootDOM.getComponent();
		if (rootComponent == null) {
			rootComponent = ComponentFactory.createComponent(rootDOM);
			rootComponent.setComponentEventListener(this);
		}
		rootComponent.removeAllComponents();
		if (rootComponent.hasChildren()) {
			for (int i = 0; i < rootDOM.children.size(); i++) {
				AbstractDOM dom = (AbstractDOM) rootDOM.children.get(i);
				AbstractSNSComponent component = dom.getComponent();
				if (component == null)
					component = DOMTree2ViewTree(dom);
				dom.setComponent(component);
				rootComponent.addComponent(component);
				component.init();
				component = null;
				dom = null;
			}
		}
		return rootComponent;
	}

	private static final CRImage token = CRImage
			.loadFromResource("/menu/pop.png");

	private MCascadeMenuItem generateMenu(MenuItemDOM menu) {
		if (menu == null)
			return null;
		MCascadeMenuItem item = new MCascadeMenuItem(menu.text, menu.onClick,
				null, null, Config.PLAIN_SMALL_FONT);
		if (menu.children != null && menu.children.size() > 0) {
			MCascadeMenuItem[] items = new MCascadeMenuItem[menu.children
					.size()];
			for (int i = 0; i < items.length; i++) {
				MenuItemDOM menuItemDOM = (MenuItemDOM) menu.children.get(i);
				items[i] = generateMenu(menuItemDOM);
				menuItemDOM = null;
			}
			item.initChildItem(items);
			item.setToken(token);
			items = null;
		}
		return item;
	}

	public void keyPressed(int iKeyCode) {
		if (menu.keyPressed(iKeyCode)) {
			return;
		}
		try {
			body.keyPressed(iKeyCode);
		} catch (Exception e) {
		}
	}

	public void componentEventFired(UComponent component, int eventID,
			Object paramObj, int param) {
		if (component instanceof AbstractSNSComponent) {
			AbstractSNSComponent com = (AbstractSNSComponent) component;
			if (eventID == EVENT_SEL_CLICKED) {
				String onclick = com.getDom().onClick;
				System.out.println("onclick = " + onclick);
				if (onclick != null) {
					try {
						zinc.callFunction(onclick);
					} catch (ZSException e) {
						e.printStackTrace();
					}
				}
				onclick = null;
			}
			com = null;
		} else if (component instanceof MenuContainer) {
			String onclick = (String) paramObj;
			System.out.println("menu onclick = " + onclick);
			if (onclick != null) {
				try {
					zinc.callFunction(onclick, null);
				} catch (ZSException e) {
					e.printStackTrace();
				}
			}
			onclick = null;
		}
	}

	public void updateView() {
		Unit unit = buffer.getCurrentBuffer();
		AbstractDOM root = unit.getDomTree();
		SNSBodyComponent newBody = (SNSBodyComponent) DOMTree2ViewTree(root);
		if (this.body != newBody) {
			if (this.body != null) {
				this.removeComponent(this.body);
				this.body.release();
				this.body = null;
			}
			this.body = newBody;
			this.addComponent(body);
		}
		root.setComponent(body);
		root = null;
		this.body.doLayout(0, 0);
		findFirstFocus(this.body);
		this.removeComponent(menu);
		if (unit.needUpdateMenu) {
			menu.setRoot(generateMenu(unit.getMenu()));
			if (menu.Root != null)
				menu.setPosition(0, getHeight());
		}
		this.addComponent(menu);
		// 响应onload
		if (unit.getOnLoad() != null) {
			try {
				zinc.callFunction(unit.getOnLoad(), null);
				unit.setOnLoad(null);
			} catch (ZSException e) {
				e.printStackTrace();
			}
		}
		unit = null;
	}

	private boolean findFirstFocus(AbstractSNSComponent root) {
		if (root.hasChildren()) {
			for (int i = 0; i < root.getComponentCount(); i++) {
				AbstractSNSComponent c = (AbstractSNSComponent) root
						.componentAt(i);
				if (findFirstFocus(c)) {
					c.setIndex(i);
					return true;
				}
				c = null;
			}
		} else {
			if (root.canFocus()) {
				root.setFocus(true);
				return true;
			}
		}
		return false;
	}
}
