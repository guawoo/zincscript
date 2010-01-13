package vm;

import vm.object.VMObject;
import vm.object.nativeobject.BooleanObject;
import vm.object.nativeobject.DateObject;
import vm.object.nativeobject.NumberObject;
import vm.object.nativeobject.StringObject;

public final class VMUtils {
	/** 由于Hashtable中不能保存null, 因此对于空值, 统一填入此空对象 */
	public static final Object EMPTY_OBJECT = new Object();

	/**
	 * 对原始类型(string, boolean, number)中的值进行一层封装, 将其封装成 {@link VMObject}的子类
	 * 
	 * @param value
	 *            待封装的数据
	 */
	public static VMObject toVMObject(Object value) {
		if (value instanceof VMObject) {
			return (VMObject) value;
		}
		if (value instanceof String) {
			VMObject o = new VMObject(StringObject.STRING_PROTOTYPE);
			o.setValue(value);
			return o;
		}
		if (value instanceof Boolean) {
			VMObject o = new VMObject(BooleanObject.BOOLEAN_PROTOTYPE);
			o.setValue(value);
			return o;
		}
		if (value instanceof Double) {
			VMObject o = new VMObject(NumberObject.NUMBER_PROTOTYPE);
			o.setValue(value);
			return o;
		}
		return new VMObject(VMObject.OBJECT_PROTOTYPE);
	}

	/**
	 * Returns a string representation of the given object. This function works
	 * like the toString method in the ECMA 262 specification, except that it
	 * never throws an exception. Instead, "null" is returned for null and
	 * "undefined" for undefined.
	 * 
	 * @param o
	 *            the object to convert to a string
	 * @return the string representation of the object.
	 */
	public static String toString(Object o) {

		if (o == null) {
			return "undefined";
		}
		if (o == EMPTY_OBJECT) {
			return "null";
		}
		if (o instanceof Double) {
			Double d = (Double) o;
			if (d.doubleValue() == (long) d.doubleValue()) {
				return Long.toString((long) d.doubleValue());
			} else {
				return Double.toString(d.doubleValue());
			}
		}
		// if (o instanceof VMObject) {
		// }
		return o.toString();
	}

	/**
	 * This function works like toBoolean in the ECMA 262 documentation, except
	 * that it does not throw an exception for null or undefined.
	 */
	public static boolean toBoolean(Object o) {
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		}
		if (o instanceof Double) {
			double d = ((Double) o).doubleValue();
			return !Double.isNaN(d) && d != 0;
		}

		return !(o == null || "".equals(o) || o == EMPTY_OBJECT);
	}

	/**
	 * This function works like toNumber in the ECMA 262 documentation, except
	 * that it does not throw an exception for null or undefined. Instead, null
	 * is converted to 0 and undefined is converted to NaN.
	 */
	public static double toNumber(Object o) {
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		}
		if (o == null) {
			return 0;
		}
		if (o instanceof String) {
			try {
				return Double.parseDouble((String) o);
			} catch (NumberFormatException e) {
				return Double.NaN;
			}
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? 1 : 0;
		}
		if (o instanceof DateObject) {
			return ((DateObject) o).time.getTime().getTime();
		}
		return Double.NaN;
	}
}
