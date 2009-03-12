package ui.snscomponent;

import zincfish.zincdom.AbstractDOM;
import dom.*;

public final class ComponentFactory {

	public static AbstractSNSComponent createComponent(AbstractDOM dom) {
		AbstractSNSComponent component = null;
		switch (dom.type) {
		case AbstractDOM.TYPE_BODY:
			component = new SNSBodyComponent(dom);
			break;
		case AbstractDOM.TYPE_LIST:
			component = new SNSListComponent(dom);
			break;
		case AbstractDOM.TYPE_LIST_ITEM:
			component = new SNSListItemComponent(dom);
			break;
		case AbstractDOM.TYPE_BUTTON:
			component = new SNSButtonComponent(dom);
			break;
		case AbstractDOM.TYPE_TEXT_FIELD:
			component = new SNSTextFieldComponent(dom);
			break;
		case AbstractDOM.TYPE_TEXT_EDITOR:
			component = new SNSTextEditorComponent(dom);
			break;
		}
		return component;
	}
}
