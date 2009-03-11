package component;

import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.forms.lafs.simplegraph.FSGRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer;
import com.mediawoz.akebono.forms.formitems.FRichTextViewer.Callback;

public class SNSRichTextViewerComponent extends AbstractSNSComponent {

	private static final int BLOCK_SPACE = 2;
	private FSGRichTextViewer richTextViewer = null;

	public void doLayout(int startX, int startY) {
		// TODO Auto-generated method stub

	}

	public int getNextX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNextY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void setMotion(int startX, int startY) {
		// TODO Auto-generated method stub

	}

	class RichTextViewerCallback implements Callback {

		/* 图片列表*/
		private CRImage[] images = null;
		/* 表情列表*/
		private CRImage[] emotionImages = null;

		public RichTextViewerCallback(CRImage[] images) {
			this.images = images;
		}

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
