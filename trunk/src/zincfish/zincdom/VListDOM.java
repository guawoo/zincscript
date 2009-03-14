package zincfish.zincdom;

/**
 * <p>
 * <code>ListDOM</code>封装了列表的数据。<code>ListDOM</code>是一个List容器
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public class VListDOM extends AbstractDOM {
	public String title = null;

	public VListDOM() {
		this.type = TYPE_VLIST;
		this.isVisible = true;
	}
}
