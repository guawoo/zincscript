package ast;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.statement.AbstractStatement;

/**
 * <code>Program</code>
 * 
 * @author Jarod Yv
 */
public class Program extends AbstractNode {
	public AbstractStatement[] functions = null;
	public AbstractStatement[] statements = null;

	/**
	 * 构造函数
	 * 
	 * @param statements
	 */
	public Program(AbstractStatement[] statements) {
		this.statements = statements;
	}

	/**
	 * @param interpreter
	 * @return
	 * @throws ParserException
	 */
	public Program interpretProgram(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}
}
