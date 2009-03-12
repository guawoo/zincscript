package zincfish.zincdom;

public class MenuItemDOM extends AbstractDOM {
	public static final byte LEFT_MENU = 0x00;
	public static final byte RIGHT_MENU = 0x01;
	public static final byte SUB_MENU = 0x02;

	public String text = null;
	public int code = 0;
	public int menuType = LEFT_MENU;

	public MenuItemDOM() {
		this.type = TYPE_MENU;
	}
}
