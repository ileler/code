package org.kerwin.tools.terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.kerwin.tools.bean.CMDButtonBean;

public class CMDBtnDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private boolean isShow;
	private JLabel infoLabel;
	private JPanel btnPanel;
	private JLabel enterLabel;
	private JLabel cancelLabel;
	private JTextArea cmdArea;
	private JTextField cmdName;
	private JScrollPane scrollPanel;
	
	public CMDBtnDialog(){
		setTitle("Add CmdBtn");
		setResizable(false);
		setSize(300, 215);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		
		DocumentListener dl = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(isShow)	hideMessage();		
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(isShow)	hideMessage();		
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(isShow)	hideMessage();		
			}
		};
		
		cmdName = new JTextField();
		cmdName.setPreferredSize(new Dimension(getWidth()-6,45));
		cmdName.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Name"));
		cmdName.getDocument().addDocumentListener(dl);
		
		cmdArea = new JTextArea();
		cmdArea.setLineWrap(true);        			//激活自动换行功能 
		cmdArea.setWrapStyleWord(true);            	//激活断行不断字功能
		cmdArea.getDocument().addDocumentListener(dl);
		scrollPanel = new JScrollPane(cmdArea);
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);
		scrollPanel.setPreferredSize(new Dimension(getWidth()-6,75));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(10);
		scrollPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Command"));
		scrollPanel.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	//设置水平滚动条从不显示
		
		enterLabel = getOperLabel("Enter");
		enterLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name,command;
				if((name = cmdName.getText()) == null || name.isEmpty()){
					showMessage("Name is null");
				}else if((command = cmdArea.getText()) == null || command.isEmpty()){
					showMessage("Command is null");
				}else{
					try {
						new CMDButtonBean(name,command).insert();
						CMDBtnDialog.this.setVisible(false);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		cancelLabel = getOperLabel("Cancel");
		cancelLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CMDBtnDialog.this.setVisible(false);
			}
		});
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING,0,10));
		
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.add(cmdName);
		add(tmpPanel);
		
		tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.add(scrollPanel);
		add(tmpPanel);
		
		btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
		btnPanel.setPreferredSize(new Dimension(getWidth(),30));
		btnPanel.add(cancelLabel);
		btnPanel.add(enterLabel);
		add(btnPanel);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setPreferredSize(new Dimension(getWidth()-6,30));
		infoLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.RED)));
		add(infoLabel);
		infoLabel.setVisible(false);
	}
	
	private void hideMessage(){
		isShow = false;
		infoLabel.setText("");
		infoLabel.setVisible(false);
		btnPanel.setVisible(true);
	}
	
	private void showMessage(String message){
		isShow = true;
		infoLabel.setText(message);
		infoLabel.setVisible(true);
		btnPanel.setVisible(false);
	}
	
	private JLabel getOperLabel(String labeltext){
		JLabel label = new JLabel(labeltext);
		label.setPreferredSize(new Dimension(60,25));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				((JComponent) e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				((JComponent) e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
		});
		return label;
	}
	
}
