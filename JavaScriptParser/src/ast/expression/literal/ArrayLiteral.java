package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

public class ArrayLiteral extends AbstractLiteral {
	public AbstractExpression[] elements;

	public ArrayLiteral(AbstractExpression[] elements) {
		this.elements = elements;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

}