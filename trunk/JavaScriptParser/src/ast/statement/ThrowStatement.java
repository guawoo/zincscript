package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ThrowStatement</code> 定义了<strong><code>throw</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>throw</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * ThrowStatement:</i></b>
 * 	<strong><code>throw</code></strong> [no LineTerminator here] <em>Expression</em>;
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 69页 12.13.The <strong><code>throw</code></strong> statement
 */
public class ThrowStatement extends AbstractStatement {
	/** <strong><code>throw</code></strong>关键字后的表达式 */
	public AbstractExpression expression = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 */
	public ThrowStatement(AbstractExpression expression) {
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
