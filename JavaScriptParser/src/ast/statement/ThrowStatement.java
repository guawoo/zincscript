package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ThrowStatement</code>
 * 
 * @author Jarod Yv
 */
public class ThrowStatement extends AbstractStatement {
	public AbstractExpression expression;

	public ThrowStatement(AbstractExpression expression) {
		this.expression = expression;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
