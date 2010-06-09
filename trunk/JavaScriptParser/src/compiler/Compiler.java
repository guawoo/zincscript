package compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import parser.Config;
import parser.Token;
import utils.ArrayList;
import vm.VirtualMachine;
import ast.AbstractSyntaxNode;
import ast.Program;
import ast.expression.AbstractExpression;
import ast.expression.CallFunctionExpression;
import ast.expression.ConditionalExpression;
import ast.expression.NewExpression;
import ast.expression.VariableDeclarationExpression;
import ast.expression.VariableExpression;
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
 * <code>Compiler</code> 负责将语法树编译成可执行的中间代码.
 * <p>
 * 整个过程分为2步, 会对语法树进行2次遍历：
 * <ol>
 * <li>分析确定偏移
 * <li>生成中间代码
 * </ol>
 * 
 * @author Jarod Yv
 */
public class Compiler implements ICompilable {
	private DataOutputStream dos = null;
	private ByteArrayOutputStream codeStream = new ByteArrayOutputStream(0);

	/** 全局字符串表 */
	private Hashtable globalStringMap = new Hashtable();
	private ArrayList globalStringTable = new ArrayList();

	/** 函数定义列表 */
	private ArrayList functionLiterals = new ArrayList();

	/** 数字定义列表 */
	private ArrayList numberLiterals = new ArrayList();

	/** 字符串定义列表 */
	private ArrayList stringLiterals = new ArrayList();

	/** 本地变量表 */
	private Hashtable localVariableTable = new Hashtable();

	/** 跳转表记表 */
	private Hashtable jumpLabels = new Hashtable();

	private ArrayList unresolvedJumps = new ArrayList();
	private ArrayList labelSet = new ArrayList();

	private ArrayList lineNumberList = new ArrayList();

	private AbstractExpression pendingAssignment;

	private AbstractStatement currentBreakStatement;
	private AbstractStatement currentContinueStatement;
	private AbstractStatement currentTryStatement;
	private String currentTryLabel;

	private boolean enableLocalsOptimization = false;

	private Compiler parent = null;

	/**
	 * 构造函数
	 * 
	 * @param dos
	 *            源代码数据流
	 */
	public Compiler(DataOutputStream dos) {
		this.dos = dos;
		this.globalStringMap = new Hashtable();
		this.globalStringTable = new ArrayList();
	}

	// /////////////////////////////// Program ///////////////////////////////
	public Program compile(Program program) throws CompilerException {
		for (int i = 0; i < program.functions.length; i++) {
			program.functions[i].compileStatement(this);
		}

		for (int i = 0; i < program.statements.length; i++) {
			program.statements[i].compileStatement(this);
		}

		writeGlobalStringTableBlock();
		writeStringLiteralBlock();
		writeNumberLiteralBlock();
		writeFunctionLiteralBlock();
		writeCodeBlock(0, 0, 0x00, codeStream.toByteArray());
		writeLineNumberBlock();
		writeEndMarker();

		return program;
	}

