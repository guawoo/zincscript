package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>LogicalAndExpression</code> 定义逻辑与表达式的语法树
 * 
 * @author Jarod Yv
 */
public class LogicalAndExpression extends AbstractBinaryExpression {

	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 */
	public LogicalAndExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

}
