package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>ConditionalExpression</code>
 * 
 * @author Jarod Yv
 */
public class ConditionalExpression extends AbstractExpression {
	/** 条件判断表达式 */
	public AbstractExpression expression;
	/** 条件为真时执行的表达式 */
	public AbstractExpression trueExpression;
	/** 条件为假时执行的表达式 */
	public AbstractExpression falseExpression;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param trueExpression
	 *            {@link #trueExpression}
	 * @param falseExpression
	 *            {@link #falseExpression}
	 */
	public ConditionalExpression(AbstractExpression expression,
			AbstractExpression trueExpression,
			AbstractExpression falseExpression) {
		this.expression = expression;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.expression.AbstractExpression#analyseExpression(analyzer.AbstractAnalyzer
	 * )
	 */
	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
