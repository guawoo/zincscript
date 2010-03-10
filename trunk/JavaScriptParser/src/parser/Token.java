package parser;

/**
 * <code>Token</code> 定义了词法单元的数据结构
 * <p>
 * 词法单元是词法分析器({@link Lexer})的最小输出. 其数据结构如下:
 * 
 * <pre>
 * 	&lt;token-name, attribute-value&gt;
 * </pre>
 * 
 * @author Jarod Yv
 * @see 《编译原理(第2版)》 3页 1.2.1.词法分析
 */
public final class Token {
	// ///////////////////////////////// 语法类型 /////////////////////////////////
	public static final int TYPE_UNKNOWN = 0x00; /* 未知语法 */
	public static final int TYPE_NEWLINE = 0x01; /* 换行 */
	public static final int TYPE_SINGLELINE_COMMENT = 0x02; /* 单行注释 */
	public static final int TYPE_MULTILINE_COMMENT = 0x03; /* 多行注释 */
	public static final int TYPE_WHITESPACE = 0x04; /* 空白字符 */
	public static final int TYPE_KEYWORD = 0x05; /* 关键字语法 */
	public static final int TYPE_OPERATOR = 0x06; /* 运算符语法 */
	public static final int TYPE_IDENTIFIER = 0x07; /* 标识符语法 */
	public static final int TYPE_STRING = 0x08; /* 字符串 */
	public static final int TYPE_REGEX = 0x09; /* 正则式 */
	public static final int TYPE_OCTAL = 0x0a; /* 8进制数字 */
	public static final int TYPE_DECIMAL = 0x0b; /* 10进制数字 */
	public static final int TYPE_HEXAL = 0x0c; /* 16进制数字 */
	public static final int TYPE_FLOAT = 0x0d; /* 浮点数 */
	public static final int TYPE_EOF = 0x0e; /* 文件结尾 */
	// ///////////////////////////////////////////////////////////////////////////

	// ////////////////////////////// 下面定义固定语法 /////////////////////////////

	/* ------------------------------ 文档结尾语法 ------------------------------ */
	public static final Token EOF = new Token(TYPE_EOF);
	/* ------------------------------------------------------------------------- */

	/* -------------------------------- 留白语法 -------------------------------- */
	public static final Token NEWLINE = new Token(TYPE_NEWLINE);
	public static final Token MULTILINECOMMENT = new Token(
			TYPE_MULTILINE_COMMENT);
	public static final Token SINGLELINECOMMENT = new Token(
			TYPE_SINGLELINE_COMMENT);
	public static final Token WHITESPACE = new Token(TYPE_WHITESPACE);
	/* ------------------------------------------------------------------------- */

