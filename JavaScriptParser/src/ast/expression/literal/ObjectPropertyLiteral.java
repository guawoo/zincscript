package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ObjectPropertyLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ObjectPropertyLiteral extends AbstractLiteral {
	public AbstractExpression name;
	public AbstractExpression value;

	public ObjectPropertyLiteral(AbstractExpression name,
			AbstractExpression value) {
		this.name = name;
		this.value = value;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

}
