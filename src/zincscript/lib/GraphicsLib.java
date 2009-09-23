package zincscript.lib;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import zincscript.core.ZSException;
import zincscript.util.ArrayList;
import zincscript.util.GraphicsUtil;

/**
 * <code>GraphicsLib</code>是一个预先实现的图形函数库，用于在屏幕上绘图<br>
 * <code>GraphicsLib</code>封装了绝大多数J2ME绘图操作，可以像在J2ME中一样在当前屏幕上绘图。<br>
 * <code>GraphicsLib</code>库中的函数全部以<em>_zsg</em>开头
 * 
 * @author Jarod Yv
 */
public final class GraphicsLib extends AbstactLib {
	private static final int FUNCTION_NUM = 13;// 库函数个数,用于制定HashTable大小

	private static final String SET_COLOR = "_zsgSetColor";
	private static final byte SET_COLOR_CODE = 1;

	private static final String FILL_RECT = "_zsgFillRect";
	private static final byte FILL_RECT_CODE = 2;

	private static final String DRAW_RECT = "_zsgDrawRect";
	private static final byte DRAW_RECT_CODE = 3;

	private static final String CANVAS_WIDTH = "_zsgCanvasWidth";
	private static final byte CANVAS_WIDTH_CODE = 4;

	private static final String CANVAS_HEIGHT = "_zsgCanvasHeight";
	private static final byte CANVAS_HEIGHT_CODE = 5;

	private static final String DRAW_LINE = "_zsgDrawLine";
	private static final byte DRAW_LINE_CODE = 6;

	private static final String STRING_WIDTH = "_zsgGetStringWidth";
	private static final byte STRING_WIDTH_CODE = 7;

	private static final String DRAW_STRING = "_zsgDrawString";
	private static final byte DRAW_STRING_CODE = 8;

	private static final String FONT_HEIGHT = "_zsgGetFontHeight";
	private static final byte FONT_HEIGHT_CODE = 9;

	private static final String LOAD_IMAGE = "_zsgLoadImage";
	private static final byte LOAD_IMAGE_CODE = 10;

	private static final String DRAW_IMAGE = "_zsgDrawImage";
	private static final byte DRAW_IMAGE_CODE = 11;

	private static final String DRAW_GRADIENT = "_zsgDrawGradient";
	private static final byte DRAW_GRADIENT_CODE = 12;

	private static final String FONT_WIDTH = "_zsgGetFontWidth";
	private static final byte FONT_WIDTH_CODE = 13;

	/**
	 * 所有库都采用单例模式，确保内存中已有一份库的实例
	 */
	private static AbstactLib instance = null;

	/**
	 * g为当前Canvas的Graphics
	 */
	public static Graphics g = null;
	/**
	 * 当前Canvas
	 */
	public static Canvas canvas = null;
	// 图片资源
	private Hashtable images = null;

