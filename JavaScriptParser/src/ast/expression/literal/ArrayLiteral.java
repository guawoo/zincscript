package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * @author Jarod Yv
 */
public class ArrayLiteral extends AbstractLiteral {
	public AbstractExpression[] elements = null;

	public ArrayLiteral(AbstractExpression[] elements) {
		this.elements = elements;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		release(elements);
		elements = null;
	}
}
