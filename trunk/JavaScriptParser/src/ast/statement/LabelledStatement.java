package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>LabelledStatement</code>
 * 
 * @author Jarod Yv
 */
public class LabelledStatement extends AbstractStatement {
	public IdentifierLiteral identifier;
	public AbstractStatement statement;

	public LabelledStatement(IdentifierLiteral identifier,
			AbstractStatement statement) {
		this.identifier = identifier;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
