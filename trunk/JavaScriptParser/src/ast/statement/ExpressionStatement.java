package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ExpressionStatement</code>
 * 
 * @author Jarod Yv
 */
public class ExpressionStatement extends AbstractStatement {
	/** 语句中包含的表达式 */
	public AbstractExpression expression;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 */
	public ExpressionStatement(AbstractExpression expression) {
		this.expression = expression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.statement.AbstractStatement#analyseStatement(analyzer.AbstractAnalyzer
	 * )
	 */
	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
