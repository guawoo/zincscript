package ast.expression;

import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>VariableExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableExpression extends AbstractExpression {
	/** 变量声明语句 */
	public VariableDeclarationExpression[] declarations = null;

	/**
	 * 构造函数
	 * 
	 * @param declarations
	 *            {@link #declarations}
	 */
	public VariableExpression(VariableDeclarationExpression[] declarations) {
		this.declarations = declarations;
	}

	public AbstractExpression compileExpression(
			ICompilable compiler) throws CompilerException {
		return null;
	}

	public void release() {
		release(declarations);
		declarations = null;
	}
}
