package zincfish.zincscript.lib;

import java.util.Hashtable;
import java.util.Random;
import screen.BrowserScreen;
import utils.ArrayList;
import zincfish.zincscript.core.ZSException;

/**
 * <code>StdLib</code>是一个预先实现的标准函数库<br>
 * 库中实现了一些常用方法，方便脚本使用者编写程序<br>
 * 为了解析上方便，规定库函数一律用下划线开头
 * 
 * @author Jarod Yv
 */
public final class StdLib extends AbstactLib {
	private static final int FUNCTION_NUM = 10;
	private static final String PRINT = "_zssprint";
	private static final byte PRINT_CODE = 1;
	private static final String PRINTLN = "_zssprintln";
	private static final byte PRINTLN_CODE = 2;
	private static final String EXIT = "_zssExit";
	private static final byte EXIT_CODE = 3;
	private static final String SUB_STRING = "_zssSubString";
	private static final byte SUB_STRING_CODE = 4;
	private static final String STRING_LEN = "_zssStringLen";
	private static final byte STRING_LEN_CODE = 5;
	private static final String RANDOM = "_zssRandom";
	private static final byte RANDOM_CODE = 6;
	private static final String ABS = "_zssABS";
	private static final byte ABS_CODE = 7;
	private static final String SWITCH = "_zssSwitch";
	private static final byte SWITCH_CODE = 8;

	/**
	 * 所有库都采用单例模式，确保内存中已有一份库的实例
	 */
	private static AbstactLib instance = null;

	public static AbstactLib getInstance() {
		if (instance == null) {
			instance = new StdLib();
			instance.createFunctionMap();
		}
		return instance;
	}

	protected void createFunctionMap() {
		if (functionMap == null)
			functionMap = new Hashtable(FUNCTION_NUM);
		functionMap.put(PRINT, new Byte(PRINT_CODE));
		functionMap.put(PRINTLN, new Byte(PRINTLN_CODE));
		functionMap.put(EXIT, new Byte(EXIT_CODE));
		functionMap.put(SUB_STRING, new Byte(SUB_STRING_CODE));
		functionMap.put(STRING_LEN, new Byte(STRING_LEN_CODE));
		functionMap.put(RANDOM, new Byte(RANDOM_CODE));
		functionMap.put(ABS, new Byte(ABS_CODE));
		functionMap.put(SWITCH, new Byte(SWITCH_CODE));
	}

	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		Byte code = (Byte) functionMap.get(name);
		if (code != null) {
			switch (code.byteValue()) {
			case PRINT_CODE:
				_zssprint(params);
				return null;
			case PRINTLN_CODE:
				_zssprintln(params);
				return null;
			case EXIT_CODE:
				_zssExit();
				return null;
			case SUB_STRING_CODE:
				return _zssSubString(params);
			case STRING_LEN_CODE:
				return _zssStringLen(params);
			case RANDOM_CODE:
				return _zssRandom(params);
			case ABS_CODE:
				return _zssABS(params);
			case SWITCH_CODE:
				_zssSwitch(params);
				return null;
			default:
				throw new ZSException("函数" + name + "不存在");
			}
		} else {
			throw new ZSException("函数" + name + "不存在");
		}
	}

	private void _zssprint(ArrayList param) {
		if (param == null || param.size() == 0)
			return;
		String s = "";
		for (int i = 0; i < param.size(); i++)
			s += param.get(i);
		System.out.print(s);
		s = null;
	}

	private void _zssprintln(ArrayList param) {
		_zssprint(param);
		System.out.println();
	}

	private void _zssExit() {
		// TestMIDlet.Exit();
	}

	private Object _zssSubString(ArrayList params) {
		String s = (String) params.get(0);
		int b = ((Integer) params.get(1)).intValue();
		int e = ((Integer) params.get(2)).intValue();
		return s.substring(b, e);
	}

	private Object _zssStringLen(ArrayList params) {
		String s = (String) params.get(0);
		int l = s.length();
		s = null;
		return new Integer(l);
	}

	private Random random = null;

	private Object _zssRandom(ArrayList params) {
		if (random == null)
			random = new Random(System.currentTimeMillis());
		int base = 0;
		int i = 0;
		int ignore = Integer.MIN_VALUE;
		if (params.size() == 3)
			ignore = ((Integer) params.remove(2)).intValue();
		if (params.size() == 2)
			base = ((Integer) params.remove(0)).intValue();
		i = ((Integer) params.get(0)).intValue();
		do {
			i = i - base + 1;
			base += Math.abs(random.nextInt()) % i;
		} while (base == ignore);
		return new Integer(base);
	}

	private Object _zssABS(ArrayList params) {
		Object o = params.get(0);
		if (o instanceof Integer) {
			Integer interger = (Integer) o;
			int i = interger.intValue();
			if (i < 0)
				return new Integer(-i);
			else
				return interger;
		}
		return o;
	}

	private void _zssSwitch(ArrayList params) {
		String path = (String) params.get(0);
		BrowserScreen.getInstance().loadUnit(path);
	}
}
