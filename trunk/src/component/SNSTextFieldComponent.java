package component;

import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGDirectTextBox;
import config.Config;
import config.Resources;
import dom.AbstractDOM;
import dom.TextFieldDOM;

public class SNSTextFieldComponent extends AbstractSNSComponent {
	private static final int MARGIN = 4;
	private static final int SPACE = 2;
	/* Akebono提供的输入框 */
	private FSGDirectTextBox textField = null;
	private String label = null;
	private CRImage leftImage = null;
	private CRImage rightImage = null;

	public SNSTextFieldComponent(AbstractDOM dom) {
		super(dom);
	}

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
	}

	public int getNextX() {
		return 0;
	}

	public int getNextY() {
		return iY + iHeight;
	}

	protected void drawCurrentFrame(CRGraphics g) {
		if (label != null) {
			g.setColor(0x3F3F3D);
			Config.PLAIN_SMALL_FONT.drawString(g, label, MARGIN, MARGIN, 20);
		}
		leftImage.draw(g, MARGIN, textField.iY - 2, 20);
		rightImage.draw(g, iWidth - MARGIN, textField.iY - 2, 24);
		textField.paintCurrentFrame(g, textField.iX, textField.iY);
		g.setColor(0xBFBFBF);
		g.drawLine(textField.iX, textField.iY - 1, textField.iX
				+ textField.getWidth(), textField.iY - 1);
		g.setColor(0x808080);
		g.drawLine(textField.iX, textField.iY - 2, textField.iX
				+ textField.getWidth(), textField.iY - 2);
	}

	public void init() {
		TextFieldDOM textFieldDOM = (TextFieldDOM) this.dom;
		this.label = textFieldDOM.label;
		int fieldY = MARGIN;
		iWidth = getContainingPanel().getWidth();
		leftImage = Resources.getInstance().getTextfield_left();
		rightImage = Resources.getInstance().getTextfield_right();
		if (this.label != null) {
			fieldY += Config.PLAIN_SMALL_FONT.getHeight();
		}
		textField = new FSGDirectTextBox(200, iWidth - (MARGIN + 10) * 2, 17,
				"", Config.PLAIN_SMALL_FONT, 255, 0, false, 0);
		textField.getPreferredHeight();
		textField.iX = MARGIN + 10;
		textField.iY = fieldY + 2;
		textField.setBgColor(-1);
		// textField.setBorder(0, 0x707070);
		textField.setInputModeIndicatorTextColor(0x292929);
		textField.setInputModeIndicatorBgColor(0xECECEC, 0xC0DAB0);
		textField.enableTransparentBackground(true);
		addComponent(textField);
		iHeight = fieldY + MARGIN + 19;
	}

	public void setMotion(int startX, int startY) {

	}

	public void setFocus(boolean isFocused) {
		textField.setFocus(isFocused);
		textField.forceActivated(isFocused);
		if(isFocused){
			textField.setBgColor(0xffffff);
		}else{
			textField.setBgColor(-1);
		}
		super.setFocus(isFocused);
	}
}
