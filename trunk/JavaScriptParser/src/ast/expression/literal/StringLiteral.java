package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>StringLiteral</code>
 * 
 * @author Jarod Yv
 */
public class StringLiteral extends AbstractLiteral {
	public String string = null;

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

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		string=null;
	}
}
