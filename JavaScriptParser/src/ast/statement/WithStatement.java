package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>WithStatement</code> 定义了<strong><code>with</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>with</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>with</code> Statement:</i></b>
 * 	<strong><code>with</code></strong> (<em>Expression</em>) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 67页 12.10.The <strong><code>with</code></strong> Statement
 */
public class WithStatement extends AbstractStatement {
	/** <strong><code>with</code></strong>关键字后的范围表达式 */
	public AbstractExpression expression = null;
	/** 条件为真时执行的语句 */
	public AbstractStatement statement = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param statement
	 *            {@link #statement}
	 */
	public WithStatement(AbstractExpression expression,
			AbstractStatement statement) {
		this.expression = expression;
		this.statement = statement;
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
		if (statement != null) {
			statement.release();
			statement = null;
		}
	}

}
