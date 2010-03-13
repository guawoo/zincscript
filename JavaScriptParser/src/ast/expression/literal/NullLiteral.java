package ast.expression.literal;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>NullLiteral</code>
 * 
 * @author Jarod Yv
 */
public class NullLiteral extends AbstractLiteral {

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
	}

}
