package vm.object.buildin;

import java.util.Random;
import vm.object.VMObject;
import vm.object.nativeobject.ArrayObject;
import vm.object.nativeobject.FunctionObject;

/**
 * @author Jarod Yv
 * @see ECMA-262 112页~117页 15.8.The Math Object
 */
public class MathObject extends VMObject {

	public static final double LN2 = 0.6931471805599453;
	/**
	 * M因子, 用于计算ln(x). 根据网上资料, M=8就能够满足对精度的要求
	 * 
	 * @see #ln(double)
	 */
	private static final int M = 8; // m*ln(2)
	public static final Random random = new Random();

	// 方法
	private static final int ID_ABS = 19;
	private static final int ID_ACOS = 20;
	private static final int ID_ASIN = 21;
	private static final int ID_ATAN = 22;
	private static final int ID_ATAN2 = 23;
	private static final int ID_CEIL = 24;
	private static final int ID_COS = 25;
	private static final int ID_EXP = 26;
	private static final int ID_FLOOR = 27;
	private static final int ID_LOG = 28;
	private static final int ID_MAX = 29;
	private static final int ID_MIN = 30;
	private static final int ID_POW = 31;
	private static final int ID_RANDOM = 32;
	private static final int ID_ROUND = 33;
	private static final int ID_SIN = 34;
	private static final int ID_SQRT = 35;
	private static final int ID_TAN = 36;
	// 常量
	private static final int ID_E = 70;
	private static final int ID_LN10 = 72;
	private static final int ID_LN2 = 74;
	private static final int ID_LOG2E = 76;
	private static final int ID_LOG10E = 78;
	private static final int ID_PI = 80;
	private static final int ID_SQRT1_2 = 82;
	private static final int ID_SQRT2 = 84;

	public static final MathObject MATH_PROTOTYPE = new MathObject();
	static {
		MATH_PROTOTYPE.addProperty("E", new FunctionObject(ID_E, -1));
		MATH_PROTOTYPE.addProperty("LN10", new FunctionObject(ID_LN10, -1));
		MATH_PROTOTYPE.addProperty("LN2", new FunctionObject(ID_LN2, -1));
		MATH_PROTOTYPE.addProperty("LOG2E", new FunctionObject(ID_LOG2E, -1));
		MATH_PROTOTYPE.addProperty("LOG10E", new FunctionObject(ID_LOG10E, -1));
		MATH_PROTOTYPE.addProperty("PI", new FunctionObject(ID_PI, -1));
		MATH_PROTOTYPE.addProperty("SQRT1_2",
				new FunctionObject(ID_SQRT1_2, -1));
		MATH_PROTOTYPE.addProperty("SQRT2", new FunctionObject(ID_SQRT2, -1));
		MATH_PROTOTYPE.addProperty("abs", new FunctionObject(ID_ABS, 1));
		MATH_PROTOTYPE.addProperty("acos", new FunctionObject(ID_ACOS, 1));
		MATH_PROTOTYPE.addProperty("asin", new FunctionObject(ID_ASIN, 1));
		MATH_PROTOTYPE.addProperty("atan", new FunctionObject(ID_ATAN, 1));
		MATH_PROTOTYPE.addProperty("atan2", new FunctionObject(ID_ATAN2, 2));
		MATH_PROTOTYPE.addProperty("ceil", new FunctionObject(ID_CEIL, 1));
		MATH_PROTOTYPE.addProperty("cos", new FunctionObject(ID_COS, 1));
		MATH_PROTOTYPE.addProperty("exp", new FunctionObject(ID_EXP, 1));
		MATH_PROTOTYPE.addProperty("floor", new FunctionObject(ID_FLOOR, 1));
		MATH_PROTOTYPE.addProperty("log", new FunctionObject(ID_LOG, 1));
		MATH_PROTOTYPE.addProperty("max", new FunctionObject(ID_MAX, 2));
		MATH_PROTOTYPE.addProperty("min", new FunctionObject(ID_MIN, 2));
		MATH_PROTOTYPE.addProperty("pow", new FunctionObject(ID_POW, 2));
		MATH_PROTOTYPE.addProperty("random", new FunctionObject(ID_RANDOM, 0));
		MATH_PROTOTYPE.addProperty("round", new FunctionObject(ID_ROUND, 1));
		MATH_PROTOTYPE.addProperty("sin", new FunctionObject(ID_SIN, 1));
		MATH_PROTOTYPE.addProperty("sqrt", new FunctionObject(ID_SQRT, 1));
		MATH_PROTOTYPE.addProperty("tan", new FunctionObject(ID_TAN, 1));
	}

	public MathObject() {
		super(OBJECT_PROTOTYPE);
	}

