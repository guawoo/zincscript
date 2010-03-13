package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ArrayLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ArrayLiteral extends AbstractLiteral {
	/** 数组元素定义表达式 */
	public AbstractExpression[] elements = null;

	/**
	 * 构造函数
	 * 
	 * @param elements
	 *            {@link #elements}
	 */
	public ArrayLiteral(AbstractExpression[] elements) {
		this.elements = elements;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(elements);
		elements = null;
	}
}
