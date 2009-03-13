package zincfish.zincdom;

import utils.ArrayList;

public class RichTextViewerDOM extends AbstractDOM {
	public ArrayList content = null;

	public RichTextViewerDOM() {
		this.type = TYPE_RICH_TEXT_VIEWER;
		this.isVisible = true;
	}

	public void addContent(AbstractDOM dom) {
		if (content == null)
			content = new ArrayList(10);
		content.add(dom);
	}
}
