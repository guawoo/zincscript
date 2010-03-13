package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>IdentifierLiteral</code>
 * 
 * @author Jarod Yv
 */
public class IdentifierLiteral extends AbstractLiteral {
	/** 标识符 */
	public String string = null;

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

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		string = null;
	}

}
