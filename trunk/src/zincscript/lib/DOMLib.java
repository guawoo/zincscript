package zincscript.lib;

import utils.ArrayList;
import zincscript.core.ZSException;

public class DOMLib extends AbstactLib {

	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void createFunctionMap() {
		// TODO Auto-generated method stub

	}

	private void _dom_getValue(ArrayList params) {
		String id = (String) params.get(0);
		if (id != null) {
			// AbstractDOM dom = DOMUtil.getInstance().get
		}
	}

}
