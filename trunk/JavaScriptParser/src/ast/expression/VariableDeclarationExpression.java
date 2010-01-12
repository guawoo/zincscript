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
	public IdentifierLiteral identifier;
	public AbstractExpression initializer;

	public VariableDeclarationExpression(IdentifierLiteral identifier,
			AbstractExpression initializer) {
		this.identifier = identifier;
		this.initializer = initializer;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
