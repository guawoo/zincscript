package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>VariableExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableExpression extends AbstractExpression {
	public VariableDeclarationExpression[] declarations = null;

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
