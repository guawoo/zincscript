package parser.wmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import parser.xmlparser.ParserException;
import parser.xmlparser.XmlParser;
import utils.StringUtil;
import zincscript.core.ZSException;
import zincscript.core.ZincScript;
import data.Unit;
import dom.*;

public final class SNSParser {
	private Unit unit = null;
	private AbstractDOM root = null;
	private AbstractDOM currentDOM = null;
	private Hashtable attr = null;
	private MenuItemDOM menu = null;
	private byte align = AbstractDOM.ALIGN_LEFT;// horizontal align
	private XmlParser parser;// xml parser
	private boolean isRunning = false;

	private SNSParser() {
		parser = new XmlParser();
		attr = new Hashtable(10);
	}

	/**
	 * get the instance of parser
	 * 
	 * @return WML parser
	 */
	public static final SNSParser getSNSParser() {
		return new SNSParser();
	}

	public final void parse() throws ParserException, IOException {
		isRunning = true;
		int eventType = parser.getEventType();
		// parse all tags
		while (eventType != XmlParser.END_DOCUMENT && isRunning) {
			// start tag event
			if (eventType == XmlParser.START_TAG) {
				parseStartTag();
			}
			// end tag event
			else if (eventType == XmlParser.END_TAG) {
				parseEndTag();
			}
			// text event
			else if (eventType == XmlParser.TEXT) {
				parseText();
			}
			// get next tag
			try {
				eventType = parser.next();
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
	}

	private final void parseStartTag() throws ParserException {
		// tag's name
		String currentTag = parser.getName();
		// number of attributes
		int attributeNum = parser.getAttributeNum();
		// get all attributes
		for (int i = 0; i < attributeNum; i++) {
			attr.put(parser.getAttributeName(i), parser.getAttributeValue(i));
		}
		if (SNSTag.LIST_ITEM_TAG.equals(currentTag)) {
			parseListItem();
		} else if (SNSTag.LIST_TAG.equals(currentTag)) {
			parseList();
		} else if (SNSTag.MENU_ITEM_TAG.equals(currentTag)
				|| SNSTag.MENU_TAG.equals(currentTag)) {
			parseMenuItem();
		} else if (SNSTag.BUTTON_TAG.equals(currentTag)) {
			parseButton();
		} else if (SNSTag.TEXT_FIELD_TAG.equals(currentTag)) {
			parseTextField();
		} else if (SNSTag.TEXT_EDITOR_TAG.equals(currentTag)) {
			parseTextEditor();
		} else if (SNSTag.BODY_TAG.equals(currentTag)) {
			parseBody();
		} else if (SNSTag.UNIT_TAG.equals(currentTag)) {
			parseUnit();
		} else if (SNSTag.DIV_TAG.equals(currentTag)) {
			parseDiv();
		} else if (SNSTag.SCRIPT_TAG.equals(currentTag)) {
			parseScript();
		} else if (SNSTag.BODY_TAG.equals(currentTag)) {
			parseBody();
		}

		currentTag = null;
		attr.clear();
	}

	private final void parseEndTag() {
		String currentTag = parser.getName();
		if (SNSTag.MENU_TAG.equals(currentTag)
				|| SNSTag.MENU_ITEM_TAG.equals(currentTag)) {
			currentDOM = currentDOM.father;
			if (currentDOM == null) {
				unit.setMenu(menu);
				menu = null;
			}
		} else {
			if (currentDOM != null && root != null)
				currentDOM = currentDOM.father == null ? root
						: currentDOM.father;
		}
		currentTag = null;
	}

	private final void parseText() {

	}

	private void add2DOMTree(AbstractDOM dom) throws ParserException {
		dom.align = align;
		if (currentDOM != null) {
			utils.DOMUtil.addSubtree(currentDOM, dom);
			currentDOM = dom;
		} else {
			if (root == null) {
				root = dom;
				currentDOM = root;
				unit.setDomTree(root);
			} else {
				utils.DOMUtil.addSubtree(root, dom);
				currentDOM = dom;
			}
		}
	}

	private final void parseList() throws ParserException {
		ListDOM list = new ListDOM();
		handelGeneralAttributes(list);

		add2DOMTree(list);
		list = null;
	}

	private final void parseButton() throws ParserException {
		ButtonDOM dom = new ButtonDOM();
		handelGeneralAttributes(dom);

		dom.text = (String) attr.get(SNSTag.TEXT_ATTR);
		dom.bgImage = (String) attr.get(SNSTag.SRC_ATTR);

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseTextField() throws ParserException {
		TextFieldDOM dom = new TextFieldDOM();
		handelGeneralAttributes(dom);

		dom.label = (String) attr.get(SNSTag.TITLE_ATTR);
		dom.name = (String) attr.get(SNSTag.NAME_ATTR);
		dom.value = (String) attr.get(SNSTag.VALUE_ATTR);
		dom.charType = StringUtil.Str2Int((String) attr.get(SNSTag.TYPE_ATTR));

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseTextEditor() throws ParserException {
		TextEditorDOM dom = new TextEditorDOM();
		handelGeneralAttributes(dom);

		dom.label = (String) attr.get(SNSTag.TITLE_ATTR);
		dom.name = (String) attr.get(SNSTag.NAME_ATTR);
		dom.value = (String) attr.get(SNSTag.VALUE_ATTR);

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseBody() throws ParserException {
		root = new BodyDOM();
		currentDOM = root;
		root.id = (String) attr.get(SNSTag.ID_ATTR);
		String onload = (String) attr.get(SNSTag.ON_LOAD_ATTR);
		unit.setOnLoad(onload);
		onload = null;
		unit.setDomTree(root);
	}

	private final void parseMenuItem() throws ParserException {
		MenuItemDOM menuItem = new MenuItemDOM();
		menuItem.text = (String) attr.get(SNSTag.TEXT_ATTR);
		menuItem.onClick = (String) attr.get(SNSTag.ON_CLICK_ATTR);
		String s = (String) attr.get(SNSTag.TYPE_ATTR);
		if (s != null)
			menuItem.menuType = StringUtil.Str2Int(s);
		s = null;
		if (menu == null) {
			menu = menuItem;
		} else {
			add2DOMTree(menuItem);
		}
		currentDOM = menuItem;
		menuItem = null;
	}

	private final void handelGeneralAttributes(AbstractDOM dom) {
		String attributeValue = null;
		if ((attributeValue = (String) attr.get(SNSTag.ON_INIT_ATTR)) != null) {
			try {
				ZincScript.getZincScript().callFunction(attributeValue);
			} catch (ZSException e) {
				e.printStackTrace();
			}
		}
		dom.id = (String) attr.get(SNSTag.ID_ATTR);
		// 事件属性
		dom.onClick = (String) attr.get(SNSTag.ON_CLICK_ATTR);
		dom.onFocus = (String) attr.get(SNSTag.ON_FOCUS_ATTR);
		dom.onLoseFocus = (String) attr.get(SNSTag.ON_LOSE_FOCUS_ATTR);
	}

	private final void parseListItem() throws ParserException {
		ListItemDOM listItem = new ListItemDOM();
		handelGeneralAttributes(listItem);

		listItem.title = (String) attr.get(SNSTag.TITLE_ATTR);
		listItem.content = (String) attr.get(SNSTag.CONTENT_ATTR);
		listItem.ltail1 = (String) attr.get(SNSTag.LTAIL1_ATTR);
		listItem.ltail2 = (String) attr.get(SNSTag.LTAIL2_ATTR);
		listItem.rtail = (String) attr.get(SNSTag.RTAIL_ATTR);
		listItem.imageScr = (String) attr.get(SNSTag.SRC_ATTR);
		listItem.cType = StringUtil.Str2Int((String) attr
				.get(SNSTag.CTYPE_ATTR));
		String s = (String) attr.get(SNSTag.LINE_ATTR);
		if (s != null)
			listItem.line = StringUtil.Str2Int(s);
		s = null;
		s = (String) attr.get(SNSTag.ICON_ATTR);
		if (s != null)
			listItem.icon = StringUtil.Str2Int(s);
		s = null;

		add2DOMTree(listItem);
		listItem = null;
	}

	private final void parseUnit() {
		unit = new Unit();
	}

	private final void parseDiv() throws ParserException {
		DivDOM dom = new DivDOM();
		// id
		String attrValue = (String) attr.get(SNSTag.ID_ATTR);
		dom.id = attrValue;
		attrValue = null;

		add2DOMTree(dom);
		dom = null;
	}

	private final void parseScript() {
		// path
		String path = (String) attr.get(SNSTag.PATH_ATTR);
		String name = (String) attr.get(SNSTag.NAME_ATTR);
		if (unit == null)
			unit = new Unit();
		unit.setScriptPath(path + name);
		path = null;
		name = null;
	}

	/**
	 * set wml page stream
	 */
	public void setInput(InputStream is, String encode) throws ParserException {
		this.parser.setInput(is, encode);
	}

	public Unit getResult() {
		return unit;
	}

	public void release() {
		unit = null;
		root = null;
		currentDOM = null;
	}
}
