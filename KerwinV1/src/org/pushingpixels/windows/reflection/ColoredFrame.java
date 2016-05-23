package org.pushingpixels.windows.reflection;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ColoredFrame extends JReflectionFrame {
	public ColoredFrame() {
		super("Reflection");
		this.add(new ColoredPanel(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Window w = new ColoredFrame();
				w.setVisible(true);
			}
		});
	}

}
