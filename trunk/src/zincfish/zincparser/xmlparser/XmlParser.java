package zincfish.zincparser.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

/**
 * XML解析器，根据KXML修改发展而来，更加适用于解析ZML文档
 * 
 * @author Jarod Yv
 * @since finerling
 */
public class XmlParser implements IXmlParser {
	// public static Log log = Log.getLog("XmlParser");
	private final int ASCII_LF = 10; // 换行符

	private final int ASCII_CR = 13; // 回车

	private final int ASCII_SPACE = 32; // 空格

	private final int ASCII_EXCALMATORY_MARK = 33; // !

	private final int ASCII_DOUBLE_QUOTATION_MARKS = 34; // "

	private final int ASCII_POUND = 35; // #

	private final int ASCII_AND = 38; // &

	private final int ASCII_SINGLE_QUOTATION_MARKS = 39; // '

	private final int ASCII_MINUS = 45; // -

	private final int ASCII_DOT = 46; // .

	private final int ASCII_SLASH = 47; // /

	private final int ASCII_COLON = 58; // :

	private final int ASCII_LT = 60; // <

	private final int ASCII_EQUAL = 61; // =

	private final int ASCII_GT = 62; // >

	private final int ASCII_QUESTION_MARK = 63; // ?

	private final int ASCII_LEFT_SQUARE_BRACKERS = 91; // [

	private final int ASCII_RIGHT_SQUARE_BRACKETS = 93; // ]

	private final int ASCII_UNDER_LINE = 95; // _

	private Object location;

	private String version;

	private boolean standalone;// standalone属性

	private boolean processNsp;// 表示是否在处理命名空间

	private boolean isRelaxed; // 标记是否格式严格

	private Hashtable entityMap; // 转义字符映射表

	private int depth; // 标签嵌套深度

	private String elementStack[]; // 开始标签栈

	private String nspStack[]; // 命名空间栈

	private int nspCounts[];

	private Reader reader;

	private String encoding; // 文件编码格式

	// 原始文件缓存，保存从文件中直接读出的字符
	// 这些字符不加任何操作，与原始文件的内容完全一致
	private char[] srcBuf;

	private int srcPos; // srcBuf的指针

	private int srcCount; // srcBuf的长度

	private int line; // 当前解析的行位置

	private int column; // 当前解析的列位置

	// 文字缓冲,保存从文本中解析出来的有用的信息
	private char[] txtBuf;

	private int txtPos; // 文字缓冲位置指针

	private int type; // 解析内容的类型

	private boolean isWhitespace; // 标记是否是空白

	private String namespace; // 命名空间

	private String prefix; // 前缀

	private String tagName; // 标签名

	// 标记是否是简化的标签<br>
	// 即< ... />类型的标签
	private boolean isDegenerate;

	private int attributeCount; // 属性个数

	private String[] attributes; // 属性列表

	private int stackMismatch; // 不匹配的标签数

	private String error; // 错误信息

	private int[] peek; // 用于存放从文件中最近读出的字符

	private int peekCount; // peek数组的大小

	private boolean wasCR; // 标记是否换行

	private boolean unresolved; // 标装以字符是否能够处理

	private boolean token; // 标记当前解析的内容是否是标签

	private int totalSize; // 解析文件的总大小

	private boolean isBlocked; // 标记是否阻塞

	private boolean isRunning = true; // 标记是否解析

	public XmlParser() {
		totalSize = 0;
		isBlocked = false;
		elementStack = new String[16];
		nspStack = new String[8];
		nspCounts = new int[4];
		txtBuf = new char[128];
		attributes = new String[16];
		stackMismatch = 0;
		peek = new int[2];
		srcBuf = new char[Runtime.getRuntime().freeMemory() < 0x100000L ? '\200'
				: 8192];
		entityMap = new Hashtable();
		entityMap.put("amp", "&");
		entityMap.put("nbsp", " ");
		entityMap.put("copy", "(c)");
		entityMap.put("apos", "'");
		entityMap.put("gt", ">");
		entityMap.put("lt", "<");
		entityMap.put("quot", "\"");
		entityMap.put("ldquo", "”");
		entityMap.put("rdquo", "“");
	}

	private final boolean adjustNsp() throws ParserException {
		boolean flag = false;
		for (int i = 0; i < attributeCount << 2; i += 4) {
			String s = attributes[i + 2];
			int l = s.indexOf(ASCII_COLON);
			String s2;
			if (l != -1) {
				s2 = s.substring(0, l);
				s = s.substring(l + 1);
			} else {
				if (!s.equals("xmlns"))
					continue;
				s2 = s;
				s = null;
			}
			if (!s2.equals("xmlns")) {
				flag = true;
				continue;
			}
			int j1 = nspCounts[depth]++ << 1;
			nspStack = ensureCapacity(nspStack, j1 + 2);
			nspStack[j1] = s;
			nspStack[j1 + 1] = attributes[i + 3];
			if (s != null && attributes[i + 3].equals(""))
				exception("illegal empty namespace");
			System.arraycopy(attributes, i + 4, attributes, i,
					(--attributeCount << 2) - i);
			i -= 4;
		}

		if (flag) {
			for (int j = (attributeCount << 2) - 4; j >= 0; j -= 4) {
				String s1 = attributes[j + 2];
				int i1 = s1.indexOf(ASCII_COLON);
				if (i1 == 0 && !isRelaxed)
					throw new RuntimeException("illegal attribute name: " + s1
							+ " at " + this);
				if (i1 == -1)
					continue;
				String s3 = s1.substring(0, i1);
				s1 = s1.substring(i1 + 1);
				String s4 = getNamespace(s3);
				if (s4 == null && !isRelaxed)
					throw new RuntimeException("Undefined Prefix: " + s3
							+ " in " + this);
				attributes[j] = s4;
				attributes[j + 1] = s3;
				attributes[j + 2] = s1;
			}

		}
		int k = tagName.indexOf(ASCII_COLON);
		if (k == 0)
			exception("illegal tag name: " + tagName);
		if (k != -1) {
			prefix = tagName.substring(0, k);
			tagName = tagName.substring(k + 1);
		}
		namespace = getNamespace(prefix);
		if (namespace == null) {
			if (prefix != null)
				exception("undefined prefix: " + prefix);
			namespace = "";
		}
		return flag;
	}

