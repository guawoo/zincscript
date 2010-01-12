package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>VariableExpression</code>
 * 
 * @author Jarod Yv
 */
public class VariableExpression extends AbstractExpression {
	public VariableDeclarationExpression[] declarations;

	public VariableExpression(VariableDeclarationExpression[] declarations) {
		this.declarations = declarations;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return null;
	}

}
