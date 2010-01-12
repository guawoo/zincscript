package ast.expression.binary;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>LogicalOrExpression</code> 定义逻辑或表达式的语法树
 * 
 * @author Jarod Yv
 */
public class LogicalOrExpression extends AbstractBinaryExpression {

	public LogicalOrExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
