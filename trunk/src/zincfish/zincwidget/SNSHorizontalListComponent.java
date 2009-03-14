package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;

import com.mediawoz.akebono.ui.UComponent;
import com.mediawoz.akebono.ui.layout.UFlowLayout;

public class SNSHorizontalListComponent extends AbstractSNSComponent {

	private UFlowLayout layout = null;

	public void doLayout(int startX, int startY) {
		// TODO Auto-generated method stub

	}

	public int getNextX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNextY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addComponent(UComponent c) {
		super.addComponent(c);
		layout.add(c);
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
		layout = new UFlowLayout(UFlowLayout.FLOW_HORIZONTAL,
				UFlowLayout.ALIGN_LEFT | UFlowLayout.ALIGN_VCENTER, 4, 0);
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
