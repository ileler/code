package coderr.kerwin.arstat.pc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import coderr.kerwin.arstat.AtteRuleInfo;
import coderr.kerwin.arstat.DayTagsDAO;

/**
 * 设置规则的Dialog
 * @author kerwin612
 */
public class AtteRuleInfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JSpinner sDay;
	private JSpinner sTimeH;
	private JSpinner sTimeM;
	private JSpinner pTimeH;
	private JSpinner pTimeM;
	private JSpinner rTimeH;
	private JSpinner rTimeM;
	private JSpinner eTimeH;
	private JSpinner eTimeM;
	private JSpinner oTimeH;
	private JSpinner oTimeM;
	
	
	public AtteRuleInfoDialog(final DayTagsDAO dtDAO, final Date date){
		setTitle("规则设置");
		setResizable(false);
		setSize(350, 400);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		
		JPanel mainPanel = new JPanel(new GridLayout(6, 1));
		
		JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sDay = new JSpinner();
		tmpPanel.add(new JLabel("1~28"));
		tmpPanel.add(sDay);
		sDay.setModel(new SpinnerNumberModel(AtteRuleInfo.getSDay() == null ? 1 : AtteRuleInfo.getSDay(), 1, 28, 1));
		sDay.setEditor(new JSpinner.NumberEditor(sDay,"##"));
		mainPanel.add(initialField("考勤开始日期",tmpPanel));
		
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sTimeH = new JSpinner();
		tmpPanel.add(new JLabel("时:"));
		tmpPanel.add(sTimeH);
		sTimeH.setModel(new SpinnerNumberModel(AtteRuleInfo.getSTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getSTime().split(":")[0]), 00, 59, 1));
	    sTimeH.setEditor(new JSpinner.NumberEditor(sTimeH,"##"));
	    
	    sTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(sTimeM);
	    sTimeM.setModel(new SpinnerNumberModel(AtteRuleInfo.getSTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getSTime().split(":")[1]), 00, 59, 1));
	    sTimeM.setEditor(new JSpinner.NumberEditor(sTimeM,"##"));
	    mainPanel.add(initialField("规则上班时间",tmpPanel));
		
	    tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pTimeH = new JSpinner();
		tmpPanel.add(new JLabel("时:"));
		tmpPanel.add(pTimeH);
		pTimeH.setModel(new SpinnerNumberModel(AtteRuleInfo.getPTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getPTime().split(":")[0]), 00, 59, 1));
		pTimeH.setEditor(new JSpinner.NumberEditor(pTimeH,"##"));
	    
		pTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(pTimeM);
	    pTimeM.setModel(new SpinnerNumberModel(AtteRuleInfo.getPTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getPTime().split(":")[1]), 00, 59, 1));
	    pTimeM.setEditor(new JSpinner.NumberEditor(pTimeM,"##"));
	    mainPanel.add(initialField("休息开始时间",tmpPanel));
		
	    tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rTimeH = new JSpinner();
		tmpPanel.add(new JLabel("时:"));
		tmpPanel.add(rTimeH);
		rTimeH.setModel(new SpinnerNumberModel(AtteRuleInfo.getRTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getRTime().split(":")[0]), 00, 59, 1));
		rTimeH.setEditor(new JSpinner.NumberEditor(rTimeH,"##"));
	    
		rTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(rTimeM);
	    rTimeM.setModel(new SpinnerNumberModel(AtteRuleInfo.getRTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getRTime().split(":")[1]), 00, 59, 1));
	    rTimeM.setEditor(new JSpinner.NumberEditor(rTimeM,"##"));
	    mainPanel.add(initialField("休息结束时间",tmpPanel));
	    
	    tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	    eTimeH = new JSpinner();
	    tmpPanel.add(new JLabel("时:"));
	    tmpPanel.add(eTimeH);
	    eTimeH.setModel(new SpinnerNumberModel(AtteRuleInfo.getETime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getETime().split(":")[0]), 00, 59, 1));
	    eTimeH.setEditor(new JSpinner.NumberEditor(eTimeH,"##"));
	    
	    eTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(eTimeM);
	    eTimeM.setModel(new SpinnerNumberModel(AtteRuleInfo.getETime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getETime().split(":")[1]), 00, 59, 1));
	    eTimeM.setEditor(new JSpinner.NumberEditor(eTimeM,"##"));
	    mainPanel.add(initialField("规则下班时间",tmpPanel));
	    
	    tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	    oTimeH = new JSpinner();
	    tmpPanel.add(new JLabel("时:"));
	    tmpPanel.add(oTimeH);
	    oTimeH.setModel(new SpinnerNumberModel(AtteRuleInfo.getOTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getOTime().split(":")[0]), 00, 59, 1));
	    oTimeH.setEditor(new JSpinner.NumberEditor(oTimeH,"##"));
	    
	    oTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(oTimeM);
	    oTimeM.setModel(new SpinnerNumberModel(AtteRuleInfo.getOTime() == null ? 00 : Integer.parseInt(AtteRuleInfo.getOTime().split(":")[1]), 00, 59, 1));
	    oTimeM.setEditor(new JSpinner.NumberEditor(oTimeM,"##"));
	    mainPanel.add(initialField("开始加班时间",tmpPanel));
		
		add(mainPanel);
		
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton cancelBtn = new JButton("取消");
		cancelBtn.setFocusPainted(false);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		tmpPanel.add(cancelBtn);
		
		JButton enterBtn = new JButton("确定");
		enterBtn.setFocusPainted(false);
		enterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Integer.valueOf(sDay.getValue().toString()) != 0) {
					AtteRuleInfo.setSDay(Integer.valueOf(sDay.getValue().toString()));
				}
				if (Integer.valueOf(sTimeH.getValue().toString()) != 0 || Integer.valueOf(sTimeM.getValue().toString()) != 0) {
					AtteRuleInfo.setSTime(sTimeH.getValue()+":"+sTimeM.getValue());
				}
				if (Integer.valueOf(pTimeH.getValue().toString()) != 0 || Integer.valueOf(pTimeM.getValue().toString()) != 0) {
					AtteRuleInfo.setPTime(pTimeH.getValue()+":"+pTimeM.getValue());
				}
				if (Integer.valueOf(rTimeH.getValue().toString()) != 0 || Integer.valueOf(rTimeM.getValue().toString()) != 0) {
					AtteRuleInfo.setRTime(rTimeH.getValue()+":"+rTimeM.getValue());
				}
				if (Integer.valueOf(eTimeH.getValue().toString()) != 0 || Integer.valueOf(eTimeM.getValue().toString()) != 0) {
					AtteRuleInfo.setETime(eTimeH.getValue()+":"+eTimeM.getValue());
				}
				if (Integer.valueOf(oTimeH.getValue().toString()) != 0 || Integer.valueOf(oTimeM.getValue().toString()) != 0) {
					AtteRuleInfo.setOTime(oTimeH.getValue()+":"+oTimeM.getValue());
				}
				AtteRuleInfo.save();
				setVisible(false);
			}
		});
		tmpPanel.add(enterBtn);
		
		JButton synBtn = new JButton("同步节假日");
		synBtn.setFocusPainted(false);
		synBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dtDAO == null || date == null) return;
				dtDAO.synDayTags(date);
			}
		});
		tmpPanel.add(synBtn);
		add(tmpPanel, BorderLayout.SOUTH);
	}
	
	private JPanel initialField(String fieldLabel, Component component) {
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.setBorder(BorderFactory.createTitledBorder(fieldLabel));
		tmpPanel.add(component);
		return tmpPanel;
	}

}
