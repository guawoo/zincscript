package ast.expression.binary;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>PropertyExpression</code>
 * 
 * @author Jarod Yv
 */
public class PropertyExpression extends AbstractBinaryExpression {

	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 */
	public PropertyExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

}
