package ast.statement;

import parser.ParserException;
import ast.AbstractSyntaxNode;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>AbstracStatement</code> 是语法树上所有语句语法节点的基类
 * <p>
 * Statement的语法如下:
 * 
 * <pre>
 * <i><b>Statement:</b></i>
 * 	Block
 * 	VariableStatement
 * 	EmptyStatement
 * 	ExpressionStatement
 * 	IfStatement
 * 	IterationStatement
 * 	ContinueStatement
 * 	BreakStatement
 * 	ReturnStatement
 * 	WithStatement
 * 	LabelledStatement
 * 	SwitchStatement
 * 	ThrowStatement
 * 	TryStatement
 * </pre>
 * 
 * 上述语法都会有自己的语法节点, 这些语法节点都是<code>AbstracStatement</code>的子类
 * 
 * @author Jarod Yv
 * @see EMCA-262 61页 12.Statements
 */
public abstract class AbstractStatement extends AbstractSyntaxNode {
	/** 声明语句的行号. 此行号对脚本的执行没有意义, 主要用于Debug时输出方便定位问题 */
	protected int lineNumber;

	/**
	 * 分析声明语句语法
	 * 
	 * @param compiler
	 *            分析器
	 * @return 声明语法节点
	 * @throws ParserException
	 */
	public abstract AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException;

	/**
	 * 获取声明语句的行号
	 * 
	 * @return {@link #lineNumber}
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * 设置声明语句的行号
	 * 
	 * @param lineNumber
	 *            {@link #lineNumber}
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void print() {
		System.out.println("This is the statement @" + this.lineNumber);
	}
}
