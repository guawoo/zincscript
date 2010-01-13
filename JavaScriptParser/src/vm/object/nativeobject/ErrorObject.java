package vm.object.nativeobject;

import vm.object.VMObject;

public class ErrorObject extends VMObject {
	static ErrorObject ERROR_PROTOTYPE = new ErrorObject();

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
