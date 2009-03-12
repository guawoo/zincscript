package ui.component;

import com.mediawoz.akebono.corerenderer.CRDisplay;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.ui.UComponent;

public class WheelLoadingComponent extends UComponent {

	private CRImage loading = null;
	private CRImage hourglass = null;

	private int offset;

	private int hourglassX = 0;

	private int loadingY = 0;

	private static WheelLoadingComponent instance = null;

	public static WheelLoadingComponent getInstance() {
		if (instance == null) {
			CRImage image = CRImage.loadFromResource("/img/hourglass.png");
			CRImage loading = CRImage.loadFromResource("/img/loading.png");
			instance = new WheelLoadingComponent(1,
					(CRDisplay.getWidth() - 64) / 2,
					(CRDisplay.getHeight() - 32) / 2, image, loading);
			image = null;
			loading = null;
		}
		return instance;
	}

	public WheelLoadingComponent(int animTickCount, int ix, int iy,
			CRImage img, CRImage load) {
		super(animTickCount, ix, iy, 64, 36);
		this.hourglass = img;
		this.loading = load;
		hourglassX = 24;
		loadingY = 20;
		offset = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mediawoz.akebono.ui.UComponent#drawCurrentFrame(com.mediawoz.akebono
	 * .corerenderer.CRGraphics)
	 */
	protected void drawCurrentFrame(CRGraphics g) {
		g.setClip(hourglassX, 0, 16, 16);
		hourglass.draw(g, hourglassX - offset, 0, 20);
		g.setClip(0, 0, CRDisplay.getWidth(), CRDisplay.getHeight());
		loading.draw(g, 0, loadingY, 20);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mediawoz.akebono.coreanimation.CAAnimator#animate()
	 */
	protected boolean animate() {
		offset += 16;
		if (offset >= 192)
			offset = 0;
		return true;
	}
}
