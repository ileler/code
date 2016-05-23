package org.kerwin.shutdownui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;

public class UIUtil {

	private static Toolkit toolKit;
	private static Dimension screenSize;
	public static int screenWidth;
	public static int screenHeight;

	static {
		toolKit = Toolkit.getDefaultToolkit();
		screenSize = toolKit.getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;
	}

	/**
	 * 得到屏幕大小
	 * @return
	 */
	public static Dimension getScreenSize() {
		return screenSize;
	}

	/**
	 * 得到屏幕的宽度
	 * 
	 * @return 返回double为屏幕的宽度
	 */
	public static double getScreenWidth() {
		return screenSize.getWidth();
	}

	/**
	 * 得到屏幕高度
	 * 
	 * @return 返回double类型为屏幕的高度
	 */
	public static double getScreenHeight() {
		return screenSize.getHeight();
	}

	/**
	 * 得到当前焦点窗体
	 * @return
	 */
	public static Window getCurrentFrame() {
		Window[] wins = JDialog.getWindows();
		for (Window win : wins) {
			if (win.isFocusableWindow())
				return (Window) win;
		}
		return null;
	}

	/**
	 * 将组件定位到屏幕中央
	 * @param component
	 */
	public static void locationToScreenCenter(Component component) {
		if (component == null)
			return;
		component.setLocation(screenSize.width / 2 - component.getWidth() / 2,
				screenSize.height / 2 - component.getHeight() / 2);
	}

	/**
	 * 设置所有子组件透明
	 * 
	 * @param component
	 *            要设置的组件
	 * @param opaque
	 *            是否透明
	 */
	public static void setAllOpaque(JComponent component, boolean opaque) {
		component.setOpaque(opaque);
		component.repaint();
		Component[] cs = component.getComponents();
		for (Component c : cs) {
			if (c instanceof JComponent) {
				setAllOpaque((JComponent) c, opaque);
			}
		}
	}

}
