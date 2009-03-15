package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import zincfish.zincdom.TextEditorDOM;

import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGDirectTextBox;
import com.mediawoz.akebono.ui.UComponent;

import config.Config;
import config.Resources;

public class SNSTextEditorComponent extends AbstractSNSComponent implements
		EComponentEventListener {
	private static final int MARGIN = 4;
	private static final int SPACE = 2;
	private static final int PADDING = 6;
	private String label = null;
	private FSGDirectTextBox textArea = null;
	private CRImage lt, ld, rt, rd = null;
	private CRImage leftMarginImage, topMarginImage = null;

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
	}

	protected void drawCurrentFrame(CRGraphics g) {
		if (label != null) {
			g.setColor(0x3F3F3D);
			Config.PLAIN_SMALL_FONT.drawString(g, label, MARGIN, MARGIN, 20);
		}
		lt.draw(g, MARGIN, textArea.iY - MARGIN, 20);
		rt.draw(g, iWidth - MARGIN, textArea.iY - MARGIN, 24);
		ld.draw(g, MARGIN, iHeight - MARGIN, 36);
		rd.draw(g, iWidth - MARGIN, iHeight - MARGIN, 40);
		for (int i = 0; i <= (textArea.getHeight() + MARGIN * 2) - 10; i += 10) {
			topMarginImage.draw(g, MARGIN + 10, textArea.iY - MARGIN + i, 20);
		}
		leftMarginImage.draw(g, MARGIN, iHeight - MARGIN - 10, 36);
		leftMarginImage.draw(g, iWidth - MARGIN, iHeight - MARGIN - 10, 40);
		topMarginImage.draw(g, MARGIN + 10, iHeight - MARGIN, 36);
		textArea.paintCurrentFrame(g, textArea.iX, textArea.iY);
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
		TextEditorDOM textEditorDOM = (TextEditorDOM) this.dom;
		this.label = textEditorDOM.label;
		lt = Resources.getInstance().getTexteditor_lt();
		ld = Resources.getInstance().getTexteditor_ld();
		rt = Resources.getInstance().getTexteditor_rt();
		rd = Resources.getInstance().getTexteditor_rd();
		iWidth = 220;// getContainingPanel().getWidth();
		int fieldY = MARGIN;
		if (this.label != null) {
			fieldY += Config.PLAIN_SMALL_FONT.getHeight();
		}
		textArea = new FSGDirectTextBox(200, iWidth - (MARGIN + PADDING) * 2,
				(Config.PLAIN_SMALL_FONT.getHeight() + SPACE) * 6, "",
				Config.PLAIN_SMALL_FONT, 255, FSGDirectTextBox.ANY, true, 0);
		textArea.iX = MARGIN + PADDING;
		textArea.iY = fieldY + PADDING;
		textArea.setBgColor(-1);
		// textArea.setBorder(0, 0x707070);
		textArea.setInputModeIndicatorTextColor(0x292929);
		textArea.setInputModeIndicatorBgColor(0xECECEC, 0xC0DAB0);
		textArea.enableTransparentBackground(true);
		textArea.setComponentEventListener(this);
		addComponent(textArea);
		iHeight = fieldY + textArea.getHeight() + MARGIN + PADDING;
		leftMarginImage = CRImage.createImage(10, textArea.getHeight()
				+ (PADDING - 10) * 2);
		leftMarginImage.setAlpha(60);
		topMarginImage = CRImage.createImage(textArea.getWidth()
				+ (PADDING - 10) * 2, 10);
		topMarginImage.setAlpha(60);
	}

	public void setMotion(int startX, int startY) {

	}

	public void release() {
		label = null;
		textArea = null;
		lt = null;
		ld = null;
		rt = null;
		rd = null;
		leftMarginImage = null;
		topMarginImage = null;
		super.release();
	}

	public void setFocus(boolean isFocused) {
		textArea.setFocus(isFocused);
		textArea.forceActivated(isFocused);
		super.setFocus(isFocused);
	}

	public boolean keyPressed(int keyCode) {
		boolean b = false;
		b = textArea.keyPressed(keyCode);
		if (!b) {
			cel.componentEventFired(this,
					EComponentEventListener.EVENT_SEL_EDGE, null, keyCode);
		}
		return true;
	}

	public boolean keyReleased(int keyCode) {
		return false;
	}

	public boolean keyRepeated(int keyCode) {
		return false;
	}

	public void componentEventFired(UComponent c, int eventId, Object paramObj,
			int param) {
		System.out.println("texteditor = " + eventId);
	}
}
