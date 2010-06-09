package vm;

import vm.object.VMObject;
import vm.object.nativeobject.DateObject;
import vm.object.nativeobject.FunctionObject;

public class VMStack {
	private static final int INITIAL_SPACE = 16;

	/**
	 * Objects contained in this array.
	 */
	private Object[] objects = new Object[INITIAL_SPACE];

	/**
	 * Active size of this array.
	 */
	private int size;

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
	public void copy(int from, VMStack target, int to, int len) {
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
	public void copy(int from, VMStack target, int to) {
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
}
