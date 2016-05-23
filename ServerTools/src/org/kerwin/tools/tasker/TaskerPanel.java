package org.kerwin.tools.tasker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.kerwin.task.KerwinTaskScheduler;
import org.kerwin.task.KerwinTasker;
import org.kerwin.tools.bean.KerwinTaskerBean;

public class TaskerPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JPanel operPanel;
	private JLabel refLabel;
	private JLabel addLabel;
	private JLabel delLabel;
	private JLabel modLabel;
	private JTable taskerTable;

	public TaskerPanel(){
		setLayout(new BorderLayout());
		
		operPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		operPanel.setPreferredSize(new Dimension(getWidth(),25));
		operPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		add(operPanel,BorderLayout.NORTH);

		refLabel = getOperLabel("REF");
		refLabel.setToolTipText("Refresh KerwinTaskerTable");
		refLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				initialTaskerTable();
			}
		});
		operPanel.add(refLabel);
		
		addLabel = getOperLabel("ADD");
		addLabel.setToolTipText("Add KerwinTasker");
		addLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new KerwinTaskerBeanDialog().setVisible(true);
				initialTaskerTable();
			}
		});
		operPanel.add(addLabel);
		
		modLabel = getOperLabel("MOD");
		modLabel.setToolTipText("Modify KerwinTasker");
		modLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel tableModel = (DefaultTableModel) taskerTable.getModel();
				int i = taskerTable.getSelectedRow();
				if(i == -1){
					JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(e.getComponent()),"Please select a row first!","Message",JOptionPane.CLOSED_OPTION);
				}else{
					KerwinTasker kt = KerwinTaskerBean.selectById(tableModel.getValueAt(i, 0).toString()).getKt();
					if(kt != null){
						try {
							new KerwinTaskerBeanDialog(kt).setVisible(true);
							initialTaskerTable();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		operPanel.add(modLabel);

		delLabel = getOperLabel("DEL");
		delLabel.setToolTipText("Delete KerwinTasker");
		delLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel tableModel = (DefaultTableModel) taskerTable.getModel();
				int i = taskerTable.getSelectedRow();
				if(i == -1){
					JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(e.getComponent()),"Please select a row first!","Message",JOptionPane.CLOSED_OPTION);
				}else{
					if(JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(e.getComponent()), "Sure to delete?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION)	return;
					KerwinTaskerBean.deleteById(tableModel.getValueAt(i, 0).toString());
					KerwinTaskScheduler.delKerwinTasker(tableModel.getValueAt(i, 0).toString());
					initialTaskerTable();
				}
			}
		});
		operPanel.add(delLabel);
		
		taskerTable = new JTable(new DefaultTableModel(new String[][]{},new String[]{"Id","Name","Status","Count","PrevTime","PrevResult","NextTime"}){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex){
				return false;
			}
		});
		taskerTable.setRowHeight(25);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		for(int i = taskerTable.getColumnCount() - 1; i >= 0; i--){
			taskerTable.getColumnModel().getColumn(i).setCellRenderer(dtcr);
			if(i == 0){
				taskerTable.getColumnModel().getColumn(i).setMinWidth(0);
				taskerTable.getColumnModel().getColumn(i).setMaxWidth(0);
				taskerTable.getColumnModel().getColumn(i).setPreferredWidth(0);
			}else if(i == 2 || i == 3 || i == 5){
				taskerTable.getColumnModel().getColumn(i).setMinWidth(80);
				taskerTable.getColumnModel().getColumn(i).setPreferredWidth(80);
			}else if(i == 4 || i == 6){
				taskerTable.getColumnModel().getColumn(i).setMinWidth(130);
				taskerTable.getColumnModel().getColumn(i).setPreferredWidth(130);
			}
			taskerTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer(){
				private static final long serialVersionUID = 1L;
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					JLabel label = (JLabel)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
					DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
					label.setToolTipText(KerwinTaskerBean.selectById(tableModel.getValueAt(row, 0).toString()).toString());
					return label;
				}
			});
		}
		initialTaskerTable();
		
		JScrollPane scrollPanel = new JScrollPane(taskerTable);
		scrollPanel.setOpaque(false);			//设置透明	
		scrollPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollPanel.getVerticalScrollBar().setOpaque(false);	//设置垂直滚动条透明//貌似毫无效果。原因未知
		scrollPanel.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	//设置水平滚动条从不显示
		scrollPanel.getViewport().setOpaque(false);	//滚动面板透明。与上面代码合起来才能使滚动面板透明
		add(scrollPanel);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				initialTaskerTable();
			}
		});
	}
	
	private void initialTaskerTable(){
		DefaultTableModel tableModel = (DefaultTableModel) taskerTable.getModel();
		tableModel.setRowCount(0);
		List<KerwinTaskerBean> ktbs = KerwinTaskerBean.selectAll();
		if(ktbs!=null&&ktbs.size()>0){
			for(final KerwinTaskerBean ktb : ktbs){
				KerwinTasker kt = ktb.getKt();
				tableModel.addRow(new Object[]{ktb.getId(), kt.getName(), kt.getStatus() ? "Opened" : "Closed", kt.getExecCount(), KerwinTasker.getDatetime(kt.getLastExecTime()), kt.getLastExecStatus(), KerwinTasker.getDatetime(kt.getNextExecTime())});
			}
		}
		validate();
	}
	
	private JLabel getOperLabel(String labelText){
		JLabel label = new JLabel(labelText);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setPreferredSize(new Dimension(50,25));
		label.setFont(new Font(getFont().getName(),getFont().getStyle(),14));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setForeground(Color.BLACK);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setForeground(Color.BLUE);
			}
		});
		return label;
	}
	
	static class OperRenderer extends JPanel implements TableCellRenderer,MouseListener{
	
		private static final long serialVersionUID = 1L;
		
	    public OperRenderer(){
	    	 add(new JLabel("test"));
	    }

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setToolTipText("test");
			return this;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("click");
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			System.out.println("enter");
		}

		@Override
		public void mouseExited(MouseEvent e) {
			System.out.println("exit");
		}
	}
	
	static class OperEditor extends AbstractCellEditor implements TableCellEditor{
		
		private static final long serialVersionUID = 1L;
		private OperRenderer operRenderer;
		
		public OperEditor(OperRenderer operRenderer){
			this.operRenderer = operRenderer;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			// TODO Auto-generated method stub
			return this.operRenderer;
		}
		
	}

}
