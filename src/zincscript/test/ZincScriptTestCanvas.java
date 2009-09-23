package zincscript.test;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import zincscript.core.ZincScript;
import zincscript.core.ZSException;
import zincscript.lib.GraphicsLib;
import zincscript.util.ArrayList;

/**
 * <code>TestCanvas</code>是测试Demo的Canvas
 * 
 * @author Jarod Yv
 */
public class ZincScriptTestCanvas extends Canvas {
	/* paint函数调用的框架函数名 */
	private static final String FRAME_FUNC_PAINT = "paint";

	/* keyPressed函数调用的框架函数名 */
	private static final String FRAME_FUNC_KEYPRESS = "keypress";

	/* 默认的字体 */
	private static final Font UI_FONT = Font.getFont(Font.FACE_SYSTEM,
			Font.STYLE_PLAIN, Font.SIZE_SMALL);

	/* ZincScript引擎 */
	private ZincScript zinc = null;

	/**
	 * 构造函数
	 */
	public ZincScriptTestCanvas() {
		setFullScreenMode(true);
		zinc = ZincScript.getZincScript();// 获取ZincScript引擎
		zinc.loadScript("/menu.zs");// 载入脚本
		try {
			zinc.executeScript();// 解释并执行脚本初始化代码
		} catch (ZSException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		g.setFont(UI_FONT);
		GraphicsLib.g = g;// 传入Graphics的引用，供图形库函数使用
		GraphicsLib.canvas = this;// 传入当前Canvas的引用，供图形库函数使用
		try {
			zinc.callFunction(FRAME_FUNC_PAINT, null);// 调用脚本的"paint"函数完成屏幕绘制
		} catch (ZSException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keycode) {
		try {
			ArrayList params = new ArrayList(1);// 用于保存参数
			params.add(new Integer(keycode));
			zinc.callFunction(FRAME_FUNC_KEYPRESS, params);// 调用脚本的"keypress"函数完成按键响应
			params = null;
		} catch (ZSException e) {
			e.printStackTrace();
		}
		repaint();
	}

}
