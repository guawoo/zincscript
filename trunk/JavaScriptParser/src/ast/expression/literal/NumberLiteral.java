package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>NumberLiteral</code>
 * 
 * @author Jarod Yv
 */
public class NumberLiteral extends AbstractLiteral {
	public double value = 0.0;
	public int index = 0;

	public NumberLiteral(double value) {
		this.value = value;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
	}

}
