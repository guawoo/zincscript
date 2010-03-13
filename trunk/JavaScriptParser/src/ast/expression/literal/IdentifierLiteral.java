package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>IdentifierLiteral</code>
 * 
 * @author Jarod Yv
 */
public class IdentifierLiteral extends AbstractLiteral {
	/** 标识符 */
	public String string = null;

	/** 标识符在变量列表中的索引 */
	public int index = -1;

	/**
	 * 构造函数
	 * 
	 * @param string
	 *            {@link #string}
	 */
	public IdentifierLiteral(String string) {
		this.string = string;
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (this.getClass() != object.getClass()) {
			return false;
		}

		IdentifierLiteral other = (IdentifierLiteral) object;

		return this.string.equals(other.string);
	}

	public int hashCode() {
		return string.hashCode();
	}

	public String toString() {
		return string;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		string = null;
	}

}
