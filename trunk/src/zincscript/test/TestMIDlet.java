package zincscript.test;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class TestMIDlet extends MIDlet {
	public static TestMIDlet midlet = null;
	private TestCanvas testCanvas = null;
	private Display display = null;

	public TestMIDlet(){
		midlet = this;
		display = Display.getDisplay(midlet);
		testCanvas = new TestCanvas();
	}
	
	protected void destroyApp(boolean arg0) {

	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		display.setCurrent(testCanvas);
	}

	public static void Exit() {
		midlet.destroyApp(true);
		midlet.notifyDestroyed();
		midlet = null;
	}

}
