package ui.menu;

import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.ui.IKeyInputHandler;
import com.mediawoz.akebono.ui.UPanel;

public class MenuContainer extends UPanel implements IKeyInputHandler {
	/**
	 * �˵�������ݽṹ
	 */
	public MCascadeMenuItem Root = null;
	/**
	 * ��ǰ����Ľڵ�
	 */
	MCascadeMenuItem nowNode = null;
	// ����Բ��
	private CRImage imgTL = null;
	// ����Բ��
	private CRImage imgBL = null;
	// ����Բ��
	private CRImage imgTR = null;
	// ����Բ��
	private CRImage imgBR = null;
	/**
	 * �������ͼ��.
	 */
	private CRImage upTrig = null;
	/**
	 * �������ͼ��.
	 */
	private CRImage downTrig = null;
	/**
	 * �Ѿ��򿪲˵�
	 */
	private boolean isOpened = false;

	/**
	 * �˵�����
	 * 
	 * @param ix
	 *            ������x
	 * @param iy
	 *            ������y
	 * @param iwidth
	 *            �����
	 * @param iheight
	 *            �����
	 * @param root
	 *            ��ڵ�
	 * @param listener
	 *            ������ �ص�ӿ�
	 */
	public MenuContainer(int ix, int iy, int iwidth, int iheight,
			MCascadeMenuItem root, EComponentEventListener listener) {
		super(1, ix, iy, iwidth, iheight);
		setRoot(root);
		super.cel = listener;

	}

	public void setRoot(MCascadeMenuItem root) {
		this.Root = root;
		nowNode = Root;
	}

	/**
	 * �˵�
	 */
	private MCascadeMenu newmenu = null;

	/**
	 * �˵���ʾ�����
	 */
	private int menuMaxItem = 8;

	// /**
	// * �Ƿ���ʱ�ı�
	// */
	// private boolean isNeedMdy = false;

	/**
	 * ����һ�˵�
	 * 
	 * @param childItems
	 *            �˵���
	 * @return ��ݲ˵�������²˵�
	 */
	private MCascadeMenu createMenu(CSList ChildList) {
		newmenu = new MCascadeMenu(ChildList, imgTL, imgBL, imgTR, imgBR,
				upTrig, downTrig, menuMaxItem, menuX, menuY);
		return newmenu;
	}

