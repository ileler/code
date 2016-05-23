package org.kerwin.tools;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Tools extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private String logoPath;

	public Tools() {
		logoPath = "/logo.png";
		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(300, 0); // 设置窗体大小
		setResizable(false); // 设置窗体大小不可变
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2
				- getWidth() / 2, 0);
		mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		mainPanel.add(new SearchPwdPanel(this), BorderLayout.NORTH);
		mainPanel.add(new SearchPlatformLinkPanel(this));
		setAppTray();
		setVisible(true);
	}

	private void setAppTray() {
		if (!SystemTray.isSupported()) {
			return;
		}
		MenuItem showItem = new MenuItem("show");
		showItem.addActionListener(new ActionListener() { // 给该菜单项添加事件

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 显示本窗体
				setVisible(true);
			}

		});

		MenuItem exitItem = new MenuItem("exit");
		exitItem.addActionListener(new ActionListener() { // 给该菜单项添加事件

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 单击该项、退出程序
				System.exit(0);
			}

		});

		// 取得托盘图标的右键菜单对象
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.add(showItem);
		popupMenu.add(exitItem);

		// 设置托盘图标：（图标 | 鼠标悬浮托盘后显示的文字 | 托盘图标右键菜单栏）
		TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource(
				logoPath)).getImage(), "Tools", popupMenu); // 托盘图标对象
		trayIcon.setImageAutoSize(true); // 设置托盘图标是否自动调整图片
		trayIcon.addMouseListener(new MouseAdapter() { // 添加托盘图标的鼠标事件

			@Override
			public void mouseClicked(MouseEvent e) { // 托盘图标的单击事件
				if (e.getClickCount() == 1
						&& e.getButton() != MouseEvent.BUTTON3) {
					// 鼠标单击
					if (isShowing())
						setVisible(false); // 如果窗体显示。则隐藏
					else
						setVisible(true); // 如果窗体隐藏。则显示
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
		new Tools().requestFocus(false);
	}

}