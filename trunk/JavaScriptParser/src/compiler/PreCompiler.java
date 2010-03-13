package compiler;

import parser.Token;
import utils.ArrayList;
import utils.ListUtil;
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
import ast.statement.DoWhileStatement;
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
public class PreCompiler implements ICompilable {
	private static final String ARGUMENTS = "arguments";

	/** 函数列表 */
	private ArrayList functionList = null;

	/** 变量列表 */
	private ArrayList variableList = null;

	/** 标识是否是<b>with</b>语句 */
	private boolean hasWithStatement = false;

	/** 标识是否拥有参数 */
	private boolean hasArgumentsVariable = false;

	/** 标识是否是函数声明 */
	private boolean hasFunctionLiteral = false;

	// /////////////////////////////// Program ///////////////////////////////
	/**
	 * {@link Program}语法节点中{@link Program#statements}在语法分析时已经建立. 但
	 * {@link Program#functions}尚为空值. {@link Program#functions}用于保存这个脚本程序的函数声明,
	 * 本方法就是遍历的入口, 用于遍历整棵语法树, 抽取函数定义语法
	 * 
	 * @see compiler.AbstractInterpreter#compile(ast.Program)
	 */
	public Program compile(Program program) throws CompilerException {
		ArrayList oldFunctionList = functionList;
		functionList = new ArrayList();
		program.statements = interpretStatementArray(program.statements);
		program.functions = ListUtil.list2StatementArray(functionList);
		functionList = oldFunctionList;
		oldFunctionList = null;

		return program;
	}

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Statement //////////////////////////////
	/**
	 * 解释{@link FunctionDeclarationStatement}
	 */
	public AbstractStatement compile(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws CompilerException {
		functionDeclarationStatement.function = (FunctionLiteral) interpretExpression(functionDeclarationStatement.function);
		functionList.add(new ExpressionStatement(
				functionDeclarationStatement.function));
		return new EmptyStatement();
	}

	/**
	 * 解释{@link BlockStatement}
	 */
	public AbstractStatement compile(BlockStatement blockStatement)
			throws CompilerException {
		blockStatement.statementList = interpretStatementArray(blockStatement.statementList);
		return blockStatement;
	}

	/**
	 * 解释{@link BreakStatement}
	 */
	public AbstractStatement compile(BreakStatement breakStatement)
			throws CompilerException {
		breakStatement.identifier = interpreteIdentifier(breakStatement.identifier);
		return breakStatement;
	}

	/**
	 * 解释{@link CaseStatement}
	 */
	public AbstractStatement compile(CaseStatement caseStatement)
			throws CompilerException {
		caseStatement.expression = interpretExpression(caseStatement.expression);
		caseStatement.statementList = interpretStatementArray(caseStatement.statementList);
		return caseStatement;
	}

	/**
	 * 解释{@link ContinueStatement}
	 */
	public AbstractStatement compile(ContinueStatement continueStatement)
			throws CompilerException {
		continueStatement.identifier = interpreteIdentifier(continueStatement.identifier);
		return continueStatement;
	}

	/**
	 * 解释{@link DoWhileStatement}
	 */
	public AbstractStatement compile(DoWhileStatement doStatement)
			throws CompilerException {
		doStatement.statement = interpretStatement(doStatement.statement);
		doStatement.expression = interpretExpression(doStatement.expression);
		return doStatement;
	}

	/**
	 * 解释{@link EmptyStatement}
	 */
	public AbstractStatement compile(EmptyStatement emptyStatement)
			throws CompilerException {
		return emptyStatement;
	}

	/**
	 * 解释{@link ExpressionStatement}
	 */
	public AbstractStatement compile(ExpressionStatement expressionStatement)
			throws CompilerException {
		expressionStatement.expression = interpretExpression(expressionStatement.expression);
		return expressionStatement;
	}

	/**
	 * 解释{@link ForStatement}
	 */
	public AbstractStatement compile(ForStatement forStatement)
			throws CompilerException {
		forStatement.initial = interpretExpression(forStatement.initial);
		forStatement.condition = interpretExpression(forStatement.condition);
		forStatement.increment = interpretExpression(forStatement.increment);
		forStatement.statement = interpretStatement(forStatement.statement);
		return forStatement;
	}

	/**
	 * 解释{@link ForInStatement}
	 */
	public AbstractStatement compile(ForInStatement forInStatement)
			throws CompilerException {
		forInStatement.variable = interpretExpression(forInStatement.variable);
		forInStatement.expression = interpretExpression(forInStatement.expression);
		forInStatement.statement = interpretStatement(forInStatement.statement);
		return forInStatement;
	}

	/**
	 * 解释{@link IfStatement}
	 */
	public AbstractStatement compile(IfStatement ifStatement)
			throws CompilerException {
		ifStatement.expression = interpretExpression(ifStatement.expression);
		ifStatement.trueStatement = interpretStatement(ifStatement.trueStatement);
		ifStatement.falseStatement = interpretStatement(ifStatement.falseStatement);
		return ifStatement;
	}

	/**
	 * 解释{@link LabelledStatement}
	 */
	public AbstractStatement compile(LabelledStatement labelledStatement)
			throws CompilerException {
		labelledStatement.identifier = interpreteIdentifier(labelledStatement.identifier);
		labelledStatement.statement = interpretStatement(labelledStatement.statement);
		return labelledStatement;
	}

	/**
	 * 解释{@link ReturnStatement}
	 */
	public AbstractStatement compile(ReturnStatement returnStatement)
			throws CompilerException {
		returnStatement.expression = interpretExpression(returnStatement.expression);
		return returnStatement;
	}

	/**
	 * 解释{@link SwitchStatement}
	 */
	public AbstractStatement compile(SwitchStatement switchStatement)
			throws CompilerException {
		switchStatement.expression = interpretExpression(switchStatement.expression);
		switchStatement.cases = (CaseStatement[]) interpretStatementArray(switchStatement.cases);
		return switchStatement;
	}

	/**
	 * 解释{@link ThrowStatement}
	 */
	public AbstractStatement compile(ThrowStatement throwStatement)
			throws CompilerException {
		throwStatement.expression = interpretExpression(throwStatement.expression);
		return throwStatement;
	}

	/**
	 * 解释{@link TryStatement}
	 */
	public AbstractStatement compile(TryStatement tryStatement)
			throws CompilerException {
		tryStatement.tryBlock = interpretStatement(tryStatement.tryBlock);
		tryStatement.catchIdentifier = interpreteIdentifier(tryStatement.catchIdentifier);
		tryStatement.catchBlock = interpretStatement(tryStatement.catchBlock);
		tryStatement.finallyBlock = interpretStatement(tryStatement.finallyBlock);
		return tryStatement;
	}

	/**
	 * 解释{@link VariableStatement}
	 * <p>
	 * 由于变量生命语句包含多个变量的声明, 在此需要将写在一句内的多个变量生命拆成多条语句
	 */
	public AbstractStatement compile(VariableStatement variableStatement)
			throws CompilerException {
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

		if (statements.size() == 0) {// 无变量声明则返回空语句
			return new EmptyStatement();
		} else if (statements.size() == 1) {// 一个变量, 返回此变量的声明语句
			return (ExpressionStatement) statements.get(0);
		} else {// 多个变量则将他们的声明语句组成代码块
			return new BlockStatement(ListUtil.list2StatementArray(statements));
		}
	}

	public AbstractStatement compile(WhileStatement whileStatement)
			throws CompilerException {
		whileStatement.expression = interpretExpression(whileStatement.expression);
		whileStatement.statement = interpretStatement(whileStatement.statement);

		return whileStatement;
	}

	/**
	 * 
	 */
	public AbstractStatement compile(WithStatement withStatement)
			throws CompilerException {
		withStatement.expression = interpretExpression(withStatement.expression);
		withStatement.statement = interpretStatement(withStatement.statement);
		hasWithStatement = true;
		return withStatement;
	}

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Expression /////////////////////////////
	public AbstractExpression compile(
			AssignmentExpression assignmentExpression) throws CompilerException {
		return interpretBinaryExpression(assignmentExpression);
	}

	public AbstractExpression compile(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws CompilerException {
		return interpretBinaryExpression(assignmentOperatorExpression);
	}

	public AbstractExpression compile(
			BinaryOperatorExpression binaryOperatorExpression)
			throws CompilerException {
		return interpretBinaryExpression(binaryOperatorExpression);
	}

	public AbstractExpression compile(
			CallFunctionExpression callFunctionExpression)
			throws CompilerException {
		callFunctionExpression.function = interpretExpression(callFunctionExpression.function);
		callFunctionExpression.arguments = interpretExpressionArray(callFunctionExpression.arguments);
		return callFunctionExpression;
	}

	public AbstractExpression compile(
			ConditionalExpression conditionalExpression) throws CompilerException {
		conditionalExpression.expression = interpretExpression(conditionalExpression.expression);
		conditionalExpression.trueExpression = interpretExpression(conditionalExpression.trueExpression);
		conditionalExpression.falseExpression = interpretExpression(conditionalExpression.falseExpression);
		return conditionalExpression;
	}

	public AbstractExpression compile(DeleteExpression deleteExpression)
			throws CompilerException {
		return interpretUnaryExpression(deleteExpression);
	}

	public AbstractExpression compile(IncrementExpression incrementExpression)
			throws CompilerException {
		return interpretUnaryExpression(incrementExpression);
	}

	public AbstractExpression compile(
			LogicalAndExpression logicalAndExpression) throws CompilerException {
		return interpretBinaryExpression(logicalAndExpression);
	}

	public AbstractExpression compile(LogicalOrExpression logicalOrExpression)
			throws CompilerException {
		return interpretBinaryExpression(logicalOrExpression);
	}

	public AbstractExpression compile(NewExpression newExpression)
			throws CompilerException {
		newExpression.function = interpretExpression(newExpression.function);
		newExpression.arguments = interpretExpressionArray(newExpression.arguments);
		return newExpression;
	}

	public AbstractExpression compile(PropertyExpression propertyExpression)
			throws CompilerException {
		return interpretBinaryExpression(propertyExpression);
	}

	public AbstractExpression compile(
			UnaryOperatorExpression unaryOperatorExpression)
			throws CompilerException {
		return interpretUnaryExpression(unaryOperatorExpression);
	}

	/**
	 * 
	 */
	public AbstractExpression compile(VariableExpression variableExpression)
			throws CompilerException {
		AbstractExpression result = null;

		for (int i = 0; i < variableExpression.declarations.length; i++) {
			AbstractExpression expression = interpretExpression(variableExpression.declarations[i]);
			if (expression != null) {
				if (result == null) {// 单变量声明
					result = expression;
				} else {// 用','号声明多变量
					result = new BinaryOperatorExpression(result, expression,
							Token.OPERATOR_COMMA);
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	public AbstractExpression compile(
			VariableDeclarationExpression variableDeclarationExpression)
			throws CompilerException {
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
	/**
	 * 解释{@link IdentifierLiteral}
	 * <p>
	 * // * 如果identifier为<b><code>"arguments"</code></b>, 则
	 * {@link #hasArgumentsVariable}为<code>true</code>
	 */
	public AbstractLiteral compile(IdentifierLiteral identifierLiteral)
			throws CompilerException{
		if (ARGUMENTS.equals(identifierLiteral.string)) {
			hasArgumentsVariable = true;
		}
		return identifierLiteral;
	}

	public AbstractLiteral compile(ThisLiteral thisLiteral)
			throws CompilerException {
		return thisLiteral;
	}

	public AbstractLiteral compile(NullLiteral nullLiteral)
			throws CompilerException {
		return nullLiteral;
	}

	public AbstractLiteral compile(BooleanLiteral booleanLiteral)
			throws CompilerException {
		return booleanLiteral;
	}

	public AbstractLiteral compile(NumberLiteral numberLiteral)
			throws CompilerException {
		return numberLiteral;
	}

	public AbstractLiteral compile(StringLiteral stringLiteral)
			throws CompilerException {
		return stringLiteral;
	}

	public AbstractLiteral compile(ArrayLiteral arrayLiteral)
			throws CompilerException {
		arrayLiteral.elements = interpretExpressionArray(arrayLiteral.elements);
		return arrayLiteral;
	}

	/**
	 * 
	 */
	public AbstractLiteral compile(FunctionLiteral functionLiteral)
			throws CompilerException {
		// 保存现场
		ArrayList oldFunctionVector = functionList;
		ArrayList oldVariableVector = variableList;
		boolean oldHasWithStatement = hasWithStatement;
		boolean oldHasArgumentsVariable = hasArgumentsVariable;

		// 初始化新环境
		functionList = new ArrayList();
		variableList = new ArrayList();
		hasWithStatement = false;
		hasArgumentsVariable = false;
		hasFunctionLiteral = false;

		// 读取函数的参数列表
		IdentifierLiteral[] parameters = functionLiteral.parameters;
		for (int i = 0; i < parameters.length; i++) {
			addVariable(parameters[i]);
		}

		functionLiteral.funcName = interpreteIdentifier(functionLiteral.funcName);
		functionLiteral.parameters = interpretIdentifierArray(functionLiteral.parameters);
		functionLiteral.statements = interpretStatementArray(functionLiteral.statements);

		// 赋值
		functionLiteral.functions = ListUtil.list2StatementArray(functionList);
		functionLiteral.variables = ListUtil.list2IdentifierArray(variableList);

		// 如果本函数定义包含其他的函数定义或者包含with语句或者包含arguments引用, 则关闭本函数的优化
		functionLiteral.enableLocalsOptimization = !(hasWithStatement
				| hasArgumentsVariable | hasFunctionLiteral);

		// 恢复现场
		functionList = oldFunctionVector;
		variableList = oldVariableVector;
		hasWithStatement = oldHasWithStatement;
		hasArgumentsVariable = oldHasArgumentsVariable;

		hasFunctionLiteral = true;

		return functionLiteral;
	}

	public AbstractLiteral compile(ObjectLiteral objectLiteral)
			throws CompilerException {
		objectLiteral.properties = (ObjectPropertyLiteral[]) interpretExpressionArray(objectLiteral.properties);
		return objectLiteral;
	}

	public AbstractLiteral compile(ObjectPropertyLiteral objectPropertyLiteral)
			throws CompilerException {
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
	 * @throws CompilerException
	 */
	private AbstractStatement[] interpretStatementArray(
			AbstractStatement[] statements) throws CompilerException {
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
	 * @throws CompilerException
	 */
	private AbstractStatement interpretStatement(AbstractStatement statement)
			throws CompilerException {
		if (statement != null) {
			statement = statement.compileStatement(this);
		}
		return statement;
	}

	private AbstractExpression interpretExpression(AbstractExpression expression)
			throws CompilerException {
		if (expression != null) {
			expression = expression.compileExpression(this);
		}

		return expression;
	}

	private AbstractExpression[] interpretExpressionArray(
			AbstractExpression[] expressions) throws CompilerException {
		if (expressions != null) {
			for (int i = 0; i < expressions.length; i++) {
				expressions[i] = interpretExpression(expressions[i]);
			}
		}

		return expressions;
	}

	private IdentifierLiteral interpreteIdentifier(IdentifierLiteral identifier)
			throws CompilerException {
		if (identifier != null) {
			identifier = (IdentifierLiteral) identifier.compileExpression(this);
		}

		return identifier;
	}

	private IdentifierLiteral[] interpretIdentifierArray(
			IdentifierLiteral[] identifiers) throws CompilerException {
		if (identifiers != null) {
			for (int i = 0; i < identifiers.length; i++) {
				identifiers[i] = interpreteIdentifier(identifiers[i]);
			}
		}

		return identifiers;
	}

	private AbstractExpression interpretBinaryExpression(
			AbstractBinaryExpression expression) throws CompilerException {
		expression.leftExpression = interpretExpression(expression.leftExpression);
		expression.rightExpression = interpretExpression(expression.rightExpression);
		return expression;
	}

	private AbstractExpression interpretUnaryExpression(
			AbstractUnaryExpression expression) throws CompilerException {
		expression.expression = interpretExpression(expression.expression);
		return expression;
	}

	/**
	 * 向变量表中添加变量定义
	 * 
	 * @param identifier
	 *            变量定义表达式
	 */
	private void addVariable(IdentifierLiteral identifier) {
		if (variableList.indexOf(identifier) == -1) {// 如果变量不存在则加入变量列表
			identifier.index = variableList.size();
			variableList.add(identifier);
		}
	}
}
