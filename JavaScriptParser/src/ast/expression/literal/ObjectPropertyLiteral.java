package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ObjectPropertyLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ObjectPropertyLiteral extends AbstractLiteral {
	/** 对象属性名 */
	public AbstractExpression name = null;

	/** 对象属性值 */
	public AbstractExpression value = null;

	/**
	 * 构造函数
	 * 
	 * @param name
	 *            {@link #name}
	 * @param value
	 *            {@link #value}
	 */
	public ObjectPropertyLiteral(AbstractExpression name,
			AbstractExpression value) {
		this.name = name;
		this.value = value;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (name != null) {
			name.release();
			name = null;
		}
		if (value != null) {
			value.release();
			value = null;
		}
	}

}
