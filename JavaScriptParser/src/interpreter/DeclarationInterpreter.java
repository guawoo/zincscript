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
		ArrayList oldFunctionList = functionList;

		functionList = new ArrayList();

		program.statements = interpretStatementArray(program.statements);

		program.functions = Util.listToStatementArray(functionList);

		functionList = oldFunctionList;
		oldFunctionList = null;

		return program;
	}

	public AbstractStatement interpret(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws ParserException {
		functionDeclarationStatement.function = (FunctionLiteral) interpretExpression(functionDeclarationStatement.function);
		return functionDeclarationStatement;
	}

	public AbstractStatement interpret(BlockStatement blockStatement)
			throws ParserException {
		blockStatement.statements = interpretStatementArray(blockStatement.statements);
		return blockStatement;
	}

	public AbstractStatement interpret(BreakStatement breakStatement)
			throws ParserException {
		breakStatement.identifier = interpreteIdentifier(breakStatement.identifier);
		return breakStatement;
	}

	public AbstractStatement interpret(CaseStatement caseStatement)
			throws ParserException {
		caseStatement.expression = interpretExpression(caseStatement.expression);
		caseStatement.statements = interpretStatementArray(caseStatement.statements);
		return caseStatement;
	}

	public AbstractStatement interpret(ContinueStatement continueStatement)
			throws ParserException {
		continueStatement.identifier = interpreteIdentifier(continueStatement.identifier);
		return continueStatement;
	}

	public AbstractStatement interpret(DoStatement doStatement)
			throws ParserException {
		doStatement.statement = interpretStatement(doStatement.statement);
		doStatement.expression = interpretExpression(doStatement.expression);
		return doStatement;
	}

	public AbstractStatement interpret(EmptyStatement emptyStatement)
			throws ParserException {
		return emptyStatement;
	}

	public AbstractStatement interpret(ExpressionStatement expressionStatement)
			throws ParserException {
		expressionStatement.expression = interpretExpression(expressionStatement.expression);
		return expressionStatement;
	}

	public AbstractStatement interpret(ForStatement forStatement)
			throws ParserException {
		forStatement.initial = interpretExpression(forStatement.initial);
		forStatement.condition = interpretExpression(forStatement.condition);
		forStatement.increment = interpretExpression(forStatement.increment);
		forStatement.statement = interpretStatement(forStatement.statement);
		return forStatement;
	}

	public AbstractStatement interpret(ForInStatement forInStatement)
			throws ParserException {
		forInStatement.variable = interpretExpression(forInStatement.variable);
		forInStatement.expression = interpretExpression(forInStatement.expression);
		forInStatement.statement = interpretStatement(forInStatement.statement);
		return forInStatement;
	}

	public AbstractStatement interpret(IfStatement ifStatement)
			throws ParserException {
		ifStatement.expression = interpretExpression(ifStatement.expression);
		ifStatement.trueStatement = interpretStatement(ifStatement.trueStatement);
		ifStatement.falseStatement = interpretStatement(ifStatement.falseStatement);
		return ifStatement;
	}

	public AbstractStatement interpret(LabelledStatement labelledStatement)
			throws ParserException {
		labelledStatement.identifier = interpreteIdentifier(labelledStatement.identifier);
		labelledStatement.statement = interpretStatement(labelledStatement.statement);
		return labelledStatement;
	}

	public AbstractStatement interpret(ReturnStatement returnStatement)
			throws ParserException {
		returnStatement.expression = interpretExpression(returnStatement.expression);
		return returnStatement;
	}

	public AbstractStatement interpret(SwitchStatement switchStatement)
			throws ParserException {
		switchStatement.expression = interpretExpression(switchStatement.expression);
		switchStatement.clauses = (CaseStatement[]) interpretStatementArray(switchStatement.clauses);
		return switchStatement;
	}

	public AbstractStatement interpret(ThrowStatement throwStatement)
			throws ParserException {
		throwStatement.expression = interpretExpression(throwStatement.expression);
		return throwStatement;
	}

	public AbstractStatement interpret(TryStatement tryStatement)
			throws ParserException {
		tryStatement.tryBlock = interpretStatement(tryStatement.tryBlock);
		tryStatement.catchIdentifier = interpreteIdentifier(tryStatement.catchIdentifier);
		tryStatement.catchBlock = interpretStatement(tryStatement.catchBlock);
		tryStatement.finallyBlock = interpretStatement(tryStatement.finallyBlock);
		return tryStatement;
	}

	public AbstractStatement interpret(VariableStatement variableStatement)
			throws ParserException {
		ArrayList statements = new ArrayList();

		for (int i = 0; i < variableStatement.declarations.length; i++) {
			AbstractExpression expression = interpretExpression(variableStatement.declarations[i]);

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
		whileStatement.expression = interpretExpression(whileStatement.expression);
		whileStatement.statement = interpretStatement(whileStatement.statement);

		return whileStatement;
	}

	public AbstractStatement interpret(WithStatement withStatement)
			throws ParserException {
		withStatement.expression = interpretExpression(withStatement.expression);
		withStatement.statement = interpretStatement(withStatement.statement);
		hasWithStatement = true;
		return withStatement;
	}

	public AbstractExpression interpret(
			AssignmentExpression assignmentExpression) throws ParserException {
		return interpretBinaryExpression(assignmentExpression);
	}

	public AbstractExpression interpret(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws ParserException {
		return interpretBinaryExpression(assignmentOperatorExpression);
	}

	public AbstractExpression interpret(
			BinaryOperatorExpression binaryOperatorExpression)
			throws ParserException {
		return interpretBinaryExpression(binaryOperatorExpression);
	}

	public AbstractExpression interpret(
			CallFunctionExpression callFunctionExpression)
			throws ParserException {
		callFunctionExpression.function = interpretExpression(callFunctionExpression.function);
		callFunctionExpression.arguments = interpretExpressionArray(callFunctionExpression.arguments);
		return callFunctionExpression;
	}

	public AbstractExpression interpret(
			ConditionalExpression conditionalExpression) throws ParserException {
		conditionalExpression.expression = interpretExpression(conditionalExpression.expression);
		conditionalExpression.trueExpression = interpretExpression(conditionalExpression.trueExpression);
		conditionalExpression.falseExpression = interpretExpression(conditionalExpression.falseExpression);
		return conditionalExpression;
	}

	public AbstractExpression interpret(DeleteExpression deleteExpression)
			throws ParserException {
		return interpretUnaryExpression(deleteExpression);
	}

	public AbstractExpression interpret(IncrementExpression incrementExpression)
			throws ParserException {
		return interpretUnaryExpression(incrementExpression);
	}

	public AbstractExpression interpret(
			LogicalAndExpression logicalAndExpression) throws ParserException {
		return interpretBinaryExpression(logicalAndExpression);
	}

	public AbstractExpression interpret(LogicalOrExpression logicalOrExpression)
			throws ParserException {
		return interpretBinaryExpression(logicalOrExpression);
	}

	public AbstractExpression interpret(NewExpression newExpression)
			throws ParserException {
		newExpression.objName = interpretExpression(newExpression.objName);
		newExpression.arguments = interpretExpressionArray(newExpression.arguments);
		return newExpression;
	}

	public AbstractExpression interpret(PropertyExpression propertyExpression)
			throws ParserException {
		return interpretBinaryExpression(propertyExpression);
	}

	public AbstractExpression interpret(
			UnaryOperatorExpression unaryOperatorExpression)
			throws ParserException {
		return interpretUnaryExpression(unaryOperatorExpression);
	}

	public AbstractExpression interpret(VariableExpression variableExpression)
			throws ParserException {
		AbstractExpression result = null;

		for (int i = 0; i < variableExpression.declarations.length; i++) {
			AbstractExpression expression = interpretExpression(variableExpression.declarations[i]);

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
		IdentifierLiteral identifier = interpreteIdentifier(variableDeclarationExpression.identifier);
		AbstractExpression initializer = interpretExpression(variableDeclarationExpression.initializer);
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
		arrayLiteral.elements = interpretExpressionArray(arrayLiteral.elements);
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

		functionLiteral.funcName = interpreteIdentifier(functionLiteral.funcName);
		functionLiteral.parameters = interpretIdentifierArray(functionLiteral.parameters);
		functionLiteral.variables = interpretIdentifierArray(functionLiteral.variables);
		functionLiteral.functions = interpretStatementArray(functionLiteral.functions);
		functionLiteral.statements = interpretStatementArray(functionLiteral.statements);

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
		objectLiteral.properties = (ObjectPropertyLiteral[]) interpretExpressionArray(objectLiteral.properties);
		return objectLiteral;
	}

	public AbstractLiteral interpret(ObjectPropertyLiteral objectPropertyLiteral)
			throws ParserException {
		objectPropertyLiteral.name = interpretExpression(objectPropertyLiteral.name);
		objectPropertyLiteral.value = interpretExpression(objectPropertyLiteral.value);
		return objectPropertyLiteral;
	}

	private AbstractStatement interpretStatement(AbstractStatement statement)
			throws ParserException {
		if (statement != null) {
			statement = statement.interpretStatement(this);
		}
		return statement;
	}

	private AbstractStatement[] interpretStatementArray(
			AbstractStatement[] statements) throws ParserException {
		if (statements != null) {
			for (int i = 0; i < statements.length; i++) {
				statements[i] = interpretStatement(statements[i]);
			}
		}

		return statements;
	}

	private AbstractExpression interpretExpression(AbstractExpression expression)
			throws ParserException {
		if (expression != null) {
			expression = expression.interpretExpression(this);
		}

		return expression;
	}

	protected AbstractExpression[] interpretExpressionArray(
			AbstractExpression[] expressions) throws ParserException {
		if (expressions != null) {
			for (int i = 0; i < expressions.length; i++) {
				expressions[i] = interpretExpression(expressions[i]);
			}
		}

		return expressions;
	}

	private IdentifierLiteral interpreteIdentifier(IdentifierLiteral identifier)
			throws ParserException {
		if (identifier != null) {
			identifier = (IdentifierLiteral) identifier
					.interpretExpression(this);
		}

		return identifier;
	}

	private IdentifierLiteral[] interpretIdentifierArray(
			IdentifierLiteral[] identifiers) throws ParserException {
		if (identifiers != null) {
			for (int i = 0; i < identifiers.length; i++) {
				identifiers[i] = interpreteIdentifier(identifiers[i]);
			}
		}

		return identifiers;
	}

	private AbstractExpression interpretBinaryExpression(
			AbstractBinaryExpression expression) throws ParserException {
		expression.leftExpression = interpretExpression(expression.leftExpression);
		expression.rightExpression = interpretExpression(expression.rightExpression);
		return expression;
	}

	private AbstractExpression interpretUnaryExpression(
			AbstractUnaryExpression expression) throws ParserException {
		expression.expression = interpretExpression(expression.expression);
		return expression;
	}

	private void addVariable(IdentifierLiteral identifier) {
		if (variableList.indexOf(identifier) == -1) {
			identifier.index = variableList.size();
			variableList.add(identifier);
		}
	}
}
