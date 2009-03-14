package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import com.mediawoz.akebono.ui.UComponent;

public class SNSHorizontalListComponent extends AbstractSNSComponent {
	private static final int MARGIN = 4;
	private static final int SPACE = 5;

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
		iWidth = getContainingPanel().getWidth();
		int subX = 0, subY = MARGIN;
		for (int i = 0; i < getComponentCount(); i++) {
			AbstractSNSComponent c = (AbstractSNSComponent) componentAt(i);
			c.doLayout(subX, subY);
			subX += c.getWidth() + SPACE;
			iHeight = iHeight < c.getHeight() ? c.getHeight() : iHeight;
			c = null;
		}
		iHeight += MARGIN * 2;
	}

	public void addComponent(UComponent c) {
		super.addComponent(c);
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
		
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
