package coderr.kerwin.arstat.pc;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import coderr.kerwin.arstat.AtteRecordBean;
import coderr.kerwin.arstat.AtteRecordDAO;
import coderr.kerwin.arstat.AtteRecordStatBean;
import coderr.kerwin.arstat.AtteRuleInfo;
import coderr.kerwin.arstat.DayTagsDAO;


/**
 * 主程序
 * @author kewrin612
 */
public class MainFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private AtteRecordDAO arDAO;
	private DayTagsDAO dtDAO;
	private Date currentDate;
	private JPanel topPanel;
	private JLabel pLabel;
	private JLabel nLabel;
	private JLabel lLabel;
	private JButton sButton;
	private JButton eButton;
	private JButton iButton;
	private CalendarPanel calPanel;
	private Image icon;

	private final String LOGOPATH = "/res/logo.png";
	
	public MainFrame() {
		signIn();
		setView();
		setSize(400, 350);
		setResizable(false);
		try {
			icon = new ImageIcon(getClass().getResource(LOGOPATH)).getImage();
			setIconImage(icon);
			initTrayIcon();
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		} catch (Exception e) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			e.printStackTrace();
		}
		Dimension screenSize = getToolkit().getScreenSize();
		setLocation((int)(screenSize.getWidth() - getSize().getWidth()) / 2, (int)(screenSize.getHeight() - getSize().getHeight()) / 2);
		
		topPanel = new JPanel(new BorderLayout());
		topPanel.add(initialMathPanel(), BorderLayout.WEST);
		
		JPanel tmpPanel = new JPanel(new GridLayout(1, 2));
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		sButton = new JButton("设置");
		sButton.setFocusPainted(false);
		sButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AtteRuleInfoDialog(dtDAO, currentDate).setVisible(true);
				refresh();
			}
		});
		tmpPanel.add(sButton);
		eButton = new JButton("导出");
		eButton.setFocusPainted(false);
		eButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(currentDate);
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH) + 1;
				List<AtteRecordStatBean> list = arDAO.getAtteRecordCalendar(year, month, true, true);
				File path = arDAO.exportData(currentDate, list);
				if (path == null) {
					//导出失败
					JOptionPane.showMessageDialog(MainFrame.this, "导出失败！");
				} else {
					try {
						Desktop.getDesktop().open(path.getParentFile());
						path = null;
					} catch (IOException e1) {
						e1.printStackTrace();
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setFileFilter(new FileFilter() {
							@Override
							public String getDescription() {
								return "*.xls,*.xlsx,*.wps";
							}
							
							@Override
							public boolean accept(File f) {
								if (f == null || f.isDirectory())	return false;
								//显示满足条件的文件
								return f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx") || f.getName().endsWith(".wps");	
							}
						});
						fileChooser.showSaveDialog(MainFrame.this);
						File file = fileChooser.getSelectedFile();
						if (file == null)	return;
						if (!(file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx") || file.getName().endsWith(".wps"))) {
							JOptionPane.showMessageDialog(MainFrame.this, "仅支持[xls,xlsx,wps]类型的文件！");
							return;
						}
						InputStream is = null;
						OutputStream os = null;
						try {
							is = new FileInputStream(path);
							os = new FileOutputStream(file);
							byte[] bs = new byte[1024];
							int index = -1;
							while ((index = is.read(bs)) != -1) {
								os.write(bs, 0, index);
							}
							JOptionPane.showMessageDialog(MainFrame.this, "导出成功！");
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
								try {
									if (os != null) os.close();
									if (is != null)	is.close();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
						}
						file = null;
					} 
				}
			}
		});
		tmpPanel.add(eButton);
		iButton = new JButton("导入");
		iButton.setFocusPainted(false);
		iButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iButton.setText("^_^");
				iButton.setToolTipText("导入功能尚未实现！");
			}
		});
		tmpPanel.add(iButton);
		topPanel.add(tmpPanel);
		
		add(topPanel, BorderLayout.NORTH);
		
		currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DAY_OF_MONTH) < AtteRuleInfo.getSDay()) {
			cal.add(Calendar.MONTH, -1);
			currentDate = cal.getTime();
		}
		setDate(currentDate);
	}
	
	/**
	 * 设置全局背景色和字体
	 */
	private void setView(){
		//设置全局背景色和字体
		Enumeration<Object> keys = UIManager.getDefaults().keys();  
	    Object key = null;  
	    Object value = null;  
	    while (keys.hasMoreElements()) {  
	        key = keys.nextElement();  
	        value = UIManager.get(key);  
	        if(key instanceof String ) {  
	            /**设置全局的背景色*/  
	            if(((String) key).endsWith(".background")) {  
	                UIManager.put(key, Color.white);  
	            }  
	        }  
	          
	        /**设置全局的字体*/  
	        if(value instanceof Font) {  
	            UIManager.put(key, new Font(Font.DIALOG,Font.PLAIN,12));
	        }  
	    }
	}
	
	/**
	 * 刷新面板
	 */
	public void refresh() {
		setDate(currentDate);
		setVisible(true);
	}
	
	/**
	 * 设置当前日期
	 * @param date
	 */
	private void setDate(final Date date) {
		if (date == null)	return;
		if (calPanel != null)	remove(calPanel);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		add(calPanel = new CalendarPanel(arDAO, arDAO.getAtteRecordCalendar(year, month, true), MainFrame.this));
		lLabel.setText("<html>&nbsp;&nbsp;&nbsp;<span style='font-size:20px;'>"+month+"</span>&nbsp;&nbsp;&nbsp;"+"</html>");
		setTitle(new SimpleDateFormat("yyyy年MM月考勤统计").format(date));
	}
	
	/**
	 * 初始化左上角月份面板
	 * @return
	 */
	private JPanel initialMathPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		pLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;<span style='font-size:15px;'>&lt;</span>&nbsp;&nbsp;&nbsp;</html>", JLabel.CENTER);
		MouseListener ml = new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				e.getComponent().setForeground(Color.BLACK);
			}
			
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setForeground(Color.GRAY);				
			}
			
			public void mouseClicked(MouseEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(currentDate);
				cal.add(Calendar.MONTH, e.getComponent().equals(pLabel) ? -1 : 1);
				setDate(currentDate = cal.getTime());
			}
		};
		pLabel.addMouseListener(ml);
		panel.add(pLabel, BorderLayout.WEST);
		nLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;<span style='font-size:15px;'>&gt;</span>&nbsp;&nbsp;&nbsp;</html>", JLabel.CENTER);
		nLabel.addMouseListener(ml);
		panel.add(nLabel, BorderLayout.EAST);
		lLabel = new JLabel("", JLabel.CENTER);
		panel.add(lLabel);
		return panel;
	}
	
	private void signIn() {
		dtDAO = new DayTagsDAOPCImpl();
		arDAO = new AtteRecordDAOPCImpl(dtDAO);
		AtteRecordBean bean = arDAO.getAtteRecordBean(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -5);
		if (bean.getSTime() == null) {
			bean.setSTime(cal.getTime());
		}
		if (bean.getETime() != null) {
			bean.setETime(null);
		}
		arDAO.save(bean);
	}
	
	private void signOut() {
		AtteRecordBean bean = arDAO.getAtteRecordBean(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, 5);
		if (bean.getETime() == null && bean.getSTime() != null) {
			bean.setETime(cal.getTime());
			arDAO.save(bean);
		}
	}
	
	/**
	 * 初始化任务栏图标
	 */
	private void initTrayIcon() {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray(); // 得到系统托盘
			try {
				MenuItem exitItem = new MenuItem("exit");
				exitItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 单击该项、退出程序
						signOut();
						if(fileLock != null){
							try {
								fileLock.release();
								fileChannel.close();
								fos.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
						if(new File(LOCKFILE).exists())	new File(LOCKFILE).deleteOnExit();
						System.exit(0);						
					}
				});
				// 取得托盘图标的右键菜单对象
				PopupMenu trayPopup = new PopupMenu();
				trayPopup.add(exitItem);
				
				// 设置托盘图标：（图标 | 鼠标悬浮托盘后显示的文字 | 托盘图标右键菜单栏）
				TrayIcon trayIcon = new TrayIcon(icon, "考勤统计", trayPopup); // 托盘图标对象
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
	
	public static void main(String[] args) {
		JFrame frame = new MainFrame();
		try {
			if (null != (fileChannel = (fos = new FileOutputStream(LOCKFILE)).getChannel()) && null != (fileLock = fileChannel.tryLock())) {
				frame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(frame,"考勤统计正在运行中...","消息",JOptionPane.CLOSED_OPTION);
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
