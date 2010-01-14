package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;

/**
 * <code>EmptyStatement</code>定义了空语句的语法结构
 * <p>
 * 空语句的语法结构如下:
 * 
 * <pre>
 * <b><i>EmptyStatement:</i></b>
 * 	;
 * </pre>
 * 
 * 所以空语句的语法结构就是空的, 什么也不做.
 * 
 * @author Jarod Yv
 * @see ECMA-262 63页 12.3.Empty Statement
 */
public class EmptyStatement extends AbstractStatement {

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

	public void release() {

	}

}
