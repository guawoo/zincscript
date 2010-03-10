package vm.object.buildin;

import javax.xml.parsers.FactoryConfigurationError;

import vm.object.VMObject;
import vm.object.VMObjectFactory;
import vm.object.nativeobject.BooleanObject;
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
	public static final Double NAN = new Double(Double.NaN);
	public static final Double INFINITY = new Double(Double.POSITIVE_INFINITY);
	public static final GlobalObject GLOBAL_PROTOTYPE = new GlobalObject();
	static {
		GLOBAL_PROTOTYPE.addProperty("Object", new FunctionObject(
				VMObjectFactory.FACTORY_ID_OBJECT, VMObject.OBJECT_PROTOTYPE,
				VMObject.ID_INIT_OBJECT, 1));
		GLOBAL_PROTOTYPE.addProperty("Boolean", new FunctionObject(VMObjectFactory.FACTORY_ID_BOOLEAN, BooleanObject.BOOLEAN_PROTOTYPE, BooleanObject.ID_INIT_OBJECT, 1));
		GLOBAL_PROTOTYPE.addProperty("String", new FunctionObject(
				VMObjectFactory.FACTORY_ID_STRING, StringObject.STRING_PROTOTYPE,
				StringObject.ID_INIT_STRING, 1));
		GLOBAL_PROTOTYPE.addProperty("Function", new FunctionObject(
				VMObjectFactory.FACTORY_ID_FUNCTION, FunctionObject.FUNCTION_PROTOTYPE,
				FunctionObject.ID_INIT_FUNCTION, 1);
		GLOBAL_PROTOTYPE.addProperty("fromCharCode",
				new FunctionObject(ID_FROM_CHAR_CODE, 1)));
		GLOBAL_PROTOTYPE.addProperty("Number",
				new FunctionObject(
						VMObjectFactory.FACTORY_ID_NUMBER,
						NumberObject.NUMBER_PROTOTYPE,
						VMObject.ID_INIT_NUMBER, 1);
						GLOBAL_PROTOTYPE.addProperty("NaN",  NAN);
		GLOBAL_PROTOTYPE.addProperty("INFINITY", INFINITY);
		GLOBAL_PROTOTYPE.addProperty("NEGATIVE_INFINITY",
				new Double(Double.NEGATIVE_INFINITY));
GLOBAL_PROTOTYPE.addProperty("MAX_VALUE",
new Double(Double.MAX_VALUE));
GLOBAL_PROTOTYPE.addProperty(
"MIN_VALUE", new Double(Double.MIN_VALUE));
	}

	public GlobalObject() {
		super(OBJECT_PROTOTYPE);
	}
}
