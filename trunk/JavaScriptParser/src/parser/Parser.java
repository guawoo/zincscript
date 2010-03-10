package parser;

import utils.ArrayList;
import utils.ListUtil;
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
 * <code>Parser</code> 是JavaScript的语法解析器。负责将JavaScript脚本解析成抽象语法树
 * 
 * @author Jarod Yv
 */
public final class Parser {

	// 词法分析器
	private Lexer lexer = null;

	// 当前词法单元
	private Token syntax = null;

	// 标识是否要换到下一行
	private boolean isNewline = false;

	// 语法解析器的唯一实例
	private static Parser instance = null;

	/**
	 * 获取语法分析器的唯一实例
	 * 
	 * @return {@link #instance}
	 */
	public static Parser getInstace() {
		if (instance == null)
			instance = new Parser();
		return instance;
	}

	private Parser() {
		this.lexer = Lexer.getInstace();
		// readToken();
	}

	/**
	 * 将整个脚本解析成一棵抽象语法树(AST)
	 * 
	 * @param script
	 *            脚本内容
	 * @return 这个脚本的抽象语法树
	 * @throws ParserException
	 * @see ECMA-262 75页~76页 14.Program
	 */
	public final Program parseProgram(String script) throws ParserException {
		this.lexer.setScript(script);
		ArrayList statements = new ArrayList();
		// 解析SourceElements
		nextSyntax();
		statements.add(parseSourceElement());
		while (Token.EOF != this.syntax) {
			statements.add(parseSourceElement());
		}
		return new Program(ListUtil.list2StatementArray(statements));
	}

	/**
	 * 解析<b><code>SourceElement</code></b>
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 75页~76页 14.Program
	 */
	private final AbstractStatement parseSourceElement() throws ParserException {
		if (Token.KEYWORD_FUNCTION == syntax) {
			return parseFunctionDeclaration();
		} else {
			return parseStatement();
		}
	}

	/**
	 * 解析函数定义语句
	 * 
	 * @see ECMA-262 71页~74页 13.Function Defination
	 * @return 函数声明语句
	 * @throws ParserException
	 */
	private final AbstractStatement parseFunctionDeclaration()
			throws ParserException {
		return new FunctionDeclarationStatement(parseFunctionDefinition(true));
	}

	/**
	 * 解析语句语法
	 * 
	 * @see ECMA-262 61页~71页 12.Statements
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseStatement() throws ParserException {
		AbstractStatement statement = null;
		int lineNumber = 0;

		if (Config.LINENUMBER) {
			lineNumber = getLineNumber();
		}

		if (Token.OPERATOR_OPENBRACE == syntax) {
			statement = parseBlockStatement();
		} else if (Token.KEYWORD_VAR == syntax) {
			statement = parseVariableStatement();
		} else if (Token.OPERATOR_SEMICOLON == syntax) {
			statement = parseEmptyStatement();
		} else if (Token.KEYWORD_IF == syntax) {
			statement = parseIfStatement();
		} else if (Token.KEYWORD_DO == syntax) {
			statement = parseDoWhileStatement();
		} else if (Token.KEYWORD_WHILE == syntax) {
			statement = parseWhileStatement();
		} else if (Token.KEYWORD_FOR == syntax) {
			statement = parseForStatement();
		} else if (Token.KEYWORD_CONTINUE == syntax) {
			statement = parseContinueStatement();
		} else if (Token.KEYWORD_BREAK == syntax) {
			statement = parseBreakStatement();
		} else if (Token.KEYWORD_RETURN == syntax) {
			statement = parseReturnStatement();
		} else if (Token.KEYWORD_WITH == syntax) {
			statement = parseWithStatement();
		} else if (Token.KEYWORD_THROW == syntax) {
			statement = parseThrowStatement();
		} else if (Token.KEYWORD_TRY == syntax) {
			statement = parseTryStatement();
		} else if (Token.KEYWORD_SWITCH == syntax) {
			statement = parseSwitchStatement();
		} else {
			statement = parseExpressionStatement();
		}

		if (Config.LINENUMBER) {
			statement.setLineNumber(lineNumber);
		}

		return statement;
	}

	/**
	 * 生成代码块的语法树节点
	 * 
	 * @see ECMA-262 61页 12.1.Block
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseBlockStatement()
			throws ParserException {
		// 读取'{}'之间的语句
		nextSyntax(Token.OPERATOR_OPENBRACE);
		ArrayList statements = new ArrayList();
		while (syntax != Token.OPERATOR_CLOSEBRACE) {
			statements.add(parseStatement());
		}
		nextSyntax(Token.OPERATOR_CLOSEBRACE);

		return new BlockStatement(ListUtil.list2StatementArray(statements));
	}

	/**
	 * 解析生成 <b><code>var</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 62页 12.2.Variable statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseVariableStatement()
			throws ParserException {
		ArrayList declarationVector = new ArrayList();

		nextSyntax(Token.KEYWORD_VAR);
		// var后至少要有一个变量
		declarationVector.add(parseVariableDeclarationException(true));
		while (syntax == Token.OPERATOR_COMMA) {
			nextSyntax(Token.OPERATOR_COMMA);// 多个变量用逗号分隔
			declarationVector.add(parseVariableDeclarationException(true));
		}

		readTokenSemicolon();
		return new VariableStatement(ListUtil
				.list2DeclarationArray(declarationVector));
	}

	/**
	 * 解析生成空语句的语法树节点
	 * 
	 * @see ECMA-262 63页 12.3.Empty statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseEmptyStatement()
			throws ParserException {
		nextSyntax(Token.OPERATOR_SEMICOLON);
		return new EmptyStatement();
	}

	/**
	 * 解析生成表达式语句的语法树节点
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 63页 12.4.Expression Statement
	 * @see ECMA-262 69页 12.12.Labelled Statements
	 */
	private final AbstractStatement parseExpressionStatement()
			throws ParserException {
		AbstractExpression expression = parseExpression(true);

		// 对于"标识符+:"形式的语法，应当视为Labelled Statements
		if (expression instanceof IdentifierLiteral
				&& syntax == Token.OPERATOR_COLON) {
			nextSyntax(Token.OPERATOR_COLON);
			return new LabelledStatement((IdentifierLiteral) expression,
					parseStatement());
		} else {
			readTokenSemicolon();
			return new ExpressionStatement(expression);
		}
	}

