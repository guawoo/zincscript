package ast.expression;

import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ConditionalExpression</code>
 * 
 * @author Jarod Yv
 */
public class ConditionalExpression extends AbstractExpression {
	/** 条件判断表达式 */
	public AbstractExpression expression = null;

	/** 条件为真时执行的表达式 */
	public AbstractExpression trueExpression = null;

	/** 条件为假时执行的表达式 */
	public AbstractExpression falseExpression = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param trueExpression
	 *            {@link #trueExpression}
	 * @param falseExpression
	 *            {@link #falseExpression}
	 */
	public ConditionalExpression(AbstractExpression expression,
			AbstractExpression trueExpression,
			AbstractExpression falseExpression) {
		this.expression = expression;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
		if (trueExpression != null) {
			trueExpression.release();
			trueExpression = null;
		}
		if (falseExpression != null) {
			falseExpression.release();
			falseExpression = null;
		}
	}

}
