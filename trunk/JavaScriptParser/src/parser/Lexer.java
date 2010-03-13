package parser;

import java.util.Hashtable;

/**
 * <code>Lexer</code> 是一个简单的词法分析器
 * <p>
 * 词法分析器读入组成源程序的字符流, 将它们组织成有意义的词素序列. 对于每个词素, 词法分析器生成相应的词法单元({@link Token})作为输出.
 * 然后交由上层语法分析器({@link Parser})处理。
 * 
 * @author Jarod Yv
 */
public final class Lexer {
	
	private static final int BASE_OCT = 8;
	private static final int BASE_HEX = 16;

	// //////////////////// 一下常量用于标识数字解析过程中状态机的状态 ////////////////////
	private static final int SYNTAX_NUMERIC_ENTRY_POINT = 0;
	private static final int SYNTAX_NUMERIC_LEADING_ZERO = 1;
	private static final int SYNTAX_NUMERIC_LEADING_DECIMAL = 2;
	private static final int SYNTAX_NUMERIC_OCTAL_LITERAL = 3;
	private static final int SYNTAX_NUMERIC_LEADING_OX = 4;
	private static final int SYNTAX_NUMERIC_HEXADECIMAL_LITERAL = 5;
	private static final int SYNTAX_NUMERIC_DECIMAL_LITERAL = 6;
	private static final int SYNTAX_NUMERIC_DECIMAL_POINT = 7;
	private static final int SYNTAX_NUMERIC_FRACTIONAL_PART = 8;
	private static final int SYNTAX_NUMERIC_EXPONENT_SYMBOL = 9;
	private static final int SYNTAX_NUMERIC_EXPONENT_SIGN = 10;
	private static final int SYNTAX_NUMERIC_EXPONENT_PART = 11;
	private static final int SYNTAX_NUMERIC_UNREAD_TWO = 12;
	private static final int SYNTAX_NUMERIC_UNREAD_ONE = 13;
	private static final int SYNTAX_NUMERIC_RETURN_FLOAT = 14;
	private static final int SYNTAX_NUMERIC_RETURN_DECIMAL = 15;
	private static final int SYNTAX_NUMERIC_RETURN_OCTAL = 16;
	private static final int SYNTAX_NUMERIC_RETURN_HEXADECIMAL = 17;
	private static final int SYNTAX_NUMERIC_RETURN_OPERATOR_DOT = 18;
	// /////////////////////////////////////////////////////////////////////////////////

	// 关键字表
	private Hashtable keywords = null;
	
	// 待分析的脚本
	private String script = null;
	
	// 行号(用于debug)
	private int lineNumber = 1;
	
	// 脚本最大字符数
	private int maxPosition;
	
	// 当前字符位置
	private int curPosition;
	
	// 上一字符位置
	private int oldPosition;
	
	// 当前字符
	private int ch;
	
	// Lexer的唯一实例
	private static Lexer instance = null;

	/**
	 * 获取{@link Lexer}的唯一实例
	 * 
	 * @return {@link Lexer}的唯一实例
	 */
	public static Lexer getInstace() {
		if (instance == null)
			instance = new Lexer();
		return instance;
	}

	/**
	 * 私有构造函数。主要用于初始化关键字表。
	 */
	private Lexer() {
		initKeywords();
	}

	/**
	 * 载入待解析的脚本内容
	 * 
	 * @param script
	 *            待解析的脚本内容
	 */
	public void setScript(String script) {
		if (script == null) {
			throw new IllegalArgumentException("脚本内容为空");
		}
		this.script = script;
		maxPosition = script.length();
		oldPosition = 0;
		curPosition = 0;
		ch = curPosition < maxPosition ? script.charAt(curPosition) : -1;
	}

	/**
	 * 向关键字表中加入关键字
	 * 
	 * @param keyword
	 *            关键字语法对象
	 */
	private void addKeyword(Token keyword) {
		keywords.put(keyword.getAttributeValue(), keyword);
	}

