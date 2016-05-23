package org.pushingpixels.windows.popup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TestFrame extends JFrame {
	public TestFrame() {
		super("Frame with popups");

		this.setLayout(new BorderLayout());

		JPanel sampleControls = new JPanel(new FlowLayout());
		JButton buttonWithTooltip = new JButton("short tooltip");
		buttonWithTooltip.setToolTipText("Short tooltip");
		sampleControls.add(buttonWithTooltip);

		JButton buttonWithTooltip2 = new JButton("Long tooltip");
		buttonWithTooltip2
				.setToolTipText("Long tooltip that most probably goes beyond the frame bounds");
		sampleControls.add(buttonWithTooltip2);

		JButton buttonWithTooltip3 = new JButton("HTML tooltip");
		buttonWithTooltip3
				.setToolTipText("<html><body>Mutliline tooltip that might go beyond<br>the frame bounds</body></html>");
		sampleControls.add(buttonWithTooltip3);

		JComboBox combo = new JComboBox(new Object[] { "Alex", "Brad", "Chad",
				"Desmond", "Eva" });
		sampleControls.add(combo);

		this.add(sampleControls, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu1 = new JMenu("menu1");
		for (int i = 0; i < 10; i++) {
			menu1.add(new JMenuItem("menu item " + i));
		}
		menuBar.add(menu1);
		JMenu menu2 = new JMenu("menu2");
		for (int i = 0; i < 5; i++) {
			menu2.add(new JMenuItem("menu item " + i));
		}
		JMenu menu3 = new JMenu("menu3");
		menu2.add(menu3);
		for (int i = 0; i < 5; i++) {
			menu3.add(new JMenuItem("menu item " + i));
		}

		menuBar.add(menu2);
		this.setJMenuBar(menuBar);

		JPanel popupFactoryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		popupFactoryPanel.add(new JLabel("Popup kind"));
		JRadioButton translucent = new JRadioButton("translucent");
		JRadioButton fading = new JRadioButton("fading");
		ButtonGroup popupGroup = new ButtonGroup();
		popupGroup.add(translucent);
		popupGroup.add(fading);
		translucent.setSelected(true);

		PopupFactory.setSharedInstance(new TranslucentPopupFactory());
		translucent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						PopupFactory
								.setSharedInstance(new TranslucentPopupFactory());
					}
				});
			}
		});
		fading.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						PopupFactory
								.setSharedInstance(new FadingPopupFactory());
					}
				});
			}
		});
		popupFactoryPanel.add(translucent);
		popupFactoryPanel.add(fading);
		this.add(popupFactoryPanel, BorderLayout.SOUTH);

		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TestFrame().setVisible(true);
			}
		});
	}

}
