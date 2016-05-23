package org.kerwin.tools;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.kerwin.task.KerwinTaskScheduler;
import org.kerwin.task.KerwinTasker;
import org.kerwin.tools.bean.KerwinTaskerBean;
import org.kerwin.tools.tasker.TaskerPanel;
import org.kerwin.tools.terminal.TerminalPanel;

public class ServerTools extends KerwinFrame{

	private static final long serialVersionUID = 1L;
	private final String LOGOPATH = "/res/logo.png";
	private final String XPATH = "/res/x.png";
	private final String XHPATH = "/res/xh.png";
	private final String FNAME = "ServerTools";
	private TrayIcon trayIcon;		//任务栏图标
	private PopupMenu trayPopup;	//任务栏右键菜单
	private JLabel closeLabel;
	private JPanel mainPanel;
	
	private JPanel tabTPanel;
	private List<JComponent> tabCPanels;
	private JComponent terminalTPanel;
	private JComponent taskerTPanel;

	public ServerTools(){
		initTrayIcon();			//初始化任务栏图标
		setTitle(FNAME);		//设置窗体标题
		setSize(800,600);		//设置大小
//		setResizable(false);	//设置窗体不能调大小
		setBorder(new Color(61,152,30,225));		//设置边框颜色
		setMinimumSize(getSize());					//设置窗体最小化大小
//		setMaxLabelEnabled(false);					//设置不显示最大化标签
		setMinLabelEnabled(false);					//设置不显示最小化标签
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);		//设置窗体的默认关闭操作
		//设置窗体居中显示
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		//设置窗体图标
		setIconImage(new ImageIcon(getClass().getResource(LOGOPATH)).getImage());
		//初始化操作栏面板
		initOperBorderPanel();
		//初始化内容面板
		initContentPanel();
	}
	
	/**
	 * 初始化顶部工具条
	 */
	private void initOperBorderPanel(){
		getTopPanel().setPreferredSize(new Dimension(getWidth(),20));

		//设置关闭标签
		closeLabel = getCloseLabel();
		closeLabel.setText("");
		closeLabel.setIcon(new ImageIcon(getClass().getResource(XPATH)));
		closeLabel.setToolTipText("Close");
		closeLabel.setPreferredSize(new Dimension(26, getTopPanel().getPreferredSize().height));
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				closeLabel.setIcon(new ImageIcon(getClass().getResource(XPATH)));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				closeLabel.setIcon(new ImageIcon(getClass().getResource(XHPATH)));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		
		final JLabel datetimeLabel = new JLabel();
		getOperPanel().add(datetimeLabel,0);
		getOperPanel().add(new JLabel(" "),1);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				datetimeLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E").format(new Date()));
			}
		}, 1L, 1000L);
		
