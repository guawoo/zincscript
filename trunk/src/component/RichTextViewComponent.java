package component;

import utils.ArrayList;

import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.ui.UPanel;

import config.Config;

public class RichTextViewComponent extends UPanel {

	/** �ı���� */
	TextImageViewer textView;
	/** ͼƬ��ʾ���� */
	ImageViewCallBackImpl imageCallback;
	/** ��ʾ�Ķ��� */
	Object[] objects;

	/** Font �б� */
	CSList fontList = new CSList();
	/** Coverage Font �б� */
	CSList converageColorFontList = new CSList();
	/** Color �б� */
	CSList colorList = new CSList();

	/** ����ͼƬ */
	ArrayList imageList = new ArrayList();
	/** �ı������ĸ߶� */
	int length = 0;
	/** ����Ŀ�� */
	int width;

	/** ���캯�� */
	public RichTextViewComponent(int iX, int iY, int width, Object[] objects) {
		super(1, iX, iY, width, 0);
		if (objects == null || objects.length == 0) {
			return;
		}
		this.objects = objects;
		this.width = width;
		imageCallback = new ImageViewCallBackImpl(null);
		textView = new TextImageViewer(10, width, TextImageViewer.BORDER_TOP
				+ TextImageViewer.BORDER_BOTTOM, imageCallback);
		textView.iX = 0;
		textView.iY = 0;
		fontList.addElement(Config.PLAIN_SMALL_FONT);
		int[] color = { 0x292929 };
		colorList.addElement(color);
		parseObjects();
		length = textView.getContentTotalHeight() + (objects.length - 1)
				* textView.blockSpace + TextImageViewer.BORDER_TOP
				+ TextImageViewer.BORDER_BOTTOM;
		setSize(iWidth, length);
		textView.setSize(iWidth, length);
		addComponent(textView);
		// textView.setActive();
	}

	/** �����4�Ķ��� */
	public void parseObjects() {
		// ͼƬ����
		int imageIndex = 0;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof String) {
				if (objects[i] == null) {
					continue;
				}
				String textStr = (String) objects[i];
				if (textStr.length() > 0) {
					int[] coverage = { 0, textStr.length() };
					converageColorFontList = null;
					converageColorFontList = new CSList();
					converageColorFontList.addElement(coverage);

					textView.appendText(textStr, fontList,
							converageColorFontList, colorList,
							converageColorFontList);
				}
			}
			if (objects[i] instanceof CRImage) {
				if (objects[i] == null) {
					continue;
				}
				CRImage image = (CRImage) objects[i];
				textView.appendImage(imageIndex, image.getWidth(), image
						.getHeight(), 0, -1);
				imageList.add(image);
				imageIndex = imageIndex + 1;
			}
		}
		if (imageList.size() == 0) {
			return;
		}
		imageCallback.images = new CRImage[imageList.size()];
		for (int i = 0; i < imageList.size(); i++) {
			imageCallback.images[i] = (CRImage) imageList.get(i);
		}
	}
}
