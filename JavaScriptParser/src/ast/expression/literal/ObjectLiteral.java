package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ObjectLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ObjectLiteral extends AbstractLiteral {
	public ObjectPropertyLiteral[] properties;

	public ObjectLiteral(ObjectPropertyLiteral[] properties) {
		this.properties = properties;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}
}
