package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.kerwin.weibo.dao.UserDao;
import org.kerwin.weibo.dao.bin.impl.UserDaoBinImpl;
import org.kerwin.weibo.mode.User;
import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

public class LoginPanel extends SelfPanel {

	private static final long serialVersionUID = 1L;
	private JPanel firstPanel;	//首页
	private JButton addUserButton;	//首页添加新用户的按钮
	private UserDao userDao;	//用户模型操作类
	private JPanel mainPanel;	//主页面
	private List<User> users;	//用户集合
	private ActionListener openOauthFrame;	//监听器
	private JButton enterButton;	//主页面登陆按钮
	private JButton newButton;	//主页面新建用户按钮
	private JButton delButton;	//主页面删除用户按钮
	private JLabel userLabel;	//主页面选中用户标签
	private JPopupMenu usersPopup;	//用户菜单
	private JPanel usersPanel;	//用户标签集合面板
	private User currentUser;	//当前用户

	public LoginPanel(){
		setLayout(new BorderLayout());	//设置面板布局管理器
		setOpaque(false);
		
		userDao = new UserDaoBinImpl();	//初始化用户模型操作类
		users = userDao.selectAllUser();	//查询所有用户信息
		
		openOauthFrame = new ActionListener(){	//初始化动作监听器。

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openOauthFrame();	//打开授权窗口
			}
			
		};
		
