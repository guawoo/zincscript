package vm.object.nativeobject;

import vm.VMStack;
import vm.VMUtils;
import vm.VirtualMachine;
import vm.object.VMObject;
import vm.object.buildin.MathObject;

/**
 * <code>ArrayObject</code> 实现了JavaScript中的数组对象
 * 
 * @author Jarod Yv
 */
public class ArrayObject extends VMObject {

	private static final int INITIAL_SPACE = 16;// 初始化数组大小

	public static final int ID_INIT_ARRAY = 0x20;// 构造函数

	private static final int ID_JOIN = 0x21;// prototype.join()
	private static final int ID_POP = 0x22;// prototype.pop()
	private static final int ID_PUSH = 0x23;// prototype.push()
	private static final int ID_REVERSE = 0x24;// prototype.reverse()
	private static final int ID_SHIFT = 0x25;// prototype.shift()
	private static final int ID_UNSHIFT = 0x26;// prototype.unshift()
	private static final int ID_SORT = 0x27;// prototype.sort()
	private static final int ID_SPLICE = 0x28;// prototype.splice()
	private static final int ID_LENGTH = 0x29;// prototype.length()
	private static final int ID_CONCAT = 0x2a;// prototype.concat()
	private static final int ID_SLICE = 0x2b;// prototype.slice()

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
	 * Java implementations of JS array members and methods.
	 */
	public void evalNative(int index, VMStack stack, int sp, int parCount) {
		switch (index) {

		case ID_CONCAT:
			ArrayObject array = new ArrayObject();
			copy(0, array, 0, size);
			stack.copy(sp + 2, array, size, parCount);
			stack.setObject(sp, array);
			break;

		case ID_JOIN:
			StringBuffer buf = new StringBuffer();
			String sep = stack.isNull(sp + 2) ? "," : stack.getString(sp + 2);
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					buf.append(sep);
				}
				if (!isNull(i)) {
					buf.append(getString(i));
				}
			}
			stack.setObject(sp, buf.toString());
			break;

		// case ID_LENGTH_SET:
		// setSize(stack.getInt(sp));
		// break;
		case ID_LENGTH:
			stack.setInt(sp, size);
			break;

		case ID_POP:
			if (size == 0) {
				stack.setObject(sp, null);
			} else {
				copy(size - 1, stack, sp);
				setSize(size - 1);
			}
			break;

		case ID_PUSH:
			stack.copy(sp + 2, this, size, parCount);
			stack.setNumber(sp, size);
			break;

		case ID_REVERSE:
			for (int i = 0; i < size / 2; i++) {
				swap(i, size - 1 - i);
			}
			break;

		case ID_SHIFT:
			if (size == 0) {
				stack.setObject(sp, null);
			} else {
				copy(0, stack, sp);
				copy(1, this, 0, size - 1);
				setSize(size - 1);
			}
			break;

		case ID_SLICE:
			int start = stack.getInt(sp + 2);
			int end = stack.isNull(sp + 3) ? size : stack.getInt(sp + 3);
			if (start < 0) {
				start = Math.max(size + start, 0);
			}
			if (end < 0) {
				end = Math.max(size + start, 0);
			}
			if (start > size) {
				start = size;
			}
			if (end > size) {
				end = size;
			}
			if (end < start) {
				end = start;
			}
			array = new ArrayObject();
			copy(start, array, 0, end - start);
			stack.setObject(sp, array);
			break;

		case ID_SORT:
			Object compare = stack.getObject(sp + 2);
			if (compare instanceof FunctionObject) {
				sort(0, size, (FunctionObject) compare, stack.getVMObject(sp),
						stack, sp);
			} else {
				sort(0, size);
			}
			stack.setObject(sp, this);
			break;

		case ID_SPLICE:
			array = new ArrayObject();
			start = stack.getInt(sp + 2);
			int delCount = stack.getInt(sp + 3);
			if (start < 0) {
				start = size - start;
			}
			int itemCount = Math.max(0, parCount - 2);
			copy(0, array, 0, start);
			stack.copy(sp + 4, array, start, itemCount);
			copy(start + delCount, array, start + itemCount, size
					- (start + delCount));
			stack.setObject(sp, array);
			break;

