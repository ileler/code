package org.kerwin.weibo.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.kerwin.weibo.service.Theme;

public class RefreshLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public RefreshLabel(){
		super(Theme.getRefreshImage(),JLabel.CENTER);
		setToolTipText("刷新");
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getOnRefreshImage());
			}
			
			@Override
			public void mouseExited(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getRefreshImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() != 1)
					return;
			}
			
		});
	}
}
