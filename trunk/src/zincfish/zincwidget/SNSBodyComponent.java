package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;

import com.mediawoz.akebono.corerenderer.CRDisplay;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;

public class SNSBodyComponent extends AbstractSNSComponent {

	public SNSBodyComponent() {
		super();
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
	}

	public void doLayout(int startX, int startY) {
		iX = startX;
		iY = startY;
		iWidth = CRDisplay.getWidth();
		int subX = 0, subY = 0;
		int lineHeight = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent component = (AbstractSNSComponent) componentAt(i);
			component.doLayout(subX, subY);
			int tmpX = component.iX + component.getWidth();
			int tmpY = component.iY;
			if (tmpX > iWidth) {
				tmpX = component.iX = 0;
				component.iY += lineHeight;
				tmpY = component.iY;
				lineHeight = component.getHeight();
			}
			if (tmpY == subY) {// 同一行
				lineHeight = lineHeight < component.getHeight() ? component
						.getHeight() : lineHeight;
			}
			subX = tmpX;
			subY = tmpY;
			component = null;
		}
		iHeight = CRDisplay.getHeight();
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
		super.release();
		System.gc();
	}

	public boolean keyPressed(int keyCode) {
		int keyAction = CSDevice.getGameAction(keyCode);
		switch (keyAction) {
		case CSDevice.KEY_DOWN:
		case CSDevice.KEY_RIGHT:
			index++;
			if (index >= getComponentCount()) {
				index = getComponentCount() - 1;
				cel.componentEventFired(this,
						EComponentEventListener.EVENT_SEL_EDGE, null, keyCode);
			} else {
				cel
						.componentEventFired(this,
								EComponentEventListener.EVENT_SEL_CHANGING,
								null, index);
			}
			break;
		case CSDevice.KEY_UP:
		case CSDevice.KEY_LEFT:
			index--;
			if (index < 0) {
				index = 0;
				cel.componentEventFired(this,
						EComponentEventListener.EVENT_SEL_EDGE, null, keyCode);
			} else {
				cel
						.componentEventFired(this,
								EComponentEventListener.EVENT_SEL_CHANGING,
								null, index);
			}
			break;
		default:
			return false;
		}
		return true;
	}

	public boolean keyReleased(int keycode) {
		return false;
	}

	public boolean keyRepeated(int keycode) {
		return false;
	}

}
