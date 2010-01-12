package ast.statement;

import interpreter.AbstractInterpreter;
import parser.ParserException;
import ast.expression.AbstractExpression;

/**
 * <code>SwitchStatement</code> 定义了<strong>switch</strong>关键字语法的语法树
 * 
 * @author Jarod Yv
 */
public class SwitchStatement extends AbstractStatement {
	public AbstractExpression expression;
	public CaseStatement[] clauses;

	public SwitchStatement(AbstractExpression expression,
			CaseStatement[] clauses) {
		this.expression = expression;
		this.clauses = clauses;
	}

	public AbstractStatement interpretStatement(AbstractInterpreter analyzer)
			throws ParserException {
		return analyzer.interpret(this);
	}

}
