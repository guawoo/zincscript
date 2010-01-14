package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>SwitchStatement</code> 定义了<strong><code>switch</code></strong>关键字语法的语法树
 * <p>
 * <strong><code>switch</code></strong>关键字语句的语法结构如下:
 * 
 * <pre>
 * SwitchStatement:</i></b>
 * 	<strong><code>switch</code></strong> (Expression) CaseBlock
 * </pre>
 * 
 * @author Jarod Yv
 * @see CaseStatement
 * @see ECMA-262 68页 12.11.The <strong><code>switch</code></strong> Statement
 */
public class SwitchStatement extends AbstractStatement {
	public AbstractExpression expression = null;
	public CaseStatement[] cases = null;

	public SwitchStatement(AbstractExpression expression, CaseStatement[] cases) {
		this.expression = expression;
		this.cases = cases;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter interpreter)
			throws ParserException {
		return interpreter.interpret(this);
	}

	public void release() {
		release(cases);
		cases = null;
		if (expression != null) {
			expression.release();
			expression = null;
		}

	}

}
