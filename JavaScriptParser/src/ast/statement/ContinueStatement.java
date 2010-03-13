package ast.statement;

import ast.expression.literal.IdentifierLiteral;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ContinueStatement</code> 定义了<strong><code>continue</code>
 * </strong>关键字语句的语法结构
 * <p>
 * <strong><code>continue</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i>ContinueStatement:</i></b>
 * 	<strong><code>continue</code></strong> [no LineTerminator here] Identifier(opt) ;
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 66页 12.8.The <code><strong>continue</code></strong> Statement
 */
public class ContinueStatement extends AbstractStatement {
	/** <strong><code>continue</code></strong> 关键字后的标识符 */
	public IdentifierLiteral identifier = null;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            {@link #identifier}
	 */
	public ContinueStatement(IdentifierLiteral identifier) {
		this.identifier = identifier;
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
	}

}
