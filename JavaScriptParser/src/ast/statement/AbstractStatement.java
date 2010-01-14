package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.AbstractSyntaxNode;

/**
 * <code>AbstracStatement</code> 是语法树上所有语句节点的基类
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
 * 这些语句都将是<code>AbstracStatement</code>的子类
 * 
 * @author Jarod Yv
 * @see EMCA-262 61页 12.Statements
 */
public abstract class AbstractStatement extends AbstractSyntaxNode {
	/** 声明语句的行号 */
	protected int lineNumber;

	/**
	 * 分析声明语句语法
	 * 
	 * @param interpreter
	 *            分析器
	 * @return 声明语法节点
	 * @throws ParserException
	 */
	public abstract AbstractStatement interpretStatement(
			AbstractInterpreter interpreter) throws ParserException;

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
}