	/**
	 * 获取当前行号
	 * 
	 * @return {@link #lineNumber}
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * 初始化关键字表
	 * 
	 * @see ECMA-262 26页 7.5.2 Keywords和7.5.3 Future Reserved Words
	 */
	private void initKeywords() {
		keywords = new Hashtable(59);

		// 关键字
		addKeyword(Token.KEYWORD_BREAK);
		addKeyword(Token.KEYWORD_CASE);
		addKeyword(Token.KEYWORD_CATCH);
		addKeyword(Token.KEYWORD_CONTINUE);
		addKeyword(Token.KEYWORD_DEFAULT);
		addKeyword(Token.KEYWORD_DELETE);
		addKeyword(Token.KEYWORD_DO);
		addKeyword(Token.KEYWORD_ELSE);
		addKeyword(Token.KEYWORD_FALSE);
		addKeyword(Token.KEYWORD_FINALLY);
		addKeyword(Token.KEYWORD_FOR);
		addKeyword(Token.KEYWORD_FUNCTION);
		addKeyword(Token.KEYWORD_IF);
		addKeyword(Token.KEYWORD_IN);
		addKeyword(Token.KEYWORD_INSTANCEOF);
		addKeyword(Token.KEYWORD_NEW);
		addKeyword(Token.KEYWORD_NULL);
		addKeyword(Token.KEYWORD_RETURN);
		addKeyword(Token.KEYWORD_SWITCH);
		addKeyword(Token.KEYWORD_THIS);
		addKeyword(Token.KEYWORD_THROW);
		addKeyword(Token.KEYWORD_TRUE);
		addKeyword(Token.KEYWORD_TRY);
		addKeyword(Token.KEYWORD_TYPEOF);
		addKeyword(Token.KEYWORD_VAR);
		addKeyword(Token.KEYWORD_VOID);
		addKeyword(Token.KEYWORD_WHILE);
		addKeyword(Token.KEYWORD_WITH);

		// 保留关键字
		addKeyword(Token.KEYWORD_ABSTRACT);
		addKeyword(Token.KEYWORD_BOOLEAN);
		addKeyword(Token.KEYWORD_BYTE);
		addKeyword(Token.KEYWORD_CHAR);
		addKeyword(Token.KEYWORD_CLASS);
		addKeyword(Token.KEYWORD_CONST);
		addKeyword(Token.KEYWORD_DEBUGGER);
		addKeyword(Token.KEYWORD_DOUBLE);
		addKeyword(Token.KEYWORD_ENUM);
		addKeyword(Token.KEYWORD_EXPORT);
		addKeyword(Token.KEYWORD_EXTENDS);
		addKeyword(Token.KEYWORD_FINAL);
		addKeyword(Token.KEYWORD_FLOAT);
		addKeyword(Token.KEYWORD_GOTO);
		addKeyword(Token.KEYWORD_IMPLEMENTS);
		addKeyword(Token.KEYWORD_IMPORT);
		addKeyword(Token.KEYWORD_INT);
		addKeyword(Token.KEYWORD_INTERFACE);
		addKeyword(Token.KEYWORD_LONG);
		addKeyword(Token.KEYWORD_NATIVE);
		addKeyword(Token.KEYWORD_PACKAGE);
		addKeyword(Token.KEYWORD_PRIVATE);
		addKeyword(Token.KEYWORD_PROTECTED);
		addKeyword(Token.KEYWORD_PUBLIC);
		addKeyword(Token.KEYWORD_SHORT);
		addKeyword(Token.KEYWORD_STATIC);
		addKeyword(Token.KEYWORD_SUPER);
		addKeyword(Token.KEYWORD_SYNCHRONIZED);
		addKeyword(Token.KEYWORD_THROWS);
		addKeyword(Token.KEYWORD_TRANSIENT);
		addKeyword(Token.KEYWORD_VOLATILE);
	}

	/**
	 * 判断是否已经达到脚本的末尾
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 达到脚本的末尾
	 *         <li><code>false</code> - 未达到脚本的末尾
	 *         </ul>
	 */
	private boolean isEOF() {
		return ch == -1;
	}

	/**
	 * 判断是否是换行符<br/>
	 * <strong>"\r\n\u2029\u2029"</strong>被认为是换行符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是换行符
	 *         <li><code>false</code> - 不是换行符
	 *         </ul>
	 * @see ECMA-262 24页 7.3 Line Terminators
	 */
	private boolean isLineTerminator() {
		return ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
	}

	/**
	 * 判断是否是空白字符<br/>
	 * <strong>"\u0009\u000B\u000C\u0020\u00A0"</strong>被认为是空白字符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是空白字符
	 *         <li><code>false</code> - 不是空白字符
	 *         </ul>
	 * @see ECMA-262 23页 7.2 White Space
	 */
	private boolean isWhitespace() {
		return ch == '\u0009' || ch == '\u000B' || ch == '\u000C'
				|| ch == '\u0020' || ch == '\u00A0';
	}

