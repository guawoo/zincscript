package ast.statement;

import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>EmptyStatement</code>定义了空语句的语法结构
 * <p>
 * 空语句的语法结构如下:
 * 
 * <pre>
 * <b><i>EmptyStatement:</i></b>
 * 	;
 * </pre>
 * 
 * 所以空语句的语法结构就是空的, 什么也不做.
 * 
 * @author Jarod Yv
 * @see ECMA-262 63页 12.3.Empty Statement
 */
public class EmptyStatement extends AbstractStatement {

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {

	}

}
