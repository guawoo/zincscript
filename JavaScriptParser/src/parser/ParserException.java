package parser;

/**
 * <code>ParserException</code> 对解析过程中出现的异常进行了封装，抛出一些必要的信息方便Debug
 * 
 * @author Jarod Yv
 */
public class ParserException extends Exception {
	private Exception exception = null;
	private int lineNumber = 0;

	public ParserException(String message) {
		super(message);
	}

	public ParserException(String message, int lineNumber) {
		super(message);

		this.lineNumber = lineNumber;
	}

	public ParserException(Exception exception) {
		super();

		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	public int getLineNumber() {
		if (lineNumber > 0) {
			return lineNumber;
		} else if (exception != null && exception instanceof ParserException) {
			return ((ParserException) exception).getLineNumber();
		} else {
			return 0;
		}
	}

	public String getMessage() {
		String message = super.getMessage() + " @ line: " + lineNumber;

		if (message != null) {
			return message;
		} else if (exception != null) {
			return exception.getMessage();
		} else {
			return null;
		}
	}

	public void printStackTrace() {
		super.printStackTrace();

		if (exception != null) {
			System.err.print("caused by:");
			exception.printStackTrace();
		}
	}
}
