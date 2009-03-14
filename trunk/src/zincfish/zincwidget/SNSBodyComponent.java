package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;

import com.mediawoz.akebono.corerenderer.CRDisplay;

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
		// iWidth = getContainingScreen().getWidth();
		int subX = 0, subY = 0;
		int lineHeight = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent component = (AbstractSNSComponent) componentAt(i);
			component.doLayout(subX, subY);
			int tmpX = component.getNextX();
			int tmpY = component.getNextY();
			if (tmpX > iWidth) {
				component.iX = 0;
				component.iY += lineHeight;
				tmpY = component.getNextY();
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

	public int getNextX() {
		return 0;
	}

	public int getNextY() {
		return iY + iHeight;
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

}
