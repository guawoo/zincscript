package zincscript.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import zincscript.util.ArrayList;

/**
 * <code>ScriptEngine</code>是对解析执行引擎interpreter进行了一层封装，只暴露出必要的接口
 * 
 * @author Jarod Yv
 */
public class ZincScript {
	/* 自身的实例 */
	private static ZincScript zincScript = null;
	/* 解释执行引擎 */
	private Interpreter interpreter = null;
	/* 脚本装载器 */
	private ScriptLoader scriptLoader = null;

	/**
	 * 获取脚本引擎的唯一实例
	 * 
	 * @return ZincScript实例
	 */
	public static ZincScript getZincScript() {
		if (zincScript == null)
			zincScript = new ZincScript();
		return zincScript;
	}

	/*
	 * 构造函数
	 */
	private ZincScript() {
		interpreter = new Interpreter(this);
		scriptLoader = new ScriptLoader();
		interpreter.setCode(scriptLoader);
	}

	/**
	 * 装载脚本
	 * 
	 * @param path
	 *            脚本文件的路径
	 */
	public void loadScript(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		path = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		try {
			int ch = -1;
			while ((ch = is.read(data)) != -1) {
				baos.write(data, 0, ch);
			}
			data = null;
			data = baos.toByteArray();
			loadScript(data);
		} catch (Exception e) {
			data = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				} finally {
					is = null;
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				} finally {
					baos = null;
				}
			}
			data = null;
		}
	}

	/**
	 * 将脚本数据载入脚本装载器
	 * 
	 * @param scriptData
	 *            脚本数据
	 */
	public void loadScript(byte[] scriptData) {
		if (scriptData == null)
			return;
		String script = null;
		try {
			script = new String(scriptData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		scriptLoader.addLines(script);
		script = null;
	}

	/**
	 * 解释执行当前载入的代码
	 * 
	 * @return 脚本的执行结果
	 */
	public Object executeScript() throws ZSException {
		try {
			interpreter.reset();
			return interpreter.interprete(0, scriptLoader.totalLineNum() - 1);
		} catch (ZSException e) {
			if (e.getType() == ZSException.INTERRUPTION_EXCEPTION) {
				scriptLoader.reset();
				loadScript((String) interpreter.getReturnValue());
				executeScript();
			}
			return interpreter.getReturnValue();
		}
	}

	/**
	 * 重置
	 */
	public void reset() {
		scriptLoader.reset();
		interpreter.reset();
		System.gc();
	}

	/**
	 * 从正在执行的脚本中退出
	 * 
	 * @param o
	 **/
	public void exit(Object o) throws ZSException {
		interpreter.exit(o);
	}

	/**
	 * 销毁
	 */
	public void release() {
		interpreter.release();
		interpreter = null;
		scriptLoader.release();
		scriptLoader = null;
		zincScript = null;
		System.gc();
	}

	/**
	 * 从脚本的当前行继续往下执行
	 * 
	 * @return 脚本执行的结束
	 */
	public Object resume() throws ZSException {
		if (scriptLoader.getCurLine() == 0) {
			return executeScript();
		} else {
			return interpreter.interprete(scriptLoader.getCurLine() + 1,
					scriptLoader.totalLineNum() - 1);
		}
	}

	/**
	 * 函数调用
	 * 
	 * @param name
	 *            函数名
	 * @param params
	 *            参数列表
	 * @return 函数执行结果
	 */
	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		try {
			return interpreter.callFunction(name, params);
		} catch (ZSException e) {
			if (e.getType() == ZSException.INTERRUPTION_EXCEPTION) {
				String s = e.getMessage();
				scriptLoader.reset();
				loadScript(s);
				s = null;
				executeScript();
			} else {
				throw e;
			}
			return interpreter.getReturnValue();
		}
	}
}
