package data;

/**
 * <code>DOMBuffer</code>是DOM Tree的缓存。
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public final class DOMBuffer {
	/**
	 * 缓存的最大长度
	 */
	public static final int BUFFER_SIZE = 5;

	/*
	 * 缓存的唯一实例
	 */
	private static DOMBuffer instance = null;

	/*
	 * DOM Tree缓存
	 */
	private Unit[] buffer = null;

	/*
	 * 当前索引
	 */
	private int index = -1;

	/*
	 * 构造函数
	 */
	private DOMBuffer() {
		buffer = new Unit[BUFFER_SIZE];
	}

	/**
	 * 获取DOMBuffer的唯一实例
	 * 
	 * @return DOMBuffer的唯一实例
	 */
	public static DOMBuffer getInstance() {
		if (instance == null)
			instance = new DOMBuffer();
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
	 * @param id
	 *            指定ID
	 * @return 具有指定ID的缓存。如果没有则返回null
	 */
	public Unit getBufferByID(String id) {
		if (id == null)
			return null;
		for (int i = 0; i < BUFFER_SIZE; i++) {
			if (buffer[i] != null) {
				if (id.equals(buffer[i].getId())) {
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
		Unit currentUnit = getCurrentBuffer();
		if (currentUnit != null && currentUnit.equals(root)) {
			currentUnit.replace(root);
			// DOMUtil.replaceSubtree(currentUnit.getDomTree(),
			// root.getDomTree());
		} else {
			if (index == BUFFER_SIZE - 1) {
				buffer[0].release();
				buffer[0] = null;
				for (int i = 0; i < BUFFER_SIZE - 1; i++) {
					buffer[i] = buffer[i + 1];
				}
				buffer[BUFFER_SIZE - 1] = root;
			} else {
				index++;
				for (int i = index; i < BUFFER_SIZE; i++) {
					if (buffer[i] != null) {
						buffer[i].release();
						buffer[i] = null;
					}
				}
				buffer[index] = root;
			}
		}
	}

	/**
	 * 获取当恰的索引
	 * 
	 * @return 当前索引
	 */
	public int getCurrentIndex() {
		return index;
	}
}