	public boolean keyPressed(int iKeyCode) {
		boolean isAction = false;
		if (iKeyCode == CSDevice.NK_LSOFT && (!isOpened)) { // �����
			if (cel != null) {
				cel.componentEventFired(this, CSDevice.NK_LSOFT, null, -1);
			}
			open();
			isOpened = true; // �Ѿ��򿪲˵�
			isAction = true;
		} else if (iKeyCode == CSDevice.NK_RSOFT && (isOpened)) {
			isOpened = false;
			isAction = true;
			closeAll();
		} else if (isOpened) {
			isAction = true;
			if (iKeyCode == CSDevice.NK_LSOFT) {
				MCascadeMenuItem selectedItem = nowNode.getSubMenu()
						.getSelectedItem();
				if (selectedItem.getChildList() != null) { // ���Ӳ˵�
					selectedItem.setSubMenu(createMenu(selectedItem
							.getChildList()));
					addComponent(selectedItem.getSubMenu());
					nowNode.openSubMenu(getWidth());
					nowNode = selectedItem;
				}
				// else {
				// //������----��ȡ����
				// isAction = false;
				// }
			} else {
				int keyCode = CSDevice.getGameAction(iKeyCode);
				switch (keyCode) {
				case CSDevice.KEY_UP:
					nowNode.getSubMenu().goToPreItem();
					break;
				case CSDevice.KEY_DOWN:
					nowNode.getSubMenu().goToNextItem();
					break;
				case CSDevice.KEY_LEFT:
					if (nowNode.getFatherNode() != null) {
						nowNode.closeSubMenu();
					} else {
						nowNode.close();
						isOpened = false;
					}
					break;
				case CSDevice.KEY_RIGHT:
					MCascadeMenuItem selectedItem = nowNode.getSubMenu()
							.getSelectedItem();
					if (selectedItem.getChildList() != null
							&& selectedItem.getOperable()) { // ���Ӳ˵� &&
						// ѡ�в˵�����ɲ���
						selectedItem.setSubMenu(createMenu(selectedItem
								.getChildList()));
						addComponent(selectedItem.getSubMenu());
						nowNode.openSubMenu(getWidth());
						nowNode = selectedItem;
					}
					break;

				case CSDevice.KEY_FIRE:
					if (cel != null) {
						if (nowNode.getSubMenu().getSelectedItem()
								.getOperable()) { // �ɲ���
							closeAll();
							isOpened = false;
							cel.componentEventFired(this, CSDevice.KEY_FIRE,
									getCurSelecItem().getCmdString(), getCurSelecItem().getCmdCode()); // �׳�ǰѡ�в˵���Ĺ�����
						}
					}
					break;

				default:
					break;
				}
			}
		}
		return isAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.IKeyInputHandler#keyReleased(int)
	 */
	public boolean keyReleased(int iKeyCode) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.IKeyInputHandler#keyRepeated(int)
	 */
	public boolean keyRepeated(int iKeyCode) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.UPanel#animate()
	 */
	protected boolean animate() {
		boolean isAnimate = false;
		if (nowNode.getSubMenu() != null) {
			if (nowNode.getSubMenu().animate()) { // �Ӳ˵��Ѿ��ر�
				nowNode.setSubMenu(null);
				nowNode = nowNode.getFatherNode();
				isAnimate = true;
			}
		}
		return isAnimate;
	}

	/**
	 * �򿪲˵�--�򿪵�һ��
	 */
	private void open() {
		nowNode = Root;
		nowNode.setSubMenu(createMenu(nowNode.getChildList()));
		addComponent(nowNode.getSubMenu());
		nowNode.open();
	}

	/**
	 * �ر����еĲ˵�
	 */
	private void closeAll() {
		if (!nowNode.equals(Root)) {
			nowNode.getSubMenu().setVisible(false);
			nowNode.setSubMenu(null);
			nowNode = nowNode.getFatherNode();
			closeAll();
			return;
		}
		nowNode.close();
	}

	// ------------------------------------------------------------------//

	/**
	 * ����ָ��������Ĳ˵���
	 * 
	 * @param CmdCode
	 *            ָ���Ĺ�����
	 * @return ���ҵ��Ĳ˵��� ���� null
	 */
	public MCascadeMenuItem QueryItem(int CmdCode) {
		return Root.QueryItem(CmdCode);
	}

	/**
	 * ��ȡ��ǰѡ�в˵���
	 * 
	 * @return ��ǰѡ�в˵���
	 */
	public MCascadeMenuItem getCurSelecItem() {
		return nowNode.getSubMenu().getSelectedItem();
	}

	/**
	 * ���ò˵���ʾ���˵�����
	 * 
	 * @param maxItem
	 *            �˵���
	 */
	public void setMenuMaxItem(int maxItem) {
		this.menuMaxItem = maxItem;
		// isNeedMdy = true;
	}

	// /**
	// * ��ȡѡ�в˵��Ĺ�����
	// *
	// * @return ������
	// */
	// private int getCode() {
	// return nowNode.getSubMenu().getSelectedItem().getCmdCode();
	// }
	//
	// /**
	// * ���õ�ǰѡ�в˵������ʾ����
	// *
	// * @param name
	// * ��ʾ������
	// */
	// public void setCurSelecName(String name) {
	// nowNode.getSubMenu().getSelectedItem().setName(name);
	// }
	//
	// /**
	// * ���õ�ǰѡ�в˵���Ĺ�����
	// *
	// * @param cmdCode
	// * ������
	// */
	// public void setCurSelecItemCode(int cmdCode) {
	// nowNode.getSubMenu().getSelectedItem().setCmdCode(cmdCode);
	// }
	//
	// /**
	// * ���õ�ǰѡ�в˵����Ƿ�߿ɲ�����
	// *
	// * @param opearable
	// * �����б�� true �ɲ��� false ���ɲ���,����ѡ��
	// */
	// public void setCurSelecItemOperable(boolean opearable) {
	// nowNode.getSubMenu().getSelectedItem().setOperable(opearable);
	// }
	//
	// /**
	// * ��ȡ��ǰѡ�в˵���Ĳ�����
	// *
	// * @return �˵�������� true ��ǰѡ�в˵���ɲ��� false ��ǰ�˵���ɲ���
	// */
	//
	// public boolean getCurSelecItemOperable() {
	// return nowNode.getSubMenu().getSelectedItem().getOperable();
	// }

	// ------------------------------------------------------------------//

	/**
	 * һ���˵������x
	 */
	private int menuX;

	/**
	 * һ���˵������y
	 */
	private int menuY;

	/**
	 * ����һ���˵���λ��(���½����)
	 * 
	 * @param ix
	 *            �˵����x
	 * @param iy
	 *            �˵����y
	 */
	public void setPosition(int ix, int iy) {
		this.menuX = ix;
		int menuItem = Root.getChildList().size();
		if (menuItem > menuMaxItem) {
			menuItem = menuMaxItem;
		}
		this.menuY = iy - (22 * menuItem + 14) - 1;
	}

	/**
	 * ���ò˵�ƴ��ͼƬ
	 * 
	 * @param imgTL
	 *            ���Ͻ�
	 * @param imgBL
	 *            ���½�
	 * @param imgTR
	 *            ���Ͻ�
	 * @param imgBR
	 *            ���½�
	 * @param upTrig
	 *            �����
	 * @param downTrig
	 *            �����
	 */
	public void setMenuImg(CRImage imgTL, CRImage imgBL, CRImage imgTR,
			CRImage imgBR, CRImage upTrig, CRImage downTrig) {
		this.imgTL = imgTL;
		this.imgBL = imgBL;
		this.imgTR = imgTR;
		this.imgBR = imgBR;
		this.upTrig = upTrig;
		this.downTrig = downTrig;
	}
}
