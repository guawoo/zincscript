package ast.statement;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ForInStatement</code> 定义了<strong><code>for</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>for</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i><code>for</code> Statement:</i></b>
 * 	<strong><code>for</code></strong>(ExpressionNoIn(opt); Expression(opt); Expression(opt)) Statement
 * 	<strong><code>for</code></strong>(<strong><code>var</code></strong> VariableDeclarationNoIn; Expression(opt); Expression(opt)) Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 64页 12.6.Iteration Statements
 */
public class ForStatement extends AbstractStatement {
	/** 变量初始化表达式 */
	public AbstractExpression initial = null;
	/** 条件判断表达式 */
	public AbstractExpression condition = null;
	/** 更改变量表达式 */
	public AbstractExpression increment = null;
	/** 循环体 */
	public AbstractStatement statement = null;

	public ForStatement(AbstractExpression initial,
			AbstractExpression condition, AbstractExpression increment,
			AbstractStatement statement) {
		this.initial = initial;
		this.condition = condition;
		this.increment = increment;
		this.statement = statement;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (initial != null) {
			initial.release();
			initial = null;
		}
		if (condition != null) {
			condition.release();
			condition = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
	}

}
