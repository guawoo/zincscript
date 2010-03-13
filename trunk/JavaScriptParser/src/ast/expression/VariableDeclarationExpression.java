package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.literal.IdentifierLiteral;

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

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
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
