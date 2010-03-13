package ast.expression.unary;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>IncrementExpression</code>
 * 
 * @author Jarod Yv
 */
public class IncrementExpression extends AbstractUnaryExpression {
	public int value;
	public boolean post;

	public IncrementExpression(AbstractExpression expression, int value,
			boolean post) {
		super(expression);
		this.value = value;
		this.post = post;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

}
