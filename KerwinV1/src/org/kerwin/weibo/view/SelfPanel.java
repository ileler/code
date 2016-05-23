package org.kerwin.weibo.view;

import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SelfPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Point loc;    
	private Point tmp;    
	private boolean isDragged; 
	
	public SelfPanel(){
		super();
		initial();
	}
	
	public SelfPanel(boolean isDoubleBuffer){
		super(isDoubleBuffer);
		initial();
	}
	
	public SelfPanel(LayoutManager layout){
		super(layout);
		initial();
	}
	
	public SelfPanel(LayoutManager layout, boolean isDoubleBuffer){
		super(layout,isDoubleBuffer);
		initial();
	}
	
	private void initial(){
		setDragable();
	}
	
	private void setDragable() {        
		addMouseListener(new MouseAdapter() {            
			public void mouseReleased(MouseEvent e) {               
				isDragged = false;               
			}            
			public void mousePressed(MouseEvent e) {               
				tmp = new Point(e.getX(), e.getY());               
				isDragged = true;               
			}        
		});
        addMouseMotionListener(new MouseMotionAdapter() {            
			public void mouseDragged(MouseEvent e) {
				if(isDragged) {                   
					loc = new Point(SwingUtilities.getWindowAncestor(SelfPanel.this).getLocation().x + e.getX() - tmp.x,SwingUtilities.getWindowAncestor(SelfPanel.this).getLocation().y + e.getY() - tmp.y);
					SwingUtilities.getWindowAncestor(SelfPanel.this).setLocation(loc);
				} 
			} 
		});
	}

}
