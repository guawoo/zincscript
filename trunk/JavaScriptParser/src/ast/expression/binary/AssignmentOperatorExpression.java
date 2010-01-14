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
	public Token type = null;

	public AssignmentOperatorExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression, Token type) {
		super(leftExpression, rightExpression);
		this.type = type;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		super.release();
		type = null;
	}

}
