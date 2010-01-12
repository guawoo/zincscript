package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>WithStatement</code>
 * 
 * @author Jarod Yv
 */
public class WithStatement extends AbstractStatement {
	public AbstractExpression expression;
	public AbstractStatement statement;

	public WithStatement(AbstractExpression expression,
			AbstractStatement statement) {
		this.expression = expression;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
