package org.pushingpixels.windows;
import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

public class TranslucentShapedWindow extends JFrame {
	public TranslucentShapedWindow() {
		super("Test translucent and oval-shaped window");
		this.setLayout(new FlowLayout());
		this.add(new JButton("test"));
		this.add(new JCheckBox("test"));
		this.add(new JRadioButton("test"));
		this.add(new JProgressBar(0, 100));

		this.setSize(new Dimension(400, 300));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Window w = new TranslucentShapedWindow();
				w.setVisible(true);
				com.sun.awt.AWTUtilities.setWindowOpacity(w, 0.5f);
				com.sun.awt.AWTUtilities
						.setWindowShape(w, new Ellipse2D.Double(0, 0, w
								.getWidth(), w.getHeight()));
			}
		});
	}

}