	/**
	 * 解析生成 <b><code>if</code></b> 语句的语法树节点
	 * 
	 * @see EMCA-262 63页~64页 12.5.The <b><code>if</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseIfStatement() throws ParserException {
		AbstractExpression expression = null;// 条件表达式
		AbstractStatement trueStatement = null;// 条件为真时执行的语句
		AbstractStatement falseStatement = null;// 条件为假是执行的语句

		// 读取条件表达式
		nextSyntax(Token.KEYWORD_IF);
		nextSyntax(Token.OPERATOR_OPENPAREN);
		expression = parseExpression(true);
		nextSyntax(Token.OPERATOR_CLOSEPAREN);
		// 读取条件为真是执行的语句
		trueStatement = parseStatement();
		// 如果有else, 则读取条件为假时的执行语句
		if (syntax == Token.KEYWORD_ELSE) {
			nextSyntax(Token.KEYWORD_ELSE);
			falseStatement = parseStatement();
		}

		return new IfStatement(expression, trueStatement, falseStatement);
	}

	/**
	 * 解析生成 <b><code>do-while</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 64页 12.6.1.The <b><code>do-while</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseDoWhileStatement()
			throws ParserException {
		AbstractStatement statement = null;// do的制定语句
		AbstractExpression expression = null;// while的条件表达式

		nextSyntax(Token.KEYWORD_DO);
		statement = parseStatement();
		nextSyntax(Token.KEYWORD_WHILE);
		nextSyntax(Token.OPERATOR_OPENPAREN);
		expression = parseExpression(true);
		nextSyntax(Token.OPERATOR_CLOSEPAREN);

		return new DoWhileStatement(statement, expression);
	}

	/**
	 * 解析生成 <b><code>while</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 64页~65页 12.6.2.The <b><code>while</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseWhileStatement()
			throws ParserException {
		AbstractExpression expression = null;// while后的条件表达式
		AbstractStatement statement = null;// while循环体语句
		// 读取while后的条件表达式
		nextSyntax(Token.KEYWORD_WHILE);
		nextSyntax(Token.OPERATOR_OPENPAREN);
		expression = parseExpression(true);
		nextSyntax(Token.OPERATOR_CLOSEPAREN);
		// 读取while循环体语句
		statement = parseStatement();

		return new WhileStatement(expression, statement);
	}

	/**
	 * 解析生成 <b><code>for</code></b> 和 <b><code>for-in</code></b> 语句的语法树节点
	 * <p>
	 * for语句有如下4中语法结构：
	 * <ul>
	 * <li>for(ExpressionNoIn_opt; Expression_opt; Expression_opt)
	 * <li>for(var VariableDeclarationListNoIn; Expression_opt; Expression_opt)
	 * <li>for(LeftHandSideExpression in Expression)
	 * <li>for(var VariableDeclarationNoIn in Expression)
	 * </ul>
	 * 
	 * @see ECMA-262 65页 12.6.3.The <b><code>for</code></b> statement
	 * @see ECMA-262 65页~66页 12.6.4.The <b><code>for-in</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseForStatement() throws ParserException {
		final int STATE_INIT = 0;
		final int STATE_CASE_1 = 1;// for(var
		final int STATE_CASE_2 = 2;// for(Expression
		final int STATE_CASE_3 = 3;// for(...in
		final int STATE_CASE_4 = 4;// for(var VariableDeclarationList
		final int STATE_CASE_5 = 5;// for(...; ...; ...)
		// 非for-in语法
		AbstractExpression initial = null;// 变量初始语句
		AbstractExpression condition = null;// 条件判断语句
		AbstractExpression increment = null;// 变量改变语句

		// for-in语法
		AbstractExpression declaration = null;// 变量声明
		AbstractExpression variable = null;// 参数列表
		AbstractExpression expression = null;// in范围语句

		// for语句执行体
		AbstractStatement statement = null;

		nextSyntax(Token.KEYWORD_FOR);
		nextSyntax(Token.OPERATOR_OPENPAREN);

		int state = STATE_INIT;
		while (statement == null) {
			switch (state) {
			case STATE_INIT:
				if (syntax == Token.KEYWORD_VAR) {
					state = STATE_CASE_1;
				} else if (syntax != Token.OPERATOR_SEMICOLON) {
					state = STATE_CASE_2;
				} else {
					state = STATE_CASE_5;
				}
				break;
			case STATE_CASE_1:
				nextSyntax(Token.KEYWORD_VAR);
				declaration = parseVariableDeclarationException(false);
				if (syntax == Token.KEYWORD_IN) {
					variable = declaration;
					state = STATE_CASE_3;
				} else {
					state = STATE_CASE_4;
				}
				break;
			case STATE_CASE_2:
				initial = parseExpression(false);
				if (syntax == Token.KEYWORD_IN) {
					variable = initial;
					state = STATE_CASE_3;
				} else {
					state = STATE_CASE_5;
				}
				break;
			case STATE_CASE_3:
				nextSyntax(Token.KEYWORD_IN);
				expression = parseExpression(true);
				nextSyntax(Token.OPERATOR_CLOSEPAREN);
				// 生成for( ... in ... )形式的for语句语法单元
				statement = new ForInStatement(variable, expression,
						parseStatement());
				break;
			case STATE_CASE_4:
				ArrayList declarationVector = new ArrayList();
				declarationVector.add(declaration);
				while (syntax == Token.OPERATOR_COMMA) {
					nextSyntax(Token.OPERATOR_COMMA);
					declarationVector
							.add(parseVariableDeclarationException(false));
				}
				initial = new VariableExpression(ListUtil
						.list2DeclarationArray(declarationVector));
			case STATE_CASE_5:
				// 第一个分号前的语句已经处理完, 此处无需进一步处理
				nextSyntax(Token.OPERATOR_SEMICOLON);

				// 处理第2个分号前的语句 -- 条件判断表达式
				if (syntax != Token.OPERATOR_SEMICOLON) {
					condition = parseExpression(true);
				}
				nextSyntax(Token.OPERATOR_SEMICOLON);

				// 处理第3个分号前的语句 -- 变量修改表达式
				if (syntax != Token.OPERATOR_CLOSEPAREN) {
					increment = parseExpression(true);
				}
				nextSyntax(Token.OPERATOR_CLOSEPAREN);

				// 生成for(...; ...; ...)形式的for语句语法单元
				statement = new ForStatement(initial, condition, increment,
						parseStatement());
				break;
			}
		}

		return statement;
	}

	/**
	 * 解析生成 <b><code>continue</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 66页~67页 12.7.The <b><code>continue</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseContinueStatement()
			throws ParserException {
		IdentifierLiteral identifier = null;
		nextSyntax(Token.KEYWORD_CONTINUE);
		if (syntax != Token.OPERATOR_SEMICOLON) {
			identifier = parseIdentifier();
		}
		readTokenSemicolon();
		return new ContinueStatement(identifier);
	}

	/**
	 * 解析生成 <b><code>break</code></b> 语句的语法树节点
	 * 
	 * @see EMCA-262 67页 12.8.The <b><code>break</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseBreakStatement()
			throws ParserException {
		IdentifierLiteral identifier = null;
		nextSyntax(Token.KEYWORD_BREAK);
		// 如果break后跟的不是';', 说明break后面有内容, 读取'break'后的内容
		if (Token.OPERATOR_SEMICOLON != syntax) {
			identifier = parseIdentifier();
		}
		readTokenSemicolon();
		return new BreakStatement(identifier);
	}

	/**
	 * 解析生成 <b><code>return</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 67页 12.9.The <b><code>return</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseReturnStatement()
			throws ParserException {
		AbstractExpression result = null;// 返回的结果表达式

		// 读取结果表达式
		nextSyntax(Token.KEYWORD_RETURN);
		if (syntax != Token.OPERATOR_SEMICOLON) {
			result = parseExpression(true);
		}
		readTokenSemicolon();

		return new ReturnStatement(result);
	}

	/**
	 * 解析生成 <b><code>with</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 67页~68页 12.10.The <b><code>with</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseWithStatement() throws ParserException {
		AbstractExpression expression = null;// with后的条件表达式
		AbstractStatement statement = null;// 满足条件时执行的语句

		// 读取with后的条件表达式
		nextSyntax(Token.KEYWORD_WITH);
		nextSyntax(Token.OPERATOR_OPENPAREN);
		expression = parseExpression(true);
		nextSyntax(Token.OPERATOR_CLOSEPAREN);
		// 读取满足条件时执行的语句
		statement = parseStatement();

		return new WithStatement(expression, statement);
	}

	/**
	 * 解析生成 <b><code>switch</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 68页~69页 12.11.The <b><code>switch</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseSwitchStatement()
			throws ParserException {
		AbstractExpression switchExpression = null;
		ArrayList caseClauseVector = new ArrayList();
		boolean hasDefault = false;// 标识是否已经出现了default

		// 读取switch()中的条件表达式
		nextSyntax(Token.KEYWORD_SWITCH);
		nextSyntax(Token.OPERATOR_OPENPAREN);
		switchExpression = parseExpression(true);
		nextSyntax(Token.OPERATOR_CLOSEPAREN);

		// 读取{}之间的case语句
		nextSyntax(Token.OPERATOR_OPENBRACE);
		while (syntax != Token.OPERATOR_CLOSEBRACE) {
			AbstractExpression caseExpression = null;// case后的条件表达式
			ArrayList caseStatements = new ArrayList();// case块的语句
			if (syntax == Token.KEYWORD_CASE) {
				nextSyntax(Token.KEYWORD_CASE);
				caseExpression = parseExpression(true);
				nextSyntax(Token.OPERATOR_COLON);
			} else {
				if (hasDefault == false) {
					hasDefault = true;
				} else {
					throwParserException("switch语句中重复出现default");
				}

				nextSyntax(Token.KEYWORD_DEFAULT);
				caseExpression = null;
				nextSyntax(Token.OPERATOR_COLON);
			}

			while (syntax != Token.KEYWORD_CASE
					&& syntax != Token.KEYWORD_DEFAULT
					&& syntax != Token.OPERATOR_CLOSEBRACE) {
				caseStatements.add(parseStatement());
			}

			caseClauseVector.add(new CaseStatement(caseExpression, ListUtil
					.list2StatementArray(caseStatements)));
		}
		nextSyntax(Token.OPERATOR_CLOSEBRACE);

		// ArrayList to array
		CaseStatement[] caseClauseArray = new CaseStatement[caseClauseVector
				.size()];
		caseClauseVector.toArray(caseClauseArray);
		caseClauseVector = null;

		return new SwitchStatement(switchExpression, caseClauseArray);
	}

	/**
	 * 解析生成 <b><code>trown</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 69页~70页 12.13.The <b><code>trown</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseThrowStatement()
			throws ParserException {
		nextSyntax(Token.KEYWORD_THROW);
		return new ThrowStatement(parseExpression(true));
	}

	/**
	 * 解析生成 <b><code>try</code></b> 语句的语法树节点
	 * 
	 * @see ECMA-262 70页~71页 12.14.The <b><code>try</code></b> statement
	 * @return
	 * @throws ParserException
	 */
	private final AbstractStatement parseTryStatement() throws ParserException {
		AbstractStatement tryBlock = null;// try块
		IdentifierLiteral catchIdentifier = null;// catch的异常
		AbstractStatement catchBlock = null;// catch块
		AbstractStatement finallyBlock = null;// finally块

		// 读取try块
		nextSyntax(Token.KEYWORD_TRY);
		tryBlock = parseBlockStatement();
		// 检查是否有catch或finally关键字
		if (syntax != Token.KEYWORD_CATCH && syntax != Token.KEYWORD_FINALLY) {
			throwParserException("try块后要有catch或finally块");
		}
		// 读取catch块
		if (syntax == Token.KEYWORD_CATCH) {
			nextSyntax(Token.KEYWORD_CATCH);
			// 读取catch的异常
			nextSyntax(Token.OPERATOR_OPENPAREN);
			catchIdentifier = parseIdentifier();
			nextSyntax(Token.OPERATOR_CLOSEPAREN);
			// 读取catch块
			catchBlock = parseBlockStatement();
		}
		// 读取finally块
		if (syntax == Token.KEYWORD_FINALLY) {
			nextSyntax(Token.KEYWORD_FINALLY);
			finallyBlock = parseBlockStatement();
		}

		return new TryStatement(tryBlock, catchIdentifier, catchBlock,
				finallyBlock);
	}

