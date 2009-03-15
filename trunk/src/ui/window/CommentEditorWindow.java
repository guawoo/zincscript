package ui.window;

import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGDirectTextBox;
import com.mediawoz.akebono.ui.ULayer;
import com.mediawoz.akebono.ui.UScreen;
import config.Config;

public class CommentEditorWindow extends ULayer {

	/** �ı������ */
	private FSGDirectTextBox textBox;

	/** ��ť: ûѡ��ʱ��ʾͼƬ */
	private CRImage[] selectImages;
	/** ��ť: ѡ��ʱ��ʾͼƬ */
	private CRImage[] okImages;
	/** Fire�������� */
	int[] iCmdCode;

	/** �۽����� */
	int index = 0;
	/** �ɾ۽��ĳ��� */
	int focusLength;

	/** ͼƬ֮��ļ�� */
	int imageSpace = 0;
	/** ��ȡ��ͼƬ����� */
	int siHeight;

	public CommentEditorWindow(int width, int height, CRImage[] selectImages,
			CRImage[] okImages, int[] iCmdCode) {
		super(1, 0, 0, width, height);
		if (selectImages == null || okImages == null || iCmdCode == null) {
			throw new IllegalArgumentException("��ťͼƬ����Ϊ��");
		}
		if (selectImages.length != okImages.length
				|| selectImages.length != iCmdCode.length) {
			throw new IllegalArgumentException(
					"ѡ��ͼƬ��û��ѡ�еĴ�СҪһ��, ������ͬ��");
		}
		this.selectImages = selectImages;
		this.okImages = okImages;
		this.iCmdCode = iCmdCode;
		setBgColor(0x99D1D3);
		textBox = new FSGDirectTextBox(200, width - 6, height - 3
				- getMaxHeightFromImage() - 2 * 12, "",
				Config.PLAIN_SMALL_FONT, 5000, FSGDirectTextBox.ANY, true, 0);
		textBox.iX = 3;
		textBox.iY = 3;
		textBox.setInputModeIndicatorTextColor(0x292929);
		textBox.setInputModeIndicatorBgColor(0xECECEC, 0xC0DAB0);
		textBox.setBgColor(0xFFFFFF);
		textBox.setBorder(0, 0xFFFFFF);
		textBox.enableTransparentBackground(true);
		addComponent(textBox);
		index = 0;
		focusLength = selectImages.length + 1;
		// ����ͼƬ֮��ļ��
		for (int i = 0; i < selectImages.length; i++) {
			imageSpace = imageSpace + selectImages[i].getWidth();
		}
		imageSpace = (getWidth() - imageSpace) / (selectImages.length + 1);
		siHeight = textBox.iY + textBox.getHeight();
	}

	/** ��ȡͼƬ�����߶� */
	public int getMaxHeightFromImage() {
		int height = 0;
		for (int i = 0; i < selectImages.length; i++) {
			if (height < selectImages[i].getHeight()) {
				height = selectImages[i].getHeight();
			}
		}
		for (int i = 0; i < okImages.length; i++) {
			if (height < okImages[i].getHeight()) {
				height = okImages[i].getHeight();
			}
		}
		return height;
	}

	/** ��ʾ�༭�� */
	public void showEditDialog(UScreen uscreen) {
		if (uscreen == null || isDismissed() == false) {
			return;
		}
		setEntryEffect(ULayer.ENTRY_FADE,
				new int[] { 0, 255, ULayer.ENTRY_FADE });
		setExitEffect(ULayer.EXIT_FADE, new int[] { 255, 0, ULayer.EXIT_FADE });
		show(uscreen, false, false);
		index = 0;
		changeEditStatus();
	}

	/** �رձ༭�� */
	public void closeEditDialog() {
		if (isDismissed() == false) {
			dismiss();
		}
	}

	/** �����ı��ɱ༭ */
	public void changeEditStatus() {
		if (index == 0) {
			textBox.setFocus(true);
			textBox.forceActivated(true);
		} else {
			textBox.setFocus(false);
			textBox.forceActivated(false);
		}
	}

	/** ���� */
	protected void drawCurrentFrame(CRGraphics crg) {
		super.drawCurrentFrame(crg);
		textBox.paintCurrentFrame(crg, textBox.iX, textBox.iY);
		for (int i = 0; i < selectImages.length; i++) {
			if (i == (index - 1)) {
				okImages[i].draw(crg, getStartX(i), siHeight
						+ (getHeight() - siHeight - okImages[i].getHeight())
						/ 2, CRGraphics.TOP | CRGraphics.LEFT);
			} else {
				selectImages[i].draw(crg, getStartX(i),
						siHeight
								+ (getHeight() - siHeight - selectImages[i]
										.getHeight()) / 2, CRGraphics.TOP
								| CRGraphics.LEFT);
			}
		}
	}

	/** ��ȡ��ͼƬ�ĺ���� */
	public int getStartX(int _index) {
		int startX = imageSpace;
		for (int i = 0; i < _index; i++) {
			if (i == (index - 1)) {
				startX = startX + okImages[i].getWidth() + imageSpace;
			} else {
				startX = startX + selectImages[i].getWidth() + imageSpace;
			}
		}
		return startX;
	}

	/** �༭���� */
	public void keyPress(int iKeyCode) {
		boolean b = false;
		if (index == 0) {
			b = textBox.keyPressed(iKeyCode);
		}
		if (!b) {
			iKeyCode = CSDevice.getGameAction(iKeyCode);
			if (iKeyCode == CSDevice.KEY_DOWN) {
				if (index < focusLength - 1) {
					index = index + 1;
				}
			}
			if (iKeyCode == CSDevice.KEY_UP) {
				if (index > 0) {
					index = index - 1;
				}
			}

			changeEditStatus();
		}
	}

	/** ��ȡ�༭���ı����� */
	public String getEditContent() {
		return textBox.getText();
	}

	/** ��ձ༭������ */
	public void clearEditContent() {
		textBox.setText("");
	}

	/**
	 * @see com.mediawoz.akebono.ui.ULayer#prepareBeforeShow()
	 */
	protected boolean prepareBeforeShow() {
		return false;
	}

	/**
	 * @see com.mediawoz.akebono.ui.ULayer#subclassActivated()
	 */
	protected void subclassActivated() {

	}

	/**
	 * @see com.mediawoz.akebono.ui.ULayer#subclassDeactivated()
	 */
	protected void subclassDeactivated() {

	}

	/**
	 * @see com.mediawoz.akebono.ui.ULayer#subclassDismissed()
	 */
	protected void subclassDismissed() {

	}

	/**
	 * @see com.mediawoz.akebono.ui.ULayer#subclassDrawCurrentFrame(com.mediawoz.akebono.corerenderer.CRGraphics)
	 */
	protected void subclassDrawCurrentFrame(CRGraphics crg) {

	}
}
