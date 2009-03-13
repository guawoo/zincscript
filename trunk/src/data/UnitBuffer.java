package data;

/**
 * <p>
 * <code>UnitBuffer</code>是{@link data.Unit}的缓存。
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public final class UnitBuffer {
	/** 缓存的最大长度 */
	public static final int BUFFER_SIZE = 5;

	/* 缓存的唯一实例 */
	private static UnitBuffer instance = null;

	/* DOM Tree缓存 */
	private Unit[] buffer = null;

	/* 当前索引 */
	private int index = -1;

	/* 用于监听DOM Tree变化的监听器 */
	private IDOMChangeListener domChangeListener = null;

	/*
	 * 构造函数
	 */
	private UnitBuffer() {
		buffer = new Unit[BUFFER_SIZE];
	}

	/**
	 * 获取DOMBuffer的唯一实例
	 * 
	 * @return DOMBuffer的唯一实例
	 */
	public static UnitBuffer getInstance() {
		if (instance == null)
			instance = new UnitBuffer();
		return instance;
	}

	/**
	 * 获取当前的缓存
	 * 
	 * @return 当前的缓存
	 */
	public Unit getCurrentBuffer() {
		if (index < 0 || index >= buffer.length)
			return null;
		return buffer[index];
	}

	/**
	 * 获取指定索引位置上的缓存
	 * 
	 * @param index
	 *            指定索引位置
	 * @return 索引位置上的缓存。如果没有则返回null
	 */
	public Unit getBufferByIndex(int index) {
		if (index >= 0 && index < BUFFER_SIZE)
			return buffer[index];
		return null;
	}

	/**
	 * 获取具有指定id的缓存
	 * 
	 * @param domid
	 *            指定ID
	 * @return 具有指定ID的缓存。如果没有则返回null
	 */
	public Unit getBufferByDOMID(String domid) {
		if (domid == null)
			return null;
		for (int i = 0; i < BUFFER_SIZE; i++) {
			if (buffer[i] != null) {
				if (domid.equals(buffer[i].getDOMID())) {
					index = i;
					return buffer[index];
				}
			}
		}
		return null;
	}

	/**
	 * 向缓存中加入一个缓存。如果缓存已满，会将最老的缓存清除。
	 * 
	 * @param root
	 *            要加入的缓存
	 */
	public void addBuffer(Unit root) {
		if (root == null)
			return;
		Unit currentUnit = getCurrentBuffer();// 当前界面
		if (currentUnit != null && currentUnit.equals(root)) {// 如果两个Unit定义的是同一个界面，则更新当前的Unit
			currentUnit.replace(root);
			currentUnit.needUpdateMenu = false;
			// DOMUtil.replaceSubtree(currentUnit.getDomTree(),
			// root.getDomTree());
		} else {
			if (index == BUFFER_SIZE - 1) {// 缓存已满
				// 释放最老的缓存
				buffer[0].release();
				buffer[0] = null;
				// 移动缓存
				for (int i = 0; i < BUFFER_SIZE - 1; i++) {
					buffer[i] = buffer[i + 1];
				}
				// 加入新的unit到缓存
				buffer[BUFFER_SIZE - 1] = root;
			} else {// 缓存未满
				index++;
				// 清除当前数组索引(包括)后的所有缓存
				for (int i = index; i < BUFFER_SIZE; i++) {
					if (buffer[i] != null) {
						buffer[i].release();
						buffer[i] = null;
					}
				}
				// 加入新的unit到缓存
				root.needUpdateMenu = true;
				buffer[index] = root;
			}
		}
		System.out.println("buffer size = " + index);
		domChangeListener.updateView();
	}

	public void prev() {

	}

	public void next() {

	}

	/**
	 * 获取当恰的索引
	 * 
	 * @return 当前索引
	 */
	public int getCurrentIndex() {
		return index;
	}

	/**
	 * @return the domChangeListener
	 */
	public IDOMChangeListener getDomChangeListener() {
		return domChangeListener;
	}

	/**
	 * @param domChangeListener
	 *            the domChangeListener to set
	 */
	public void setDomChangeListener(IDOMChangeListener domChangeListener) {
		this.domChangeListener = domChangeListener;
	}
}
