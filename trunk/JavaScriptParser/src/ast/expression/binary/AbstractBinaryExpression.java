package ast.expression.binary;

import ast.expression.AbstractExpression;

/**
 * <code>AbstractBinaryExpression</code> 是抽象的二叉表达式树
 * 
 * @author Jarod Yv
 */
public abstract class AbstractBinaryExpression extends AbstractExpression {
	/** 左子树 */
	public AbstractExpression leftExpression = null;
	/** 右子树 */
	public AbstractExpression rightExpression = null;

	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 */
	public AbstractBinaryExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		if (leftExpression == null || rightExpression == null) {
			throw new NullPointerException();
		}
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
	}

	public void release() {
		if (leftExpression != null) {
			leftExpression.release();
			leftExpression = null;
		}
		if (rightExpression != null) {
			rightExpression.release();
			rightExpression = null;
		}
	}
}
