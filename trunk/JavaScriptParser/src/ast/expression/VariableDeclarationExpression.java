package ast.expression;

import ast.expression.literal.IdentifierLiteral;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>VariableDeclarationExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableDeclarationExpression extends AbstractExpression {
	/** 标识符 */
	public IdentifierLiteral identifier = null;

	/** 初始化表达式 */
	public AbstractExpression initializer = null;

	/**
	 * 构造函数
	 * 
	 * @param identifier
	 *            {@link #identifier}
	 * @param initializer
	 *            {@link #initializer}
	 */
	public VariableDeclarationExpression(IdentifierLiteral identifier,
			AbstractExpression initializer) {
		this.identifier = identifier;
		this.initializer = initializer;
	}

	public AbstractExpression compileExpression(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		if (identifier != null) {
			identifier.release();
			identifier = null;
		}
		if (initializer != null) {
			initializer.release();
			initializer = null;
		}
	}

}
