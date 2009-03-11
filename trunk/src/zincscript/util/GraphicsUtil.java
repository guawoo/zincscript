package zincscript.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class GraphicsUtil {
	public final static int UP = 0x0001;
	public final static int DOWN = 0x0010;
	public final static int RIGHT = 0x0100;
	public final static int LEFT = 0x1000;
	private final static int SCAN_LEN = 4;

	public static final int[] getGradient(int startColor, int endColor,
			int steps) {
		int[] gradient = new int[steps];

		if (steps == 0) {
			return null;
		} else if (steps == 1) {
			gradient[0] = startColor;
			return gradient;
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0xff;
		int startGreen = (startColor >>> 8) & 0xff;
		int startBlue = startColor & 0xff;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0xff;
		int endGreen = (endColor >>> 8) & 0xff;
		int endBlue = endColor & 0xff;

		int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps - 1);
		int stepRed = ((endRed - startRed) << 8) / (steps - 1);
		int stepGreen = ((endGreen - startGreen) << 8) / (steps - 1);
		int stepBlue = ((endBlue - startBlue) << 8) / (steps - 1);
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;

		gradient[0] = startColor;
		for (int i = 0; i < steps; i++) {
			startAlpha += stepAlpha;
			startAlpha = startAlpha > 0xff00 ? 0xff00 : startAlpha;
			startRed += stepRed;
			startRed = startRed > 0xff00 ? 0xff00 : startRed;
			startGreen += stepGreen;
			startGreen = startGreen > 0xff00 ? 0xff00 : startGreen;
			startBlue += stepBlue;
			startBlue = startBlue > 0xff00 ? 0xff00 : startBlue;

			gradient[i] = ((startAlpha << 16) & 0xff000000)
					| ((startRed << 8) & 0x00ff0000)
					| (startGreen & 0x0000ff00) | (startBlue >>> 8);
			// | ((startBlue >>> 8) & 0x000000FF);
		}
		return gradient;
	}

	public static Image transImage(Image src, int percent) {
		if (src == null || percent <= 0)
			return null;
		if (percent == 100)
			return src;
		int srcW = src.getWidth();
		int srcH = src.getHeight();
		int dstW = srcW * percent / 100;
		int dstH = srcH * percent / 100;
		Image tmp = Image.createImage(dstW, srcH);
		Graphics g = tmp.getGraphics();
		// int scale = 16;
		int delta = (srcW << SCAN_LEN) / dstW; // 扫描长度
		int pos = delta >> 1; // 扫描位置
		for (int x = 0; x < dstW; x++) {
			g.setClip(x, 0, 1, srcH);
			g.drawImage(src, x - (pos >> SCAN_LEN), 0, 20);
			pos += delta;
		}
		Image newImage = Image.createImage(dstW, dstH);
		g = newImage.getGraphics();
		delta = (srcH << SCAN_LEN) / dstH;
		pos = delta >> 1;
		for (int y = 0; y < dstH; y++) {
			g.setClip(0, y, dstW, 1);
			g.drawImage(tmp, 0, y - (pos >> SCAN_LEN), 20);
			pos += delta;
		}
		g = null;
		tmp = null;
		return newImage;
	}

	/**
	 * 画渐进色背景
	 * 
	 * @param g
	 *            Graphics
	 * @param startColor
	 *            int 开始颜色
	 * @param endColor
	 *            int 结束颜色
	 * @param unit
	 *            int 渐进度
	 * @param x
	 *            int 坐标
	 * @param y
	 *            int
	 * @param width
	 *            int
	 * @param height
	 *            int
	 * @param direction
	 *            int 方向
	 */
	public final static void drawShadeRect(Graphics g, int startColor,
			int endColor, int x, int y, int width, int height, int direction) {
		int steps = 0;
		int unit = 0;
		if ((direction & 0x00ff) == 0) {
			// 左右渐变
			unit = width / (endColor - startColor);
			if (unit < 0)
				unit = -1 * unit;
			else if (unit == 0)
				unit = 1;
			steps = width / unit;
		} else {
			// 上下渐变
			unit = height / (endColor - startColor);
			if (unit < 0)
				unit = -1 * unit;
			else if (unit == 0)
				unit = 1;
			steps = height / unit;
		}
		if ((direction & 0x00ff) == 0)
			// 左右渐变
			steps = width / unit;
		else
			// 上下渐变
			steps = height / unit;
		// 获取颜色渐变的梯度颜色值
		int[] colors = getGradient(startColor, endColor, steps);
		// System.out.println("显示高度："+height+"steps："+steps+"数组长度："+colors.length
		// );
		for (int i = 0; i < steps; i++) {
			g.setColor(colors[i]);
			switch (direction) {
			case UP:
				g.fillRect(x, y + height - unit * (i + 1), width, unit);
				break;
			case LEFT:
				g.fillRect(x + width - unit * (i + 1), y, unit, height);
				break;
			case RIGHT:
				g.fillRect(x + unit * i, y, unit, height);
				break;
			default:
				g.fillRect(x, y + unit * i, width, unit);
				break;
			}
		}
		colors = null;
	}

	/***
	 * drawShadeRoundRect() 画(圆角)矩形的方法
	 * 
	 * startColor 颜色开始值 endColor 颜色结束值 x 矩形开始x坐标 y 矩形开始y坐标 width 矩形宽度 height
	 * 矩形高度 arcWidth 圆角宽 arcHeight 圆角高
	 * */
	// public final static void drawShadeRoundRect(Graphics g, int startColor,
	// int endColor, int x, int y, int width, int height, int direction) {
	// int steps = 0; // 要画的颜色数目
	// int unit = 0; // 一种颜色对应多少个象素
	// if (direction == LEFT || direction == RIGHT) {
	// // 左右渐变
	// if (endColor == startColor)
	// unit = width;
	// else
	// unit = width / (endColor - startColor);
	// if (unit < 0)
	// unit = -1 * unit;
	// else if (unit == 0)
	// unit = 1;
	// steps = width / unit;
	// } else {
	// // 上下渐变
	// if (endColor == startColor)
	// unit = height;
	// else
	// unit = height / (endColor - startColor);
	// if (unit < 0)
	// unit = -1 * unit;
	// else if (unit == 0)
	// unit = 1;
	// steps = height / unit;
	// }
	// // 获取颜色渐变的梯度颜色值
	// int[] colors = getGradient(startColor, endColor, steps);
	// colors[steps - 1] = endColor;
	//
	// int colorsIndex = 0; // 颜色数组的下标
	//
	// if (direction == UP || direction == DOWN) {
	// int colorChangeTime = drawLengthsUD.length / unit; // 画第一部分，要变多少次颜色
	// if (drawLengthsUD.length % unit != 0)
	// colorChangeTime++;
	// for (int i = 0; i < colorChangeTime; i++) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// for (int j = i * unit; j < (i + 1) * unit; j++) {
	// if (j >= drawLengthsUD.length)
	// break;
	// g.fillRect(x + drawLengthsUD[j], y + j, width - 2
	// * drawLengthsUD[j], 1);
	// }
	// }
	//
	// y = y + drawLengthsUD.length;
	// int part2Lenght = unit - drawLengthsUD.length % unit; // 画第二部分长度
	// if (part2Lenght > height - drawLengthsDU.length
	// - drawLengthsUD.length)
	// part2Lenght = height - drawLengthsDU.length
	// - drawLengthsUD.length;
	// // System.out.println("第二部分part2Lenght == "+part2Lenght);
	// g.fillRect(x, y, width, part2Lenght);
	//
	// // 画第三部分，要变多少次颜色
	// colorChangeTime = (height - drawLengthsUD.length
	// - drawLengthsDU.length - part2Lenght)
	// / unit;
	// y = y + part2Lenght;
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// // System.out.println("第三部分colorChangeTime == "+colorChangeTime);
	// for (int i = 0; i < colorChangeTime; i++) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// g.fillRect(x, y + i * unit, width, unit);
	// }
	//
	// y = y + unit * colorChangeTime;
	// // 画第四部分长度
	// int part4Lenght = height - drawLengthsUD.length
	// - drawLengthsDU.length - part2Lenght - unit
	// * colorChangeTime;
	// // System.out.println("第四部分part4Lenght == "+part4Lenght);
	// if (part4Lenght < 0)
	// part4Lenght = 0;
	// g.fillRect(x, y, width, part4Lenght);
	//
	// // 画第五部分
	// y = y + part4Lenght;
	// for (int i = 0; i < drawLengthsDU.length; i++) {
	// if ((y + i) % unit == 0) {
	// if (colorsIndex < colors.length) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// }
	// }
	// g.fillRect(x + drawLengthsDU[i], y + i, width - 2
	// * drawLengthsDU[i], 1);
	// }
	// } else if (direction == RIGHT || direction == LEFT) {
	// int colorChangeTime = drawLengthsLR.length / unit;
	// if (drawLengthsLR.length % unit != 0)
	// colorChangeTime++;
	// for (int i = 0; i < colorChangeTime; i++) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// for (int j = i * unit; j < (i + 1) * unit; j++) {
	// if (j >= drawLengthsLR.length)
	// break;
	// g.fillRect(x + j, y + drawLengthsLR[j], 1, height - 2
	// * drawLengthsLR[j]);
	// }
	// }
	// x = x + drawLengthsLR.length;
	// int part2Lenght = unit - drawLengthsLR.length % unit;
	// if (part2Lenght > width - drawLengthsRL.length
	// - drawLengthsLR.length)
	// part2Lenght = width - drawLengthsRL.length
	// - drawLengthsLR.length;
	// g.fillRect(x, y, part2Lenght, height);
	// colorChangeTime = (width - drawLengthsLR.length
	// - drawLengthsRL.length - part2Lenght)
	// / unit;
	// x = x + part2Lenght;
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// for (int i = 0; i < colorChangeTime; i++) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// g.fillRect(x + i * unit, y, unit, height);
	// }
	//
	// x = x + unit * colorChangeTime;
	// int part4Lenght = width - drawLengthsLR.length
	// - drawLengthsRL.length - part2Lenght - unit
	// * colorChangeTime;
	// if (part4Lenght < 0)
	// part4Lenght = 0;
	// g.fillRect(x, y, part4Lenght, height);
	//
	// x = x + part4Lenght;
	// for (int i = 0; i < drawLengthsLR.length; i++) {
	// if ((y + i) % unit == 0) {
	// if (colorsIndex < colors.length) {
	// if (colorsIndex >= colors.length)
	// colorsIndex = colors.length - 1;
	// g.setColor(colors[colorsIndex++]);
	// }
	// }
	// g.fillRect(x + i, y + drawLengthsRL[i], 1, height - 2
	// * drawLengthsRL[i]);
	// }
	// }
	// colors = null;
	// }
}
