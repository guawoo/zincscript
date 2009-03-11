package midlet;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import screen.BrowserScreen;

import com.mediawoz.akebono.corerenderer.CRDisplay;

/**
 * �������
 * 
 * @author Jarod Yv
 */
public class SNSMIDlet extends MIDlet {

	public SNSMIDlet() {
		CRDisplay.init(this);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		CRDisplay.setCurrent(BrowserScreen.getInstance());
	}

}
