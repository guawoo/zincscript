package vm.object;

import vm.object.nativeobject.ArrayObject;
import vm.object.nativeobject.BooleanObject;
import vm.object.nativeobject.DateObject;
import vm.object.nativeobject.ErrorObject;
import vm.object.nativeobject.FunctionObject;
import vm.object.nativeobject.NumberObject;
import vm.object.nativeobject.StringObject;

public final class VMObjectFactory {

	public static final int FACTORY_ID_OBJECT = 0x00;
	public static final int FACTORY_ID_ARRAY = 0x01;
	public static final int FACTORY_ID_DATE = 0x02;
	public static final int FACTORY_ID_BOOLEAN = 0x03;
	public static final int FACTORY_ID_STRING = 0x04;
	public static final int FACTORY_ID_NUMBER = 0x05;
	public static final int FACTORY_ID_ERROR = 0x06;
	public static final int FACTORY_ID_EVAL_ERROR = 0x07;
	public static final int FACTORY_ID_RANGE_ERROR = 0x08;
	public static final int FACTORY_ID_REFERENCE_ERROR = 0x09;
	public static final int FACTORY_ID_SYNTAX_ERROR = 0x0a;
	public static final int FACTORY_ID_TYPE_ERROR = 0x0b;
	public static final int FACTORY_ID_URI_ERROR = 0x0c;
	public static final int FACTORY_ID_FUNCTION = 0x0d;

	/**
	 * Creates a new instance of a JS object (Date, Object, Array), depending on
	 * the factory id.
	 * 
	 * @param type
	 *            one of FACTORY_ID_OBJECT, FACTORY_ID_ARRAY, FACTORY_ID_DATE
	 * @return the newly created instance
	 */
	public static final VMObject newInstance(int type) {
		switch (type) {
		case FACTORY_ID_OBJECT:
			return new VMObject(null);
		case FACTORY_ID_ARRAY:
			return new ArrayObject();
		case FACTORY_ID_DATE:
			return new DateObject();
		case FACTORY_ID_BOOLEAN:
			return new BooleanObject();
		case FACTORY_ID_NUMBER:
			return new NumberObject();
		case FACTORY_ID_STRING:
			return new StringObject();
		case FACTORY_ID_ERROR:
		case FACTORY_ID_EVAL_ERROR:
		case FACTORY_ID_RANGE_ERROR:
		case FACTORY_ID_REFERENCE_ERROR:
		case FACTORY_ID_SYNTAX_ERROR:
		case FACTORY_ID_TYPE_ERROR:
		case FACTORY_ID_URI_ERROR:
			return new ErrorObject();
		case FACTORY_ID_FUNCTION:
			// this will be overwritten by the eval result in case
			// ID_INIT_FUNCITON in JsObject
			return new FunctionObject(-1, -1);
		}
		throw new IllegalArgumentException();
	}
}
