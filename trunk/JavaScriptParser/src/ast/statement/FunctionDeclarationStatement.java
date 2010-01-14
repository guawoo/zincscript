package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.FunctionLiteral;

/**
 * <code>FunctionDeclarationStatement</code> 定义了函数声明的语法树
 * 
 * <pre>
 * <em>FunctionDeclaration:</em>
 * 	<b><code>function</code></b> Identifier ( FormalParameterList ){ FunctionBody}
 * </pre>
 * 
 * 从语法上可以看出，FunctionDeclaration和FunctionExpression具有相同的语法结构,因此
 * <code>FunctionDeclarationStatement</code>可以简单的对
 * <code>FunctionDefinition</code>进行一层封装, 即可实现相关语法树结构
 * 
 * @author Jarod Yv
 */
public class FunctionDeclarationStatement extends AbstractStatement {
	/** 函数结构 */
	public FunctionLiteral function = null;

	/**
	 * 构造函数
	 * 
	 * @param function
	 *            {@link #function}
	 */
	public FunctionDeclarationStatement(FunctionLiteral function) {
		this.function = function;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (function != null) {
			function.release();
			function = null;
		}
	}

}
