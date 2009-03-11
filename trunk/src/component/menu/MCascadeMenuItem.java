package component.menu;

import com.mediawoz.akebono.corerenderer.CRFont;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.filters.motion.FMLinear;
import com.mediawoz.akebono.ui.UPanel;

/**
 * This class is the data structure of the menu. The data structure is a
 * tree.This class can not do anything except store the data of the menu.
 * 
 * @author chenzhenjun
 * 
 */
public class MCascadeMenuItem extends UPanel {

	private MCascadeMenuItem father = null;

	/**
	 * ��ǰ�˵������.
	 */
	private String itemname = null;
	/**
	 * �˵������.
	 */
	private int cmdCode = 0;

	private String cmdString = null;

	public String getCmdString() {
		return cmdString;
	}

	public void setCmdString(String cmdString) {
		this.cmdString = cmdString;
	}

	/**
	 * �Ӳ˵�// �����Ӳ˵�����±��򿪵�ʱ�Ž������
	 */
	private MCascadeMenu subMenu = null;
	/**
	 * ��������.
	 */
	private CRFont crfont = null;
	/**
	 * ������ɫ.
	 */
	private int textColor = 0xffffff;
	/**
	 * �Ӳ˵���ʶ.
	 */
	private CRImage token = null;

	/**
	 * �Ų˵��б�
	 */
	private CSList SubItems = null;

	/**
	 * �˵��ɲ�����
	 */
	private boolean Operable = true;

	/**
	 * �˵������
	 * 
	 * @param name
	 *            �˵�������
	 * @param cmdCode
	 *            �˵������
	 * @param items
	 *            �Ӳ˵���
	 * @param token
	 *            �Ӳ˵���ʶ
	 */
	public MCascadeMenuItem(String name, String cmdString,
			MCascadeMenuItem[] items, CRImage token, CRFont font) {
		super(1, 0, 0, 86, 22);
		this.itemname = name;
		// this.cmdCode = cmdCode;
		this.cmdString = cmdString;
		this.token = token;
		initChildItem(items);
		crfont = font;// new
		// CRSystemFont(CRSystemFont.FACE_SYSTEM,CRSystemFont.STYLE_PLAIN,
		// CRSystemFont.SIZE_SMALL);
	}

	public void setToken(CRImage token) {
		this.token = token;
	}

