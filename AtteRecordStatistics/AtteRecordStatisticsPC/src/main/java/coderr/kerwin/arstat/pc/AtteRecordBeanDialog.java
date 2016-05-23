package coderr.kerwin.arstat.pc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import coderr.kerwin.arstat.AtteRecordBean;
import coderr.kerwin.arstat.AtteRecordDAO;
import coderr.kerwin.arstat.AtteRuleInfo;
import coderr.kerwin.arstat.DateUtil;

/**
 * 考勤信息展示的Dialog
 * @author kerwin612
 */
public class AtteRecordBeanDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 是否节假日（是）
	 */
	private JRadioButton yesRadio;
	
	/**
	 * 是否节假日（否）
	 */
	private JRadioButton noRadio;
	
	private JSpinner sTimeH;
	private JSpinner sTimeM;
	private JSpinner sTimeS;
	private JSpinner eTimeH;
	private JSpinner eTimeM;
	private JSpinner eTimeS;
	private	JTextArea remarkTextArea;
	
	private AtteRecordDAO arDAO;
	
	
	public AtteRecordBeanDialog(AtteRecordDAO arDAO, final AtteRecordBean bean){
		if (arDAO == null || bean == null || bean.getDate() == null)
			try {
				throw new Exception("Parameter is not valid.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		this.arDAO = arDAO;
		setTitle(new SimpleDateFormat("yyyy年MM月dd日考勤").format(bean.getDate()));
		setResizable(false);
		setSize(300, 300);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		
		JPanel mainPanel = new JPanel(new GridLayout(4, 1));
		
		JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		yesRadio = new JRadioButton("是", bean.isHoliday());
		noRadio = new JRadioButton("否", !bean.isHoliday());
		ButtonGroup bg = new ButtonGroup();
		bg.add(yesRadio);
		bg.add(noRadio);
		tmpPanel.add(yesRadio);
		tmpPanel.add(noRadio);
		mainPanel.add(initialField("节假日",tmpPanel));
		
		int sh = 0, sm = sh, ss = sh, eh = sh, em = sh, es = sh;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (DateUtil.isSameDay(bean.getDate(), new Date()) && (bean.getSTime() == null)) {
			sh = cal.get(Calendar.HOUR_OF_DAY);
			sm = cal.get(Calendar.MINUTE);
			ss = cal.get(Calendar.SECOND);
		}
		if (DateUtil.isSameDay(bean.getDate(), new Date()) && (bean.getSTime() != null && bean.getETime() == null)) {
			eh = cal.get(Calendar.HOUR_OF_DAY);
			em = cal.get(Calendar.MINUTE);
			es = cal.get(Calendar.SECOND);
		}
		
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sTimeH = new JSpinner();
		tmpPanel.add(new JLabel("时:"));
		tmpPanel.add(sTimeH);
		sTimeH.setModel(new SpinnerNumberModel(bean.getSTime() == null ? sh : Integer.parseInt(new SimpleDateFormat("HH").format(bean.getSTime())), 00, 23, 1));
	    sTimeH.setEditor(new JSpinner.NumberEditor(sTimeH,"##"));
	    
	    sTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(sTimeM);
	    sTimeM.setModel(new SpinnerNumberModel(bean.getSTime() == null ? sm : Integer.parseInt(new SimpleDateFormat("mm").format(bean.getSTime())), 00, 59, 1));
	    sTimeM.setEditor(new JSpinner.NumberEditor(sTimeM,"##"));
	    
	    sTimeS = new JSpinner();
	    tmpPanel.add(new JLabel("秒:"));
	    tmpPanel.add(sTimeS);
	    sTimeS.setModel(new SpinnerNumberModel(bean.getSTime() == null ? ss : Integer.parseInt(new SimpleDateFormat("ss").format(bean.getSTime())), 00, 59, 1));
	    sTimeS.setEditor(new JSpinner.NumberEditor(sTimeS,"##"));
	    mainPanel.add(initialField("上班时间",tmpPanel));
		
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eTimeH = new JSpinner();
		tmpPanel.add(new JLabel("时:"));
		tmpPanel.add(eTimeH);
		eTimeH.setModel(new SpinnerNumberModel(bean.getETime() == null ? eh : Integer.parseInt(new SimpleDateFormat("HH").format(bean.getETime())), 00, 23, 1));
	    eTimeH.setEditor(new JSpinner.NumberEditor(eTimeH,"##"));
	    
	    eTimeM = new JSpinner();
	    tmpPanel.add(new JLabel("分:"));
	    tmpPanel.add(eTimeM);
	    eTimeM.setModel(new SpinnerNumberModel(bean.getETime() == null ? em : Integer.parseInt(new SimpleDateFormat("mm").format(bean.getETime())), 00, 59, 1));
	    eTimeM.setEditor(new JSpinner.NumberEditor(eTimeM,"##"));
	    
	    eTimeS = new JSpinner();
	    tmpPanel.add(new JLabel("秒:"));
	    tmpPanel.add(eTimeS);
	    eTimeS.setModel(new SpinnerNumberModel(bean.getETime() == null ? es : Integer.parseInt(new SimpleDateFormat("ss").format(bean.getETime())), 00, 59, 1));
	    eTimeS.setEditor(new JSpinner.NumberEditor(eTimeS,"##"));
	    mainPanel.add(initialField("下班时间",tmpPanel));
		
		remarkTextArea = new JTextArea();
		remarkTextArea.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(remarkTextArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(initialField("备注", scroll));
		
		add(mainPanel);
		
		tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton clearBtn = new JButton("清空");
		clearBtn.setToolTipText("清空数据");
		clearBtn.setFocusPainted(false);
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bean.setHoliday(false);
				bean.setRemark(null);
				bean.setSTime(null);
				bean.setETime(null);
				AtteRecordBeanDialog.this.arDAO.save(bean);
				setVisible(false);
			}
		});
		tmpPanel.add(clearBtn);
		
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
				bean.setHoliday(yesRadio.isSelected());
				if (DateUtil.isSameDay(bean.getDate(), new Date()) || (Integer.valueOf(sTimeH.getValue().toString()) != 0 || Integer.valueOf(sTimeM.getValue().toString()) != 0 || Integer.valueOf(sTimeS.getValue().toString()) != 0)) {
					bean.setSTime(DateUtil.getDateByTime(bean.getDate(), sTimeH.getValue()+":"+sTimeM.getValue()+":"+sTimeS.getValue()));
				}
				if (DateUtil.isSameDay(bean.getDate(), new Date()) || (bean.getSTime() != null && (Integer.valueOf(eTimeH.getValue().toString()) != 0 || Integer.valueOf(eTimeM.getValue().toString()) != 0 || Integer.valueOf(eTimeS.getValue().toString()) != 0))) {
					if (DateUtil.isSameDay(bean.getDate(), new Date()) && Integer.valueOf(eTimeH.getValue().toString()) == 0 && Integer.valueOf(eTimeM.getValue().toString()) == 0 && Integer.valueOf(eTimeS.getValue().toString()) == 0) {
						bean.setETime(null);
					} else {
						Date eDate = DateUtil.getDateByTime(bean.getDate(), eTimeH.getValue()+":"+eTimeM.getValue()+":"+eTimeS.getValue());
						if (eDate.before(bean.getSTime())) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(eDate);
							cal.add(Calendar.DAY_OF_MONTH, 1);
							eDate = cal.getTime();
						}
						bean.setETime(eDate);
					}
				}
				bean.setRemark(remarkTextArea.getText());
				AtteRecordBeanDialog.this.arDAO.save(bean);
				setVisible(false);
			}
		});
		tmpPanel.add(enterBtn);
		
		if (!DateUtil.isSameDay(bean.getDate(), new Date()) && bean.getDate().before(new Date())) {
			JButton ruleBtn = new JButton("标勤");
			ruleBtn.setToolTipText("标准考勤");
			ruleBtn.setFocusPainted(false);
			ruleBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bean.setHoliday(yesRadio.isSelected());
					bean.setSTime(DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getSTime()));
					bean.setETime(DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getETime()));
					bean.setRemark(remarkTextArea.getText());
					AtteRecordBeanDialog.this.arDAO.save(bean);
					setVisible(false);
				}
			});
			tmpPanel.add(ruleBtn);
		}
		add(tmpPanel, BorderLayout.SOUTH);
	}
	
	private JPanel initialField(String fieldLabel, Component component) {
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.setBorder(BorderFactory.createTitledBorder(fieldLabel));
		tmpPanel.add(component);
		return tmpPanel;
	}

}
