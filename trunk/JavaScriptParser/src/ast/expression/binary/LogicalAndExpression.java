package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>LogicalAndExpression</code> 定义逻辑与表达式的语法树
 * 
 * @author Jarod Yv
 */
public class LogicalAndExpression extends AbstractBinaryExpression {
	

	public LogicalAndExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
