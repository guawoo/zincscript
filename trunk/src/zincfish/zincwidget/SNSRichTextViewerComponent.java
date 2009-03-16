package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import zincfish.zincdom.ImageDOM;
import zincfish.zincdom.PlainTextDOM;
import zincfish.zincdom.RichTextViewerDOM;

import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer.Callback;

import config.Config;
import config.Resources;

public class SNSRichTextViewerComponent extends AbstractSNSComponent {

	private static final int BLOCK_SPACE = 2;

	private FSGRichTextViewer richTextViewer = null;

	private CSList fontList = null;

	private CSList converageColorFontList = null;

	private CSList colorList = null;

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
	}

	protected void drawCurrentFrame(CRGraphics g) {
		richTextViewer.paintCurrentFrame(g, 0, 0);
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
		RichTextViewerDOM richTextViewerDOM = (RichTextViewerDOM) this.dom;
		Callback callback = new RichTextViewerCallback();
		iWidth = getContainingPanel().getWidth();
		// iHeight = 300;
		richTextViewer = new FSGRichTextViewer(1, iWidth, iHeight, callback);
		// richTextViewer.setBgColor(-1);
		addComponent(richTextViewer);
		callback = null;
		if (richTextViewerDOM.content != null
				&& richTextViewerDOM.content.size() > 0) {
			int imageIndex = 0;
			for (int i = 0; i < 1; i++) {
				AbstractDOM child = (AbstractDOM) richTextViewerDOM.content
						.get(i);
				if (child.type == AbstractDOM.TYPE_PLAIN_TEXT) {
					PlainTextDOM plainTextDOM = (PlainTextDOM) child;
					generateFont(plainTextDOM.text);
					richTextViewer.appendText(plainTextDOM.text, fontList,
							converageColorFontList, colorList,
							converageColorFontList);
					plainTextDOM = null;
				} else if (child.type == AbstractDOM.TYPE_IMAGE) {
					ImageDOM imageDOM = (ImageDOM) child;
					richTextViewer.appendImage(imageIndex, 0, 0, 0, -1);
					imageIndex++;
					imageDOM = null;
				}
			}
		} else {
			// generateFont("正在加载内容, 请稍候...");
			// System.out.println("appent text");
			// richTextViewer.appendText("正在加载内容,请稍候...", fontList,
			// converageColorFontList, colorList, converageColorFontList);
			// System.out.println("appent text OK");
		}
		iHeight = richTextViewer.getContentTotalHeight();
		richTextViewer.setSize(iWidth, iHeight);
		// System.out.println("iHeight = " + iHeight);
	}

	private void generateFont(String textStr) {
		int[] converage = { 0, textStr.length() };
		converageColorFontList = null;
		converageColorFontList = new CSList();
		converageColorFontList.addElement(converage);
		converage = null;
		int[] color = { 0x292929 };
		colorList = null;
		colorList = new CSList();
		colorList.addElement(color);
		color = null;
		fontList = null;
		fontList = new CSList();
		fontList.addElement(Config.PLAIN_SMALL_FONT);
	}

	public void setMotion(int startX, int startY) {
		// TODO Auto-generated method stub

	}

	/**
	 * 处理图片的callback
	 * 
	 * @author Jarod Yv
	 * @since fingerling
	 */
	class RichTextViewerCallback implements Callback {

		/* 图片列表 */
		private CRImage[] images = null;

		public CRImage getContentImage(FRichTextViewer richText, int imageID) {
			if (images == null) {
				return null;
			}
			if (imageID >= images.length) {
				return null;
			}
			return images[imageID];
		}

		public CRImage getEmoticonImage(FRichTextViewer richText, int imageID) {
			return Resources.getInstance().getEmotion_faces()[imageID];
		}

	}

	public void release() {
		richTextViewer = null;
		fontList = null;
		converageColorFontList = null;
		colorList = null;
		super.release();
	}

	public boolean keyPressed(int keyCode) {
		return richTextViewer.keyPressed(keyCode);
	}

	public boolean keyReleased(int arg0) {
		return false;
	}

	public boolean keyRepeated(int arg0) {
		return false;
	}

}
