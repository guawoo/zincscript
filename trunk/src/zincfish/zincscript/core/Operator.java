package zincfish.zincscript.core;

/**
 * <code>Operator</code>封装了各种运算符的操作
 * 
 * @author Jarod Yv
 */
public final class Operator {
	// Operator的实例,实现Singleton模式
	private static Operator instance = null;

	/**
	 * 获取<code>Operator</code>的实例.<br>
	 * <code>Operator</code>采用单例模式
	 * 
	 * @return <code>Operator</code>的唯一实例
	 */
	public static Operator getOperator() {
		if (instance == null)
			instance = new Operator();
		return instance;
	}

	/**
	 * 根据不同运算符执行相应的运算
	 * 
	 * @param operatorType
	 *            运算符类型
	 * @param leftValue
	 *            左操作数(被操作数)
	 * @param rightValue
	 *            右操作数(操作数)
	 * @return 运算结果
	 * @throws ZSException
	 */
	public final Object operate(int operatorType, Object leftValue,
			Object rightValue) throws ZSException {
		switch (operatorType) {
		case Syntax.OPERATOR_PLUS_TYPE:
			return plusOperate(leftValue, rightValue);
		case Syntax.OPERATOR_MINUS_TYPE:
			return minusOperate(leftValue, rightValue);
		case Syntax.OPERATOR_MULT_TYPE:
			return multOperate(leftValue, rightValue);
		case Syntax.OPERATOR_DIV_TYPE:
			return divOperate(leftValue, rightValue);
		case Syntax.LOGICAL_EQUAL_TYPE:
			return equalOperate(leftValue, rightValue);
		case Syntax.LOGICAL_NEGTIVE_TYPE:
			return notEqualOperate(leftValue, rightValue);
		case Syntax.OPERATOR_LT_TYPE:
			return ltOperate(leftValue, rightValue);
		case Syntax.OPERATOR_LTE_TYPE:
			return lteOperate(leftValue, rightValue);
		case Syntax.OPERATOR_GT_TYPE:
			return gtOperate(leftValue, rightValue);
		case Syntax.OPERATOR_GTE_TYPE:
			return gteOperate(leftValue, rightValue);
		case Syntax.OPERATOR_MOD_TYPE:
			return modOperate(leftValue, rightValue);
		case Syntax.LOGICAL_AND_TYPE:
			return andOperate(leftValue, rightValue);
		case Syntax.LOGICAL_OR_TYPE:
			return orOperate(leftValue, rightValue);
		}
		throw new ZSException();
	}