	/**
	 * ��ʼ���Ӳ˵�t��
	 * 
	 * @param items
	 *            ����Ӳ˵���
	 */
	public void initChildItem(MCascadeMenuItem[] items) {
		if (items != null) {
			SubItems = new CSList();
			for (int i = 0; i < items.length; i++) {
				items[i].setFatherNode(this);
				SubItems.addElement(items[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediawoz.akebono.ui.UPanel#drawCurrentFrame(com.mediawoz.akebono.
	 * corerenderer.CRGraphics)
	 */
	protected void drawCurrentFrame(CRGraphics g) {
		if (father != null) {
			g.setColor(textColor);
			crfont.drawString(g, itemname, 5,
					(getHeight() - crfont.getHeight()) / 2, CRGraphics.TOP
							| CRGraphics.LEFT);

			if (SubItems != null) {
				token.draw(g, getWidth() - token.getWidth() - 5,
						(getHeight() - token.getHeight()) / 2, CRGraphics.TOP
								| CRGraphics.LEFT);
			}

			if (isActive) {
				// ����
			}
		}
	}

	/**
	 * �˵����Ч��.
	 */
	private FMLinear scroll = null;

	/**
	 * ���ò˵������Ч.
	 * 
	 * @param destinationY
	 *            �����
	 */
	public void move(int destinationY) {
		if (scroll != null) {
			scroll = null;
		}
		scroll = new FMLinear(1, FMLinear.CACCEL, iX, iY, iX, destinationY, 6,
				0f, 0.25f);
		setMotionFilter(scroll);
	}

	/**
	 * ��ȡ������.
	 * 
	 * @return ������
	 */
	public int getCmdCode() {
		return cmdCode;
	}

	/**
	 * ���ù�����
	 * 
	 * @param cmdCode
	 */
	public void setCmdCode(int cmdCode) {
		this.cmdCode = cmdCode;
	}

	/**
	 * ���ò˵������ʾ����
	 * 
	 * @param name
	 *            ��ʾ����
	 */
	public void setName(String name) {
		this.itemname = name;
	}

	/**
	 * ��ȡ�˵��������
	 * 
	 * @return �˵��������
	 */
	public String getName() {
		return this.itemname;
	}

	/**
	 * ���ò˵����Ƿ�߲�����
	 * 
	 * @param operate
	 *            ������ true for ʹ�˵���ɲ��� false for ʹ�˵����߲�����,����ѡ��
	 */
	public void setOperable(boolean operate) {
		this.Operable = operate;
		if (Operable) { // �ɲ���
			textColor = 0xFFFFFF; // ��ɫ
		} else { // ���ɲ���
			textColor = 0x666666; // ��ɫ
		}
	}

	/**
	 * ��ȡ�˵��������
	 * 
	 * @return �˵������Ա�� false or true
	 */
	public boolean getOperable() {
		return this.Operable;
	}

	/**
	 * ��ȡ�Ӳ˵���t��
	 * 
	 * @return �Ӳ˵���t��
	 */
	public CSList getChildList() {
		return SubItems;
	}

	/**
	 * �����Ӳ˵�
	 * 
	 * @param subMemu
	 *            �Ӳ˵�
	 */
	public void setSubMenu(MCascadeMenu subMemu) {
		this.subMenu = subMemu;
	}

	/**
	 * ��ȡ�Ӳ˵�
	 * 
	 * @return �Ӳ˵�
	 */
	public MCascadeMenu getSubMenu() {
		return this.subMenu;
	}

	/**
	 * ����Ӳ˵��ָ����λ����
	 * 
	 * @param Child
	 *            ��Ҫ��ӵ��Ӳ˵���
	 * @param index
	 *            ����Ӳ˵���Ӧ��λ��
	 */
	public void addChildItems(MCascadeMenuItem[] Child, int[] index) {
		if (SubItems != null) {
			for (int i = 0; i < Child.length; ++i) {
				SubItems.insertElementAt(Child[i], index[i]);
			}
		} else {
			SubItems = new CSList();
			for (int i = 0; i < Child.length; ++i) {
				SubItems.addElement(Child[i]);
			}
		}
	}

	/**
	 * ɾ��ָ��index�ϵ��Ӳ˵���
	 * 
	 * @param index
	 *            ��Ҫɾ����Ӳ˵�������
	 */
	public void delChildItem(int index) {
		if (SubItems != null) {
			SubItems.removeElementAt(index);
		}
	}

	/**
	 * ɾ��ָ���Ĳ˵���
	 * 
	 * @param item
	 *            ��Ҫɾ��Ĳ˵���
	 */
	public void delChildItem(MCascadeMenuItem item) {
		if (SubItems != null) {
			SubItems.removeElement(item);
		}
	}

	/**
	 * ���ø��׽ڵ�
	 * 
	 * @param father
	 *            ���׽ڵ�
	 */
	private void setFatherNode(MCascadeMenuItem father) {
		this.father = father;
	}

	/**
	 * ��ȡ���׽ڵ�
	 * 
	 * @return ���׽ڵ�
	 */
	public MCascadeMenuItem getFatherNode() {
		return this.father;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.UPanel#animate()
	 */
	protected boolean animate() {
		boolean isAnimate = false;
		if (scroll != null) {
			if (scroll.isFinished()) {
				scroll = null;
				isAnimate = true;
			}
		}
		return isAnimate;
	}

	/**
	 * ���Ӳ˵�
	 * 
	 * @param maxwidth
	 *            �Ӳ˵�����ʾ��Χ�ڵ������
	 */
	public void openSubMenu(int maxwidth) {
		subMenu.openMenu(maxwidth);
	}

	/**
	 * �ر��Ӳ˵�
	 */
	public void closeSubMenu() {
		subMenu.closeMenu();
	}

	/**
	 * ��һ���˵�--->Ϊ��Ч����
	 */
	public void open() {
		subMenu.open();
	}

	/**
	 * �ر��Ӳ˵���Ч-->Ϊ��Ч����
	 */
	public void close() {
		subMenu.close();
	}

	/**
	 * �Ƿ񼤻�
	 */
	private boolean isActive = false;

	public void setMark(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * ��ѯ����
	 */
	private CSList searchQueue = null;

	/**
	 * ���Ҷ�Ӧ������Ĳ˵���
	 * 
	 * @param CmdCode
	 *            ָ��������
	 * @return ���ҵ��Ĳ˵��� ���� null
	 */
	public MCascadeMenuItem QueryItem(int CmdCode) {
		searchQueue = new CSList();
		searchQueue.addElement(this);
		while (searchQueue.size() > 0) {
			MCascadeMenuItem curItem = (MCascadeMenuItem) searchQueue
					.elementAt(0); // ��ȡ��ѯ���еĵ�һ��Ԫ��
			searchQueue.removeElementAt(0); // ɾ���ѯ�����еĵ�һ��Ԫ��
			if (CmdCode == curItem.getCmdCode()) { // �ҵ���Ӧ�Ĳ˵���
				searchQueue = null;
				return curItem;
			} else {
				MCascadeMenuItem addItem = null;
				if (curItem.getChildList() != null) {
					for (int i = 0; i < curItem.getChildList().size(); ++i) {
						addItem = (MCascadeMenuItem) curItem.getChildList()
								.elementAt(i);
						searchQueue.addElement(addItem);
					}
				}
			}
		}
		return null;

	}
}
