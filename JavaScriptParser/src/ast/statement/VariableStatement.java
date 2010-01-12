package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.VariableDeclarationExpression;

/**
 * @author Jarod Yv
 */
public class VariableStatement extends AbstractStatement {
	public VariableDeclarationExpression[] declarations;

	public VariableStatement(VariableDeclarationExpression[] declarations) {
		this.declarations = declarations;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
