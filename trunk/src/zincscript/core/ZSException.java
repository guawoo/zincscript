package zincscript.core;

/**
 * <code>ZSException</code>是自定义异常类
 * 
 * @author Jarod Yv
 */
public class ZSException extends Exception {

	/**
	 * 语法异常
	 */
	public static final byte SYNTAX_EXCEPTION = 0x00;
	/**
	 * 解释执行
	 */
	public static final byte INTERPRETE_EXCEPTION = 0x01;
	/**
	 * 当遇到return关键字时,抛出RETURN_EXCEPTION可以立即从当前执行函数中跳出,返回返回值
	 */
	public static final byte RETURN_EXCEPTION = 0x02;
	/**
	 * 中断当前脚本的执行,用于动态装在脚本
	 */
	public static final byte INTERRUPTION_EXCEPTION = 0x03;
	/**
	 * 算术运算错误
	 */
	public static final byte ARITHMETIC_EXCEPTION = 0x04;

	private byte type = 0;// 异常的类型

	public byte getType() {
		return type;
	}

	public ZSException() {
	}

	public ZSException(byte type) {
		this.type = type;
	}

	public ZSException(String msg) {
		super(msg);
		this.type = INTERPRETE_EXCEPTION;
	}

	public ZSException(byte type, String msg) {
		super(msg);
		this.type = type;
	}
}
