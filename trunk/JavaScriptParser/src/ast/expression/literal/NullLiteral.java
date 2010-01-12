package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>NullLiteral</code>
 * 
 * @author Jarod Yv
 */
public class NullLiteral extends AbstractLiteral {
	public NullLiteral() {
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