//		final JLabel helpLabel = new JLabel("Help ");
//		getOperPanel().add(helpLabel,1);
//		helpLabel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				
//			}
//		});
		
		getTopPanel().setOpaque(false);
		getOperPanel().setOpaque(false);
		getMainPanel().setOpaque(false);
		getTitleLabel().setOpaque(false);
		getTitleLabel().setForeground(Color.BLUE);
		KerwinFrame.setFontToChildren(getTopPanel(),new Font(Font.MONOSPACED,Font.BOLD,14));
	}
	
	/**
	 * 初始化内容面板
	 */
	private void initContentPanel(){
		mainPanel = getContentPanel();
		mainPanel.setBackground(Color.BLACK);
		
		tabTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,1,0));
		tabTPanel.setPreferredSize(new Dimension(mainPanel.getWidth(),30));
		tabTPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.RED), BorderFactory.createEmptyBorder(0, 0, 3, 0)));
		mainPanel.add(tabTPanel,BorderLayout.NORTH);
		
		tabCPanels = new ArrayList<JComponent>();
		
		terminalTPanel = new TerminalPanel();
		terminalTPanel.setName("Terminal");
		mainPanel.add(terminalTPanel);
		tabCPanels.add(terminalTPanel);
		
		taskerTPanel = new TaskerPanel();
		taskerTPanel.setName("Tasker");
		tabCPanels.add(taskerTPanel);
		
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(src.getName()))	return;
				src.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(src.getName()))	return;
				src.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.RED), BorderFactory.createMatteBorder(0, 0, 3, 0, Color.PINK)));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = null;
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(name = src.getName()))	return;
				Component[] cs = tabTPanel.getComponents();
				for(Component c : cs){
					if("current".equals(c.getName()))	c.setName(mainPanel.getComponent(mainPanel.getComponents().length - 1).getName());
					((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
				}
				for(Component c : tabCPanels){
					if(name.equals(c.getName())){
						mainPanel.add(c);
						repaint();
					}else{
						mainPanel.remove(c);
						validate();
					}	
				}
				setVisible(true);
				src.setName("current");
				src.setBorder(BorderFactory.createMatteBorder(0, 0, 6, 0, Color.RED));
			}
		};
		
		JLabel terminalTLabel = new JLabel("Terminal");
		terminalTLabel.setFont(new Font(terminalTLabel.getFont().getName(),Font.BOLD,16));
		terminalTLabel.setPreferredSize(new Dimension(80,tabTPanel.getPreferredSize().height));
		terminalTLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 6, 0, Color.RED));
		terminalTLabel.setName("current");
		terminalTLabel.setHorizontalAlignment(JLabel.CENTER);
		terminalTLabel.addMouseListener(ml);
		tabTPanel.add(terminalTLabel);
		
		JLabel taskerTLabel = new JLabel("Tasker");
		taskerTLabel.setFont(new Font(taskerTLabel.getFont().getName(),Font.BOLD,16));
		taskerTLabel.setPreferredSize(new Dimension(80,tabTPanel.getPreferredSize().height));
		taskerTLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		taskerTLabel.setName("Tasker");
		taskerTLabel.setHorizontalAlignment(JLabel.CENTER);
		taskerTLabel.addMouseListener(ml);
		tabTPanel.add(taskerTLabel);
	}
	
	/**
	 * 初始化任务栏图标
	 */
	private void initTrayIcon(){
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray(); // 得到系统托盘
			try {
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
						KerwinTasker[] kts = KerwinTaskScheduler.getKTS();
						if(kts != null && kts.length > 0){
							if(JOptionPane.showConfirmDialog(ServerTools.this, "Some taskers is being executed or waiting to execute, confirm exit?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION)	return;
						}
						if(fileLock != null){
							try {
								fileLock.release();
								fileChannel.close();
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if(new File(LOCKFILE).exists())	new File(LOCKFILE).deleteOnExit();
						System.exit(0);
					}

				});
				// 取得托盘图标的右键菜单对象
				trayPopup = new PopupMenu();
				trayPopup.add(showItem);
				trayPopup.add(exitItem);
				
				// 设置托盘图标：（图标 | 鼠标悬浮托盘后显示的文字 | 托盘图标右键菜单栏）
				trayIcon = new TrayIcon(new ImageIcon(getClass().getResource(
						LOGOPATH)).getImage(), "ServerTools", trayPopup); // 托盘图标对象
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
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private static final String LOCKFILE = "KerwinTasker~";
	private static FileChannel fileChannel;
	private static FileOutputStream fos;
	private static FileLock fileLock;

	public static void main(String[] args){
		ServerTools st = new ServerTools();
		try {
			if (null != (fileChannel = (fos = new FileOutputStream(LOCKFILE)).getChannel()) && null != (fileLock = fileChannel.tryLock())) {
				//读取任务、添加到任务管理器
				List<KerwinTaskerBean> ktbs = KerwinTaskerBean.selectAll();
				if(ktbs!=null&&ktbs.size()>0){
					for(final KerwinTaskerBean ktb : ktbs){
						KerwinTaskScheduler.addKerwinTasker(ktb.getKt());
					}
				}
				//启动任务管理器
				KerwinTaskScheduler.exec();
				st.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(st,st.getTitle()+" is running...","Message",JOptionPane.CLOSED_OPTION);
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
