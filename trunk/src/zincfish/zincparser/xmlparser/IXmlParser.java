package zincfish.zincparser.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * XML解析器接口
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public interface IXmlParser {
	public static final String NO_NAMESPACE = "";

	public static final int START_DOCUMENT = 0;

	public static final int END_DOCUMENT = 1;

	public static final int START_TAG = 2;

	public static final int END_TAG = 3;

	public static final int TEXT = 4;

	public static final int CDSECT = 5;

	public static final int ENTITY_REF = 6;

	public static final int IGNORABLE_WHITESPACE = 7;

	public static final int PROCESSING_INSTRUCTION = 8;

	public static final int COMMENT = 9;

	public static final int DOCDECL = 10;

	public static final int ERROR = 11;

	public static final int TOP_HEAD = 998;

	public static final String TYPES[] = { "START_DOCUMENT", "END_DOCUMENT",
			"START_TAG", "END_TAG", "TEXT", "CDSECT", "ENTITY_REF",
			"IGNORABLE_WHITESPACE", "PROCESSING_INSTRUCTION", "COMMENT",
			"DOCDECL" };

	/**
	 * 设置解析器的reader
	 * 
	 * @param reader
	 *            Reader
	 * @throws ParserException
	 *             解析异常
	 */
	public abstract void setInput(Reader reader) throws ParserException;

	/**
	 * 重载方法 -- 设置解析器的Reader<br>
	 * 
	 * @see #setInput(Reader)
	 * @param inputstream
	 *            输入流
	 * @param encodeStr
	 *            编码格式
	 * @throws ParserException
	 *             解析异常
	 */
	public abstract void setInput(InputStream inputstream, String encodeStr)
			throws ParserException;

	/**
	 * 获取当前解析的位置
	 * 
	 * @return 位置信息
	 */
	public abstract String getPositionDescription();

	/**
	 * <em>!For Debug Use Only! 正式发布时可去掉</em><br>
	 * 获取当前解析位置的行号
	 * 
	 * @return 当前解析位置的行号
	 */
	public abstract int getLineNumber();

	/**
	 * <em>!For Debug Use Only! 正式发布时可去掉</em><br>
	 * 获取当前解析位置的列号
	 * 
	 * @return 当前解析位置的列号
	 */
	public abstract int getColumnNumber();

	/**
	 * 判断是否是空白字符
	 * 
	 * @return 是-true；否-false
	 * @throws ParserException
	 *             解析异常
	 */
	public abstract boolean isWhitespace() throws ParserException;

	public abstract String getName();

	/**
	 * 获取解析的文字
	 * 
	 * @return 文字
	 */
	public abstract String getText();

	/**
	 * 获取一个封闭的标签中属性的个数
	 * 
	 * @return 属性的个数
	 */
	public abstract int getAttributeNum();

	/**
	 * 获取属性名
	 * 
	 * @param i
	 *            属性在一组属性中的位置
	 * @return 属性名
	 */
	public abstract String getAttributeName(int i);

	/**
	 * 获取属性值
	 * 
	 * @param i
	 *            属性在一组属性中的位置
	 * @return 属性值
	 */
	public abstract String getAttributeValue(int i);

	/**
	 * 获取标签的类型
	 * 
	 * @return 标签的类型
	 * @throws ParserException
	 *             解析异常
	 */
	public abstract int getEventType() throws ParserException;

	/**
	 * 获取下一个标签
	 * 
	 * @return 下一个标签的类型
	 * @throws ParserException
	 *             解析异常
	 * @throws IOException
	 *             IO异常
	 */
	public abstract int next() throws ParserException, IOException;

	/**
	 * 获取解析完成部分的字节数<br>
	 * 该方法用于在解析过程中返回解析完成的数量,绘制进度.
	 * 
	 * @return 已经解析完成部分的字节数
	 */
	public abstract int getTotalSize();

	/**
	 * 标记当前的解析是否处于阻塞状态<br>
	 * <li>如果处于阻塞状态,则不允许用户中断解析<br> <li>
	 * 如果处于非阻塞状态,当用户取消解析时(通过<b>cancel()</b>方法),可以马上停止解析
	 * 
	 * @return 阻塞状态 true = 阻塞;false = 非阻塞
	 */
	public abstract boolean isBlocked();

	public abstract int getDepth();

	/**
	 * 取消解析. 该方法有两个调用时机:
	 * <ol>
	 * <li>解析完成时调用
	 * <li>用户主动中断解析任务
	 * </ol>
	 * 第2中情况需要在非阻塞状态下才能中断解析
	 */
	public abstract void cancel();

	public abstract void reset();
}
