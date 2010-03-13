package ast.expression.unary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import parser.Token;
import ast.expression.AbstractExpression;

/**
 * <code>UnaryOperatorExpression</code> 是一元运算符表达式
 * 
 * @author Jarod Yv
 */
public class UnaryOperatorExpression extends AbstractUnaryExpression {
	/** 运算符 */
	public Token operator = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param operator
	 *            {@link #operator}
	 */
	public UnaryOperatorExpression(AbstractExpression expression, Token operator) {
		super(expression);
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
