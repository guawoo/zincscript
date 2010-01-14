package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ReturnStatement</code> 定义了<strong><code>return</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>return</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>for-in</code> Statement:</i></b>
 * 	<strong><code>for</code></strong>(LeftHandSideExpression <strong><code>in</code></strong> Expression ) Statement
 * 	<strong><code>for</code></strong>(<strong><code>var</code></strong> VariableDeclarationNoIn <strong><code>in</code></strong> Expression ) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 67页 12.9.The <strong><code>return</code></strong> Statement
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

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
	}

}
