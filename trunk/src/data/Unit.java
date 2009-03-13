package data;

import zincfish.zincdom.AbstractDOM;
import zincfish.zincdom.MenuItemDOM;
import zincfish.zincscript.ZSException;
import zincfish.zincscript.ZincScript;

/**
 * <p>
 * <code>Unit</code> 代表一个完整的UI界面定义，它由一个DOM Tree，一个菜单和脚本地址组成。
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public final class Unit {
	/* Unit的唯一标识 */
	private String id = null;
	/* DOM tree */
	private AbstractDOM domTree = null;
	/* 关联脚本的路径 */
	private String scriptPath = null;
	/* 左软键菜单 */
	private MenuItemDOM menu = null;
	/* onload属性 */
	private String onLoad = null;

	public boolean needUpdateMenu = false;

	/**
	 * 加入DomTree
	 * 
	 * @param dom
	 *            DOM Tree
	 */
	public void setDomTree(AbstractDOM dom) {
		this.domTree = dom;
	}

	/**
	 * 获取Dom Tree
	 * 
	 * @return DOM Tree
	 */
	public AbstractDOM getDomTree() {
		return domTree;
	}

	/**
	 * 装载脚本
	 * 
	 * @param path
	 */
	public void setScriptPath(String path) {
		this.scriptPath = path;
		ZincScript.getZincScript().loadScript(scriptPath);
		try {
			ZincScript.getZincScript().executeScript();
		} catch (ZSException e) {
			e.printStackTrace();
		}
	}

	public String getScriptPath() {
		return scriptPath;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void release() {
		if (domTree != null) {
			domTree.release();
			domTree = null;
		}
		scriptPath = null;
		id = null;
	}

	/**
	 * @return the menu
	 */
	public MenuItemDOM getMenu() {
		return menu;
	}

	/**
	 * @param menu
	 *            the menu to set
	 */
	public void setMenu(MenuItemDOM menu) {
		this.menu = menu;
	}

	/**
	 * 重写的{@link #equals(Object)}方法，判断两个Unit是否表示同一个Unit
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return (getDOMID() != null && getDOMID()
				.equals(((Unit) obj).getDOMID()));
	}

	/**
	 * 获取DOM Tree的ID
	 * 
	 * @return DOM Tree的ID
	 */
	public String getDOMID() {
		if (domTree != null)
			return domTree.id;
		return null;
	}

	/**
	 * 用传入的DomTree更新当前的DomTree
	 * 
	 * @param subTree
	 *            用于更新的子树
	 */
	public void replace(Unit subTree) {
		AbstractDOM root = subTree.domTree;// 获取子树
		if (root == null || root.children.size() == 0)
			return;
		// 遍历整个树，逐个判断节点是否需要更新
		for (int i = 0; i < root.children.size(); i++) {
			AbstractDOM dom = (AbstractDOM) root.children.get(i);
			if (!replaceNode(domTree, dom)) {// 返回false，说明原树上没有相同的节点
				// 将此节点加到当前数的根节点上
				domTree.children.add(i, dom);
				dom.father = domTree;
			}
			dom = null;
		}
		root = null;
	}

	/*
	 * 递归地查找替换节点
	 * 
	 * @param srcDom
	 * 
	 * @param tagDom
	 * 
	 * @return
	 */
	private boolean replaceNode(AbstractDOM srcDom, AbstractDOM tagDom) {
		System.out.println("srcDom = " + srcDom.toString() + " id = "
				+ srcDom.id);
		if (srcDom.equals(tagDom)) {// 两个节点相等则需要替换
			AbstractDOM father = srcDom.father;// 获取父节点
			int index = father.children.indexOf(srcDom);// 获取子节点的索引
			System.out.println("index = " + index);
			father.children.set(index, tagDom);// 用新节点替换原节点
			tagDom.father = father;// 设置新节点的父亲
			srcDom.release();// 清理源节点
			srcDom = null;
			father = null;
			return true;
		} else {// 如果不相等，则继续遍历其子节点
			for (int i = 0; srcDom.children != null
					&& i < srcDom.children.size(); i++) {
				AbstractDOM child = (AbstractDOM) srcDom.children.get(i);
				if (replaceNode(child, tagDom))// 如果替换成功，则返回true
					return true;
				child = null;
			}
		}
		return false;
	}

	/**
	 * @return the onLoad
	 */
	public String getOnLoad() {
		return onLoad;
	}

	/**
	 * @param onLoad
	 *            the onLoad to set
	 */
	public void setOnLoad(String onLoad) {
		this.onLoad = onLoad;
	}

}
