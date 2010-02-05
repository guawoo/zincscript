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
	/** <strong><code>while</code></strong>关键字后的条件表达式 */
	public AbstractExpression expression = null;
	/** 循环体 */
	public AbstractStatement statement = null;

	/**
	 * 构造函数
	 * @param expression {@link #expression}
	 * @param statement {@link #statement}
	 */
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
