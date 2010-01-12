package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>IfStatement</code> 定义了<strong>if</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class IfStatement extends AbstractStatement {
	public AbstractExpression expression;
	public AbstractStatement trueStatement;
	public AbstractStatement falseStatement;

	public IfStatement(AbstractExpression expression,
			AbstractStatement trueStatement, AbstractStatement falseStatement) {
		this.expression = expression;
		this.trueStatement = trueStatement;
		this.falseStatement = falseStatement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
