package ast.statement;

import ast.expression.literal.IdentifierLiteral;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>BreakStatement</code> 定义了<strong><code>break</code></strong>关键字语句的语法结构
 * <p>
 * <strong><code>break</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i>BreakStatement:</i></b>
 * 	<strong><code>break</code></strong> [no LineTerminator here] Identifier(opt) ;
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 67页 12.8.The <code><strong>break</code></strong> Statement
 */
public class BreakStatement extends AbstractStatement {
	/** <strong><code>break</code></strong> 关键字后的标识符 */
	public IdentifierLiteral identifier = null;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            <strong><code>break</code></strong> 关键字后的标识符
	 */
	public BreakStatement(IdentifierLiteral identifier) {
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
