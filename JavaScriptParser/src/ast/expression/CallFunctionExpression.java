package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>CallFunctionExpression</code> 定义了函数调用的语法树
 * 
 * @author Jarod Yv
 */
public class CallFunctionExpression extends AbstractExpression {
	/** 函数名 */
	public AbstractExpression function;
	/** 参数列表 */
	public AbstractExpression[] arguments;

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

	/* (non-Javadoc)
	 * @see ast.expression.AbstractExpression#analyseExpression(analyzer.AbstractAnalyzer)
	 */
	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