	/* ------------------------------- 关键字语法 ------------------------------- */
	public static final Token KEYWORD_BREAK = new Token(TYPE_KEYWORD, "break");
	public static final Token KEYWORD_CASE = new Token(TYPE_KEYWORD, "case");
	public static final Token KEYWORD_CATCH = new Token(TYPE_KEYWORD, "catch");
	public static final Token KEYWORD_CONTINUE = new Token(TYPE_KEYWORD,
			"continue");
	public static final Token KEYWORD_DEFAULT = new Token(TYPE_KEYWORD,
			"default");
	public static final Token KEYWORD_DELETE = new Token(TYPE_KEYWORD, "delete");
	public static final Token KEYWORD_DO = new Token(TYPE_KEYWORD, "do");
	public static final Token KEYWORD_ELSE = new Token(TYPE_KEYWORD, "else");
	public static final Token KEYWORD_FALSE = new Token(TYPE_KEYWORD, "false");
	public static final Token KEYWORD_FINALLY = new Token(TYPE_KEYWORD,
			"finally");
	public static final Token KEYWORD_FOR = new Token(TYPE_KEYWORD, "for");
	public static final Token KEYWORD_FUNCTION = new Token(TYPE_KEYWORD,
			"function");
	public static final Token KEYWORD_IF = new Token(TYPE_KEYWORD, "if");
	public static final Token KEYWORD_IN = new Token(TYPE_KEYWORD, "in");
	public static final Token KEYWORD_INSTANCEOF = new Token(TYPE_KEYWORD,
			"instanceof");
	public static final Token KEYWORD_NEW = new Token(TYPE_KEYWORD, "new");
	public static final Token KEYWORD_NULL = new Token(TYPE_KEYWORD, "null");
	public static final Token KEYWORD_RETURN = new Token(TYPE_KEYWORD, "return");
	public static final Token KEYWORD_SWITCH = new Token(TYPE_KEYWORD, "switch");
	public static final Token KEYWORD_THIS = new Token(TYPE_KEYWORD, "this");
	public static final Token KEYWORD_THROW = new Token(TYPE_KEYWORD, "throw");
	public static final Token KEYWORD_TRUE = new Token(TYPE_KEYWORD, "true");
	public static final Token KEYWORD_TRY = new Token(TYPE_KEYWORD, "try");
	public static final Token KEYWORD_TYPEOF = new Token(TYPE_KEYWORD, "typeof");
	public static final Token KEYWORD_VAR = new Token(TYPE_KEYWORD, "var");
	public static final Token KEYWORD_VOID = new Token(TYPE_KEYWORD, "void");
	public static final Token KEYWORD_WHILE = new Token(TYPE_KEYWORD, "while");
	public static final Token KEYWORD_WITH = new Token(TYPE_KEYWORD, "with");
	/* ------------------------------------------------------------------------- */

	/* ------------------------------ 保留关键字语法 ----------------------------- */
	public static final Token KEYWORD_ABSTRACT = new Token(TYPE_KEYWORD,
			"abstract");
	public static final Token KEYWORD_BOOLEAN = new Token(TYPE_KEYWORD,
			"boolean");
	public static final Token KEYWORD_BYTE = new Token(TYPE_KEYWORD, "byte");
	public static final Token KEYWORD_CHAR = new Token(TYPE_KEYWORD, "char");
	public static final Token KEYWORD_CLASS = new Token(TYPE_KEYWORD, "class");
	public static final Token KEYWORD_CONST = new Token(TYPE_KEYWORD, "const");
	public static final Token KEYWORD_DEBUGGER = new Token(TYPE_KEYWORD,
			"debugger");
	public static final Token KEYWORD_DOUBLE = new Token(TYPE_KEYWORD, "double");
	public static final Token KEYWORD_ENUM = new Token(TYPE_KEYWORD, "enum");
	public static final Token KEYWORD_EXPORT = new Token(TYPE_KEYWORD, "export");
	public static final Token KEYWORD_EXTENDS = new Token(TYPE_KEYWORD,
			"extends");
	public static final Token KEYWORD_FINAL = new Token(TYPE_KEYWORD, "final");
	public static final Token KEYWORD_FLOAT = new Token(TYPE_KEYWORD, "float");
	public static final Token KEYWORD_GOTO = new Token(TYPE_KEYWORD, "goto");
	public static final Token KEYWORD_IMPLEMENTS = new Token(TYPE_KEYWORD,
			"implements");
	public static final Token KEYWORD_IMPORT = new Token(TYPE_KEYWORD, "import");
	public static final Token KEYWORD_INT = new Token(TYPE_KEYWORD, "int");
	public static final Token KEYWORD_INTERFACE = new Token(TYPE_KEYWORD,
			"interface");
	public static final Token KEYWORD_LONG = new Token(TYPE_KEYWORD, "long");
	public static final Token KEYWORD_NATIVE = new Token(TYPE_KEYWORD, "native");
	public static final Token KEYWORD_PACKAGE = new Token(TYPE_KEYWORD,
			"package");
	public static final Token KEYWORD_PRIVATE = new Token(TYPE_KEYWORD,
			"private");
	public static final Token KEYWORD_PROTECTED = new Token(TYPE_KEYWORD,
			"protected");
	public static final Token KEYWORD_PUBLIC = new Token(TYPE_KEYWORD, "public");
	public static final Token KEYWORD_SHORT = new Token(TYPE_KEYWORD, "short");
	public static final Token KEYWORD_STATIC = new Token(TYPE_KEYWORD, "static");
	public static final Token KEYWORD_SUPER = new Token(TYPE_KEYWORD, "super");
	public static final Token KEYWORD_SYNCHRONIZED = new Token(TYPE_KEYWORD,
			"synchronized");
	public static final Token KEYWORD_THROWS = new Token(TYPE_KEYWORD, "throws");
	public static final Token KEYWORD_TRANSIENT = new Token(TYPE_KEYWORD,
			"transient");
	public static final Token KEYWORD_VOLATILE = new Token(TYPE_KEYWORD,
			"volatile");
	/* ------------------------------------------------------------------------- */

