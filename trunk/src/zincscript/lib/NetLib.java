package zincscript.lib;

import java.util.Hashtable;

import component.WheelLoadingComponent;

import screen.BrowserScreen;
import utils.ArrayList;
import zincscript.core.ZSException;

public class NetLib extends AbstactLib {
	private static final int FUNCTION_NUM = 1;

	private static final String SEND = "_zsnSend";
	private static final byte SEND_CODE = 1;
	/**
	 * 所有库都采用单例模式，确保内存中已有一份库的实例
	 */
	private static NetLib instance = null;

	public static NetLib getInstance() {
		if (instance == null) {
			instance = new NetLib();
			instance.createFunctionMap();
		}
		return instance;
	}

	public Object callFunction(String name, ArrayList params)
			throws ZSException {
		Byte code = (Byte) functionMap.get(name);
		if (code != null) {
			switch (code.byteValue()) {
			case SEND_CODE:
				_zsnSend(params);
				return null;
			}
			return null;
		} else {
			throw new ZSException("函数" + name + "不存在");
		}
	}

	protected void createFunctionMap() {
		if (functionMap == null)
			functionMap = new Hashtable(FUNCTION_NUM);
		functionMap.put(SEND, new Byte(SEND_CODE));
	}

	private void _zsnSend(ArrayList params) {
		if (params == null || params.size() == 0)
			return;
		final String url = (String) params.get(0);
		String callBack = null;
		if (params.size() > 1)
			callBack = (String) params.get(1);
		System.out.println("onload url = " + url);
		new Thread() {
			public void run() {
				WheelLoadingComponent hourglass = WheelLoadingComponent
						.getInstance();
				BrowserScreen.getInstance().setHourglass(hourglass);
				hourglass = null;
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				BrowserScreen.getInstance().loadUnit(url);
				BrowserScreen.getInstance().setHourglass(null);
			}
		}.start();
	}
}
