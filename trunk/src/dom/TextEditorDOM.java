package dom;

public class TextEditorDOM extends AbstractDOM {
	/** 输入框的label，此label会绘制到输入框的左上角 */
	public String label = null;
	/** 文本框对应字段的名称 */
	public String name = null;
	/** 文本框的默认文字 */
	public String value = null;

	public TextEditorDOM() {
		this.type = TYPE_TEXT_EDITOR;
		this.isVisible = true;
	}
}
