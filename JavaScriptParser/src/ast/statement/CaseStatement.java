package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>CaseStatement</code> 定义了<strong>case</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class CaseStatement extends AbstractStatement {
	/** case后面的条件表达式 */
	public AbstractExpression expression;
	/** case块包含的执行语句 */
	public AbstractStatement[] statements;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param statements
	 *            {@link #statements}
	 */
	public CaseStatement(AbstractExpression expression,
			AbstractStatement[] statements) {
		this.expression = expression;
		this.statements = statements;
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
