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
	public IdentifierLiteral identifier;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            {@link #identifier}
	 */
	public ContinueStatement(IdentifierLiteral identifier) {
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
		return analyzer.interpret(this);
	}

}
