package vm.object.nativeobject;

import vm.object.VMObject;

public class BooleanObject extends VMObject {
	/**
	 * 
	 */
	public static final BooleanObject BOOLEAN_PROTOTYPE = new BooleanObject();

	/**
	 * 构造函数
	 */
	public BooleanObject() {
		super(OBJECT_PROTOTYPE);
	}
}