	/**
	 * 判断是否是8进制数字
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是8进制数字
	 *         <li><code>false</code> - 不是8进制数字
	 *         </ul>
	 */
	private boolean isOctalDigit() {
		return (ch >= '0' && ch <= '7');
	}

	/**
	 * 判断是否是10进制数字
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是10进制数字
	 *         <li><code>false</code> - 不是10进制数字
	 *         </ul>
	 */
	private boolean isDecimalDigit() {
		return (ch >= '0' && ch <= '9');
	}

	/**
	 * 判断是否是16进制数字
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是16进制数字
	 *         <li><code>false</code> - 不是16进制数字
	 *         </ul>
	 */
	private boolean isHexalDigit() {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')
				|| (ch >= 'A' && ch <= 'F');
	}

	/**
	 * 判断是否是合法的标识符首字母
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是合法的标识符首字母
	 *         <li><code>false</code> - 不是合法的标识符首字母
	 *         </ul>
	 */
	private boolean isIdentifierStart() {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
				|| ch == '$' || ch == '_';
	}

	/**
	 * 判断是否是合法的标识符字母
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是合法的标识符字母
	 *         <li><code>false</code> - 不是合法的标识符字母
	 *         </ul>
	 * @see ECMA-262 26页 7.6 Identifiers
	 */
	private boolean isIdentifierPart() {
		return isIdentifierStart() || isDecimalDigit();
	}

	/**
	 * 读取下一个字符
	 */
	private void nextChar() {
		curPosition++;
		if (curPosition < maxPosition) {
			ch = script.charAt(curPosition);
		} else {
			ch = -1;
		}
	}

	/**
	 * 返回上一个字符
	 * <p>
	 * <em>注:如果遇到行终结符，则不能返回上一行的字符</em>
	 * 
	 * @exception ParserException
	 */
	private void prevChar() throws ParserException {
		if (curPosition > 0) {
			if (isLineTerminator()) {
				throwLexerException("不能返回上一条语句的字符");
			}
			ch = script.charAt(--curPosition);
		} else {
			ch = -1;
		}
	}

	/**
	 * 处理标识符中的转移字符
	 * 
	 * @exception ParserException
	 */
	private void handleIdentifierEscape() throws ParserException {
		nextChar();// 读取下一字符
		if (isEOF()) {
			throwLexerException("处理标识符中的转移字符时遇到EOF");
		} else if (isLineTerminator()) {
			throwLexerException("处理标识符中的转移字符时遇到换行符");
		} else if (ch == 'u') {
			nextChar();
			// u后面只能跟4位16进制数字
			if (isHexalDigit()) {
				readHexEscapeSequence(4);
			} else {
				throwLexerException("处理标识符中的转移字符时遇到无效转移字符");
			}
		} else {
			throwLexerException("处理标识符中的转移字符时遇到无效转移字符");
		}
	}

	/**
	 * 处理字符串中的转移字符
	 * 
	 * @exception ParserException
	 */
	private void handleStringEscape() throws ParserException {
		nextChar();
		if (isEOF()) {
			throwLexerException("处理字符串中的转移字符时遇到EOF");
		} else if (isLineTerminator()) {
			throwLexerException("处理字符串中的转移字符时遇到换行符");
		} else if (ch == 'b') {
			ch = 0x0008;
		} else if (ch == 't') {
			ch = 0x0009;
		} else if (ch == 'n') {
			ch = 0x000a;
		} else if (ch == 'v') {
			ch = 0x000b;
		} else if (ch == 'f') {
			ch = 0x000c;
		} else if (ch == 'r') {
			ch = 0x000d;
		} else if (ch == 'u') {
			nextChar();
			if (isHexalDigit()) {// 如果是16进制数字的话，则认为是转义
				readHexEscapeSequence(4);
			} else {// 否则按一般字符处理
				prevChar();
			}
		} else if (ch == 'x') {
			nextChar();
			if (isHexalDigit()) {
				readHexEscapeSequence(2);
			} else {
				prevChar();
			}
		} else if (isOctalDigit()) {
			readOctEscapeSequence();
		} else {
			// 其他字符就是字符本身
		}
	}

