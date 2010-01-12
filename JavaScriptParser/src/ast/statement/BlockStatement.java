package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>BlockStatement</code>
 * 
 * @author Jarod Yv
 */
public class BlockStatement extends AbstractStatement {
	/** 代码块中的所有声明 */
	public AbstractStatement[] statements;

	/**
	 * 构造函数
	 * 
	 * @param statements
	 *            {@link #statements}
	 */
	public BlockStatement(AbstractStatement[] statements) {
		this.statements = statements;
	}

	/* (non-Javadoc)
	 * @see ast.statement.AbstractStatement#analyseStatement(analyzer.AbstractAnalyzer)
	 */
	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
