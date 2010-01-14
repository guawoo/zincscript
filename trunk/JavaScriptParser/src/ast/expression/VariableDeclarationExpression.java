package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>VariableDeclarationExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableDeclarationExpression extends AbstractExpression {
	public IdentifierLiteral identifier = null;
	public AbstractExpression initializer = null;

	public VariableDeclarationExpression(IdentifierLiteral identifier,
			AbstractExpression initializer) {
		this.identifier = identifier;
		this.initializer = initializer;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (identifier != null) {
			identifier.release();
			identifier = null;
		}
		if (initializer != null) {
			initializer.release();
			initializer = null;
		}
	}

}
