package zincfish.zincparser.xmlparser;

/**
 * 解析异常类<br>
 * 为了方便调试而派生出的一个自定义异常类。<br>
 * 能够在解析过程出现异常时输出出错的具体位置，方便定位xml文件的内容。
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public final class ParserException extends Exception {

	/**
	 * 详细错误信息
	 */
	private Throwable throwable;

	public ParserException(String msg) {
		super(msg);
	}

	public ParserException(String msg, IXmlParser parser, Throwable throwable) {
		super((msg != null ? msg + " " : "")
				+ (parser != null ? "(position:"
						+ parser.getPositionDescription() + ") " : "")
				+ (throwable != null ? "caused by: " + throwable : ""));
		this.throwable = throwable;
	}

	public void printStackTrace() {
		if (throwable == null)
			super.printStackTrace();
		else
			synchronized (System.err) {
				System.err.println(super.getMessage()
						+ "; nested exception is:");
				throwable.printStackTrace();
			}
	}
}
