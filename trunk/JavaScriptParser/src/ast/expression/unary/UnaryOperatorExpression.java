package ast.expression.unary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import parser.Token;
import ast.expression.AbstractExpression;

/**
 * @author Jarod Yv
 * 
 */
public class UnaryOperatorExpression extends AbstractUnaryExpression {
	public Token operator = null;

	public UnaryOperatorExpression(AbstractExpression expression, Token operator) {
		super(expression);
		this.operator = operator;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		super.release();
		operator = null;
	}
}