	//
	// Expressions
	//
	/**
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 */
	private final VariableDeclarationExpression parseVariableDeclarationException(
			boolean inFlag) throws ParserException {
		IdentifierLiteral identifier = parseIdentifier();
		AbstractExpression initializer = null;

		if (syntax == Token.OPERATOR_ASSIGN) {
			nextSyntax(Token.OPERATOR_ASSIGN);
			initializer = parseAssignmentExpression(inFlag);
		}

		return new VariableDeclarationExpression(identifier, initializer);
	}

/**
	 * 根据运算符的优先级顺序, 将表达式链式解析成一颗表达式树.
	 * <p>
	 * 所谓链式解析,即由一个入口(链首)开始，按照一定规则，依次调用上一级(后续链)的方法，如此一层一层的调用，最终完成整个解析过程。
	 * <code>parseExpression</code> 便是解析的入口和出口.
	 * <p>
	 * JavaScript表达式解析使用运算符优先级规则. 该规则在计算表达式时控制运算符执行的顺序. 具有较高优先级的运算符先于较低优先级的运算符执行.
	 * <p>
	 * 按照优先级由高到低排列如下:
	 * <p>
	 * <dl>
	 * <dt>. [] ()<dd>字段访问、数组下标、函数调用以及表达式分组
	 * <dt>++ -- - ~ ! delete new typeof void<dd>一元运算符、返回数据类型、对象创建、未定义值
	 * <dt>/ %<dd>乘法、除法、取模
	 * <dt>+ - +<dd>加法、减法、字符串连接 
	 * <dt><< >> >>> <dd>移位 
	 * <dt>< <= > >= instanceof<dd>小于、小于等于、大于、大于等于、instanceof 
	 * <dt>== != === !== <dd>等于、不等于、严格相等、非严格相等 
	 * <dt>& <dd>按位与 
	 * <dt> ^<dd>按位异或
	 * <dt>|<dd>按位或 
	 * <dt>&&<dd>逻辑与 
	 * <dt>|| <dd>逻辑或 
	 * <dt>?: <dd>条件 
	 * <dt>= oP= <dd>赋值、运算赋值 
	 * <dt>, <dd>多重求值
	 * </dl>
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 */
	private final AbstractExpression parseExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算(后续链)符的解析
		// 这里是'='赋值运算符
		AbstractExpression leftExeption = parseAssignmentExpression(inFlag);

