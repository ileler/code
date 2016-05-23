package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.kerwin.weibo.dao.UserDao;
import org.kerwin.weibo.dao.bin.impl.UserDaoBinImpl;
import org.kerwin.weibo.mode.User;
import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.util.WeiboConfig;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;

public class OAuthFrame extends SelfFrame {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(OAuthFrame.class);
	private String oauthURL;	//授权页地址
	private UserDao userDao;	//用户信息操作类
	private JLabel msgLabel;	//显示消息的标签
	private JPanel browserPanel;	//此面板用来放浏览器
	private JWebBrowser browser;	//浏览器对象
	private JPanel btnPanel;	//存放按钮的面板
	private JButton cancelButton;	//取消按钮
	private JButton ticklingButton;	//反馈按钮

	/**
	 * @param mainFrame
	 */
	public OAuthFrame(){
		log.debug("来到授权页");
		setTitle("KerwinV1");
		setSize(650,490);
		setResizable(false);	//设置窗体大小不可变
		setIconImage(Theme.getTrayImage().getImage());	//设置图标
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		FrameUtil.locationToScreenCenter(this);
		setMaxLabelEnabled(false);
		setCloseLabelEnabled(false);
		btnPanel = new SelfPanel(new FlowLayout(FlowLayout.CENTER));	//初始化按钮面板
		btnPanel.setOpaque(false);	//设置透明
		//添加按钮
		cancelButton = getCancelButton();
		ticklingButton = getTicklingButton();
		btnPanel.add(cancelButton);	
//		btnPanel.add(ticklingButton);
		add(btnPanel,BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				openLoginFrame();	
				dispose();
			}
		});
		setMessage("加载授权页");	//设置窗体显示消息。
		setVisible(true);
		setBgImage(Theme.getBgImage());	//设置窗体背景图
		
		new Thread(new Runnable(){
			public void run(){
				new Thread(new Runnable(){
					public void run(){
						userDao = new UserDaoBinImpl();	//实例化用户信息操作对象
						oauthURL = userDao.getOauthURL();	//得到授权页面地址
					}
				}).start();
				initialBrowserPanel();	//初始化浏览器面板
			}
		}).start();
		
	}
	
	/**
	 * 初始化浏览器面板
	 */
	private void initialBrowserPanel(){
		if(!NativeInterface.isOpen())	NativeInterface.open();
		UIUtils.setPreferredLookAndFeel();
		browserPanel = new JPanel(new BorderLayout());	//初始化浏览器面板
		browserPanel.setOpaque(false);	//设置为透明
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				browser = getWebBroswer();
				browserPanel.add(browser);
				add(browserPanel);	//将浏览器面板添加到窗体
				startOauth();	//开始授权
			}
		});
		if(!NativeInterface.isEventPumpRunning())	NativeInterface.runEventPump();
		log.debug("浏览器初始化成功");
	}
	
	/**
	 * 得到浏览器对象
	 * @return
	 */
	private JWebBrowser getWebBroswer(){
//		if(browser != null)	return browser;
		browser = new JWebBrowser(JWebBrowser.destroyOnFinalization());	//实例化浏览器
		//浏览器样式设置
		browser.setOpaque(false);	
		browser.setBarsVisible(false);
		browser.setMenuBarVisible(false);
		browser.setRequestFocusEnabled(false);
		browser.setDefaultPopupMenuRegistered(false);
		browser.setJavascriptEnabled(true);
		browser.addWebBrowserListener(getWebBrowserAdapter());
		return browser;
	}
	
	/**
	 * 调用系统默认浏览器打开指定链接
	 * @param url	需要打开的链接
	 */
	private void openURL(String url){
		log.debug("调用浏览器打开链接："+url);
		//判断当前系统是否支持Java AWT Desktop扩展
		if(Desktop.isDesktopSupported()){
			try {
				//创建一个URI实例
				URI uri = URI.create(url);
				//获取当前系统桌面扩展
				Desktop dp = Desktop.getDesktop();
				//判断系统桌面是否支持要执行的功能
				if(dp.isSupported(Desktop.Action.BROWSE)){
					//获取系统默认浏览器打开链接
					dp.browse(uri);   
				}
			} catch(java.lang.NullPointerException e){
				//此为uri为空时抛出异常
				e.printStackTrace();
			} catch (java.io.IOException e) {
				//此为无法获取系统默认浏览器
				e.printStackTrace();
			}            
		}else{
			log.debug("系统不支持Java AWT Desktop扩展");
			try {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 浏览器监听适配器
	 * @return
	 */
	private WebBrowserAdapter getWebBrowserAdapter(){
		return new WebBrowserAdapter() {
			/* (non-Javadoc)
			 * @see chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter#locationChanging(chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent)
			 */
			public void locationChanging(WebBrowserNavigationEvent e){
				String url = e.getNewResourceLocation();
				log.debug("locationChanging-URL:"+url);
				if(!url.startsWith(WeiboConfig.getValue("authorizeURL"))){
					if(url.startsWith(WeiboConfig.getValue("register")) || url.startsWith(WeiboConfig.getValue("weibo")) || url.startsWith(WeiboConfig.getValue("app"))){
						openURL(url);
						e.getWebBrowser().navigate(oauthURL);
					}else if(url.startsWith(WeiboConfig.getValue("logout"))){
						JWebBrowser.clearSessionCookies();
						e.getWebBrowser().navigate(oauthURL);
					}else{
						setMessage("授权失败、请稍候重试");
						openLoginFrame(1000);
					}
				}else{
					setMessage("请稍等");
					showBrowserPanel(false);	//隐藏浏览器
				}
			}
			
			/* (non-Javadoc)
			 * @see chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter#locationChanged(chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent)
			 */
			public void locationChanged(WebBrowserNavigationEvent e){
				String url = e.getWebBrowser().getResourceLocation();
				log.debug("locationChanged-URL:"+url);
				
				//布局页面
				e.getWebBrowser().executeJavascript("document.body.style.overflow='hidden'; document.body.style.padding='0px'; document.body.style.margin='0px';");
				//把页面所有超链接的target属性值设为_self
				e.getWebBrowser().executeJavascript("as=document.getElementsByTagName('a'); for(var i=0;i<as.length;i++){as[i].target='_self';} ");
				//去掉页面取消按钮
				e.getWebBrowser().executeJavascript("for(var i=0;i<as.length;i++){if(as[i].className == 'WB_btn_cancel')as[i].parentNode.removeChild(as[i]);} ");
				//去掉提醒消息
				e.getWebBrowser().executeJavascript("ps=document.getElementsByTagName('p'); for(var i=0;i<ps.length;i++){if(ps[i].className == 'oauth_tiptxt')ps[i].parentNode.removeChild(ps[i]);}");
				//把按钮放中间
				e.getWebBrowser().executeJavascript("for(var i=0;i<ps.length;i++){if(ps[i].className == 'oauth_formbtn'){ps[i].style.margin='0px auto';ps[i].style.padding='0px 0px 0px 12px';}}");
				
				if(url.equalsIgnoreCase(oauthURL)){
					//e.getWebBrowser().executeJavascript("as=document.getElementsByTagName('a'); var i=0; for(i=0;i<as.length;i++){ if(as[i].innerHTML=='注册') as[i].parentNode.removeChild(as[i]); as[i].href='#'; as[i].target='_self'; as[i].style.textDecoration='none'; as[i].style.cursor='default'; if(as[i].className == 'WB_btn_cancel'){as[i].parentNode.removeChild(as[i]);}} var ps = document.getElementsByTagName('p'); for(var i=0;i<ps.length;i++){if(ps[i].className == 'oauth_tiptxt'){ps[i].parentNode.removeChild(ps[i]);}else if(ps[i].className == 'oauth_formbtn'){ps[i].style.margin='0px auto';ps[i].style.padding='0px 0px 0px 12px';}}");
				}else if(url.equalsIgnoreCase(WeiboConfig.getValue("authorizeURL"))){
					//e.getWebBrowser().executeJavascript("as=document.getElementsByTagName('a'); var i=0; for(i=0;i<as.length;i++){ if(as[i].parentNode.className=='login_account') continue; as[i].href='#'; as[i].target='_self'; as[i].style.textDecoration='none'; as[i].style.cursor='default'; if(as[i].className == 'WB_btn_cancel'){as[i].parentNode.removeChild(as[i]);}} var ps = document.getElementsByTagName('p'); for(var i=0;i<ps.length;i++){if(ps[i].className == 'oauth_tiptxt'){ps[i].parentNode.removeChild(ps[i]);}else if(ps[i].className == 'oauth_formbtn'){ps[i].style.margin='0px auto';ps[i].style.padding='0px 0px 0px 15px';}}");
				}else if(url.startsWith(WeiboConfig.getValue("redirect_URI"))){
					oauthByCode(FrameUtil.getURLValue(url,"code"));
					return;
				}
				showBrowserPanel();	//显示浏览器面板
			}
			
		};
	}
	
	/**
	 * 开始授权
	 */
	public void startOauth(){
		startOauth("加载授权页",0);
	}
	
	/**
	 * 开始授权
	 */
	public void startOauth(String msg, final long time){
		setMessage(msg);	//设置窗体显示消息。
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(NativeInterface.isOpen()&&browser!=null){
							JWebBrowser.clearSessionCookies();	//清空缓存
							browser.navigate(oauthURL);	//开始授权页
						}
					}
				});
			}
		}).start();
	}
	
	/**
	 * 通过code去获得授权
	 * @param code	获权所需code
	 */
	private void oauthByCode(String code){
		if(code == null){
			openLoginFrame();
			return;
		}
		StringBuffer msg = new StringBuffer("<html><center>授权失败<br />");
		User user = null;
		try {
			user = userDao.getUserByCode(code);
		} catch (WeiboException e) {
			log.debug("WeiboException:"+e.getStatusCode());
			if(401 == e.getStatusCode() || 403 == e.getStatusCode()){
				msg.append("【测试版、非测试用户不能登陆】");
			}else{
				msg.append("【连接服务器失败、检测网络环境或稍后重试】");
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(user != null){
			setMessage("授权成功");
			openMainFrame(user,1000);
		}else{
			startOauth(msg.append("</center></html>").toString(),1500);	//重新开始授权
		}
	}
	
	/**
	 * 得到返回按钮
	 * @return
	 */
	private JButton getCancelButton(){
		if(cancelButton != null)	return cancelButton;
		cancelButton = new SelfButton("返回登陆",Theme.getLoginCancelImage(),JButton.CENTER,JButton.BOTTOM);
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openLoginFrame();
			}
			
		});
		return cancelButton;
	}
	
	/**
	 * 得到反馈按钮
	 * @return
	 */
	private JButton getTicklingButton(){
		if(ticklingButton != null)	return ticklingButton;
		ticklingButton = new SelfButton("提交反馈",Theme.getLoginTicklingImage(),JButton.CENTER,JButton.BOTTOM);
		return ticklingButton;
	}
	
	/**
	 * 通知主窗体切换到登陆面板
	 */
	private void openLoginFrame(){
		openLoginFrame(0);
	}
	
	/**
	 * 通知主窗体切换到登陆面板
	 */
	private void openLoginFrame(final long time){
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setVisible(false);
				((MainFrame)FrameUtil.getCurrentFrame()).event.call(RequestInfo.intoLoginPanel);
			}
		}).start();
	}

	/**
	 * 通知主窗体切换到主面板
	 * @param user
	 * @param time
	 */
	private void openMainFrame(final User user, final long time){
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setVisible(false);
				((MainFrame)FrameUtil.getCurrentFrame()).event.call(RequestInfo.intoMainPanel,user);
			}
		}).start();
	}
	
	/**
	 * 显示浏览器面板
	 */
	private void showBrowserPanel(){
		showBrowserPanel(true);
	}
	
	/**
	 * 显示浏览器面板
	 * @param state	为true则显示浏览器面板。为false反之
	 */
	private void showBrowserPanel(final boolean state){
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(browserPanel != null){	//浏览器面板和标签只能显示一个、一个隐藏一个显示
					browserPanel.setVisible(state);
					if(msgLabel != null)
						msgLabel.setVisible(!state);
				}
			}
		}).start();
	}
	
	/**
	 * 设定窗口显示的消息
	 * @param msg	需要显示的消息
	 */
	private void setMessage(String msg){
		setMessage(Theme.getOauthLoadingImage(),msg);
	}
	
	/**
	 * 设定窗口显示的消息
	 * @param img	消息所需图标
	 * @param msg	消息
	 */
	private void setMessage(ImageIcon img, String msg){
		if(img == null)	return;
		if(msgLabel == null){
			msgLabel = new JLabel(msg,img,JLabel.CENTER);	//实例化消息标签
			//设置布局
			msgLabel.setHorizontalTextPosition(JLabel.CENTER);
			msgLabel.setVerticalTextPosition(JLabel.BOTTOM);
			add(msgLabel);	//将标签加入到面板
			validate();	//刷新面板
		}else{
			if(!img.equals(msgLabel.getIcon()))
				msgLabel.setIcon(img);	//如果图标变更、则重设
			msgLabel.setText(msg);
			msgLabel.setVisible(true);
		}
		if(browserPanel != null)
			browserPanel.setVisible(false);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 * 1.使用dispose()方法关闭窗体会释放该窗体的占用的部分资源，不过呢不是全部的，如上面说的，只是屏幕资源。
	 * 2.使用dispose()方法关闭的窗体可以使用pack 或 show 方法恢复，并且可以恢复到dispose前的状态（呵呵~感觉好神奇的，一开始都不相信）
	 * 使用dispose()和setVisible()方法，在表象上没有任何区别，它们的实质区别在于setVisible方法仅仅隐藏窗体，而dispose方法是关闭窗体，并释放一部分资源。
	 */
	@Override
	public void dispose(){
		super.dispose();
		if(NativeInterface.isOpen() || NativeInterface.isEventPumpRunning())	NativeInterface.close();
	}

}