		case ID_UNSHIFT:
			copy(0, this, parCount, size);
			stack.copy(sp + 2, this, 0, parCount);
			stack.setInt(sp, parCount);
			break;

		default:
			super.evalNative(index, stack, sp, parCount);
		}
	}

	/**
	 * Set the object at the given index. For Long objects, the LONG_MARKER is
	 * set in the object array and the longValue is stored in longs. In contrast
	 * to Java arrays, the array grows automatically.
	 */
	public final void setObject(int i, Object v) {
		// System.out.println("size: "+size+" arr.len: "+objects.length+" i: "+i);
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

	public VMObject getVMObject(int i) {
		return VMUtils.toVMObject(getObject(i));
	}

	public String getString(int i) {
		Object o = getObject(i);
		return (o instanceof String) ? ((String) o) : VMUtils.toString(o);
	}

	/**
	 * Sets a boolean value for the given index.
	 * 
	 * @param i
	 *            index
	 * @param b
	 *            value
	 */
	public void setBoolean(int i, boolean b) {
		setObject(i, b ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Returns the boolean value at array index i. If the actual value is not
	 * boolean, it is converted automatically according to the ECMA conversion
	 * rules. This method handles Boolean.TRUE, Boolean.FALSE and numbers. All
	 * other values are delegated to JsSystem.toBoolean()
	 */
	public final boolean getBoolean(int i) {
		if (i >= size) {
			return false;
		}
		Object o = objects[i];

		if (o instanceof Double) {
			double d = ((Double) o).doubleValue();
			return d != 0 && !Double.isNaN(d);
		}
		if (o == Boolean.TRUE) {
			return true;
		}
		if (o == Boolean.FALSE) {
			return false;
		}

		return VMUtils.toBoolean(o);
	}

	/**
	 * Returns the numerical value at array index i. If the actual value is not
	 * numeric, it is converted automatically.
	 */
	public final double getNumber(int i) {
		if (i >= size) {
			return 0;
		}
		Object o = objects[i];
		return o instanceof Double ? ((Double) o).doubleValue() : VMUtils
				.toNumber(o);
	}

	/**
	 * Returns the value at array index i, casted to an integer. This routine
	 * corresponds to toInt32 in the ECMAScript v3 specification.
	 */
	public final int getInt(int i) {
		double d = getNumber(i);

		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return 0;
		} else {
			return (int) (long) d;
		}
	}

	/**
	 * Shortcut for setFP(i, v &lt;&lt; 16);
	 */
	public final void setInt(int i, int v) {
		setNumber(i, v);
	}

	/**
	 * Set the fix point value at array index i to v
	 */
	public final void setNumber(int i, double v) {
		if (i >= size) {
			setObject(i, null);
		}
		objects[i] = new Double(v);
		// numbers[i] = v;
	}

	/**
	 * Determines whether the value can be converted to a number in a meaningful
	 * way (used to decide whether the plus operator operates on toNumber or
	 * toString).
	 */
	public boolean isNumber(int i) {
		if (i >= size) {
			return true;
		}
		Object o = objects[i];
		return o instanceof Double || o == Boolean.TRUE || o == Boolean.FALSE
				|| (o instanceof DateObject);
	}

	/**
	 * Determines whether the array content at the given index is null or
	 * undefined.
	 * 
	 * @param i
	 *            Array index
	 * @return true if the array content at the given index is null or undefined
	 */
	public boolean isNull(int i) {
		return i >= size || objects[i] == null
				|| objects[i] == VMUtils.EMPTY_OBJECT;
	}

	/**
	 * Copy len values from index from to index to. The ranges may overlap, but
	 * Max(from, to)+len must be smaller than size.
	 * 
	 * @param from
	 *            source index
	 * @param to
	 *            target index
	 * @param len
	 *            number of elements to copy
	 */
	public void copy(int from, ArrayObject target, int to, int len) {
		if (target.size < to + len) {
			target.setObject(to + len - 1, null);
		}

		int maxIdx = Math.min(size, from + len);
		int l = Math.max(0, maxIdx - from);

		// System.arraycopy(numbers, from, target.numbers, to, l);
		System.arraycopy(objects, from, target.objects, to, l);

		for (int i = to + l; i < maxIdx; i++) {
			target.setObject(i, null);
		}
	}

	/**
	 * Copy a single value at the index "from" to index "to" in the array
	 * "target".
	 */
	public void copy(int from, ArrayObject target, int to) {
		if (from >= size) {
			target.setObject(to, null);
			return;
		}
		if (to >= target.size) {
			target.setObject(to, null);
		}
		// target.numbers[to] = numbers[from];
		target.objects[to] = objects[from];
	}

	/**
	 * Sort the array contents between the given indices using string comparison
	 * 
	 * @param left
	 *            lower interval border (inclusive)
	 * @param right
	 *            higher interval border (exclusive)
	 */
	private void sort(int left, int right) {

		if (right > left + 1) {
			int pivotIndex = left + MathObject.RANDOM.nextInt(right - left);
			String pivotValue = getString(pivotIndex);
			swap(pivotIndex, right - 1);
			int storeIndex = left;

			for (int i = left; i < right - 1; i++) {
				if (getString(i).compareTo(pivotValue) <= 0) {
					swap(i, storeIndex++);
				}
			}
			swap(storeIndex, right - 1);

			sort(left, storeIndex);
			sort(storeIndex, right);
		}
	}

	/**
	 * Sort the array contents between the given indices using a Javascript
	 * comparator function,
	 * 
	 * @param left
	 *            lower interval border (inclusive)
	 * @param right
	 *            higher interval border (exclusive)
	 */
	private void sort(int left, int right, FunctionObject compare,
			VMObject ctx, VMStack stack, int sp) {

		if (right > left + 1) {
			int pivotIndex = left + MathObject.RANDOM.nextInt(right - left);
			swap(pivotIndex, right - 1);
			int storeIndex = left;

			for (int i = left; i < right - 1; i++) {
				stack.setObject(sp, ctx);
				stack.setObject(sp + 1, compare);
				copy(i, stack, sp + 2);
				copy(right - 1, stack, sp + 3);
				new VirtualMachine().eval(compare, stack, sp, 2);

				if (stack.getNumber(sp) <= 0) {
					swap(i, storeIndex++);
				}
			}
			swap(storeIndex, right - 1);

			sort(left, storeIndex, compare, ctx, stack, sp);
			sort(storeIndex, right, compare, ctx, stack, sp);
		}
	}

	/**
	 * Returns the element type for the given index (One of TYPE_XXX constants)
	 * defined in JsObject.
	 * 
	 * @param i
	 *            index
	 * @return type
	 */
	public int getType(int i) {
		if (i > size)
			return VirtualMachine.TYPE_UNDEFINED;
		Object o = objects[i];
		if (o == Boolean.TRUE || o == Boolean.FALSE) {
			return VirtualMachine.TYPE_BOOLEAN;
		}
		if (o == null) {
			return VirtualMachine.TYPE_UNDEFINED;
		}
		if (o == VMUtils.EMPTY_OBJECT) {
			return VirtualMachine.TYPE_NULL;
		}
		if (o instanceof Double) {
			return VirtualMachine.TYPE_NUMBER;
		}
		if (o instanceof String) {
			return VirtualMachine.TYPE_STRING;
		}
		if (o instanceof FunctionObject) {
			return VirtualMachine.TYPE_FUNCTION;
		}
		return VirtualMachine.TYPE_OBJECT;
	}

	/**
	 * Swap the values at indices i1 and i2. i1 and i2 both must be smaller than
	 * size.
	 */
	public void swap(int i1, int i2) {
		// double f = numbers[i1];
		Object o = objects[i1];
		// numbers[i1] = numbers[i2];
		objects[i1] = objects[i2];
		// numbers[i2] = f;
		objects[i2] = o;
	}

	/**
	 * Returns the active size of this array.
	 */
	public int size() {
		return size;
	}

	/**
	 * Sets the size of the array to the given new size.
	 * 
	 * @param newLen
	 *            the new array size
	 */
	public void setSize(int newLen) {
		if (objects.length < newLen) {
			setObject(newLen - 1, null);
		} else {
			size = newLen;
		}
	}
}
