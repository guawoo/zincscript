package vm.object.buildin;

import vm.object.VMObject;
import vm.object.VMObjectFactory;
import vm.object.nativeobject.ArrayObject;
import vm.object.nativeobject.BooleanObject;
import vm.object.nativeobject.DateObject;
import vm.object.nativeobject.ErrorObject;
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

		GLOBAL_PROTOTYPE.addProperty("Boolean", new FunctionObject(
				VMObjectFactory.FACTORY_ID_BOOLEAN,
				BooleanObject.BOOLEAN_PROTOTYPE, BooleanObject.ID_INIT_BOOLEAN,
				1));

		GLOBAL_PROTOTYPE.addProperty("Array", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ARRAY, ArrayObject.ARRAY_PROTOTYPE,
				ArrayObject.ID_INIT_ARRAY, 1));

		GLOBAL_PROTOTYPE.addProperty("Error", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("EvalError", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("RangeError", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("ReferenceError", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("SyntaxError", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("TypeError", new FunctionObject(
				VMObjectFactory.FACTORY_ID_ERROR, ErrorObject.ERROR_PROTOTYPE,
				ErrorObject.ID_INIT_ERROR, 1));

		GLOBAL_PROTOTYPE.addProperty("String", new FunctionObject(
				VMObjectFactory.FACTORY_ID_STRING,
				StringObject.STRING_PROTOTYPE, StringObject.ID_INIT_STRING, 1)
				.addProperty("fromCharCode", new FunctionObject(
						VMObject.ID_FROM_CHAR_CODE, 1)));

		GLOBAL_PROTOTYPE.addProperty("Function", new FunctionObject(
				VMObjectFactory.FACTORY_ID_FUNCTION,
				FunctionObject.FUNCTION_PROTOTYPE,
				FunctionObject.ID_INIT_FUNCTION, 1).addProperty("fromCharCode",
				new FunctionObject(VMObject.ID_FROM_CHAR_CODE, 1)));

		GLOBAL_PROTOTYPE.addProperty("Number", new FunctionObject(
				VMObjectFactory.FACTORY_ID_NUMBER,
				NumberObject.NUMBER_PROTOTYPE, NumberObject.ID_INIT_NUMBER, 1)
				.addProperty("MAX_VALUE", new Double(Double.MAX_VALUE))
				.addProperty("MIN_VALUE", new Double(Double.MIN_VALUE))
				.addProperty("NaN", NAN).addProperty("NEGATIVE_INFINITY",
						new Double(Double.NEGATIVE_INFINITY)).addProperty(
						"POSITIVE_INFINITY", INFINITY));

		GLOBAL_PROTOTYPE.addProperty("Date", new FunctionObject(
				VMObjectFactory.FACTORY_ID_DATE, DateObject.DATE_PROTOTYPE,
				DateObject.ID_INIT_DATE, 7).addProperty("parse",
				new FunctionObject(DateObject.ID_PARSE, 1)).addProperty("UTC",
				new FunctionObject(DateObject.ID_UTC, 7)));

		GLOBAL_PROTOTYPE.addProperty("Math", MathObject.MATH_PROTOTYPE);

		GLOBAL_PROTOTYPE.addProperty("NaN", NAN);
		GLOBAL_PROTOTYPE.addProperty("Infinity", INFINITY);
		GLOBAL_PROTOTYPE.addProperty("undefined", null);
		GLOBAL_PROTOTYPE.addProperty("parseInt", new FunctionObject(
				ID_PARSE_INT, 2));
		GLOBAL_PROTOTYPE.addProperty("parseFloat", new FunctionObject(
				ID_PARSE_FLOAT, 1));
		GLOBAL_PROTOTYPE.addProperty("isNaN", new FunctionObject(ID_IS_NAN, 1));
		GLOBAL_PROTOTYPE.addProperty("isFinite", new FunctionObject(
				ID_IS_FINITE, 1));
		GLOBAL_PROTOTYPE.addProperty("decodeURI", new FunctionObject(
				ID_DECODE_URI, 1));
		GLOBAL_PROTOTYPE.addProperty("print", new FunctionObject(ID_PRINT, 1));
		GLOBAL_PROTOTYPE.addProperty("decodeURIComponent", new FunctionObject(
				ID_DECODE_URI_COMPONENT, 1));
		GLOBAL_PROTOTYPE.addProperty("encodeURI", new FunctionObject(
				ID_ENCODE_URI, 1));
		GLOBAL_PROTOTYPE.addProperty("encodeURIComponent", new FunctionObject(
				ID_ENCODE_URI_COMPONENT, 1));

	}

	public GlobalObject() {
		super(OBJECT_PROTOTYPE);
	}
}
