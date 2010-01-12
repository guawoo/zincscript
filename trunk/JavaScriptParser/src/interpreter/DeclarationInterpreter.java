package interpreter;

import parser.ParserException;
import parser.Token;
import parser.Util;
import utils.ArrayList;
import ast.*;
import ast.expression.*;
import ast.expression.binary.*;
import ast.expression.literal.*;
import ast.expression.unary.*;
import ast.statement.*;

/**
 * <code>DeclarationInterpreter</code> 负责对脚本中声明函数和声明变量的语法进行语义分析
 * 
 * @author Jarod Yv
 */
public class DeclarationInterpreter extends AbstractInterpreter {
	private ArrayList functionList = null;
	private ArrayList variableList = null;
	private boolean hasWithStatement = false;
	private boolean hasArgumentsVariable = false;
	private boolean hasFunctionLiteral = false;

	public Program interpret(Program program) throws ParserException {
		ArrayList oldFunctionVector = functionList;

		functionList = new ArrayList();

		program.functions = visitStatementArray(program.functions);
		program.statements = visitStatementArray(program.statements);

		program.functions = Util.listToStatementArray(functionList);

		functionList = oldFunctionVector;

		return program;
	}

	public AbstractStatement interpret(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws ParserException {
		functionDeclarationStatement.function = (FunctionLiteral) visitExpression(functionDeclarationStatement.function);
		return functionDeclarationStatement;
	}

	public AbstractStatement interpret(BlockStatement blockStatement)
			throws ParserException {
		blockStatement.statements = visitStatementArray(blockStatement.statements);
		return blockStatement;
	}

	public AbstractStatement interpret(BreakStatement breakStatement)
			throws ParserException {
		breakStatement.identifier = visitIdentifier(breakStatement.identifier);
		return breakStatement;
	}

	public AbstractStatement interpret(CaseStatement caseStatement)
			throws ParserException {
		caseStatement.expression = visitExpression(caseStatement.expression);
		caseStatement.statements = visitStatementArray(caseStatement.statements);
		return caseStatement;
	}

	public AbstractStatement interpret(ContinueStatement continueStatement)
			throws ParserException {
		continueStatement.identifier = visitIdentifier(continueStatement.identifier);
		return continueStatement;
	}

	public AbstractStatement interpret(DoStatement doStatement)
			throws ParserException {
		doStatement.statement = visitStatement(doStatement.statement);
		doStatement.expression = visitExpression(doStatement.expression);
		return doStatement;
	}

	public AbstractStatement interpret(EmptyStatement emptyStatement)
			throws ParserException {
		return emptyStatement;
	}

	public AbstractStatement interpret(ExpressionStatement expressionStatement)
			throws ParserException {
		expressionStatement.expression = visitExpression(expressionStatement.expression);
		return expressionStatement;
	}

	public AbstractStatement interpret(ForStatement forStatement)
			throws ParserException {
		forStatement.initial = visitExpression(forStatement.initial);
		forStatement.condition = visitExpression(forStatement.condition);
		forStatement.increment = visitExpression(forStatement.increment);
		forStatement.statement = visitStatement(forStatement.statement);
		return forStatement;
	}

	public AbstractStatement interpret(ForInStatement forInStatement)
			throws ParserException {
		forInStatement.variable = visitExpression(forInStatement.variable);
		forInStatement.expression = visitExpression(forInStatement.expression);
		forInStatement.statement = visitStatement(forInStatement.statement);
		return forInStatement;
	}

	public AbstractStatement interpret(IfStatement ifStatement)
			throws ParserException {
		ifStatement.expression = visitExpression(ifStatement.expression);
		ifStatement.trueStatement = visitStatement(ifStatement.trueStatement);
		ifStatement.falseStatement = visitStatement(ifStatement.falseStatement);
		return ifStatement;
	}

	public AbstractStatement interpret(LabelledStatement labelledStatement)
			throws ParserException {
		labelledStatement.identifier = visitIdentifier(labelledStatement.identifier);
		labelledStatement.statement = visitStatement(labelledStatement.statement);
		return labelledStatement;
	}

	public AbstractStatement interpret(ReturnStatement returnStatement)
			throws ParserException {
		returnStatement.expression = visitExpression(returnStatement.expression);
		return returnStatement;
	}

	public AbstractStatement interpret(SwitchStatement switchStatement)
			throws ParserException {
		switchStatement.expression = visitExpression(switchStatement.expression);
		switchStatement.clauses = (CaseStatement[]) visitStatementArray(switchStatement.clauses);
		return switchStatement;
	}

	public AbstractStatement interpret(ThrowStatement throwStatement)
			throws ParserException {
		throwStatement.expression = visitExpression(throwStatement.expression);
		return throwStatement;
	}

	public AbstractStatement interpret(TryStatement tryStatement)
			throws ParserException {
		tryStatement.tryBlock = visitStatement(tryStatement.tryBlock);
		tryStatement.catchIdentifier = visitIdentifier(tryStatement.catchIdentifier);
		tryStatement.catchBlock = visitStatement(tryStatement.catchBlock);
		tryStatement.finallyBlock = visitStatement(tryStatement.finallyBlock);
		return tryStatement;
	}

	public AbstractStatement interpret(VariableStatement variableStatement)
			throws ParserException {
		ArrayList statements = new ArrayList();

		for (int i = 0; i < variableStatement.declarations.length; i++) {
			AbstractExpression expression = visitExpression(variableStatement.declarations[i]);

			if (expression != null) {
				AbstractStatement statement = new ExpressionStatement(
						expression);
				statement.setLineNumber(variableStatement.getLineNumber());
				statements.add(statement);
			}
		}

		if (statements.size() == 0) {
			return new EmptyStatement();
		} else if (statements.size() == 1) {
			return (ExpressionStatement) statements.get(0);
		} else {
			return new BlockStatement(Util.listToStatementArray(statements));
		}
	}

	public AbstractStatement interpret(WhileStatement whileStatement)
			throws ParserException {
		whileStatement.expression = visitExpression(whileStatement.expression);
		whileStatement.statement = visitStatement(whileStatement.statement);

		return whileStatement;
	}

	public AbstractStatement interpret(WithStatement withStatement)
			throws ParserException {
		withStatement.expression = visitExpression(withStatement.expression);
		withStatement.statement = visitStatement(withStatement.statement);
		hasWithStatement = true;
		return withStatement;
	}

	public AbstractExpression interpret(
			AssignmentExpression assignmentExpression) throws ParserException {
		return visitBinaryExpression(assignmentExpression);
	}

	public AbstractExpression interpret(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws ParserException {
		return visitBinaryExpression(assignmentOperatorExpression);
	}

	public AbstractExpression interpret(
			BinaryOperatorExpression binaryOperatorExpression)
			throws ParserException {
		return visitBinaryExpression(binaryOperatorExpression);
	}

	public AbstractExpression interpret(
			CallFunctionExpression callFunctionExpression)
			throws ParserException {
		callFunctionExpression.function = visitExpression(callFunctionExpression.function);
		callFunctionExpression.arguments = visitExpressionArray(callFunctionExpression.arguments);
		return callFunctionExpression;
	}

	public AbstractExpression interpret(
			ConditionalExpression conditionalExpression) throws ParserException {
		conditionalExpression.expression = visitExpression(conditionalExpression.expression);
		conditionalExpression.trueExpression = visitExpression(conditionalExpression.trueExpression);
		conditionalExpression.falseExpression = visitExpression(conditionalExpression.falseExpression);
		return conditionalExpression;
	}

	public AbstractExpression interpret(DeleteExpression deleteExpression)
			throws ParserException {
		return visitUnaryExpression(deleteExpression);
	}

	public AbstractExpression interpret(IncrementExpression incrementExpression)
			throws ParserException {
		return visitUnaryExpression(incrementExpression);
	}

	public AbstractExpression interpret(
			LogicalAndExpression logicalAndExpression) throws ParserException {
		return visitBinaryExpression(logicalAndExpression);
	}

	public AbstractExpression interpret(LogicalOrExpression logicalOrExpression)
			throws ParserException {
		return visitBinaryExpression(logicalOrExpression);
	}

	public AbstractExpression interpret(NewExpression newExpression)
			throws ParserException {
		newExpression.objName = visitExpression(newExpression.objName);
		newExpression.arguments = visitExpressionArray(newExpression.arguments);
		return newExpression;
	}

	public AbstractExpression interpret(PropertyExpression propertyExpression)
			throws ParserException {
		return visitBinaryExpression(propertyExpression);
	}

	public AbstractExpression interpret(
			UnaryOperatorExpression unaryOperatorExpression)
			throws ParserException {
		return visitUnaryExpression(unaryOperatorExpression);
	}

	public AbstractExpression interpret(VariableExpression variableExpression)
			throws ParserException {
		AbstractExpression result = null;

		for (int i = 0; i < variableExpression.declarations.length; i++) {
			AbstractExpression expression = visitExpression(variableExpression.declarations[i]);

			if (expression != null) {
				if (result == null) {
					result = expression;
				} else {
					result = new BinaryOperatorExpression(result, expression,
							Token.OPERATOR_COMMA);
				}
			}
		}

		return result;
	}

	public AbstractExpression interpret(
			VariableDeclarationExpression variableDeclarationExpression)
			throws ParserException {
		IdentifierLiteral identifier = visitIdentifier(variableDeclarationExpression.identifier);
		AbstractExpression initializer = visitExpression(variableDeclarationExpression.initializer);
		AbstractExpression result = null;

		if (variableList != null) {
			addVariable(identifier);
		}

		if (initializer != null) {
			result = new AssignmentExpression(identifier, initializer);
		} else {
			result = identifier;
		}

		return result;
	}

	public AbstractLiteral interpret(IdentifierLiteral identifierLiteral)
			throws ParserException {
		if (identifierLiteral.string.equals("arguments")) {
			hasArgumentsVariable = true;
		}
		return identifierLiteral;
	}

	public AbstractLiteral interpret(ThisLiteral thisLiteral)
			throws ParserException {
		return thisLiteral;
	}

	public AbstractLiteral interpret(NullLiteral nullLiteral)
			throws ParserException {
		return nullLiteral;
	}

	public AbstractLiteral interpret(BooleanLiteral booleanLiteral)
			throws ParserException {
		return booleanLiteral;
	}

	public AbstractLiteral interpret(NumberLiteral numberLiteral)
			throws ParserException {
		return numberLiteral;
	}

	public AbstractLiteral interpret(StringLiteral stringLiteral)
			throws ParserException {
		return stringLiteral;
	}

	public AbstractLiteral interpret(ArrayLiteral arrayLiteral)
			throws ParserException {
		arrayLiteral.elements = visitExpressionArray(arrayLiteral.elements);
		return arrayLiteral;
	}

	public AbstractLiteral interpret(FunctionLiteral functionLiteral)
			throws ParserException {
		ArrayList oldFunctionVector = functionList;
		ArrayList oldVariableVector = variableList;
		boolean oldHasWithStatement = hasWithStatement;
		boolean oldHasArgumentsVariable = hasArgumentsVariable;

		functionList = new ArrayList();
		variableList = new ArrayList();
		hasWithStatement = false;
		hasArgumentsVariable = false;
		hasFunctionLiteral = false;

		IdentifierLiteral[] parameters = functionLiteral.parameters;
		for (int i = 0; i < parameters.length; i++) {
			addVariable(parameters[i]);
		}

		functionLiteral.funcName = visitIdentifier(functionLiteral.funcName);
		functionLiteral.parameters = visitIdentifierArray(functionLiteral.parameters);
		functionLiteral.variables = visitIdentifierArray(functionLiteral.variables);
		functionLiteral.functions = visitStatementArray(functionLiteral.functions);
		functionLiteral.statements = visitStatementArray(functionLiteral.statements);

		functionLiteral.functions = Util.listToStatementArray(functionList);
		functionLiteral.variables = Util.listToIdentifierArray(variableList);

		// if this function literal:
		// * contains a function literal
		// * contains a 'with' statement
		// * contains a reference to 'arguments'
		//
		// then we need to disable the "access locals by index" optimisation for
		// this function literal.

		functionLiteral.enableLocalsOptimization = !(hasWithStatement
				| hasArgumentsVariable | hasFunctionLiteral);

		functionList = oldFunctionVector;
		variableList = oldVariableVector;
		hasWithStatement = oldHasWithStatement;
		hasArgumentsVariable = oldHasArgumentsVariable;
		hasFunctionLiteral = true;

		return functionLiteral;
	}

	public AbstractLiteral interpret(ObjectLiteral objectLiteral)
			throws ParserException {
		objectLiteral.properties = (ObjectPropertyLiteral[]) visitExpressionArray(objectLiteral.properties);
		return objectLiteral;
	}

	public AbstractLiteral interpret(ObjectPropertyLiteral objectPropertyLiteral)
			throws ParserException {
		objectPropertyLiteral.name = visitExpression(objectPropertyLiteral.name);
		objectPropertyLiteral.value = visitExpression(objectPropertyLiteral.value);
		return objectPropertyLiteral;
	}

	private AbstractStatement visitStatement(AbstractStatement statement)
			throws ParserException {
		if (statement != null) {
			statement = statement.interpretStatement(this);
		}
		return statement;
	}

	private AbstractStatement[] visitStatementArray(
			AbstractStatement[] statements) throws ParserException {
		if (statements != null) {
			for (int i = 0; i < statements.length; i++) {
				statements[i] = visitStatement(statements[i]);
			}
		}

		return statements;
	}

	private AbstractExpression visitExpression(AbstractExpression expression)
			throws ParserException {
		if (expression != null) {
			expression = expression.interpretExpression(this);
		}

		return expression;
	}

	protected AbstractExpression[] visitExpressionArray(
			AbstractExpression[] expressions) throws ParserException {
		if (expressions != null) {
			for (int i = 0; i < expressions.length; i++) {
				expressions[i] = visitExpression(expressions[i]);
			}
		}

		return expressions;
	}

	private IdentifierLiteral[] visitIdentifierArray(
			IdentifierLiteral[] identifiers) throws ParserException {
		if (identifiers != null) {
			for (int i = 0; i < identifiers.length; i++) {
				identifiers[i] = visitIdentifier(identifiers[i]);
			}
		}

		return identifiers;
	}

	private IdentifierLiteral visitIdentifier(IdentifierLiteral identifier)
			throws ParserException {
		if (identifier != null) {
			identifier = (IdentifierLiteral) identifier
					.interpretExpression(this);
		}

		return identifier;
	}

	private void addVariable(IdentifierLiteral identifier) {
		if (variableList.indexOf(identifier) == -1) {
			identifier.index = variableList.size();
			variableList.add(identifier);
		}
	}

	private AbstractExpression visitBinaryExpression(
			AbstractBinaryExpression expression) throws ParserException {
		expression.leftExpression = visitExpression(expression.leftExpression);
		expression.rightExpression = visitExpression(expression.rightExpression);
		return expression;
	}

	private AbstractExpression visitUnaryExpression(
			AbstractUnaryExpression expression) throws ParserException {
		expression.expression = visitExpression(expression.expression);

		return expression;
	}
}
