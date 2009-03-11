package zincscript.core;

import utils.ArrayList;

/**
 * <code>LineLoader</code>用于以行为单位获取脚本语句
 */
public final class ScriptLoader {

	public ArrayList lines = null;
	
	private int curLine = 0;

	public ScriptLoader() {
		lines = new ArrayList(20);
		curLine = 0;
	}

	/**
	 * 重置
	 */
	public final void reset() {
		lines = null;
		lines = new ArrayList(20);
		curLine = 0;
	}

	/**
	 * 销毁
	 */
	public final void release() {
		if (lines != null) {
			lines.removeAll();
			lines = null;
		}
	}

	/**
	 * 添加一行
	 * 
	 * @param s
	 *            行内容
	 */
	public final void addLine(String s) {
		if (s != null && !s.trim().equals("")) {
			lines.add(s);
		} else {
			lines.add("");
		}
	}

	/**
	 *加入多行，用\n分隔
	 * 
	 * @param s
	 *            行内容
	 */
	public final void addLines(String s) {
		int pos = 0;
		if (s != null && !s.trim().equals("")) {
			pos = s.indexOf('\n');
			while (pos >= 0) {
				addLine(s.substring(0, pos));
				s = s.substring(pos + 1, s.length());
				pos = s.indexOf('\n');
			}
			if (!s.trim().equals("")) {
				addLine(s);
			}
		}
	}
	
	public final void remove(int index){
		lines.remove(index);
	}

	/**
	 * 设置当前行号
	 * 
	 * @param n
	 *            行号
	 */
	public final void setCurrentLine(int n) {
		if (lines != null && lines.size() < n) {
			n = lines.size() - 1;
		} else if (n < 0) {
			n = 0;
		}
		curLine = n;
	}

	/**
	 * 获取当前行行号
	 */
	public final int getCurLine() {
		return curLine;
	}

	/**
	 * 获取总行数
	 */
	public final int totalLineNum() {
		return lines == null ? 0 : lines.size();
	}

	/**
	 * 获取当前行内容
	 */
	public final String getCurrentLine() {
		return lines == null ? null : (String) lines.get(curLine);
	}

	/**
	 * 获取指定行内容
	 */
	public final String getLine(int n) {
		if (lines == null || n < 0 || n >= lines.size())
			return "";
		return (String) lines.get(n);
	}
	
}
