package parser;

/**
 * <code>ParserException</code> 对解析过程中出现的异常进行了封装，抛出一些必要的信息方便Debug
 * 
 * @author Jarod Yv
 */
public class ParserException extends Exception {
	private int lineNumber = 0;

	public ParserException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMessage() {
		return super.getMessage() + " @ line: " + lineNumber;
	}
}
