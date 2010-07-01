package vm.object.nativeobject;

import vm.VMStack;
import vm.object.VMObject;

public class NumberObject extends VMObject {
	public static final int ID_INIT_NUMBER = 0x70;// 构造函数

	private static final int ID_TO_FIXED = 0x71;// prototype.toFixed()
	private static final int ID_TO_EXPONENTIAL = 0x72;// prototype.toExponential()
	private static final int ID_TO_PRECISION = 0x73;// prototype.toPrecision()

	public static final NumberObject NUMBER_PROTOTYPE = new NumberObject();
	static {
		NUMBER_PROTOTYPE.addProperty("toFixed", new FunctionObject(ID_TO_FIXED,
				1));
		NUMBER_PROTOTYPE.addProperty("toExponential", new FunctionObject(
				ID_TO_EXPONENTIAL, 1));
		NUMBER_PROTOTYPE.addProperty("toPrecision", new FunctionObject(
				ID_TO_PRECISION, 1));
	}

	public NumberObject() {
		super(OBJECT_PROTOTYPE);
	}

	public void evalNative(int index, VMStack stack, int sp, int parCount) {
		switch (index) {
		case ID_TO_EXPONENTIAL:
		case ID_TO_FIXED:
		case ID_TO_PRECISION:
			stack.setObject(sp, formatNumber(index, stack.getNumber(sp + 2),
					stack.getNumber(sp + 3)));
			break;
		default:
			if (parentPrototype != null)
				parentPrototype.evalNative(index, stack, sp, parCount);
			break;
		}
	}

	/**
	 * 将浮点数转换为字符串. 这个方法会在javascript的
	 * <code>toFixed(), toExponential(), toPrecision()</code> 中被调用,作用等同于
	 * <code>toString()</code>.
	 * <p>
	 * 这个方法写得有点垃圾,有时间应该对其进行重构
	 */
	private String formatNumber(int op, double d, double digitsRaw) {
		String s = Double.toString(d);

		if (Double.isInfinite(d) || Double.isNaN(d)
				|| (op == ID_TO_PRECISION && Double.isNaN(digitsRaw))) {
			return s;
		}

		int digits = (int) digitsRaw;

		StringBuffer buf = new StringBuffer();
		boolean neg = false;
		boolean negExp = false;

		long value = 0;
		int exp = 0;
		long subExp = 0;
		int part = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '-':
				if (part == 2) {
					negExp = true;
				} else {
					neg = true;
				}
				break;
			case 'e':
			case 'E':
				part = 2;
				break;

			case '.':
				part = 1;
				break;

			default:
				if (part == 2) {
					exp = exp * 10 + (c - 48);
				} else {
					buf.append(c);
					if (part == 1) {
						subExp++;
					}
				}
			}
		}

		while (buf.length() > 1 && buf.charAt(0) == '0') {
			buf.deleteCharAt(0);
		}

		if (negExp) {
			exp = -exp;
		}
		exp -= subExp;

		System.out.println(neg ? "-" : "" + buf + "E" + exp);

		if (op == ID_TO_PRECISION) {
			if (exp < -6 || exp >= digits) {
				digits = Math.max(0, digits - 1);
				op = ID_TO_EXPONENTIAL;
			} else {
				op = ID_TO_FIXED;
				digits = digits + Math.min(exp, 0);
			}
		}

		if (digits < 0) {
			digits = 0;
		}

		if (op == NumberObject.ID_TO_EXPONENTIAL) {
			while (buf.length() > digits) {
				exp += buf.length() - digits;
				buf.setLength(digits + 1);
				long l = Long.parseLong(buf.toString());
				l = (l + 5) / 10;
				buf.setLength(0);
				buf.append(l);
			}
			if (buf.length() > 1) {
				buf.insert(1, ".");
			}
			exp += buf.length() - 1;
			return (neg ? "" : "-") + value + "E" + exp;
		} else {
			int delta = digits + exp; // 2 digits, exp -5 -> 3 to cut off
			System.out.println("delta: " + delta);
			if (delta < 0) {
				delta = -delta; // delta = digits to cut off
				if (buf.length() - delta + 1 <= 0) {
					buf.setLength(0);
				} else {
					buf.setLength(buf.length() - delta + 1);
					long l = Long.parseLong(buf.toString());
					l = (l + 5) / 10;
					buf.setLength(0);
					buf.append(l);
				}
				while (buf.length() < digits + 1) {
					buf.insert(0, '0');
				}
			} else
				while (delta > 0) {
					buf.append('0');
					delta--;
				}

			buf.insert(buf.length() - digits, '.');

			return buf.toString();
		}
	}
}
