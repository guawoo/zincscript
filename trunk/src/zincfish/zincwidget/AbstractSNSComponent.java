package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.ui.IKeyInputHandler;
import com.mediawoz.akebono.ui.UPanel;

public abstract class AbstractSNSComponent extends UPanel implements
		IKeyInputHandler {

	/**
	 * 该组件对应的DOM对象
	 */
	protected AbstractDOM dom = null;

	public AbstractDOM getDom() {
		return dom;
	}

	public void setDom(AbstractDOM dom) {
		this.dom = dom;
	}

	/**
	 * 标识组件是否获得焦点
	 */
	protected boolean isFocused = false;

	/**
	 * 标识组件是否能够得到焦点
	 */
	protected boolean canFocus = true;

	protected int index = -1;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	protected int motionStartX = -1;
	protected int motionStartY = -1;

	public AbstractSNSComponent() {
		super(1, 0, 0, 0, 0);
	}

	/**
	 * 初始化组件
	 */
	public abstract void init(AbstractDOM dom);

	public abstract void doLayout(int startX, int startY);

	public abstract void setMotion(int startX, int startY);

	public AbstractSNSComponent getCurrentChild() {
		if (hasChildren()) {
			AbstractDOM child = (AbstractDOM) dom.children.get(index);
			return child.getComponent();
		} else {
			return null;
		}
	}

	/**
	 * 设置组件的焦点状态。不同组件获得焦点后会有不同的动作。
	 * 
	 * @param isFocused
	 */
	public void setFocus(boolean isFocused) {
		if (canFocus()) {// 设置焦点的前提是能够获得焦点
			this.isFocused = isFocused;
			if (this.isFocused) {
				cel.componentEventFired(this, 0, this.dom.onFocus, 0);// 响应onfocus事件
			} else {
				cel.componentEventFired(this, 1, this.dom.onLoseFocus, 0);// 响应onlosefocus事件
			}
		}
	}

	/**
	 * 判断组件是否能够获得焦点
	 * 
	 * @return
	 */
	public boolean canFocus() {
		return canFocus && this.dom.isAvailable;
	}

	public boolean hasChildren() {
		if (dom == null)
			System.out.println("DOM is NULL");
		return (dom.children != null && dom.children.size() > 0);
	}

	// private void switchFocus(int ikeycode) {
	// AbstractSNSComponent currentComonent = (AbstractSNSComponent)
	// componentAt(index);
	// int key = CSDevice.getGameAction(ikeycode);
	// switch (key) {
	// case CSDevice.KEY_DOWN:
	// index++;
	// if (index >= getComponentCount()) {
	// --index;
	// throw new IndexOutOfBoundsException();
	// }
	// break;
	// case CSDevice.KEY_UP:
	// index--;
	// if (index < 0) {
	// ++index;
	// throw new IndexOutOfBoundsException();
	// }
	// break;
	// }
	// if (currentComonent.hasChildren()) {
	// currentComonent.setFocus(false);
	// if (currentComonent.hasChildren()) {
	// currentComonent = (AbstractSNSComponent) currentComonent
	// .componentAt(currentComonent.index);
	// }
	// }
	// currentComonent.setFocus(false);
	// currentComonent = (AbstractSNSComponent) componentAt(index);
	// if (currentComonent.hasChildren()) {
	// currentComonent = (AbstractSNSComponent) currentComonent
	// .componentAt(currentComonent.index);
	// }
	// currentComonent.setFocus(true);
	// currentComonent = null;
	// }

	// public String toString() {
	// return dom.id;
	// }

	public void release(){
		this.dom.setComponent(null);
		this.dom = null;
	}
}
