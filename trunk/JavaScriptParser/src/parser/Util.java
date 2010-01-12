package parser;

import utils.ArrayList;
import ast.expression.AbstractExpression;
import ast.expression.VariableDeclarationExpression;
import ast.expression.literal.FunctionLiteral;
import ast.expression.literal.IdentifierLiteral;
import ast.expression.literal.NumberLiteral;
import ast.expression.literal.StringLiteral;
import ast.statement.AbstractStatement;

public final class Util {

	public static AbstractStatement[] listToStatementArray(ArrayList list) {
		AbstractStatement[] statementArray = new AbstractStatement[list.size()];
		list.toArray(statementArray);
		return statementArray;
	}

	public static AbstractExpression[] vectorToExpressionArray(ArrayList list) {
		AbstractExpression[] expressionArray = new AbstractExpression[list
				.size()];
		list.toArray(expressionArray);
		return expressionArray;
	}

	public static VariableDeclarationExpression[] vectorToDeclarationArray(
			ArrayList list) {
		VariableDeclarationExpression[] declarationArray = new VariableDeclarationExpression[list
				.size()];
		list.toArray(declarationArray);
		return declarationArray;
	}

	public static IdentifierLiteral[] listToIdentifierArray(ArrayList list) {
		IdentifierLiteral[] identifierArray = new IdentifierLiteral[list.size()];
		list.toArray(identifierArray);
		return identifierArray;
	}

	public static FunctionLiteral[] vectorToFunctionLiteralArray(ArrayList list) {
		FunctionLiteral[] literalArray = new FunctionLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}

	public static NumberLiteral[] vectorToNumberLiteralArray(ArrayList list) {
		NumberLiteral[] literalArray = new NumberLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}

	public static StringLiteral[] vectorToStringLiteralArray(ArrayList list) {
		StringLiteral[] literalArray = new StringLiteral[list.size()];
		list.toArray(literalArray);
		return literalArray;
	}
}
