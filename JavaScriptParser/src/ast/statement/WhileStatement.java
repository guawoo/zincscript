package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>WhileStatement</code> 定义了<strong><code>while</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>while</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>while</code> Statement:</i></b>
 * 	<strong><code>while</code></strong>(<em>Expression</em>) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 64页 12.6.Iteration Statements
 */
public class WhileStatement extends AbstractStatement {

	public AbstractExpression expression = null;
	public AbstractStatement statement = null;

	public WhileStatement(AbstractExpression expression,
			AbstractStatement statement) {
		this.expression = expression;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
	}

}
