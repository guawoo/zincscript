package vm.object.nativeobject;

import vm.object.VMObject;

public class FunctionObject extends VMObject {
	/** Number of declared parameters; -1 for native getter/setter */
	public int expectedParameterCount;

	/** Number of local variables */
	public int varCount;

	/** Byte code containing the implementation of this function */
	public byte[] byteCode;

	/** native method index if this function is implemented in Java */
	int index;

	public String[] localNames;

	/** String literal table, used when putting strings on the stack. */
	public String[] stringLiterals;

	/** function literal table, used when putting strings on the stack. */
	public FunctionObject[] functionLiterals;

	/** number literal table, used when putting strings on the stack. */
	public double[] numberLiterals;

	/**
	 * Prototype object if this function is a constructor. Currently not used;
	 * required to implement the JS prototype property.
	 */
	private VMObject prototype;

	/** Object factory id if this is a native constructor. */
	private int factoryTypeId;

	/** Evaluation context for this function. */
	private VMObject context;

	public int[] lineNumbers;

	/**
	 * Constructor for functions implemented in Java.
	 */
	public FunctionObject(int index, int parCount) {
		super(OBJECT_PROTOTYPE);
		this.index = index;
		this.expectedParameterCount = parCount;
	}

	private int getLineNumber(int pc) {
		if (lineNumbers != null && lineNumbers.length > 0) {
			int i = 0;
			while (i + 2 < lineNumbers.length && lineNumbers[i + 2] <= pc) {
				i += 2;
			}
			return lineNumbers[i + 1];
		}
		return -1;
	}

	/**
	 * Returns the number of expected (declared) parameters.
	 */
	public int getParameterCount() {
		return expectedParameterCount;
	}
}
