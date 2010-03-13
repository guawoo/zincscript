package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ArrayLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ArrayLiteral extends AbstractLiteral {
	/** 数组元素定义表达式 */
	public AbstractExpression[] elements = null;

	/**
	 * 构造函数
	 * 
	 * @param elements
	 *            {@link #elements}
	 */
	public ArrayLiteral(AbstractExpression[] elements) {
		this.elements = elements;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		release(elements);
		elements = null;
	}
}
