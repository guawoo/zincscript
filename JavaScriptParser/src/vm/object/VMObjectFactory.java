package vm.object;

import vm.object.nativeobject.ArrayObject;
import vm.object.nativeobject.BooleanObject;
import vm.object.nativeobject.DateObject;
import vm.object.nativeobject.ErrorObject;
import vm.object.nativeobject.FunctionObject;
import vm.object.nativeobject.NumberObject;
import vm.object.nativeobject.StringObject;

public final class VMObjectFactory {

	private static final int FACTORY_ID_OBJECT = 0;
	private static final int FACTORY_ID_ARRAY = 1;
	private static final int FACTORY_ID_DATE = 2;
	private static final int FACTORY_ID_BOOLEAN = 3;
	private static final int FACTORY_ID_STRING = 4;
	private static final int FACTORY_ID_NUMBER = 5;
	private static final int FACTORY_ID_ERROR = 6;
	private static final int FACTORY_ID_EVAL_ERROR = 7;
	private static final int FACTORY_ID_RANGE_ERROR = 8;
	private static final int FACTORY_ID_REFERENCE_ERROR = 9;
	private static final int FACTORY_ID_SYNTAX_ERROR = 10;
	private static final int FACTORY_ID_TYPE_ERROR = 11;
	private static final int FACTORY_ID_URI_ERROR = 12;
	private static final int FACTORY_ID_FUNCTION = 13;

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
