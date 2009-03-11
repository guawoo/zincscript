package data;

/**
 * <code>IDOMChangeListener</code> 接口定义了DOM Tree发生变化时的处理接口。当DOM
 * Tree发生变化时，会回调监听器的{@link #updateView()}接口，完成对UI的更新。
 * 
 * @author Jarod Yv
 * @since fingerling
 */
public interface IDOMChangeListener {

	public void updateView();
}
