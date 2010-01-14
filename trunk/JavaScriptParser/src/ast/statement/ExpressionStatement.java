package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ExpressionStatement</code> 定义了表达式语句的语法 结构
 * 
 * <pre>
 * ExpressionStatement:
 * [lookahead 不属于 {,function}] Expression ;
 * </pre>
 * 
 * @author Jarod Yv
 */
public class ExpressionStatement extends AbstractStatement {
	/** 语句中包含的表达式 */
	public AbstractExpression expression = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 */
	public ExpressionStatement(AbstractExpression expression) {
		this.expression = expression;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (expression != null) {
			expression.release();
			expression = null;
		}
	}

}
