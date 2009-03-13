package zincfish.zinclib;

import java.util.Hashtable;

import utils.ArrayList;

import zincfish.zincscript.ZSException;

/**
 * <code>AbstactLib</code>是自定义函数库的基类<br>
 * <code>AbstactLib</code>为所有自定义函数库执行了统一的组织和调用接口,为<code>ZincSriptc</code>
 * 提供了无限扩充的能力
 * 
 * @author Jarod Yv
 */
public abstract class AbstactLib {
	/**
	 * 函数名映射表
	 */
	protected Hashtable functionMap = null;

	/**
	 * 调用库中的函数
	 * 
	 * @param name
	 *            函数名
	 * @param param
	 *            参数列表
	 * @return true=调用成功 false=调用失败
	 */
	public abstract Object callFunction(String name, ArrayList params)
			throws ZSException;

	/**
	 * 构造库函数名映射表.最简单的方法是使用HashTable,也可以实现更加高效的哈希算法
	 */
	protected abstract void createFunctionMap();
}
