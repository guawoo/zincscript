package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>SwitchStatement</code> 定义了<strong><code>switch</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>switch</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * SwitchStatement:</i></b>
 * 	<strong><code>switch</code></strong> (Expression) CaseBlock
 * </pre>
 * 
 * @author Jarod Yv
 * @see CaseStatement
 * @see ECMA-262 68页 12.11.The <strong><code>switch</code></strong> Statement
 */
public class SwitchStatement extends AbstractStatement {
	/** <strong><code>switch</code></strong>关键字后的表达式 */
	public AbstractExpression expression = null;
	
	/**
	 * <strong><code>switch</code></strong>语句包含的<strong><code>case</code>
	 * </strong>语句
	 */
	public CaseStatement[] cases = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param cases
	 *            {@link #cases}
	 */
	public SwitchStatement(AbstractExpression expression, CaseStatement[] cases) {
		this.expression = expression;
		this.cases = cases;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(cases);
		cases = null;
		if (expression != null) {
			expression.release();
			expression = null;
		}

	}

}
