package component.menu;

import com.mediawoz.akebono.coreanimation.CAAnimator;
import com.mediawoz.akebono.corefilter.CFImageFilter;
import com.mediawoz.akebono.corefilter.CFMotion;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.corerenderer.fonts.CRSystemFont;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.events.EAnimationEventListener;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.filters.motion.FMLinear;
import com.mediawoz.akebono.ui.UComponent;
import com.mediawoz.akebono.ui.UPanel;

/**
 * This class construct and initialize a menu. User can use the open and close
 * method to open and close the a menu. In this class contain the private method
 * to operate the menu, including open and close the sub menu and go through the
 * item of the menu.
 * 
 * @author chenzhenjun
 * 
 */
public class MCascadeMenu extends UPanel implements EAnimationEventListener,
		EComponentEventListener {
	/**
	 * ����1��.
	 */
	public static final int THEME_LEFTRIGHT = 0;
	/**
	 * ����2��.
	 */
	public static final int THEME_UPDOWN = 1;
	/**
	 * �������.
	 */
	private int themeID = THEME_LEFTRIGHT;
	/**
	 * �˵�ѡ���б�.
	 */
	private CSList menuItemList = null;
	/**
	 * �˵���index.
	 */
	private int itemIndex = 0;
	/**
	 * ���ƿ�ʼ�˵���index.
	 */
	private int startIndex = 0;
	/**
	 * ���˵�ѡ������.
	 */
	private int maxItem = 8;
	/**
	 * �˵����򱳾�ͼ.
	 */
	private CRImage imgmid = null;
	/**
	 * ����margin.
	 */
	private int lrspace = 3;
	/**
	 * ����margin.
	 */
	private int udspace = 7;
	/**
	 * ѡ����.
	 */
	private MenuBar menuBar = null;
	/**
	 * ѡ�����.
	 */
	private int menuBarW = 86;
	/**
	 * ѡ�����.
	 */
	private int menuBarH = 22;
	/**
	 * ѡ������ʼλ��.
	 */
	private int menuBarX = lrspace;
	/**
	 * ѡ������ʼλ��.
	 */
	private int menuBarY = udspace;
	/**
	 * ѡ��������.
	 */
	private int menuBarIndex = 0;
	/**
	 * �Ӳ˵�������Ч.
	 */
	private FMLinear InOutMotion = null;
	/**
	 * ��Ч--���㵱ǰ���.
	 */
	private int curW = 0;
	/**
	 * ��Ч--���㵱ǰ�߶�.
	 */
	private int curH = 0;
	/**
	 * ʹ����Чʱ����Ƿ������Ч.
	 */
	private boolean isNeedClip = false;
	/**
	 * �˵�ѡ���������8��ʱ����Ҫ������ͼ��.
	 */
	private boolean isnavigate = false;
	/**
	 * ��ֲ˵��Ĺر��Լ���.
	 */
	public boolean isOpen = false;
	/**
	 * �˵�����Բ��
	 */
	private CRImage imgTL = null;
	/**
	 * �˵�����Բ��
	 */
	private CRImage imgBL = null;
	/**
	 * �˵�����Բ��
	 */
	private CRImage imgTR = null;
	/**
	 * �˵�����Բ��
	 */
	private CRImage imgBR = null;
	/**
	 * �˵�������ͼ.
	 */
	private CRImage imgtop = null;
	/**
	 * �˵�������ͼ.
	 */
	private CRImage imgbot = null;
	/**
	 * �������ͼ��.
	 */
	private CRImage upTrig = null;
	/**
	 * �������ͼ��.
	 */
	private CRImage downTrig = null;
	/**
	 * �˵�ԭʼλ��,x���.
	 */
	private int saveX = 0;
	/**
	 * �˵�ԭʼλ��,y���.
	 */
	private int saveY = 0;

	/**
	 * �˵�����չ�����
	 * 
	 * @param ChildItems
	 *            �˵���
	 * @param imgTL
	 *            ���ϱ���ͼ��
	 * @param imgBL
	 *            ���±���ͼ��
	 * @param imgTR
	 *            ���ϱ���ͼ��
	 * @param imgBR
	 *            ���±���ͼ��
	 * @param upTrig
	 *            ����ͼ��(��)
	 * @param downTrig
	 *            ����ͼ��(��)
	 * @param maxItem
	 *            ���˵���
	 * @param ix
	 *            �˵���λ��
	 * @param iy
	 *            �˵���λ��
	 */
	public MCascadeMenu(CSList ChildItems, CRImage imgTL, CRImage imgBL,
			CRImage imgTR, CRImage imgBR, CRImage upTrig, CRImage downTrig,
			int maxItem, int ix, int iy) {
		super(1, ix, iy, 92, 182); // 1 128
		this.imgTL = imgTL;
		this.imgBL = imgBL;
		this.imgTR = imgTR;
		this.imgBR = imgBR;
		this.upTrig = upTrig;
		this.downTrig = downTrig;
		this.maxItem = maxItem;
		initList(ChildItems);
		initBgImg();
		addItems();
		// ���ѡ����
		initBar();

		// for test
		saveX = iX;
		saveY = iY;
	}

	/**
	 * ��ʼ��ѡ����.
	 */
	private void initBar() {
		addMenuBar();
	}

	/**
	 * ��ʼ����������ѡ����.
	 */
	private void addMenuBar() {
		if (menuBar == null) {
			menuBarW = getWidth() - 6;
			menuBar = new MenuBar(1, menuBarX, menuBarY, menuBarW, menuBarH);
		}
		addComponent(menuBar);
	}

	/**
	 * ����Ѹ�Ĳ˵����ʼ���˵��б�.
	 * 
	 * @param ItemList
	 *            ��Ҫ��ʼ���Ĳ˵�������
	 */
	private void initList(CSList ChildItems) {
		this.menuItemList = ChildItems;
		if (maxItem < menuItemList.size()) {
			isnavigate = true;
		}
	}

	/**
	 * ��ʼ���˵��м䲿�ֱ���.
	 */
	private void initBgImg() {
		int maxW = 0;
		CRSystemFont font = new CRSystemFont(CRSystemFont.FACE_SYSTEM,
				CRSystemFont.STYLE_PLAIN, CRSystemFont.SIZE_SMALL);
		MCascadeMenuItem item = null;
		item = (MCascadeMenuItem) menuItemList.elementAt(0);

		maxW = font.stringWidth(item.getName());
		// find the max width of the item name in the item list

		int size = menuItemList.size();
		for (int i = 1; i < size; ++i) {
			item = (MCascadeMenuItem) menuItemList.elementAt(i);
			if (font.stringWidth(item.getName()) > maxW) {
				maxW = font.stringWidth(item.getName());
			}
		}

		// �ó����ѡ����Ŀ-->�����
		maxItem = menuItemList.size() > maxItem ? maxItem : menuItemList.size();
		int width = maxW + 15 + 12; // �����
		int textheight = maxItem * menuBarH + 2; // �м�߶�
		int tdbarheight = 6; // ���¼��
		int height = textheight + tdbarheight * 2; // �����
		setSize(width, height);
		imgtop = CFImageFilter.util_generateSolidColorImage(getWidth()
				- tdbarheight * 2, tdbarheight, 0x191919, 90);
		imgmid = CFImageFilter.util_generateSolidColorImage(getWidth(),
				getHeight() - tdbarheight * 2, 0x191919, 90);
		imgbot = imgtop;
	}

	/**
	 * ��ʼ���м䱳��ͼƬ
	 */
	// private void initMidImg() {
	// imgmid = CFImageFilter.util_generateSolidColorImage(getWidth(),
	// getHeight() - 12, 0x191919, 90);
	// }

	/**
	 * �Ѳ˵��б��еĲ˵���ӵ��˵�������.
	 */
	private void addItems() {
		int ix = lrspace;
		int iy = udspace;
		for (int i = 0; i < menuItemList.size(); ++i) {
			MCascadeMenuItem item = (MCascadeMenuItem) menuItemList
					.elementAt(i);
			item.iX = ix;
			item.iY = iy;
			item.setSize(getWidth() - 6, item.getHeight());
			addComponent(item);
			iy += menuBarH;
		}
		curW = 0;
		curH = getHeight();
		startIndex = 0;
	}

	/**
	 * ���˵�����.
	 * 
	 * @param crg
	 */
	private void drawBg(CRGraphics crg) {
		imgtop.draw(crg, 6, 0, CRGraphics.TOP | CRGraphics.LEFT);
		imgmid
				.draw(crg, 0, imgTL.getHeight(), CRGraphics.TOP
						| CRGraphics.LEFT);
		imgbot.draw(crg, 6, imgtop.getHeight() + imgmid.getHeight(),
				CRGraphics.TOP | CRGraphics.LEFT);
		// �����Ͻ�
		imgTL.draw(crg, 0, 0, CRGraphics.TOP | CRGraphics.LEFT);
		// �����½�
		imgBL.draw(crg, 0, getHeight() - imgBL.getHeight(), CRGraphics.TOP
				| CRGraphics.LEFT);
		// �����Ͻ�
		imgTR.draw(crg, getWidth() - imgTR.getWidth(), 0, CRGraphics.TOP
				| CRGraphics.LEFT);
		// �����½�
		imgBR.draw(crg, getWidth() - imgBR.getWidth(), getHeight()
				- imgBR.getHeight(), CRGraphics.TOP | CRGraphics.LEFT);
		crg.setColor(0x7c7c7c);
		// ����
		crg.drawLine(6, 0, getWidth() - 6, 0);
		crg.drawLine(6, getHeight() - 1, getWidth() - 6, getHeight() - 1);
		// ����
		crg.drawLine(0, 6, 0, imgmid.getHeight() + 6);
		crg.drawLine(getWidth() - 1, 6, getWidth() - 1, imgmid.getHeight() + 6);
	}

	// private boolean isOn;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.UPanel#drawCurrentFrame(com.mediawoz.akebono.corerenderer.CRGraphics)
	 */
	protected void drawCurrentFrame(CRGraphics g) {
		g.setClip(0, 0, getWidth(), getHeight());
		if (isNeedClip) {
			g.setClip(0, 0, curW, curH);
			drawBg(g);
			menuBar.paintCurrentFrame(g, menuBar.iX, menuBar.iY);
			g.setClip(0, udspace, curW, curH - 2 * udspace);
			MCascadeMenuItem item = null;
			for (int i = 0; i < menuItemList.size(); i++) {
				item = (MCascadeMenuItem) menuItemList.elementAt(i);
				item.paintCurrentFrame(g, item.iX, item.iY);
			}
		} else {
			drawBg(g);
			menuBar.paintCurrentFrame(g, menuBar.iX, menuBar.iY);
			MCascadeMenuItem item = null;
			for (int i = 0; i < menuItemList.size(); i++) {
				g.setClip(0, udspace, getWidth(), getHeight() - 2 * udspace);
				item = (MCascadeMenuItem) menuItemList.elementAt(i);
				item.paintCurrentFrame(g, item.iX, item.iY);
			}
		}
		/* �Ƿ�������µ��� */
		if (isnavigate) {
			g.setClip(0, getHeight() - udspace, getWidth(), udspace);
			upTrig.draw(g, (getWidth() - 12) / 2, getHeight() - udspace,
					CRGraphics.TOP | CRGraphics.LEFT);
			downTrig.draw(g, (getWidth() - 12) / 2 + 7, getHeight() - udspace,
					CRGraphics.TOP | CRGraphics.LEFT);
		}
	}

	/**
	 * �ƶ�ѡ����ǰһ��ѡ��.
	 */
	public void goToPreItem() {
		itemIndex = (itemIndex + menuItemList.size() - 1) % menuItemList.size();
		menuBarIndex = menuBarIndex == 0 ? 0 : --menuBarIndex;
		if (itemIndex < startIndex && itemIndex != menuItemList.size() - 1) { // ѡ������
			startIndex -= 1;
			upcount--;
			int maxfar = udspace - upcount * menuBarH;
			itemsMove(maxfar);
		} else if (itemIndex == menuItemList.size() - 1) { // ѡ������
			startIndex = itemIndex - maxItem + 1;
			menuBarIndex = maxItem - 1;
			// ȫ���ƶ�
			upcount += (menuItemList.size() - maxItem);
			int maxfar = udspace - upcount * menuBarH;
			itemsMove(maxfar);
		}
		menuBarMove(menuBarY + menuBarH * menuBarIndex);
	}

	/**
	 * ÿ�����ƶ�һ��menuBarH, upcount + 1 �����ƶ�һ��menuBarH, upcount - 1
	 */
	private int upcount = 0;

	/**
	 * �ƶ�ѡ������һ��ѡ��.
	 */
	public void goToNextItem() {
		itemIndex = (itemIndex + 1) % menuItemList.size();
		menuBarIndex = menuBarIndex == maxItem - 1 ? maxItem - 1
				: ++menuBarIndex;
		if ((itemIndex - startIndex) >= maxItem && itemIndex != 0) { // ѡ������
			startIndex += 1;
			upcount++;
			int maxfar = udspace - upcount * menuBarH;
			itemsMove(maxfar);
		} else if (itemIndex < startIndex && startIndex > 0) { // ѡ������
			startIndex = 0;
			menuBarIndex = 0;
			// ȫ���ƶ�
			upcount -= (menuItemList.size() - maxItem);
			int maxfar = udspace - upcount * menuBarH;
			itemsMove(maxfar);
		} else if (itemIndex == 0 && startIndex == 0) {
			menuBarIndex = 0;
		}
		menuBarMove(menuBarY + menuBarH * menuBarIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.UPanel#animate()
	 */
	protected boolean animate() {
		boolean isAnimate = false;
		if (InOutMotion != null) {
			if (InOutMotion.isFinished()) {
				detachAnimator(InOutMotion);
				isNeedClip = false;
				InOutMotion = null;
				if (!isOpen) {
					// menuGrade--; // �˵��������
					setVisible(false);
					// fatherMenu.childMenu = null;
					isAnimate = true;
				}
			} else {
				curW = InOutMotion.getCurX();
			}
		}
		return isAnimate;
	}

	/**
	 * ����ʱ����ѡ���ƶ�.
	 * 
	 * @param maxfar
	 *            ��0��ѡ��Ҫ�ƶ�Ҫ��λ��
	 */
	private void itemsMove(int maxfar) {
		int destination = maxfar;
		for (int i = 0; i < menuItemList.size(); ++i) {
			MCascadeMenuItem item = (MCascadeMenuItem) menuItemList
					.elementAt(i);
			item.move(destination);
			destination += menuBarH;
		}
	}

	/**
	 * ������һ���˵�.
	 */
	public void openMenu(int maxwidth) {
		MCascadeMenuItem item = (MCascadeMenuItem) menuItemList
				.elementAt(itemIndex);
		// ���Ӳ˵�
		if (item.getSubMenu() != null) {
			// �����Ӳ˵�λ��
			item.getSubMenu().iX = this.iX + getWidth() + 1;
			if (this.iX + getWidth() + 1 + item.getSubMenu().getWidth() > maxwidth) {
				item.getSubMenu().iX = maxwidth - item.getSubMenu().getWidth();
			}
			if (item.getAbsoluteY() + item.getSubMenu().getHeight() > this
					.getAbsoluteY()
					+ getHeight()) {
				item.getSubMenu().iY = this.getAbsoluteY() + getHeight()
						- item.getSubMenu().getHeight() - udspace;
			} else {
				item.getSubMenu().iY = item.getAbsoluteY();
			}
			item.getSubMenu().setVisible(true);
			item.getSubMenu().isOpen = true;
			item.getSubMenu().SetClip();
		}
	}

	/**
	 * �رղ˵�
	 */
	public void closeMenu() {
		isOpen = false;
		SetClip();
	}

	/**
	 * ������Ч������һ���˵�.
	 */
	private void SetClip() {
		isNeedClip = true;
		if (isOpen) {
			// ���Ӳ˵���Ч
			curW = 1;
			initMotion(curW, getHeight(), getWidth(), getHeight(), subInStep);
		} else {
			// �ر��Ӳ˵���Ч
			curW = getWidth();
			initMotion(getWidth(), getHeight(), 1, getHeight(), subOutStep);
		}
		attachAnimator(InOutMotion);
	}

	/**
	 * ��ȡ��ǰѡ�еĲ˵���
	 * 
	 * @return ѡ�еĲ˵���
	 */
	public MCascadeMenuItem getSelectedItem() {
		MCascadeMenuItem item = (MCascadeMenuItem) menuItemList
				.elementAt(itemIndex);
		return item;
	}

	/**
	 * �Ӳ˵�����Ч����
	 */
	private int subInStep = 6;
	/**
	 * �Ӳ˵��ر���Ч����
	 */
	private int subOutStep = 6;
	/**
	 * �Ӳ˵��رմ���Ч���ٶ�
	 */
	private double subInOutAccl = 0.3;

	/**
	 * �����Ӳ˵�����Ч����.������Ӳ˵�������.
	 * 
	 * @param step
	 *            ����
	 */
	public void setSubMenuInStep(int step) {
		this.subInStep = step;
	}

	/**
	 * �����Ӳ˵��ر���Ч����.������Ӳ˵�������.
	 * 
	 * @param step
	 *            ����
	 */
	public void setSubMenuOutStep(int step) {
		this.subOutStep = step;
	}

	/**
	 * �����Ӳ˵����Լ��ر���Ч���ٶ�.������Ӳ˵�������.
	 * 
	 * @param accl
	 *            ���ٶ�
	 */
	public void setSubMenuInOutAccl(double accl) {
		this.subInOutAccl = accl;
	}

	/**
	 * ��ʼ���Ӵ��Ӳ˵��Լ��ر��Ӳ˵���Ч.
	 * 
	 * @param startW
	 *            ��ʼ���
	 * @param startH
	 *            ��ʼ�߶�
	 * @param endW
	 *            ���տ��
	 * @param endH
	 *            ���ո߶�
	 * @param steps
	 *            �����Ч�Ĳ���
	 */
	private void initMotion(int startW, int startH, int endW, int endH,
			int steps) {
		if (InOutMotion != null) {
			InOutMotion = null;
		}
		InOutMotion = new FMLinear(1, FMLinear.CACCEL, startW, startH, endW,
				endH, steps, (endW - startW) * (float) subInOutAccl, 0f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.IKeyInputHandler#keyReleased(int)
	 */
	public boolean keyReleased(int arg0) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.IKeyInputHandler#keyRepeated(int)
	 */
	public boolean keyRepeated(int arg0) {
		return false;
	}

	/**
	 * ѡ������Ч.
	 */
	private FMLinear linear = null;

	/**
	 * ����ѡ������Ч.
	 * 
	 * @param destination
	 *            ѡ�����ƶ�����
	 */
	private void menuBarMove(int destination) {
		if (linear != null) {
			linear = null;
		}
		linear = new FMLinear(1, FMLinear.CACCEL, menuBar.iX, menuBar.iY,
				menuBar.iX, destination, 10, 0f,
				(destination - menuBar.iY) * 0.2f);
		linear.setAnimationEventListener(this);
		menuBar.setMotionFilter(linear);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.events.EAnimationEventListener#onFinish
	 *      (com.mediawoz.akebono.coreanimation.CAAnimator)
	 */
	public void onFinish(CAAnimator animator) {
		// һ���˵�Ч��
		if (animator == OCMotion) {
			if (!isOpen) {
				isOpen = true;
			} else {
				setVisible(false);
				isOpen = false;
			}
			OCMotion = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.events.EAnimationEventListener#onProgress
	 *      (com.mediawoz.akebono.coreanimation.CAAnimator)
	 */
	public void onProgress(CAAnimator arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.events.EAnimationEventListener#onStart
	 *      (com.mediawoz.akebono.coreanimation.CAAnimator)
	 */
	public void onStart(CAAnimator arg0) {
	}

	/**
	 * һ���˵���Ч(�����Լ����ϳ�).
	 */
	private CFMotion OCMotion = null;
	/**
	 * һ���˵����ҳ����Ч����
	 */
	private int switchInStep = 10;
	/**
	 * һ���˵����ҳ����Ч���ٶ�
	 */
	private double switchInAccl = 0.2;
	/**
	 * һ���˵��˳���Ч����
	 */
	private int switchOutStep = 10;
	/**
	 * һ���˵��˳���Ч���ٶ�
	 */
	private double switchOutAccl = 0.3;
	/**
	 * һ���˵��ϳ���Ч����
	 */
	private int aroseInStep = 9;
	/**
	 * һ���˵��ϳ���Ч���ٶ�
	 */
	private double aroseInAccl = 0.1;
	/**
	 * һ���˵������˳���Ч����
	 */
	private int aroseOutStep = 8;
	/**
	 * һ���˵������˳���Ч���ٶ�
	 */
	private double aroseOutAccl = 0.1;

	// /**
	// * һ���˵�������Ч.
	// */
	// private CFMotion POPMotion = null;
	// /**
	// * ������Ч����ͼƬ.
	// */
	// private EnhanceImage outImg = null;
	// /**
	// * ������Ч����ͼƬ.
	// */
	// private EnhanceImage inImg = null;
	/**
	 * �򿪲˵�����.
	 */
	public void open() {
		if (isOpen) {
			return;
		}
		switch (themeID) {
		// �ҳ�
		case THEME_LEFTRIGHT:
			setVisible(true);
			if (OCMotion != null) {
				OCMotion = null;
			}
			OCMotion = new FMLinear(1, FMLinear.CACCEL, -getWidth(), saveY, 1,
					saveY, switchInStep, (1 + getWidth())
							* (float) switchInAccl, 0f);
			OCMotion.setAnimationEventListener(this);
			setMotionFilter(OCMotion);
			break;
		// �ϳ�
		case THEME_UPDOWN:
			setVisible(true);
			if (OCMotion != null) {
				OCMotion = null;
			}
			OCMotion = new FMLinear(1, FMLinear.CACCEL, saveX, saveY
					+ getHeight(), saveX, saveY, aroseInStep, 0f,
					(float) aroseInAccl * (-getHeight()));
			OCMotion.setAnimationEventListener(this);
			setMotionFilter(OCMotion);
			break;
		// // ����
		// case 2:
		// setVisible(true);
		// isOn = true;
		// iX = saveX;
		// iY = saveY;
		// CRImage img = getCurrentFrameImage();
		// // setVisible(false);
		// inImg = new EnhanceImage(img, 0, 0, false, this);
		// POPMotion = new FMLinear(1, FMLinear.CACCEL, img.getWidth() / 8,
		// img.getHeight() / 8, img.getWidth(), img.getHeight(), 10,
		// 0.4f * (img.getWidth() - img.getWidth() / 8), 0.4f * (img
		// .getHeight() - img.getHeight() / 8));
		// inImg.changeSize(POPMotion);
		// addComponent(inImg);
		// break;
		}
	}

	// private boolean isOff;
	/**
	 * �رղ˵�����.
	 */
	public void close() {
		if (!isOpen && !isVisible()) {
			return;
		}
		switch (themeID) {
		case THEME_LEFTRIGHT:
			if (OCMotion != null) {
				OCMotion = null;
			}
			OCMotion = new FMLinear(1, FMLinear.CACCEL, iX, iY, -getWidth(),
					iY, switchOutStep, (-1 - getWidth())
							* (float) switchOutAccl, 0f);
			OCMotion.setAnimationEventListener(this);
			setMotionFilter(OCMotion);
			break;
		case THEME_UPDOWN:
			if (OCMotion != null) {
				OCMotion = null;
			}
			OCMotion = new FMLinear(1, FMLinear.CACCEL, iX, iY, iX, iY
					+ getHeight(), aroseOutStep, 0f, (getHeight())
					* (float) aroseOutAccl);
			OCMotion.setAnimationEventListener(this);
			setMotionFilter(OCMotion);
			break;
		// case 2:
		// isOff = true;
		// CRImage img = getCurrentFrameImage();
		// outImg = new EnhanceImage(img, 0, 0, false, this);
		// POPMotion = new FMLinear(1, FMLinear.CACCEL, img.getWidth(), img
		// .getHeight(), img.getWidth() / 8, img.getHeight() / 8, 8,
		// 0.4f * (img.getWidth() / 8 - img.getWidth()), 0.4f * (img
		// .getHeight() / 8 - img.getHeight()));
		// outImg.changeSize(POPMotion);
		// addComponent(outImg);
		// // setVisible(false);
		// break;
		}
	}

	/**
	 * ����һ���˵������г��,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param step
	 *            ����
	 */
	public void setSwitchInStep(int step) {
		this.switchInStep = step;
	}

	/**
	 * ����һ���˵������г���ٶ�,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param accl
	 *            ���ٶ�
	 */
	public void setSwithInAccl(double accl) {
		this.switchInAccl = accl;
	}

	/**
	 * ����һ���˵������ҳ��,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param step
	 *            ����
	 */
	public void setSwitchOutStep(int step) {
		this.switchOutStep = step;
	}

	/**
	 * ����һ���˵������ҳ�,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param accl
	 *            ���ٶ�
	 */
	public void setSwitchOutAccl(double accl) {
		this.switchOutAccl = accl;
	}

	/**
	 * ����һ���˵�������Ч����,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param step
	 *            ����
	 */
	public void setAroseInStep(int step) {
		this.aroseInStep = step;
	}

	/**
	 * ����һ���˵�������Ч���ٶ�,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param accl
	 *            ���ٶ�
	 */
	public void setAroseInAccl(double accl) {
		this.aroseInAccl = accl;
	}

	/**
	 * ����һ���˵������˳���Ч����,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param step
	 *            ����
	 */
	public void setAroseOutStep(int step) {
		this.aroseOutStep = step;
	}

	/**
	 * ����һ���˵������˳���ٶ�,�ڴ�һ���˵�ǰ����.
	 * 
	 * @param accl
	 *            ���ٶ�
	 */
	public void setAroseOutAccl(double accl) {
		this.aroseOutAccl = accl;
	}

	/**
	 * ��������. ���ⲿ�ṩ�Ľӿ�.
	 * 
	 * @param theme
	 */
	public void setTheme(int theme) {
		themeID = theme;
	}

	/**
	 * ��ȡ�˵������. ���ⲿ�ṩ�Ľӿ�.
	 * 
	 * @return ������
	 */
	public int getCmdCode() {
		MCascadeMenuItem item = (MCascadeMenuItem) menuItemList
				.elementAt(itemIndex);
		return item.getCmdCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.events.EComponentEventListener#componentEventFired
	 *      (com.mediawoz.akebono.ui.UComponent, int, java.lang.Object, int)
	 */
	public void componentEventFired(UComponent comp, int EventID,
			Object paramObj, int param) {
		// if (inImg == comp && EnhanceImage.TYPE_SIZE_CHANGE == EventID) {
		// inImg.setVisible(false);
		// setVisible(true);
		// POPMotion = null;
		// isOn = false;
		// }
		//
		// if (outImg == comp && EventID == EnhanceImage.TYPE_SIZE_CHANGE) {
		// outImg.setVisible(false);
		// POPMotion = null;
		// isOff = false;
		// }
	}
}
