package zincfish.zincscript;

/**
 * 基于关键字语法的解析器<br>
 * 该解析器
 */
public class Parser {
	/** 当前解析内容的类型 */
	public int type = 0;
	/** 当前解析的内容 */
	public Object value = null;

	private boolean pBack;
	private char cBuf[], line[];
	private int c = 0;
	private int pos = 0;
	private static final int MAX_CHAR = 1024;// 默认字符串的最大字数为1024

	/** String representation of token (needs work) */
	public String toString() {
		return value + ":" + type;
	}

	public Parser() {
		cBuf = new char[MAX_CHAR];
	}

	public Parser(String firstLine) {
		cBuf = new char[MAX_CHAR];
		setString(firstLine);
	}

	/**
	 * 加入一行代码
	 * 
	 * @param str
	 */
	public void setString(String str) {
		line = str.toCharArray();
		pos = 0;
		c = 0;
	}

	/*
	 * 获取下一个字符
	 */
	private int getChar() {
		if (line != null && pos < line.length) {
			return line[pos++];
		} else {
			return Syntax.EOL_TYPE;
		}
	}

	/*
	 * 获取当前位置后offset个字符的位置上的字符。此方法用于前瞻后面的字符，并不改变当前字符的位置
	 * 
	 * @param offset 前瞻字符的个数
	 */
	private int peekChar(int offset) {
		int n = pos + offset - 1;
		if (line == null || n >= line.length) {
			return Syntax.EOL_TYPE;
		} else {
			return line[n];
		}
	}

	/** Causes next call to nextToken to return same value */
	public void pushBack() {
		pBack = true;
	}

	/**
	 * 读取下一个标签，返回标签的类型
	 * 
	 * @return int 标签的类型
	 */
	public int next() {
		if (!pBack) {
			return nextImpl();
		} else {
			pBack = false;
			return type;
		}
	}