	/**
	 * 加法运算(或字符串链接)<br>
	 * 如果两个操作数都是整数,则进行两数相加;如果两操作数中有一个是字符串,则进行字符串连接
	 * 
	 * @param leftValue
	 *            被加数
	 * @param rightValue
	 *            加数
	 * @return 运算结果
	 * @throws ZSException
	 */
	private final Object plusOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数相加
			return new Integer(((Integer) leftValue).intValue()
					+ ((Integer) rightValue).intValue());
		} else if (leftValue instanceof String || rightValue instanceof String) {// 整数与数字相连
			return new String(leftValue.toString() + rightValue.toString());
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					"+号连接的两个值只能是整型或字符串");
		}
		return null;
	}

	/**
	 * 减法运算.减号只能连接连个整数,执行减法运算
	 * 
	 * @param leftValue
	 *            被减数
	 * @param rightValue
	 *            减数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object minusOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数相减
			return new Integer(((Integer) leftValue).intValue()
					- ((Integer) rightValue).intValue());
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "-号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 乘法运算.乘号只能连接连个整数,执行乘法运算
	 * 
	 * @param leftValue
	 *            被乘数
	 * @param rightValue
	 *            乘数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object multOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数相乘
			return new Integer(((Integer) leftValue).intValue()
					* ((Integer) rightValue).intValue());
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "*号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 除法运算.除号只能连接连个整数,执行除法运算.注意除数不能为0
	 * 
	 * @param leftValue
	 *            被除数
	 * @param rightValue
	 *            除数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object divOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (((Integer) rightValue).intValue() == 0)
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "除数为零");
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数相除
			return new Integer(((Integer) leftValue).intValue()
					/ ((Integer) rightValue).intValue());
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "/号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 模运算.模号只能连接连个整数,执行取模运算.注意除数不能为0
	 * 
	 * @param leftValue
	 *            被除数
	 * @param rightValue
	 *            除数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object modOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (((Integer) rightValue).intValue() == 0)
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "除数为零");
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数取模
			return new Integer(((Integer) leftValue).intValue()
					% ((Integer) rightValue).intValue());
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "%号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 逻辑与运算.
	 * 
	 * @param leftValue
	 *            左逻辑子式结果
	 * @param rightValue
	 *            右逻辑子式结果
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object andOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {
			boolean b1 = ((Integer) leftValue).intValue() != 0;
			boolean b2 = ((Integer) rightValue).intValue() != 0;
			if (b1 && b2) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "&&号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 逻辑或运算.
	 * 
	 * @param leftValue
	 *            左逻辑子式结果
	 * @param rightValue
	 *            右逻辑子式结果
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object orOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {
			boolean b1 = ((Integer) leftValue).intValue() != 0;
			boolean b2 = ((Integer) rightValue).intValue() != 0;
			if (b1 || b2) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION, "||号连接的两个值只能是整型");
		}
		return null;
	}

	/**
	 * 判断相等.判断的两者可以同为整数也可以同为字符串
	 * 
	 * @param leftValue
	 *            左操作数
	 * @param rightValue
	 *            右操作数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object equalOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 判断两整数相等
			if (leftValue.equals(rightValue)) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 判断两字符串相等
			if (leftValue.equals(rightValue)) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					"==号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}

	/**
	 * 小于.判断的两者可以同为整数也可以同为字符串.如果是字符串,则比较两字符串的长度
	 * 
	 * @param leftValue
	 *            左操作数
	 * @param rightValue
	 *            右操作数
	 * @return 计算结果
	 * @throws ZSException
	 */
	private final Object ltOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数比较大小
			if (((Integer) leftValue).intValue() < ((Integer) rightValue)
					.intValue()) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 两字符串比较长度
			if (((String) leftValue).compareTo((String) rightValue) < 0) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					"<号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}

	/**
	 * @see ltOperate
	 */
	private final Object lteOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数比较大小
			if (((Integer) leftValue).intValue() <= ((Integer) rightValue)
					.intValue()) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 两字符串比较长度
			if (((String) leftValue).compareTo((String) rightValue) <= 0) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					"<=号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}

	/**
	 *@see ltOperate
	 */
	private final Object gtOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数比较大小
			if (((Integer) leftValue).intValue() > ((Integer) rightValue)
					.intValue()) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 两字符串比较长度
			if (((String) leftValue).compareTo((String) rightValue) > 0) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					">号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}

	/**
	 *@see ltOperate
	 */
	private final Object gteOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数比较大小
			if (((Integer) leftValue).intValue() >= ((Integer) rightValue)
					.intValue()) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 两字符串比较长度
			if (((String) leftValue).compareTo((String) rightValue) >= 0) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					">=号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}

	/**
	 *@see equalOperate
	 */
	private final Object notEqualOperate(Object leftValue, Object rightValue)
			throws ZSException {
		if (leftValue instanceof Integer && rightValue instanceof Integer) {// 两整数比较大小
			if (!leftValue.equals(rightValue)) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else if (leftValue instanceof String && rightValue instanceof String) {// 两字符串比较
			if (!leftValue.equals(rightValue)) {
				return new Integer(1);
			} else {
				return new Integer(0);
			}
		} else {
			new ZSException(ZSException.ARITHMETIC_EXCEPTION,
					"!=号连接的两个值只能两个都是是整型或都是字符串");
		}
		return null;
	}
}
