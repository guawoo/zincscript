package zincfish.zincdom;

public class ImageDOM extends AbstractDOM {
	public String src = null;
	public String alt = null;

	public ImageDOM() {
		this.type = TYPE_IMAGE;
		this.isVisible = true;
	}
}
