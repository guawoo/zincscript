package vm.object.nativeobject;

import vm.VMStack;
import vm.object.VMObject;

/**
 * <code>FunctionObject</code> 是JavaScript中的函数对象.
 * JavaScript中所有数据都是对象,连函数也被看作一个对象,可以像其他对象一样生成和传递.
 * 甚至一个JavaScript程序也可以被看作是一个函数集合对象.
 * <p>
 * 函数可以说是JavaScript中可执行的单元,任何代码要想运行需要封装进一个函数, 因此函数对象与其他对象不同,函数对象需要负责维护运行时的临时数据.
 * 
 * @author Jarod Yv
 * 
 */
public class FunctionObject extends VMObject {

	public static final int ID_INIT_FUNCTION = 0x60;

	private static final int ID_PROTOTYPE = 0x61;
	private static final int ID_LENGTH = 0x62;
	private static final int ID_APPLY = 0x63;

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

	public static final FunctionObject FUNCTION_PROTOTYPE = new FunctionObject();
	static {
		FUNCTION_PROTOTYPE.addProperty("prototype", new FunctionObject(
				ID_PROTOTYPE, -1));
		FUNCTION_PROTOTYPE.addProperty("length", new FunctionObject(ID_LENGTH,
				-1));
		FUNCTION_PROTOTYPE.addProperty("apply",
				new FunctionObject(ID_APPLY, -1));
	}

	public FunctionObject() {
		super(OBJECT_PROTOTYPE);
	}

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
	 * Constructor for constructors implemented in Java
	 * 
	 * @param factory
	 *            factory instance that is able to create new objects
	 * @param factoryTypeId
	 *            instance type id, used by the factory
	 * @param prototype
	 *            the prototype object
	 * @param index
	 *            the ID of the function call, used in evalNative
	 * @param parCount
	 */
	public FunctionObject(int factoryTypeId, VMObject prototype, int nativeId,
			int parCount) {
		this(nativeId, parCount);
		this.prototype = prototype;
		this.factoryTypeId = factoryTypeId;
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

	public void evalNative(int id, VMStack stack, int sp, int pc) {
		switch (id) {
		case ID_PROTOTYPE:
			stack.setObject(sp, prototype);
			break;

		case ID_LENGTH:
			stack.setNumber(sp, pc);
			break;

		case ID_APPLY:
			throw new RuntimeException("NYI");

		default:
			super.evalNative(id, stack, sp, pc);
		}
	}
}
