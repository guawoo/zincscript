package zincfish.zincdom;

public class PlainTextDOM extends AbstractDOM {
	public String text = null;

	public PlainTextDOM() {
		this.type = TYPE_PLAIN_TEXT;
		this.isVisible = true;
	}
}
