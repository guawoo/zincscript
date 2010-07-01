package ast.expression;

import ast.AbstractSyntaxNode;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>AbstractExpression</code> 是语法树上所有表达式节点的基类
 * 
 * @author Jarod Yv
 */
public abstract class AbstractExpression extends AbstractSyntaxNode {
	/**
	 * 分析表达式语法
	 * 
	 * @param compiler
	 *            编译器
	 * @return 表达式语法节点
	 * @throws ParserException
	 */
	public abstract AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException;

	public void print() {
		System.out.println("This is the expression");
	}
}
