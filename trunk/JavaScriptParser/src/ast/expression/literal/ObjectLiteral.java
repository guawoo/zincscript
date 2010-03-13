package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ObjectLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ObjectLiteral extends AbstractLiteral {
	/** 对象属性 */
	public ObjectPropertyLiteral[] properties = null;

	/**
	 * 构造函数
	 * 
	 * @param properties
	 *            {@link #properties}
	 */
	public ObjectLiteral(ObjectPropertyLiteral[] properties) {
		this.properties = properties;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(properties);
		properties = null;
	}
}
