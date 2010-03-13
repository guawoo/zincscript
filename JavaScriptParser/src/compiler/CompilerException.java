package compiler;

/**
 * <code>CompilerException</code> 对编译过程中出现的异常进行了封装，抛出一些必要的信息方便Debug
 * 
 * @author Jarod Yv
 */
public class CompilerException extends Exception {
	private Exception exception = null;

	public CompilerException(Exception exception) {
		super();
		this.exception = exception;
	}

	public CompilerException(String message) {
		super(message);
	}

	public String getMessage() {
		if (exception != null) {
			return exception.getMessage();
		} else {
			return null;
		}
	}

	public void printStackTrace() {
		if (exception != null) {
			exception.printStackTrace();
		} else {
			super.printStackTrace();
		}
	}
}