		firstPanel = getFirstPanel();	//初始化首页面板
		mainPanel = getMainPanel();	//初始化主面板
		refreshPanel();	//刷新面板
	}
	
	/**
	 * 打开授权窗口
	 */
	private void openOauthFrame(){
		((MainFrame)FrameUtil.getCurrentFrame()).event.call(RequestInfo.intoOauthPanel);
	}
	
	/**
	 * 获得首页面板
	 * @return 返回首页面板
	 */
	private JPanel getFirstPanel(){
		JPanel jp = new JPanel(new BorderLayout());	//实例化带指定布局管理器的面板
		jp.setOpaque(false);	//设置面板透明
		addUserButton = new SelfButton("添加用户",Theme.getLoginAddImage(),JButton.CENTER,JButton.BOTTOM);
		addUserButton.setToolTipText("添加用户");
		addUserButton.setForeground(Color.WHITE);
		addUserButton.addActionListener(openOauthFrame);
		jp.add(addUserButton);	//将按钮加入面板
		return jp;
	}
	
	/**
	 * 获得主面板（按钮面板：西方是添加新用户按钮。东方是删除用户按钮。中央是登陆按钮。主面板：按钮面板放在南方。中央是显示当前选中用户的标签。提供一个菜单面板显示所有可选用户）
	 * @return
	 */
	private JPanel getMainPanel(){
		JPanel jp = new JPanel(new BorderLayout());
		JPanel btnPanel = new JPanel(new BorderLayout());
		
		enterButton = new SelfButton(Theme.getLoginEnterImage());
		enterButton.setToolTipText("登陆");
		enterButton.addActionListener(getEnterBtnActionListener());
		btnPanel.add(enterButton);
		
		newButton = new SelfButton(Theme.getLoginNewImage());
		newButton.setToolTipText("新增用户");
		newButton.addActionListener(openOauthFrame);
		btnPanel.add(newButton,BorderLayout.WEST);
		
		delButton = new SelfButton(Theme.getLoginDelImage());
		delButton.setToolTipText("删除用户");
		delButton.addActionListener(getDelBtnActionListener());
		btnPanel.add(delButton,BorderLayout.EAST);
		
		userLabel = new JLabel("",JLabel.CENTER);
		userLabel.setForeground(Color.WHITE);
		userLabel.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		userLabel.setBorder(BorderFactory.createLineBorder(Theme.getTransparentColor(getForeground(),50)));
		
		usersPopup = new JPopupMenu();
		usersPopup.setLayout(new BorderLayout());
//		usersPopup.setOpaque(false);
//		usersPopup.setBorder(BorderFactory.createEmptyBorder());
		usersPopup.setBorder(BorderFactory.createLineBorder(Theme.getTransparentColor(50)));
		usersPanel = new JPanel(new GridLayout(users.size(), 1, 0, 5));
//		usersPanel.setOpaque(false);
		JScrollPane scrollPanel = new JScrollPane(usersPanel);
//		scrollPanel.setOpaque(false);
//		scrollPanel.getViewport().setOpaque(false); 
		scrollPanel.setBorder(BorderFactory.createEmptyBorder());
		
//		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		usersPopup.add(scrollPanel);
		FrameUtil.setAllOpaque(usersPopup, false);
		userLabel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				if(usersPopup.isShowing())	return;
				int maxHeight = (int) (e.getComponent().getParent().getParent().getHeight()-e.getComponent().getParent().getSize().getHeight())-10;
				int height = users.size()*e.getComponent().getHeight()+(users.size() > 0 ? users.size() : 0)*5;
				if(height >= maxHeight)	height=maxHeight;
				usersPopup.setPopupSize(e.getComponent().getWidth(),height);
				usersPopup.show(e.getComponent(), 0, -height);
			}
		});
		btnPanel.add(userLabel,BorderLayout.NORTH);
		
		jp.add(btnPanel,BorderLayout.SOUTH);
		
		return jp;
	}
	
	/**
	 * 刷新用户列表面板
	 */
	public void refreshPanel(){
		users = userDao.selectAllUser();	//得到用户集合
		if(!(users == null || users.size() == 0)){
			usersPanel.removeAll();
			usersPanel.setLayout(new GridLayout(users.size(), 1, 0, 2));
			for(int i = 0,j = users.size(); i < j; i++){
				JLabel jl = new JLabel(users.get(i).getName(),JLabel.CENTER);
				jl.setName(users.get(i).getUid().toString());
				jl.setCursor(new Cursor(Cursor.HAND_CURSOR));
				jl.addMouseListener(getPopupListItemMouseListener());
				usersPanel.add(jl);
			}
			setCurrentUser(users.get(users.size()-1));
		}
		changePanel();
	}
	
	/**
	 * 切换面板
	 */
	private void changePanel(){
		//repaint()方法是重绘，而validate()是重载，一般来说，从一个容器中删除某个组件需要调用repaint()，而把某个组件添加到某一容器中，则需调用validate()。
		removeAll();
		repaint();
		if(currentUser == null)	//如果当前无用户则显示首页。否则显示主面板
			add(firstPanel);
		else
			add(mainPanel);
		repaint();
		validate();
		FrameUtil.setAllOpaque(this, false);
	}
	
	/**
	 * 得到用户列表面板的监听器
	 * @return
	 */
	private MouseListener getPopupListItemMouseListener(){
		return new MouseAdapter(){

			@Override
			public void mouseClicked(final MouseEvent e) {
				JLabel jl = (JLabel)e.getSource();
				new Thread(new Runnable(){
					public void run(){
						for(int i = 0, j = users.size(); i < j; i++){
							if(users.get(i).getUid().toString().equals(e.getComponent().getName())){
								setCurrentUser(users.get(i));
								break;
							}
						}
					}
				}).start();
				usersPopup.setVisible(false);
				jl.setForeground(getForeground());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel jl = (JLabel)e.getSource();
				jl.setForeground(userLabel.getForeground());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JLabel jl = (JLabel)e.getSource();
				jl.setForeground(getForeground());
			}
			
		};
	}
	
	/**
	 * 得到登陆按钮的监听对象
	 * @return
	 */
	private ActionListener getEnterBtnActionListener(){
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				userDao.updateUser(currentUser);
				((MainFrame)FrameUtil.getCurrentFrame()).event.call(RequestInfo.intoMainPanel,currentUser);
			}
			
		};
	}
	
	private ActionListener getDelBtnActionListener(){
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				userDao.deleteUser(currentUser);
				currentUser = null;
				refreshPanel();
			}
			
		};
	}
	
	/**
	 * 设置当前用户
	 * @param user
	 */
	private void setCurrentUser(User user){
		if(user == null)	return;
		currentUser = user;
		userLabel.setText(currentUser.getName());
		validate();
	}
	
}
