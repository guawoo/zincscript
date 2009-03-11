package component;

import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.ui.IKeyInputHandler;
import com.mediawoz.akebono.ui.UPanel;
import dom.AbstractDOM;

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

	protected int index = 0;

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

	public AbstractSNSComponent(AbstractDOM dom) {
		super(1, dom.x, dom.y, dom.w, dom.h);
		this.dom = dom;
	}

	/**
	 * 初始化组件
	 */
	public abstract void init();

	public abstract void doLayout(int startX, int startY);

	public abstract void setMotion(int startX, int startY);

	public abstract int getNextX();

	public abstract int getNextY();

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
		return (dom.children != null && dom.children.size() > 0);
	}

	private void switchFocus(int ikeycode) {
		AbstractSNSComponent currentComonent = (AbstractSNSComponent) componentAt(index);
		int key = CSDevice.getGameAction(ikeycode);
		switch (key) {
		case CSDevice.KEY_DOWN:
			index++;
			if (index >= getComponentCount()) {
				--index;
				throw new IndexOutOfBoundsException();
			}
			break;
		case CSDevice.KEY_UP:
			index--;
			if (index < 0) {
				++index;
				throw new IndexOutOfBoundsException();
			}
			break;
		}
		if (currentComonent.hasChildren()) {
			currentComonent.setFocus(false);
			if (currentComonent.hasChildren()) {
				currentComonent = (AbstractSNSComponent) currentComonent
						.componentAt(currentComonent.index);
			}
		}
		currentComonent.setFocus(false);
		currentComonent = (AbstractSNSComponent) componentAt(index);
		if (currentComonent.hasChildren()) {
			currentComonent = (AbstractSNSComponent) currentComonent
					.componentAt(currentComonent.index);
		}
		currentComonent.setFocus(true);
		currentComonent = null;
	}

	public String toString() {
		return dom.id;
	}

	public boolean keyPressed(int ikeycode) throws IndexOutOfBoundsException {
		AbstractSNSComponent currentComonent = (AbstractSNSComponent) componentAt(index);
		if (currentComonent.hasChildren()) {
			try {
				currentComonent.keyPressed(ikeycode);
			} catch (Exception e) {
				switchFocus(ikeycode);
			}
		} else {
			int key = CSDevice.getGameAction(ikeycode);
			if (key == CSDevice.KEY_FIRE) {
				cel.componentEventFired(currentComonent,
						EComponentEventListener.EVENT_SEL_CLICKED, null, 0);
			} else {
				switchFocus(ikeycode);
			}
		}

		currentComonent = null;

		return false;
	}

	public boolean keyReleased(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keyRepeated(int arg0) {
		// TODO Auto-generated method stub
		return false;
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
