package org.pushingpixels.windows;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class Testing {

	public static void main(String[] args) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		System.out
				.println("\tCan create shaped windows: "
						+ com.sun.awt.AWTUtilities
								.isTranslucencySupported(com.sun.awt.AWTUtilities.Translucency.PERPIXEL_TRANSPARENT));
		System.out
				.println("\tCan create translucent windows: "
						+ com.sun.awt.AWTUtilities
								.isTranslucencySupported(com.sun.awt.AWTUtilities.Translucency.TRANSLUCENT));
		System.out
				.println("\tCan create shaped translucent windows: "
						+ com.sun.awt.AWTUtilities
								.isTranslucencySupported(com.sun.awt.AWTUtilities.Translucency.PERPIXEL_TRANSLUCENT));
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				System.out.println("Screen " + j + ", config " + i);
				System.out
						.println("\tTranslucency capable: "
								+ com.sun.awt.AWTUtilities
										.isTranslucencyCapable(gc[i]));
			}
		}
	}
}
