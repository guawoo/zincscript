package interpreter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import parser.Config;
import parser.ParserException;
import parser.Token;
import utils.ArrayList;
import ast.*;
import ast.expression.*;
import ast.expression.binary.*;
import ast.expression.literal.*;
import ast.expression.unary.*;
import ast.statement.*;

/**
 * <code>CompilationInterpreter</code> 负责将语法树解释成可执行的中间代码.
 * <p>
 * 整个过程分为2步, 会对语法树进行2次遍历：
 * <ol>
 * <li>分析确定偏移
 * <li>生成中间代码
 * </ol>
 * 
 * @author Jarod Yv
 */
public class CompilationInterpreter extends AbstractInterpreter {
	public static final byte BLOCK_COMMENT = (byte) 0x00;
	public static final byte BLOCK_GLOBAL_STRING_TABLE = (byte) 0x10;
	public static final byte BLOCK_NUMBER_LITERALS = (byte) 0x20;
	public static final byte BLOCK_STRING_LITERALS = (byte) 0x30;
	public static final byte BLOCK_REGEX_LITERALS = (byte) 0x40;
	public static final byte BLOCK_FUNCTION_LITERALS = (byte) 0x50;
	public static final byte BLOCK_LOCAL_VARIABLE_NAMES = (byte) 0x60;
	public static final byte BLOCK_CODE = (byte) 0x80;
	public static final byte BLOCK_LINENUMBER = (byte) 0xE0;
	public static final byte BLOCK_DEBUG = (byte) 0xF0;
	public static final byte BLOCK_END = (byte) 0xFF;

	private DataOutputStream dos = null;
	private ByteArrayOutputStream codeStream = new ByteArrayOutputStream(0);

	private Hashtable globalStringMap = new Hashtable();
	private ArrayList globalStringTable = new ArrayList();

	private ArrayList functionLiterals = new ArrayList();
	private ArrayList numberLiterals = new ArrayList();
	private ArrayList stringLiterals = new ArrayList();

	private Hashtable localVariableTable = new Hashtable();
	private Hashtable jumpLabels = new Hashtable();

	private ArrayList unresolvedJumps = new ArrayList();
	private ArrayList labelSet = new ArrayList();

	private ArrayList lineNumberVector = new ArrayList();

	private AbstractExpression pendingAssignment;

	private AbstractStatement currentBreakStatement;
	private AbstractStatement currentContinueStatement;
	private AbstractStatement currentTryStatement;
	private String currentTryLabel;

	private boolean enableLocalsOptimization = false;

	private CompilationInterpreter parent = null;

	public CompilationInterpreter(DataOutputStream stream) {
		this.dos = stream;
		this.globalStringMap = new Hashtable();
		this.globalStringTable = new ArrayList();
	}

	public CompilationInterpreter(CompilationInterpreter parent,
			FunctionLiteral function, DataOutputStream dos)
			throws ParserException {
		this.parent = parent;
		this.globalStringMap = parent.globalStringMap;
		this.globalStringTable = parent.globalStringTable;
		this.dos = dos;
		this.enableLocalsOptimization = function.enableLocalsOptimization;

		// 将函数的变量写入局部变量表
		for (int i = 0; i < function.variables.length; i++) {
			IdentifierLiteral variable = function.variables[i];
			addToGlobalStringTable(variable.string);
			localVariableTable.put(variable, variable);
		}

		// for (int i = 0; i < function.variables.length; i++) {
		// addToGlobalStringTable(function.variables[i].string);
		// }

		// 处理函数中的函数
		for (int i = 0; i < function.functions.length; i++) {
			if (function.functions[i] != null) {
				function.functions[i].interpretStatement(this);
			}
		}
		// 处理函数中的语句
		for (int i = 0; i < function.statements.length; i++) {
			if (function.statements[i] != null) {
				function.statements[i].interpretStatement(this);
			}
		}

		byte[] byteCode = codeStream.toByteArray();

		// TODO remove this magic numbers.
		int flags = Config.FASTLOCALS && function.enableLocalsOptimization ? 0x01
				: 0x00;

		if (function.funcName != null) {
			writeCommentBlock("function " + function.funcName.string);
		}

		writeStringLiteralBlock();
		writeNumberLiteralBlock();
		writeFunctionLiteralBlock();
		writeLocalVariableNameBlock(function.variables);
		writeCodeBlock(function.variables.length, function.parameters.length,
				flags, byteCode);
		writeLineNumberBlock();
		writeEndMarker();
	}

	public Program interpret(Program program) throws ParserException {
		for (int i = 0; i < program.functions.length; i++) {
			program.functions[i].interpretStatement(this);
		}

		for (int i = 0; i < program.statements.length; i++) {
			program.statements[i].interpretStatement(this);
		}

		writeMagic();
		writeGlobalStringTableBlock();
		writeStringLiteralBlock();
		writeNumberLiteralBlock();
		writeFunctionLiteralBlock();
		writeCodeBlock(0, 0, 0x00, codeStream.toByteArray());
		writeLineNumberBlock();
		writeEndMarker();

		return program;
	}

