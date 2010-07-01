package vm.object.nativeobject;

import vm.object.VMObject;

/**
 * <code>ErrorObject</code> 是JavaScript中的错误对象.
 * 实际JavaScript中有很多种错误对象,用于区分不同的错误类型.再次我们将错误类型统一成一个{@link ErrorObject},
 * 错误类型可以通过具体错误信息来区分.
 * 
 * @author Jarod Yv
 * 
 */
public class ErrorObject extends VMObject {

	public static final int ID_INIT_ERROR = 0x30;// 构造函数

	public static ErrorObject ERROR_PROTOTYPE = new ErrorObject();

	public ErrorObject() {
		super(OBJECT_PROTOTYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.value.toString();
	}
}
