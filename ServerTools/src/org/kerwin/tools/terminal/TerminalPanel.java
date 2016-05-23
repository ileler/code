package org.kerwin.tools.terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.kerwin.tools.bean.CMDButtonBean;

public class TerminalPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JLabel addLabel;
	private JPanel btnsPanel;
	private CMDPanel cmdPanel;
	private JSplitPane splitPanel;
	
	public TerminalPanel(){
		setLayout(new BorderLayout());
		addLabel = new JLabel("Add CMD");
		addLabel.setHorizontalAlignment(JLabel.CENTER);
		addLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addLabel.setPreferredSize(new Dimension(getWidth(),25));
		addLabel.setFont(new Font(getFont().getName(),getFont().getStyle(),14));
		addLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		addLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setForeground(Color.BLACK);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setForeground(Color.BLUE);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				new CMDBtnDialog().setVisible(true);
				initBtnsPanel();
			}
		});
		add(addLabel,BorderLayout.NORTH);
		
		btnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		btnsPanel.setFont(new Font(getFont().getName(),getFont().getStyle(),16));
		initBtnsPanel();
		
		cmdPanel = new CMDPanel("[kerwin@localhost ~]# ");
		
		splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, btnsPanel, cmdPanel);
		splitPanel.setBorder(BorderFactory.createEmptyBorder());
		splitPanel.setDividerSize(3);
		add(splitPanel);
	}
	
	private void initBtnsPanel(){
		btnsPanel.removeAll();
		List<CMDButtonBean> cmdbbs = CMDButtonBean.selectAll();
		if(cmdbbs!=null&&cmdbbs.size()>0){
			for(final CMDButtonBean cmdbb : cmdbbs){
				final JLabel tmpLabel = new JLabel(" "+cmdbb.getBtnName()+" ");
				tmpLabel.setToolTipText(cmdbb.getBtnCommand());
				tmpLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				tmpLabel.setName(cmdbb.getId());
				tmpLabel.setFont(btnsPanel.getFont());
				tmpLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseExited(MouseEvent e) {
						tmpLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							if(e.getButton() == MouseEvent.BUTTON3){
								if(e.getClickCount() == 2){
									CMDButtonBean.deleteById(e.getComponent().getName());
									initBtnsPanel();
								}
							}else{
								cmdPanel.exec(cmdbb.getBtnCommand());
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
				btnsPanel.add(tmpLabel);
			}
			btnsPanel.setToolTipText("Double click btn to delete.");
		}else{
			JLabel tmpLabel = new JLabel("cmdBtns is null");
			tmpLabel.setFont(btnsPanel.getFont());
			btnsPanel.add(tmpLabel);
			btnsPanel.setToolTipText(null);
		}
		if(splitPanel != null)	splitPanel.setDividerLocation(btnsPanel.getHeight());
		validate();
	}
	
}
