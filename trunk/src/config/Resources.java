package config;

import com.mediawoz.akebono.corerenderer.CRImage;

public final class Resources {
	private static Resources instance = null;

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	private CRImage list_selector = null;
	private CRImage list_cover = null;
	private CRImage list_ltail1 = null;
	private CRImage list_ltail2 = null;
	private CRImage[] iconList = null;
	private CRImage textfield_left = null;
	private CRImage textfield_right = null;

	private CRImage texteditor_lt = null;
	private CRImage texteditor_ld = null;
	private CRImage texteditor_rt = null;
	private CRImage texteditor_rd = null;

	private CRImage[] emotion_faces = null;

	/**
	 * @return the list_select_lt
	 */
	public CRImage getListSelector() {
		if (list_selector == null)
			list_selector = CRImage
					.loadFromResource("/widgets/diary/img/selector.png");
		return list_selector;
	}

	public CRImage getListTail1() {
		if (list_ltail1 == null)
			list_ltail1 = CRImage
					.loadFromResource("/widgets/diary/img/read.png");
		return list_ltail1;
	}

	public CRImage getListTail2() {
		if (list_ltail2 == null)
			list_ltail2 = CRImage
					.loadFromResource("/widgets/diary/img/replay.png");
		return list_ltail2;
	}

	public CRImage getListCover() {
		if (list_cover == null) {
			list_cover = CRImage.createImage(235, 10);
			list_cover.setAlpha(25);
		}
		return list_cover;
	}

	public CRImage getIcon(int index) {
		if (iconList == null) {
			iconList = new CRImage[5];
			iconList[0] = CRImage
					.loadFromResource("/widgets/diary/img/ico1.png");
			iconList[1] = CRImage
					.loadFromResource("/widgets/diary/img/ico2.png");
			iconList[2] = CRImage
					.loadFromResource("/widgets/diary/img/ico3.png");
			iconList[3] = CRImage
					.loadFromResource("/widgets/diary/img/ico4.png");
			iconList[4] = CRImage
					.loadFromResource("/widgets/diary/img/ico5.png");
		}
		return iconList[index];
	}

	/**
	 * @return the textfield_left
	 */
	public CRImage getTextfield_left() {
		if (textfield_left == null)
			textfield_left = CRImage.loadFromResource("/img/tfl.png");
		return textfield_left;
	}

	/**
	 * @return the textfield_right
	 */
	public CRImage getTextfield_right() {
		if (textfield_right == null)
			textfield_right = CRImage.loadFromResource("/img/tfr.png");
		return textfield_right;
	}

	/**
	 * @return the texteditor_lt
	 */
	public CRImage getTexteditor_lt() {
		if (texteditor_lt == null)
			texteditor_lt = CRImage.loadFromResource("/img/telt.png");
		return texteditor_lt;
	}

	/**
	 * @return the texteditor_ld
	 */
	public CRImage getTexteditor_ld() {
		if (texteditor_ld == null)
			texteditor_ld = CRImage.loadFromResource("/img/teld.png");
		return texteditor_ld;
	}

	/**
	 * @return the texteditor_rt
	 */
	public CRImage getTexteditor_rt() {
		if (texteditor_rt == null)
			texteditor_rt = CRImage.loadFromResource("/img/tert.png");
		return texteditor_rt;
	}

	/**
	 * @return the texteditor_rd
	 */
	public CRImage getTexteditor_rd() {
		if (texteditor_rd == null)
			texteditor_rd = CRImage.loadFromResource("/img/terd.png");
		return texteditor_rd;
	}

	public CRImage[] getEmotion_faces() {
		if (emotion_faces == null)
			emotion_faces = new CRImage[10];
		for (int i = 0; i < emotion_faces.length; i++)
			emotion_faces[i] = CRImage.loadFromResource("/img/face/" + i
					+ ".png");
		return emotion_faces;
	}
}
