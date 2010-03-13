package ast.expression.literal;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>ObjectLiteral</code>
 * 
 * @author Jarod Yv
 */
public class ObjectLiteral extends AbstractLiteral {
	/** 对象属性 */
	public ObjectPropertyLiteral[] properties = null;

	/**
	 * 构造函数
	 * 
	 * @param properties
	 *            {@link #properties}
	 */
	public ObjectLiteral(ObjectPropertyLiteral[] properties) {
		this.properties = properties;
	}

	public AbstractExpression interpretExpression(
			AbstractInterpreter interpreter) throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		release(properties);
		properties = null;
	}
}
