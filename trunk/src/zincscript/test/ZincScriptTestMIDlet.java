package zincscript.test;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * <code>ZincScriptTestMIDlet</code> 是测试Demo的MIDlet，程序的执行入口
 * 
 * @author Jarod Yv
 */
public class ZincScriptTestMIDlet extends MIDlet {
	/** 指向MIDlet本身的指针 */
	public static ZincScriptTestMIDlet midlet = null;

	/**
	 * 构造函数
	 */
	public ZincScriptTestMIDlet() {
		midlet = this;
	}

	protected void startApp() throws MIDletStateChangeException {
		Display display = Display.getDisplay(midlet);
		ZincScriptTestCanvas testCanvas = new ZincScriptTestCanvas();
		display.setCurrent(testCanvas);
		testCanvas = null;
		display = null;
	}

	protected void destroyApp(boolean arg0) {
	}

	protected void pauseApp() {
	}

	/**
	 * 退出程序
	 */
	public static void Exit() {
		midlet.destroyApp(true);
		midlet.notifyDestroyed();
		midlet = null;
	}

}
