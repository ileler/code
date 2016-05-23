package org.kerwin.weibo.view;

import java.awt.Dimension;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.kerwin.weibo.mode.User;
import org.kerwin.weibo.service.FrameEvent;
import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

enum RequestInfo{
	intoLoginPanel,intoOauthPanel,intoMainPanel
}

public class MainFrame extends SelfFrame {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(MainFrame.class);
	private LoginPanel loginPanel;	//登陆面板
	private OAuthFrame oauthPanel;	//授权面板
	private MainPanel mainPanel;	//主面板
	public FrameEvent<RequestInfo> event;

	public MainFrame(){
		super();
		//这个可以设置一个JFrame的透明度。取值(0-1)。0为全透明、1为不透明
//		com.sun.awt.AWTUtilities.setWindowOpacity(this, 1); 
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		event = new FrameEvent<RequestInfo>(){
			@Override
			public void echo(Observable arg0, Request arg1) {
				if(arg1 == null)	return;
//				setVisible(false);
				dispose();
				final Request request = arg1;
				final RequestInfo requestInfo = request.getInfo();
				log.debug("request: "+requestInfo);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(requestInfo.equals(RequestInfo.intoLoginPanel)){
							intoLoginPanel();
						}else if(requestInfo.equals(RequestInfo.intoOauthPanel)){
							intoOauthPanel();
						}else if(requestInfo.equals(RequestInfo.intoMainPanel)){
							intoMainPanel((request.getArgs() != null && request.getArgs().length != 0) ? (User)request.getArgs()[0] : null);
						}
						validate();
					}
				});
			}
		};
		setTitle("KerwinV1");
		setIconImage(Theme.getTrayImage().getImage());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);	
		FrameUtil.getSystemTray(this);		//设置系统托盘
		intoLoginPanel();
	}
	
	public MainFrame(String title){
		this();
		setTitle(title);
	}
	
	/**
	 * 进入登陆面板
	 */
	private void intoLoginPanel(){
		if(loginPanel == null){
			loginPanel = new LoginPanel();	
			setContentPane(loginPanel);
		}
		setMinimumSize(new Dimension(300,200));	//设置窗体最小的大小
		setSize(300,200);	//设置窗体大小
		setResizable(false);	//设置窗体大小不可变
		FrameUtil.locationToScreenCenter(this);
		loginPanel.refreshPanel();
		showPanel(loginPanel);
		setBgImage(Theme.getVersionImage());	//设置窗体背景图
	}
	
	/**
	 * 进入授权页面
	 */
	private void intoOauthPanel(){
		oauthPanel = new OAuthFrame();
	}

	/**
	 * 进入主面板
	 */
	private void intoMainPanel(User user){
		if(oauthPanel != null)	oauthPanel.dispose();
		mainPanel = new MainPanel(user);
		setSize(315,650);	//设置窗体大小
//		setResizable(false);	//设置窗体大小不可变
		setResizable(true);	
		setMinimumSize(new Dimension(getWidth(),450));	//设置窗体最小的大小
		setLocation((int)FrameUtil.getScreenWidth()-getWidth()-100, 30);
		showPanel(mainPanel);
		setBgImage(Theme.getBgImage());	//设置窗体背景图
	}
	
	/**
	 * 显示指定面板
	 * @param jp
	 */
	private void showPanel(JPanel jp){
		setContentPane(jp);
		try {
			Thread.sleep(50);
			validate();
			setVisible(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
