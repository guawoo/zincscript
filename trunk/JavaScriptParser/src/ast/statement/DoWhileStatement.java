package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>DoStatement</code> 定义了<strong><code>do-while</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>while</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i>do-while Statement:</i></b>
 * 	<strong><code>do</code></strong> Statement <strong><code>while</code></strong>( Expression );
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 64页 12.6.Iteration Statements
 */
public class DoWhileStatement extends AbstractStatement {
	/** do后面要循环执行的语句 */
	public AbstractStatement statement = null;
	/** while后面的条件表达式 */
	public AbstractExpression expression = null;

	/**
	 * 构造函数
	 * 
	 * @param statement
	 *            {@link #statement}
	 * @param expression
	 *            {@link #expression}
	 */
	public DoWhileStatement(AbstractStatement statement,
			AbstractExpression expression) {
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