	/**
	 * 获取<code>GraphicsLib</code>的唯一实例
	 * 
	 * @return GraphicsLib的实例
	 */
	public static AbstactLib getInstance() {
		if (instance == null) {
			instance = new GraphicsLib();
			instance.createFunctionMap();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see zincscript.lib.AbstactLib#createFunctionMap()
	 */
	protected void createFunctionMap() {
		if (functionMap == null)
			functionMap = new Hashtable(FUNCTION_NUM);
		functionMap.put(SET_COLOR, new Byte(SET_COLOR_CODE));
		functionMap.put(FILL_RECT, new Byte(FILL_RECT_CODE));
		functionMap.put(DRAW_RECT, new Byte(DRAW_RECT_CODE));
		functionMap.put(DRAW_LINE, new Byte(DRAW_LINE_CODE));
		functionMap.put(CANVAS_WIDTH, new Byte(CANVAS_WIDTH_CODE));
		functionMap.put(CANVAS_HEIGHT, new Byte(CANVAS_HEIGHT_CODE));
		functionMap.put(STRING_WIDTH, new Byte(STRING_WIDTH_CODE));
		functionMap.put(DRAW_STRING, new Byte(DRAW_STRING_CODE));
		functionMap.put(FONT_HEIGHT, new Byte(FONT_HEIGHT_CODE));
		functionMap.put(FONT_WIDTH, new Byte(FONT_WIDTH_CODE));
		functionMap.put(LOAD_IMAGE, new Byte(LOAD_IMAGE_CODE));
		functionMap.put(DRAW_IMAGE, new Byte(DRAW_IMAGE_CODE));
		functionMap.put(DRAW_GRADIENT, new Byte(DRAW_GRADIENT_CODE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see zincscript.lib.AbstactLib#callFunction(java.lang.String,
	 * zincscript.util.ArrayList)
	 */
	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		Byte code = (Byte) functionMap.get(name);
		if (code != null) {
			switch (code.byteValue()) {
			case SET_COLOR_CODE:
				_zsgSetColor(params);
				return null;
			case FILL_RECT_CODE:
				_zsgFillRect(params);
				return null;
			case DRAW_RECT_CODE:
				_zsgDrawRect(params);
				return null;
			case DRAW_LINE_CODE:
				_zsgDrawLine(params);
				return null;
			case CANVAS_WIDTH_CODE:
				return _zsgCanvasWidth();
			case CANVAS_HEIGHT_CODE:
				return _zsgCanvasHeight();
			case STRING_WIDTH_CODE:
				return _zsgGetStringWidth(params);
			case FONT_HEIGHT_CODE:
				return _zsgGetFontHeight();
			case DRAW_STRING_CODE:
				_zsgDrawString(params);
				return null;
			case LOAD_IMAGE_CODE:
				_zsgLoadImage(params);
				return null;
			case DRAW_IMAGE_CODE:
				_zsgDrawImage(params);
				return null;
			case DRAW_GRADIENT_CODE:
				_zsgDrawGradient(params);
				return null;
			case FONT_WIDTH_CODE:
				return _zsgGetFontWidth();
			default:
				throw new ZSException("函数" + name + "不存在");
			}
		} else {
			throw new ZSException("函数" + name + "不存在");
		}
	}

	/**
	 * 设置颜色.等同于g.setColor(int color)<br>
	 * 本方法接受一个整形的颜色值.如果没有传入参数或参数类型不正确,则默认为白色.如果传入多个参数,只有第一个参数有效.
	 * 
	 * @param param
	 *            参数列表.本方法只有一个整形的参数值,表示颜色值
	 */
	private void _zsgSetColor(ArrayList param) {
		if (param == null || param.size() == 0) {
			g.setColor(0xffffff);
		} else {
			Object o = param.get(0);
			if (o instanceof Integer)
				g.setColor(((Integer) o).intValue());
			else
				g.setColor(0xffffff);
			o = null;
		}
	}

	/**
	 * 填充矩形,等同于g.fillRect(int x, int y, int w, int h)<br>
	 * 本方法可接受4个或5个参数,即存在重载方法:
	 * <ul>
	 * <li>_zsgFillRect(int x, int y, int w, int h)
	 * <li>_zsgFillRect(int color, int x, int y, int w, int h)
	 * </ul>
	 * 后者多出一个代表填充颜色的颜色值,其等价于
	 * 
	 * <pre>
	 * _zsgSetColor(color)
	 * _zsgFillRect(x, y, w, h)
	 * </pre>
	 * 
	 * @param param
	 *            参数列表
	 */
	private void _zsgFillRect(ArrayList param) {
		handleColor(param);
		int x = ((Integer) param.get(0)).intValue();
		int y = ((Integer) param.get(1)).intValue();
		int w = ((Integer) param.get(2)).intValue();
		int h = ((Integer) param.get(3)).intValue();
		g.fillRect(x, y, w, h);
	}

	/**
	 * 绘制矩形,等同于g.drawRect(int x, int y, int w, int h)<br>
	 * 本方法可接受4个或5个参数,即存在重载方法:
	 * <ul>
	 * <li>_zsgDrawRect(int x, int y, int w, int h)
	 * <li>_zsgDrawRect(int color, int x, int y, int w, int h)
	 * </ul>
	 * 后者多出一个代表填充颜色的颜色值,其等价于
	 * 
	 * <pre>
	 * _zsgSetColor(color)
	 * _zsgDrawRect(x, y, w, h)
	 * </pre>
	 * 
	 * @param param
	 *            参数列表
	 */
	private void _zsgDrawRect(ArrayList param) {
		handleColor(param);
		int x = ((Integer) param.get(0)).intValue();
		int y = ((Integer) param.get(1)).intValue();
		int w = ((Integer) param.get(2)).intValue();
		int h = ((Integer) param.get(3)).intValue();
		g.drawRect(x, y, w, h);
	}

	/**
	 * 绘制矩形,等同于g.drawLine(int x, int y, int w, int h)<br>
	 * 本方法可接受4个或5个参数,即存在重载方法:
	 * <ul>
	 * <li>_zsgDrawLine(int x, int y, int w, int h)
	 * <li>_zsgDrawLine(int color, int x, int y, int w, int h)
	 * </ul>
	 * 后者多出一个代表填充颜色的颜色值,其等价于
	 * 
	 * <pre>
	 * _zsgSetColor(color)
	 * _zsgDrawLine(x, y, w, h)
	 * </pre>
	 * 
	 * @param param
	 *            参数列表
	 */
	private void _zsgDrawLine(ArrayList param) {
		handleColor(param);
		int x = ((Integer) param.get(0)).intValue();
		int y = ((Integer) param.get(1)).intValue();
		int w = ((Integer) param.get(2)).intValue();
		int h = ((Integer) param.get(3)).intValue();
		g.drawLine(x, y, w, h);
	}

	/**
	 * 获取字符串的长度
	 * 
	 * @param param
	 *            参数列表
	 * @return 字符串的长度
	 */
	private Object _zsgGetStringWidth(ArrayList param) {
		String s = (String) param.get(0);
		int w = g.getFont().stringWidth(s);
		s = null;
		return new Integer(w);
	}

	/**
	 * 获取字体高度
	 * 
	 * @return 字体高度
	 */
	private Object _zsgGetFontHeight() {
		int h = g.getFont().getHeight();
		return new Integer(h);
	}

	/**
	 * 获取字体宽度
	 * 
	 * @return 字体宽度
	 */
	private Object _zsgGetFontWidth() {
		int w = g.getFont().charWidth('宽');
		return new Integer(w);
	}

	/**
	 * 绘制字符串,等同于g.drawString(String s, int x, int y, int anchor)<br>
	 * 本方法可接受4个或5个参数,即存在重载方法:
	 * <ul>
	 * <li>_zsgDrawString(string s, int x, int y, int anchor)
	 * <li>_zsgDrawString(int color, string s, int x, int y, int anchor)
	 * </ul>
	 * 后者多出一个代表填充颜色的颜色值,其等价于
	 * 
	 * <pre>
	 * _zsgSetColor(color)
	 * _zsgDrawString(s, x, y, anchor)
	 * </pre>
	 * 
	 * @param param
	 *            参数列表
	 */
	private void _zsgDrawString(ArrayList param) {
		handleColor(param);
		Object o = param.get(0);
		// String s = (String) param.get(0);
		if (o == null)
			return;
		int x = ((Integer) param.get(1)).intValue();
		int y = ((Integer) param.get(2)).intValue();
		int align = ((Integer) param.get(3)).intValue();
		g.drawString(o.toString(), x, y, align);
		o = null;
	}

	/**
	 * 获取屏幕的宽度
	 * 
	 * @return 屏幕宽度
	 */
	private Object _zsgCanvasWidth() {
		return new Integer(canvas.getWidth());
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @return 屏幕高度
	 */
	private Object _zsgCanvasHeight() {
		return new Integer(canvas.getHeight());
	}

	/**
	 * 载入图片资源,以图片的文件名作为参数
	 * 
	 * @param params
	 *            参数列表
	 */
	private void _zsgLoadImage(ArrayList params) {
		if (params == null || params.size() == 0)
			return;
		if (images == null)
			images = new Hashtable(params.size());
		else
			images.clear();
		for (int i = 0; i < params.size(); i++) {
			try {
				String s = (String) params.get(i);
				Image image = Image.createImage(s);
				images.put(s, image);
				s = null;
				image = null;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 绘制图片,以图片名作为参数
	 * 
	 * @param params
	 *            参数列表
	 */
	private void _zsgDrawImage(ArrayList params) {
		if (params == null || params.size() == 0)
			return;
		Object o = (String) params.get(0);
		Image image = (Image) images.get(o);
		o = null;
		if (image != null) {
			int x = ((Integer) params.get(1)).intValue();
			int y = ((Integer) params.get(2)).intValue();
			int align = ((Integer) params.get(3)).intValue();
			g.drawImage(image, x, y, align);
			image = null;
		}
	}

	/**
	 * 绘制渐进色.该方法接受7个参数,分别是:
	 * <ul>
	 * <li>startColor-开始颜色
	 * <li>endColor-结束颜色
	 * <li>x-绘制的起始横坐标
	 * <li>y-绘制的起始纵坐标
	 * <li>w-宽度
	 * <li>h-高度
	 * <li>direction-渐进方向
	 * </ul>
	 * 
	 * @param params
	 *            参数列表
	 */
	private void _zsgDrawGradient(ArrayList params) {
		int startColor = ((Integer) params.get(0)).intValue();
		int endColor = ((Integer) params.get(1)).intValue();
		int x = ((Integer) params.get(2)).intValue();
		int y = ((Integer) params.get(3)).intValue();
		int w = ((Integer) params.get(4)).intValue();
		int h = ((Integer) params.get(5)).intValue();
		int direction = ((Integer) params.get(6)).intValue();
		GraphicsUtil.drawShadeRect(g, startColor, endColor, x, y, w, h,
				direction);
	}

	private void handleColor(ArrayList param) {
		if (param == null || param.size() < 4 || param.size() > 5)
			return;
		if (param.size() == 5) {
			Integer i = (Integer) param.remove(0);
			g.setColor(i.intValue());
			i = null;
		}
	}
}
