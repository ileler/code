package org.pushingpixels.windows.popup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * A fading popup with shadow border.
 * 
 * @author Kirill Grouchnikov
 */
public class FadingPopup extends Popup {
	private JWindow popupWindow;

	private int currOpacity;

	Timer fadeInTimer;

	Timer fadeOutTimer;

	boolean toFade;

	FadingPopup(Component owner, Component contents, int ownerX, int ownerY) {
		// create a new heavyweighht window
		this.popupWindow = new JWindow();
		// determine the popup location
		popupWindow.setLocation(ownerX, ownerY);
		// add the contents to the popup
		popupWindow.getContentPane().add(contents, BorderLayout.CENTER);
		contents.invalidate();
		JComponent parent = (JComponent) contents.getParent();
		// set the shadow border
		parent.setBorder(new ShadowPopupBorder());

		// only fade tooltips
		this.toFade = (contents instanceof JToolTip);
	}

	@Override
	public void show() {
		if (this.toFade) {
			// mark the popup with 0% opacity
			this.currOpacity = 0;
			com.sun.awt.AWTUtilities.setWindowOpacity(popupWindow, 0.0f);
		}

		this.popupWindow.setVisible(true);
		this.popupWindow.pack();

		// mark the window as non-opaque, so that the
		// shadow border pixels take on the per-pixel
		// translucency
		com.sun.awt.AWTUtilities.setWindowOpaque(this.popupWindow, false);

		if (this.toFade) {
			// start fading in
			this.fadeInTimer = new Timer(50, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currOpacity += 20;
					if (currOpacity <= 100) {
						com.sun.awt.AWTUtilities.setWindowOpacity(popupWindow,
								currOpacity / 100.0f);
						// workaround bug 6670649 - should call
						// popupWindow.repaint() but that will not repaint the
						// panel
						popupWindow.getContentPane().repaint();
					} else {
						currOpacity = 100;
						fadeInTimer.stop();
					}
				}
			});
			this.fadeInTimer.setRepeats(true);
			this.fadeInTimer.start();
		}
	}

	@Override
	public void hide() {
		if (this.toFade) {
			// cancel fade-in if it's running.
			if (this.fadeInTimer.isRunning())
				this.fadeInTimer.stop();

			// start fading out
			this.fadeOutTimer = new Timer(50, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currOpacity -= 10;
					if (currOpacity >= 0) {
						com.sun.awt.AWTUtilities.setWindowOpacity(popupWindow,
								currOpacity / 100.0f);
						// workaround bug 6670649 - should call
						// popupWindow.repaint() but that will not repaint the
						// panel
						popupWindow.getContentPane().repaint();
					} else {
						fadeOutTimer.stop();
						popupWindow.setVisible(false);
						popupWindow.removeAll();
						popupWindow.dispose();
						currOpacity = 0;
					}
				}
			});
			this.fadeOutTimer.setRepeats(true);
			this.fadeOutTimer.start();
		} else {
			popupWindow.setVisible(false);
			popupWindow.removeAll();
			popupWindow.dispose();
		}
	}
}
