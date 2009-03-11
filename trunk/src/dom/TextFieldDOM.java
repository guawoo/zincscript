package dom;

import com.mediawoz.akebono.forms.formitems.FDirectTextBox;

/**
 * <p>
 * <code>TextFieldDOM</code> 封装了文本框的各种数据
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public class TextFieldDOM extends AbstractDOM {

	/** 输入框的label，此label会绘制到输入框的左上角 */
	public String label = null;
	/** 文本框对应字段的名称 */
	public String name = null;
	/** 文本框的默认文字 */
	public String value = null;
	/** 文本框允许输入文字的类型 */
	public int charType = FDirectTextBox.ANY;

	public TextFieldDOM() {
		this.type = TYPE_TEXT_FIELD;
		isVisible = true;
	}
}
