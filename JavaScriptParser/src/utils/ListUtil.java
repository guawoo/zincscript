package utils;

import ast.expression.AbstractExpression;
import ast.expression.VariableDeclarationExpression;
import ast.expression.literal.FunctionLiteral;
import ast.expression.literal.IdentifierLiteral;
import ast.expression.literal.NumberLiteral;
import ast.expression.literal.StringLiteral;
import ast.statement.AbstractStatement;

/**
 * <code>ListUtil</code>是一个工具类，用于将List类型的数据转换成对应的静态数组形式。
 * 
 * @author Jarod Yv
 */
public final class ListUtil {

	public static final AbstractStatement[] list2StatementArray(ArrayList list) {
		AbstractStatement[] statementArray = new AbstractStatement[list.size()];
		list.toArray(statementArray);
		return statementArray;
	}

	public static final AbstractExpression[] list2ExpressionArray(ArrayList list) {
		AbstractExpression[] expressionArray = new AbstractExpression[list
				.size()];
		list.toArray(expressionArray);
		return expressionArray;
	}

	public static final VariableDeclarationExpression[] list2DeclarationArray(
			ArrayList list) {
		VariableDeclarationExpression[] declarationArray = new VariableDeclarationExpression[list
				.size()];
		list.toArray(declarationArray);
		return declarationArray;
	}

	public static final IdentifierLiteral[] list2IdentifierArray(ArrayList list) {
		IdentifierLiteral[] identifierArray = new IdentifierLiteral[list.size()];
		list.toArray(identifierArray);
		return identifierArray;
	}

	public static final FunctionLiteral[] list2FunctionLiteralArray(
			ArrayList list) {
		FunctionLiteral[] literalArray = new FunctionLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}

	public static final NumberLiteral[] list2NumberLiteralArray(ArrayList list) {
		NumberLiteral[] literalArray = new NumberLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}

	public static final StringLiteral[] list2StringLiteralArray(ArrayList list) {
		StringLiteral[] literalArray = new StringLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}
}
