package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;
import zincfish.zincdom.ListItemDOM;

import com.mediawoz.akebono.corefilter.CFMotion;
import com.mediawoz.akebono.corerenderer.CRDisplay;
import com.mediawoz.akebono.corerenderer.CRGraphics;
import com.mediawoz.akebono.corerenderer.CRImage;
import com.mediawoz.akebono.coreservice.utils.CSDevice;
import com.mediawoz.akebono.events.EComponentEventListener;
import com.mediawoz.akebono.filters.motion.FMLinear;
import com.vodafone.io.RemoteControlData;

import config.Config;
import config.Resources;

public class SNSListItemComponent extends AbstractSNSComponent {
	private static final String DEFAULT_IMAGE_PATH = "/widgets/diary/img/header.png";
	private static final int MARGIN = 8;
	private static final int SPACE = 2;
	/* 列表左边的图像 */
	private CRImage header = null;
	/* 标题 */
	private String title = null;
	// private CScrollingTicker titie = null;
	/* 正文 */
	private String content = null;
	/* 左脚注1 */
	private String ltail1 = null;
	/* 左脚注2 */
	private String ltail2 = null;
	/* 右脚注 */
	private String rtail = null;

	private boolean hasMore = false;

	/* 列表弹出的motion */
	private CFMotion motion = null;

	private int icon = 0;

	private int wordStartX = MARGIN;

	private int titleX, titleW, titleL;
	private boolean needScroll = false;

	private void drawSelector(CRGraphics g) {
		Resources resource = Resources.getInstance();
		CRImage selectltor = resource.getListCover();
		for (int y = 20; y <= iHeight - 30; y += 10)
			selectltor.draw(g, 3, y, 20);
		selectltor = null;
		selectltor = resource.getListSelector();
		selectltor.draw(g, 3, 0, 20);
		selectltor.draw(g, 3, iHeight, CRImage.FLIP_MIRROR_ROT180, 36);
		selectltor = null;
	}

	protected void drawCurrentFrame(CRGraphics g) {

		g.setColor(0x5D5F52);
		if (header != null) {
			header.draw(g, 0, MARGIN, 20);
		}
		if (icon > 0) {
			int num = 0;
			for (int i = 0; i < 5; i++) {
				if (((icon >> i) & 0x01) == 1) {
					Resources.getInstance().getIcon(i).draw(g,
							iWidth - MARGIN - 15 * num, MARGIN, 24);
					num++;
				}
			}
		}
		if (title != null) {
			g.setClip(wordStartX, MARGIN, titleW, Config.BOLD_MEDIUM_FONT
					.getHeight());
			Config.BOLD_MEDIUM_FONT.drawString(g, title, titleX, MARGIN, 20);
			g.setClip(0, 0, CRDisplay.getWidth(), CRDisplay.getHeight());
		}
		if (content != null) {
			Config.PLAIN_SMALL_FONT.drawString(g, content, wordStartX, MARGIN
					+ (title == null ? 0 : Config.BOLD_MEDIUM_FONT.getHeight()
							+ SPACE), 20);
		}
		if (ltail1 != null) {
			Resources.getInstance().getListTail1().draw(
					g,
					wordStartX,
					iHeight
							- MARGIN
							+ (Resources.getInstance().getListTail1()
									.getHeight() - Config.PLAIN_SMALL_FONT
									.getHeight()) / 2, 36);
			Config.PLAIN_SMALL_FONT.drawString(g, ltail1, wordStartX + 14,
					iHeight - MARGIN, 36);
		}
		if (ltail2 != null) {
			Resources.getInstance().getListTail2().draw(
					g,
					wordStartX + 20
							+ Config.PLAIN_SMALL_FONT.stringWidth(ltail1),
					iHeight
							- MARGIN
							+ (Resources.getInstance().getListTail2()
									.getHeight() - Config.PLAIN_SMALL_FONT
									.getHeight()) / 2, 36);
			Config.PLAIN_SMALL_FONT.drawString(g, ltail2, wordStartX + 34
					+ Config.PLAIN_SMALL_FONT.stringWidth(ltail1), iHeight
					- MARGIN, 36);
		}
		if (rtail != null) {
			Config.PLAIN_SMALL_FONT.drawString(g, rtail, iWidth - MARGIN,
					iHeight - MARGIN, 40);
		}
		if (isFocused) {
			drawSelector(g);
		}
	}

