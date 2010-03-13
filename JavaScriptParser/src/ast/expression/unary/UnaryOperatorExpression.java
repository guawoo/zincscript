package ast.expression.unary;

import parser.Token;
import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>UnaryOperatorExpression</code> 是一元运算符表达式
 * 
 * @author Jarod Yv
 */
public class UnaryOperatorExpression extends AbstractUnaryExpression {
	/** 运算符 */
	public Token operator = null;

	/**
	 * 构造函数
	 * 
	 * @param expression
	 *            {@link #expression}
	 * @param operator
	 *            {@link #operator}
	 */
	public UnaryOperatorExpression(AbstractExpression expression, Token operator) {
		super(expression);
		this.operator = operator;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		super.release();
		operator = null;
	}
}
