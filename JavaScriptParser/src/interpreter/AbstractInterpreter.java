package interpreter;

import parser.ParserException;
import ast.*;
import ast.expression.*;
import ast.expression.binary.*;
import ast.expression.literal.*;
import ast.expression.unary.*;
import ast.statement.*;

/**
 * <code>AbstractInterpreter</code> 是所有语义分析器的基类, 定义了对语法树的操作集合.
 * <p>
 * 由于抽象语法树是一个数据结构相对未定的系统, 因此这里采用<b><code>Visitor</code> </b>模式将数据结构和作用于结构上的操作解耦,
 * 使得操作集合可以相对自由地修改和维护.
 * <p>
 * <code>AbstractInterpreter</code> 相当于Visitor模式中的抽象访问者
 * 
 * @author Jarod Yv
 * @see {@link AbstractSyntaxNode}
 */
public abstract class AbstractInterpreter {

	/**
	 * 解释 <code>Program</code> 语法节点的语法. <code>Program</code> 是整个语法树的根,
	 * 因此此方法也是解释过程的入口.
	 * 
	 * @param program
	 * @return
	 * @throws ParserException
	 */
	public abstract Program interpret(Program program) throws ParserException;

	public abstract AbstractStatement interpret(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(BlockStatement blockStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(BreakStatement breakStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(CaseStatement caseStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(
			ContinueStatement continueStatement) throws ParserException;

	public abstract AbstractStatement interpret(DoStatement doStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(EmptyStatement emptyStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(
			ExpressionStatement expressionStatement) throws ParserException;

	public abstract AbstractStatement interpret(ForStatement forStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(ForInStatement forInStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(IfStatement ifStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(
			LabelledStatement labelledStatement) throws ParserException;

	public abstract AbstractStatement interpret(ReturnStatement returnStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(SwitchStatement switchStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(ThrowStatement throwStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(TryStatement tryStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(
			VariableStatement variableStatement) throws ParserException;

	public abstract AbstractStatement interpret(WhileStatement whileStatement)
			throws ParserException;

	public abstract AbstractStatement interpret(WithStatement withStatement)
			throws ParserException;

	//
	// expressions
	//
	public abstract AbstractExpression interpret(
			AssignmentExpression assignmentExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws ParserException;

	public abstract AbstractExpression interpret(
			BinaryOperatorExpression binaryOperatorExpression)
			throws ParserException;

	public abstract AbstractExpression interpret(
			CallFunctionExpression callFunctionExpression)
			throws ParserException;

	public abstract AbstractExpression interpret(
			ConditionalExpression conditionalExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			DeleteExpression deleteExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			IncrementExpression incrementExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			LogicalAndExpression logicalAndExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			LogicalOrExpression logicalOrExpression) throws ParserException;

	public abstract AbstractExpression interpret(NewExpression newExpression)
			throws ParserException;

	public abstract AbstractExpression interpret(
			PropertyExpression propertyExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			UnaryOperatorExpression unaryOperatorExpression)
			throws ParserException;

	public abstract AbstractExpression interpret(
			VariableExpression variableExpression) throws ParserException;

	public abstract AbstractExpression interpret(
			VariableDeclarationExpression variableDeclarationExpression)
			throws ParserException;

	//
	// literals
	//
	public abstract AbstractLiteral interpret(
			IdentifierLiteral identifierLiteral) throws ParserException;

	public abstract AbstractLiteral interpret(ThisLiteral thisLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(NullLiteral nullLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(BooleanLiteral booleanLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(NumberLiteral numberLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(StringLiteral stringLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(ArrayLiteral arrayLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(FunctionLiteral functionLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(ObjectLiteral objectLiteral)
			throws ParserException;

	public abstract AbstractLiteral interpret(
			ObjectPropertyLiteral objectPropertyLiteral) throws ParserException;
}
