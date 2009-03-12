package utils;

import zincfish.zincdom.AbstractDOM;

/**
 * <code>DOMUtil</code> 是一个工具类，用于对DOM Tree进行操作。
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public final class DOMUtil {

	/**
	 * 在目标节点下添加子节点
	 * 
	 * @param target
	 *            目标节点
	 * @param subTree
	 *            要添加的子节点
	 * @since fingerling
	 */
	public static final void addSubtree(AbstractDOM target, AbstractDOM subTree) {
		if (target == null || subTree == null)
			return;
		if (target.children == null) {
			target.children = new ArrayList(10);
		}
		target.children.add(subTree);
		subTree.father = target;
	}

	/**
	 * 替换目标节点下的某个子节点
	 * 
	 * @param target
	 *            目标节点
	 * @param srcTree
	 *            被替换的节点
	 * @param subTree
	 *            替换的节点
	 */
	public static final void setSubtree(AbstractDOM target,
			AbstractDOM srcTree, AbstractDOM subTree) {
		if (target == null || subTree == null || srcTree == null)
			return;
		int index = getIndex(target, srcTree);
		if (index >= 0)
			target.children.set(index, subTree);
	}

	/**
	 * 获取目标节点下指定节点的索引
	 * 
	 * @param target
	 *            目标节点
	 * @param subTree
	 *            子节点
	 * @return 子节点在子节点队列中的索引。如果不存在则返回-1。
	 * @since fingerling
	 */
	public static final int getIndex(AbstractDOM target, AbstractDOM subTree) {
		if (target == null || subTree == null || target.children == null)
			return -1;
		return target.children.indexOf(subTree);
	}

	/**
	 * 替换目标树下的子树。本方法不单单是替换当前节点的子节点，而是会遍历整棵树，找到id相同的子树，完成替换。
	 * 
	 * @param target
	 *            遍历的根节点
	 * @param subTree
	 *            用于替换的子树
	 * @since fingerling
	 */
	public static final void replaceSubtree(AbstractDOM target,
			AbstractDOM subTree) {
		if (target == null || subTree == null)
			return;
		if (target.children != null) {
			for (int i = 0; i < target.children.size(); i++) {
				AbstractDOM child = (AbstractDOM) target.children.get(i);
				if (child.id.equals(subTree.id)) {
					subTree.father = child.father;
					setSubtree(target, child, subTree);
					child.release();
					child = null;
					return;
				} else {
					replaceSubtree(child, subTree);
				}
				child = null;
			}
		}
	}
}
