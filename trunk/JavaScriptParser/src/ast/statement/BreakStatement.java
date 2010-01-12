package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>BreakStatement</code>
 * 
 * @author Jarod Yv
 */
public class BreakStatement extends AbstractStatement {
	public IdentifierLiteral identifier;

	public BreakStatement(IdentifierLiteral identifier) {
		this.identifier = identifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ast.statement.AbstractStatement#analyseStatement(analyzer.AbstractAnalyzer
	 * )
	 */
	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		// TODO Auto-generated method stub
		return analyzer.interpret(this);
	}

}
