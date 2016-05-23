package org.kerwin.shutdownui.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.kerwin.shutdownui.bean.User;
import org.kerwin.shutdownui.bean.UserWithToken;
import org.kerwin.shutdownui.service.Session;
import org.kerwin.shutdownui.service.Theme;
import org.kerwin.shutdownui.service.UserDao;

import weibo4j.Timeline;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

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
	private JTextField hField;
	private JTextField mField;
	private JTextField sField;
	private JPanel countPanel;
	private JLabel countLabel;
	private JLabel cancelLabel;
	private JPanel btnPanel;
	private JLabel nextLabel;
	private JLabel prevLabel;
	private String cmdStr;
	private long totalCount;
	private String msgStr;
	private Timer selfT;
	private JPopupMenu msgPop;
	private JPanel msgPanel;
	private Timeline timeLine;
	private HomeTimelinePane homeTimelinePane;
	private JLabel tjl;

	public static JDialog self;

	public ShutDownUI() {
		self = this;
		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(326, 25); // 设置窗体大小
		setResizable(false); // 设置窗体大小不可变
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2
				- getWidth() / 2, 0);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		msgPanel = new JPanel(new BorderLayout());
		msgPanel.setPreferredSize(new Dimension(getWidth() - 2, 500));

		msgPop = new JPopupMenu();
		msgPop.add(msgPanel);
		msgPop.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		msgPop.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				hideMsgPop();
			}
		});

		exitLabel = new JLabel();
		exitLabel.setPreferredSize(new Dimension(getHeight(), getHeight()));
		exitLabel.setToolTipText("O(∩_∩)O 单击笑话、双击退出");
		exitLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					hideMsgPop();
				} else if (e.getClickCount() == 2) {
					System.exit(0);
				} else if (e.getClickCount() == 1) {
					if (exitLabel.getComponentPopupMenu() != null) {
						hideMsgPop();
					} else {
						if (homeTimelinePane == null) {
							if(tjl != null)	msgPanel.remove(tjl);
							tjl = new JLabel(Theme
									.getThemeRec(Theme.SysRec.loadingImage));
							msgPanel.add(tjl);
							validate();
							new Thread(new Runnable() {
								@Override
								public void run() {
									timeLine = new Timeline();
									User user = new UserDao().selectAllUser()
											.get(0);
									timeLine.client.setToken(user
											.getAccessToken());
									UserWithToken uwt = null;
									try {
										uwt = new UserWithToken(
												user.getAccessToken(),
												new UserDao()
														.getWeiboUserByUser(user));
									} catch (JSONException e1) {
										e1.printStackTrace();
									} catch (WeiboException e1) {
										e1.printStackTrace();
									}
									if(uwt == null){
										tjl.setIcon(null);
										tjl.setText("加载失败、请重试！");
										return;
									}
									// 设置本次登陆用户到Session
									Session.set("currentUser", uwt);
									homeTimelinePane = new HomeTimelinePane();
									tjl.setVisible(false);
									msgPanel.add(homeTimelinePane);
									homeTimelinePane.addList();
									validate();
								}
							}).start();
						}
						showMsgPop();
					}
				}
			}

		});
		mainPanel.add(exitLabel, BorderLayout.EAST);

		newLabel = new JLabel("新建任务", Theme.getThemeRec(Theme.SysRec.newImage),
				JLabel.CENTER);
		newLabel.setOpaque(true);
		newLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		newLabel.setFont(new Font("新宋体", Font.PLAIN, 20));
		newLabel.addMouseListener(new SelfMouseAdapter1() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					changeToSetPanel();
				}
			}

		});
		newLabel.addMouseListener(new org.kerwin.shutdownui.service.SelfMouseAdapter1(
				this));
		mainPanel.add(newLabel);

		setPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		sdsLabel = new JLabel("关机", JLabel.CENTER);
		sdsLabel.setOpaque(true);
		cmdStr = "/s";
		sdsLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		sdsLabel.setPreferredSize(new Dimension(30, getHeight()));
		sdsLabel.addMouseListener(new SelfMouseAdapter1() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				cmdStr = "/s";
				sdrLabel.setBackground(Color.WHITE);
			}

		});

		sdrLabel = new JLabel("重启", JLabel.CENTER);
		sdrLabel.setOpaque(true);
		sdrLabel.setPreferredSize(new Dimension(30, getHeight()));
		sdrLabel.addMouseListener(new SelfMouseAdapter1() {

			@Override
			public void selfMouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					cmdStr = "/r";
					sdsLabel.setBackground(Color.WHITE);
				}
			}

		});

		hLabel = new JLabel("时", JLabel.CENTER);

		mLabel = new JLabel("分", JLabel.CENTER);

		sLabel = new JLabel("秒", JLabel.CENTER);

		hField = new JTextField(4);
		hField.setDocument(new NumberLenghtLimitedDmt(3));
		hField.setText("1");
		hField.setHorizontalAlignment(JTextField.CENTER);
		hField.setPreferredSize(new Dimension(27, getHeight()));
		hField.setBorder(BorderFactory.createEmptyBorder());

		mField = new JTextField(4);
		mField.setDocument(new NumberLenghtLimitedDmt(3));
		mField.setText("0");
		mField.setHorizontalAlignment(JTextField.CENTER);
		mField.setPreferredSize(new Dimension(27, getHeight()));
		mField.setBorder(BorderFactory.createEmptyBorder());

		sField = new JTextField(4);
		sField.setDocument(new NumberLenghtLimitedDmt(3));
		sField.setText("0");
		sField.setHorizontalAlignment(JTextField.CENTER);
		sField.setPreferredSize(new Dimension(27, getHeight()));
		sField.setBorder(BorderFactory.createEmptyBorder());

		nextLabel = new JLabel("确定", JLabel.CENTER);
		nextLabel.setOpaque(true);
		nextLabel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		nextLabel.setPreferredSize(new Dimension(40, getHeight()));
		nextLabel.addMouseListener(new SelfMouseAdapter1() {

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
		prevLabel.addMouseListener(new SelfMouseAdapter1() {

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
		countLabel.addMouseListener(new SelfMouseAdapter1() {
			@Override
			public void selfMouseClicked(MouseEvent e) {

			}
		});
		countLabel
				.addMouseListener(new org.kerwin.shutdownui.service.SelfMouseAdapter1(
						this));

		cancelLabel = new JLabel();
		cancelLabel.setOpaque(true);
		cancelLabel.setBackground(Color.RED);
		cancelLabel.setPreferredSize(new Dimension(getHeight(), getHeight()));
		cancelLabel.setToolTipText("取消任务");
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

		selfT = new Timer("ShutDownUI");
		exitLabel.setIcon(Theme.getThemeRec(Theme.SysRec.trayImage));
		exitLabel
				.addMouseListener(new org.kerwin.shutdownui.service.SelfMouseAdapter1(
						this));
		mainPanel.add(exitLabel, BorderLayout.WEST);
		setContentPane(mainPanel);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowDeactivated(WindowEvent e) {
				if (exitLabel.getComponentPopupMenu() != null) {
					hideMsgPop();
				}
			}

		});
		setAppTray();
	}

	private void hideMsgPop() {
		msgPop.setVisible(false);
		exitLabel.setComponentPopupMenu(null);
	}

	private void showMsgPop() {
		exitLabel.setComponentPopupMenu(msgPop);
		if (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getY()
				- getHeight() >= msgPop.getHeight()) {
			msgPop.show(mainPanel, mainPanel.getX(), mainPanel.getY()
					+ mainPanel.getHeight());
		} else {
			msgPop.show(mainPanel, mainPanel.getX(), -msgPop.getHeight());
		}
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
		setPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		setPanel.add(hField);
		setPanel.add(hLabel);
		setPanel.add(getSeparatorLabel());
		setPanel.add(mField);
		setPanel.add(mLabel);
		setPanel.add(getSeparatorLabel());
		setPanel.add(sField);
		setPanel.add(sLabel);
		setPanel.add(getSeparatorLabel());
		setPanel.add(sdsLabel);
		setPanel.add(getSeparatorLabel());
		setPanel.add(sdrLabel);
		btnPanel.add(nextLabel);
		btnPanel.add(prevLabel);
		mainPanel.add(btnPanel, BorderLayout.EAST);
		mainPanel.remove(newLabel);
		mainPanel.add(setPanel);
		repaint();
		setVisible(true);
	}

	private String getMsgStr() {
		String msg = "";
		long ih = (totalCount / 3600);
		long im = (totalCount - (ih * 3600)) / 60;
		long is = (totalCount - (ih * 3600)) % 60;
		if (ih > 0) {
			msg += (ih + "时");
		}
		if (im > 9) {
			msg += (im + "分");
		} else {
			if (!"".equals(msg) || im != 0) {
				msg += ("0" + im + "分");
			}
		}
		if (is > 9) {
			msg += (is + "秒");
		} else {
			msg += ("0" + is + "秒");
		}
		return msg;
	}

	private void changeToCountPanel() {
		String h = hField.getText();
		String m = mField.getText();
		String s = sField.getText();
		if (h != null && !"".equals(h)) {
			totalCount = totalCount + Integer.valueOf(h) * 3600;
		}
		if (m != null && !"".equals(m)) {
			totalCount = totalCount + Integer.valueOf(m) * 60;
		}
		if (s != null && !"".equals(s)) {
			totalCount = totalCount + Integer.valueOf(s) * 1;
		}
		if (totalCount == 0) {
			totalCount = 3600;
		}
		msgStr = "后" + ("/s".equals(cmdStr) ? "关机" : "重启");
		countLabel.setText(getMsgStr() + msgStr);
		countPanel.removeAll();
		countPanel.add(countLabel);
		countPanel.add(cancelLabel, BorderLayout.WEST);
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
		// 设置托盘图标
		TrayIcon trayIcon; // 托盘图标对象
		trayIcon = new TrayIcon(
				Theme.getThemeRec(Theme.SysRec.trayImage) == null ? new ImageIcon().getImage()
						: Theme.getThemeRec(Theme.SysRec.trayImage).getImage(),
				"ShutDownUI", popupMenu);
		trayIcon.setImageAutoSize(true); // 设置托盘图标是否自动调整图片
		trayIcon.addMouseListener(new MouseAdapter() { // 添加托盘图标的鼠标事件

			@Override
			public void mouseClicked(MouseEvent e) { // 托盘图标的单击事件
				if (e.getClickCount() == 1
						&& e.getButton() != MouseEvent.BUTTON3) {
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
						countLabel.setText(getMsgStr() + msgStr);
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

	private abstract class SelfMouseAdapter1 extends MouseAdapter {

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