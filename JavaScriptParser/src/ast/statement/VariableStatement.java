package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.VariableDeclarationExpression;

/**
 * <code>VariableStatement</code> 对函数定义表达式进行了封装. 按照语法定义, 一条变量声明语句包含一个或多个变量声明表达式.
 * 
 * @author Jarod Yv
 */
public class VariableStatement extends AbstractStatement {
	/** 变量声明表达式集合 */
	public VariableDeclarationExpression[] declarations = null;

	/**
	 * 构造函数
	 * 
	 * @param declarations
	 *            变量声明表达式集合
	 */
	public VariableStatement(VariableDeclarationExpression[] declarations) {
		this.declarations = declarations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeast.statement.AbstractStatement#interpretStatement(interpreter.
	 * AbstractInterpreter)
	 */
	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
