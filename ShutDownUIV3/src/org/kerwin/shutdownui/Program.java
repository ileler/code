package org.kerwin.shutdownui;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.kerwin.shutdownui.view.ShutDownUI;

public class Program {

	public static void initGlobalFontSetting(Font fnt) {
		FontUIResource fontRes = new FontUIResource(fnt);
		for (Enumeration<?> keys = UIManager.getDefaults().keys(); keys
				.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, fontRes);
		}
	}

	public static void main(String[] args) {
		initGlobalFontSetting(new Font("新宋体", Font.PLAIN, 14));
		new ShutDownUI().setVisible(true);
	}

}
