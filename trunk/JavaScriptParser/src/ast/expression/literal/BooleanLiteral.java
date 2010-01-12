package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>BooleanLiteral</code>
 * 
 * @author Jarod Yv
 */
public class BooleanLiteral extends AbstractLiteral {
	/** 封装的布尔值 */
	public boolean value;

	/**
	 * 构造函数
	 * 
	 * @param value
	 *            {@link #value}
	 */
	public BooleanLiteral(boolean value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.literal.AbstractLiteral#analyseLiteral(analyzer.AbstractAnalyzer)
	 */
	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
