package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>DoStatement</code> 定义了<strong>do</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class DoStatement extends AbstractStatement {
	/** do后面的条件表达式 */
	public AbstractExpression expression;
	/** 要循环执行的语句 */
	public AbstractStatement statement;

	/**
	 * 构造函数
	 * 
	 * @param statement
	 *            {@link #statement}
	 * @param expression
	 *            {@link #expression}
	 */
	public DoStatement(AbstractStatement statement,
			AbstractExpression expression) {
		this.expression = expression;
		this.statement = statement;
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
