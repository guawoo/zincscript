package ast.statement;

import ast.expression.literal.IdentifierLiteral;
import compiler.CompilerException;
import compiler.ICompilable;

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
	/** label标识符 */
	public IdentifierLiteral identifier = null;
	/** label标识符后的语句 */
	public AbstractStatement statement = null;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            {@link #identifier}
	 * @param statement
	 *            {@link #statement}
	 */
	public LabelledStatement(IdentifierLiteral identifier,
			AbstractStatement statement) {
		this.identifier = identifier;
		this.statement = statement;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
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
