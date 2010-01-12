package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>TryStatement</code>
 * 
 * @author Jarod Yv
 */
public class TryStatement extends AbstractStatement {
	public AbstractStatement tryBlock;
	public IdentifierLiteral catchIdentifier;
	public AbstractStatement catchBlock;
	public AbstractStatement finallyBlock;

	public TryStatement(AbstractStatement tryBlock,
			IdentifierLiteral catchIdentifier, AbstractStatement catchBlock,
			AbstractStatement finallyBlock) {
		this.tryBlock = tryBlock;
		this.catchIdentifier = catchIdentifier;
		this.catchBlock = catchBlock;
		this.finallyBlock = finallyBlock;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
