package zincfish.zincparser.zmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import utils.StringUtil;
import zincfish.zincdom.*;
import zincfish.zincparser.xmlparser.ParserException;
import zincfish.zincparser.xmlparser.XmlParser;
import zincfish.zincscript.core.ZSException;
import zincfish.zincscript.core.ZincScript;
import data.Unit;

public final class ZMLParser {
	private Unit unit = null;
	private AbstractDOM root = null;
	private AbstractDOM currentDOM = null;
	private Hashtable attr = null;
	private MenuItemDOM menu = null;
	private byte align = AbstractDOM.ALIGN_LEFT;// horizontal align
	private XmlParser parser;// xml parser
	private boolean isRunning = false;

	private ZMLParser() {
		parser = new XmlParser();
		attr = new Hashtable(10);
	}

	/**
	 * get the instance of parser
	 * 
	 * @return WML parser
	 */
	public static final ZMLParser getSNSParser() {
		return new ZMLParser();
	}

	public final void parse() throws ParserException, IOException {
		isRunning = true;
		int eventType = parser.getEventType();
		// parse all tags
		while (eventType != XmlParser.END_DOCUMENT && isRunning) {
			// start tag event
			if (eventType == XmlParser.START_TAG) {
				parseStartTag();
			}
			// end tag event
			else if (eventType == XmlParser.END_TAG) {
				parseEndTag();
			}
			// text event
			else if (eventType == XmlParser.TEXT) {
				parseText();
			}
			// get next tag
			try {
				eventType = parser.next();
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
	}

	private final void parseStartTag() throws ParserException {
		// tag's name
		String currentTag = parser.getName();
		// number of attributes
		int attributeNum = parser.getAttributeNum();
		// get all attributes
		for (int i = 0; i < attributeNum; i++) {
			attr.put(parser.getAttributeName(i), parser.getAttributeValue(i));
		}
		if (ZMLTag.LIST_ITEM_TAG.equals(currentTag)) {
			parseListItem();
		} else if (ZMLTag.LIST_TAG.equals(currentTag)) {
			parseList();
		} else if (ZMLTag.MENU_ITEM_TAG.equals(currentTag)
				|| ZMLTag.MENU_TAG.equals(currentTag)) {
			parseMenuItem();
		} else if (ZMLTag.BUTTON_TAG.equals(currentTag)) {
			parseButton();
		} else if (ZMLTag.IMG_TAG.equals(currentTag)) {
			parseImage();
		} else if (ZMLTag.TEXT_FIELD_TAG.equals(currentTag)) {
			parseTextField();
		} else if (ZMLTag.TEXT_EDITOR_TAG.equals(currentTag)) {
			parseTextEditor();
		} else if (ZMLTag.BODY_TAG.equals(currentTag)) {
			parseBody();
		} else if (ZMLTag.UNIT_TAG.equals(currentTag)) {
			parseUnit();
		} else if (ZMLTag.DIV_TAG.equals(currentTag)) {
			parseDiv();
		} else if (ZMLTag.SCRIPT_TAG.equals(currentTag)) {
			parseScript();
		} else if (ZMLTag.BODY_TAG.equals(currentTag)) {
			parseBody();
		} else if (ZMLTag.RICH_TEXT_VIEWER_TAG.equals(currentTag)) {
			parseRichTextViewer();
		}

		String attributeValue = (String) attr.get(ZMLTag.ON_INIT_ATTR);
		if (attributeValue != null) {
			try {
				ZincScript.getZincScript().callFunction(attributeValue);
			} catch (ZSException e) {
				e.printStackTrace();
			}
		}

		attributeValue = null;
		currentTag = null;
		attr.clear();
	}

	private final void parseEndTag() {
		String currentTag = parser.getName();
		if (ZMLTag.MENU_TAG.equals(currentTag)
				|| ZMLTag.MENU_ITEM_TAG.equals(currentTag)) {
			currentDOM = currentDOM.father;
			if (currentDOM == null) {
				unit.setMenu(menu);
				menu = null;
			}
		} else {
			if (currentDOM != null && root != null)
				currentDOM = currentDOM.father == null ? root
						: currentDOM.father;
		}
		currentTag = null;
	}

	private final void parseText() throws ParserException {
		boolean isWhitespace = false;
		try {
			isWhitespace = parser.isWhitespace();
		} catch (ParserException ex) {
			System.out.println("error2:" + ex.getMessage());
		}
		if (!isWhitespace) {
			String text = parser.getText();
			PlainTextDOM dom = new PlainTextDOM();
			dom.text = text;
			add2DOMTree(dom);
			parseEndTag();
			dom = null;
			text = null;
		}
	}

	private void add2DOMTree(AbstractDOM dom) throws ParserException {
		dom.align = align;
		if (currentDOM != null) {
			utils.DOMUtil.addSubtree(currentDOM, dom);
			currentDOM = dom;
		} else {
			if (root == null) {
				root = dom;
				currentDOM = root;
				unit.setDomTree(root);
			} else {
				utils.DOMUtil.addSubtree(root, dom);
				currentDOM = dom;
			}
		}
	}

	private final void parseList() throws ParserException {
		ListDOM list = new ListDOM();
		handelGeneralAttributes(list);

		add2DOMTree(list);
		list = null;
	}

	private final void parseButton() throws ParserException {
		ButtonDOM dom = new ButtonDOM();
		handelGeneralAttributes(dom);

		dom.text = (String) attr.get(ZMLTag.TEXT_ATTR);
		dom.bgImage = (String) attr.get(ZMLTag.SRC_ATTR);

		add2DOMTree(dom);
		dom = null;
	}

	public final void parseImage() throws ParserException {
		ImageDOM dom = new ImageDOM();
		handelGeneralAttributes(dom);

		dom.src = (String) attr.get(ZMLTag.SRC_ATTR);
		dom.alt = (String) attr.get(ZMLTag.ALT_ATTR);

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseTextField() throws ParserException {
		TextFieldDOM dom = new TextFieldDOM();
		handelGeneralAttributes(dom);

		dom.label = (String) attr.get(ZMLTag.TITLE_ATTR);
		dom.name = (String) attr.get(ZMLTag.NAME_ATTR);
		dom.value = (String) attr.get(ZMLTag.VALUE_ATTR);
		dom.charType = StringUtil.Str2Int((String) attr.get(ZMLTag.TYPE_ATTR));

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseTextEditor() throws ParserException {
		TextEditorDOM dom = new TextEditorDOM();
		handelGeneralAttributes(dom);

		dom.label = (String) attr.get(ZMLTag.TITLE_ATTR);
		dom.name = (String) attr.get(ZMLTag.NAME_ATTR);
		dom.value = (String) attr.get(ZMLTag.VALUE_ATTR);

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseRichTextViewer() throws ParserException {
		RichTextViewerDOM dom = new RichTextViewerDOM();
		handelGeneralAttributes(dom);

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseBody() throws ParserException {
		root = new BodyDOM();
		currentDOM = root;
		root.id = (String) attr.get(ZMLTag.ID_ATTR);
		String onload = (String) attr.get(ZMLTag.ON_LOAD_ATTR);
		unit.setOnLoad(onload);
		onload = null;
		unit.setDomTree(root);
	}

	private final void parseMenuItem() throws ParserException {
		MenuItemDOM menuItem = new MenuItemDOM();
		menuItem.text = (String) attr.get(ZMLTag.TEXT_ATTR);
		menuItem.onClick = (String) attr.get(ZMLTag.ON_CLICK_ATTR);
		String s = (String) attr.get(ZMLTag.TYPE_ATTR);
		if (s != null)
			menuItem.menuType = StringUtil.Str2Int(s);
		s = null;
		if (menu == null) {
			menu = menuItem;
		} else {
			add2DOMTree(menuItem);
		}
		currentDOM = menuItem;
		menuItem = null;
	}

	private final void handelGeneralAttributes(AbstractDOM dom) {
		dom.id = (String) attr.get(ZMLTag.ID_ATTR);
		// 事件属性
		dom.onClick = (String) attr.get(ZMLTag.ON_CLICK_ATTR);
		dom.onFocus = (String) attr.get(ZMLTag.ON_FOCUS_ATTR);
		dom.onLoseFocus = (String) attr.get(ZMLTag.ON_LOSE_FOCUS_ATTR);
	}

	private final void parseListItem() throws ParserException {
		ListItemDOM listItem = new ListItemDOM();
		handelGeneralAttributes(listItem);

		listItem.title = (String) attr.get(ZMLTag.TITLE_ATTR);
		listItem.content = (String) attr.get(ZMLTag.CONTENT_ATTR);
		listItem.ltail1 = (String) attr.get(ZMLTag.LTAIL1_ATTR);
		listItem.ltail2 = (String) attr.get(ZMLTag.LTAIL2_ATTR);
		listItem.rtail = (String) attr.get(ZMLTag.RTAIL_ATTR);
		listItem.imageScr = (String) attr.get(ZMLTag.SRC_ATTR);
		listItem.cType = StringUtil.Str2Int((String) attr
				.get(ZMLTag.CTYPE_ATTR));
		String s = (String) attr.get(ZMLTag.LINE_ATTR);
		if (s != null)
			listItem.line = StringUtil.Str2Int(s);
		s = null;
		s = (String) attr.get(ZMLTag.ICON_ATTR);
		if (s != null)
			listItem.icon = StringUtil.Str2Int(s);
		s = null;

		add2DOMTree(listItem);
		listItem = null;
	}

	private final void parseUnit() {
		unit = new Unit();
	}

	private final void parseDiv() throws ParserException {
		DivDOM dom = new DivDOM();
		// id
		String attrValue = (String) attr.get(ZMLTag.ID_ATTR);
		dom.id = attrValue;
		attrValue = null;

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseScript() {
		// path
		String path = (String) attr.get(ZMLTag.PATH_ATTR);
		String name = (String) attr.get(ZMLTag.NAME_ATTR);
		if (unit == null)
			unit = new Unit();
		unit.setScriptPath(path + name);
		path = null;
		name = null;
	}

	/**
	 * set wml page stream
	 */
	public void setInput(InputStream is, String encode) throws ParserException {
		this.parser.setInput(is, encode);
	}

	public Unit getResult() {
		return unit;
	}

	public void release() {
		unit = null;
		root = null;
		currentDOM = null;
	}

	/**
	 * @return the currentDOM
	 */
	public AbstractDOM getCurrentDOM() {
		return currentDOM;
	}
}
