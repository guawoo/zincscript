package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import parser.Token;
import ast.expression.AbstractExpression;

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

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		super.release();
		operator = null;
	}

}
