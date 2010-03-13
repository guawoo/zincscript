package ast.expression.binary;

import parser.Token;
import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>BinaryOperatorExpression</code> 定义了二元运算符表达式的语法树节点
 * 
 * @author Jarod Yv
 */
public class BinaryOperatorExpression extends AbstractBinaryExpression {
	/** 运算符 */
	public Token operator = null;

	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 * @param operator
	 *            {@link #operator}
	 */
	public BinaryOperatorExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression, Token operator) {
		super(leftExpression, rightExpression);
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
