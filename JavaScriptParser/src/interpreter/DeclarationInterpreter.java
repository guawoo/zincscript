package interpreter;

import parser.ParserException;
import parser.Token;
import parser.Util;
import utils.ArrayList;
import ast.Program;
import ast.expression.AbstractExpression;
import ast.expression.CallFunctionExpression;
import ast.expression.ConditionalExpression;
import ast.expression.NewExpression;
import ast.expression.VariableDeclarationExpression;
import ast.expression.VariableExpression;
import ast.expression.binary.AbstractBinaryExpression;
import ast.expression.binary.AssignmentExpression;
import ast.expression.binary.AssignmentOperatorExpression;
import ast.expression.binary.BinaryOperatorExpression;
import ast.expression.binary.LogicalAndExpression;
import ast.expression.binary.LogicalOrExpression;
import ast.expression.binary.PropertyExpression;
import ast.expression.literal.AbstractLiteral;
import ast.expression.literal.ArrayLiteral;
import ast.expression.literal.BooleanLiteral;
import ast.expression.literal.FunctionLiteral;
import ast.expression.literal.IdentifierLiteral;
import ast.expression.literal.NullLiteral;
import ast.expression.literal.NumberLiteral;
import ast.expression.literal.ObjectLiteral;
import ast.expression.literal.ObjectPropertyLiteral;
import ast.expression.literal.StringLiteral;
import ast.expression.literal.ThisLiteral;
import ast.expression.unary.AbstractUnaryExpression;
import ast.expression.unary.DeleteExpression;
import ast.expression.unary.IncrementExpression;
import ast.expression.unary.UnaryOperatorExpression;
import ast.statement.AbstractStatement;
import ast.statement.BlockStatement;
import ast.statement.BreakStatement;
import ast.statement.CaseStatement;
import ast.statement.ContinueStatement;
import ast.statement.DoStatement;
import ast.statement.EmptyStatement;
import ast.statement.ExpressionStatement;
import ast.statement.ForInStatement;
import ast.statement.ForStatement;
import ast.statement.FunctionDeclarationStatement;
import ast.statement.IfStatement;
import ast.statement.LabelledStatement;
import ast.statement.ReturnStatement;
import ast.statement.SwitchStatement;
import ast.statement.ThrowStatement;
import ast.statement.TryStatement;
import ast.statement.VariableStatement;
import ast.statement.WhileStatement;
import ast.statement.WithStatement;

/**
 * <code>DeclarationInterpreter</code> 负责对脚本中声明函数和声明变量的语法进行语义分析
 * 
 * @author Jarod Yv
 */
public class DeclarationInterpreter extends AbstractInterpreter {
	/** 函数列表 */
	private ArrayList functionList = null;
	/** 变量列表 */
	private ArrayList variableList = null;
	private boolean hasWithStatement = false;
	/** 标识是否拥有参数 */
	private boolean hasArgumentsVariable = false;
	private boolean hasFunctionLiteral = false;

