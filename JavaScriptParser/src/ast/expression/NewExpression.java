package ast.expression;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>NewExpression</code>
 * 
 * @author Jarod Yv
 */
public class NewExpression extends AbstractExpression {
	public AbstractExpression function = null;
	public AbstractExpression[] arguments = null;

	public NewExpression(AbstractExpression objName,
			AbstractExpression[] arguments) {
		this.function = objName;
		this.arguments = arguments;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		if (function != null) {
			function.release();
			function = null;
		}
		release(arguments);
		arguments = null;
	}

}
