package ast.statement;

import compiler.CompilerException;
import compiler.ICompilable;

/**
 * <code>BlockStatement</code> 定义了代码块的语法树结构
 * <p>
 * Block的语法结构如下:
 * 
 * <pre>
 * <i><b>Block:</b></i>
 * 	{StatementList(opt)}
 * </pre>
 * 
 * @author Jarod Yv
 * @see ECMA-262 61页 12.1.Block
 */
public class BlockStatement extends AbstractStatement {
	/** 代码块中的所有语句 */
	public AbstractStatement[] statementList = null;

	/**
	 * 构造函数
	 * 
	 * @param statementList
	 *            {@link #statementList}
	 */
	public BlockStatement(AbstractStatement[] statementList) {
		this.statementList = statementList;
	}

	public AbstractStatement compileStatement(ICompilable compiler)
			throws CompilerException {
		return compiler.compile(this);
	}

	public void release() {
		release(statementList);
		statementList = null;
	}

}
