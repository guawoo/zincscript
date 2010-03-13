package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>VariableExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableExpression extends AbstractExpression {
	/** 函数生命语句 */
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

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return null;
	}

	public void release() {
		release(declarations);
		declarations = null;
	}
}