	public void evalNative(int index, ArrayObject stack, int sp, int parCount) {
		switch (index) {
		case ID_ABS:
			stack.setNumber(sp, Math.abs(stack.getNumber(sp + 2)));
			break;
		case ID_ACOS:
		case ID_ASIN:
		case ID_ATAN:
		case ID_ATAN2:
			throw new RuntimeException("NYI");
		case ID_CEIL:
			stack.setNumber(sp, Math.ceil(stack.getNumber(sp + 2)));
			break;
		case ID_COS:
			stack.setNumber(sp, Math.cos(stack.getNumber(sp + 2)));
			break;
		case ID_EXP:
			stack.setNumber(sp, exp(stack.getNumber(sp + 2)));
			break;
		case ID_FLOOR:
			stack.setNumber(sp, Math.floor(stack.getNumber(sp + 2)));
			break;
		case ID_LOG:
			stack.setNumber(sp, ln(stack.getNumber(sp + 2)));
			break;
		case ID_MAX:
			double d = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < parCount; i++) {
				d = Math.max(d, stack.getNumber(sp + 2 + i));
			}
			stack.setNumber(sp, d);
			break;
		case ID_MIN:
			d = Double.POSITIVE_INFINITY;
			for (int i = 0; i < parCount; i++) {
				d = Math.min(d, stack.getNumber(sp + 2 + i));
			}
			stack.setNumber(sp, d);
			break;
		case ID_POW:
			stack.setNumber(sp, pow(stack.getNumber(sp + 2), stack
					.getNumber(sp + 3)));
			break;
		case ID_RANDOM:
			stack.setNumber(sp, random.nextDouble());
			break;
		case ID_ROUND:
			stack.setNumber(sp, Math.floor(stack.getNumber(sp + 2) + 0.5));
			break;
		case ID_SIN:
			stack.setNumber(sp, Math.sin(stack.getNumber(sp + 2)));
			break;
		case ID_SQRT:
			stack.setNumber(sp, Math.sqrt(stack.getNumber(sp + 2)));
			break;
		case ID_TAN:
			stack.setNumber(sp, Math.tan(stack.getNumber(sp + 2)));
			break;
		case ID_E:
			stack.setNumber(sp, Math.E);
			break;
		case ID_LN10:
			stack.setNumber(sp, 2.302585092994046);
			break;
		case ID_LN2:
			stack.setNumber(sp, LN2);
			break;
		case ID_LOG2E:
			stack.setNumber(sp, 1.4426950408889634);
			break;
		case ID_LOG10E:
			stack.setNumber(sp, 0.4342944819032518);
			break;
		case ID_PI:
			stack.setNumber(sp, Math.PI);
			break;
		case ID_SQRT1_2:
			stack.setNumber(sp, Math.sqrt(0.5));
			break;
		case ID_SQRT2:
			stack.setNumber(sp, Math.sqrt(2.0));
			break;
		default:
			if (parentPrototype != null)
				parentPrototype.evalNative(index, stack, sp, parCount);
			break;
		}
	}

	/**
	 * 计算以e为底x的对数, 即自然对数
	 * <p>
	 * 算法来自：http://en.wikipedia.org/wiki/Natural_logarithm
	 * 
	 * @param x
	 * @return
	 * @see http://en.wikipedia.org/wiki/Natural_logarithm
	 */
	private final double ln(double x) {
		return Math.PI / (2.0 * avg(1.0, 4.0 / (x * (1L << M)))) - LN2 * M;
	}

	/**
	 * 计算x的y次方
	 * <ul>
	 * <li>如果指数y是整数, 则采用循环相乘法求值
	 * <li>如果指数y是小数, 则采用exp(y * ln(x))求值
	 * </ul>
	 * 
	 * @param x
	 *            底数
	 * @param y
	 *            指数
	 * @return
	 */
	private final double pow(double x, double y) {
		long n = (long) y;
		if (y > 0 && y == n) {
			double result = 1;
			while (n > 0) {
				if ((n & 1) != 0) {
					result *= x;
					n--;
				}
				x *= x;
				n >>= 1;
			}
			return result;
		}
		return exp(y * ln(x));
	}

	/**
	 * 求e的x次方
	 * <p>
	 * 算法来自:http://en.wikipedia.org/wiki/Exponential_function
	 * 
	 * @param x
	 * @return
	 * @see http://en.wikipedia.org/wiki/Exponential_function
	 */
	private final double exp(double x) {
		long n = (long) Math.floor(x / LN2);
		double u = x - n * LN2;
		double m = 1;
		for (int i = 15; i >= 1; i--) {
			m = 1 + (u / i) * m;
		}
		if (n != 0) {
			long bits = Double.doubleToLongBits(m);
			bits += (n << 52);
			m = Double.longBitsToDouble(bits);
		}
		return m;

	}

	/**
	 * 求2数的平均值
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private final double avg(double a, double b) {
		for (int i = 0; i < 10; i++) {
			double tmp = (a + b) / 2.0;
			b = Math.sqrt(a * b);
			a = tmp;
		}
		return (a + b) / 2;
	}
}