	public AbstractStatement interpret(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws ParserException {
		addLineNumber(functionDeclarationStatement);
		functionDeclarationStatement.function.interpretExpression(this);
		writeOp(JsFunctionObject.OP_DROP);
		return functionDeclarationStatement;
	}

	public AbstractStatement interpret(BlockStatement blockStatement)
			throws ParserException {
		for (int i = 0; i < blockStatement.statements.length; i++) {
			blockStatement.statements[i].interpretStatement(this);
		}
		return blockStatement;
	}

	public AbstractStatement interpret(BreakStatement breakStatement)
			throws ParserException {
		addLineNumber(breakStatement);
		writeJump(
				JsFunctionObject.XOP_GO,
				breakStatement.identifier == null ? (Object) currentBreakStatement
						: breakStatement.identifier.string, "break");
		return breakStatement;
	}

	public AbstractStatement interpret(CaseStatement caseStatement)
			throws ParserException {
		throw new RuntimeException("should not be visited");
	}

	public AbstractStatement interpret(ContinueStatement continueStatement)
			throws ParserException {
		addLineNumber(continueStatement);
		writeJump(
				JsFunctionObject.XOP_GO,
				continueStatement.identifier == null ? (Object) currentBreakStatement
						: continueStatement.identifier.string, "continue");
		return continueStatement;
	}

	public AbstractStatement interpret(DoStatement doStatement)
			throws ParserException {
		addLineNumber(doStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;
		currentBreakStatement = doStatement;
		currentContinueStatement = doStatement;

		setLabel(doStatement, "do");

		visitWithNewLabelSet(doStatement.statement);

		setLabel(doStatement, "continue");
		visitWithNewLabelSet(doStatement.expression);
		writeOp(JsFunctionObject.OP_NOT);
		writeJump(JsFunctionObject.XOP_IF, doStatement, "do");
		setLabel(doStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;
		return doStatement;
	}

	public AbstractStatement interpret(EmptyStatement emptyStatement)
			throws ParserException {
		return emptyStatement;
	}

	public AbstractStatement interpret(ExpressionStatement expressionStatement)
			throws ParserException {
		addLineNumber(expressionStatement);
		expressionStatement.expression.interpretExpression(this);
		writeOp(JsFunctionObject.OP_DROP);
		return expressionStatement;
	}

	public AbstractStatement interpret(ForStatement forStatement)
			throws ParserException {
		addLineNumber(forStatement);

		if (forStatement.initial != null) {
			forStatement.initial.interpretExpression(this);
			if (!(forStatement.initial instanceof VariableExpression)) {
				writeOp(JsFunctionObject.OP_DROP);
			}
		}

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = forStatement;
		currentContinueStatement = forStatement;

		setLabel(forStatement, "start");

		if (forStatement.condition != null) {
			visitWithNewLabelSet(forStatement.condition);
			writeJump(JsFunctionObject.XOP_IF, forStatement, "break");
		}

		if (forStatement.statement != null) {
			visitWithNewLabelSet(forStatement.statement);
		}

		setLabel(forStatement, "continue");

		if (forStatement.increment != null) {
			visitWithNewLabelSet(forStatement.increment);
			writeOp(JsFunctionObject.OP_DROP);
		}

		writeJump(JsFunctionObject.XOP_GO, forStatement, "start");

		setLabel(forStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;

		return forStatement;
	}

	public AbstractStatement interpret(ForInStatement forInStatement)
			throws ParserException {
		addLineNumber(forInStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = forInStatement;
		currentContinueStatement = forInStatement;

		forInStatement.expression.interpretExpression(this);
		writeOp(JsFunctionObject.OP_ENUM);
		setLabel(forInStatement, "continue");
		writeJump(JsFunctionObject.XOP_NEXT, forInStatement, "break");

		if (forInStatement.variable instanceof IdentifierLiteral) {
			writeXop(
					JsFunctionObject.XOP_PUSH_STR,
					getStringLiteralIndex(((IdentifierLiteral) forInStatement.variable).string));
			writeOp(JsFunctionObject.OP_CTX_SET);
		} else if (forInStatement.variable instanceof VariableDeclarationExpression) {
			writeVarDef(
					((VariableDeclarationExpression) forInStatement.variable).identifier.string,
					true);
		} else {
			throw new IllegalArgumentException();
		}
		writeOp(JsFunctionObject.OP_DROP);

		forInStatement.statement.interpretStatement(this);
		writeJump(JsFunctionObject.XOP_GO, forInStatement, "continue");
		setLabel(forInStatement, "break");
		writeOp(JsFunctionObject.OP_DROP);

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;

		return forInStatement;
	}

	public AbstractStatement interpret(IfStatement ifStatement)
			throws ParserException {
		addLineNumber(ifStatement);

		ifStatement.expression.interpretExpression(this);
		if (ifStatement.falseStatement == null) {
			writeJump(JsFunctionObject.XOP_IF, ifStatement, "endif");
			ifStatement.trueStatement.interpretStatement(this);
		} else {
			writeJump(JsFunctionObject.XOP_IF, ifStatement, "else");
			ifStatement.trueStatement.interpretStatement(this);
			writeJump(JsFunctionObject.XOP_GO, ifStatement, "endif");
			setLabel(ifStatement, "else");
			ifStatement.falseStatement.interpretStatement(this);
		}
		setLabel(ifStatement, "endif");
		return ifStatement;
	}

	public AbstractStatement interpret(LabelledStatement labelledStatement)
			throws ParserException {
		labelSet.add(labelledStatement.identifier.string);
		labelledStatement.statement.interpretStatement(this);
		return labelledStatement;
	}

	public AbstractStatement interpret(ReturnStatement returnStatement)
			throws ParserException {
		addLineNumber(returnStatement);

		if (returnStatement.expression == null) {
			writeOp(JsFunctionObject.OP_PUSH_UNDEF);
		} else {
			returnStatement.expression.interpretExpression(this);
		}
		writeOp(JsFunctionObject.OP_RET);
		return returnStatement;
	}

	public AbstractStatement interpret(SwitchStatement switchStatement)
			throws ParserException {
		addLineNumber(switchStatement);

		switchStatement.expression.interpretExpression(this);

		AbstractStatement saveBreakStatemet = currentBreakStatement;
		currentBreakStatement = switchStatement;

		String defaultLabel = "break";

		for (int i = 0; i < switchStatement.clauses.length; i++) {
			CaseStatement cs = switchStatement.clauses[i];
			if (cs.expression == null) {
				defaultLabel = "case" + i;
			} else {
				writeOp(JsFunctionObject.OP_DUP);
				cs.expression.interpretExpression(this);
				writeOp(JsFunctionObject.OP_EQEQEQ);
				writeOp(JsFunctionObject.OP_NOT);
				writeJump(JsFunctionObject.XOP_IF, switchStatement, "case" + i);
			}
		}

		writeOp(JsFunctionObject.OP_DROP);
		writeJump(JsFunctionObject.XOP_GO, switchStatement, defaultLabel);

		for (int i = 0; i < switchStatement.clauses.length; i++) {
			setLabel(switchStatement, "case" + i);
			AbstractStatement[] statements = switchStatement.clauses[i].statements;
			for (int j = 0; j < statements.length; j++) {
				statements[j].interpretStatement(this);
			}
		}
		setLabel(switchStatement, "break");

		currentBreakStatement = saveBreakStatemet;
		return switchStatement;
	}

	public AbstractStatement interpret(ThrowStatement throwStatement)
			throws ParserException {
		addLineNumber(throwStatement);

		throwStatement.expression.interpretExpression(this);
		if (currentTryStatement == null) {
			writeOp(JsFunctionObject.OP_THROW);
		} else {
			writeJump(JsFunctionObject.XOP_GO, currentTryStatement, "catch");
		}
		return throwStatement;
	}

	public AbstractStatement interpret(TryStatement tryStatement)
			throws ParserException {
		addLineNumber(tryStatement);

		AbstractStatement saveTryStatement = currentTryStatement;
		String saveTryLabel = currentTryLabel;

		currentTryStatement = tryStatement;
		currentTryLabel = tryStatement.catchBlock != null ? "catch" : "finally";

		tryStatement.tryBlock.interpretStatement(this);

		writeJump(JsFunctionObject.XOP_GO, tryStatement, "end");

		if (tryStatement.catchBlock != null) {
			setLabel(tryStatement, "catch");
			if (tryStatement.finallyBlock == null) {
				currentTryLabel = saveTryLabel;
				currentTryStatement = saveTryStatement;
			} else {
				currentTryLabel = "finally";
			}

			// add var and init from stack
			writeVarDef(tryStatement.catchIdentifier.string, true);
			writeOp(JsFunctionObject.OP_DROP);
			tryStatement.catchBlock.interpretStatement(this);

			writeJump(JsFunctionObject.XOP_GO, tryStatement, "end");
		}

		// reset everything
		currentTryStatement = saveTryStatement;
		currentTryLabel = saveTryLabel;

		if (tryStatement.finallyBlock != null) {
			// finally block for the case that an exception was thrown --
			// it is kept on the stack and rethrown at the end
			setLabel(tryStatement, "finally");
			tryStatement.finallyBlock.interpretStatement(this);

			if (currentTryStatement == null) {
				writeOp(JsFunctionObject.OP_THROW);
			} else {
				writeJump(JsFunctionObject.XOP_GO, currentTryStatement, "catch");
			}
		}

		// finally block if no exception was thrown
		setLabel(tryStatement, "end");

		if (tryStatement.finallyBlock != null) {
			tryStatement.finallyBlock.interpretStatement(this);
		}

		return tryStatement;
	}

	public AbstractStatement interpret(VariableStatement variableStatement)
			throws ParserException {
		for (int i = 0; i < variableStatement.declarations.length; i++) {
			variableStatement.declarations[i].interpretExpression(this);
		}
		return variableStatement;
	}

	public AbstractStatement interpret(WhileStatement whileStatement)
			throws ParserException {
		addLineNumber(whileStatement);

		AbstractStatement saveBreakStatement = currentBreakStatement;
		AbstractStatement saveContinueStatement = currentContinueStatement;

		currentBreakStatement = whileStatement;
		currentContinueStatement = whileStatement;

		setLabel(whileStatement, "continue");
		visitWithNewLabelSet(whileStatement.expression);
		writeJump(JsFunctionObject.XOP_IF, whileStatement, "break");

		visitWithNewLabelSet(whileStatement.statement);

		writeJump(JsFunctionObject.XOP_GO, whileStatement, "continue");

		setLabel(whileStatement, "break");

		currentBreakStatement = saveBreakStatement;
		currentContinueStatement = saveContinueStatement;
		return whileStatement;
	}

	public AbstractStatement interpret(WithStatement withStatement)
			throws ParserException {
		addLineNumber(withStatement);

		if (currentTryStatement == null) {
			withStatement.expression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_WITH_START);
			withStatement.statement.interpretStatement(this);
			writeOp(JsFunctionObject.OP_WITH_END);
		} else {
			// if an exception is thrown inside the with statement,
			// it is necessary to restore the context
			AbstractStatement saveTryStatement = currentTryStatement;
			String saveTryLabel = currentTryLabel;
			currentTryLabel = "finally";
			currentTryStatement = withStatement;
			withStatement.expression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_WITH_END);
			withStatement.statement.interpretStatement(this);
			writeOp(JsFunctionObject.OP_WITH_END);
			writeJump(JsFunctionObject.XOP_GO, withStatement, "end");

			currentTryStatement = saveTryStatement;
			currentTryLabel = saveTryLabel;

			setLabel(withStatement, "finally");
			writeOp(JsFunctionObject.OP_WITH_END);
			writeOp(JsFunctionObject.OP_THROW);
			setLabel(withStatement, "end");
		}
		return withStatement;
	}

	public AbstractExpression interpret(
			AssignmentExpression assignmentExpression) throws ParserException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = assignmentExpression;
		assignmentExpression.leftExpression.interpretExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return assignmentExpression;
	}

	public AbstractExpression interpret(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws ParserException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = assignmentOperatorExpression;
		assignmentOperatorExpression.leftExpression.interpretExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return assignmentOperatorExpression;
	}

	public AbstractExpression interpret(
			BinaryOperatorExpression binaryOperatorExpression)
			throws ParserException {
		binaryOperatorExpression.leftExpression.interpretExpression(this);
		binaryOperatorExpression.rightExpression.interpretExpression(this);
		writeBinaryOperator(binaryOperatorExpression.operator);
		return binaryOperatorExpression;
	}

	public AbstractExpression interpret(
			CallFunctionExpression callFunctionExpression)
			throws ParserException {
		if (callFunctionExpression.function instanceof PropertyExpression) {
			PropertyExpression pe = (PropertyExpression) callFunctionExpression.function;
			pe.leftExpression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_DUP);
			pe.rightExpression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_GET);
		} else {
			writeOp(JsFunctionObject.OP_PUSH_GLOBAL);
			callFunctionExpression.function.interpretExpression(this);
		}
		// push arguments
		for (int i = 0; i < callFunctionExpression.arguments.length; i++) {
			callFunctionExpression.arguments[i].interpretExpression(this);
		}

		if (currentTryStatement == null) {
			writeXop(JsFunctionObject.XOP_CALL,
					callFunctionExpression.arguments.length);
		} else {
			writeXop(JsFunctionObject.XOP_TRY_CALL,
					callFunctionExpression.arguments.length);
			writeJump(JsFunctionObject.XOP_IF, currentTryStatement,
					currentTryLabel);
		}
		return callFunctionExpression;
	}

