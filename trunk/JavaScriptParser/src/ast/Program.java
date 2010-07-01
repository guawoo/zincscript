package ast;

import ast.statement.AbstractStatement;
import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>Program</code> 是整棵语法树的根. 从结构上讲, <code>Program</code>由若干
 * <code>Statement</code>构成
 * 
 * @author Jarod Yv
 */
public class Program extends AbstractSyntaxNode {

	/**
	 * 语句集. 从语法树结构上看, statements是program的子树集合
	 */
	public AbstractStatement[] statements = null;

	/**
	 * 函数定义
	 */
	public AbstractStatement[] functions = null;

	/**
	 * 构造函数
	 * 
	 * @param statements
	 *            语句集
	 */
	public Program(AbstractStatement[] statements) {
		this.statements = statements;
	}

	/**
	 * 访问者接口
	 * <p>
	 * 用于调用相关语义分析器对语法树进行语义分析
	 * 
	 * @param compiler
	 *            编译器
	 * @return {@link Program}
	 * @throws ParserException
	 */
	public Program compileProgram(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(statements);
		statements = null;
		release(functions);
		functions = null;
		System.gc();
	}

	public void print() {
		if (statements != null) {
			System.out.println("statements");
			for (int i = 0; i < statements.length; i++) {
				statements[i].print();
			}
		}
		if (functions != null) {
			System.out.println("functions");
			for (int i = 0; i < functions.length; i++) {
				functions[i].print();
			}
		}
	}
}