	/**
	 * 确认字符串数组的容量<br>
	 * <li>如果字符串数组的长度大于指定长度，则直接返回 <li>如果小于指定长度，则对字符串数组扩容
	 * 
	 * @param strs
	 *            字符串数组
	 * @param capacity
	 *            容量
	 * @return 处理后的字符串数组
	 */
	private final String[] ensureCapacity(String[] strs, int capacity) {
		if (strs.length >= capacity) {
			// 如果字符串长度>=容量，直接返回字符串
			return strs;
		} else {
			// 否则新建字符串，将原字符串拷贝进去
			String temp[] = new String[capacity + 16];
			System.arraycopy(strs, 0, temp, 0, strs.length);
			return temp;
		}
	}

	/**
	 * 抛出解析中的错误<br>
	 * 当解析过程中遇到不符合语法规范或者书写不严格的情况时，提示错误
	 * 
	 * @param msg
	 *            错误信息
	 * @throws ParserException
	 *             解析异常
	 */
	// private final void error(String msg) throws ParserException {
	// // 如果遇到xml文件不符合语法规范或者书写不严格
	// // 则记录错误信息
	// if (isRelaxed) {
	// if (error == null)
	// error = "ERR: " + msg;
	// }
	// // 否则抛出解析异常
	// else {
	// exception(msg);
	// }
	// }
	/**
	 * 抛出解析中的异常
	 * 
	 * @param msg
	 *            异常信息
	 * @throws ParserException
	 *             解析异常
	 */
	private final void exception(String msg) throws ParserException {
		if (isRelaxed) {
			throw new ParserException(msg.length() >= 100 ? msg.substring(0,
					100)
					+ "\n" : msg, this, null);
		}
	}

	/**
	 * 解析下一个标签的具体实现
	 * 
	 * @throws IOException
	 *             IO异常
	 * @throws ParserException
	 *             解析异常
	 */
	private final void nextImpl() throws IOException, ParserException {
		if (reader == null)
			exception("No Input specified");
		// 遇到结束标签，嵌套深度-1
		if (type == END_TAG)
			depth--;
		do {
			attributeCount = -1;
			// 如果是简化的标签，则返回结束标签
			if (isDegenerate) {
				isDegenerate = false;
				type = END_TAG;
				return;
			}
			// 遇到任何错误退出解析，
			// 为了安全，把当前解析的内容认为是注释
			if (error != null) {
				pushError(error);
				error = null;
				type = ERROR;
				return;
			}
			// 如果文档格式不严格并且存在不匹配的标签或者还存在嵌套标签没有正确关闭
			// 则处理嵌套标签的深度,将嵌套标签闭合.
			// 注:此处仅能处理一个不匹配标签,如果不匹配标签多于1个,仍然会抛异常
			if (isRelaxed && (stackMismatch > 0 || peek(0) == -1 && depth > 0)) {
				int j = depth - 1 << 2;
				type = END_TAG;
				namespace = elementStack[j];
				prefix = elementStack[j + 1];
				tagName = elementStack[j + 2];
				// 不匹配数-1
				if (stackMismatch > 0)
					stackMismatch--;
				// 如果不匹配标签数大于1个,则抛出异常
				if (stackMismatch != 0)
					exception("missing end tag /" + tagName + " inserted");
				return;
			}
			prefix = null;
			tagName = null;
			namespace = null;
			type = peekType(); // 获取标签的类型

			// 根据不同的类型调用相应的解析方法
			switch (type) {
			// 如果是转义字符,处理转义字符
			case ENTITY_REF: // '\006'
				pushEntity();
				return;
				// 如果是开始标签,解析开始标签
			case START_TAG: // '\002'
				parseStartTag(false);
				return;
				// 如果是结束标签,解析结束标签
			case END_TAG: // '\003'
				parseEndTag();
				return;
				// 如果是文档结尾,不作处理
			case END_DOCUMENT: // '\001'
				return;
				// 如果是文本,解析文本
			case TEXT: // '\004'
				pushText(ASCII_LT, !token);
				if (depth == 0 && isWhitespace)
					type = IGNORABLE_WHITESPACE;
				return;
				// 如果是其他类型,解析文件头或注释
			case CDSECT: // '\005'
			default:
				type = parseHead(token);
				break;
			}
		} while (type == TOP_HEAD);
	}

