package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;
import ast.statement.AbstractStatement;

/**
 * <code>FunctionLiteral</code> 定义了函数的语法树
 * 
 * <pre>
 * <em>FunctionExpression:</em>
 * 	<b><code>function</code></b> Identifier ( FormalParameterList ){ FunctionBody}
 * </pre>
 * 
 * @author Jarod Yv
 */
public class FunctionLiteral extends AbstractLiteral {
	/** 函数名 */
	public IdentifierLiteral funcName;
	/** 参数列表 */
	public IdentifierLiteral[] parameters;
	/** 函数体语句 */
	public AbstractStatement[] statements;

	public IdentifierLiteral[] variables;
	public AbstractStatement[] functions;
	public boolean enableLocalsOptimization;

	public int index;

	/**
	 * 构造函数
	 * 
	 * @param funcName
	 *            函数名
	 * @param parameters
	 *            参数列表
	 * @param statements
	 *            函数体语句
	 */
	public FunctionLiteral(IdentifierLiteral funcName,
			IdentifierLiteral[] parameters, AbstractStatement[] statements) {
		this.funcName = funcName;
		this.parameters = parameters;
		this.statements = statements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.expression.AbstractExpression#analyseExpression(analyzer.AbstractAnalyzer
	 * )
	 */
	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

}
