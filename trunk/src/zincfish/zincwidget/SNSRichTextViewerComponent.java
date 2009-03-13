package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import zincfish.zincdom.ImageDOM;
import zincfish.zincdom.PlainTextDOM;
import zincfish.zincdom.RichTextViewerDOM;

import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSList;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer.Callback;

import config.Config;

public class SNSRichTextViewerComponent extends AbstractSNSComponent {

	private static final int BLOCK_SPACE = 2;

	private FSGRichTextViewer richTextViewer = null;

	private CSList fontList = new CSList();

	private CSList converageColorFontList = new CSList();

	private CSList colorList = new CSList();

	public void doLayout(int startX, int startY) {

	}

	public int getNextX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNextY() {
		// TODO Auto-generated method stub
		return 0;
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
		colorList.addElement(color);
		color = null;
		fontList = new CSList();
		fontList.addElement(Config.PLAIN_SMALL_FONT);
		colorList = new CSList();
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
		/* 表情列表 */
		private CRImage[] emotionImages = null;

		public CRImage getContentImage(FRichTextViewer richText, int imageID) {
			if (images == null || imageID >= images.length) {
				return null;
			}
			return images[imageID];
		}

		public CRImage getEmoticonImage(FRichTextViewer richText, int imageID) {
			if (emotionImages == null || imageID >= emotionImages.length) {
				return CRImage.loadFromResource("/ceshi2/s1.png");
			}
			return emotionImages[imageID];
		}

	}

}
