package ui.snscomponent;

import zincfish.zincdom.AbstractDOM;

import com.mediawoz.akebono.corerenderer.CRDisplay;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.ui.UComponent;


public class SNSListComponent extends AbstractSNSComponent {
	private static final int SPACE = 2;
	private static final int MARGIN = 4;

	public SNSListComponent(AbstractDOM dom) {
		super(dom);
	}

	public void init() {
	}

	protected void drawCurrentFrame(CRGraphics g) {
		for (int i = 0; i < getComponentCount(); i++) {
			UComponent component = componentAt(i);
			component.paintCurrentFrame(g, component.iX, component.iY);
			g.setColor(0x9DC469);
			g.drawLine(MARGIN,
					component.iY + component.getHeight() + SPACE - 1, iWidth
							- MARGIN, component.iY + component.getHeight()
							+ SPACE - 1);
			g.setColor(0xffffff);
			g.drawLine(MARGIN, component.iY + component.getHeight() + SPACE,
					iWidth - MARGIN, component.iY + component.getHeight()
							+ SPACE);
			component = null;
		}
	}

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY + MARGIN : dom.y;
		iWidth = dom.w == -1 ? (getContainingPanel() == null ? (getContainingScreen() == null ? CRDisplay
				.getWidth()
				: getContainingScreen().getWidth())
				: getContainingPanel().getWidth())
				: dom.w;
		int subX = 0, subY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent componet = (AbstractSNSComponent) componentAt(i);
			componet.doLayout(subX, subY);
			subX = componet.getNextX();
			subY = componet.getNextY() + SPACE * 2;
			componet = null;
		}
		this.iHeight = subY;
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent componet = (AbstractSNSComponent) componentAt(i);
			componet.setMotion(componet.iX, this.iHeight);
			componet = null;
		}
	}

	public int getNextX() {
		return 0;
	}

	public int getNextY() {
		return iY + getHeight();
	}


	public void setMotion(int startX, int startY) {
		// TODO Auto-generated method stub

	}
}