	/* ------------------------------- 运算符语法 ------------------------------- */
	public static final Token OPERATOR_ASSIGN = new Token(TYPE_OPERATOR, "=");
	public static final Token OPERATOR_SEMICOLON = new Token(TYPE_OPERATOR, ";");
	public static final Token OPERATOR_COLON = new Token(TYPE_OPERATOR, ":");
	public static final Token OPERATOR_COMMA = new Token(TYPE_OPERATOR, ",");
	public static final Token OPERATOR_QUESTION = new Token(TYPE_OPERATOR, "?");
	public static final Token OPERATOR_DOT = new Token(TYPE_OPERATOR, ".");

	// 括号
	public static final Token OPERATOR_OPENBRACE = new Token(TYPE_OPERATOR, "{");
	public static final Token OPERATOR_CLOSEBRACE = new Token(TYPE_OPERATOR,
			"}");
	public static final Token OPERATOR_OPENPAREN = new Token(TYPE_OPERATOR, "(");
	public static final Token OPERATOR_CLOSEPAREN = new Token(TYPE_OPERATOR,
			")");
	public static final Token OPERATOR_OPENSQUARE = new Token(TYPE_OPERATOR,
			"[");
	public static final Token OPERATOR_CLOSESQUARE = new Token(TYPE_OPERATOR,
			"]");

	// 位运算符
	public static final Token OPERATOR_ARITHMETICAL_NOT = new Token(
			TYPE_OPERATOR, "~");
	public static final Token OPERATOR_ARITHMETICAL_AND = new Token(
			TYPE_OPERATOR, "&");
	public static final Token OPERATOR_ARITHMETICAL_AND_ASSIGN = new Token(
			TYPE_OPERATOR, "&=");
	public static final Token OPERATOR_ARITHMETICAL_OR = new Token(
			TYPE_OPERATOR, "|");
	public static final Token OPERATOR_ARITHMETICAL_OR_ASSIGN = new Token(
			TYPE_OPERATOR, "|=");
	public static final Token OPERATOR_ARITHMETICAL_XOR = new Token(
			TYPE_OPERATOR, "^");
	public static final Token OPERATOR_ARITHMETICAL_XOR_ASSIGN = new Token(
			TYPE_OPERATOR, "^=");

	// 逻辑运算符
	public static final Token OPERATOR_EQUAL = new Token(TYPE_OPERATOR, "==");
	public static final Token OPERATOR_NOT_EQUAL = new Token(TYPE_OPERATOR,
			"!=");
	public static final Token OPERATOR_STRICT_EQUAL = new Token(TYPE_OPERATOR,
			"===");
	public static final Token OPERATOR_NOT_STRICT_EQUAL = new Token(
			TYPE_OPERATOR, "!==");
	public static final Token OPERATOR_GT = new Token(TYPE_OPERATOR, ">");
	public static final Token OPERATOR_GTE = new Token(TYPE_OPERATOR, ">=");
	public static final Token OPERATOR_LT = new Token(TYPE_OPERATOR, "<");
	public static final Token OPERATOR_LTE = new Token(TYPE_OPERATOR, "<=");
	public static final Token OPERATOR_LOGICAL_AND = new Token(TYPE_OPERATOR,
			"&&");
	public static final Token OPERATOR_LOGICAL_NOT = new Token(TYPE_OPERATOR,
			"!");
	public static final Token OPERATOR_LOGICAL_OR = new Token(TYPE_OPERATOR,
			"||");

