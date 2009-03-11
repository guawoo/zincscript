package zincscript.test;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import utils.ArrayList;
import zincscript.core.ZincScript;
import zincscript.core.ZSException;
import zincscript.lib.GraphicsLib;

public class TestCanvas extends Canvas {
	public static final Font UI_FONT = Font.getFont(Font.FACE_SYSTEM,
			Font.STYLE_PLAIN, Font.SIZE_SMALL);
	private ZincScript zinc = null;// ZincScript引擎

	public TestCanvas() {
		// setFullScreenMode(true);
		zinc = ZincScript.getZincScript();// 获取ZincScript引擎
		zinc.loadScript("/menu.zs");// 载入脚本
		try {
			zinc.executeScript();// 解释并执行脚本初始化代码
		} catch (ZSException e) {
			e.printStackTrace();
		}
	}

	protected void paint(Graphics g) {
		g.setFont(UI_FONT);
		GraphicsLib.g = g;// 传入Graphics的引用，供图形库函数使用
		GraphicsLib.canvas = this;// 传入当前Canvas的引用，供图形库函数使用
		try {
			zinc.callFunction("paint", null);// 调用脚本的"paint"函数完成屏幕绘制
		} catch (ZSException e) {
			e.printStackTrace();
		}
	}

	protected void keyPressed(int keycode) {
		try {
			ArrayList params = new ArrayList(1);// 用于保存参数
			params.add(new Integer(keycode));
			zinc.callFunction("keypress", params);// 调用脚本的"keypress"函数完成按键响应
			params = null;
		} catch (ZSException e) {
			e.printStackTrace();
		}
		repaint();
	}

}
