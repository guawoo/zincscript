package parser;

/**
 * <code>Config</code> 中配置了解析相关的一些参数和开关，主要用于控制解析器debug参数。
 * 
 * @author Jarod Yv
 */
public class Config {

	/**
	 * 启动所有debug
	 */
	public static final boolean DEBUG_ALL = false;

	/**
	 * 控制是否在执行前显示输出源代码
	 */
	public static final boolean DEBUG_SOURCE = DEBUG_ALL | false;

	/**
	 * 控制是否在执行前显示输出抽象语法树
	 */
	public static final boolean DEBUG_PARSETREE = DEBUG_ALL | false;

	/**
	 * 控制是否在执行前显示输出bytecode
	 */
	public static final boolean DEBUG_DISSASSEMBLY = DEBUG_ALL | false;

	/**
	 * 控制是否支持快速本地变量
	 */
	public static final boolean FASTLOCALS = true;

	/**
	 * 控制是否支持行号输出
	 */
	public static final boolean LINENUMBER = true;
}
