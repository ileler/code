package org.pushingpixels.windows.reflection;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Frame that shows a reflection under the bottom border. Uses some of the code
 * from chapter 11 of <a href="https://filthyrichclients.dev.java.net/">Filthy
 * Rich Clients</a> by Romain Guy and Chet Haase available under BSD license.
 * 
 * The code in this class is available under BSD license.
 * 
 * @author Kirill Grouchnikov
 */
public class JReflectionFrame extends JFrame {
	private BufferedImage contentBuffer;
	private BufferedImage reflectionBuffer;

	private Graphics2D contentGraphics;
	private Graphics2D reflectionGraphics;

	private GradientPaint alphaMask;

	private float length = 0.65f;
	private float opacity = 0.75f;

	private Window reflection;
	private JPanel reflectionPanel;

	public JReflectionFrame(String title) {
		super(title);
		reflection = new JWindow();
		reflectionPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// paint the reflection of the main window
				paintReflection(g);
			}
		};
		// mark the panel as non-double buffered and non-opaque
		// to make it translucent.
		reflectionPanel.setDoubleBuffered(false);
		reflectionPanel.setOpaque(false);

		reflection.setLayout(new BorderLayout());
		reflection.add(reflectionPanel, BorderLayout.CENTER);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				reflection.setVisible(false);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// update the reflection location
				reflection.setLocation(getX(), getY() + getHeight());
			}

			@Override
			public void componentResized(ComponentEvent e) {
				// update the reflection size and location
				reflection.setSize(getWidth(), getHeight());
				reflection.setLocation(getX(), getY() + getHeight());
			}

			@Override
			public void componentShown(ComponentEvent e) {
				reflection.setVisible(true);

				// if the reflection window is opaque, mark
				// it as per-pixel translucent
				if (com.sun.awt.AWTUtilities.isWindowOpaque(reflection)) {
					com.sun.awt.AWTUtilities.setWindowOpaque(reflection, false);
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				// force showing the reflection window
				reflection.setAlwaysOnTop(true);
				reflection.setAlwaysOnTop(false);
			}
		});

		// initialize the reflection size and location
		reflection.setSize(getSize());
		reflection.setLocation(getX(), getY() + getHeight());
		reflection.setVisible(true);

		// install custom repaint manager to force re-painting
		// the reflection when something in the main window is
		// repainted
		RepaintManager.setCurrentManager(new ReflectionRepaintManager());
	}

	@Override
	protected JRootPane createRootPane() {
		return new JRootPane() {
			@Override
			public void paint(Graphics g) {
				paintContent(g);
			}

			private void paintContent(Graphics g) {
				if (contentBuffer == null
						|| contentBuffer.getWidth() != getWidth()
						|| contentBuffer.getHeight() != getHeight()) {
					if (contentBuffer != null) {
						contentBuffer.flush();
						contentGraphics.dispose();
					}

					GraphicsConfiguration gc = ((Graphics2D) g)
							.getDeviceConfiguration();
					contentBuffer = gc.createCompatibleImage(getWidth(),
							getHeight(), Transparency.TRANSLUCENT);
					contentGraphics = contentBuffer.createGraphics();
				}

				// paint the contents to an off-screen image
				Graphics2D g2 = contentGraphics;
				g2.clipRect(getX(), getY(), getWidth(), getHeight());

				g2.setComposite(AlphaComposite.Clear);
				Rectangle clip = g.getClipBounds();
				g2.fillRect(clip.x, clip.y, clip.width, clip.height);
				g2.setComposite(AlphaComposite.SrcOver);

				g2.setColor(g.getColor());
				g2.setFont(g.getFont());
				super.paint(g2);

				// and paint the off-screen image back
				g.drawImage(contentBuffer, 0, 0, null);
			}
		};
	}

	public void paintReflection(Graphics g) {
		// paint the reflection of the main window
		JRootPane rootPane = this.getRootPane();
		Border rootPaneBorder = rootPane.getBorder();
		int width = rootPane.getWidth();
		int height = (int) (rootPane.getHeight() * length);
		createReflection(g, width, height);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(1.0, -1.0);
		g2.drawImage(reflectionBuffer, 0, -height
				+ rootPaneBorder.getBorderInsets(rootPane).bottom, null);
		g2.dispose();
	}

	private void createReflection(Graphics g, int width, int height) {
		// create the reflection of the main window
		JRootPane rootPane = this.getRootPane();
		if (reflectionBuffer == null || reflectionBuffer.getWidth() != width
				|| reflectionBuffer.getHeight() != height) {
			if (reflectionBuffer != null) {
				reflectionBuffer.flush();
				reflectionGraphics.dispose();
			}

			GraphicsConfiguration gc = ((Graphics2D) g)
					.getDeviceConfiguration();
			reflectionBuffer = gc.createCompatibleImage(getWidth(),
					getHeight(), Transparency.TRANSLUCENT);

			reflectionGraphics = reflectionBuffer.createGraphics();

			alphaMask = new GradientPaint(0.0f, 0.0f, new Color(0.0f, 0.0f,
					0.0f, 0.0f), 0.0f, height, new Color(0.0f, 0.0f, 0.0f,
					opacity), true);
		}

		int yOffset = rootPane.getHeight() - height;
		Rectangle clip = g.getClipBounds();

		Graphics2D g2 = (Graphics2D) reflectionGraphics.create();
		g2.setClip(clip.x, clip.y - yOffset, clip.width, clip.height);

		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(clip.x, clip.y - yOffset, clip.width, clip.height);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setComposite(AlphaComposite.SrcOver);

		g2.translate(0, -yOffset);
		g2.drawImage(contentBuffer, 0, 0, null);
		g2.translate(0, yOffset);

		g2.setComposite(AlphaComposite.DstIn);
		g2.setPaint(alphaMask);
		g2.fillRect(clip.x, clip.y - yOffset, clip.width, clip.height);

		g2.dispose();
	}

	private class ReflectionRepaintManager extends RepaintManager {
		@Override
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
			Window win = SwingUtilities.getWindowAncestor(c);
			if (win instanceof JReflectionFrame) {
				// mark the entire root pane to be repainted
				JRootPane rp = ((JReflectionFrame) win).getRootPane();
				super.addDirtyRegion(rp, 0, 0, rp.getWidth(), rp.getHeight());

				// workaround bug 6670649 - should call reflection.repaint()
				// but that will not repaint the panel
				reflectionPanel.repaint();
			} else {
				super.addDirtyRegion(c, x, y, w, h);
			}
		}
	}
}
