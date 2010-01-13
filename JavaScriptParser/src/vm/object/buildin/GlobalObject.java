package vm.object.buildin;

import vm.object.VMObject;
import vm.object.nativeobject.FunctionObject;
import vm.object.nativeobject.NumberObject;
import vm.object.nativeobject.StringObject;

public class GlobalObject extends VMObject {
	private static final int ID_PARSE_INT = 10;
	private static final int ID_PARSE_FLOAT = 11;
	private static final int ID_IS_NAN = 12;
	private static final int ID_IS_FINITE = 13;
	private static final int ID_DECODE_URI = 14;
	private static final int ID_DECODE_URI_COMPONENT = 15;
	private static final int ID_ENCODE_URI = 16;
	private static final int ID_ENCODE_URI_COMPONENT = 17;
	private static final int ID_PRINT = 18;

	public static final GlobalObject GLOBAL_PROTOTYPE = new GlobalObject();
	static {
		GLOBAL_PROTOTYPE.addProperty("Object", new FunctionObject(
				FACTORY_ID_OBJECT, VMObject.OBJECT_PROTOTYPE,
				VMObject.ID_INIT_OBJECT, 1));
		GLOBAL_PROTOTYPE.addProperty("String", new FunctionObject(
				FACTORY_ID_STRING, StringObject.STRING_PROTOTYPE,
				ID_INIT_STRING, 1));
		GLOBAL_PROTOTYPE.addProperty("Function", new FunctionObject(
				FACTORY_ID_FUNCTION, FunctionObject.FUNCTION_PROTOTYPE,
				Object.ID_INIT_FUNCTION, 1).addProperty("fromCharCode",
				new JsFunctionObject(JsObject.ID_FROM_CHAR_CODE, 1)));
		GLOBAL_PROTOTYPE.addProperty("Number",
				new FunctionObject(
						FACTORY_ID_NUMBER,
						NumberObject.NUMBER_PROTOTYPE,
						VMObject.ID_INIT_NUMBER, 1).addVar("MAX_VALUE",
						new Double(Double.MAX_VALUE)).addVar(
						"MIN_VALUE", new Double(Double.MIN_VALUE))
						.addVar("NaN", NAN).addVar("NEGATIVE_INFINITY",
								new Double(Double.NEGATIVE_INFINITY))
						.addVar("POSITIVE_INFINITY", INFINITY)))
	}

	public GlobalObject() {
		super(OBJECT_PROTOTYPE);
	}
}
