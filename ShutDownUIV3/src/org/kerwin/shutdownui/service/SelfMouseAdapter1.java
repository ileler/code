package org.kerwin.shutdownui.service;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SelfMouseAdapter1 extends MouseAdapter {
	private Point origin;
	private Component component;

	public SelfMouseAdapter1(Component component) {
		this.origin = new Point();
		this.component = component;
	}

	private MouseMotionAdapter mm = new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent e) { // 拖动（mouseDragged指的不是鼠标在窗口中移动，而是用鼠标拖动）
			Point p = component.getLocation(); // 当鼠标拖动时获取窗口当前位置
			// 设置窗口的位置
			// 窗口当前的位置 + 鼠标当前在窗口的位置 - 鼠标按下的时候在窗口的位置
			component.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
					- origin.y);
			component.repaint();
		}
	};

	@Override
	public void mousePressed(MouseEvent e) {
		origin.x = e.getX();
		origin.y = e.getY();
		e.getComponent().removeMouseMotionListener(mm);
		e.getComponent().addMouseMotionListener(mm);
	}
}
