package dom;

/**
 * <p>
 * <code>ListItemDOM</code>封装了列表项的数据。
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public class ListItemDOM extends AbstractDOM {
	/** 列表的标题 */
	public String title = null;
	/** 列表的内容 */
	public String content = null;
	/** 列表的左下角文字 */
	public String ltail1 = null;
	public String ltail2 = null;
	/** 列表的右下角文字 */
	public String rtail = null;
	/** 列表左上的图标 */
	public int icon = 0;
	/** 列表左边的图片 */
	public String imageScr = null;
	/** 默认图片 */
	public String altImageSrc = null;
	/** 文字的行数 */
	public int line = 3;
	public int cType = 0;

	public ListItemDOM() {
		type = TYPE_LIST_ITEM;
		isVisible = true;
	}
}
