package org.kerwin.weibo.util;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kerwin.weibo.service.Theme;


public class FrameUtil {
	
	private static Toolkit toolKit;
	private static Dimension screenSize;
	private static SystemTray tray;		//系统托盘对象
	private static TrayIcon trayIcon;		//托盘图标对象
	private static PopupMenu popupMenu;		//托盘图标右键菜单
	private static MenuItem showItem;
	private static MenuItem exitItem;
	
	static{
		toolKit = Toolkit.getDefaultToolkit();
		screenSize = toolKit.getScreenSize();
	}
	
	public static Dimension getScreenSize(){
		return screenSize;
	}
	
	/**
	 * 得到屏幕的宽度
	 * @return 返回double为屏幕的宽度
	 */
	public static double getScreenWidth(){
		return screenSize.getWidth();
	}
	
	/**
	 * 得到屏幕高度
	 * @return 返回double类型为屏幕的高度
	 */
	public static double getScreenHeight(){
		return screenSize.getHeight();
	}
	
	public static JFrame getCurrentFrame(){
		Frame[] jfs = JFrame.getFrames();
		for(Frame jf : jfs){
			if(jf.isFocusableWindow())
				return (JFrame)jf;
		}
		return null;
	}
	
	public static void locationToScreenCenter(JFrame frame){
		if(frame == null)
			return;
		frame.setLocation(
				screenSize.width / 2 - frame.getWidth() / 2,
				screenSize.height / 2 - frame.getHeight() / 2
				);
	}
	
	/**
	 * @return		返回托盘图标对象
	 * 此方法是用来获得系统任务栏图标
	 */
	public static TrayIcon getTrayIcon(){
		return (trayIcon == null) ? trayIcon = new TrayIcon(Theme.getTrayImage().getImage(), "微博", getPopupMenu()): trayIcon;
	}
	
	/**
	 * @return		返回托盘右键菜单
	 * 此方法是用来获得系统托盘图标的右键菜单
	 */
	public static PopupMenu getPopupMenu(){
		return (popupMenu == null) ? popupMenu = new PopupMenu() : popupMenu;
	}
	
	/**
	 * @return		返回系统托盘对象
	 * 此方法是用来获得程序所注册的系统托盘
	 */
	public static SystemTray getSystemTray(final JFrame frame){
		if(frame == null || !SystemTray.isSupported())
			return null;
		if(tray != null)
			return tray;
		//设置托盘图标：（图标 | 鼠标悬浮托盘后显示的文字 | 托盘图标右键菜单栏）
		trayIcon = getTrayIcon();	
		trayIcon.setImageAutoSize(true);	//设置托盘图标是否自动调整图片
		trayIcon.addMouseListener(new MouseAdapter(){	//添加托盘图标的鼠标事件
			
			@Override
			public void mouseClicked(MouseEvent e) {	//托盘图标的单击事件
				if(e.getClickCount() == 1 && e.getButton() != MouseEvent.BUTTON3){
					//鼠标单击
					if(frame.isShowing())
						frame.setVisible(false);	//如果窗体显示。则隐藏
					else
						frame.setVisible(true);	//如果窗体隐藏。则显示
				}
			}
			
		});
		//取得托盘图标的右键菜单对象
		popupMenu = FrameUtil.getPopupMenu();
		popupMenu.add(getShowItem(frame));
		popupMenu.add(getExitItem());
		tray = SystemTray.getSystemTray();	//得到系统托盘
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		trayIcon.displayMessage("新浪微博JAVA客户端", "开发者：杰倫_", MessageType.INFO);
		return tray;
	}
	
	/**
	 * @return		返回退出显示项
	 * 此方法用来获得系统托盘图标的右键菜单显示项
	 */
	private static MenuItem getShowItem(final JFrame frame){
		showItem = new MenuItem("show");
		showItem.addActionListener(new ActionListener() {	//给该菜单项添加事件
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//显示本窗体
				frame.setVisible(true);
			}
			
		});
		return showItem;
	}
	
	/**
	 * @return		返回退出菜单项
	 * 此方法用来获得系统托盘图标的右键菜单退出项
	 */
	private static MenuItem getExitItem(){
		exitItem = new MenuItem("exit");
		exitItem.addActionListener(new ActionListener() {	//给该菜单项添加事件
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//单击该项、退出程序
				System.exit(0);
			}
			
		});
		return exitItem;
	}
	
	public static String getURLValue(String url, String key){
		Map<String, String> args = new HashMap<String, String>();
		String[] strs = url.split("\\?").length >= 2 ? url.split("\\?")[1].split("\\&") : new String[]{};
		for(String str : strs){
			String[] tmp = str.split("=");
			args.put(tmp[0], tmp[1]);
		}
		if(args.size() == 0 || !args.keySet().contains(key))	return null;
		return args.get(key);
	}
	
	/**
	 * 设置所有子组件透明
	 * @param component	要设置的组件
	 * @param opaque	是否透明
	 */
	public static void setAllOpaque(JComponent component,boolean opaque){
		component.setOpaque(opaque);
		component.repaint();
		Component[] cs = component.getComponents();
		for(Component c : cs){
			if(c instanceof JComponent){
				setAllOpaque((JComponent)c,opaque);
			}
		}
	}
	
}
