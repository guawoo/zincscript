package compiler;

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
 * <code>IComplable</code>接口定义了对语法树的操作集合.
 * <p>
 * 由于抽象语法树是一个数据结构相对未定的系统, 因此这里采用<b><code>Visitor</code> </b>模式将数据结构和作用于结构上的操作解耦,
 * 使得操作集合可以相对自由地修改和维护.
 * <p>
 * <code>IComplable</code> 相当于Visitor模式中的抽象访问者
 * 
 * @author Jarod Yv
 * @see {@link AbstractSyntaxNode}
 */
public interface ICompilable {

	// /////////////////////////////// Program ///////////////////////////////
	/**
	 * 处理/编译{@link Program}语法节点的语法. {@link Program}是整个语法树的根, 因此此方法也是解释过程的入口.
	 */
	public abstract Program compile(Program program) throws CompilerException;

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Statement //////////////////////////////
	/**
	 * 处理/编译{@link FunctionDeclarationStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(
			FunctionDeclarationStatement functionDeclarationStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link BlockStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(BlockStatement blockStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link BreakStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(BreakStatement breakStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link CaseStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(CaseStatement caseStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link ContinueStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(
			ContinueStatement continueStatement) throws CompilerException;

	/**
	 * 处理/编译{@link DoWhileStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(DoWhileStatement doStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link EmptyStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(EmptyStatement emptyStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link ExpressionStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(
			ExpressionStatement expressionStatement) throws CompilerException;

	/**
	 * 处理/编译{@link ForStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(ForStatement forStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link ForInStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(ForInStatement forInStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link IfStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(IfStatement ifStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link LabelledStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(
			LabelledStatement labelledStatement) throws CompilerException;

	/**
	 * 处理/编译{@link ReturnStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(ReturnStatement returnStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link SwitchStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(SwitchStatement switchStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link ThrowStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(ThrowStatement throwStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link TryStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(TryStatement tryStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link VariableStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(
			VariableStatement variableStatement) throws CompilerException;

	/**
	 * 处理/编译{@link WhileStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(WhileStatement whileStatement)
			throws CompilerException;

	/**
	 * 处理/编译{@link WithStatement}语法节点的语法
	 */
	public abstract AbstractStatement compile(WithStatement withStatement)
			throws CompilerException;

	// ///////////////////////////////////////////////////////////////////////

	// ////////////////////////////// Expression /////////////////////////////
	/**
	 * 处理/编译{@link AssignmentExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			AssignmentExpression assignmentExpression) throws CompilerException;

	/**
	 * 处理/编译{@link AssignmentOperatorExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			AssignmentOperatorExpression assignmentOperatorExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link BinaryOperatorExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			BinaryOperatorExpression binaryOperatorExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link CallFunctionExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			CallFunctionExpression callFunctionExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link ConditionalExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			ConditionalExpression conditionalExpression) throws CompilerException;

	/**
	 * 处理/编译{@link DeleteExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(DeleteExpression deleteExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link IncrementExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			IncrementExpression incrementExpression) throws CompilerException;

	/**
	 * 处理/编译{@link LogicalAndExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			LogicalAndExpression logicalAndExpression) throws CompilerException;

	/**
	 * 处理/编译{@link LogicalOrExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			LogicalOrExpression logicalOrExpression) throws CompilerException;

	/**
	 * 处理/编译{@link NewExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(NewExpression newExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link PropertyExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			PropertyExpression propertyExpression) throws CompilerException;

	/**
	 * 处理/编译{@link UnaryOperatorExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			UnaryOperatorExpression unaryOperatorExpression)
			throws CompilerException;

	/**
	 * 处理/编译{@link VariableExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			VariableExpression variableExpression) throws CompilerException;

	/**
	 * 处理/编译{@link VariableDeclarationExpression}语法节点的语法
	 */
	public abstract AbstractExpression compile(
			VariableDeclarationExpression variableDeclarationExpression)
			throws CompilerException;

	// ///////////////////////////////////////////////////////////////////////

	// /////////////////////////////// Literal ///////////////////////////////
	/**
	 * 处理/编译{@link IdentifierLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(
			IdentifierLiteral identifierLiteral) throws CompilerException;

	/**
	 * 处理/编译{@link ThisLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(ThisLiteral thisLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link NullLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(NullLiteral nullLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link BooleanLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(BooleanLiteral booleanLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link NumberLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(NumberLiteral numberLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link StringLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(StringLiteral stringLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link ArrayLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(ArrayLiteral arrayLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link FunctionLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(FunctionLiteral functionLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link ObjectLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(ObjectLiteral objectLiteral)
			throws CompilerException;

	/**
	 * 处理/编译{@link ObjectPropertyLiteral}语法节点的语法
	 */
	public abstract AbstractLiteral compile(
			ObjectPropertyLiteral objectPropertyLiteral) throws CompilerException;
	// ///////////////////////////////////////////////////////////////////////
}