	/*
	 * 获取下一个标签的具体实现方法
	 * 
	 * @return int 标签的类型
	 */
	private int nextImpl() {
		int cPos = 0;

		if (c == 0)
			c = getChar();

		value = null;

		// //////////////////// 去掉留白字符 //////////////////////
		while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
			c = getChar();

		if (c == Syntax.EOL_TYPE) {
			type = Syntax.EOL_TYPE;
		}
		// //////////////////////////////////////////////////////

		// ////////// 处理注释，以#开头的行认为是注注释 ///////////
		else if (c == '#') {
			while (c != Syntax.EOL_TYPE)
				c = getChar();
			// 跳过注释
			nextImpl();
			// 取出下面真正有用的关键字
			nextImpl();
		}
		// //////////////////////////////////////////////////////

		// ///////////////////// 处理引号 ////////////////////////
		else if (c == '"') {
			c = getChar();
			while ((c != Syntax.EOL_TYPE) && (c != '"')) {// 循环读取字符直到遇到关闭的引号
				if (c == '\\') {// 处理字符串中的特殊字符
					int nextChar = peekChar(1);
					switch (nextChar) {
					case 'n':
						cBuf[cPos++] = '\n';
						getChar();
						break;
					case 't':
						cBuf[cPos++] = '\t';
						getChar();
						break;
					case 'r':
						cBuf[cPos++] = '\r';
						getChar();
						break;
					case '\"':
						cBuf[cPos++] = '"';
						getChar();
						break;
					case '\\':
						cBuf[cPos++] = '\\';
						getChar();
						break;
					}
				} else {// 将字符压入缓存
					cBuf[cPos++] = (char) c;
				}
				c = getChar();
			}
			value = new String(cBuf, 0, cPos);
			c = getChar();
			type = Syntax.STRING_TYPE;
		}
		// //////////////////////////////////////////////////////

		// /////////////////// 处理关键字 ////////////////////////
		else if (Syntax.ALLOW_WORD_START.indexOf(c) >= 0) {
			while (Syntax.ALLOW_WORD.indexOf(c) >= 0) {
				cBuf[cPos++] = (char) c;
				c = getChar();
			}

			value = new String(cBuf, 0, cPos);

			if (value.equals(Syntax.IF)) {
				type = Syntax.KEYWORD_IF_TYPE;
			} else if (value.equals(Syntax.ELSE)) {
				type = Syntax.KEYWORD_ELSE_TYPE;
			} else if (value.equals(Syntax.ELSE_IF)) {
				type = Syntax.KEYWORD_ELSIF_TYPE;
			} else if (value.equals(Syntax.WHILE)) {
				type = Syntax.KEYWORD_WHILE_TYPE;
			} else if (value.equals(Syntax.DEF)) {
				type = Syntax.KEYWORD_DEF_TYPE;
			} else if (value.equals(Syntax.RETURN)) {
				type = Syntax.KEYWORD_RETURN_TYPE;
			} else if (value.equals(Syntax.EXIT)) {
				type = Syntax.KEYWORD_EXIT_TYPE;
			} else if (value.equals(Syntax.INT)) {
				type = Syntax.KEYWORD_INT_TYPE;
			} else if (value.equals(Syntax.STRING)) {
				type = Syntax.KEYWORD_STRING_TYPE;
			} else if (value.equals(Syntax.END)) {
				type = Syntax.KEYWORD_END_TYPE;
			} else if (value.equals(Syntax.ARRAY)) {
				type = Syntax.KEYWORD_ARRAY_TYPE;
			} else if (value.equals(Syntax.ARRAY)) {
				type = Syntax.KEYWORD_ARRAY_TYPE;
			} else if (c == '(') {
				type = Syntax.FUNC_TYPE;
			} else if (c == '[') {
				type = Syntax.ARRAY_ASSIGN_TYPE;
			} else {
				type = Syntax.VAR_TYPE;
			}

		}
		// //////////////////////////////////////////////////////

		// ///////////////////// 处理数字 ////////////////////////
		else if (c >= '0' && c <= '9') {
			boolean isHex = false;
			if (c == '0') {
				int p = peekChar(1);
				if (p == 'x' || p == 'X') {
					isHex = true;
					c = getChar();
					c = getChar();
				} else {
					isHex = false;
				}
			}
			do {
				cBuf[cPos++] = (char) c;
				c = getChar();
			} while (c >= '0' && c <= '9' || isHex
					&& (c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F'));
			String str = new String(cBuf, 0, cPos);
			type = Syntax.NUMBER_TYPE;
			value = new Integer(Integer.parseInt(str, isHex ? 16 : 10));
			str = null;
		}
		// //////////////////////////////////////////////////////

		// ///////////////////// 处理运算符//////////////////////
		else {
			if (c == '+') {
				type = Syntax.OPERATOR_PLUS_TYPE;
			} else if (c == '-') {
				type = Syntax.OPERATOR_MINUS_TYPE;
			} else if (c == '*') {
				type = Syntax.OPERATOR_MULT_TYPE;
			} else if (c == '/') {
				type = Syntax.OPERATOR_DIV_TYPE;
			} else if (c == '%') {
				type = Syntax.OPERATOR_MOD_TYPE;
			} else if (c == '>') {
				if (peekChar(1) == '=') {
					getChar();
					type = Syntax.OPERATOR_GTE_TYPE;
				} else {
					type = Syntax.OPERATOR_GT_TYPE;
				}
			} else if (c == '<') {
				if (peekChar(1) == '=') {
					getChar();
					type = Syntax.OPERATOR_LTE_TYPE;
				} else {
					type = Syntax.OPERATOR_LT_TYPE;
				}
			} else if (c == '=') {
				if (peekChar(1) == '=') {
					getChar();
					type = Syntax.LOGICAL_EQUAL_TYPE;
				} else {
					type = Syntax.OPERATOR_EQUAL_TYPE;
				}
			} else if (c == '!') {
				if (peekChar(1) == '=') {
					getChar();
					type = Syntax.LOGICAL_NEGTIVE_TYPE;
				} else {
					type = Syntax.OPERATOR_NEGTIVE_TYPE;
				}
			} else if ((c == '|') && (peekChar(1) == '|')) {
				getChar();
				type = Syntax.LOGICAL_OR_TYPE;
			} else if ((c == '&') && (peekChar(1) == '&')) {
				getChar();
				type = Syntax.LOGICAL_AND_TYPE;
			} else {
				type = c;
			}
			c = getChar();
		}
		// //////////////////////////////////////////////////////

		return type;
	}

	public void reset() {
		type = 0;
		value = null;
		line = null;
		pos = 0;
		c = 0;
	}

	public void release() {
		reset();
		cBuf = null;
		line = null;
	}
}
