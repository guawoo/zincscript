package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ReturnStatement</code> 定义了<strong>return</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class ReturnStatement extends AbstractStatement {
	public AbstractExpression expression;

	public ReturnStatement(AbstractExpression expression) {
		this.expression = expression;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
