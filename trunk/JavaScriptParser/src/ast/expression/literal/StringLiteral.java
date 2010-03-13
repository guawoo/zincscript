package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>StringLiteral</code>
 * 
 * @author Jarod Yv
 */
public class StringLiteral extends AbstractLiteral {
	/** 封装的字符串内容 */
	public String string = null;

	/**
	 * 构造函数
	 * 
	 * @param string
	 *            {@link #string}
	 */
	public StringLiteral(String string) {
		this.string = string;
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (this.getClass() != object.getClass()) {
			return false;
		}

		StringLiteral other = (StringLiteral) object;

		return this.string.equals(other.string);
	}

	public int hashCode() {
		return string.hashCode();
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		string = null;
	}
}
