package org.kerwin.weibo.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.kerwin.weibo.service.Theme;

public class SearchLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public SearchLabel(){
		super(Theme.getSearchImage(),JLabel.CENTER);
		setToolTipText("搜索");
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getOnSearchImage());
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e){
				((JLabel)e.getSource()).setIcon(Theme.getSearchImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() != 1)
					return;
			}
			
		});
	}
}
