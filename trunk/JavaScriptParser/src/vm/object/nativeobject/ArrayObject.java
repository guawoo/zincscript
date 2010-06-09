package vm.object.nativeobject;

import vm.object.VMObject;

/**
 * <code>ArrayObject</code> 实现了JavaScript中的数组对象
 * 
 * @author Jarod Yv
 */
public class ArrayObject extends VMObject {

	private static final int INITIAL_SPACE = 16;

	private static final int ID_JOIN = 405;
	private static final int ID_POP = 406;
	private static final int ID_PUSH = 407;
	private static final int ID_REVERSE = 408;
	private static final int ID_SHIFT = 409;
	private static final int ID_SORT = 411;
	private static final int ID_SPLICE = 412;
	private static final int ID_UNSHIFT = 413;
	private static final int ID_LENGTH = 414;
	private static final int ID_CONCAT = 415;
	private static final int ID_SLICE = 416;

	public static final VMObject ARRAY_PROTOTYPE = new VMObject(
			OBJECT_PROTOTYPE);
	static {
		ARRAY_PROTOTYPE
				.addProperty("length", new FunctionObject(ID_LENGTH, -1));
		ARRAY_PROTOTYPE.addProperty("concat", new FunctionObject(ID_CONCAT, 1));
		ARRAY_PROTOTYPE.addProperty("join", new FunctionObject(ID_JOIN, 0));
		ARRAY_PROTOTYPE.addProperty("pop", new FunctionObject(ID_POP, 0));
		ARRAY_PROTOTYPE.addProperty("push", new FunctionObject(ID_PUSH, 1));
		ARRAY_PROTOTYPE.addProperty("reverse",
				new FunctionObject(ID_REVERSE, 0));
		ARRAY_PROTOTYPE.addProperty("shift", new FunctionObject(ID_SHIFT, 0));
		ARRAY_PROTOTYPE.addProperty("slice", new FunctionObject(ID_SLICE, 2));
		ARRAY_PROTOTYPE.addProperty("sort", new FunctionObject(ID_SORT, 1));
		ARRAY_PROTOTYPE.addProperty("splice", new FunctionObject(ID_SPLICE, 2));
		ARRAY_PROTOTYPE.addProperty("unshift",
				new FunctionObject(ID_UNSHIFT, 1));
	}

	/**
	 * Marker object, used to indicate that the actual value is contained in the
	 * longs array.
	 */
	// private static final Object NUMBER_MARKER = new Object();

	/**
	 * Objects contained in this array.
	 */
	private Object[] objects = new Object[INITIAL_SPACE];

	/**
	 * numeric values contained in this array, represented as 64 bit fixed point
	 * value (16 bit fraction).
	 */
	// private double[] numbers = new double[INITIAL_SPACE];

	/**
	 * Active size of this array.
	 */
	private int size;

	/**
	 * Creates a new empty array.
	 */
	public ArrayObject() {
		super(ARRAY_PROTOTYPE);
	}

	/**
	 * Returns the Object at the given index. Numeric values are returned as an
	 * instance of Double. Used to transfer values between hashtables and arrayz
	 */
	public final Object getObject(int i) {
		if (i >= size)
			return null;
		return objects[i];
		// Object o = objects[i];
		// return o == NUMBER_MARKER ? new Double(numbers[i]) : o;
	}

	// public VMObject getVMObject(int i) {
	// return VMUtils.toVMObject(getObject(i));
	// }

	/**
	 * Set the object at the given index. For Long objects, the LONG_MARKER is
	 * set in the object array and the longValue is stored in longs. In contrast
	 * to Java arrays, the array grows automatically.
	 */
	public final void setObject(int i, Object v) {
		//
		System.out.println("size: " + size + " arr.len: " + objects.length
				+ " i: " + i);
		if (i >= size) {
			size = i + 1;
			if (i >= objects.length) {
				// double[] newNums = new double[i * 3 / 2];
				// System.arraycopy(numbers, 0, newNums, 0, numbers.length);
				// numbers = newNums;
				Object[] newObjects = new Object[i * 3 / 2];
				System.arraycopy(objects, 0, newObjects, 0, objects.length);
				objects = newObjects;
			}
		}
		// if (v instanceof Double) {
		// numbers[i] = ((Double) v).doubleValue();
		// objects[i] = NUMBER_MARKER;
		// } else {
		objects[i] = v;
		// }
	}

	
	/**
	 * Returns the active size of this array.
	 */
	public int size() {
		return size;
	}
}
