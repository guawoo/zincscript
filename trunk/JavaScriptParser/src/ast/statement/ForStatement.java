package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ForStatement</code> 定义了<strong>for(,,)</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class ForStatement extends AbstractStatement {
	public AbstractExpression initial;
	public AbstractExpression condition;
	public AbstractExpression increment;
	public AbstractStatement statement;

	public ForStatement(AbstractExpression initial,
			AbstractExpression condition, AbstractExpression increment,
			AbstractStatement statement) {
		this.initial = initial;
		this.condition = condition;
		this.increment = increment;
		this.statement = statement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.statement.AbstractStatement#analyseStatement(analyzer.AbstractAnalyzer
	 * )
	 */
	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
