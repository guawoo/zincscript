package vm.object.nativeobject;

import vm.object.VMObject;

public class FunctionObject extends VMObject {
	/** native method index if this function is implemented in Java */
	int index;
	/** Number of declared parameters; -1 for native getter/setter */
	int expectedParameterCount;

	/**
	 * Constructor for functions implemented in Java.
	 */
	public FunctionObject(int index, int parCount) {
		super(FUNCTION_PROTOTYPE);
		this.index = index;
		this.expectedParameterCount = parCount;
	}
}