	/**
	 * 读取16进制转移字符串
	 * 
	 * @param count
	 *            字符个数
	 * @throws ParserException
	 */
	private void readHexEscapeSequence(int count) throws ParserException {
		int value = Character.digit((char) ch, BASE_HEX);
		while (--count > 0) {
			nextChar();
			if (isHexalDigit()) {
				value = (value << 4) + Character.digit((char) ch, BASE_HEX);
			} else {
				throwLexerException("非法的转移字符格式");
			}
		}
		ch = value;
	}

	/**
	 * 读取8进制转移字符串
	 * 
	 * @throws ParserException
	 */
	private void readOctEscapeSequence() throws ParserException {
		int value = Character.digit((char) ch, BASE_OCT);
		nextChar();
		if (isOctalDigit()) {
			value = (value << 3) + Character.digit((char) ch, BASE_OCT);
			nextChar();
			if (isOctalDigit()) {
				value = (value << 3) + Character.digit((char) ch, BASE_OCT);
			} else {
				prevChar();
			}
		} else {
			prevChar();
		}
		ch = value;
	}

	/**
	 * Windows格式的脚本往往使用"\r\n"作为换行符。本方法用于将"\r\n"处理成一个换行符。
	 */
	private void skipLineTerminator() {
		if (ch == '\r') {
			nextChar();
			if (ch == '\n') {
				nextChar();
			}
		} else {
			nextChar();
		}
		lineNumber++;
	}

	/**
	 * 获取下一个语法单元
	 * 
	 * @return {@link Token}
	 * @throws ParserException
	 */
	public Token nextToken() throws ParserException {
		oldPosition = curPosition;

		if (isIdentifierStart()) {
			return syntaxIdentifier();
		} else if (ch == '.') {
			return syntaxNumeric();
		} else if (isDecimalDigit()) {
			return syntaxNumeric();
		} else if (ch == '\'' || ch == '\"') {
			return syntaxString();
		} else if (isLineTerminator()) {
			return syntaxLineTerminator();
		} else if (isWhitespace()) {
			return syntaxWhitespace();
		} else if (ch == '/') {
			return syntaxSlash();
		} else if (isEOF()) {
			return Token.EOF;
		} else {
			return syntaxOperator();
		}
	}

	/**
	 * 返回换行语法单元
	 */
	private Token syntaxLineTerminator() {
		do {
			skipLineTerminator();
		} while (isLineTerminator());
		return Token.NEWLINE;
	}

	/**
	 * 返回空白字符语法单元
	 */
	private Token syntaxWhitespace() {
		do {
			nextChar();
		} while (isWhitespace());
		return Token.WHITESPACE;
	}

	/**
	 * 处理'/'
	 * <p>
	 * 根据'/'后跟的不同字符，可以分为4种情况：
	 * <ul>
	 * <li><code>'//'</code> - 单行注释
	 * <li><code>'/*'</code> - 多行注释
	 * <li><code>'/='</code> - 相除后赋值
	 * <li><code>'/ '</code> - 除法运算
	 * </ul>
	 * 
	 * @return 语法单元
	 */
	private Token syntaxSlash() {
		nextChar();
		if (ch == '/') {
			return syntaxSingleLineComment();
		} else if (ch == '*') {
			return syntaxMultiLineComment();
		} else if (ch == '=') {
			nextChar();
			return Token.OPERATOR_DIVIDE_ASSIGN;
		} else {
			return Token.OPERATOR_DIVIDE;
		}
	}

	/**
	 * 生成单行注释语法单元
	 * 
	 * @return 语法单元
	 */
	private Token syntaxSingleLineComment() {
		do {
			nextChar();
		} while (!isEOF() && !isLineTerminator());
		return Token.SINGLELINECOMMENT;
	}

	/**
	 * 生成多行注释语法单元
	 * 
	 * @return 语法单元
	 */
	private Token syntaxMultiLineComment() {
		boolean isMultiLine = false;// 标识是否是多行注释
		nextChar();
		while (true) {
			if (isEOF()) {
				break;
			} else if (isLineTerminator()) {
				skipLineTerminator();
				isMultiLine = true;
			} else if (ch == '*') {
				nextChar();
				if (ch == '/') {
					nextChar();
					break;
				}
			} else {
				nextChar();
			}
		}

		if (isMultiLine) {
			return Token.MULTILINECOMMENT;
		} else {
			return Token.SINGLELINECOMMENT;
		}
	}

