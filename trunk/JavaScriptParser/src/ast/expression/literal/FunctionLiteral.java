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
	public IdentifierLiteral funcName = null;

	/** 参数列表 */
	public IdentifierLiteral[] parameters = null;

	/** 函数体语句 */
	public AbstractStatement[] statements = null;

	/**
	 * 函数内部变量列表.
	 * <p>
	 * 内部变量列表在语法分析过程中不能确定,因此在语法树构建完成后需要经过 {@link DeclarationInterpreter}
	 * 分析出整个语法树中缺失的数据,然后才能交给 {@link CompilationInterpreter}生成三地址代码
	 */
	public IdentifierLiteral[] variables = null;

	/**
	 * 函数内部函数列表
	 * <p>
	 * 内部函数列表在语法分析过程中不能确定,因此在语法树构建完成后需要经过 {@link DeclarationInterpreter}
	 * 分析出整个语法树中缺失的数据,然后才能交给 {@link CompilationInterpreter}生成三地址代码
	 */
	public AbstractStatement[] functions = null;

	/** */
	public boolean enableLocalsOptimization = false;

	/** */
	public int index = 0;

	/**
	 * 构造函数
	 * 
	 * @param funcName
	 *            {@link #funcName}
	 * @param parameters
	 *            {@link #parameters}
	 * @param statements
	 *            {@link #statements}
	 */
	public FunctionLiteral(IdentifierLiteral funcName,
			IdentifierLiteral[] parameters, AbstractStatement[] statements) {
		this.funcName = funcName;
		this.parameters = parameters;
		this.statements = statements;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (funcName != null) {
			funcName.release();
			funcName = null;
		}
		release(parameters);
		parameters = null;
		release(statements);
		statements = null;
		release(functions);
		functions = null;
		release(variables);
		variables = null;
	}

}
