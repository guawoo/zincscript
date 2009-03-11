package component;

import com.mediawoz.akebono.corefilter.CFMotion;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.filters.motion.FMShake;

import config.Config;

import dom.AbstractDOM;
import dom.ButtonDOM;

public class SNSButtonComponent extends AbstractSNSComponent {
	private static final int SPACE = 4;
	/* 按钮的背景 */
	private CRImage bgImage = null;
	/* 按钮选中后的高亮 */
	private CRImage bgImageHighLight = null;

	private String text = null;

	private CFMotion motion = null;

	public SNSButtonComponent(AbstractDOM dom) {
		super(dom);
	}

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
	}

	public int getNextX() {
		return iX + iWidth + SPACE;
	}

	public int getNextY() {
		return iY;
	}

	protected void drawCurrentFrame(CRGraphics g) {
		if (isFocused) {
			if (bgImageHighLight != null) {
				bgImageHighLight.draw(g, 0, 0, 20);
			}
		} else {
			if (bgImage != null) {
				bgImage.draw(g, 0, 0, 20);
			}
		}
		if (text != null) {
			Config.PLAIN_SMALL_FONT.drawString(g, text, iX, iY, 20);
		}
	}

	public void init() {
		ButtonDOM buttonDom = (ButtonDOM) this.dom;
		text = buttonDom.text;
		if (buttonDom.bgImage != null) {
			bgImage = CRImage.loadFromResource(buttonDom.bgImage + ".png");
			bgImageHighLight = CRImage.loadFromResource(buttonDom.bgImage
					+ "1.png");
			iWidth = bgImage.getWidth();
			iHeight = bgImage.getHeight();
		}
		if (text != null) {

		}
	}

	protected boolean animate() {
		if (motion != null) {
			if (motion.isFinished()) {
				detachAnimator(motion);
				motion = null;
			} else {
				iX = motion.getCurX();
			}
			return true;
		}
		return false;
	}

	public void setFocus(boolean isFocused) {
		if (isFocused) {
			motion = new FMShake(1, iX, iY, 5, 1, 0);
			attachAnimator(motion);
		} else {
			detachAnimator(motion);
			motion = null;
		}
		super.setFocus(isFocused);
	}

	public void setMotion(int startX, int startY) {
	}

	public void release() {
		bgImage = null;
		bgImageHighLight = null;
		text = null;
		motion = null;
	}

}