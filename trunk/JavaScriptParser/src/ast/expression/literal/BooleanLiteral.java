package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>BooleanLiteral</code>
 * 
 * @author Jarod Yv
 */
public class BooleanLiteral extends AbstractLiteral {
	/** 封装的布尔值 */
	public boolean value = false;

	/**
	 * 构造函数
	 * 
	 * @param value
	 *            {@link #value}
	 */
	public BooleanLiteral(boolean value) {
		this.value = value;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
	}
}
