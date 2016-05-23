package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

import weibo4j.model.Source;
import weibo4j.model.Status;
import weibo4j.model.User;

public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Status status;
	private JPanel firstLeftPane;	//第一层左边面板
	private JPanel firstCenterPane;	//第一层中间面板
	private JPanel secondPane;	//第二层面板
	private JPanel secondTopPane;	//第二层顶部面板
	private JPanel secondCenterPane;	//第二层中间面板
	private JPanel secondBottomPane;	//第二层底部面板
	private JPanel secondBottomOperPane;	//第二层底部操作面板
	private JLabel nameLabel;	//昵称标签
	private JLabel dateLabel;	//日期标签
	private JLabel sourceLabel;	//源标签
	private JPanel contentPanel;	//内容父面板
	private JTextPane contentPane;	//内容面板
	private JLabel imgLabel;	//头像标签
	private JLabel operLabel;	//操作标签
	private JPopupMenu operMenu;	//操作菜单
	private JLabel praiseLabel;	//赞标签
	private JLabel commentLabel;	//评论标签
	private JLabel retransmissionLabel;	//转发标签
	private int nameLength;
	
	/**
	 * 构造函数、
	 * @param status
	 */
	public StatusPanel(Status status) throws Exception{
		this(status,false);
	}
	
	/**
	 * 构造函数
	 * @param status 
	 * @param isRt 是否是放在被被转发的
	 */
	public StatusPanel(Status status, boolean isRt) throws Exception{
		this.status = status;
		if(status == null)	throw new NullPointerException("status is null");
		nameLength = isRt ? 12 : 15;
		setLayout(new BorderLayout());
		initial();
		initTopPanel();
		initSecondCenterPane();
		if(isRt){
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0), BorderFactory.createLineBorder(Theme.getTransparentColor(40))));
			initRtBottomPanel();
		}else{
			imgLabel = new JLabel(Theme.getAvatarSImage(),JLabel.LEFT);
			new Thread(new Runnable(){
				public void run(){
					ImageIcon ii = null;
					do{
						ii = Theme.getAvatarSImage(StatusPanel.this.status.getUser().getProfileImageURL());
					}while(ii!=null&&(ii.getIconWidth() < imgLabel.getIcon().getIconWidth() || ii.getIconHeight() < imgLabel.getIcon().getIconHeight()));
					imgLabel.setIcon(ii);
					validate();
				}
			}).start();
			firstLeftPane = new JPanel(new FlowLayout());
			firstLeftPane.add(imgLabel);
			add(firstLeftPane,BorderLayout.WEST);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.getTransparentColor(80)), BorderFactory.createEmptyBorder(0, 0, 5, 0)));
			Status rt = status.getRetweetedStatus();
			if(rt!=null){
				secondCenterPane.add(new StatusPanel(rt,true),BorderLayout.SOUTH);
			}
			initBottomPanel();
		}
		FrameUtil.setAllOpaque(this,false);
	}
	
	/**
	 * 初始化
	 */
	private void initial(){
		secondPane = new JPanel(new BorderLayout());
		firstCenterPane = new JPanel(new BorderLayout());
		firstCenterPane.add(secondPane);
		add(firstCenterPane);

		secondTopPane = new JPanel(new BorderLayout());
		secondPane.add(secondTopPane,BorderLayout.NORTH);
		
		secondCenterPane = new JPanel(new BorderLayout());
		secondPane.add(secondCenterPane);
		
		secondBottomOperPane = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
		secondBottomPane = new JPanel(new BorderLayout());
		secondBottomPane.add(secondBottomOperPane);
		secondPane.add(secondBottomPane,BorderLayout.SOUTH);
		
	}
	
	private void initTopPanel(){
		secondTopPane.add(getDateLabel(),BorderLayout.EAST);
		secondTopPane.add(getNameLabel(),BorderLayout.WEST);
	}
	
	private void initBottomPanel(){
		secondBottomOperPane.add(getPraiseLabel());
		secondBottomOperPane.add(getOperLabel());
		secondBottomPane.add(getSourceLabel(),BorderLayout.WEST);
	}
	
	private void initRtBottomPanel(){
		secondBottomOperPane.add(getCommentLabel());
		secondBottomOperPane.add(getRetransmissionLabel());
	}
	
	/**
	 * @return 返回主面板
	 */
	private void initSecondCenterPane(){
		contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPane = new JTextPane();
		contentPane.setOpaque(false);
		contentPane.setEditable(false);
		GroupLayout gl = new GroupLayout(contentPanel);
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(contentPane,GroupLayout.DEFAULT_SIZE,0,Short.MAX_VALUE));
		gl.setVerticalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(contentPane));
		contentPanel.setLayout(gl);
		contentPane.setText(status.getText());
		secondCenterPane.add(contentPanel);
	}
	
	/**
	 * @return 返回昵称标签
	 */
	private JLabel getNameLabel(){
		nameLabel = new JLabel("姓名未知");
		User user = null;
		if(status == null || (user = status.getUser()) == null)
			return nameLabel;
		String name = user.getName();
		try {
			String tmp = new String(name.getBytes("GBK"),"ISO8859_1");
			if(tmp.length() > nameLength){
				name = Theme.bSubstring(name,nameLength)+"...";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		nameLabel.setText(name);
		nameLabel.setToolTipText(status.getUser().getName());
		nameLabel.setForeground(Color.RED);
		nameLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event){
				
			}
		});
		return nameLabel;
	}
	
	/**
	 * @return 返回时间标签
	 */
	private JLabel getDateLabel(){
		dateLabel = new JLabel("时间未知");
		Date date = null;
		if(status == null || (date = status.getCreatedAt()) == null)
			return dateLabel;
		Calendar cc = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(cc.get(Calendar.YEAR) == c.get(Calendar.YEAR) && cc.get(Calendar.MONTH) == c.get(Calendar.MONTH) && Calendar.getInstance().get(Calendar.DATE) == c.get(Calendar.DATE) && cc.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)){
			if(cc.get(Calendar.MINUTE)-c.get(Calendar.MINUTE) == 0)
				dateLabel.setText("刚刚");
			else
				dateLabel.setText((cc.get(Calendar.MINUTE)-c.get(Calendar.MINUTE))+"分钟前");
		}else if(cc.get(Calendar.YEAR) == c.get(Calendar.YEAR) && cc.get(Calendar.MONTH) == c.get(Calendar.MONTH) && cc.get(Calendar.DATE) < c.get(Calendar.DATE)+3){
			String text = "";
			if(cc.get(Calendar.DATE) == c.get(Calendar.DATE))
				text = "今天 ";
			else if(cc.get(Calendar.DATE) == c.get(Calendar.DATE)+1)
				text = "昨天 ";
			else if(cc.get(Calendar.DATE) == c.get(Calendar.DATE)+2)
				text = "前天 ";
			text+=c.get(Calendar.HOUR_OF_DAY)+":"+(c.get(Calendar.MINUTE) < 10 ? "0"+c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE));
			dateLabel.setText(text);
		}else
			dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(status.getCreatedAt()));
		return dateLabel;
	}
	
	/**
	 * @return 返回“来自”标签
	 */
	private JPanel getSourceLabel(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.setOpaque(false);
		jp.add(new JLabel("来自"),BorderLayout.WEST);
		sourceLabel = new JLabel("未知");
		Source source = null;
		if(status == null || (source = status.getSource()) == null)
			return jp;
		sourceLabel.setText(source.getName());
		sourceLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event){
				
			}
		});
		jp.add(sourceLabel);
		return jp;
	}
	
	/**
	 * @return 返回操作标签
	 */
	private JLabel getOperLabel(){
		operLabel = new JLabel(Theme.getMoreImage());
		operLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		operLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent event){
				operLabel.setIcon(Theme.getOnMoreImage());
				operMenu.show(event.getComponent(),event.getX(), (int)operLabel.getBounds().getY()+operLabel.getHeight());
			}
			@Override
			public void mouseClicked(MouseEvent event){
				if(event.getButton() == MouseEvent.BUTTON3)
					return;
				mouseEntered(event);
			}
		});
		operLabel.setComponentPopupMenu(getOperMenu());
		return operLabel;
	}
	
	/**
	 * @return 返回信息操作菜单（评论、转发..）
	 */
	private JPopupMenu getOperMenu(){
		operMenu = new JPopupMenu();
		operMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				operLabel.setIcon(Theme.getMoreImage());
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				
			}
		});
		new Thread(new Runnable(){
			public void run(){
				operMenu.add(getRetransmissionLabel());
				operMenu.addSeparator();
				operMenu.add(getCommentLabel());
			}
		}).start();
		
		return operMenu;
	}
	
	/**
	 * @return 返回转发标签
	 */
	private JLabel getRetransmissionLabel(){
		retransmissionLabel = new JLabel("转发("+status.getRepostsCount()+")",JLabel.CENTER);
		retransmissionLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event){
				
			}
		});
		return retransmissionLabel;
	}
	
	/**
	 * @return 返回评论标签
	 */
	private JLabel getCommentLabel(){
		commentLabel = new JLabel("评论("+status.getCommentsCount()+")",JLabel.CENTER);
		commentLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event){
				
			}
		});
		return commentLabel;
	}
	
	/**
	 * @return 返回“赞”标签
	 */
	private JLabel getPraiseLabel(){
		praiseLabel = new JLabel("赞",JLabel.CENTER);
		praiseLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event){
				
			}
		});
		return praiseLabel;
	}

//	
//	/**
//	 * 设置所有子组件透明
//	 * @param component	要设置的组件
//	 * @param opaque	是否透明
//	 */
//	private void setAllOpaque(JComponent component,boolean opaque){
//		if(!component.equals(this))
////			if(component instanceof JTextPane)
////				component.setBackground(Theme.getTransparentColor(15));
////			else
//				component.setOpaque(opaque);
//		Component[] cs = component.getComponents();
//		for(Component c : cs){
//			if(c instanceof JComponent){
//				setAllOpaque((JComponent)c,opaque);
//			}
//		}
//	}
	
	private abstract class SelfMouseAdapter extends MouseAdapter{
		
		private Color foreground;
		
		@Override
		public void mouseEntered(MouseEvent event){
			foreground = event.getComponent().getForeground();
			event.getComponent().setForeground(Color.MAGENTA);
			event.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		@Override
		public void mouseExited(MouseEvent event){
			event.getComponent().setForeground(foreground);
		}
		
	}

}
