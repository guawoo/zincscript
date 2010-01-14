package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import parser.Token;
import ast.expression.AbstractExpression;

/**
 * <code>BinaryOperatorExpression</code> 定义了二元运算符表达式的语法树节点
 * 
 * @author Jarod Yv
 */
public class BinaryOperatorExpression extends AbstractBinaryExpression {
	/** 运算符 */
	public Token operator = null;

	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            左子式
	 * @param rightExpression
	 *            右子式
	 * @param operator
	 *            运算符
	 */
	public BinaryOperatorExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression, Token operator) {
		super(leftExpression, rightExpression);
		this.operator = operator;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		super.release();
		operator = null;
	}

}