	protected boolean animate() {
		if (motion != null) {
			if (motion.isFinished()) {
				detachAnimator(motion);
				motion = null;
			} else {
				iY = motion.getCurY();
			}
			return true;
		}
		if (isFocused && needScroll) {
			titleX -= 5;
			if (titleX + titleL <= wordStartX)
				titleX = wordStartX + titleW;
			return true;
		}
		return false;
	}

	public void setFocus(boolean isFocused) {
		// 获得焦点后如果Title过长，一行显示不下，则开启滚动
		titleX = wordStartX;
		super.setFocus(isFocused);
	}

	public void init(AbstractDOM dom) {
		ListItemDOM listItemDOM = (ListItemDOM) dom;
		title = listItemDOM.title;
		content = listItemDOM.content;
		ltail1 = listItemDOM.ltail1;
		ltail2 = listItemDOM.ltail2;
		rtail = listItemDOM.rtail;
		icon = listItemDOM.icon;
		if (listItemDOM.cType == 1) {
			this.canFocus = false;
			this.isFocused = false;
		}
		if (listItemDOM.imageScr != null) {// 有左边的图片
			header = CRImage
					.loadFromResource(listItemDOM.altImageSrc == null ? DEFAULT_IMAGE_PATH
							: listItemDOM.altImageSrc);// 载入默认图片
			wordStartX = header.getWidth();
		}
		int iconnum = 0;
		if (icon > 0) {
			for (int i = 0; i < 5; i++) {
				if (((icon >> i) & 0x01) == 1) {
					iconnum++;
				}
			}
		}

		if (title != null) {
			titleL = Config.BOLD_MEDIUM_FONT.stringWidth(title);
			if (wordStartX + titleL + 15 * iconnum > CRDisplay.getWidth())
				needScroll = true;
			titleX = wordStartX;
			titleW = CRDisplay.getWidth() - wordStartX - 15 * iconnum;
		}
		this.iHeight = (title == null ? 0 : Config.BOLD_MEDIUM_FONT.getHeight())
				+ (Config.PLAIN_SMALL_FONT.getHeight() + SPACE + MARGIN) * 2;
		if (header != null && this.iHeight < header.getHeight())
			this.iHeight = header.getHeight();
	}

	public void doLayout(int startX, int startY) {
		iX = dom.x == -1 ? startX : dom.x;
		iY = dom.y == -1 ? startY : dom.y;
		iWidth = dom.w == -1 ? (getContainingPanel() == null ? (getContainingScreen() == null ? CRDisplay
				.getWidth()
				: getContainingScreen().getWidth())
				: getContainingPanel().getWidth())
				: dom.w;
	}

	public void setMotion(int startX, int startY) {
		motion = new FMLinear(1, FMLinear.PULLBACK, startX, startY, iX, iY, 10,
				0, -100);
		iY = startY;
		attachAnimator(motion);
	}

	public void release() {
		header = null;
		content = null;
		title = null;
		ltail1 = null;
		ltail2 = null;
		rtail = null;
		motion = null;
		this.dom = null;
	}

	public boolean keyPressed(int keyCode) {
		int keyAction = CSDevice.getGameAction(keyCode);
		if (hasMore) {// 某些列表项，比如日记，可能有更多内容，此时方向右键 == fire键
			if (keyAction == CSDevice.KEY_RIGHT)
				keyAction = CSDevice.KEY_FIRE;
		}
		switch (keyAction) {
		case CSDevice.KEY_FIRE:
			cel.componentEventFired(this,
					EComponentEventListener.EVENT_SEL_CLICKED, dom.onClick, 0);
			return true;
		case CSDevice.KEY_UP:
		case CSDevice.KEY_RIGHT:
		case CSDevice.KEY_DOWN:
		case CSDevice.KEY_LEFT:
			cel.componentEventFired(this,
					EComponentEventListener.EVENT_SEL_EDGE, null, keyCode);
			return true;
		default:
			return false;
		}
	}

	public boolean keyReleased(int keyCode) {
		return false;
	}

	public boolean keyRepeated(int keyCode) {
		return false;
	}
}
