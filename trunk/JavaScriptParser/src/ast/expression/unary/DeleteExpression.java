package ast.expression.unary;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>DecrementExpression</code>
 * 
 * @author Jarod Yv
 */
public class DeleteExpression extends AbstractUnaryExpression {

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link AbstractUnaryExpression#expression}
	 */
	public DeleteExpression(AbstractExpression expression) {
		super(expression);
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

}