	/**
	 * 处理xml体以外的标签和语句<br>
	 * 包括xml的头、注释、CDATA和DOCTYPE
	 * 
	 * @param isToken
	 *            标记是否是标签
	 * @return IXmlParser.TOP_HEAD
	 * @throws IOException
	 * @throws ParserException
	 */
	private final int parseHead(boolean isToken) throws IOException,
			ParserException {
		String followStr = ""; // ?或!后跟着的字符串
		int i = 0;
		readOneChar(); // 忽略第一个"<"字符
		int j = readOneChar(); // 读取第2个字符
		byte endChar; // 结束的字符
		byte headType; // 类型
		// 如果第二个字符是？,说明这是xml文档的第一行，处理xml的头
		if (j == ASCII_QUESTION_MARK) {
			if ((peek(0) == 120 || peek(0) == 88) // x || X
					&& (peek(1) == 109 || peek(1) == 77)) { // m || M
				if (isToken) {
					pushOneChar(peek(0));
					pushOneChar(peek(1));
				}
				readOneChar();
				readOneChar();
				if ((peek(0) == 108 || peek(0) == 76) // l || L
						&& peek(1) <= ASCII_SPACE) {
					if (line != 1 || column > 4)
						exception("PI must not start with xml");
					parseStartTag(true);
					// 处理Version属性
					if (attributeCount < 1 || !"version".equals(attributes[2]))
						exception("version expected");
					version = attributes[3];
					int l = 1;
					// 处理encoding属性
					if (l < attributeCount && "encoding".equals(attributes[6])) {
						encoding = attributes[7];
						l++;
					} else
						exception("encoding expected");
					// 处理standalone属性
					if (l < attributeCount
							&& "standalone".equals(attributes[4 * l + 2])) {
						String s1 = attributes[3 + 4 * l];
						if ("yes".equals(s1))
							standalone = true;
						else if ("no".equals(s1))
							standalone = false;
						else
							exception("illegal standalone value: " + s1);
						l++;
					} else
						exception("standalone expected");
					// 如果处理的属性数与返回的属性数不相等
					// xml文档结构有问题
					if (l != attributeCount)
						exception("illegal xmldecl");
					isWhitespace = true;
					txtPos = 0;
					return TOP_HEAD;
				}
			}
			endChar = ASCII_QUESTION_MARK;
			headType = PROCESSING_INSTRUCTION;
		}
		// 如果第二个字符为！，则可能是注释、CDATA或DOCTYPE
		// 根据!后的字符具体判断
		else if (j == ASCII_EXCALMATORY_MARK) {
			// 如果!后跟着-,说明是注释
			if (peek(0) == ASCII_MINUS) {
				headType = COMMENT;
				followStr = "--";
				endChar = ASCII_MINUS;
			}
			// 如果!后跟着[，说明是CDATA
			else if (peek(0) == ASCII_LEFT_SQUARE_BRACKERS) {
				headType = CDSECT;
				followStr = "[CDATA[";
				endChar = ASCII_RIGHT_SQUARE_BRACKETS;
				isToken = true;
			}
			// 如果是其余字符，统一理解为DOCTYPE
			else {
				headType = DOCDECL;
				followStr = "DOCTYPE";
				endChar = -1;
			}
		}
		// 如果第二个字符是!和?外的其他字符，统一理解为非法字符
		else {
			exception("illegal: <" + j);
			return COMMENT;
		}

		// 验证接下来的字符串是否是我们预期的字符串
		// 如果不是，会报解析异常。
		for (int i1 = 0; i1 < followStr.length(); i1++)
			read(followStr.charAt(i1));
		// 如果类型为Doctype,则解析Doctype
		if (headType == DOCDECL) {
			parseDoctype(isToken);
		} else {
			do {
				int k = readOneChar();
				if (k == -1) {
					exception("Unexpected EOF");
					return COMMENT;
				}
				if (isToken)
					pushOneChar(k);
				if ((endChar == ASCII_QUESTION_MARK || k == endChar)
						&& peek(0) == endChar && peek(1) == ASCII_GT)
					break;
				i = k;
			} while (true);
			if (endChar == ASCII_MINUS && i == ASCII_MINUS)
				exception("illegal comment delimiter: --->");
			readOneChar();
			readOneChar();
			if (isToken && endChar != ASCII_QUESTION_MARK)
				txtPos--;
		}
		return headType;
	}

	/**
	 * 解析Doctype
	 * 
	 * @param flag
	 * @throws IOException
	 * @throws ParserException
	 */
	private final void parseDoctype(boolean flag) throws IOException,
			ParserException {
		int nestNum = 1; // <>对的个数，默认一对
		boolean isQuoted = false; // 标记是否是在单引号中
		do {
			int firstChar;
			do {
				firstChar = readOneChar();
				switch (firstChar) {
				// 没有读到字符，抛出EOF
				case -1:
					exception("Unexpected EOF");
					return;
					// 读到单引号，开关引用状态
				case ASCII_SINGLE_QUOTATION_MARKS: // '\''
					isQuoted = !isQuoted;
					break;
				// 读到<,并且不再单引号中，说明有嵌套标记，<>对数量+1
				case ASCII_LT: // '<'
					if (!isQuoted) // 不在单引号中
						nestNum++;
					break;
				// 读到>,并且不在单引号中，则关闭一对嵌套的<>
				case ASCII_GT: // '>'
					if (!isQuoted && --nestNum == 0)
						// 当所有的<>对都关闭后，则说明Doctype解析完成
						return;
					break;
				}
			} while (!flag);
			pushOneChar(firstChar);
		} while (true);
	}

