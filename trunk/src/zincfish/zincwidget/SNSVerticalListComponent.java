package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;

import com.mediawoz.akebono.corerenderer.CRDisplay;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.ui.UComponent;

public class SNSVerticalListComponent extends AbstractSNSComponent {
	private static final int SPACE = 2;
	private static final int MARGIN = 4;

	public void init(AbstractDOM dom) {
		this.dom = dom;
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

	public void addComponent(UComponent c) {
		super.addComponent(c);
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
			AbstractSNSComponent c = (AbstractSNSComponent) componentAt(i);
			c.doLayout(subX, subY);
			subY = c.iY + c.getHeight() + SPACE * 2;
			c = null;
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

	public void release() {
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent c = (AbstractSNSComponent) componentAt(i);
			c.release();
			c = null;
		}
		this.dom = null;
		System.gc();
	}

	public boolean keyPressed(int keyCode) {
		int keyAction = CSDevice.getGameAction(keyCode);
		switch (keyAction) {
		case CSDevice.KEY_DOWN:
		case CSDevice.KEY_RIGHT:
			index++;
			break;
		case CSDevice.KEY_UP:
		case CSDevice.KEY_LEFT:
			index--;
		default:
			break;
		}
		if (index < 0 || index >= getComponentCount()) {
			cel.componentEventFired(this,
					EComponentEventListener.EVENT_SEL_EDGE, null, keyCode);
		} else {
			cel.componentEventFired(this,
					EComponentEventListener.EVENT_SEL_CHANGING, null, index);
		}
		return false;
	}

	public boolean keyReleased(int arg0) {
		return false;
	}

	public boolean keyRepeated(int arg0) {
		return false;
	}
}
