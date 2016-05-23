package org.pushingpixels.windows.popup;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.*;

/**
 * A translucent popup with shadow border.
 * 
 * @author Kirill Grouchnikov
 */
public class TranslucentPopup extends Popup {
	private JWindow popupWindow;

	TranslucentPopup(Component owner, Component contents, int ownerX, int ownerY) {
		// create a new heavyweight window
		this.popupWindow = new JWindow();
		// mark the popup with partial opacity
		com.sun.awt.AWTUtilities.setWindowOpacity(popupWindow,
				(contents instanceof JToolTip) ? 0.8f : 0.95f);
		// determine the popup location
		popupWindow.setLocation(ownerX, ownerY);
		// add the contents to the popup
		popupWindow.getContentPane().add(contents, BorderLayout.CENTER);
		contents.invalidate();
		JComponent parent = (JComponent) contents.getParent();
		// set the shadow border
		parent.setBorder(new ShadowPopupBorder());
	}

	@Override
	public void show() {
		this.popupWindow.setVisible(true);
		this.popupWindow.pack();
		// mark the window as non-opaque, so that the
		// shadow border pixels take on the per-pixel
		// translucency
		com.sun.awt.AWTUtilities.setWindowOpaque(this.popupWindow, false);
	}

	@Override
	public void hide() {
		this.popupWindow.setVisible(false);
		this.popupWindow.removeAll();
		this.popupWindow.dispose();
	}
}
