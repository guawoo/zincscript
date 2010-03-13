package ast.expression;

import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>NewExpression</code>
 * 
 * @author Jarod Yv
 */
public class NewExpression extends AbstractExpression {
	/** 函数名 */
	public AbstractExpression function = null;

	/** 参数列表 */
	public AbstractExpression[] arguments = null;

	/**
	 * 构造函数
	 * 
	 * @param function
	 *            {@link #function}
	 * @param arguments
	 *            {@link #arguments}
	 */
	public NewExpression(AbstractExpression function,
			AbstractExpression[] arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (function != null) {
			function.release();
			function = null;
		}
		release(arguments);
		arguments = null;
	}

}
