package ast.expression.binary;

import parser.Token;
import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>AssignmentOperatorExpression</code>
 * 
 * @author Jarod Yv
 */
public class AssignmentOperatorExpression extends AbstractBinaryExpression {
	/** 运算符 */
	public Token operator = null;

	/**
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 * @param operator
	 *            {@link #operator}
	 */
	public AssignmentOperatorExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression, Token operator) {
		super(leftExpression, rightExpression);
		this.operator = operator;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		super.release();
		operator = null;
	}

}
