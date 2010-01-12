package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.AbstractNode;

/**
 * <code>AbstracStatement</code> 是语法树上所有声明节点的基类
 * 
 * @author Jarod Yv
 */
public abstract class AbstractStatement extends AbstractNode {
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
