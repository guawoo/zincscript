package vm.object;

import java.util.Enumeration;
import java.util.Hashtable;

import vm.VMStack;
import vm.VMUtils;
import vm.object.nativeobject.FunctionObject;

/**
 * <code>VMObject</code> 是所有虚拟机对象的基类. 所谓虚拟机, 其实是的一个JavaScript的运行环境, 即解释执行引擎
 * <p>
 * 
 * 
 * @author Jarod Yv
 * @see ECMA-262 3页 4.2.1.Object
 */
public class VMObject {

	public static final int ID_INIT_OBJECT = 0x01;// 构造函数
	public static final int ID_FROM_CHAR_CODE = 0x0f;

	protected static final int ID_TO_STRING = 0x02;// prototype.toString()
	protected static final int ID_TO_LOCALE_STRING = 0x03;// prototype.toLocalString()
	protected static final int ID_VALUE_OF = 0x04;// prototype.valueOf()
	protected static final int ID_HAS_OWN_PROPERTY = 0x05;// prototype.hasOwnProperty()
	protected static final int ID_IS_PROTOTYPE_OF = 0x06;// prototype.isPrototypeOf()
	protected static final int ID_PROPERTY_IS_ENUMERABLE = 0x07;// prototype.propertyIsEnumerable()
	
	/**
	 * Object的{@link #parentPrototype}, 后面的static块用于添加Object中的方法。
	 * <p>
	 * 由于对象中的常量和方法只需要一份, 且作为继承传递用的prototype是共享的, 因此一个对象固有的prototype是个静态常量,
	 * 其所包含的方法也一次性注入, 不需多次添加.
	 * 
	 * @see ECMA-262 83页~85页 15.2.Object Objects
	 */
	public static final VMObject OBJECT_PROTOTYPE = new VMObject(null);
	static {
		OBJECT_PROTOTYPE.addProperty("toString", new FunctionObject(
				ID_TO_STRING, 0));// Object.prototype.toString()
		OBJECT_PROTOTYPE.addProperty("toLocaleString", new FunctionObject(
				ID_TO_LOCALE_STRING, 0));// Object.prototype.toLocaleString()
		OBJECT_PROTOTYPE.addProperty("valueOf", new FunctionObject(ID_VALUE_OF,
				0));// Object.prototype.valueOf()
		OBJECT_PROTOTYPE.addProperty("hasOwnProperty", new FunctionObject(
				ID_HAS_OWN_PROPERTY, 1));// Object.prototype.hasOwnProperty(V)
		OBJECT_PROTOTYPE.addProperty("isPrototypeOf", new FunctionObject(
				ID_IS_PROTOTYPE_OF, 1));// Object.prototype.isPrototypeOf(V)
		OBJECT_PROTOTYPE.addProperty("propertyIsEnumerable",
				new FunctionObject(ID_PROPERTY_IS_ENUMERABLE, 1));// Object.prototype.propertyIsEnumerable(V)
	}

	/**
	 * JavaScript中利用prototype实现继承和成员变量共享
	 * <p>
	 * parentPrototype用于记录父类的类型
	 */
	public VMObject parentPrototype = null;

	/** 用于保存对象的属性和值 */
	private Hashtable data = null;

	/** 父类对象 */
	public VMObject scopeChain = null;

	/** Boolean, Number, String类型的原始值 */
	protected Object value = null;

	public VMObject(VMObject parentPrototype) {
		this.parentPrototype = parentPrototype;
	}

	public VMObject addProperty(String property, Object value) {
		if (data == null)
			data = new Hashtable();
		data.put(property, value == null ? VMUtils.EMPTY_OBJECT : value);
		return this;
	}

	/**
	 * Execute java member implementation. Parameters for functions start at
	 * stack[sp+2]. Function and getter results are returned at stack[sp+0]. The
	 * assignement value for a setter is stored at stack[sp+0].
	 */
	public void evalNative(int index, VMStack stack, int sp, int parCount) {
		switch (index) {
		case ID_INIT_OBJECT:
			Object value = stack.getObject(sp + 2);
			if (isConstruction(stack, sp)) {
				if (value instanceof Boolean || value instanceof Double
						|| value instanceof String) {
					setValue(value);
				} else if (value instanceof VMObject) {
					stack.setObject(sp - 1, value);
				}
				// otherwise, don't do anything -- regular constructor call
			} else {
				if (value == null || value == VMUtils.EMPTY_OBJECT) {
					stack.setObject(sp, new VMObject(OBJECT_PROTOTYPE));
				} else {
					stack.setObject(sp, VMUtils.toVMObject(value));
				}
			}
			value = null;
			break;
		case ID_TO_STRING:
		case ID_TO_LOCALE_STRING:
			stack.setObject(sp, VMUtils.toString(stack.getObject(sp)));
			break;
		case ID_HAS_OWN_PROPERTY:
			stack.setBoolean(sp, data != null
					&& data.get(stack.getString(sp + 2)) != null);
			break;
		case ID_IS_PROTOTYPE_OF:
			value = stack.getObject(sp + 2);
			stack.setBoolean(sp, false);
			while (value instanceof VMObject) {
				if (value == this) {
					stack.setBoolean(sp, true);
					break;
				}
			}
			value = null;
			break;
		case ID_PROPERTY_IS_ENUMERABLE:
			value = getRawInPrototypeChain(stack.getString(sp + 2));
			stack.setBoolean(sp, value != null
					&& !(value instanceof FunctionObject));
			break;
		case ID_VALUE_OF:
			stack.setObject(sp, getValue() == null ? this : getValue());
			break;
		default:
			if (parentPrototype != null)
				parentPrototype.evalNative(index, stack, sp, parCount);
			break;
		}
	}