		// 然后调用本运算符的解析
		// 这里是','运算符
		while (true) {
			if (syntax == Token.OPERATOR_COMMA) {
				nextSyntax(Token.OPERATOR_COMMA);
				AbstractExpression rightException = parseAssignmentExpression(inFlag);
				leftExeption = new BinaryOperatorExpression(leftExeption,
						rightException, Token.OPERATOR_COMMA);
			} else {
				return leftExeption;
			}
		}
	}

	/**
	 * 解析赋值表达式
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseAssignmentExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符('?:')的解析
		AbstractExpression expression = parseConditionalExpression(inFlag);

		// 然后调用赋值符的解析
		// 赋值运算符需要自右向左解析
		if (syntax == Token.OPERATOR_ASSIGN) {
			nextSyntax();
			return new AssignmentExpression(expression,
					parseAssignmentExpression(inFlag));
		} else if (syntax == Token.OPERATOR_ASSIGN
				|| syntax == Token.OPERATOR_MUL_ASSIGN
				|| syntax == Token.OPERATOR_DIVIDE_ASSIGN
				|| syntax == Token.OPERATOR_MOD_ASSIGN
				|| syntax == Token.OPERATOR_PLUS_ASSIGN
				|| syntax == Token.OPERATOR_MINUS_ASSIGN
				|| syntax == Token.OPERATOR_SHL_ASSIGN
				|| syntax == Token.OPERATOR_SHR_ASSIGN
				|| syntax == Token.OPERATOR_ASR_ASSIGN
				|| syntax == Token.OPERATOR_ARITHMETICAL_AND_ASSIGN
				|| syntax == Token.OPERATOR_ARITHMETICAL_OR_ASSIGN
				|| syntax == Token.OPERATOR_ARITHMETICAL_XOR_ASSIGN) {
			Token op = syntax;
			nextSyntax();
			return new AssignmentOperatorExpression(expression,
					parseAssignmentExpression(inFlag), op);
		} else {
			return expression;
		}
	}

	/**
	 * 解析<b><code>?:</code> </b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseConditionalExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符('||')的解析
		AbstractExpression expression = parseLogicalOrExpression(inFlag);

		// 然后调用'?:'的解析
		if (syntax == Token.OPERATOR_QUESTION) {
			nextSyntax(Token.OPERATOR_QUESTION);
			AbstractExpression trueExpression = parseAssignmentExpression(inFlag);
			nextSyntax(Token.OPERATOR_COLON);
			AbstractExpression falseExpression = parseAssignmentExpression(inFlag);
			return new ConditionalExpression(expression, trueExpression,
					falseExpression);
		} else {
			return expression;
		}
	}

	/**
	 * 解析<b><code>||</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseLogicalOrExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符('&&')的解析
		AbstractExpression leftExpression = parseLogicalAndExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'||'的解析
		while (true) {
			if (syntax == Token.OPERATOR_LOGICAL_OR) {
				nextSyntax(Token.OPERATOR_LOGICAL_OR);
				rightExpression = parseLogicalAndExpression(inFlag);
				leftExpression = new LogicalOrExpression(leftExpression,
						rightExpression);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>&&</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseLogicalAndExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符('|')的解析
		AbstractExpression leftExpression = parseArithmeticalOrExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'&&'的解析
		while (true) {
			if (syntax == Token.OPERATOR_LOGICAL_AND) {
				nextSyntax(Token.OPERATOR_LOGICAL_AND);
				rightExpression = parseArithmeticalOrExpression(inFlag);
				leftExpression = new LogicalAndExpression(leftExpression,
						rightExpression);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>|</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseArithmeticalOrExpression(
			boolean inFlag) throws ParserException {
		// 首先调用上一优先级运算符('^')的解析
		AbstractExpression leftExpression = parseArithmeticalXorExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'|'的解析
		while (true) {
			if (syntax == Token.OPERATOR_ARITHMETICAL_OR) {
				nextSyntax(Token.OPERATOR_ARITHMETICAL_OR);
				rightExpression = parseArithmeticalXorExpression(inFlag);
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, Token.OPERATOR_ARITHMETICAL_OR);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>^</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseArithmeticalXorExpression(
			boolean inFlag) throws ParserException {
		// 首先调用上一优先级运算符('&')的解析
		AbstractExpression leftExpression = parseArithmeticalAndExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'^'的解析
		while (true) {
			if (syntax == Token.OPERATOR_ARITHMETICAL_XOR) {
				nextSyntax(Token.OPERATOR_ARITHMETICAL_XOR);
				rightExpression = parseArithmeticalAndExpression(inFlag);
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, Token.OPERATOR_ARITHMETICAL_XOR);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>&</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseArithmeticalAndExpression(
			boolean inFlag) throws ParserException {
		// 首先调用上一优先级运算符('==')的解析
		AbstractExpression leftExpression = parseEqualityExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'&'的解析
		while (true) {
			if (syntax == Token.OPERATOR_ARITHMETICAL_AND) {
				nextSyntax(Token.OPERATOR_ARITHMETICAL_AND);
				rightExpression = parseEqualityExpression(inFlag);
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, Token.OPERATOR_ARITHMETICAL_AND);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>== != === !==</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseEqualityExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符('==')的解析
		AbstractExpression leftExpression = parseRelationalExpression(inFlag);
		AbstractExpression rightExpression = null;

		// 然后调用'==' '!=' '===' '!=='的解析
		while (true) {
			if (Token.OPERATOR_EQUAL == syntax
					|| Token.OPERATOR_NOT_EQUAL == syntax
					|| Token.OPERATOR_STRICT_EQUAL == syntax
					|| Token.OPERATOR_NOT_STRICT_EQUAL == syntax) {
				Token op = syntax;
				nextSyntax();
				rightExpression = parseRelationalExpression(inFlag);
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, op);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>< <= > >= instanceof</code></b>运算符
	 * 
	 * @param inFlag
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseRelationalExpression(boolean inFlag)
			throws ParserException {
		// 首先调用上一优先级运算符(以为运算符)的解析
		AbstractExpression leftExpression = parseShiftExpression();
		AbstractExpression rightExpression = null;

		// 然后调用'<' '<=' '>' '>=' 'instanceof'的解析
		while (true) {
			if (Token.OPERATOR_LT == syntax || Token.OPERATOR_GT == syntax
					|| Token.OPERATOR_LTE == syntax
					|| Token.OPERATOR_GTE == syntax
					|| Token.KEYWORD_INSTANCEOF == syntax
					|| (Token.KEYWORD_IN == syntax && inFlag)) {
				Token op = syntax;
				nextSyntax();
				rightExpression = parseShiftExpression();
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, op);
				op = null;
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code><< >> >>></code></b>运算符
	 * 
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseShiftExpression()
			throws ParserException {
		// 首先调用上一优先级运算符(加减运算)的解析
		AbstractExpression leftExpression = parseAdditionExpression();
		AbstractExpression rightExpression = null;

		// 然后调用'<<' '>>' '>>>'的解析
		while (true) {
			if (Token.OPERATOR_SHL == syntax || Token.OPERATOR_SHR == syntax
					|| Token.OPERATOR_ASR == syntax) {
				Token op = syntax;
				nextSyntax();
				rightExpression = parseAdditionExpression();
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, op);
				op = null;
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>+ -</code></b>运算符
	 * 
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseAdditionExpression()
			throws ParserException {
		// 首先调用上一优先级运算符(乘除运算)的解析
		AbstractExpression leftExpression = parseMultiplyExpression();
		AbstractExpression rightExpression = null;

		// 然后调用 '+' '-' 的解析
		while (true) {
			if (Token.OPERATOR_PLUS == syntax) {
				nextSyntax(Token.OPERATOR_PLUS);
				rightExpression = parseMultiplyExpression();
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, Token.OPERATOR_PLUS);
			} else if (Token.OPERATOR_MINUS == syntax) {
				nextSyntax(Token.OPERATOR_MINUS);
				rightExpression = parseMultiplyExpression();
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, Token.OPERATOR_MINUS);
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>* / %</code></b>运算符
	 * 
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseMultiplyExpression()
			throws ParserException {
		// 首先调用上一优先级运算符(一元运算)的解析
		AbstractExpression leftExpression = parseUnaryExpression();
		AbstractExpression rightExpression = null;

		// 然后调用 '*' '/' '%' 的解析
		while (true) {
			if (Token.OPERATOR_MUL == syntax || Token.OPERATOR_DIVIDE == syntax
					|| Token.OPERATOR_MOD == syntax) {
				Token op = syntax;
				nextSyntax();
				rightExpression = parseUnaryExpression();
				leftExpression = new BinaryOperatorExpression(leftExpression,
						rightExpression, op);
				op = null;
			} else {
				return leftExpression;
			}
		}
	}

	/**
	 * 解析<b><code>++ -- - ~ ! delete new typeof void</code></b>等一元运算符
	 * 
	 * @return
	 * @throws ParserException
	 * @see {@link #parseExpression(boolean)}
	 */
	private final AbstractExpression parseUnaryExpression()
			throws ParserException {
		// TODO parse '-' numeric literal directly into literals,
		// to ensure that -0 keeps its proper value.

		// unary expressions are right associative
		if (Token.OPERATOR_INCREASE == syntax) {
			nextSyntax(Token.OPERATOR_INCREASE);
			return new IncrementExpression(parseUnaryExpression(), 1, false);
		} else if (Token.OPERATOR_DECREASE == syntax) {
			nextSyntax(Token.OPERATOR_DECREASE);
			return new IncrementExpression(parseUnaryExpression(), -1, false);
		} else if (Token.KEYWORD_DELETE == syntax) {
			nextSyntax(Token.KEYWORD_DELETE);
			return new DeleteExpression(parseUnaryExpression());
		} else if (Token.OPERATOR_PLUS == syntax
				|| Token.OPERATOR_MINUS == syntax
				|| Token.OPERATOR_ARITHMETICAL_NOT == syntax
				|| Token.OPERATOR_LOGICAL_NOT == syntax
				|| Token.KEYWORD_VOID == syntax
				|| Token.KEYWORD_TYPEOF == syntax) {
			Token op = syntax;
			nextSyntax();
			UnaryOperatorExpression result = new UnaryOperatorExpression(
					parseUnaryExpression(), op);
			op = null;
			return result;
		} else {
			return parsePostfixExpression();
		}
	}

	private AbstractExpression parsePostfixExpression() throws ParserException {
		// TODO this can be merged with parseUnary().

		AbstractExpression expression = parseMemberExpression(false);

		// postfix expressions aren't associative
		if (Token.OPERATOR_INCREASE == syntax) {
			nextSyntax(Token.OPERATOR_INCREASE);
			return new IncrementExpression(expression, 1, true);
		} else if (Token.OPERATOR_DECREASE == syntax) {
			nextSyntax(Token.OPERATOR_DECREASE);
			return new IncrementExpression(expression, -1, true);
		} else {
			return expression;
		}
	}

	/**
	 * The grammar for the 'new' keyword is a little complicated. The keyword
	 * 'new' can occur in either a NewExpression (where its not followed by an
	 * argument list) or in MemberExpresison (where it is followed by an
	 * argument list). The intention seems to be that an argument list should
	 * bind to any unmatched 'new' keyword to the left in the same expression if
	 * possible, otherwise an argument list is taken as a call expression.
	 * 
	 * Since the rest of the productions for NewExpressions and CallExpressions
	 * are similar we roll these two into one routine with a parameter to
	 * indicate whether we're currently parsing a 'new' expression or not.
	 * 
	 * @param newFlag
	 *            标识当前是否正在解析'new'语句
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 43页 11.2.MemberExpression
	 */
	private final AbstractExpression parseMemberExpression(boolean newFlag)
			throws ParserException {
		AbstractExpression expression;

		if (syntax == Token.KEYWORD_NEW) {
			expression = parseNewExpression();
		} else if (syntax == Token.KEYWORD_FUNCTION) {
			expression = parseFunctionDefinition(false);
		} else {
			expression = parsePrimaryExpression();
		}

		// 处理括号
		while (true) {
			if (!newFlag && syntax == Token.OPERATOR_OPENPAREN) {
				AbstractExpression[] arguments = parseArgumentList();
				expression = new CallFunctionExpression(expression, arguments);
			} else if (syntax == Token.OPERATOR_OPENSQUARE) {
				nextSyntax(Token.OPERATOR_OPENSQUARE);
				AbstractExpression property = parseExpression(true);
				nextSyntax(Token.OPERATOR_CLOSESQUARE);
				expression = new PropertyExpression(expression, property);
			} else if (syntax == Token.OPERATOR_DOT) {
				// 将 x.bar转换成x["bar"]的形式
				nextSyntax(Token.OPERATOR_DOT);
				IdentifierLiteral identifier = parseIdentifier();
				expression = new PropertyExpression(expression,
						new StringLiteral(identifier.string));
			} else {
				return expression;
			}
		}
	}

	/**
	 * @return
	 * @throws ParserException
	 */
	private final NewExpression parseNewExpression() throws ParserException {
		AbstractExpression objName = null;
		nextSyntax(Token.KEYWORD_NEW);
		objName = parseMemberExpression(true);
		AbstractExpression[] arguments = null;
		if (syntax == Token.OPERATOR_OPENPAREN) {
			arguments = parseArgumentList();
		}
		return new NewExpression(objName, arguments);
	}

	/**
	 * 解析参数列表
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 45页 11.2.4.Argument Lists
	 */
	private final AbstractExpression[] parseArgumentList()
			throws ParserException {
		ArrayList argumentList = new ArrayList();

		nextSyntax(Token.OPERATOR_OPENPAREN);

		if (syntax != Token.OPERATOR_CLOSEPAREN) {
			argumentList.add(parseAssignmentExpression(true));
			while (syntax == Token.OPERATOR_COMMA) {
				nextSyntax(Token.OPERATOR_COMMA);
				argumentList.add(parseAssignmentExpression(true));
			}
		}
		nextSyntax(Token.OPERATOR_CLOSEPAREN);
		return ListUtil.list2ExpressionArray(argumentList);
	}

	/**
	 * 解析基本表达式语法
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 40页~43页 11.1.Primary Expressions
	 */
	private final AbstractExpression parsePrimaryExpression()
			throws ParserException {
		if (Token.KEYWORD_THIS == syntax) {
			nextSyntax(Token.KEYWORD_THIS);
			return new ThisLiteral();
		} else if (Token.KEYWORD_NULL == syntax) {
			nextSyntax(Token.KEYWORD_NULL);
			return new NullLiteral();
		} else if (Token.KEYWORD_TRUE == syntax) {
			nextSyntax(Token.KEYWORD_TRUE);
			return new BooleanLiteral(true);
		} else if (Token.KEYWORD_FALSE == syntax) {
			nextSyntax(Token.KEYWORD_FALSE);
			return new BooleanLiteral(false);
		} else if (Token.OPERATOR_OPENPAREN == syntax) {
			nextSyntax(Token.OPERATOR_OPENPAREN);
			AbstractExpression expression = parseExpression(true);
			nextSyntax(Token.OPERATOR_CLOSEPAREN);
			return expression;
		} else if (Token.OPERATOR_OPENBRACE == syntax) {
			return parseObjectLiteral();
		} else if (Token.OPERATOR_OPENSQUARE == syntax) {
			return parseArrayLiteral();
		} else if (syntax.isIdentifier()) {
			return parseIdentifier();
		} else if (syntax.isStringLiteral()) {
			return parseStringLiteral();
		} else if (syntax.isNumericLiteral()) {
			return parseNumericLiteral();
		} else {
			throwParserException("非法的表达式语法: " + syntax);
		}
		return null;
	}

	/**
	 * 解析数组
	 * 
	 * @return
	 * @throws ParserException
	 */
	private final ArrayLiteral parseArrayLiteral() throws ParserException {
		ArrayList arrayElements = new ArrayList();

		nextSyntax(Token.OPERATOR_OPENSQUARE);

		while (syntax != Token.OPERATOR_CLOSESQUARE) {
			if (syntax == Token.OPERATOR_COMMA) {
				arrayElements.add(null);
			} else {
				arrayElements.add(parseAssignmentExpression(true));
			}

			if (syntax != Token.OPERATOR_CLOSESQUARE) {
				nextSyntax(Token.OPERATOR_COMMA);
			}
		}

		nextSyntax(Token.OPERATOR_CLOSESQUARE);

		return new ArrayLiteral(ListUtil.list2ExpressionArray(arrayElements));
	}

	/**
	 * 解析函数定义语句语法
	 * 
	 * @param nameFlag
	 *            标识是否需要读取函数名
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 71页 13.Function Definition
	 */
	private final FunctionLiteral parseFunctionDefinition(boolean nameFlag)
			throws ParserException {
		IdentifierLiteral funcName = null;// 函数名
		ArrayList parameters = new ArrayList();// 参数列表
		ArrayList statements = new ArrayList();// 函数体语句

		nextSyntax(Token.KEYWORD_FUNCTION);
		// 读取函数名
		if (nameFlag || Token.OPERATOR_OPENPAREN != syntax) {
			funcName = parseIdentifier();
		}

		// 读取函数的参数
		nextSyntax(Token.OPERATOR_OPENPAREN);// 函数名后应跟'('
		if (syntax != Token.OPERATOR_CLOSEPAREN) {
			parameters.add(parseIdentifier());
			while (syntax != Token.OPERATOR_CLOSEPAREN) {
				nextSyntax(Token.OPERATOR_COMMA);// 参数之间应该用','间隔
				parameters.add(parseIdentifier());
			}
		}
		nextSyntax(Token.OPERATOR_CLOSEPAREN);// ')'关闭参数定义

		// 读取函数体
		nextSyntax(Token.OPERATOR_OPENBRACE);// 函数体以'{'开始
		while (syntax != Token.OPERATOR_CLOSEBRACE) {// 循环读取'{}'之间的语句
			statements.add(parseSourceElement());
		}
		nextSyntax(Token.OPERATOR_CLOSEBRACE);// 以'}'结束函数体定义

		// 生成函数定义的语法结构
		return new FunctionLiteral(funcName, ListUtil
				.list2IdentifierArray(parameters), ListUtil
				.list2StatementArray(statements));
	}

	/**
	 * 解析对象
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 41页~42页 11.1.5.Object Initialiser
	 */
	private final ObjectLiteral parseObjectLiteral() throws ParserException {
		ArrayList propertyList = new ArrayList();

		nextSyntax(Token.OPERATOR_OPENBRACE);

		while (syntax != Token.OPERATOR_CLOSEBRACE) {
			propertyList.add(parseObjectLiteralProperty());
			while (syntax == Token.OPERATOR_COMMA) {
				nextSyntax(Token.OPERATOR_COMMA);
				propertyList.add(parseObjectLiteralProperty());
			}
		}

		nextSyntax(Token.OPERATOR_CLOSEBRACE);

		ObjectPropertyLiteral[] propertyArray = new ObjectPropertyLiteral[propertyList
				.size()];
		propertyList.toArray(propertyArray);

		return new ObjectLiteral(propertyArray);
	}

	/**
	 * 解析对象属性
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 41页~42页 11.1.5.Object Initialiser
	 */
	private final ObjectPropertyLiteral parseObjectLiteralProperty()
			throws ParserException {
		AbstractExpression propertyName = null;
		AbstractExpression propertyValue = null;
		// 读取参数名
		if (syntax.isIdentifier()) {
			propertyName = new StringLiteral(parseIdentifier().string);
		} else if (syntax.isStringLiteral()) {
			propertyName = parseStringLiteral();
		} else if (syntax.isNumericLiteral()) {
			propertyName = parseNumericLiteral();
		} else {
			throwParserException("非法参数名");
		}
		// 读取参数值
		nextSyntax(Token.OPERATOR_COLON);
		propertyValue = parseAssignmentExpression(true);

		return new ObjectPropertyLiteral(propertyName, propertyValue);
	}

	/**
	 * 解析标识符语法
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 14页~15页 7.6.Identifiers
	 */
	private final IdentifierLiteral parseIdentifier() throws ParserException {
		IdentifierLiteral identifier = null;
		if (syntax.getTokenType() == Token.TYPE_IDENTIFIER) {
			identifier = new IdentifierLiteral(syntax.getAttributeValue());
		} else {
			throwParserException("非法标识符语法");
		}
		nextSyntax();
		return identifier;
	}

	/**
	 * 解析字符串
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 18页 7.8.4.String Literals
	 */
	private final StringLiteral parseStringLiteral() throws ParserException {
		String string = null;
		if (syntax.getTokenType() == Token.TYPE_STRING) {
			string = syntax.getAttributeValue();
		} else {
			throwParserException("非法字符串格式");
		}
		nextSyntax();
		return new StringLiteral(string);
	}

	/**
	 * 解析数字
	 * 
	 * @return
	 * @throws ParserException
	 * @see ECMA-262 16页~18页 7.8.3.Numeric Literals
	 */
	private final NumberLiteral parseNumericLiteral() throws ParserException {
		double value = 0.0;
		try {
			switch (syntax.getTokenType()) {
			case Token.TYPE_FLOAT:
				value = Double.parseDouble(syntax.getAttributeValue());
				break;
			case Token.TYPE_DECIMAL:
				value = Long.parseLong(syntax.getAttributeValue());
				break;
			case Token.TYPE_OCTAL:
				value = Long.parseLong(syntax.getAttributeValue().substring(1),
						8);
				break;
			case Token.TYPE_HEXAL:
				value = Long.parseLong(syntax.getAttributeValue().substring(2),
						16);
				break;
			default:
				throwParserException("非法数字格式");
			}
		} catch (NumberFormatException e) {
			value = Double.NaN;
		}
		nextSyntax();
		return new NumberLiteral(value);
	}

	/**
	 * 获取当前解析语句的行号
	 * 
	 * @return 当前解析语句的行号
	 */
	private final int getLineNumber() {
		return lexer.getLineNumber();
	}

	/**
	 * 获取下一语法单元(忽略空白字符)
	 * 
	 * @throws ParserException
	 */
	private final void nextSyntax() throws ParserException {
		isNewline = false;
		do {
			syntax = lexer.nextToken();
			isNewline = (syntax.isEOF() || syntax.isNewLine());
		} while (syntax.isWhitespace());
	}

	/**
	 * 当当前语法单元与目标语法单元相同时，获取下一语法单元
	 * 
	 * @param targetSyntax
	 *            目标语法单元
	 * @throws ParserException
	 */
	private final void nextSyntax(Token targetSyntax) throws ParserException {
		if (syntax == targetSyntax) {
			nextSyntax();
		} else {
			throwParserException("此处应当是 '" + targetSyntax.getAttributeValue()
					+ "'");
		}
	}

	/**
	 * 本方法用于校验语句是否正常终结<br/>
	 * 如果当前词法单元为';'或者行终结符或者'}'，则认为是一条语句终结。
	 * 
	 * @throws ParserException
	 */
	private final void readTokenSemicolon() throws ParserException {
		if (syntax == Token.OPERATOR_SEMICOLON) {
			nextSyntax();
		} else if (syntax == Token.OPERATOR_CLOSEBRACE) {
			// semicolon insertion
		} else if (isNewline) {
			// semicolon insertion
		} else {
			throwParserException("expected '"
					+ Token.OPERATOR_SEMICOLON.getAttributeValue() + "'");
		}
	}

	/**
	 * 抛出解析异常
	 * 
	 * @param message
	 *            异常信息
	 * @throws ParserException
	 */
	private final void throwParserException(String message)
			throws ParserException {
		throw new ParserException("Parser Exeption: " + message,
				getLineNumber());
	}
}
