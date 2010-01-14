package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

/**
 * <code>LabelledStatement</code> 定义了标记语句的语法树
 * <p>
 * 标记语句的语法结构如下:
 * 
 * <pre>
 * <b><i>LabelledStatement:</i></b>
 * 	Identifier: Statement
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 69页 12.12.Labelled Statements
 */
public class LabelledStatement extends AbstractStatement {
	public IdentifierLiteral identifier = null;
	public AbstractStatement statement = null;

	public LabelledStatement(IdentifierLiteral identifier,
			AbstractStatement statement) {
		this.identifier = identifier;
		this.statement = statement;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

	public void release() {
		if (identifier != null) {
			identifier.release();
			identifier = null;
		}
		if (statement != null) {
			statement.release();
			statement = null;
		}
	}

}