	// 算数运算符
	public static final Token OPERATOR_PLUS = new Token(TYPE_OPERATOR, "+");
	public static final Token OPERATOR_PLUS_ASSIGN = new Token(TYPE_OPERATOR,
			"+=");
	public static final Token OPERATOR_INCREASE = new Token(TYPE_OPERATOR, "++");
	public static final Token OPERATOR_MINUS = new Token(TYPE_OPERATOR, "-");
	public static final Token OPERATOR_MINUS_ASSIGN = new Token(TYPE_OPERATOR,
			"-=");
	public static final Token OPERATOR_DECREASE = new Token(TYPE_OPERATOR, "--");
	public static final Token OPERATOR_MUL = new Token(TYPE_OPERATOR, "*");
	public static final Token OPERATOR_MUL_ASSIGN = new Token(TYPE_OPERATOR,
			"*=");
	public static final Token OPERATOR_DIVIDE = new Token(TYPE_OPERATOR, "/");
	public static final Token OPERATOR_DIVIDE_ASSIGN = new Token(TYPE_OPERATOR,
			"/=");
	public static final Token OPERATOR_MOD = new Token(TYPE_OPERATOR, "%");
	public static final Token OPERATOR_MOD_ASSIGN = new Token(TYPE_OPERATOR,
			"%=");

	// 移位运算符
	public static final Token OPERATOR_SHL = new Token(TYPE_OPERATOR, "<<");
	public static final Token OPERATOR_SHL_ASSIGN = new Token(TYPE_OPERATOR,
			"<<=");
	public static final Token OPERATOR_SHR = new Token(TYPE_OPERATOR, ">>");
	public static final Token OPERATOR_SHR_ASSIGN = new Token(TYPE_OPERATOR,
			">>=");
	public static final Token OPERATOR_ASR = new Token(TYPE_OPERATOR, ">>>");
	public static final Token OPERATOR_ASR_ASSIGN = new Token(TYPE_OPERATOR,
			">>>=");
	/* ------------------------------------------------------------------------- */

	/**
	 * tokenType是一个由语法分析步骤使用的抽象符号, 用于定义词法单元的类型
	 */
	private int tokenType = TYPE_UNKNOWN;

	/**
	 * attributeValue记录了关于这个词法单元的条目
	 */
	private String attributeValue;

