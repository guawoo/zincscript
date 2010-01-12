package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>NewExpression</code>
 * 
 * @author Jarod Yv
 */
public class NewExpression extends AbstractExpression {
	public AbstractExpression objName;
	public AbstractExpression[] arguments;

	public NewExpression(AbstractExpression objName,
			AbstractExpression[] arguments) {
		this.objName = objName;
		this.arguments = arguments;
	}

	public AbstractExpression interpretExpression(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
