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
	public Token type;

	public AssignmentOperatorExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression, Token type) {
		super(leftExpression, rightExpression);
		this.type = type;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