	/**
	 * 生成数字语法单元
	 * <p>
	 * <em>此处对数字的词法分析比较丑陋，但却是采用有限状态机思想实现的，希望能够找到一种更精简清晰的实现方式。</em>
	 * 
	 * @return 数字语法单元
	 * @throws ParserException
	 * @see ECMA-262 28页 7.8.3 NumericLiterals
	 */
	private Token syntaxNumeric() throws ParserException {
		int state = SYNTAX_NUMERIC_ENTRY_POINT;
		while (true) {
			switch (state) {
			// 处理入口
			case SYNTAX_NUMERIC_ENTRY_POINT:
				if (ch == '.') {
					state = SYNTAX_NUMERIC_LEADING_ZERO;
				} else if (ch == '0') {
					state = SYNTAX_NUMERIC_LEADING_DECIMAL;
				} else {
					state = SYNTAX_NUMERIC_DECIMAL_LITERAL;
				}
				break;
			// 以小数点开头
			case SYNTAX_NUMERIC_LEADING_ZERO:
				nextChar();
				if (isDecimalDigit()) {
					state = SYNTAX_NUMERIC_DECIMAL_POINT;
				} else {
					state = SYNTAX_NUMERIC_RETURN_OPERATOR_DOT;
				}
				break;
			// 以0开头
			case SYNTAX_NUMERIC_LEADING_DECIMAL:
				nextChar();
				if (isOctalDigit()) {
					state = SYNTAX_NUMERIC_OCTAL_LITERAL;
				} else if (ch == 'x' || ch == 'X') {
					state = SYNTAX_NUMERIC_LEADING_OX;
				} else if (isDecimalDigit()) {
					state = SYNTAX_NUMERIC_DECIMAL_LITERAL;
				} else if (ch == '.') {
					state = SYNTAX_NUMERIC_DECIMAL_POINT;
				} else if (ch == 'e' || ch == 'E') {
					state = SYNTAX_NUMERIC_EXPONENT_SYMBOL;
				} else {
					state = SYNTAX_NUMERIC_RETURN_DECIMAL;
				}
				break;
			// 以 '0x' 或 '0X'开头
			case SYNTAX_NUMERIC_LEADING_OX:
				nextChar();
				if (isHexalDigit()) {
					state = SYNTAX_NUMERIC_HEXADECIMAL_LITERAL;
				} else {
					throwLexerException("非法的十六进制数格式");
				}
				break;
			// 8进制数
			case SYNTAX_NUMERIC_OCTAL_LITERAL:
				nextChar();
				if (isOctalDigit()) {
					// 继续读取后面的数字
				} else {
					state = SYNTAX_NUMERIC_RETURN_OCTAL;
				}
				break;
			// 16进制数
			case SYNTAX_NUMERIC_HEXADECIMAL_LITERAL:
				nextChar();
				if (isHexalDigit()) {
					// 继续读取后面的数字
				} else {
					state = SYNTAX_NUMERIC_RETURN_HEXADECIMAL;
				}
				break;
			// 10进制数
			case SYNTAX_NUMERIC_DECIMAL_LITERAL:
				nextChar();
				if (isDecimalDigit()) {
					// 继续读取后面的数字
				} else if (ch == '.') {
					state = SYNTAX_NUMERIC_DECIMAL_POINT;
				} else if (ch == 'e' || ch == 'E') {
					state = SYNTAX_NUMERIC_EXPONENT_SYMBOL;
				} else {
					state = SYNTAX_NUMERIC_RETURN_DECIMAL;
				}
				break;
			// 10进制小数
			case SYNTAX_NUMERIC_DECIMAL_POINT:
				nextChar();
				if (isDecimalDigit()) {
					state = SYNTAX_NUMERIC_FRACTIONAL_PART;
				} else if (ch == 'e' || ch == 'E') {
					state = SYNTAX_NUMERIC_EXPONENT_SYMBOL;
				} else {
					state = SYNTAX_NUMERIC_RETURN_FLOAT;
				}
				break;
			// 读取小数部分
			case SYNTAX_NUMERIC_FRACTIONAL_PART:
				nextChar();
				if (isDecimalDigit()) {
					// 继续读取后面的数字
				} else if (ch == 'e' || ch == 'E') {
					state = SYNTAX_NUMERIC_EXPONENT_SYMBOL;
				} else {
					state = SYNTAX_NUMERIC_RETURN_FLOAT;
				}
				break;
			// 处理指数
			case SYNTAX_NUMERIC_EXPONENT_SYMBOL:
				nextChar();
				if (ch == '+' || ch == '-') {
					state = SYNTAX_NUMERIC_EXPONENT_SIGN;
				} else if (isDecimalDigit()) {
					state = SYNTAX_NUMERIC_EXPONENT_PART;
				} else {
					state = SYNTAX_NUMERIC_UNREAD_ONE;
				}
				break;
			// 处理指数部分的符号
			case SYNTAX_NUMERIC_EXPONENT_SIGN:
				nextChar();
				if (isDecimalDigit()) {
					state = SYNTAX_NUMERIC_EXPONENT_PART;
				} else {
					state = SYNTAX_NUMERIC_UNREAD_TWO;
				}
				break;
			// 处理指数部分
			case SYNTAX_NUMERIC_EXPONENT_PART:
				nextChar();
				if (isDecimalDigit()) {
					// 继续读取后面的数字
				} else {
					state = SYNTAX_NUMERIC_RETURN_FLOAT;
				}
				break;
			// 忽略指数符号(2个)
			case SYNTAX_NUMERIC_UNREAD_TWO:
				prevChar();
				state = SYNTAX_NUMERIC_UNREAD_ONE;
				break;
			// 忽略指数符号(1个)
			case SYNTAX_NUMERIC_UNREAD_ONE:
				prevChar();
				state = SYNTAX_NUMERIC_RETURN_FLOAT;
				break;
			// 生成浮点数语法单元
			case SYNTAX_NUMERIC_RETURN_FLOAT:
				return new Token(Token.TYPE_FLOAT, script.substring(
						oldPosition, curPosition));
				// 生成10进制数语法单元
			case SYNTAX_NUMERIC_RETURN_DECIMAL:
				return new Token(Token.TYPE_DECIMAL, script.substring(
						oldPosition, curPosition));
				// 生成8进制数语法单元
			case SYNTAX_NUMERIC_RETURN_OCTAL:
				return new Token(Token.TYPE_OCTAL, script.substring(
						oldPosition, curPosition));
				// 生成16进制数语法单元
			case SYNTAX_NUMERIC_RETURN_HEXADECIMAL:
				return new Token(Token.TYPE_HEXAL, script.substring(
						oldPosition, curPosition));
				// 生成'.'运算符语法单元
			case SYNTAX_NUMERIC_RETURN_OPERATOR_DOT:
				return Token.OPERATOR_DOT;
			}
		}
	}