	public AbstractExpression interpret(
			ConditionalExpression conditionalExpression) throws ParserException {
		conditionalExpression.expression.interpretExpression(this);
		writeJump(JsFunctionObject.XOP_IF, conditionalExpression, "else");
		conditionalExpression.trueExpression.interpretExpression(this);
		writeJump(JsFunctionObject.XOP_GO, conditionalExpression, "endif");
		setLabel(conditionalExpression, "else");
		conditionalExpression.falseExpression.interpretExpression(this);
		setLabel(conditionalExpression, "endif");
		return conditionalExpression;
	}

	public AbstractExpression interpret(DeleteExpression deleteExpression)
			throws ParserException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = deleteExpression;
		deleteExpression.expression.interpretExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return deleteExpression;
	}

	public AbstractExpression interpret(IncrementExpression incrementExpression)
			throws ParserException {
		AbstractExpression savePendingAssignment = pendingAssignment;
		pendingAssignment = incrementExpression;
		incrementExpression.expression.interpretExpression(this);
		if (pendingAssignment != null) {
			throw new RuntimeException("Pending assignment was not resolved");
		}
		pendingAssignment = savePendingAssignment;
		return incrementExpression;
	}

	public AbstractExpression interpret(
			LogicalAndExpression logicalAndExpression) throws ParserException {
		logicalAndExpression.leftExpression.interpretExpression(this);
		writeOp(JsFunctionObject.OP_DUP);
		// jump (= skip) if false since false && any = false
		writeJump(JsFunctionObject.XOP_IF, logicalAndExpression, "end");
		writeOp(JsFunctionObject.OP_DROP);
		logicalAndExpression.rightExpression.interpretExpression(this);
		setLabel(logicalAndExpression, "end");
		return logicalAndExpression;
	}

	public AbstractExpression interpret(LogicalOrExpression logicalOrExpression)
			throws ParserException {
		logicalOrExpression.leftExpression.interpretExpression(this);
		writeOp(JsFunctionObject.OP_DUP);
		// jump (= skip) if true since true && any =
		writeOp(JsFunctionObject.OP_NOT);
		writeJump(JsFunctionObject.XOP_IF, logicalOrExpression, "end");
		writeOp(JsFunctionObject.OP_DROP);
		logicalOrExpression.rightExpression.interpretExpression(this);
		setLabel(logicalOrExpression, "end");
		return logicalOrExpression;
	}

	public AbstractExpression interpret(NewExpression newExpression)
			throws ParserException {
		newExpression.objName.interpretExpression(this);
		writeOp(JsFunctionObject.OP_NEW);
		if (newExpression.arguments != null) {
			for (int i = 0; i < newExpression.arguments.length; i++) {
				newExpression.arguments[i].interpretExpression(this);
			}
			writeXop(JsFunctionObject.XOP_CALL, newExpression.arguments.length);
		} else {
			writeXop(JsFunctionObject.XOP_CALL, 0);
		}
		writeOp(JsFunctionObject.OP_DROP);
		return newExpression;
	}

	public AbstractExpression interpret(PropertyExpression propertyExpression)
			throws ParserException {
		AbstractExpression pa = pendingAssignment;
		pendingAssignment = null;

		if (pa == null) {
			propertyExpression.leftExpression.interpretExpression(this);
			propertyExpression.rightExpression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_GET);
		} else if (pa instanceof AssignmentExpression) {
			// push value
			((AssignmentExpression) pa).rightExpression
					.interpretExpression(this);
			// push object
			propertyExpression.leftExpression.interpretExpression(this);
			// push property
			propertyExpression.rightExpression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_SET);
		} else if (pa instanceof AssignmentOperatorExpression) {
			// this case is a bit tricky...
			AssignmentOperatorExpression aoe = (AssignmentOperatorExpression) pa;
			propertyExpression.leftExpression.interpretExpression(this);
			propertyExpression.rightExpression.interpretExpression(this);
			// duplicate object and member
			writeOp(JsFunctionObject.OP_DDUP);
			writeOp(JsFunctionObject.OP_GET);
			// push value
			aoe.rightExpression.interpretExpression(this);
			// exec assignment op
			writeBinaryOperator(aoe.type);
			// move result value below object and property
			writeOp(JsFunctionObject.OP_ROT);
			writeOp(JsFunctionObject.OP_SET);
		} else if (pa instanceof IncrementExpression) {
			IncrementExpression ie = (IncrementExpression) pa;
			propertyExpression.leftExpression.interpretExpression(this);
			propertyExpression.rightExpression.interpretExpression(this);
			// duplicate object and member
			writeOp(JsFunctionObject.OP_DDUP);
			writeOp(JsFunctionObject.OP_GET);
			// increment / decrement
			writeXop(JsFunctionObject.XOP_ADD, ie.value);
			// move result value below object and property
			writeOp(JsFunctionObject.OP_ROT);
			writeOp(JsFunctionObject.OP_SET);
			if (ie.post) {
				writeXop(JsFunctionObject.XOP_ADD, -ie.value);
			}
		} else if (pa instanceof DeleteExpression) {
			propertyExpression.leftExpression.interpretExpression(this);
			propertyExpression.rightExpression.interpretExpression(this);
			writeOp(JsFunctionObject.OP_DEL);
		}
		return propertyExpression;
	}

	public AbstractExpression interpret(
			UnaryOperatorExpression unaryOperatorExpression)
			throws ParserException {
		unaryOperatorExpression.expression.interpretExpression(this);
		writeUnaryOperator(unaryOperatorExpression.operator);
		return unaryOperatorExpression;
	}

	public AbstractExpression interpret(VariableExpression variableExpression)
			throws ParserException {
		for (int i = 0; i < variableExpression.declarations.length; i++) {
			variableExpression.declarations[i].interpretExpression(this);
		}
		return variableExpression;
	}

	public AbstractExpression interpret(
			VariableDeclarationExpression variableDeclarationExpression)
			throws ParserException {
		if (variableDeclarationExpression.initializer != null) {
			variableDeclarationExpression.initializer.interpretExpression(this);
			writeVarDef(variableDeclarationExpression.identifier.string, true);
			writeOp(JsFunctionObject.OP_DROP);
		} else {
			writeVarDef(variableDeclarationExpression.identifier.string, false);
		}
		return variableDeclarationExpression;
	}

	public AbstractLiteral interpret(IdentifierLiteral identifierLiteral)
			throws ParserException {
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
			((AssignmentExpression) pa).rightExpression
					.interpretExpression(this);
			writeOpSet(identifierLiteral);
		} else if (pa instanceof AssignmentOperatorExpression) {
			writeOpGet(identifierLiteral);
			((AssignmentOperatorExpression) pa).rightExpression
					.interpretExpression(this);
			writeBinaryOperator(((AssignmentOperatorExpression) pa).type);
			writeOpSet(identifierLiteral);
		} else if (pa instanceof IncrementExpression) {
			IncrementExpression ie = (IncrementExpression) pa;
			writeOpGet(identifierLiteral);
			writeXop(JsFunctionObject.XOP_ADD, ((IncrementExpression) pa).value);
			writeOpSet(identifierLiteral);
			if (ie.post) {
				writeXop(JsFunctionObject.XOP_ADD,
						-((IncrementExpression) pa).value);
			}
		} else if (pa instanceof DeleteExpression) {
			writeOp(JsFunctionObject.OP_CTX);
			writeXop(JsFunctionObject.XOP_PUSH_STR,
					getStringLiteralIndex(identifierLiteral.string));
			writeOp(JsFunctionObject.OP_DEL);
		} else {
			throw new IllegalArgumentException();
		}

		return identifierLiteral;
	}

	public AbstractLiteral interpret(ThisLiteral thisLiteral)
			throws ParserException {
		writeOp(JsFunctionObject.OP_PUSH_THIS);
		return thisLiteral;
	}

	public AbstractLiteral interpret(NullLiteral nullLiteral)
			throws ParserException {
		writeOp(JsFunctionObject.OP_PUSH_NULL);
		return nullLiteral;
	}

	public AbstractLiteral interpret(BooleanLiteral booleanLiteral)
			throws ParserException {
		writeOp(booleanLiteral.value ? JsFunctionObject.OP_PUSH_TRUE
				: JsFunctionObject.OP_PUSH_FALSE);
		return booleanLiteral;
	}

	public AbstractLiteral interpret(NumberLiteral numberLiteral)
			throws ParserException {
		double v = numberLiteral.value;
		if (32767 >= v && v >= -32767 && v == Math.floor(v)) {
			writeXop(JsFunctionObject.XOP_PUSH_INT, (int) v);
		} else {
			Double d = new Double(v);
			int i = numberLiterals.indexOf(d);
			if (i == -1) {
				i = numberLiterals.size();
				numberLiterals.add(d);
			}
			writeXop(JsFunctionObject.XOP_PUSH_NUM, i);
		}

		return numberLiteral;
	}

	public AbstractLiteral interpret(StringLiteral stringLiteral)
			throws ParserException {
		writeXop(JsFunctionObject.XOP_PUSH_STR,
				getStringLiteralIndex(stringLiteral.string));
		return stringLiteral;
	}

	public AbstractLiteral interpret(ArrayLiteral arrayLiteral)
			throws ParserException {
		writeOp(JsFunctionObject.OP_NEW_ARR);
		for (int i = 0; i < arrayLiteral.elements.length; i++) {
			if (arrayLiteral.elements[i] == null) {
				writeOp(JsFunctionObject.OP_PUSH_UNDEF);
			} else {
				arrayLiteral.elements[i].interpretExpression(this);
			}
			writeOp(JsFunctionObject.OP_APPEND);
		}
		return arrayLiteral;
	}

	public AbstractLiteral interpret(FunctionLiteral functionLiteral)
			throws ParserException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		new CompilationInterpreter(this, functionLiteral, new DataOutputStream(
				baos));

		functionLiterals.add(baos.toByteArray());

		writeXop(JsFunctionObject.XOP_PUSH_FN, functionLiterals.size() - 1);

		if (functionLiteral.funcName != null) {
			writeVarDef(functionLiteral.funcName.string, true);
		}
		return functionLiteral;
	}

	public AbstractLiteral interpret(ObjectLiteral objectLiteral)
			throws ParserException {
		writeOp(JsFunctionObject.OP_NEW_OBJ);
		for (int i = 0; i < objectLiteral.properties.length; i++) {
			objectLiteral.properties[i].interpretExpression(this);
		}
		return objectLiteral;
	}

	public AbstractLiteral interpret(ObjectPropertyLiteral objectPropertyLiteral)
			throws ParserException {
		objectPropertyLiteral.name.interpretExpression(this);
		objectPropertyLiteral.value.interpretExpression(this);
		writeOp(JsFunctionObject.OP_SET_KC);

		return objectPropertyLiteral;
	}

	private void writeMagic() throws ParserException {
		try {
			dos.write('M');
			dos.write('i');
			dos.write('n');
			dos.write('i');
			dos.write('J');
			dos.write('o');
			dos.write('e');
			dos.write(0x00); // version
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeCommentBlock(String comment) throws ParserException {
		try {
			if (comment != null) {
				dos.write(BLOCK_COMMENT);
				dos.writeUTF(comment);
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeGlobalStringTableBlock() throws ParserException {
		try {
			if (globalStringTable.size() > 0) {
				dos.write(BLOCK_GLOBAL_STRING_TABLE);
				dos.writeShort(globalStringTable.size());
				for (int i = 0; i < globalStringTable.size(); i++) {
					dos.writeUTF((String) globalStringTable.get(i));
				}
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeNumberLiteralBlock() throws ParserException {
		try {
			if (numberLiterals.size() > 0) {
				dos.write(BLOCK_NUMBER_LITERALS);
				dos.writeShort(numberLiterals.size());
				for (int i = 0; i < numberLiterals.size(); i++) {
					dos.writeDouble(((Double) numberLiterals.get(i))
							.doubleValue());
				}
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeStringLiteralBlock() throws ParserException {
		try {
			if (stringLiterals.size() > 0) {
				dos.write(BLOCK_STRING_LITERALS);
				dos.writeShort(stringLiterals.size());
				for (int i = 0; i < stringLiterals.size(); i++) {
					dos.writeShort((short) ((Integer) globalStringMap
							.get(stringLiterals.get(i))).intValue());
				}
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeLocalVariableNameBlock(IdentifierLiteral[] variables)
			throws ParserException {
		try {
			if (variables != null) {
				dos.write(BLOCK_LOCAL_VARIABLE_NAMES);
				dos.writeShort(variables.length);
				for (int i = 0; i < variables.length; i++) {
					dos.writeShort((short) ((Integer) globalStringMap
							.get(variables[i].string)).intValue());
				}
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeFunctionLiteralBlock() throws ParserException {
		try {
			if (functionLiterals.size() > 0) {
				dos.write(BLOCK_FUNCTION_LITERALS);
				dos.writeShort(functionLiterals.size());
				for (int i = 0; i < functionLiterals.size(); i++) {
					dos.write((byte[]) functionLiterals.get(i));
				}
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeCodeBlock(int localVariableCount, int paramenterCount,
			int flags, byte[] code) throws ParserException {
		try {
			dos.write(BLOCK_CODE);
			dos.writeShort(localVariableCount);
			dos.writeShort(paramenterCount);
			dos.write(flags);
			dos.writeShort(code.length);

			for (int i = 0; i < unresolvedJumps.size(); i += 2) {
				String label = (String) unresolvedJumps.get(i);
				int address = ((Integer) unresolvedJumps.get(i + 1)).intValue();
				Integer target = (Integer) jumpLabels.get(label);

				if (target == null) {
					throw new ParserException("Unresolved Jump Label: " + label);
				}

				int delta = target.intValue() - address - 2;

				code[address + 0] = (byte) (delta >> 8);
				code[address + 1] = (byte) (delta & 255);
			}

			dos.write(code);
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeLineNumberBlock() throws ParserException {
		try {
			dos.write(BLOCK_LINENUMBER);
			int lineNumberCount = lineNumberVector.size();
			dos.writeShort(lineNumberVector.size());
			for (int i = 0; i < lineNumberCount; i++) {
				LineNumber lineNumber = (LineNumber) lineNumberVector.get(i);
				dos.writeShort(lineNumber.programCounter & 0xffff);
				dos.writeShort(lineNumber.lineNumber & 0xffff);
			}
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	/**
	 * 写入结束标志
	 * 
	 * @throws ParserException
	 */
	private void writeEndMarker() throws ParserException {
		try {
			dos.write(BLOCK_END);
		} catch (IOException e) {
			throw new ParserException(e);
		}
	}

	private void writeOp(int op) {
		codeStream.write(op);
	}

	private void writeOpGet(IdentifierLiteral identifier) {
		int index = identifier.index;

		if (Config.FASTLOCALS && enableLocalsOptimization && index >= 0) {
			writeXop(JsFunctionObject.XOP_LCL_GET, index);
		} else {
			writeXop(JsFunctionObject.XOP_PUSH_STR,
					getStringLiteralIndex(identifier.string));
			writeOp(JsFunctionObject.OP_CTX_GET);
		}
	}

	private void writeOpSet(IdentifierLiteral identifier) {
		int index = identifier.index;

		if (Config.FASTLOCALS && enableLocalsOptimization && index >= 0) {
			writeXop(JsFunctionObject.XOP_LCL_SET, index);
		} else {
			writeXop(JsFunctionObject.XOP_PUSH_STR,
					getStringLiteralIndex(identifier.string));
			writeOp(JsFunctionObject.OP_CTX_SET);
		}
	}

	private void visitWithNewLabelSet(AbstractNode node) throws ParserException {
		ArrayList saveLabelSet = labelSet;
		labelSet = new ArrayList();
		if (node instanceof AbstractStatement) {
			((AbstractStatement) node).interpretStatement(this);
		} else if (node instanceof AbstractExpression) {
			((AbstractExpression) node).interpretExpression(this);
		}
		labelSet = saveLabelSet;
	}

	/**
	 * Write a variable-length operation with an immediate parameter to the
	 * given output stream.
	 */
	void writeXop(int opcode, int param) {
		if (opcode == JsFunctionObject.XOP_ADD) {
			switch (param) {
			case 1:
				writeOp(JsFunctionObject.OP_INC);
				return;
			case -1:
				writeOp(JsFunctionObject.OP_DEC);
				return;
			}
		}

		if ((param & 0x0ff80) == 0 || (param & 0x0ff80) == 0xff80) {
			codeStream.write(opcode << 1);
			codeStream.write(param);
		} else {
			codeStream.write((opcode << 1) | 1);
			codeStream.write(param >> 8);
			codeStream.write(param & 255);
		}
	}

	void writeJump(int op, Object base, String type) {
		int pos = codeStream.size() + 1;

		if (base instanceof String) {
			type = type + "-" + base;
		} else if (base instanceof AbstractNode) {
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

	private void setLabel(AbstractNode node, String label) {
		Integer pos = new Integer(codeStream.size());
		jumpLabels.put(label + "=" + node.hashCode(), pos);
		for (int i = 0; i < labelSet.size(); i++) {
			jumpLabels.put(label + "-" + labelSet.get(i), pos);
		}
	}

	private void writeBinaryOperator(Token type) {
		if (type == Token.OPERATOR_ASSIGN) {
			// should be handled as special case
			writeOp(JsFunctionObject.OP_DROP);
		} else if (type == Token.OPERATOR_ARITHMETICAL_AND
				|| type == Token.OPERATOR_ARITHMETICAL_AND_ASSIGN) {
			writeOp(JsFunctionObject.OP_AND);
		} else if (type == Token.OPERATOR_ARITHMETICAL_OR
				|| type == Token.OPERATOR_ARITHMETICAL_OR_ASSIGN) {
			writeOp(JsFunctionObject.OP_OR);
		} else if (type == Token.OPERATOR_ARITHMETICAL_XOR
				|| type == Token.OPERATOR_ARITHMETICAL_XOR_ASSIGN) {
			writeOp(JsFunctionObject.OP_XOR);
		} else if (type == Token.OPERATOR_COMMA) {
			// should be handled as special case in caller to avoid swap
			writeOp(JsFunctionObject.OP_SWAP);
			writeOp(JsFunctionObject.OP_DROP);
		} else if (type == Token.OPERATOR_DIVIDE
				|| type == Token.OPERATOR_DIVIDE_ASSIGN) {
			writeOp(JsFunctionObject.OP_DIV);
		} else if (type == Token.OPERATOR_EQUAL) {
			writeOp(JsFunctionObject.OP_EQEQ);
		} else if (type == Token.OPERATOR_STRICT_EQUAL) {
			writeOp(JsFunctionObject.OP_EQEQEQ);
		} else if (type == Token.OPERATOR_GT) {
			writeOp(JsFunctionObject.OP_GT);
		} else if (type == Token.OPERATOR_GTE) {
			writeOp(JsFunctionObject.OP_LT);
			writeOp(JsFunctionObject.OP_NOT);
		} else if (type == Token.OPERATOR_LT) {
			writeOp(JsFunctionObject.OP_LT);
		} else if (type == Token.OPERATOR_LTE) {
			writeOp(JsFunctionObject.OP_GT);
			writeOp(JsFunctionObject.OP_NOT);
		} else if (type == Token.OPERATOR_MINUS
				|| type == Token.OPERATOR_MINUS_ASSIGN) {
			writeOp(JsFunctionObject.OP_SUB);
		} else if (type == Token.OPERATOR_MOD
				|| type == Token.OPERATOR_MOD_ASSIGN) {
			writeOp(JsFunctionObject.OP_MOD);
		} else if (type == Token.OPERATOR_MUL
				|| type == Token.OPERATOR_MUL_ASSIGN) {
			writeOp(JsFunctionObject.OP_MUL);
		} else if (type == Token.OPERATOR_NOT_EQUAL) {
			writeOp(JsFunctionObject.OP_EQEQ);
			writeOp(JsFunctionObject.OP_NOT);
		} else if (type == Token.OPERATOR_NOT_STRICT_EQUAL) {
			writeOp(JsFunctionObject.OP_EQEQEQ);
			writeOp(JsFunctionObject.OP_NOT);
		} else if (type == Token.OPERATOR_PLUS
				|| type == Token.OPERATOR_PLUS_ASSIGN) {
			writeOp(JsFunctionObject.OP_ADD);
		} else if (type == Token.OPERATOR_SHL
				|| type == Token.OPERATOR_SHL_ASSIGN) {
			writeOp(JsFunctionObject.OP_SHL);
		} else if (type == Token.OPERATOR_SHR
				|| type == Token.OPERATOR_SHR_ASSIGN) {
			writeOp(JsFunctionObject.OP_SHR);
		} else if (type == Token.OPERATOR_ASR
				|| type == Token.OPERATOR_ASR_ASSIGN) {
			writeOp(JsFunctionObject.OP_ASR);
		} else if (type == Token.KEYWORD_IN) {
			writeOp(JsFunctionObject.OP_IN);
		} else if (type == Token.KEYWORD_INSTANCEOF) {
			writeOp(JsFunctionObject.OP_INSTANCEOF);
		} else {
			throw new IllegalArgumentException("Not binary: " + type.toString());
		}
	}

	private void writeUnaryOperator(Token type) {
		if (type == Token.OPERATOR_PLUS) {
			writeXop(JsFunctionObject.XOP_ADD, 0);
		} else if (type == Token.OPERATOR_MINUS) {
			writeOp(JsFunctionObject.OP_NEG);
		} else if (type == Token.OPERATOR_ARITHMETICAL_NOT) {
			writeOp(JsFunctionObject.OP_INV);
		} else if (type == Token.OPERATOR_LOGICAL_NOT) {
			writeOp(JsFunctionObject.OP_NOT);
		} else if (type == Token.KEYWORD_VOID) {
			writeOp(JsFunctionObject.OP_DROP);
			writeOp(JsFunctionObject.OP_PUSH_UNDEF);
		} else if (type == Token.KEYWORD_TYPEOF) {
			writeOp(JsFunctionObject.OP_TYPEOF);
		} else {
			throw new IllegalArgumentException("Not unary: " + type.toString());
		}
	}

	/** value must be on stack, is kept on stack */
	private void writeVarDef(String name, boolean initialize) {
		if (initialize) {
			writeXop(JsFunctionObject.XOP_PUSH_STR, getStringLiteralIndex(name));
			writeOp(JsFunctionObject.OP_CTX_SET);
		}
	}

	/**
	 * 如果待加入的字符串不在全局符号表中，则将字符串加入符号表中
	 * 
	 * @param s
	 *            待加入的字符串
	 */
	private void addToGlobalStringTable(String s) {
		if (globalStringMap.get(s) == null) {
			globalStringMap.put(s, new Integer(globalStringTable.size()));
			globalStringTable.add(s);
		}
	}

	private int getStringLiteralIndex(String string) {
		int i = stringLiterals.indexOf(string);
		if (i == -1) {
			i = stringLiterals.size();
			addToGlobalStringTable(string);
			stringLiterals.add(string);
		}
		return i;
	}

	private void addLineNumber(AbstractStatement statement) {
		if (Config.LINENUMBER) {
			lineNumberVector.add(new LineNumber(codeStream.size(), statement
					.getLineNumber()));
		}
	}

	private class LineNumber {
		private int programCounter;
		private int lineNumber;

		private LineNumber(int programCounter, int lineNumber) {
			this.programCounter = programCounter;
			this.lineNumber = lineNumber;
		}
	}
}