	private void pushError(String error) {
		if (error == null)
			return;
		for (int i = 0; i < error.length(); i++)
			pushOneChar(error.charAt(i));
	}

	/**
	 * 处理结束标记
	 * 
	 * @throws IOException
	 *             IO异常
	 * @throws ParserException
	 *             解析异常
	 */
	private final void parseEndTag() throws IOException, ParserException {
		readOneChar(); // 读取<,并丢掉
		readOneChar(); // 读取/,并丢掉
		tagName = readTagName().toLowerCase(); // 读取标签名
		skipWhiteSpace(); // 跳过空白
		read('>'); // 判断标签名后是否是>,不是则抛出异常
		int i = depth - 1 << 2;
		// 如果标签栈为空，说明没有与之匹配的开始标签，
		// 则放弃解析结束标签，当前内容当成注释处理
		if (depth == 0) {
			exception("element stack empty");
			type = ERROR;
			return;
		}
		// 如果结束标签名不匹配开始标签名
		// 则在标签栈中向上搜索匹配标签名
		if (!tagName.equals("br") && !tagName.equals(elementStack[i + 3])) {
			exception("expected: /" + elementStack[i + 3] + " read: " + tagName);
			error = "</" + tagName + ">";
			pushError(error);
			error = null;
			type = ERROR;
			int j;
			// 向上匹配标签栈中的标签
			for (j = i; j >= 0
					&& !tagName.equals(elementStack[j + 3].toLowerCase()); j -= 4)
				stackMismatch++; // 不匹配则不匹配标签数+1

			// 如果j不是4的倍数，则放弃解析结束标签，把当前内容当成注释
			if (j < 0) {
				stackMismatch = 0;
				type = ERROR;
				return;
			}
		}
		// 取出标签的命名空间信息和名字
		namespace = elementStack[i];
		prefix = elementStack[i + 1];
		tagName = elementStack[i + 2];
	}

	/**
	 * 获取当前解析的标签的类型
	 * 
	 * @return
	 * @throws IOException
	 */
	private final int peekType() throws IOException {
		// 判断当前的字符
		switch (peek(0)) {
		// 如果没有，则认为是文档的末尾
		case -1:
			return END_DOCUMENT;
			// 如果读到&，说明这里存在转义字符
		case ASCII_AND: // '&'
			return ENTITY_REF;
			// 如果读到<,需要根据<后的字符进一步判断到底是什么类型
		case ASCII_LT: // '<'
			switch (peek(1)) {
			// 如果<后跟着/,认为是结束标签
			case ASCII_SLASH: // '/'
				return END_TAG;
				// 如果<后跟着!或?,认为是非文档体部分
			case ASCII_EXCALMATORY_MARK: // '!'
			case ASCII_QUESTION_MARK: // '?'
				return TOP_HEAD;
			}

			// 如果是/!?以外的其他字符,则认为是开始标记
			return START_TAG;
		default:
			// switch (peek(1)) {
			// // 如果<后跟着/,认为是结束标签
			// case ASCII_SLASH: // '/'
			// return END_TAG;
			// 如果<后跟着!或?,认为是非文档体部分
			// case ASCII_EXCALMATORY_MARK: // '!'

			// case ASCII_QUESTION_MARK: // '?'
			// return TOP_HEAD;
			// }
			return TEXT;
		}
		// 如果是&<以外的其他字符，则认为是文本
		// return TEXT;
	}

	/**
	 * 从txtBuf中取出一段字符<br>
	 * 传入一个开始标记，将返回从开始标记到txtBuf结尾的这段字符串
	 * 
	 * @param startIndex
	 *            开始标记
	 * @return 从开始标记到txtBuf结尾的这段字符串
	 */
	private final String getChars(int startIndex) {
		return new String(txtBuf, startIndex, txtPos - startIndex);
	}

	/**
	 * 将一个字符压入txtBuf
	 * 
	 * @param ascii
	 *            要压入的字符的ASCII码
	 */
	private final void pushOneChar(int ascii) {
		// 如果传入的字符的ASCII码小于32，则认为是空白字符
		isWhitespace &= ascii <= ASCII_SPACE;
		// 如果textBuf已满,则扩大textBuf的容量
		if (txtPos == txtBuf.length) {
			char[] chars = new char[(txtPos * 4) / 3 + 4];
			System.arraycopy(txtBuf, 0, chars, 0, txtPos);
			txtBuf = chars;
		}
		// 压入字符
		txtBuf[txtPos++] = (char) ascii;
	}