	/**
	 * 生成字符串语法单元
	 * 
	 * @return 语法单元
	 * @throws ParserException
	 */
	private Token syntaxString() throws ParserException {
		StringBuffer buffer = new StringBuffer();
		int quote = ch;// 缓存第一个引号，用于结束时配对
		nextChar();// 跳过第一个引号，读取下一个字符
		while (true) {
			if (ch == quote) {// 读到与开始引号配对的引号，则结束字符串读取
				break;
			} else if (isEOF()) {
				throwLexerException("读取字符串是遇到EOF");
			} else if (isLineTerminator()) {
				throwLexerException("读取字符串是遇到换行符");
			} else if (ch == '\\') {
				handleStringEscape();
				buffer.append((char) ch);
			} else {
				buffer.append((char) ch);
			}
			nextChar();
		}
		nextChar();// 掉过结束的引号
		return new Token(Token.TYPE_STRING, buffer.toString());
	}

	/**
	 * 生成标识符语法单元
	 * 
	 * @return 语法单元
	 * @throws ParserException
	 */
	private Token syntaxIdentifier() throws ParserException {
		StringBuffer buffer = new StringBuffer();
		buffer.append((char) ch);// 写入标识符首字母
		nextChar();
		while (true) {
			if (isIdentifierPart()) {
				buffer.append((char) ch);
			} else if (ch == '\\') {
				handleIdentifierEscape();
				if (isIdentifierPart()) {
					buffer.append((char) ch);
				} else {
					throwLexerException("读取标识符时遇到非法字符");
				}
			} else {
				break;
			}
			nextChar();
		}

		// 判断标识符是否是关键字
		Token token = (Token) keywords.get(buffer.toString());
		if (token != null) {
			return token;
		} else {
			return new Token(Token.TYPE_IDENTIFIER, buffer.toString());
		}
	}

