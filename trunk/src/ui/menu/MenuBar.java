package ui.menu;

import com.mediawoz.akebono.corefilter.CFImageFilter;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.ui.UComponent;

/**
 * ѡ��������.
 */
public class MenuBar extends UComponent {

	/**
	 * ѡ����ͼƬ,�����.
	 */
	private CRImage barImg;

	/**
	 * ����ѡ����
	 * 
	 * @param animTickCount
	 *            ͬ��Ƶ��
	 * @param ix
	 *            ��ʼ���x
	 * @param iy
	 *            ��ʼ���y
	 * @param width
	 *            ѡ�����
	 * @param height
	 *            ѡ�����
	 */
	public MenuBar(int animTickCount, int ix, int iy, int width, int height) {
		super(animTickCount, ix, iy, width, height);
		init();
	}

	/**
	 * ��ʼ��ѡ����.
	 */
	private void init() {
		// ���͸���
		barImg = CFImageFilter.util_generateSolidColorImage(iWidth, iHeight,
				0xffffff, 20);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.ui.UComponent#drawCurrentFrame
	 *      (com.mediawoz.akebono.corerenderer.CRGraphics)
	 */
	protected void drawCurrentFrame(CRGraphics g) {
		barImg.draw(g, 0, 0, CRGraphics.TOP | CRGraphics.LEFT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.coreanimation.CAAnimator#animate()
	 */
	protected boolean animate() {
		return true;
	}

}
