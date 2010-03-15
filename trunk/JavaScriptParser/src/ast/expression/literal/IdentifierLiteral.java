package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>IdentifierLiteral</code>是标识符定义语法
 * <p>
 * 标识符定义语法结构如下:
 * 
 * <pre>
 * <em>Identifier::</em>
 * 	<em>IdentifierName</em> <b>but not</b> <em>ReservedWord</em>
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 14页~15页 7.6.Identifiers
 */

public class IdentifierLiteral extends AbstractLiteral {
	/** 标识符名 */
	public String identifierName = null;


	/** 标识符在变量列表中的索引 */
	public int index = -1;

	/**
	 * 构造函数
	 * 
	 * @param identifierName
	 *            {@link #identifierName}
	 */
	public IdentifierLiteral(String identifierName) {
		this.identifierName = identifierName;

	}

	/**
	 * 判断两个标识符是否相同。如果两个标识符名相同，则认为他们是同一标识符
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (this.getClass() != object.getClass()) {
			return false;
		}

		IdentifierLiteral other = (IdentifierLiteral) object;

		return this.identifierName.equals(other.identifierName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return identifierName.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return identifierName;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		identifierName = null;
	}
}
