package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>CallFunctionExpression</code> 定义了函数调用表达式的语法树
 * 
 * @author Jarod Yv
 */
public class CallFunctionExpression extends AbstractExpression {
	/** 函数名 */
	public AbstractExpression function = null;

	/** 参数列表 */
	public AbstractExpression[] arguments = null;

	/**
	 * 构造函数
	 * 
	 * @param function
	 *            {@link #function}
	 * @param arguments
	 *            {@link #arguments}
	 */
	public CallFunctionExpression(AbstractExpression function,
			AbstractExpression[] arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (function != null) {
			function.release();
			function = null;
		}
		release(arguments);
		arguments = null;
	}

}