	// /////////////////////////////// Program ///////////////////////////////
	/**
	 * {@link Program}语法节点中{@link Program#statements}在语法分析时已经建立. 但
	 * {@link Program#functions}尚为空值. {@link Program#functions}用于保存这个脚本程序的函数声明,
	 * 本方法就是遍历的入口, 用于遍历整棵语法树, 抽取函数定义语法
	 * 
	 * @see interpreter.AbstractInterpreter#interpret(ast.Program)
	 */
	public Program interpret(Program program) throws ParserException {
		ArrayList oldFunctionList = functionList;
		functionList = new ArrayList();
		program.statements = interpretStatementArray(program.statements);
		program.functions = Util.listToStatementArray(functionList);
		functionList = oldFunctionList;
		oldFunctionList = null;

		return program;
	}

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Statement //////////////////////////////
	/**
	 * 解释{@link FunctionDeclarationStatement}
	 */
	public AbstractStatement interpret(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws ParserException {
		functionDeclarationStatement.function = (FunctionLiteral) interpretExpression(functionDeclarationStatement.function);
		return functionDeclarationStatement;
	}

	/**
	 * 解释{@link BlockStatement}
	 */
	public AbstractStatement interpret(BlockStatement blockStatement)
			throws ParserException {
		blockStatement.statementList = interpretStatementArray(blockStatement.statementList);
		return blockStatement;
	}

	/**
	 * 解释{@link BreakStatement}
	 */
	public AbstractStatement interpret(BreakStatement breakStatement)
			throws ParserException {
		breakStatement.identifier = interpreteIdentifier(breakStatement.identifier);
		return breakStatement;
	}

	/**
	 * 解释{@link CaseStatement}
	 */
	public AbstractStatement interpret(CaseStatement caseStatement)
			throws ParserException {
		caseStatement.expression = interpretExpression(caseStatement.expression);
		caseStatement.statementList = interpretStatementArray(caseStatement.statementList);
		return caseStatement;
	}

	/**
	 * 解释{@link ContinueStatement}
	 */
	public AbstractStatement interpret(ContinueStatement continueStatement)
			throws ParserException {
		continueStatement.identifier = interpreteIdentifier(continueStatement.identifier);
		return continueStatement;
	}

	/**
	 * 解释{@link DoStatement}
	 */
	public AbstractStatement interpret(DoStatement doStatement)
			throws ParserException {
		doStatement.statement = interpretStatement(doStatement.statement);
		doStatement.expression = interpretExpression(doStatement.expression);
		return doStatement;
	}

	/**
	 * 解释{@link EmptyStatement}
	 */
	public AbstractStatement interpret(EmptyStatement emptyStatement)
			throws ParserException {
		return emptyStatement;
	}

	/**
	 * 解释{@link ExpressionStatement}
	 */
	public AbstractStatement interpret(ExpressionStatement expressionStatement)
			throws ParserException {
		expressionStatement.expression = interpretExpression(expressionStatement.expression);
		return expressionStatement;
	}

	/**
	 * 解释{@link ForStatement}
	 */
	public AbstractStatement interpret(ForStatement forStatement)
			throws ParserException {
		forStatement.initial = interpretExpression(forStatement.initial);
		forStatement.condition = interpretExpression(forStatement.condition);
		forStatement.increment = interpretExpression(forStatement.increment);
		forStatement.statement = interpretStatement(forStatement.statement);
		return forStatement;
	}

	/**
	 * 解释{@link ForInStatement}
	 */
	public AbstractStatement interpret(ForInStatement forInStatement)
			throws ParserException {
		forInStatement.variable = interpretExpression(forInStatement.variable);
		forInStatement.expression = interpretExpression(forInStatement.expression);
		forInStatement.statement = interpretStatement(forInStatement.statement);
		return forInStatement;
	}

	/**
	 * 解释{@link IfStatement}
	 */
	public AbstractStatement interpret(IfStatement ifStatement)
			throws ParserException {
		ifStatement.expression = interpretExpression(ifStatement.expression);
		ifStatement.trueStatement = interpretStatement(ifStatement.trueStatement);
		ifStatement.falseStatement = interpretStatement(ifStatement.falseStatement);
		return ifStatement;
	}

	/**
	 * 解释{@link LabelledStatement}
	 */
	public AbstractStatement interpret(LabelledStatement labelledStatement)
			throws ParserException {
		labelledStatement.identifier = interpreteIdentifier(labelledStatement.identifier);
		labelledStatement.statement = interpretStatement(labelledStatement.statement);
		return labelledStatement;
	}

	/**
	 * 解释{@link ReturnStatement}
	 */
	public AbstractStatement interpret(ReturnStatement returnStatement)
			throws ParserException {
		returnStatement.expression = interpretExpression(returnStatement.expression);
		return returnStatement;
	}

	/**
	 * 解释{@link SwitchStatement}
	 */
	public AbstractStatement interpret(SwitchStatement switchStatement)
			throws ParserException {
		switchStatement.expression = interpretExpression(switchStatement.expression);
		switchStatement.clauses = (CaseStatement[]) interpretStatementArray(switchStatement.clauses);
		return switchStatement;
	}

	/**
	 * 解释{@link ThrowStatement}
	 */
	public AbstractStatement interpret(ThrowStatement throwStatement)
			throws ParserException {
		throwStatement.expression = interpretExpression(throwStatement.expression);
		return throwStatement;
	}

	/**
	 * 解释{@link TryStatement}
	 */
	public AbstractStatement interpret(TryStatement tryStatement)
			throws ParserException {
		tryStatement.tryBlock = interpretStatement(tryStatement.tryBlock);
		tryStatement.catchIdentifier = interpreteIdentifier(tryStatement.catchIdentifier);
		tryStatement.catchBlock = interpretStatement(tryStatement.catchBlock);
		tryStatement.finallyBlock = interpretStatement(tryStatement.finallyBlock);
		return tryStatement;
	}

	/**
	 * 解释{@link VariableStatement}
	 */
	public AbstractStatement interpret(VariableStatement variableStatement)
			throws ParserException {
		ArrayList statements = new ArrayList(
				variableStatement.declarations.length);

		for (int i = 0; i < variableStatement.declarations.length; i++) {
			AbstractExpression expression = interpretExpression(variableStatement.declarations[i]);
			if (expression != null) {
				AbstractStatement statement = new ExpressionStatement(
						expression);
				statement.setLineNumber(variableStatement.getLineNumber());
				statements.add(statement);
				statement = null;
			}
			expression = null;
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

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Expression /////////////////////////////
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

	// ///////////////////////////////////////////////////////////////////////

	// /////////////////////////////// Literal ///////////////////////////////
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

	// ///////////////////////////////////////////////////////////////////////

	/**
	 * 逐条解释语句的语法
	 * 
	 * @param statements
	 *            语句集合
	 * @return
	 * @throws ParserException
	 */
	private AbstractStatement[] interpretStatementArray(
			AbstractStatement[] statements) throws ParserException {
		if (statements != null) {
			for (int i = 0; i < statements.length; i++) {
				statements[i] = interpretStatement(statements[i]);
			}
		}
		return statements;
	}

	/**
	 * 解释单条语句的语法
	 * 
	 * @param statement
	 *            单条语句
	 * @return
	 * @throws ParserException
	 */
	private AbstractStatement interpretStatement(AbstractStatement statement)
			throws ParserException {
		if (statement != null) {
			statement = statement.interpretStatement(this);
		}
		return statement;
	}

	private AbstractExpression interpretExpression(AbstractExpression expression)
			throws ParserException {
		if (expression != null) {
			expression = expression.interpretExpression(this);
		}

		return expression;
	}

	private AbstractExpression[] interpretExpressionArray(
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
