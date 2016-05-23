package org.kerwin.shutdownui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class ShutDownUI extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JLabel exitLabel;
	private JLabel newLabel;
	private JPanel setPanel;
	private JLabel sdsLabel;
	private JLabel sdrLabel;
	private JLabel hLabel;
	private JLabel mLabel;
	private JLabel sLabel;
	private JTextField tField;
	private JPanel countPanel;
	private JLabel countLabel;
	private JLabel cancelLabel;
	private JPanel btnPanel;
	private JLabel nextLabel;
	private JLabel prevLabel;
	private boolean isRight;
	private String cmdStr;
	private int mulCount;
	private int totalCount;
	private String msgStr;
	private Timer selfT = new Timer("ShutDownUI");

	public ShutDownUI() {
		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(300, 30); // 设置窗体大小
		setResizable(false); // 设置窗体大小不可变

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		exitLabel = new JLabel();
		exitLabel.setPreferredSize(new Dimension(30, getHeight()));
		exitLabel.setToolTipText("退出");
		exitLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					System.exit(0);
				}
			}

		});
		mainPanel.add(exitLabel, BorderLayout.EAST);

		newLabel = new JLabel("新建任务", new ImageIcon(getClass().getResource(
				"/res/new.png")), JLabel.CENTER);
		newLabel.setOpaque(true);
		newLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		newLabel.setFont(new Font("新宋体", Font.PLAIN, 20));
		newLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					changeToSetPanel();
				}
			}

		});
		mainPanel.add(newLabel);

		setPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		sdsLabel = new JLabel("关机", JLabel.CENTER);
		sdsLabel.setOpaque(true);
		cmdStr = "/p";
		sdsLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		sdsLabel.setPreferredSize(new Dimension(30, getHeight()));
		sdsLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				cmdStr = "/p";
				sdrLabel.setBackground(Color.WHITE);
			}

		});

		sdrLabel = new JLabel("重启", JLabel.CENTER);
		sdrLabel.setOpaque(true);
		sdrLabel.setPreferredSize(new Dimension(30, getHeight()));
		sdrLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					cmdStr = "/r";
					sdsLabel.setBackground(Color.WHITE);
				}
			}

		});

		hLabel = new JLabel("时", JLabel.CENTER);
		hLabel.setOpaque(true);
		mulCount = 360;
		hLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		hLabel.setPreferredSize(new Dimension(20, getHeight()));
		hLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					mulCount = 360;
					mLabel.setBackground(Color.WHITE);
					sLabel.setBackground(Color.WHITE);
				}
			}

		});

		mLabel = new JLabel("分", JLabel.CENTER);
		mLabel.setOpaque(true);
		mLabel.setPreferredSize(new Dimension(20, getHeight()));
		mLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					mulCount = 60;
					hLabel.setBackground(Color.WHITE);
					sLabel.setBackground(Color.WHITE);
				}
			}

		});

		sLabel = new JLabel("秒", JLabel.CENTER);
		sLabel.setOpaque(true);
		sLabel.setPreferredSize(new Dimension(20, getHeight()));
		sLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					mulCount = 1;
					hLabel.setBackground(Color.WHITE);
					mLabel.setBackground(Color.WHITE);
				}
			}

		});

		tField = new JTextField(5);
		tField.setDocument(new NumberLenghtLimitedDmt(3));
		tField.setText("1");
		tField.setHorizontalAlignment(JTextField.CENTER);
		tField.setPreferredSize(new Dimension(25, getHeight()));
		tField.setBorder(BorderFactory.createEmptyBorder());
		tField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				String str = tField.getText();
				int num = tField.getText() == null
						|| "".equals(tField.getText()) ? 0 : Integer
						.valueOf(str);
				if (num == 0) {
					tField.setText("1");
				}
			}

		});

		nextLabel = new JLabel("确定", JLabel.CENTER);
		nextLabel.setOpaque(true);
		nextLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		nextLabel.setPreferredSize(new Dimension(40, getHeight()));
		nextLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					changeToCountPanel();
				}
			}

		});

		prevLabel = new JLabel("取消", JLabel.CENTER);
		prevLabel.setOpaque(true);
		prevLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		prevLabel.setPreferredSize(new Dimension(40, getHeight()));
		prevLabel.addMouseListener(new SelfMouseAdapter() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					mainPanel.remove(setPanel);
					mainPanel.remove(btnPanel);
					mainPanel.add(newLabel);
					repaint();
				}
			}

		});

		btnPanel = new JPanel(new GridLayout(1, 2));

		countPanel = new JPanel(new BorderLayout());
		countLabel = new JLabel("未设置", JLabel.CENTER);
		countLabel.setOpaque(true);
		countLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		countLabel.setFont(new Font("新宋体", Font.PLAIN, 20));
		countLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void selfMouseClicked(MouseEvent e) {

			}
		});

		cancelLabel = new JLabel();
		cancelLabel.setOpaque(true);
		cancelLabel.setBackground(Color.RED);
		cancelLabel.setPreferredSize(new Dimension(30, getHeight()));
		cancelLabel.setToolTipText("取消");
		cancelLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					totalCount = -1;
					mainPanel.remove(countPanel);
					mainPanel.add(newLabel);
					repaint();
					setVisible(true);
				}
			}

		});

		setContentPane(mainPanel);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowDeactivated(WindowEvent e) {
				setVisible(false);
			}

		});
		setAppTray();
	}

	private JLabel getSeparatorLabel() {
		JLabel separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.RED);
		separatorLabel.setPreferredSize(new Dimension(2, getHeight() / 2));
		return separatorLabel;
	}

	private void changeToSetPanel() {
		setPanel.removeAll();
		if (isRight) {
			setPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));
			setPanel.add(sdrLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(sdsLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(sLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(mLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(hLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(tField);
			btnPanel.add(prevLabel);
			btnPanel.add(nextLabel);
			mainPanel.add(btnPanel, BorderLayout.WEST);
		} else {
			setPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
			setPanel.add(tField);
			setPanel.add(getSeparatorLabel());
			setPanel.add(hLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(mLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(sLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(sdsLabel);
			setPanel.add(getSeparatorLabel());
			setPanel.add(sdrLabel);
			btnPanel.add(nextLabel);
			btnPanel.add(prevLabel);
			mainPanel.add(btnPanel, BorderLayout.EAST);
		}
		mainPanel.remove(newLabel);
		mainPanel.add(setPanel);
		repaint();
		setVisible(true);
	}

	private void changeToCountPanel() {
		totalCount = Integer.valueOf(tField.getText()) * mulCount;
		msgStr = "秒后" + ("/p".equals(cmdStr) ? "关机" : "重启");
		countLabel.setText(totalCount + msgStr);
		countPanel.removeAll();
		countPanel.add(countLabel);
		if (isRight) {
			countPanel.add(cancelLabel, BorderLayout.EAST);
		} else {
			countPanel.add(cancelLabel, BorderLayout.WEST);
		}
		mainPanel.remove(newLabel);
		mainPanel.remove(setPanel);
		mainPanel.remove(btnPanel);
		mainPanel.add(countPanel);
		repaint();
		setVisible(true);
		if (totalCount != 0) {
			selfT.schedule(this.new SelfTT(), 0);
		}
	}

	private void setAppTray() {
		if (!SystemTray.isSupported()) {
			System.exit(0);
		}
		// 设置托盘图标
		TrayIcon trayIcon; // 托盘图标对象
		trayIcon = new TrayIcon(new ImageIcon(getClass().getResource(
				"/res/logo.png")).getImage(), "ShutDownUI");
		trayIcon.setImageAutoSize(true); // 设置托盘图标是否自动调整图片
		trayIcon.addMouseListener(new MouseAdapter() { // 添加托盘图标的鼠标事件

			@Override
			public void mouseClicked(MouseEvent e) { // 托盘图标的单击事件
				if (e.getClickCount() == 1) {
					// 鼠标单击
					if (isShowing())
						setVisible(false); // 如果窗体显示。则隐藏
					else {
						if (Toolkit.getDefaultToolkit().getScreenSize()
								.getWidth()
								- e.getXOnScreen() > getWidth()) {
							isRight = true;
							mainPanel.remove(exitLabel);
							exitLabel.setIcon(new ImageIcon(getClass()
									.getResource("/res/logo.png")));
							mainPanel.add(exitLabel, BorderLayout.EAST);
							setLocation(e.getXOnScreen(), e.getYOnScreen()
									- getHeight());
						} else {
							isRight = false;
							mainPanel.remove(exitLabel);
							exitLabel.setIcon(new ImageIcon(getClass()
									.getResource("/res/logo.png")));
							mainPanel.add(exitLabel, BorderLayout.WEST);
							setLocation(e.getXOnScreen() - getWidth(),
									e.getYOnScreen() - getHeight());
						}
						setVisible(true); // 如果窗体隐藏。则显示
					}
				}
			}

		});

		SystemTray tray = SystemTray.getSystemTray(); // 得到系统托盘
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

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
		new ShutDownUI().setVisible(false);
	}

	private class SelfTT extends TimerTask {

		@Override
		public void run() {
			try {
				try {
					if (totalCount == -1) {
						totalCount = 0;
						return;
					}
					if (totalCount == 0) {
						Runtime.getRuntime().exec(
								"cmd /c shutdown " + cmdStr + " /f /t 0");
					} else {
						countLabel.setText(totalCount + msgStr);
						repaint();
					}
					totalCount--;
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			selfT.schedule(new SelfTT(), 1000);
		}

	}

	private class NumberLenghtLimitedDmt extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int limit;

		public NumberLenghtLimitedDmt(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {
			if (str == null) {
				return;
			}
			if ((getLength() + str.length()) <= limit) {
				char[] upper = str.toCharArray();
				int length = 0;
				for (int i = 0; i < upper.length; i++) {
					if (upper[i] >= '0' && upper[i] <= '9') {
						upper[length++] = upper[i];
					}
				}
				super.insertString(offset, new String(upper, 0, length), attr);
			}
		}
	}

	private abstract class SelfMouseAdapter extends MouseAdapter {

		private Color cc = null;

		@Override
		public void mouseEntered(MouseEvent e) {
			cc = e.getComponent().getBackground();
			e.getComponent().setBackground(new Color(0.6f, 0.6f, 0.6f, 0.5f));
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (cc != null) {
				e.getComponent().setBackground(cc);
			}
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				cc = new Color(0.5f, 0.5f, 0.5f, 0.5f);
				e.getComponent().setBackground(
						new Color(0.5f, 0.5f, 0.5f, 0.5f));
				selfMouseClicked(e);
				repaint();
			}
		}

		public abstract void selfMouseClicked(MouseEvent e);

	}

}