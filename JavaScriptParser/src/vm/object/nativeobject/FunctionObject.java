package vm.object.nativeobject;

import vm.object.VMObject;

public class FunctionObject extends VMObject {
	/** 函数接受参数的个数; -1表示本地getter/setter方法 */
	public int expectedParameterCount;

	/** 本地变量个数 */
	public int varCount;

	/** 函数的具体执行代码 */
	public byte[] byteCode;

	/** 本地方法索引 */
	public int index;

	/** 本地变量名列表 */
	public String[] localVariableNames;

	/** 字符串定义列表，用于向栈中压入字符串 */
	public String[] stringLiterals;

	/** 函数定义列表，用于向栈中压入函数对象 */
	public FunctionObject[] functionLiterals;

	/** 胡子定义列表，用于向栈中压入数字 */
	public double[] numberLiterals;

	/**
	 * Prototype object if this function is a constructor. Currently not used;
	 * required to implement the JS prototype property.
	 */
	private VMObject prototype;

	/** 如果该函数拥有本地构造函数，则factoryTypeId是该构造函数在工厂中的索引 */
	public int factoryTypeId;

	/** 该函数的运行环境 */
	public VMObject context;

	public int[] lineNumbers;

	/**
	 * 构造函数
	 * 
	 * @param index
	 *            内部函数索引
	 * @param parameterCount
	 *            期望参数数量
	 */
	public FunctionObject(int index, int parameterCount) {
		super(OBJECT_PROTOTYPE);
		this.index = index;
		this.expectedParameterCount = parameterCount;
	}

	/**
	 * 根据跟定的函数定义封装出新的函数对象
	 * 
	 * @param literal
	 *            函数对象
	 * @param context
	 *            运行环境
	 */
	public FunctionObject(FunctionObject literal, VMObject context) {
		super(literal.prototype);
		this.byteCode = literal.byteCode;
		this.context = context;
		this.functionLiterals = literal.functionLiterals;
		this.localVariableNames = literal.localVariableNames;
		this.numberLiterals = literal.numberLiterals;
		this.expectedParameterCount = literal.expectedParameterCount;
		this.prototype = literal.prototype;
		this.stringLiterals = literal.stringLiterals;
		this.varCount = literal.varCount;
		// this.factory = JsSystem.getInstance();
		// this.factoryTypeId = JsSystem.FACTORY_ID_OBJECT;
		this.lineNumbers = literal.lineNumbers;
	}

	public int getLineNumber(int pc) {
		if (lineNumbers != null && lineNumbers.length > 0) {
			int i = 0;
			while (i + 2 < lineNumbers.length && lineNumbers[i + 2] <= pc) {
				i += 2;
			}
			return lineNumbers[i + 1];
		}
		return -1;
	}
}
