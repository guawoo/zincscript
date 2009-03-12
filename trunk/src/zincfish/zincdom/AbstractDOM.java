package zincfish.zincdom;


import ui.snscomponent.AbstractSNSComponent;
import utils.ArrayList;

/**
 * <p>
 * <code>Akebono</code>的UI组件是以树形结构组织的，xml文档也是树形结构。使用xml来定义<code>Akebono</code>
 * UI界面是一件简单又自然的做法。但是考虑到<code>Akebono</code>巨大的内存消耗，因此缓存解析好的<code>Akebono</code>
 * 组件是不可行的
 * 。同样是出于对性能和内存的考虑，在解析xml文件时，我们仍然采用<b>Pull</b>式解析,而非<b>DOM</b>式解析。尽管在解析方式上不采用
 * <b>DOM</b>式解析，但是上面已经提到，<code>Akebono</code>的UI组件是以树形结构组织的，为了方便xml到
 * <code>Akebono</code> UI界面的转换，我们仍然要将xml解析为一颗DOM Tree。
 * </p>
 * <p>
 * DOM Tree的存在主要是在内存和性能约束的条件下，在两者之间寻找的一种平衡。直接缓存<code>Akebono</code>
 * 组件可以很快的实现界面的切换(因为不需要再解析排版)，但是会造成较大的内存开销；缓存xml数据内存开销很小，但是用xml到UI界面需要经历
 * <em>解析</em>、<em>排版</em>等处理过程，因此会损失性能。在这种约束下，构建并缓存DOM Tree是一种折中的做法。DOM Tree比
 * <code>Akebono</code> 组件消耗更少的内存，又可以省去解析xml的过程，达到了内存和性能的平衡。
 * </p>
 * <p>
 * DOM Tree的另一个重要作用是实现Ajax。Ajax的实现很大程度上依靠对DOM Tree的操作。<code>ZincFish</code>
 * 引擎利用<b>Zinc Script</b>脚本实现了一种在移动设备上工作的ajax框架。我们提供了一套对DOM Tree进行操作的库供<b>Zinc
 * Script</b>脚本调用，从而完成对DOM Tree的操作，实现Ajax，完成对UI的更新。
 * </p>
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public abstract class AbstractDOM {
	// /////////////////////// DOM节点类型 ////////////////////////
	/** 列表类型 */
	public static final byte TYPE_LIST = 0x01;
	/** 列表项类型 */
	public static final byte TYPE_LIST_ITEM = 0x02;
	/** 按钮类型 */
	public static final byte TYPE_BUTTON = 0x03;
	/** 富文本编辑器类型 */
	public static final byte TYPE_RICH_EDTOR = 0x04;
	/** 菜单类型 */
	public static final byte TYPE_MENU = 0x05;
	/** DIV类型 */
	public static final byte TYPE_DIV = 0x06;
	/** BODY类型 */
	public static final byte TYPE_BODY = 0x07;
	/** 输入框类型 */
	public static final byte TYPE_TEXT_FIELD = 0x08;
	/** 多行输入框类型 */
	public static final byte TYPE_TEXT_EDITOR = 0x09;
	// ///////////////////////////////////////////////////////////

	// ///////////////////////// 对齐方式 /////////////////////////
	public static final byte ALIGN_LEFT = 0x00;
	public static final byte ALIGN_CENTER = 0x01;
	public static final byte ALIGN_RIGHT = 0x02;
	// ///////////////////////////////////////////////////////////

	/** DOM节点的唯一标识 */
	public String id = null;

	/** 节点类型 */
	public byte type = 0x00;

	/** 屏幕上的位置 */
	public int x = -1, y = -1, w = -1, h = -1;

	/** 组件的对齐方式 */
	public byte align = ALIGN_LEFT;

	/** 标志是否可视 */
	public boolean isVisible = true;

	/** 标志是否有效 */
	public boolean isAvailable = true;

	/** 背景颜色 -1表示没有指定，采用组件默认的背景颜色 */
	public int bgColor = -1;

	/** 前景颜色 -1表示没有指定，采用组件默认的背景颜色 */
	public int fgColor = -1;

	// ////////////////////////// DOM事件 //////////////////////////
	/** 生成组件时的响应函数，该方法在生成组件时调用，不需要保存 */
	// public String onInit = null;
	/** 点击事件的响应函数 */
	public String onClick = null;

	/** 获得焦点事件的响应函数 */
	public String onFocus = null;

	/** 失去焦点时的响应函数 */
	public String onLoseFocus = null;

	/** 载入事件的响应函数 */
	// public String onLoad = null;
	// ///////////////////////////////////////////////////////////
	/** 父节点 */
	public AbstractDOM father = null;

	/** 子节点 */
	public ArrayList children = null;

	/* DOM节点相关联的SNS UI组件 */
	private AbstractSNSComponent component = null;

	/**
	 * 释放资源
	 */
	public void release() {
		id = null;
		onClick = null;
		onFocus = null;
		onLoseFocus = null;
		father = null;
		if (children != null) {
			while (children.size() > 0) {
				AbstractDOM child = (AbstractDOM) children.remove(0);
				child.release();
				child = null;
			}
			children = null;
		}
		if (component != null) {
			component.release();
			component = null;
		}
		System.gc();// 在此调用gc()是必要的
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return id != null && id.equals(((AbstractDOM) obj).id);
	}

	/**
	 * @return the component
	 */
	public AbstractSNSComponent getComponent() {
		return component;
	}

	/**
	 * @param component
	 *            the component to set
	 */
	public void setComponent(AbstractSNSComponent component) {
		this.component = component;
	}
}
