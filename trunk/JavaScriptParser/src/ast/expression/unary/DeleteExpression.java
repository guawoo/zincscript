package ast.expression.unary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>DecrementExpression</code>
 * 
 * @author Jarod Yv
 */
public class DeleteExpression extends AbstractUnaryExpression {

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link AbstractUnaryExpression#expression}
	 */
	public DeleteExpression(AbstractExpression expression) {
		super(expression);
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

}
