package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ReturnStatement</code> 定义了<strong><code>return</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>return</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>return</code> Statement:</i></b>
 * 	<strong><code>return</code></strong>(LeftHandSideExpression <strong><code>in</code></strong> Expression ) Statement
 * 	<strong><code>for</code></strong>(<strong><code>var</code></strong> VariableDeclarationNoIn <strong><code>in</code></strong> Expression ) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 67页 12.9.The <strong><code>return</code></strong> Statement
 */
public class ReturnStatement extends AbstractStatement {
	/** <strong><code>return</code></strong>关键字后的表达式 */
	public AbstractExpression expression = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 */
	public ReturnStatement(AbstractExpression expression) {
		this.expression = expression;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
	}

}
