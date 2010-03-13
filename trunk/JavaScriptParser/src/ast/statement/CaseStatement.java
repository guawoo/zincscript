package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>CaseStatement</code> 定义了<strong><code>case</code></strong>关键字语句的语法结构
 * <p>
 * <strong><code>case</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <i><b>CaseClause:</b></i>
 * 	<strong><code>case</code></strong> Expression : StatementList(opt)
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 68页 12.11.The <strong><code>switch</code></strong> Statement
 */
public class CaseStatement extends AbstractStatement {
	/** case后面的条件表达式 */
	public AbstractExpression expression = null;
	/** case块包含的执行语句 */
	public AbstractStatement[] statementList = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param statements
	 *            {@link #statementList}
	 */
	public CaseStatement(AbstractExpression expression,
			AbstractStatement[] statementList) {
		this.expression = expression;
		this.statementList = statementList;
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
		release(statementList);
		statementList = null;
	}

}
