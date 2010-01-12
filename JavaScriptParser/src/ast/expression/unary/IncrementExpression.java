package ast.expression.unary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>IncrementExpression</code>
 * 
 * @author Jarod Yv
 */
public class IncrementExpression extends AbstractUnaryExpression {
	public int value;
	public boolean post;

	public IncrementExpression(AbstractExpression expression, int value,
			boolean post) {
		super(expression);
		this.value = value;
		this.post = post;
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