	/**
	 * 解析开始标签
	 * 
	 * @param isHead
	 *            标记是否是文档的第一行<br>
	 *            即<?xml ... ?>这一行
	 * @throws IOException
	 * @throws ParserException
	 */
	private final void parseStartTag(boolean isHead) throws IOException,
			ParserException {
		// 如果不是第一行,则读取<,丢掉
		if (!isHead)
			readOneChar();
		tagName = readTagName().toLowerCase(); // 读取标签名
		attributeCount = 0;
		do {
			skipWhiteSpace(); // 掉过空白
			int firstChar = peek(0);
			// 如果是第一行
			if (isHead) {
				// 如果遇到?，则判断后面是否跟着>
				// 如果不是，说明没有正确关闭<?标签，抛出异常
				if (firstChar == ASCII_QUESTION_MARK) {
					readOneChar();
					read('>');
					return;
				}
			}
			// 如果不是文档的第一行
			else {
				// 如果遇到/，说明这个标签是一个简化的标签<br>
				// 即< ... />的形式
				if (firstChar == ASCII_SLASH) {
					isDegenerate = true; // 标记为简化标签
					readOneChar(); // 读取/,丢掉
					skipWhiteSpace(); // 跳过空白
					// 判断/后跟的是否是>
					// 如果不是则抛出异常
					read('>');
					break;
				}
				// 如果遇到>,则完成解析
				else if (firstChar == ASCII_GT) {
					readOneChar();
					break;
				}

			}
			// 没有关闭标签，则抛出异常
			if (firstChar == -1) {
				exception("Unexpected EOF");
				return;
			}
			// 读取属性名
			String attrName = readTagName();
			// 属性名为空则抛出异常
			if (attrName.length() == 0) {
				exception("attr name expected");
				break;
			}
			int k = attributeCount++ << 2;
			attributes = ensureCapacity(attributes, k + 4);
			attributes[k++] = "";
			attributes[k++] = null;
			attributes[k++] = attrName;
			skipWhiteSpace();
			// 如果属性名后跟的不是=，则抛出异常，默认属性值为1
			if (peek(0) != ASCII_EQUAL) {
				exception("Attr.value missing f. " + attrName);
				attributes[k] = "1";
			} else {
				read('=');
				skipWhiteSpace();
				int curChar = peek(0);
				// 如果不是'或",抛出异常
				if (curChar != ASCII_SINGLE_QUOTATION_MARKS
						&& curChar != ASCII_DOUBLE_QUOTATION_MARKS) {
					// exception("attr value delimiter missing!");
					curChar = ASCII_SPACE;
				}
				// 否则读出并抛弃
				else {
					readOneChar();
				}

				int oldPos = txtPos;
				pushText(curChar, true);
				attributes[k] = getChars(oldPos);
				txtPos = oldPos;
				if (curChar != ASCII_SPACE)
					readOneChar();
			}
		} while (true);
		if (tagName.equals("br") || tagName.equals("img")) {
			isDegenerate = false;
			return;
		}
		int j = depth++ << 2;
		elementStack = ensureCapacity(elementStack, j + 4);
		elementStack[j + 3] = tagName;
		if (depth >= nspCounts.length) {
			int ai[] = new int[depth + 4];
			System.arraycopy(nspCounts, 0, ai, 0, nspCounts.length);
			nspCounts = ai;
		}
		nspCounts[depth] = nspCounts[depth - 1];
		if (processNsp)
			adjustNsp();
		else
			namespace = "";
		elementStack[j] = namespace;
		elementStack[j + 1] = prefix;
		elementStack[j + 2] = tagName;
	}

	/**
	 * 处理转义字符
	 * 
	 * @throws IOException
	 * @throws ParserException
	 */
	private final void pushEntity() throws IOException, ParserException {
		pushOneChar(readOneChar()); // 读取并压入&
		int pos = txtPos;
		// boolean isComplete = false;
		do {
			int firstChar = readOneChar();
			// int firstChar = peek(0);
			// 遇到;
			// 说明已经完成，跳出循环
			if (firstChar == 59) {
				// isComplete = true;
				break;
			}
			// 如果遇到非法字符，退出
			if (firstChar < 128
					&& (firstChar < 48 || firstChar > 57) // 小于0大于9
					&& (firstChar < 97 || firstChar > 122) // 小于a大于z
					&& (firstChar < 65 || firstChar > 90) // 小于A大于Z
					&& firstChar != ASCII_UNDER_LINE
					&& firstChar != ASCII_MINUS && firstChar != ASCII_POUND) {
				if (!isRelaxed)
					exception("unterminated entity ref");
				if (firstChar != -1)
					pushOneChar(firstChar);
				// isComplete = false;
				return;
			}
			// 合法字符压入txtBuf
			// pushOneChar(readOneChar());
			pushOneChar(firstChar);
		} while (true);
		// 取出转义字符串，不包括&
		// if (isComplete) {
		String entityStr = getChars(pos);
		txtPos = pos - 1; // 忽略掉&
		if (token && type == ENTITY_REF)
			tagName = entityStr;
		// 如果转义字符串以#开头
		// 说明转义内容是以ASCII表示的
		if (entityStr.charAt(0) == '#') {
			// 如果#后跟有x,说明是16进制数字
			int k = entityStr.charAt(1) != 'x' ? Integer.parseInt(entityStr
					.substring(1)) : Integer.parseInt(entityStr.substring(2),
					16);
			pushOneChar(k);
			return;
		}
		// 从转义映射表中取出真实字符
		String entityChar = (String) entityMap.get(entityStr);
		// 如果为空，则标记为未处理
		unresolved = entityChar == null;
		if (unresolved) {
			if (!token)
				exception("unresolved: &" + entityStr + ";");
		} else {
			for (int l = 0; l < entityChar.length(); l++)
				pushOneChar(entityChar.charAt(l));
		}
		// }
	}

