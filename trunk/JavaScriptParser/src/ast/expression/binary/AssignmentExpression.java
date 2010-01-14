package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>AssignmentExpression</code> 定义赋值语法树
 * 
 * @author Jarod Yv
 */
public class AssignmentExpression extends AbstractBinaryExpression {
	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            左子式
	 * @param rightExpression
	 *            右子式
	 */
	public AssignmentExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

}