	/**
	 * 生成运算符语法单元
	 * 
	 * @return 运算符语法单元
	 * @throws ParserException
	 */
	private Token syntaxOperator() throws ParserException {
		int oldCh = ch;
		nextChar();
		switch (oldCh) {
		case ';':
			return Token.OPERATOR_SEMICOLON;
		case ',':
			return Token.OPERATOR_COMMA;
		case '(':
			return Token.OPERATOR_OPENPAREN;
		case ')':
			return Token.OPERATOR_CLOSEPAREN;
		case '{':
			return Token.OPERATOR_OPENBRACE;
		case '}':
			return Token.OPERATOR_CLOSEBRACE;
		case '[':
			return Token.OPERATOR_OPENSQUARE;
		case ']':
			return Token.OPERATOR_CLOSESQUARE;
		case '?':
			return Token.OPERATOR_QUESTION;
		case ':':
			return Token.OPERATOR_COLON;
		case '~':
			return Token.OPERATOR_ARITHMETICAL_NOT;
		case '+':
			if (ch == '+') {
				nextChar();
				return Token.OPERATOR_INCREASE;
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_PLUS_ASSIGN;
			} else {
				return Token.OPERATOR_PLUS;
			}
		case '-':
			if (ch == '-') {
				nextChar();
				return Token.OPERATOR_DECREASE;
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_MINUS_ASSIGN;
			} else {
				return Token.OPERATOR_MINUS;
			}
		case '*':
			if (ch == '=') {
				nextChar();
				return Token.OPERATOR_MUL_ASSIGN;
			} else {
				return Token.OPERATOR_MUL;
			}
		case '%':
			if (ch == '=') {
				nextChar();
				return Token.OPERATOR_MOD_ASSIGN;
			} else {
				return Token.OPERATOR_MOD;
			}
		case '=':
			if (ch == '=') {
				nextChar();
				if (ch == '=') {
					nextChar();
					return Token.OPERATOR_STRICT_EQUAL;
				} else {
					return Token.OPERATOR_EQUAL;
				}
			} else {
				return Token.OPERATOR_ASSIGN;
			}
		case '!':
			if (ch == '=') {
				nextChar();
				if (ch == '=') {
					nextChar();
					return Token.OPERATOR_NOT_STRICT_EQUAL;
				} else {
					return Token.OPERATOR_NOT_EQUAL;
				}
			} else {
				return Token.OPERATOR_LOGICAL_NOT;
			}
		case '&':
			if (ch == '&') {
				nextChar();
				return Token.OPERATOR_LOGICAL_AND;
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_ARITHMETICAL_AND_ASSIGN;
			} else {
				return Token.OPERATOR_ARITHMETICAL_AND;
			}
		case '|':
			if (ch == '|') {
				nextChar();
				return Token.OPERATOR_LOGICAL_OR;
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_ARITHMETICAL_OR_ASSIGN;
			} else {
				return Token.OPERATOR_ARITHMETICAL_OR;
			}
		case '^':
			if (ch == '=') {
				nextChar();
				return Token.OPERATOR_ARITHMETICAL_XOR_ASSIGN;
			} else {
				return Token.OPERATOR_ARITHMETICAL_XOR;
			}
		case '<':
			if (ch == '<') {
				nextChar();
				if (ch == '=') {
					nextChar();
					return Token.OPERATOR_SHL_ASSIGN;
				} else {
					return Token.OPERATOR_SHL;
				}
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_LTE;
			} else {
				return Token.OPERATOR_LT;
			}
		case '>':
			if (ch == '>') {
				nextChar();
				if (ch == '>') {
					nextChar();
					if (ch == '=') {
						nextChar();
						return Token.OPERATOR_ASR_ASSIGN;
					} else {
						return Token.OPERATOR_ASR;
					}
				} else if (ch == '=') {
					nextChar();
					return Token.OPERATOR_SHR_ASSIGN;
				} else {
					return Token.OPERATOR_SHR;
				}
			} else if (ch == '=') {
				nextChar();
				return Token.OPERATOR_GTE;
			} else {
				return Token.OPERATOR_GT;
			}
		default:
			return new Token(Token.TYPE_UNKNOWN, script.substring(oldPosition,
					curPosition));
		}
	}

	/**
	 * 抛出解析异常
	 * 
	 * @throws ParserException
	 */
	private void throwLexerException(String message) throws ParserException {
		throw new ParserException("Lexer Exception: " + message,
				getLineNumber());
	}
}
