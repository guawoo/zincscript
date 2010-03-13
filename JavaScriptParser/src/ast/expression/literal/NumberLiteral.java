package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>NumberLiteral</code>
 * 
 * @author Jarod Yv
 */
public class NumberLiteral extends AbstractLiteral {
	/** 封装的数字数值 */
	public double value = 0.0;

	public int index = 0;

	/**
	 * 构造函数
	 * 
	 * @param value
	 *            {@link #value}
	 */
	public NumberLiteral(double value) {
		this.value = value;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
	}

}
