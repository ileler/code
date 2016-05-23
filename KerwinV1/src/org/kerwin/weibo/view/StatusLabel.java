package org.kerwin.weibo.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.kerwin.weibo.service.Theme;

public class StatusLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public StatusLabel(){
		super(Theme.getStatusImage(),JLabel.CENTER);
		setToolTipText("发微博");
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getOnStatusImage());
			}
			
			@Override
			public void mouseExited(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getStatusImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() != 1)
					return;
			}
			
		});
	}
	
}
