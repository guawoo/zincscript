package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ForInStatement</code> 定义了<strong>for...in...</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class ForInStatement extends AbstractStatement {
	public AbstractExpression variable;
	public AbstractExpression expression;
	public AbstractStatement statement;

	public ForInStatement(AbstractExpression variable,
			AbstractExpression expression, AbstractStatement statement) {
		this.variable = variable;
		this.expression = expression;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
