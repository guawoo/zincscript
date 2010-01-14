package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>IfStatement</code> 定义了<strong><code>if</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>if</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>if</code> Statement:</i></b>
 * 	<strong><code>if</code></strong> (Expression) Statement <strong><code>else</code></strong> Statement
 * 	<strong><code>if</code></strong> (Expression) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 63页~64页 12.5.The <strong><code>if</code></strong> Statement
 */
public class IfStatement extends AbstractStatement {
	public AbstractExpression expression = null;
	public AbstractStatement trueStatement = null;
	public AbstractStatement falseStatement = null;

	public IfStatement(AbstractExpression expression,
			AbstractStatement trueStatement, AbstractStatement falseStatement) {
		this.expression = expression;
		this.trueStatement = trueStatement;
		this.falseStatement = falseStatement;
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
		if (trueStatement != null) {
			trueStatement.release();
			trueStatement = null;
		}
		if (falseStatement != null) {
			falseStatement.release();
			falseStatement = null;
		}
	}

}
