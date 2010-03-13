package ast.expression.binary;

import ast.expression.AbstractExpression;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>AssignmentExpression</code> 定义赋值语法树
 * 
 * @author Jarod Yv
 */
public class AssignmentExpression extends AbstractBinaryExpression {
	/**
	 * 构造函数
	 * 
	 * @param leftExpression
	 *            {@link #leftExpression}
	 * @param rightExpression
	 *            {@link #rightExpression}
	 */
	public AssignmentExpression(AbstractExpression leftExpression,
			AbstractExpression rightExpression) {
		super(leftExpression, rightExpression);
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

}
