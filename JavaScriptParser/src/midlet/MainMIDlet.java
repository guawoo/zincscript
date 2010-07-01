package midlet;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import vm.VirtualMachine;

public class MainMIDlet extends MIDlet implements CommandListener {
	static final int MAX_INPUT_SIZE = 32000;
	static final Command CMD_EVAL = new Command("Eval", Command.OK, 0);
	static final Command CMD_BACK = new Command("Back", Command.BACK, 1);
	static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);

	TextBox textBox = new TextBox("JsShell", "", MAX_INPUT_SIZE, TextField.ANY);
	Vector history = new Vector();
	VirtualMachine global = new VirtualMachine();

	public MainMIDlet() {
		textBox.addCommand(CMD_EVAL);
		textBox.addCommand(CMD_BACK);
		textBox.addCommand(CMD_EXIT);

		textBox.setCommandListener(this);
		textBox
				.setString("function sumPrime(a,b){\nvar sum=0;\nfor(var x=a;x<=b;x++){\nif(prime(x))sum+=x*x;\n}\nreturn(sum);\n}\nfunction prime(x){\nvar flag=true;\nfor(var i=2;i<=Math.sqrt(x);i++)\n{if(x%i==0){flag=false;break;} \n}\nreturn(flag); \n}\nsumPrime(3,7);");
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		Display.getDisplay(this).setCurrent(textBox);

	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == CMD_EXIT) {
			notifyDestroyed();
		} else if (cmd == CMD_BACK) {
			int size = history.size();
			if (size == 0) {
				textBox.setString("");
			} else {
				textBox.setString((String) history.elementAt(size - 1));
				history.removeElementAt(size - 1);
			}
		} else if (cmd == CMD_EVAL) {
			String expr = textBox.getString();
			history.addElement(expr);
			try {
				textBox.setString("" + global.exec(expr));
			} catch (Exception e) {
				e.printStackTrace();
				textBox.setString(e.toString());
			}
		}
	}

}
