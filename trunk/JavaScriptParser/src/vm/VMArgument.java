package vm;

import utils.ArrayList;
import vm.object.VMObject;
import vm.object.nativeobject.FunctionObject;

public class VMArgument {
	private FunctionObject function;
	private VMObject context;
	private FunctionObject caller = null;
	private ArrayList arguments = new ArrayList(3);

	/**
	 * Creates a new arguments object.
	 * 
	 * @param fn
	 *            the function this arguments object holds the parameters for
	 * @param context
	 *            the evaluation context
	 */
	public VMArgument(FunctionObject fn, VMObject context) {
		this.function = fn;
		this.context = context;
	}

	public void setCaller(FunctionObject function) {
		this.function = function;
	}

	public void addArgument(Object obj) {
		arguments.add(obj);
	}
}
