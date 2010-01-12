package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.AbstractNode;

/**
 * <code>AbstractExpression</code> 是语法树上所有表达式节点的基类
 * 
 * @author Jarod Yv
 */
public abstract class AbstractExpression extends AbstractNode {
	/**
	 * 分析表达式语法
	 * 
	 * @param interpreter
	 *            分析器
	 * @return 表达式语法节点
	 * @throws ParserException
	 */
	public abstract AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException;
}
