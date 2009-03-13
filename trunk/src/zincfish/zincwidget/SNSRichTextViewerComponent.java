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

	public int getNextX() {
		return 0;
	}

	public int getNextY() {
		return iY + iHeight;
	}

	protected void drawCurrentFrame(CRGraphics g) {
		System.out.println("Paint");
		richTextViewer.paintCurrentFrame(g, 0, 0);
	}

	public void init(AbstractDOM dom) {
		this.dom = dom;
		RichTextViewerDOM richTextViewerDOM = (RichTextViewerDOM) this.dom;
		Callback callback = new RichTextViewerCallback();
		richTextViewer = new FSGRichTextViewer(1, iWidth, iHeight, callback);
		callback = null;
		if (richTextViewerDOM.children != null
				&& richTextViewerDOM.children.size() > 0) {
			int imageIndex = 0;
			for (int i = 0; i < richTextViewerDOM.children.size(); i++) {
				AbstractDOM child = (AbstractDOM) richTextViewerDOM.children
						.get(i);
				if (child.type == AbstractDOM.TYPE_PLAIN_TEXT) {
					PlainTextDOM plainTextDOM = (PlainTextDOM) child;
					if (fontList == null || colorList == null
							|| converageColorFontList == null)
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
		}
		addComponent(richTextViewer);
		iHeight = richTextViewer.getContentTotalHeight();
	}

	private void generateFont(String textStr) {
		int[] converage = { 0, textStr.length() };
		converageColorFontList = new CSList();
		converageColorFontList.addElement(converage);
		converage = null;
		int[] color = { 0x292929 };
		colorList = new CSList();
		colorList.addElement(color);
		color = null;
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

}