	/**
	 * 将separator之前的文字解析出来，并压入txtBuf
	 * 
	 * @param separator
	 *            分割字符的ASCII值<br>
	 *            该字符前的所有文字都要解析出来放入txtBuf
	 * @param flag
	 * @throws IOException
	 * @throws ParserException
	 */
	private final void pushText(int separator, boolean flag)
			throws IOException, ParserException {
		int firstChar = peek(0);
		int rightSquareBracketsNum = 0; // ]的数量
		// System.out.println("ok------0000000000");
		while (firstChar != -1
				&& firstChar != separator
				&& (separator != ASCII_SPACE || firstChar > ASCII_SPACE
						&& firstChar != ASCII_GT)) {
			// 遇到&，则处理转义字符
			if (firstChar == ASCII_AND) {
				// if (flag) {
				// pushEntity();
				// break;
				// } else {
				// pushOneChar(readOneChar());
				// }
				if (!flag)
					break;
				pushEntity();
			}
			// 是开始标签中的换行，则变成空格
			else if (firstChar == ASCII_LF && type == START_TAG) {
				readOneChar();
				pushOneChar(ASCII_SPACE);
			} else {
				int m = readOneChar();
				pushOneChar(m);
			}
			// 如果遇到，并且]的数量超过1个，则抛出错误
			if (firstChar == ASCII_GT && rightSquareBracketsNum >= 2
					&& separator != ASCII_RIGHT_SQUARE_BRACKETS)
				exception("Illegal: ]]>");
			// 如果遇到]，]数+1
			if (firstChar == ASCII_RIGHT_SQUARE_BRACKETS)
				rightSquareBracketsNum++;
			else
				rightSquareBracketsNum = 0;

			firstChar = peek(0);
			// System.out.println("ok------:"+firstChar);
		}
		// for (; firstChar != -1
		// && firstChar != separator
		// && (separator != ASCII_SPACE || firstChar > ASCII_SPACE
		// && firstChar != ASCII_GT); firstChar = peek(0)) {
		//
		// }
	}

	/**
	 * 读取指定字符<br>
	 * 实际上这个方法的最终目的不是将制定字符读取出来，<br>
	 * 而是判断当前字符是否是指定的字符.<br>
	 * 如果不是则抛出解析异常
	 * 
	 * @param c
	 *            指定字符
	 * @throws IOException
	 *             IO异常
	 * @throws ParserException
	 *             解析异常
	 */
	private final void read(char c) throws IOException, ParserException {
		int i = readOneChar();
		if (i != c)
			exception("expected: '" + c + "' actual: '" + (char) i + "'");
	}

