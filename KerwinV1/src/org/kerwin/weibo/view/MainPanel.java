package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;
import org.kerwin.weibo.dao.bin.impl.UserDaoBinImpl;
import org.kerwin.weibo.mode.User;
import org.kerwin.weibo.mode.UserWithToken;
import org.kerwin.weibo.service.Session;
import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

/**
 * @author O0O
 * 程序主面板
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MainPanel.class);
	private JSplitPane contentPane;		//分割面板	
	private JPanel topPane;		//上部分面板
	private TabPane tabPane;	//选项卡面板
	private JPanel bottomPane;	//下部分面板
	private JPanel infoPane;		//用户信息面板
	private JPanel toolPane;		//工具面板
	private JPanel btoolPane;
	private RefreshLabel refreshLabel;		//刷新标签
	private SearchLabel searchLabel;			//搜索标签
	private StatusLabel statusLabel;			//发微薄标签
	private JLabel msgLabel;
	private HomeTimelinePane homeTimelinePane;
	private UserTimelinePane userTimelinePane;
	private MentionTimelinePane mentionTimelinePane;
	
	public MainPanel(final User user){
		log.debug("正在加载用户信息");
		setLayout(new BorderLayout());		//设置MainPane的布局
		msgLabel = new JLabel("正在加载用户信息",Theme.getLoadingImage(),JLabel.CENTER);
		msgLabel.setHorizontalTextPosition(JLabel.CENTER);
		msgLabel.setVerticalTextPosition(JLabel.BOTTOM);
		add(msgLabel);
		new Thread(new Runnable(){
			public void run(){
				UserWithToken uwt = null;
				try {
					//获取用户信息
					uwt = new UserWithToken(user.getAccessToken(),new UserDaoBinImpl().getWeiboUserByUser(user));
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (WeiboException e1) {
					e1.printStackTrace();
				}
				if(user == null || uwt == null || uwt.getUser() == null){
					msgLabel.setText("获取用户信息失败、请重试");
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//通知主窗体切换至登陆面板
					((MainFrame)FrameUtil.getCurrentFrame()).event.call(RequestInfo.intoLoginPanel);
					return;
				}
				//设置本次登陆用户到Session
				Session.set("currentUser", uwt);
				initMainPane();
				new Thread(new Runnable(){
					public void run(){
						initTopPane();
						new Thread(new Runnable(){
							public void run(){
								initBottomPane();
							}
						}).start();
					}
				}).start();
			}
		}).start();
		FrameUtil.setAllOpaque(this, false);
	}
	
	/**
	 * 初始化主面板
	 */
	private void initMainPane(){
		//实例化顶部面板
		topPane = new SelfPanel(new BorderLayout());
		//实例化底部面板
		tabPane = new TabPane();
		bottomPane = new SelfPanel(new BorderLayout());
		bottomPane.add(new JPanel(),BorderLayout.NORTH);
		bottomPane.add(tabPane);
		bottomPane.add(getBtoolPane(),BorderLayout.SOUTH);
		//顶部面板。底部面板实例化分割面板
		contentPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,topPane,bottomPane);
		contentPane.setDividerSize(0);		//设置分割线大小
		contentPane.setDividerLocation(165);	//设置分割线定位
		//contentPane.setOneTouchExpandable(true);
		contentPane.setBorder(BorderFactory.createEmptyBorder());	//清空分割面板默认的边框
		
		add(contentPane,BorderLayout.CENTER);		//将分割面板添加到当前面板
		remove(msgLabel);
		validate();
		FrameUtil.setAllOpaque(this, false);
	}
	
	/**
	 * 初始化顶部面板
	 */
	private void initTopPane(){
		//将工具面板添加到顶部面板
		topPane.add(getToolPane(),BorderLayout.SOUTH);
		//实例化信息面板
		infoPane = new InfoPane();
		
		//将信息面板添加到顶部面板
		topPane.add(infoPane);
		validate();
		FrameUtil.setAllOpaque(this, false);
	}

	/**
	 * 初始化底部面板
	 */
	private void initBottomPane(){
		homeTimelinePane = new HomeTimelinePane();
		userTimelinePane = new UserTimelinePane();
		mentionTimelinePane = new MentionTimelinePane();
		
		tabPane.addTab("首页",homeTimelinePane);
		tabPane.addTab("评论");
		tabPane.addTab("@我",mentionTimelinePane);
		tabPane.addTab("收藏");
		tabPane.addTab("关注");
		tabPane.addTab("粉丝");
		tabPane.addTab("微博",userTimelinePane);
		
		homeTimelinePane.addList();
		userTimelinePane.addList();
		mentionTimelinePane.addList();
		validate();
		FrameUtil.setAllOpaque(this, false);
	}
	
	/**
	 * @return		返回工具面板
	 */
	private JPanel getToolPane(){
		if(toolPane != null)
			return toolPane;
		//实例化工具面板
		toolPane = new SelfPanel();
		toolPane.setCursor(new Cursor(Cursor.HAND_CURSOR));	//设置工具面板的数遍样式
		
		//将发微薄标签添加到工具面板
		toolPane.add(getStatusLabel());
		
		//将刷新标签添加到工具面板
		toolPane.add(getRefreshLabel());
		
		//将搜索标签添加到工具面板
		toolPane.add(getSearchLabel());
		
		//设置工具面板的样式
		toolPane.setLayout(new GridLayout(1,toolPane.getComponentCount()));
		
		//返回工具面板
		return toolPane;
	}
	
	private JPanel getBtoolPane(){
		if(btoolPane != null)	return btoolPane;
		btoolPane = new SelfPanel();
		return btoolPane;
	}
	
	/**
	 * @return 返回发状态标签
	 */
	private StatusLabel getStatusLabel(){
		if(statusLabel == null){
			statusLabel = new StatusLabel();
		}
		return statusLabel;
	}
	
	/**
	 * @return 返回刷新标签
	 */
	private RefreshLabel getRefreshLabel(){
		if(refreshLabel == null){
			refreshLabel = new RefreshLabel();
			refreshLabel.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e){
					super.mouseClicked(e);
					((TimelinePane)tabPane.getCurrentBody()).refresh();
				}
				
			});
		}
		return refreshLabel;
	}
	
	/**
	 * @return 返回搜索标签
	 */
	private SearchLabel getSearchLabel(){
		if(searchLabel == null){
			searchLabel = new SearchLabel();
		}
		return searchLabel;
	}

}
