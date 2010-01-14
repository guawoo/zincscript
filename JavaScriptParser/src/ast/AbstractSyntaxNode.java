package ast;

import interpreter.AbstractInterpreter;

/**
 * <code>AbstractSyntaxNode</code> 是语法树上所有节点的抽象基类。
 * <p>
 * <code>AbstractSyntaxNode</code> 相当于Visitor模式中的抽象节点
 * 
 * @author Jarod Yv
 * @see {@link AbstractInterpreter}
 */
public abstract class AbstractSyntaxNode {
	/**
	 * 销毁该语法节点(子树), 释放资源
	 */
	public abstract void release();

	/**
	 * 释放一组节点
	 * 
	 * @param nodes
	 *            节点集合
	 */
	protected void release(AbstractSyntaxNode[] nodes) {
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				nodes[i].release();
				nodes[i] = null;
			}
		}
	}
}
