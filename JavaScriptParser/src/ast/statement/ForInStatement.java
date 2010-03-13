package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ForInStatement</code> 定义了<strong><code>for-in</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>for-in</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>for-in</code> Statement:</i></b>
 * 	<strong><code>for</code></strong>(<em>LeftHandSideExpression</em> <strong><code>in</code></strong> <em>Expression</em>) Statement
 * 	<strong><code>for</code></strong>(<strong><code>var</code></strong> <em>VariableDeclarationNoIn</em> <strong><code>in</code></strong> <em>Expression</em>) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 64页 12.6.Iteration Statements
 */
public class ForInStatement extends AbstractStatement {
	/** <strong><code>in</code></strong> 前面的表达式 */
	public AbstractExpression variable = null;
	/** <strong><code>in</code></strong> 后面的表达式 */
	public AbstractExpression expression = null;
	/** 循环体 */
	public AbstractStatement statement = null;

	public ForInStatement(AbstractExpression variable,
			AbstractExpression expression, AbstractStatement statement) {
		this.variable = variable;
		this.expression = expression;
		this.statement = statement;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (variable != null) {
			variable.release();
			variable = null;
		}
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