	/**
	 * 判断是否是调用构造函数
	 * 
	 * @param stack
	 * @param sp
	 * @return
	 */
	private boolean isConstruction(VMStack stack, int sp) {
		return sp > 0 && stack.getObject(sp - 1) == stack.getObject(sp);
	}

	/**
	 * 递归地获取属性的值
	 * 
	 * @param property
	 *            属性名
	 */
	public Object getRawInPrototypeChain(String property) {
		Object result = null;
		if (data != null) {
			result = data.get(property);
			if (result != null) {
				return result == VMUtils.EMPTY_OBJECT ? null : result;
			}
		}
		if (parentPrototype != null) {
			result = parentPrototype.getRawInPrototypeChain(property);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Set method called from the byte code interpreter, avoiding temporary
	 * stack creation. This method is overwritten in JsArray.
	 */
	public void vmSetOperation(VMStack stack, int keyIndex, int valueIndex) {
		String key = stack.getString(keyIndex);

		// TODO re-enable optimization
		// Object old = getRaw(key);
		//
		// if (old instanceof JsFunction){
		// JsFunction nat = (JsFunction) old;
		// if (nat.getParameterCount() == -1){
		// evalNative(nat.index + 1, stack, valueIndex, 0);
		// return;
		// }
		// }

		setObject(key, stack.getObject(valueIndex));
	}

	/**
	 * Get method called from the bytecode interpreter, avoiding temporary stack
	 * creation. This method is overwritten in JsArray and JsArguments.
	 */
	public void vmGetOperation(VMStack stack, int keyIndex, int valueIndex) {
		String key = stack.getString(keyIndex);

		// TODO re-enable optimization
		// Object old = getRaw(key);
		//
		// if (old instanceof JsFunction){
		// JsFunction f = (JsFunction) old;
		// if (f.getParameterCount() == -1){
		// evalNative(f.index, stack, valueIndex, 0);
		// return;
		// }
		// }
		stack.setObject(valueIndex, getObject(key));
	}

	/**
	 * Sets the given property to the given value, taking the prototype chain,
	 * scope chain, and setters into account.
	 * 
	 * @param prop
	 *            property name
	 * @param value
	 *            value to set
	 * @return this (for chained calls)
	 */
	public void setObject(String key, Object v) {
		Object old = getRawInPrototypeChain(key);
		if (old instanceof FunctionObject
				&& ((FunctionObject) old).expectedParameterCount == -1) {
			FunctionObject nat = (FunctionObject) old;
			VMStack stack = new VMStack();
			stack.setObject(0, v);
			evalNative(nat.index + 1, stack, 0, 0);
			return;
		} else if (old == null && scopeChain != null) {
			scopeChain.setObject(key, v);
		} else {
			if (data == null) {
				data = new Hashtable();
			}
			data.put(key, v == null ? VMUtils.EMPTY_OBJECT : v);
		}
	}

	/**
	 * Returns the given property, taking native getters into account.
	 * 
	 * @param prop
	 *            Name of the property
	 * @return stored value or null
	 */
	public Object getObject(String prop) {
		Object v = getRawInPrototypeChain(prop);
		if (v instanceof FunctionObject) {
			FunctionObject nat = (FunctionObject) v;
			if (nat.expectedParameterCount == -1) {
				VMStack stack = new VMStack();
				evalNative(nat.index, stack, 0, 0);
				return stack.getObject(0);
			}
		} else if (v == null && scopeChain != null) {
			v = scopeChain.getObject(prop);
		}

		return v;
	}

	/**
	 * Delete the given property. Returns true if it was actually deleted.
	 */
	public boolean delete(String key) {
		if (data == null) {
			return true;
		}

		// TODO check whether this covers dontdelete sufficiently

		Object old = data.get(key);
		if (old instanceof FunctionObject
				&& ((FunctionObject) old).expectedParameterCount == -1) {
			return false;
		}

		data.remove(key);
		return true;
	}

	/**
	 * Returns a key enumeration for this object only, not including the
	 * prototype or scope chain.
	 */
	public Enumeration keys() {
		return data == null ? new Hashtable().keys() : data.keys();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
