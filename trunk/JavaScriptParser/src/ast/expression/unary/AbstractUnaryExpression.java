package ast.expression.unary;

import ast.expression.AbstractExpression;

/**
 * <code>UnaryExpression</code> 是抽象的一元运算表达式
 * 
 * @author Jarod Yv
 */
public abstract class AbstractUnaryExpression extends AbstractExpression {
	/** 一元运算符后跟的表达式 */
	public AbstractExpression expression = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 */
	public AbstractUnaryExpression(AbstractExpression expression) {
		this.expression = expression;
	}

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
	}
}
