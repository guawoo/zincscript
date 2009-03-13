package zincfish.zincwidget;

import zincfish.zincdom.AbstractDOM;

public final class ComponentFactory {

	public static AbstractSNSComponent createComponent(AbstractDOM dom) {
		AbstractSNSComponent component = null;
		switch (dom.type) {
		case AbstractDOM.TYPE_BODY:
			component = new SNSBodyComponent();
			break;
		case AbstractDOM.TYPE_LIST:
			component = new SNSVerticalListComponent();
			break;
		case AbstractDOM.TYPE_LIST_ITEM:
			component = new SNSListItemComponent();
			break;
		case AbstractDOM.TYPE_BUTTON:
			component = new SNSButtonComponent();
			break;
		case AbstractDOM.TYPE_TEXT_FIELD:
			component = new SNSTextFieldComponent();
			break;
		case AbstractDOM.TYPE_TEXT_EDITOR:
			component = new SNSTextEditorComponent();
			break;
		case AbstractDOM.TYPE_RICH_TEXT_VIEWER:
			component = new SNSRichTextViewerComponent();
			break;
		}
		if (component != null)
			component.setDom(dom);
		return component;
	}
}
