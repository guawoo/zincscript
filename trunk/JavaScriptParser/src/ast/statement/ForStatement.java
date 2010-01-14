package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ForInStatement</code> 定义了<strong><code>for</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>for</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>for</code> Statement:</i></b>
 * 	<strong><code>for</code></strong>(ExpressionNoIn(opt); Expression(opt); Expression(opt)) Statement
 * 	<strong><code>for</code></strong>(<strong><code>var</code></strong> VariableDeclarationNoIn; Expression(opt); Expression(opt)) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 64页 12.6.Iteration Statements
 */
public class ForStatement extends AbstractStatement {
	public AbstractExpression initial = null;
	public AbstractExpression condition = null;
	public AbstractExpression increment = null;
	public AbstractStatement statement = null;

	public ForStatement(AbstractExpression initial,
			AbstractExpression condition, AbstractExpression increment,
			AbstractStatement statement) {
		this.initial = initial;
		this.condition = condition;
		this.increment = increment;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (initial != null) {
			initial.release();
			initial = null;
		}
		if (condition != null) {
			condition.release();
			condition = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
	}

}