	/**
	 * 构造函数
	 * 
	 * @param tokenType
	 *            {@link #tokenType}
	 */
	public Token(int tokenType) {
		this(tokenType, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param tokenType
	 *            {@link #tokenType}
	 * @param attributeValue
	 *            {@link #attributeValue}
	 */
	public Token(int tokenType, String attributeValue) {
		this.tokenType = tokenType;
		this.attributeValue = attributeValue;
	}

	/**
	 * 对于数字、字符串、正则式和标识符语法，只要他们的类型和值相同则认为相同；<br/>
	 * 其他类型语法必须是同一对象引用才认为是相同
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (this.getClass() == object.getClass()) {
			Token token = (Token) object;
			switch (tokenType) {
			case TYPE_FLOAT:
			case TYPE_OCTAL:
			case TYPE_DECIMAL:
			case TYPE_HEXAL:
			case TYPE_REGEX:
			case TYPE_STRING:
			case TYPE_IDENTIFIER:
				return (this.tokenType == token.tokenType && this.attributeValue
						.equals(token.attributeValue));
			default:
				return (this == token);
			}
		}
		return false;
	}

	/**
	 * 获取语法类型
	 * 
	 * @return {@link #tokenType}
	 */
	public int getTokenType() {
		return tokenType;
	}

	/**
	 * 获取语法内容
	 * 
	 * @return {@link #attributeValue}
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * 实现对象Hashcode的计算
	 * 
	 * @return 对象的Hashcode
	 */
	public int hashCode() {
		return tokenType ^ attributeValue.hashCode();
	}

	/**
	 * 判断是否是空白字符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是空白字符
	 *         <li><code>false</code> - 不是空白字符
	 *         </ul>
	 */
	public boolean isWhitespace() {
		return isWhitespaceWithoutNewline() || tokenType == TYPE_NEWLINE;
	}

	/**
	 * 判断是否是除换行符以外的空白字符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是除换行符以外的空白字符
	 *         <li><code>false</code> - 不是除换行符以外的空白字符
	 *         </ul>
	 */
	public boolean isWhitespaceWithoutNewline() {
		return tokenType == TYPE_MULTILINE_COMMENT
				|| tokenType == TYPE_SINGLELINE_COMMENT
				|| tokenType == TYPE_WHITESPACE;
	}

	/**
	 * 判断是否是文件结尾
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是文件结尾
	 *         <li><code>false</code> - 不是文件结尾
	 *         </ul>
	 */
	public boolean isEOF() {
		return tokenType == TYPE_EOF;
	}

	/**
	 * 判断是否是标识符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是标识符
	 *         <li><code>false</code> - 不是标识符
	 *         </ul>
	 */
	public boolean isIdentifier() {
		return tokenType == TYPE_IDENTIFIER;
	}

	/**
	 * 判断是否是换行符
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是换行符
	 *         <li><code>false</code> - 不是换行符
	 *         </ul>
	 */
	public boolean isNewLine() {
		return tokenType == TYPE_NEWLINE;
	}

	/**
	 * 判断是否是数字
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是数字
	 *         <li><code>false</code> - 不是数字
	 *         </ul>
	 */
	public boolean isNumericLiteral() {
		return tokenType == TYPE_DECIMAL || tokenType == TYPE_FLOAT
				|| tokenType == TYPE_HEXAL || tokenType == TYPE_OCTAL;
	}

	/**
	 * 判断是否是正则式
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是正则式
	 *         <li><code>false</code> - 不是正则式
	 *         </ul>
	 */
	public boolean isRegexLiteral() {
		return tokenType == TYPE_REGEX;
	}

	/**
	 * 判断是否是字符串
	 * 
	 * @return <ul>
	 *         <li><code>true</code> - 是字符串
	 *         <li><code>false</code> - 不是字符串
	 *         </ul>
	 */
	public boolean isStringLiteral() {
		return tokenType == TYPE_STRING;
	}

	/**
	 * 返回语法描述，用于Debug
	 * 
	 * @see java.lang.Object#toString()
	 */
	public final String toString() {
		switch (tokenType) {
		case TYPE_NEWLINE:
			return "NEWLINE";
		case TYPE_MULTILINE_COMMENT:
			return "MULTILINECOMMENT:";
		case TYPE_SINGLELINE_COMMENT:
			return "SINGLELINECOMMENT:";
		case TYPE_WHITESPACE:
			return "WHITESPACE:";
		case TYPE_KEYWORD:
			return "KEYWORD: " + attributeValue;
		case TYPE_OPERATOR:
			return "OPERATOR: " + attributeValue;
		case TYPE_OCTAL:
			return "OCTAL: " + attributeValue;
		case TYPE_FLOAT:
			return "FLOAT: " + attributeValue;
		case TYPE_DECIMAL:
			return "DECIMAL: " + attributeValue;
		case TYPE_HEXAL:
			return "HEXADECIMAL: " + attributeValue;
		case TYPE_REGEX:
			return "REGEX: " + attributeValue;
		case TYPE_STRING:
			return "STRING: " + attributeValue;
		case TYPE_IDENTIFIER:
			return "IDENTIFIER: " + attributeValue;
		case TYPE_EOF:
			return "EOF";
		case TYPE_UNKNOWN:
			return "UNKNOWN: " + attributeValue;
		default:
			throw new IllegalArgumentException();
		}
	}
}