	/**
	 * @param parent
	 * @param function
	 * @param dos
	 * @throws CompilerException
	 */
	private void CompileFunction(Compiler parent, FunctionLiteral function,
			DataOutputStream dos) throws CompilerException {
		this.parent = parent;
		this.globalStringMap = this.parent.globalStringMap;
		this.globalStringTable = this.parent.globalStringTable;
		this.dos = dos;
		this.enableLocalsOptimization = function.enableLocalsOptimization;

		// 将函数的变量写入局部变量表
		for (int i = 0; i < function.variables.length; i++) {
			IdentifierLiteral variable = function.variables[i];
			addToGlobalStringTable(variable.identifierName);
			localVariableTable.put(variable, variable);
		}

		// for (int i = 0; i < function.variables.length; i++) {
		// addToGlobalStringTable(function.variables[i].string);
		// }

		// 处理函数中的函数
		for (int i = 0; i < function.functions.length; i++) {
			if (function.functions[i] != null) {
				function.functions[i].compileStatement(this);
			}
		}

		// 处理函数中的语句
		for (int i = 0; i < function.statements.length; i++) {
			if (function.statements[i] != null) {
				function.statements[i].compileStatement(this);
			}
		}

		byte[] byteCode = codeStream.toByteArray();

		// TODO remove this magic numbers.
		int flags = Config.FASTLOCALS && function.enableLocalsOptimization ? 0x01
				: 0x00;

		// if (function.funcName != null) {
		// writeCommentBlock("function " + function.funcName.string);
		// }

		writeStringLiteralBlock();
		writeNumberLiteralBlock();
		writeFunctionLiteralBlock();
		writeLocalVariableNameBlock(function.variables);
		writeCodeBlock(function.variables.length, function.parameters.length,
				flags, byteCode);
		writeLineNumberBlock();
		writeEndMarker();
	}

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Statement //////////////////////////////
	public AbstractStatement compile(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws CompilerException {
		addLineNumber(functionDeclarationStatement);
		functionDeclarationStatement.function.compileExpression(this);
		writeOp(VirtualMachine.OP_DROP);
		return functionDeclarationStatement;
	}

	public AbstractStatement compile(BlockStatement blockStatement)
			throws CompilerException {
		for (int i = 0; i < blockStatement.statementList.length; i++) {
			blockStatement.statementList[i].compileStatement(this);
		}
		return blockStatement;
	}

	public AbstractStatement compile(BreakStatement breakStatement)
			throws CompilerException {
		addLineNumber(breakStatement);
		writeJump(
				VirtualMachine.XOP_GO,
				breakStatement.identifier == null ? (Object) currentBreakStatement
						: breakStatement.identifier.identifierName, "break");
		return breakStatement;
	}

	public AbstractStatement compile(CaseStatement caseStatement)
			throws CompilerException {
		throw new RuntimeException("should not be visited");
	}

	public AbstractStatement compile(ContinueStatement continueStatement)
			throws CompilerException {
		addLineNumber(continueStatement);
		writeJump(
				VirtualMachine.XOP_GO,
				continueStatement.identifier == null ? (Object) currentBreakStatement
						: continueStatement.identifier.identifierName, "continue");
		return continueStatement;
	}

	public AbstractStatement compile(DoWhileStatement doStatement)
			throws CompilerException {
		addLineNumber(doStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;
		currentBreakStatement = doStatement;
		currentContinueStatement = doStatement;

		setLabel(doStatement, "do");

		visitWithNewLabelSet(doStatement.statement);

		setLabel(doStatement, "continue");
		visitWithNewLabelSet(doStatement.expression);
		writeOp(VirtualMachine.OP_NOT);
		writeJump(VirtualMachine.XOP_IF, doStatement, "do");
		setLabel(doStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;
		return doStatement;
	}

	public AbstractStatement compile(EmptyStatement emptyStatement)
			throws CompilerException {
		return emptyStatement;
	}

	public AbstractStatement compile(ExpressionStatement expressionStatement)
			throws CompilerException {
		addLineNumber(expressionStatement);
		expressionStatement.expression.compileExpression(this);
		writeOp(VirtualMachine.OP_DROP);
		return expressionStatement;
	}

	public AbstractStatement compile(ForStatement forStatement)
			throws CompilerException {
		addLineNumber(forStatement);

		if (forStatement.initial != null) {
			forStatement.initial.compileExpression(this);
			if (!(forStatement.initial instanceof VariableExpression)) {
				writeOp(VirtualMachine.OP_DROP);
			}
		}

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = forStatement;
		currentContinueStatement = forStatement;

		setLabel(forStatement, "start");

		if (forStatement.condition != null) {
			visitWithNewLabelSet(forStatement.condition);
			writeJump(VirtualMachine.XOP_IF, forStatement, "break");
		}

		if (forStatement.statement != null) {
			visitWithNewLabelSet(forStatement.statement);
		}

		setLabel(forStatement, "continue");

		if (forStatement.increment != null) {
			visitWithNewLabelSet(forStatement.increment);
			writeOp(VirtualMachine.OP_DROP);
		}

		writeJump(VirtualMachine.XOP_GO, forStatement, "start");

		setLabel(forStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;

		return forStatement;
	}

	public AbstractStatement compile(ForInStatement forInStatement)
			throws CompilerException {
		addLineNumber(forInStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = forInStatement;
		currentContinueStatement = forInStatement;

		forInStatement.expression.compileExpression(this);
		writeOp(VirtualMachine.OP_ENUM);
		setLabel(forInStatement, "continue");
		writeJump(VirtualMachine.XOP_NEXT, forInStatement, "break");

		if (forInStatement.variable instanceof IdentifierLiteral) {
			writeXop(
					VirtualMachine.XOP_PUSH_STR,
					getStringLiteralIndex(((IdentifierLiteral) forInStatement.variable).identifierName));
			writeOp(VirtualMachine.OP_CTX_SET);
		} else if (forInStatement.variable instanceof VariableDeclarationExpression) {
			writeVarDef(
					((VariableDeclarationExpression) forInStatement.variable).identifier.identifierName,
					true);
		} else {
			throw new IllegalArgumentException();
		}
		writeOp(VirtualMachine.OP_DROP);

		forInStatement.statement.compileStatement(this);
		writeJump(VirtualMachine.XOP_GO, forInStatement, "continue");
		setLabel(forInStatement, "break");
		writeOp(VirtualMachine.OP_DROP);

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;

		return forInStatement;
	}

	public AbstractStatement compile(IfStatement ifStatement)
			throws CompilerException {
		addLineNumber(ifStatement);

		ifStatement.expression.compileExpression(this);
		if (ifStatement.falseStatement == null) {
			writeJump(VirtualMachine.XOP_IF, ifStatement, "endif");
			ifStatement.trueStatement.compileStatement(this);
		} else {
			writeJump(VirtualMachine.XOP_IF, ifStatement, "else");
			ifStatement.trueStatement.compileStatement(this);
			writeJump(VirtualMachine.XOP_GO, ifStatement, "endif");
			setLabel(ifStatement, "else");
			ifStatement.falseStatement.compileStatement(this);
		}
		setLabel(ifStatement, "endif");
		return ifStatement;
	}

	public AbstractStatement compile(LabelledStatement labelledStatement)
			throws CompilerException {
		labelSet.add(labelledStatement.identifier.identifierName);
		labelledStatement.statement.compileStatement(this);
		return labelledStatement;
	}

	public AbstractStatement compile(ReturnStatement returnStatement)
			throws CompilerException {
		addLineNumber(returnStatement);

		if (returnStatement.expression == null) {
			writeOp(VirtualMachine.OP_PUSH_UNDEF);
		} else {
			returnStatement.expression.compileExpression(this);
		}
		writeOp(VirtualMachine.OP_RET);
		return returnStatement;
	}

	public AbstractStatement compile(SwitchStatement switchStatement)
			throws CompilerException {
		addLineNumber(switchStatement);

		switchStatement.expression.compileExpression(this);

		AbstractStatement saveBreakStatemet = currentBreakStatement;
		currentBreakStatement = switchStatement;

		String defaultLabel = "break";

		for (int i = 0; i < switchStatement.cases.length; i++) {
			CaseStatement cs = switchStatement.cases[i];
			if (cs.expression == null) {
				defaultLabel = "case" + i;
			} else {
				writeOp(VirtualMachine.OP_DUP);
				cs.expression.compileExpression(this);
				writeOp(VirtualMachine.OP_EQEQEQ);
				writeOp(VirtualMachine.OP_NOT);
				writeJump(VirtualMachine.XOP_IF, switchStatement, "case" + i);
			}
		}

		writeOp(VirtualMachine.OP_DROP);
		writeJump(VirtualMachine.XOP_GO, switchStatement, defaultLabel);

		for (int i = 0; i < switchStatement.cases.length; i++) {
			setLabel(switchStatement, "case" + i);
			AbstractStatement[] statements = switchStatement.cases[i].statementList;
			for (int j = 0; j < statements.length; j++) {
				statements[j].compileStatement(this);
			}
		}
		setLabel(switchStatement, "break");

		currentBreakStatement = saveBreakStatemet;
		return switchStatement;
	}

	public AbstractStatement compile(ThrowStatement throwStatement)
			throws CompilerException {
		addLineNumber(throwStatement);

		throwStatement.expression.compileExpression(this);
		if (currentTryStatement == null) {
			writeOp(VirtualMachine.OP_THROW);
		} else {
			writeJump(VirtualMachine.XOP_GO, currentTryStatement, "catch");
		}
		return throwStatement;
	}

	public AbstractStatement compile(TryStatement tryStatement)
			throws CompilerException {
		addLineNumber(tryStatement);

		AbstractStatement saveTryStatement = currentTryStatement;
		String saveTryLabel = currentTryLabel;

		currentTryStatement = tryStatement;
		currentTryLabel = tryStatement.catchBlock != null ? "catch" : "finally";

		tryStatement.tryBlock.compileStatement(this);

		writeJump(VirtualMachine.XOP_GO, tryStatement, "end");

		if (tryStatement.catchBlock != null) {
			setLabel(tryStatement, "catch");
			if (tryStatement.finallyBlock == null) {
				currentTryLabel = saveTryLabel;
				currentTryStatement = saveTryStatement;
			} else {
				currentTryLabel = "finally";
			}

			// add var and init from stack
			writeVarDef(tryStatement.catchIdentifier.identifierName, true);
			writeOp(VirtualMachine.OP_DROP);
			tryStatement.catchBlock.compileStatement(this);

			writeJump(VirtualMachine.XOP_GO, tryStatement, "end");
		}

		// reset everything
		currentTryStatement = saveTryStatement;
		currentTryLabel = saveTryLabel;

		if (tryStatement.finallyBlock != null) {
			// finally block for the case that an exception was thrown --
			// it is kept on the stack and rethrown at the end
			setLabel(tryStatement, "finally");
			tryStatement.finallyBlock.compileStatement(this);

			if (currentTryStatement == null) {
				writeOp(VirtualMachine.OP_THROW);
			} else {
				writeJump(VirtualMachine.XOP_GO, currentTryStatement, "catch");
			}
		}

		// finally block if no exception was thrown
		setLabel(tryStatement, "end");

		if (tryStatement.finallyBlock != null) {
			tryStatement.finallyBlock.compileStatement(this);
		}

		return tryStatement;
	}

	public AbstractStatement compile(VariableStatement variableStatement)
			throws CompilerException {
		for (int i = 0; i < variableStatement.declarations.length; i++) {
			variableStatement.declarations[i].compileExpression(this);
		}
		return variableStatement;
	}

	public AbstractStatement compile(WhileStatement whileStatement)
			throws CompilerException {
		addLineNumber(whileStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = whileStatement;
		currentContinueStatement = whileStatement;

		setLabel(whileStatement, "continue");
		visitWithNewLabelSet(whileStatement.expression);
		writeJump(VirtualMachine.XOP_IF, whileStatement, "break");

		visitWithNewLabelSet(whileStatement.statement);

		writeJump(VirtualMachine.XOP_GO, whileStatement, "continue");

		setLabel(whileStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;
		return whileStatement;
	}

	public AbstractStatement compile(WithStatement withStatement)
			throws CompilerException {
		addLineNumber(withStatement);

		if (currentTryStatement == null) {
			withStatement.expression.compileExpression(this);
			writeOp(VirtualMachine.OP_WITH_START);
			withStatement.statement.compileStatement(this);
			writeOp(VirtualMachine.OP_WITH_END);
		} else {
			// if an exception is thrown inside the with statement,
			// it is necessary to restore the context
			AbstractStatement saveTryStatement = currentTryStatement;
			String saveTryLabel = currentTryLabel;
			currentTryLabel = "finally";
			currentTryStatement = withStatement;
			withStatement.expression.compileExpression(this);
			writeOp(VirtualMachine.OP_WITH_END);
			withStatement.statement.compileStatement(this);
			writeOp(VirtualMachine.OP_WITH_END);
			writeJump(VirtualMachine.XOP_GO, withStatement, "end");

			currentTryStatement = saveTryStatement;
			currentTryLabel = saveTryLabel;

			setLabel(withStatement, "finally");
			writeOp(VirtualMachine.OP_WITH_END);
			writeOp(VirtualMachine.OP_THROW);
			setLabel(withStatement, "end");
		}
		return withStatement;
	}

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Expression /////////////////////////////
	public AbstractExpression compile(AssignmentExpression assignmentExpression)
			throws CompilerException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = assignmentExpression;
		assignmentExpression.leftExpression.compileExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return assignmentExpression;
	}

	public AbstractExpression compile(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws CompilerException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = assignmentOperatorExpression;
		assignmentOperatorExpression.leftExpression.compileExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return assignmentOperatorExpression;
	}

	public AbstractExpression compile(
			BinaryOperatorExpression binaryOperatorExpression)
			throws CompilerException {
		binaryOperatorExpression.leftExpression.compileExpression(this);
		binaryOperatorExpression.rightExpression.compileExpression(this);
		writeBinaryOperator(binaryOperatorExpression.operator);
		return binaryOperatorExpression;
	}

	public AbstractExpression compile(
			CallFunctionExpression callFunctionExpression)
			throws CompilerException {
		if (callFunctionExpression.function instanceof PropertyExpression) {
			PropertyExpression pe = (PropertyExpression) callFunctionExpression.function;
			pe.leftExpression.compileExpression(this);
			writeOp(VirtualMachine.OP_DUP);
			pe.rightExpression.compileExpression(this);
			writeOp(VirtualMachine.OP_GET);
		} else {
			writeOp(VirtualMachine.OP_PUSH_GLOBAL);
			callFunctionExpression.function.compileExpression(this);
		}
		// push arguments
		for (int i = 0; i < callFunctionExpression.arguments.length; i++) {
			callFunctionExpression.arguments[i].compileExpression(this);
		}

		if (currentTryStatement == null) {
			writeXop(VirtualMachine.XOP_CALL,
					callFunctionExpression.arguments.length);
		} else {
			writeXop(VirtualMachine.XOP_TRY_CALL,
					callFunctionExpression.arguments.length);
			writeJump(VirtualMachine.XOP_IF, currentTryStatement,
					currentTryLabel);
		}
		return callFunctionExpression;
	}

	public AbstractExpression compile(
			ConditionalExpression conditionalExpression)
			throws CompilerException {
		conditionalExpression.expression.compileExpression(this);
		writeJump(VirtualMachine.XOP_IF, conditionalExpression, "else");
		conditionalExpression.trueExpression.compileExpression(this);
		writeJump(VirtualMachine.XOP_GO, conditionalExpression, "endif");
		setLabel(conditionalExpression, "else");
		conditionalExpression.falseExpression.compileExpression(this);
		setLabel(conditionalExpression, "endif");
		return conditionalExpression;
	}

	public AbstractExpression compile(DeleteExpression deleteExpression)
			throws CompilerException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = deleteExpression;
		deleteExpression.expression.compileExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return deleteExpression;
	}

	public AbstractExpression compile(IncrementExpression incrementExpression)
			throws CompilerException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = incrementExpression;
		incrementExpression.expression.compileExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return incrementExpression;
	}

	public AbstractExpression compile(LogicalAndExpression logicalAndExpression)
			throws CompilerException {
		logicalAndExpression.leftExpression.compileExpression(this);
		writeOp(VirtualMachine.OP_DUP);
		// jump (= skip) if false since false && any = false
		writeJump(VirtualMachine.XOP_IF, logicalAndExpression, "end");
		writeOp(VirtualMachine.OP_DROP);
		logicalAndExpression.rightExpression.compileExpression(this);
		setLabel(logicalAndExpression, "end");
		return logicalAndExpression;
	}

	public AbstractExpression compile(LogicalOrExpression logicalOrExpression)
			throws CompilerException {
		logicalOrExpression.leftExpression.compileExpression(this);
		writeOp(VirtualMachine.OP_DUP);
		// jump (= skip) if true since true && any =
		writeOp(VirtualMachine.OP_NOT);
		writeJump(VirtualMachine.XOP_IF, logicalOrExpression, "end");
		writeOp(VirtualMachine.OP_DROP);
		logicalOrExpression.rightExpression.compileExpression(this);
		setLabel(logicalOrExpression, "end");
		return logicalOrExpression;
	}

	public AbstractExpression compile(NewExpression newExpression)
			throws CompilerException {
		newExpression.function.compileExpression(this);
		writeOp(VirtualMachine.OP_NEW);
		if (newExpression.arguments != null) {
			for (int i = 0; i < newExpression.arguments.length; i++) {
				newExpression.arguments[i].compileExpression(this);
			}
			writeXop(VirtualMachine.XOP_CALL, newExpression.arguments.length);
		} else {
			writeXop(VirtualMachine.XOP_CALL, 0);
		}
		writeOp(VirtualMachine.OP_DROP);
		return newExpression;
	}

	public AbstractExpression compile(PropertyExpression propertyExpression)
			throws CompilerException {
		AbstractExpression pa = pendingAssignment;
		pendingAssignment = null;

		if (pa == null) {
			propertyExpression.leftExpression.compileExpression(this);
			propertyExpression.rightExpression.compileExpression(this);
			writeOp(VirtualMachine.OP_GET);
		} else if (pa instanceof AssignmentExpression) {
			// push value
			((AssignmentExpression) pa).rightExpression.compileExpression(this);
			// push object
			propertyExpression.leftExpression.compileExpression(this);
			// push property
			propertyExpression.rightExpression.compileExpression(this);
			writeOp(VirtualMachine.OP_SET);
		} else if (pa instanceof AssignmentOperatorExpression) {
			// this case is a bit tricky...
			AssignmentOperatorExpression aoe = (AssignmentOperatorExpression) pa;
			propertyExpression.leftExpression.compileExpression(this);
			propertyExpression.rightExpression.compileExpression(this);
			// duplicate object and member
			writeOp(VirtualMachine.OP_DDUP);
			writeOp(VirtualMachine.OP_GET);
			// push value
			aoe.rightExpression.compileExpression(this);
			// exec assignment op
			writeBinaryOperator(aoe.operator);
			// move result value below object and property
			writeOp(VirtualMachine.OP_ROT);
			writeOp(VirtualMachine.OP_SET);
		} else if (pa instanceof IncrementExpression) {
			IncrementExpression ie = (IncrementExpression) pa;
			propertyExpression.leftExpression.compileExpression(this);
			propertyExpression.rightExpression.compileExpression(this);
			// duplicate object and member
			writeOp(VirtualMachine.OP_DDUP);
			writeOp(VirtualMachine.OP_GET);
			// increment / decrement
			writeXop(VirtualMachine.XOP_ADD, ie.value);
			// move result value below object and property
			writeOp(VirtualMachine.OP_ROT);
			writeOp(VirtualMachine.OP_SET);
			if (ie.post) {
				writeXop(VirtualMachine.XOP_ADD, -ie.value);
			}
		} else if (pa instanceof DeleteExpression) {
			propertyExpression.leftExpression.compileExpression(this);
			propertyExpression.rightExpression.compileExpression(this);
			writeOp(VirtualMachine.OP_DEL);
		}
		return propertyExpression;
	}

	public AbstractExpression compile(
			UnaryOperatorExpression unaryOperatorExpression)
			throws CompilerException {
		unaryOperatorExpression.expression.compileExpression(this);
		writeUnaryOperator(unaryOperatorExpression.operator);
		return unaryOperatorExpression;
	}

	public AbstractExpression compile(VariableExpression variableExpression)
			throws CompilerException {
		for (int i = 0; i < variableExpression.declarations.length; i++) {
			variableExpression.declarations[i].compileExpression(this);
		}
		return variableExpression;
	}

	public AbstractExpression compile(
			VariableDeclarationExpression variableDeclarationExpression)
			throws CompilerException {
		if (variableDeclarationExpression.initializer != null) {
			variableDeclarationExpression.initializer.compileExpression(this);
			writeVarDef(variableDeclarationExpression.identifier.identifierName, true);
			writeOp(VirtualMachine.OP_DROP);
		} else {
			writeVarDef(variableDeclarationExpression.identifier.identifierName, false);
		}
		return variableDeclarationExpression;
	}

	// ///////////////////////////////////////////////////////////////////////

	// /////////////////////////////// Literal ///////////////////////////////
	public AbstractLiteral compile(IdentifierLiteral identifierLiteral)
			throws CompilerException {
		AbstractExpression pa = pendingAssignment;
		pendingAssignment = null;

		IdentifierLiteral localVariable = (IdentifierLiteral) localVariableTable
				.get(identifierLiteral);

		if (localVariable != null) {
			identifierLiteral = localVariable;
		}

		if (pa == null) {
			writeOpGet(identifierLiteral);
		} else if (pa instanceof AssignmentExpression) {
			((AssignmentExpression) pa).rightExpression.compileExpression(this);
			writeOpSet(identifierLiteral);
		} else if (pa instanceof AssignmentOperatorExpression) {
			writeOpGet(identifierLiteral);
			((AssignmentOperatorExpression) pa).rightExpression
					.compileExpression(this);
			writeBinaryOperator(((AssignmentOperatorExpression) pa).operator);
			writeOpSet(identifierLiteral);
		} else if (pa instanceof IncrementExpression) {
			IncrementExpression ie = (IncrementExpression) pa;
			writeOpGet(identifierLiteral);
			writeXop(VirtualMachine.XOP_ADD, ((IncrementExpression) pa).value);
			writeOpSet(identifierLiteral);
			if (ie.post) {
				writeXop(VirtualMachine.XOP_ADD,
						-((IncrementExpression) pa).value);
			}
		} else if (pa instanceof DeleteExpression) {
			writeOp(VirtualMachine.OP_CTX);
			writeXop(VirtualMachine.XOP_PUSH_STR,
					getStringLiteralIndex(identifierLiteral.identifierName));
			writeOp(VirtualMachine.OP_DEL);
		} else {
			throw new IllegalArgumentException();
		}

		return identifierLiteral;
	}

	public AbstractLiteral compile(ThisLiteral thisLiteral)
			throws CompilerException {
		writeOp(VirtualMachine.OP_PUSH_THIS);
		return thisLiteral;
	}

	public AbstractLiteral compile(NullLiteral nullLiteral)
			throws CompilerException {
		writeOp(VirtualMachine.OP_PUSH_NULL);
		return nullLiteral;
	}

	public AbstractLiteral compile(BooleanLiteral booleanLiteral)
			throws CompilerException {
		writeOp(booleanLiteral.value ? VirtualMachine.OP_PUSH_TRUE
				: VirtualMachine.OP_PUSH_FALSE);
		return booleanLiteral;
	}

	public AbstractLiteral compile(NumberLiteral numberLiteral)
			throws CompilerException {
		double v = numberLiteral.value;
		if (32767 >= v && v >= -32767 && v == Math.floor(v)) {
			writeXop(VirtualMachine.XOP_PUSH_INT, (int) v);
		} else {
			Double d = new Double(v);
			int i = numberLiterals.indexOf(d);
			if (i == -1) {
				i = numberLiterals.size();
				numberLiterals.add(d);
			}
			writeXop(VirtualMachine.XOP_PUSH_NUM, i);
		}

		return numberLiteral;
	}

	public AbstractLiteral compile(StringLiteral stringLiteral)
			throws CompilerException {
		writeXop(VirtualMachine.XOP_PUSH_STR,
				getStringLiteralIndex(stringLiteral.string));
		return stringLiteral;
	}

	public AbstractLiteral compile(ArrayLiteral arrayLiteral)
			throws CompilerException {
		writeOp(VirtualMachine.OP_NEW_ARR);
		for (int i = 0; i < arrayLiteral.elements.length; i++) {
			if (arrayLiteral.elements[i] == null) {
				writeOp(VirtualMachine.OP_PUSH_UNDEF);
			} else {
				arrayLiteral.elements[i].compileExpression(this);
			}
			writeOp(VirtualMachine.OP_APPEND);
		}
		return arrayLiteral;
	}

	public AbstractLiteral compile(FunctionLiteral functionLiteral)
			throws CompilerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		CompileFunction(this, functionLiteral, new DataOutputStream(baos));

		functionLiterals.add(baos.toByteArray());

		writeXop(VirtualMachine.XOP_PUSH_FN, functionLiterals.size() - 1);

		if (functionLiteral.funcName != null) {
			writeVarDef(functionLiteral.funcName.identifierName, true);
		}
		return functionLiteral;
	}

	public AbstractLiteral compile(ObjectLiteral objectLiteral)
			throws CompilerException {
		writeOp(VirtualMachine.OP_NEW_OBJ);
		for (int i = 0; i < objectLiteral.properties.length; i++) {
			objectLiteral.properties[i].compileExpression(this);
		}
		return objectLiteral;
	}

	public AbstractLiteral compile(ObjectPropertyLiteral objectPropertyLiteral)
			throws CompilerException {
		objectPropertyLiteral.name.compileExpression(this);
		objectPropertyLiteral.value.compileExpression(this);
		writeOp(VirtualMachine.OP_SET_KC);

		return objectPropertyLiteral;
	}

	// ///////////////////////////////////////////////////////////////////////

	/**
	 * 写入全局字符串表
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x10</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>字符串数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>length</td>
	 * <td>字符串数据长度</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>data</td>
	 * <td>字符串数据</td>
	 * <td>byte[]</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws CompilerException
	 */
	private void writeGlobalStringTableBlock() throws CompilerException {
		try {
			if (globalStringTable.size() > 0) {
				dos.write(VirtualMachine.BLOCK_GLOBAL_STRING_TABLE);
				dos.writeShort(globalStringTable.size());
				for (int i = 0; i < globalStringTable.size(); i++) {
					dos.writeUTF((String) globalStringTable.get(i));
				}
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入数字定义表
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x20</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>数字数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>number</td>
	 * <td>数值</td>
	 * <td>double</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws CompilerException
	 */
	private void writeNumberLiteralBlock() throws CompilerException {
		try {
			if (numberLiterals.size() > 0) {
				dos.write(VirtualMachine.BLOCK_NUMBER_LITERALS);
				dos.writeShort(numberLiterals.size());
				for (int i = 0; i < numberLiterals.size(); i++) {
					dos.writeDouble(((Double) numberLiterals.get(i))
							.doubleValue());
				}
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入字符串定义表
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x30</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>数字数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>index</td>
	 * <td>字符串在全局字符串表中的索引</td>
	 * <td>short</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws CompilerException
	 */
	private void writeStringLiteralBlock() throws CompilerException {
		try {
			if (stringLiterals.size() > 0) {
				dos.write(VirtualMachine.BLOCK_STRING_LITERALS);
				dos.writeShort(stringLiterals.size());
				for (int i = 0; i < stringLiterals.size(); i++) {
					dos.writeShort((short) ((Integer) globalStringMap
							.get(stringLiterals.get(i))).intValue());
				}
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入变量名列表
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x60</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>数字数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>index</td>
	 * <td>变量名在全局字符串表中的索引</td>
	 * <td>short</td>
	 * </tr>
	 * </table>
	 * 
	 * @param variables
	 *            变量名列表
	 * @throws CompilerException
	 */
	private void writeLocalVariableNameBlock(IdentifierLiteral[] variables)
			throws CompilerException {
		try {
			if (variables != null) {
				dos.write(VirtualMachine.BLOCK_LOCAL_VARIABLE_NAMES);
				dos.writeShort(variables.length);
				for (int i = 0; i < variables.length; i++) {
					dos.writeShort((short) ((Integer) globalStringMap
							.get(variables[i].identifierName)).intValue());
				}
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入函数定义数据
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x50</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>函数数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>FunctionLiteral</td>
	 * <td>函数数据</td>
	 * <td>byte[]</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws CompilerException
	 */
	private void writeFunctionLiteralBlock() throws CompilerException {
		try {
			if (functionLiterals.size() > 0) {
				dos.write(VirtualMachine.BLOCK_FUNCTION_LITERALS);
				dos.writeShort(functionLiterals.size());
				for (int i = 0; i < functionLiterals.size(); i++) {
					dos.write((byte[]) functionLiterals.get(i));
				}
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * @param localVariableCount
	 * @param paramenterCount
	 * @param flags
	 * @param code
	 * @throws CompilerException
	 */
	private void writeCodeBlock(int localVariableCount, int paramenterCount,
			int flags, byte[] code) throws CompilerException {
		try {
			dos.write(VirtualMachine.BLOCK_BYTE_CODE);
			dos.writeShort(localVariableCount);
			dos.writeShort(paramenterCount);
			dos.write(flags);
			dos.writeShort(code.length);

			for (int i = 0; i < unresolvedJumps.size(); i += 2) {
				String label = (String) unresolvedJumps.get(i);
				int address = ((Integer) unresolvedJumps.get(i + 1)).intValue();
				Integer target = (Integer) jumpLabels.get(label);

				if (target == null) {
					throw new CompilerException("Unresolved Jump Label: "
							+ label);
				}

				int delta = target.intValue() - address - 2;

				code[address + 0] = (byte) (delta >> 8);
				code[address + 1] = (byte) (delta & 255);
			}

			dos.write(code);
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入行号数据
	 * <p>
	 * <table>
	 * <tr style="background-color:#990000;color:#FFFFFF">
	 * <th>数据内容</th>
	 * <th>数据描述</th>
	 * <th>数据长度</th>
	 * </tr>
	 * <tr>
	 * <td>flag</td>
	 * <td>数据段标识</td>
	 * <td>0x7f</td>
	 * </tr>
	 * <tr>
	 * <td>count</td>
	 * <td>行号数量</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>pc</td>
	 * <td>ProgramCounter</td>
	 * <td>short</td>
	 * </tr>
	 * <tr style="background-color:#ccffff">
	 * <td>linenumber</td>
	 * <td>行号</td>
	 * <td>short</td>
	 * </tr>
	 * </table>
	 * 
	 * @throws CompilerException
	 */
	private void writeLineNumberBlock() throws CompilerException {
		try {
			dos.write(VirtualMachine.BLOCK_LINE_NUMBERS);
			int lineNumberCount = lineNumberList.size();
			dos.writeShort(lineNumberList.size());
			for (int i = 0; i < lineNumberCount; i++) {
				LineNumber lineNumber = (LineNumber) lineNumberList.get(i);
				dos.writeShort(lineNumber.programCounter & 0xffff);
				dos.writeShort(lineNumber.lineNumber & 0xffff);
			}
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入结束标志
	 * 
	 * @throws CompilerException
	 */
	private void writeEndMarker() throws CompilerException {
		try {
			dos.write(VirtualMachine.BLOCK_END);
		} catch (IOException e) {
			throw new CompilerException(e);
		}
	}

	/**
	 * 写入一个操作符
	 * 
	 * @param op
	 *            操作符
	 */
	private void writeOp(int op) {
		codeStream.write(op);
	}

	/**
	 * 写入取出(get)给定标识符指向数据的操作
	 * 
	 * @param identifier
	 *            指定的标识符
	 */
	private void writeOpGet(IdentifierLiteral identifier) {
		int index = identifier.index;

		if (Config.FASTLOCALS && enableLocalsOptimization && index >= 0) {
			writeXop(VirtualMachine.XOP_LCL_GET, index);
		} else {
			writeXop(VirtualMachine.XOP_PUSH_STR,
					getStringLiteralIndex(identifier.identifierName));
			writeOp(VirtualMachine.OP_CTX_GET);
		}
	}

	/**
	 * 写入设置(set)数据给定标识符的操作
	 * 
	 * @param identifier
	 *            指定的标识符
	 */
	private void writeOpSet(IdentifierLiteral identifier) {
		int index = identifier.index;

		if (Config.FASTLOCALS && enableLocalsOptimization && index >= 0) {
			writeXop(VirtualMachine.XOP_LCL_SET, index);
		} else {
			writeXop(VirtualMachine.XOP_PUSH_STR,
					getStringLiteralIndex(identifier.identifierName));
			writeOp(VirtualMachine.OP_CTX_SET);
		}
	}

	private void visitWithNewLabelSet(AbstractSyntaxNode node)
			throws CompilerException {
		ArrayList saveLabelSet = labelSet;
		labelSet = new ArrayList();
		if (node instanceof AbstractStatement) {
			((AbstractStatement) node).compileStatement(this);
		} else if (node instanceof AbstractExpression) {
			((AbstractExpression) node).compileExpression(this);
		}
		labelSet = saveLabelSet;
	}

	/**
	 * 写入后面带有立即数的操作符
	 * 
	 * @param opcode
	 *            操作符
	 * @param imm
	 *            操作符后跟的立即数字
	 */
	void writeXop(int opcode, int imm) {
		if (opcode == VirtualMachine.XOP_ADD) {
			switch (imm) {
			case 1:
				writeOp(VirtualMachine.OP_INC);
				return;
			case -1:
				writeOp(VirtualMachine.OP_DEC);
				return;
			}
		}

		if ((imm & 0x0ff80) == 0 || (imm & 0x0ff80) == 0xff80) {
			codeStream.write(opcode << 1);
			codeStream.write(imm);
		} else {
			codeStream.write((opcode << 1) | 1);
			codeStream.write(imm >> 8);
			codeStream.write(imm & 255);
		}
	}

	private void writeJump(int op, Object base, String type) {
		int pos = codeStream.size() + 1;

		if (base instanceof String) {
			type = type + "-" + base;
		} else if (base instanceof AbstractSyntaxNode) {
			type = type + "=" + base.hashCode();
		} else if (base == null) {
			throw new RuntimeException("Invalid position for " + type);
		} else {
			throw new RuntimeException("Illegal Jump base object");
		}

		Integer target = (Integer) jumpLabels.get(type);
		if (jumpLabels.get(type) == null) {
			writeXop(op, 32767);
			unresolvedJumps.add(type);
			unresolvedJumps.add(new Integer(pos));
		} else {
			// minus one for pc after decoding 8 bit imm
			int delta = target.intValue() - pos - 1;

			if (delta > -127 && delta < 128) {
				codeStream.write(op << 1);
				codeStream.write(delta);
			} else {
				// minus one more for pc after decoding 16 bit imm
				codeStream.write((op << 1) | 1);
				// minus one more for pc after decoding 16 bit imm
				delta -= 1;
				codeStream.write(delta >> 8);
				codeStream.write(delta & 255);
			}
		}
	}

	private void setLabel(AbstractSyntaxNode node, String label) {
		Integer pos = new Integer(codeStream.size());
		jumpLabels.put(label + "=" + node.hashCode(), pos);
		for (int i = 0; i < labelSet.size(); i++) {
			jumpLabels.put(label + "-" + labelSet.get(i), pos);
		}
	}

	/**
	 * 写入二元操作符
	 * 
	 * @param type
	 *            操作符类型
	 */
	private void writeBinaryOperator(Token type) {
		if (type == Token.OPERATOR_ASSIGN) {
			// should be handled as special case
			writeOp(VirtualMachine.OP_DROP);
		} else if (type == Token.OPERATOR_ARITHMETICAL_AND
				|| type == Token.OPERATOR_ARITHMETICAL_AND_ASSIGN) {
			writeOp(VirtualMachine.OP_AND);
		} else if (type == Token.OPERATOR_ARITHMETICAL_OR
				|| type == Token.OPERATOR_ARITHMETICAL_OR_ASSIGN) {
			writeOp(VirtualMachine.OP_OR);
		} else if (type == Token.OPERATOR_ARITHMETICAL_XOR
				|| type == Token.OPERATOR_ARITHMETICAL_XOR_ASSIGN) {
			writeOp(VirtualMachine.OP_XOR);
		} else if (type == Token.OPERATOR_COMMA) {
			// should be handled as special case in caller to avoid swap
			writeOp(VirtualMachine.OP_SWAP);
			writeOp(VirtualMachine.OP_DROP);
		} else if (type == Token.OPERATOR_DIVIDE
				|| type == Token.OPERATOR_DIVIDE_ASSIGN) {
			writeOp(VirtualMachine.OP_DIV);
		} else if (type == Token.OPERATOR_EQUAL) {
			writeOp(VirtualMachine.OP_EQEQ);
		} else if (type == Token.OPERATOR_STRICT_EQUAL) {
			writeOp(VirtualMachine.OP_EQEQEQ);
		} else if (type == Token.OPERATOR_GT) {
			writeOp(VirtualMachine.OP_GT);
		} else if (type == Token.OPERATOR_GTE) {
			writeOp(VirtualMachine.OP_LT);
			writeOp(VirtualMachine.OP_NOT);
		} else if (type == Token.OPERATOR_LT) {
			writeOp(VirtualMachine.OP_LT);
		} else if (type == Token.OPERATOR_LTE) {
			writeOp(VirtualMachine.OP_GT);
			writeOp(VirtualMachine.OP_NOT);
		} else if (type == Token.OPERATOR_MINUS
				|| type == Token.OPERATOR_MINUS_ASSIGN) {
			writeOp(VirtualMachine.OP_SUB);
		} else if (type == Token.OPERATOR_MOD
				|| type == Token.OPERATOR_MOD_ASSIGN) {
			writeOp(VirtualMachine.OP_MOD);
		} else if (type == Token.OPERATOR_MUL
				|| type == Token.OPERATOR_MUL_ASSIGN) {
			writeOp(VirtualMachine.OP_MUL);
		} else if (type == Token.OPERATOR_NOT_EQUAL) {
			writeOp(VirtualMachine.OP_EQEQ);
			writeOp(VirtualMachine.OP_NOT);
		} else if (type == Token.OPERATOR_NOT_STRICT_EQUAL) {
			writeOp(VirtualMachine.OP_EQEQEQ);
			writeOp(VirtualMachine.OP_NOT);
		} else if (type == Token.OPERATOR_PLUS
				|| type == Token.OPERATOR_PLUS_ASSIGN) {
			writeOp(VirtualMachine.OP_ADD);
		} else if (type == Token.OPERATOR_SHL
				|| type == Token.OPERATOR_SHL_ASSIGN) {
			writeOp(VirtualMachine.OP_SHL);
		} else if (type == Token.OPERATOR_SHR
				|| type == Token.OPERATOR_SHR_ASSIGN) {
			writeOp(VirtualMachine.OP_SHR);
		} else if (type == Token.OPERATOR_ASR
				|| type == Token.OPERATOR_ASR_ASSIGN) {
			writeOp(VirtualMachine.OP_ASR);
		} else if (type == Token.KEYWORD_IN) {
			writeOp(VirtualMachine.OP_IN);
		} else if (type == Token.KEYWORD_INSTANCEOF) {
			writeOp(VirtualMachine.OP_INSTANCEOF);
		} else {
			throw new IllegalArgumentException("Not binary: " + type.toString());
		}
	}

	/**
	 * 写入一元操作符
	 * 
	 * @param type
	 *            操作符类型
	 */
	private void writeUnaryOperator(Token type) {
		if (type == Token.OPERATOR_PLUS) {
			writeXop(VirtualMachine.XOP_ADD, 0);
		} else if (type == Token.OPERATOR_MINUS) {
			writeOp(VirtualMachine.OP_NEG);
		} else if (type == Token.OPERATOR_ARITHMETICAL_NOT) {
			writeOp(VirtualMachine.OP_INV);
		} else if (type == Token.OPERATOR_LOGICAL_NOT) {
			writeOp(VirtualMachine.OP_NOT);
		} else if (type == Token.KEYWORD_VOID) {
			writeOp(VirtualMachine.OP_DROP);
			writeOp(VirtualMachine.OP_PUSH_UNDEF);
		} else if (type == Token.KEYWORD_TYPEOF) {
			writeOp(VirtualMachine.OP_TYPEOF);
		} else {
			throw new IllegalArgumentException("Not unary: " + type.toString());
		}
	}

	/** value must be on stack, is kept on stack */
	private void writeVarDef(String name, boolean initialize) {
		if (initialize) {
			writeXop(VirtualMachine.XOP_PUSH_STR, getStringLiteralIndex(name));
			writeOp(VirtualMachine.OP_CTX_SET);
		}
	}

	/**
	 * 如果待加入的字符串不在全局符号表中，则将字符串加入符号表中
	 * 
	 * @param string
	 *            待加入的字符串
	 */
	private void addToGlobalStringTable(String string) {
		if (globalStringMap.get(string) == null) {
			globalStringMap.put(string, new Integer(globalStringTable.size()));
			globalStringTable.add(string);
		}
	}

	/**
	 * 获取字符串在字符串定义列表中的索引. 如果不存在则加入进去.
	 * 
	 * @param string
	 *            待查找的字符串
	 * @return 字符串在字符串定义列表中的索引
	 */
	private int getStringLiteralIndex(String string) {
		int i = stringLiterals.indexOf(string);
		if (i == -1) {
			i = stringLiterals.size();
			addToGlobalStringTable(string);
			stringLiterals.add(string);
		}
		return i;
	}

	/**
	 * 将给定语句的行号数据添加进列表
	 * 
	 * @param statement
	 *            给定语句
	 */
	private void addLineNumber(AbstractStatement statement) {
		if (Config.LINENUMBER) {
			lineNumberList.add(new LineNumber(codeStream.size(), statement
					.getLineNumber()));
		}
	}

	/**
	 * <code>LineNumber</code>封装了行号数据
	 * 
	 * @author Jarod Yv
	 */
	private class LineNumber {
		private int programCounter;
		private int lineNumber;

		private LineNumber(int programCounter, int lineNumber) {
			this.programCounter = programCounter;
			this.lineNumber = lineNumber;
		}
	}
}
