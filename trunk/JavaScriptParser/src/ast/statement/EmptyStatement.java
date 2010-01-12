package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>EmptyStatement</code>
 * 
 * @author Jarod Yv
 */
public class EmptyStatement extends AbstractStatement {

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
