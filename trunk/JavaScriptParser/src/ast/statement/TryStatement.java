package ast.statement;

import ast.expression.literal.IdentifierLiteral;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>TryStatement</code> 定义了<strong><code>try</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>try</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * <b><i>TryStatement:</i></b>
 * 	<strong><code>try</code></strong> <em>BlockCatchv</em>
 * 	<strong><code>try</code></strong> <em>BlockFinally</em>
 * 	<strong><code>try</code></strong> <em>BlockCatchFinally</em>
 * <b><i>Catch:</i></b>
 * 	<strong><code>catch</code></strong> (<em>Identifier</em>) <em>Block</em>
 * <b><i>Finally:</i></b>
 * 	<strong><code>finally</code></strong> <em>Block</em>
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 70页 12.14.The <strong><code>try</code></strong> statement
 */
public class TryStatement extends AbstractStatement {
	/** try语句块 */
	public AbstractStatement tryBlock = null;
	/** catch后的标识符 */
	public IdentifierLiteral catchIdentifier = null;
	/** catch语句块 */
	public AbstractStatement catchBlock = null;
	/** finally语句块 */
	public AbstractStatement finallyBlock = null;

	/**
	 * 构造函数
	 * 
	 * @param tryBlock
	 *            {@link #tryBlock}
	 * @param catchIdentifier
	 *            {@link #catchIdentifier}
	 * @param catchBlock
	 *            {@link #catchBlock}
	 * @param finallyBlock
	 *            {@link #finallyBlock}
	 */
	public TryStatement(AbstractStatement tryBlock,
			IdentifierLiteral catchIdentifier, AbstractStatement catchBlock,
			AbstractStatement finallyBlock) {
		this.tryBlock = tryBlock;
		this.catchIdentifier = catchIdentifier;
		this.catchBlock = catchBlock;
		this.finallyBlock = finallyBlock;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (tryBlock != null) {
			tryBlock.release();
			tryBlock = null;
		}
		if (catchIdentifier != null) {
			catchIdentifier.release();
			catchIdentifier = null;
		}
		if (catchBlock != null) {
			catchBlock.release();
			catchBlock = null;
		}
		if (finallyBlock != null) {
			finallyBlock.release();
			finallyBlock = null;
		}
	}

}
