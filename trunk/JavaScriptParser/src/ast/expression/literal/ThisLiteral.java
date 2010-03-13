package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>ThisLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ThisLiteral extends AbstractLiteral {

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
	}

}
