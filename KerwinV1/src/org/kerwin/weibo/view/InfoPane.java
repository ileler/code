package org.kerwin.weibo.view;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JLabel;

import org.kerwin.weibo.mode.UserWithToken;
import org.kerwin.weibo.service.Session;
import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

import weibo4j.model.User;

public class InfoPane extends SelfPanel {

	private static final long serialVersionUID = 1L;
	private JLabel profileLabel;
	private JLabel infoLabel;
	private UserWithToken user;
	
	public InfoPane(){
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		user = (UserWithToken) Session.get("currentUser");
		if(user == null)	return;
		profileLabel = new JLabel(Theme.getAvatarMImage(),JLabel.CENTER);
		profileLabel.setHorizontalTextPosition(JLabel.CENTER);
		profileLabel.setVerticalTextPosition(JLabel.BOTTOM);
		profileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		profileLabel.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		profileLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				infoLabel.setVisible(false);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				infoLabel.setVisible(true);
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		add(profileLabel);
		add(Box.createHorizontalStrut((int) FrameUtil.getScreenWidth()));
		infoLabel = new JLabel();
		infoLabel.setOpaque(false);
		infoLabel.setVisible(false);
		add(infoLabel);
		new Thread(new Runnable(){
			public void run(){
				User u = user.getUser();
				profileLabel.setIcon(Theme.getAvatarMImage(u.getAvatarLarge()));
				profileLabel.setText(user.getUser().getName());
				infoLabel.setText("<html><center>"+("m".equals(u.getGender()) ? "男" : "女")+" | "+u.getLocation()+" | 粉丝："+u.getFollowersCount()+" | 关注："+u.getFriendsCount()+" | 微博："+u.getStatusesCount()+"</center></html>");
			}
		}).start();
		
		setOpaque(false);
	}
	
}
