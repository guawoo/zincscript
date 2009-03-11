package zincscript.tree;

/**
 * 解析运算表达式的一种高效的做法是构建表达式树<br>
 * 表达式树节点，用于解析表达式
 */
public class ExpressionNode {

	public static final byte OPERATOR = 0;
	public static final byte VALUE = 1;

	/**
	 * 节点类型 0-运算符 1-数值
	 * 
	 * @see OPERATOR
	 * @see VALUE
	 */
	public byte type;

	/** 节点的值 */
	public Object value;

	/** 左子树 */
	public ExpressionNode left;

	/** 右子树 */
	public ExpressionNode right;

	/** 父节点 */
	public ExpressionNode parent;

	public String toString() {
		return new String("Type=" + type + " Value=" + value);
	}

	/**
	 * 向节点树中添加子节点。添加原则是如果左子树为空，则添加到左子树，如果不为空添加到右子树
	 * 
	 * @param child
	 *            子节点
	 */
	public void addChild(ExpressionNode child) {
		if (left == null) {
			left = child;
		} else if (right == null) {
			right = child;
		}
		child.parent = this;
	}
}