	/**
	 * 从peek数组中弹出最近的字符
	 * 
	 * @return 最近的字符
	 * @throws IOException
	 */
	private final int readOneChar() throws IOException {
		int i;
		if (peekCount == 0) {
			i = peek(0);
		} else {
			// 否则取出第一个字符
			i = peek[0];
			// 左移一个字符
			peek[0] = peek[1];
		}
		peekCount--; // 数-1
		column++; // 列数+1
		// 如果是换行符，处理换行
		if (i == ASCII_LF) {
			line++;
			column = 1;
		}
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#getTotalSize()
	 */
	public int getTotalSize() {
		// System.out.println("totalSize = " + totalSize);
		return totalSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#isBlocked()
	 */
	public boolean isBlocked() {
		return isBlocked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#cancel()
	 */
	public void cancel() {
		if (isRunning)
			isRunning = false;
		type = IXmlParser.START_DOCUMENT;
		totalSize = 0;
		try {
			if (this.reader != null)
				this.reader.close();
		} catch (Exception e) {
		} finally {
			this.reader = null;
		}
	}

	/**
	 * 返回peek数组中指定的位置的字符<br>
	 * 如果指定位置大于peek数组的长度，<br>
	 * 则从原始文件中读取最近的一组数据,放入peek数组
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private final int peek(int index) throws IOException {
		while (index >= peekCount) {
			int curChar = 0;
			// 如果srcBuf的长度<=1，则从reader中读取当前字符的ASCII码
			if (srcBuf.length <= 1) {
				if (!isRunning)
					throw new IOException();

				isBlocked = true;
				curChar = reader.read();
				// log.out(String.valueOf((char) curChar));
				isBlocked = false;

				if (!isRunning)
					throw new IOException();
			}
			// 如果srcBuf当前指针小于srcBuf长度，则直接取出scrBuf的下一个字符
			else if (srcPos < srcCount) {
				curChar = srcBuf[srcPos++];
			}
			// 如果scrBuf中没有还要使用的数据，则读出下一组数据放入scrBuf
			else {
				// if (!isRunning)
				// throw new IOException();

				isBlocked = true;
				try {
					srcCount = reader.read(srcBuf, 0, srcBuf.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
				isBlocked = false;

				// if (!isRunning)
				// throw new IOException();
				// 当前字符为srcBuf的第一个字符
				if (srcCount <= 0)
					curChar = -1;
				else
					curChar = srcBuf[0];
				srcPos = 1;
			}
			// 如果遇到换行，变成空格
			if (curChar == ASCII_CR) {
				totalSize += 1;
				wasCR = true;
				peek[peekCount++] = ASCII_SPACE;
			} else {
				// 如果遇到回车，并且没有换行，则变成空格
				if (curChar == ASCII_LF) {
					totalSize += 1;
					if (!wasCR)
						peek[peekCount++] = ASCII_SPACE;
				}
				// 其他字符都放入peek数组
				else {
					peek[peekCount++] = curChar;
					if (curChar != -1) {
						// 由于中英文字符的大小不同，需要分别统计
						if (curChar > 255)
							totalSize += 3;
						else
							totalSize += 1;
					}
				}
				wasCR = false;
			}
		}
		return peek[index];
	}

	/**
	 * 读取标签名称
	 * 
	 * @return 标签名称
	 * @throws IOException
	 * @throws ParserException
	 */
	private final String readTagName() throws IOException, ParserException {
		int oldTxtPos = txtPos;
		int firstChar = peek(0);
		// 判断标签名称是否合法
		if ((firstChar < 97 || firstChar > 122) // a>fistChar>z
				&& (firstChar < 65 || firstChar > 90) // A>fistChar>Z
				&& firstChar != ASCII_UNDER_LINE
				&& firstChar != ASCII_COLON
				&& firstChar < 192 && !isRelaxed)
			exception("name expected");
		// 读出标签名称，放入txtBuf
		do {
			pushOneChar(readOneChar());
			firstChar = peek(0);
		} while (firstChar >= 97
				&& firstChar <= 122 // a<fistChar<z
				|| firstChar >= 65
				&& firstChar <= 90 // A<fistChar<Z
				|| firstChar >= 48
				&& firstChar <= 57 // 0<fistChar<9
				|| firstChar == ASCII_UNDER_LINE || firstChar == ASCII_MINUS
				|| firstChar == ASCII_COLON || firstChar == ASCII_DOT
				|| firstChar >= 183);
		// 从txtBuf中取出标签名
		String tagName = getChars(oldTxtPos);
		// 将txtBuf的顶置回原来的位置
		txtPos = oldTxtPos;
		return tagName;
	}

	/**
	 * 跳过文档中的空白
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	private final void skipWhiteSpace() throws IOException {
		do {
			int firstChar = peek(0);
			// 如果是空白，则跳过，读取下一个字符
			if (firstChar <= ASCII_SPACE && firstChar != -1)
				readOneChar();
			else
				return;
		} while (true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#setInput(java.io.Reader)
	 */
	public void setInput(Reader reader) throws ParserException {
		this.reader = reader;
		encoding = null;
		version = null;
		standalone = false;
		line = 1;
		column = 0;
		totalSize = 0;
		srcPos = 0;
		srcCount = 0;
		peekCount = 0;
		depth = 0;
		reset();
	}

	public void reset() {
		type = IXmlParser.START_DOCUMENT;
		tagName = null;
		namespace = null;
		isDegenerate = false;
		attributeCount = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#setInput(java.io.InputStream,
	 * java.lang.String)
	 */

	public void setInput(InputStream inputstream, String encodeStr)
			throws ParserException {
		srcPos = 0;
		// srcCount = 0;
		encoding = encodeStr;
		if (encoding == null)
			encoding = "UTF-8";
		// String encode = encodeStr;
		if (inputstream == null)
			throw new ParserException("InputStream is null!");
		try {
			// label0: {
			// int binaryCode; // 用于记录4字节的二进制编码形式
			// label1: {
			// // 如果没有显式地给出encode，则跳出处理，默认为UTF-8
			// if (encode != null)
			// break label0;
			// binaryCode = 0;
			// // 读取4个字符
			// do {
			// if (srcCount >= 4)
			// break;
			// int newChar = inputstream.read();
			// if (newChar == -1)
			// break;
			// // 左移8位，用新字节填充最后8位
			// binaryCode = binaryCode << 8 | newChar;
			// srcBuf[srcCount++] = (char) newChar;
			// } while (true);
			// // 如果读出的字符不是4个，说明输入流中不足4个字符
			// // 存在异常，跳出处理，默认为UTF-8
			// if (srcCount != 4)
			// break label0;
			// // 根据4字节的编码形式，确定编码格式
			// switch (binaryCode) {
			// case 65279:
			// encode = "UTF-32BE";
			// srcCount = 0;
			// break label0;
			// case -131072:
			// encode = "UTF-32LE";
			// srcCount = 0;
			// break label0;
			// case ASCII_LT: // '<'
			// encode = "UTF-32BE";
			// srcBuf[0] = '<';
			// srcCount = 1;
			// break label0;
			// case 1006632960:
			// encode = "UTF-32LE";
			// srcBuf[0] = '<';
			// srcCount = 1;
			// break label0;
			// case 3932223:
			// encode = "UTF-16BE";
			// srcBuf[0] = '<';
			// srcBuf[1] = '?';
			// srcCount = 2;
			// break label0;
			// case 1006649088:
			// encode = "UTF-16LE";
			// srcBuf[0] = '<';
			// srcBuf[1] = '?';
			// srcCount = 2;
			// break label0;
			// case 1010792557: // 从第一行中读取encoding信息
			// int tempChar;
			// do {
			// tempChar = inputstream.read();
			// if (tempChar == -1)
			// break label1;
			// srcBuf[srcCount++] = (char) tempChar;
			// } while (tempChar != ASCII_GT); // '>'
			// String tempStr = new String(srcBuf, 0, srcCount);
			// int pos = tempStr.indexOf("encoding");
			// if (pos != -1) {
			// for (; tempStr.charAt(pos) != '"'
			// && tempStr.charAt(pos) != '\''; pos++)
			// ;
			// char c = tempStr.charAt(pos++);
			// int j1 = tempStr.indexOf(c, pos);
			// encode = tempStr.substring(pos, j1);
			// }
			// tempStr = null;
			// break;
			// }
			// }
			//
			// if ((binaryCode & 0xffff0000) == 0xfeff0000) {
			// encode = "UTF-16BE";
			// srcBuf[0] = (char) (srcBuf[2] << 8 | srcBuf[3]);
			// srcCount = 1;
			// } else if ((binaryCode & 0xffff0000) == 0xfffe0000) {
			// encode = "UTF-16LE";
			// srcBuf[0] = (char) (srcBuf[3] << 8 | srcBuf[2]);
			// srcCount = 1;
			// } else if ((binaryCode & 0xffffff00) == 0xefbbbf00) {
			// encode = "UTF-8";
			// srcBuf[0] = srcBuf[3];
			// srcCount = 1;
			// }
			// }

			// int j = srcCount;

			if (null == reader) {
				reader = (Reader) (new InputStreamReader(inputstream, encoding));
			}
			setInput(((Reader) (new InputStreamReader(inputstream, encoding))));
			setInput(reader);
			// srcCount = j;
		} catch (Exception e) {
			throw new ParserException("Invalid stream or encoding: "
					+ e.toString(), this, e);
		}
	}

	/**
	 * @param index
	 * @return
	 */
	private int getNamespaceCount(int index) {
		if (index > depth)
			throw new IndexOutOfBoundsException();
		else
			return nspCounts[index];
	}

	/**
	 * 从命名空间中栈中获取命名空间<br>
	 * 如果传入的值为"xml"或"xmlns"，则返回固定的命名空间
	 * 
	 * @param ns
	 *            命名空间
	 * @return
	 */
	private String getNamespace(String ns) {
		if ("xml".equals(ns))
			return "http://www.w3.org/XML/1TOP_HEAD/namespace";
		if ("xmlns".equals(ns))
			return "http://www.w3.org/2000/xmlns/";
		for (int i = (getNamespaceCount(depth) << 1) - 2; i >= 0; i -= 2) {
			if (ns == null) {
				if (nspStack[i] == null)
					return nspStack[i + 1];
				continue;
			}
			if (ns.equals(nspStack[i]))
				return nspStack[i + 1];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#getPositionDescription()
	 */
	public String getPositionDescription() {
		StringBuffer stringbuffer = new StringBuffer(
				type >= TYPES.length ? "unknown" : TYPES[type]);
		stringbuffer.append(' ');
		if (type == 2 || type == 3) {
			if (isDegenerate)
				stringbuffer.append("(empty) ");
			stringbuffer.append('<');
			if (type == 3)
				stringbuffer.append('/');
			if (prefix != null)
				stringbuffer.append("{" + namespace + "}" + prefix + ":");
			stringbuffer.append(tagName);
			int i = attributeCount << 2;
			for (int j = 0; j < i; j += 4) {
				stringbuffer.append(' ');
				if (attributes[j + 1] != null)
					stringbuffer.append("{" + attributes[j] + "}"
							+ attributes[j + 1] + ":");
				stringbuffer.append(attributes[j + 2] + "='"
						+ attributes[j + 3] + "'");
			}

			stringbuffer.append('>');
		} else if (type != 7)
			if (type != 4)
				stringbuffer.append(getText());
			else if (isWhitespace) {
				stringbuffer.append("(whitespace)");
			} else {
				String s = getText();
				if (s.length() > 16)
					s = s.substring(0, 16) + "...";
				stringbuffer.append(s);
			}
		stringbuffer.append("@" + line + ":" + column);
		if (location != null) {
			stringbuffer.append(" in ");
			stringbuffer.append(location);
		} else if (reader != null) {
			stringbuffer.append(" in ");
			stringbuffer.append(reader.toString());
		}
		return stringbuffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#getLineNumber()
	 */
	public int getLineNumber() {
		return line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#getColumnNumber()
	 */
	public int getColumnNumber() {
		return column;
	}

	public boolean isWhitespace() throws ParserException {
		if (type != TEXT && type != IGNORABLE_WHITESPACE && type != CDSECT)
			exception("Wrong event type");
		return isWhitespace;
	}

	public String getText() {
		return type == ERROR || type >= TEXT
				&& (type != ENTITY_REF || !unresolved) ? getChars(0) : null;
	}

	// public String getNamespace() {
	// return namespace;
	// }

	public String getName() {
		return tagName;
	}

	public int getAttributeNum() {
		return attributeCount;
	}

	public String getAttributeName(int i) {
		if (i >= attributeCount)
			throw new IndexOutOfBoundsException();
		else
			return attributes[(i << 2) + 2];
	}

	public String getAttributeValue(int i) {
		if (i >= attributeCount)
			throw new IndexOutOfBoundsException();
		else
			return attributes[(i << 2) + 3];
	}

	// public String getAttributeValue(String s, String s1) {
	// for (int i = (attributeCount << 2) - 4; i >= 0; i -= 4)
	// if (attributes[i + 2].equals(s1)
	// && (s == null || attributes[i].equals(s)))
	// return attributes[i + 3];
	//
	// return null;
	// }

	public int getEventType() throws ParserException {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.xmlparser.IXmlParser#next()
	 */
	public int next() throws ParserException, IOException {
		txtPos = 0;
		isWhitespace = true;
		int tempType = 9999;
		token = false;
		do {
			nextImpl();
			if (type < tempType)
				tempType = type;
		} while (tempType > ENTITY_REF || tempType >= TEXT
				&& peekType() >= TEXT);
		type = tempType;
		if (type > TEXT)
			type = TEXT;
		return type;
	}

	// public String getEncoding() {
	// return encoding;
	// }

	public String getVersion() {
		return version;
	}

	public boolean getStandalone() {
		return standalone;
	}

	public int getDepth() {
		return this.depth;
	}
}
