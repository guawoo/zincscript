package vm;

import vm.object.VMObject;
import vm.object.nativeobject.ErrorObject;

/**
 * <code>VMRuntimeException</code> 对JavaScript运行期间抛出的异常({@link ErrorObject}继承自
 * {@link VMObject}, 而非{@link Exception} )进行了一层封装, 使运行时抛出的异常可以被捕获.
 * 
 * @author Jarod Yv
 */
public class VMRuntimeException extends RuntimeException {
	private ErrorObject error = null;
	public int pc = -1;
	public int lineNumber = -1;

	/**
	 * 根据传入的对象生成相应的异常
	 * 
	 * @param e
	 */
	public VMRuntimeException(Object e) {
		if (e instanceof Exception) {
			error = new ErrorObject();
		} else if (e instanceof ErrorObject) {
			error = (ErrorObject) e;
		} else {
			error = new ErrorObject();
		}
	}

	/**
	 * 获取错误对象
	 * 
	 * @return {@link #error}
	 */
	public ErrorObject getError() {
		return error;
	}

	/**
	 * 获取异常发生的行号
	 * 
	 * @return {@link #lineNumber}
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return lineNumber + "行发生错误  (pc:0x" + Integer.toHexString(pc) + "): "
				+ error;
	}
}
