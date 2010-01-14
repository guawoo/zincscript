package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>ContinueStatement</code>
 * 
 * @author Jarod Yv
 */
public class ContinueStatement extends AbstractStatement {
	public IdentifierLiteral identifier = null;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            {@link #identifier}
	 */
	public ContinueStatement(IdentifierLiteral identifier) {
		this.identifier = identifier;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (identifier != null) {
			identifier.release();
			identifier = null;
		}
	}

}
