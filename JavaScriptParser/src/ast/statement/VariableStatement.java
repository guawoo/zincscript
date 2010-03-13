package ast.statement;

import ast.expression.VariableDeclarationExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>VariableStatement</code> 对变量声明表达式进行了封装. 按照语法定义, 一条变量声明语句包含一个或多个变量声明表达式.
 * <p>
 * 变量声明语句的语法结构如下:
 * 
 * <pre>
 * <b><i>VariableStatement:</i></b>
 * 	<strong><code>var</code></strong> <em>VariableDeclarationList</em>
 * </pre>
 * 
 * @author Jarod Yv
 * @see {@link VariableDeclarationExpression}
 * @see ECMA-262 62页 12.2.Variable statement
 */
public class VariableStatement extends AbstractStatement {
	/** 变量声明表达式集合 */
	public VariableDeclarationExpression[] declarations = null;

	/**
	 * 构造函数
	 * 
	 * @param declarations
	 *            变量声明表达式集合
	 */
	public VariableStatement(VariableDeclarationExpression[] declarations) {
		this.declarations = declarations;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(declarations);
		declarations = null;
	}

}
