package org.kerwin.tools.tasker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.kerwin.task.KerwinTaskScheduler;
import org.kerwin.task.KerwinTasker;
import org.kerwin.task.KerwinTasker.KerwinTaskerType5.IntervalType;
import org.kerwin.tools.bean.KerwinTaskerBean;
import org.kerwin.tools.bean.KerwinTaskerBean.KerwinTaskerType1Impl;
import org.kerwin.tools.bean.KerwinTaskerBean.KerwinTaskerType2Impl;
import org.kerwin.tools.bean.KerwinTaskerBean.KerwinTaskerType3Impl;
import org.kerwin.tools.bean.KerwinTaskerBean.KerwinTaskerType4Impl;
import org.kerwin.tools.bean.KerwinTaskerBean.KerwinTaskerType5Impl;
import org.kerwin.tools.util.BeanUtil;

public class KerwinTaskerBeanDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private boolean isShow;
	private JLabel infoLabel;
	private JPanel btnPanel;
	private JLabel enterLabel;
	private JLabel cancelLabel;
	private JPanel tabPanel;
	private JPanel tabTPanel;
	private String currentTab;
	private List<JComponent> tabCPanels;
	private JPanel taskType1Panel;
	private JPanel taskType2Panel;
	private JPanel taskType3Panel;
	private JPanel taskType4Panel;
	private JPanel taskType5Panel;
	
	private JTextField taskName;
	private JPanel baseInfoPanel;
	private JTextField taskClass;
	private JTextArea taskCommand;
	private OnOffPanel onOffPanel;
	
	private JSpinner execTimeH;
	private JSpinner execTimeM;
	private JSpinner execTimeS;
	
	private JSpinner type2Y;
	private JSpinner type2M;
	private JSpinner type2D;
	
	private JPanel weekPanel;
	
	private JSpinner type4D;
	
	private JSpinner type5I;
	private JSpinner type5V;
	
	private Timer timer;
	private KerwinTasker kt;
	
	public KerwinTaskerBeanDialog(){
		setTitle("Add Tasker");
		setResizable(false);
		setSize(500, 450);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getHeight()) / 2));
		
		enterLabel = getOperLabel("Enter");
		enterLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = null,className = null,command = null,taskCs = null;
				if((name = taskName.getText()) == null || name.isEmpty()){
					showMessage("Name is null");
					return;
				}
				if(((className = taskClass.getText()) == null || className.isEmpty()) && ((command = taskCommand.getText()) == null || command.isEmpty())){
					showMessage("TaskClass&Command is null");
					return;
				}
				if(className == null || className.isEmpty()){
					taskCs = "Command:"+command;
				}else{
					taskCs = className;
				}
				if("type2".equals(currentTab)){
					if(kt == null)	kt = new KerwinTaskerType2Impl();
					if(!(kt instanceof KerwinTaskerType2Impl))	kt = KerwinTasker.convertKerwinTasker(KerwinTaskerType2Impl.class, kt);
					try {
						type2Y.commitEdit();
						type2M.commitEdit();
						type2D.commitEdit();
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
					((KerwinTaskerType2Impl)kt).setDate(type2Y.getValue()+"-"+type2M.getValue()+"-"+type2D.getValue());
				}else if("type3".equals(currentTab)){
					List<Integer> is = new ArrayList<Integer>();
					if(kt == null)	kt = new KerwinTaskerType3Impl();
					if(!(kt instanceof KerwinTaskerType3Impl))	kt = KerwinTasker.convertKerwinTasker(KerwinTaskerType3Impl.class, kt);
					Component[] cs = weekPanel.getComponents();
					for(Component c: cs){
						if(!(c.getName().startsWith("checked|")))	continue;
						is.add(Integer.parseInt(c.getName().substring(8)));
					}
					if(is.size() < 1){
						showMessage("Week is null");
						return;
					}
					((KerwinTaskerType3Impl)kt).setWeeks(is.toArray(new Integer[is.size()]));
				}else if("type4".equals(currentTab)){
					if(kt == null)	kt = new KerwinTaskerType4Impl();
					if(!(kt instanceof KerwinTaskerType4Impl))	kt = KerwinTasker.convertKerwinTasker(KerwinTaskerType4Impl.class, kt);
					try {
						type4D.commitEdit();
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
					((KerwinTaskerType4Impl)kt).setDay(Integer.parseInt(type4D.getValue().toString()));
				}else if("type5".equals(currentTab)){
					if(kt == null)	kt = new KerwinTaskerType5Impl();
					if(!(kt instanceof KerwinTaskerType5Impl))	kt = KerwinTasker.convertKerwinTasker(KerwinTaskerType5Impl.class, kt);
					try {
						type5I.commitEdit();
						type5V.commitEdit();
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
					if("Hour".equals(type5I.getValue())){
						((KerwinTaskerType5Impl)kt).setIntervalType(IntervalType.H);
					}else if("Min".equals(type5I.getValue())){
						((KerwinTaskerType5Impl)kt).setIntervalType(IntervalType.M);
					}else{
						((KerwinTaskerType5Impl)kt).setIntervalType(IntervalType.S);
					}
					((KerwinTaskerType5Impl)kt).setInterval(Integer.parseInt(type5V.getValue().toString()));
				}else{
					if(kt == null)	kt = new KerwinTaskerType1Impl();
					if(!(kt instanceof KerwinTaskerType1Impl))	kt = KerwinTasker.convertKerwinTasker(KerwinTaskerType1Impl.class, kt);
				}
				kt.setName(name);
				kt.setTaskerClass(taskCs);
				kt.setStatus(onOffPanel.isOn());
				try {
					execTimeH.commitEdit();
					execTimeM.commitEdit();
					execTimeS.commitEdit();
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
				kt.setExecTime(execTimeH.getValue()+":"+execTimeM.getValue()+":"+execTimeS.getValue());
				try {
					if(kt.getId() != null)	kt.setNextExecTime(null);
					kt.setNextExecTime(kt.calculateNextExecTime());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if(kt.getId() == null){
					kt.setId(BeanUtil.generateID());
					KerwinTaskerBean ktb = new  KerwinTaskerBean(kt);
					try {
						ktb.insert();
						KerwinTaskScheduler.addKerwinTasker(kt);
						KerwinTaskerBeanDialog.this.setVisible(false);
					} catch (Exception e1) {
						showMessage(e1.getMessage());
					}
				}else{
					kt.updateKerwinTasker();
					KerwinTaskScheduler.updateKerwinTasker(kt);
					KerwinTaskerBeanDialog.this.setVisible(false);
				}
			}
		});
		
		cancelLabel = getOperLabel("Cancel");
		cancelLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				KerwinTaskerBeanDialog.this.setVisible(false);
			}
		});
		
		JPanel tmpPanel = new JPanel();
		add(tmpPanel,BorderLayout.SOUTH);
		
		btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
		btnPanel.setPreferredSize(new Dimension(getWidth(),30));
		btnPanel.add(cancelLabel);
		btnPanel.add(enterLabel);
		tmpPanel.add(btnPanel);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setPreferredSize(new Dimension(getWidth()-6,30));
		infoLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createLineBorder(Color.RED)));
		tmpPanel.add(infoLabel);
		infoLabel.setVisible(false);
		
		initialTabPanel();
		add(tabPanel,BorderLayout.NORTH);
		
		initialBaseInfoPanel();
		add(baseInfoPanel);
	}
	
	public KerwinTaskerBeanDialog(KerwinTasker kt) throws Exception{
		this();
		if(kt == null){
			throw new Exception("arg is null");
		}
		this.kt = kt;
		timer.cancel();
		timer.purge();
		
		String labelName = null;
		if(kt instanceof KerwinTaskerType2Impl){
			labelName = "type2";
			String str = ((KerwinTaskerType2Impl)kt).getDate();
			type2Y.setValue(Integer.parseInt(str.split(":")[0]));
			type2M.setValue(Integer.parseInt(str.split(":")[1]));
			type2D.setValue(Integer.parseInt(str.split(":")[2]));
		}else if(kt instanceof KerwinTaskerType3Impl){
			labelName = "type3";
			Integer[] is = ((KerwinTaskerType3Impl)kt).getWeeks();
			if(is != null){
				List<Integer> list = Arrays.asList(is);
				Component[] cs = weekPanel.getComponents();
				for(Component c: cs){
					if(c.getName() == null || !list.contains(Integer.valueOf(c.getName())))	continue;
					((JLabel)c).setBorder(BorderFactory.createLineBorder(Color.RED));
					c.setName("checked|"+c.getName());
				}
			}
		}else if(kt instanceof KerwinTaskerType4Impl){
			labelName = "type4";
			int i = ((KerwinTaskerType4Impl)kt).getDay();
			type4D.setValue(Integer.valueOf(i));
		}else if(kt instanceof KerwinTaskerType5Impl){
			labelName = "type5";
			int iv = ((KerwinTaskerType5Impl)kt).getInterval();
			IntervalType it = ((KerwinTaskerType5Impl)kt).getIntervalType();
			if(it != null){
				if(it.equals(IntervalType.H)){
					type5I.setValue("Hour");
				}else if(it.equals(IntervalType.M)){
					type5I.setValue("Min");
				}else{
					type5I.setValue("Sec");
				}
				type5V.setValue(iv);
			}
		}else{
		}
		if(labelName != null){
			Component[] cs = tabTPanel.getComponents();
			for(Component c : cs){
				if(c.getName() == null)	continue;
				if(labelName.equals(c.getName())){
					changeTab((JLabel)c, labelName);
					break;
				}
			}
		}
		
		taskName.setText(kt.getName());
		String str = kt.getTaskerClass();
		if(str != null){
			if(str.startsWith("Command:")){
				taskCommand.setText(str.substring(8));
			}else{
				taskClass.setText(str);
			}
		}
		onOffPanel.setStatus(kt.getStatus());
		str = kt.getExecTime();
		execTimeH.setValue(Integer.parseInt(str.split(":")[0]));
		execTimeM.setValue(Integer.parseInt(str.split(":")[1]));
		execTimeS.setValue(Integer.parseInt(str.split(":")[2]));
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
	
	private void initialBaseInfoPanel(){
		baseInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING,0,5));
		
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
		
		taskName = new JTextField();
		taskName.setPreferredSize(new Dimension(getWidth()-6,45));
		taskName.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Name"));
		taskName.getDocument().addDocumentListener(dl);
		baseInfoPanel.add(taskName);
		
		JPanel taskStatus = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		taskStatus.setPreferredSize(new Dimension(getWidth()-6,45));
		taskStatus.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Status"));
		onOffPanel = new OnOffPanel("   ON   ","   OFF   ");
		onOffPanel.setEnabledColor(Color.PINK);
		onOffPanel.setDisabledColor(new Color(51,153,255));
		taskStatus.add(onOffPanel);
		
		baseInfoPanel.add(taskStatus);
		
		taskClass = new JTextField();
		taskClass.setPreferredSize(new Dimension(getWidth()-6,45));
		taskClass.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "TaskClass"));
		taskClass.getDocument().addDocumentListener(dl);
		baseInfoPanel.add(taskClass);
		
		taskCommand = new JTextArea();
		taskCommand.setLineWrap(true);        			//激活自动换行功能 
		taskCommand.setWrapStyleWord(true);            	//激活断行不断字功能
		taskCommand.setBorder(BorderFactory.createEmptyBorder());
		taskCommand.getDocument().addDocumentListener(dl);
		JScrollPane scrollPanel = new JScrollPane(taskCommand);
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);
		scrollPanel.setPreferredSize(new Dimension(getWidth()-6,90));
		scrollPanel.getVerticalScrollBar().setUnitIncrement(10);
		scrollPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Command"));
		scrollPanel.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	//设置水平滚动条从不显示
		baseInfoPanel.add(scrollPanel);
		
		JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		timePanel.setPreferredSize(new Dimension(getWidth()-6,45));
		timePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "ExecTime"));
		
		execTimeH = new JSpinner();
		timePanel.add(new JLabel("Hour:"));
		timePanel.add(execTimeH);
		SpinnerNumberModel snmh = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("HH").format(new Date())), 00, 23, 1);
		execTimeH.setModel(snmh);
	    JSpinner.NumberEditor jsneh = new JSpinner.NumberEditor(execTimeH,"##");
	    execTimeH.setEditor(jsneh);
	    
	    execTimeM = new JSpinner();
	    timePanel.add(new JLabel("Minute:"));
	    timePanel.add(execTimeM);
	    SpinnerNumberModel snmm = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("mm").format(new Date())), 00, 59, 1);
	    execTimeM.setModel(snmm);
	    JSpinner.NumberEditor jsnem = new JSpinner.NumberEditor(execTimeM,"##");
	    execTimeM.setEditor(jsnem);
	    
	    execTimeS = new JSpinner();
	    timePanel.add(new JLabel("Second:"));
	    timePanel.add(execTimeS);
	    SpinnerNumberModel snms = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("ss").format(new Date())), 00, 59, 1);
	    execTimeS.setModel(snms);
	    JSpinner.NumberEditor jsnes = new JSpinner.NumberEditor(execTimeS,"##");
	    execTimeS.setEditor(jsnes);
	    
	    timer = new Timer();
	    timer.schedule(new TimerTask() {
			@Override
			public void run() {
				execTimeH.setValue(Integer.parseInt(new SimpleDateFormat("HH").format(new Date())));
				execTimeM.setValue(Integer.parseInt(new SimpleDateFormat("mm").format(new Date())));
				execTimeS.setValue(Integer.parseInt(new SimpleDateFormat("ss").format(new Date())));
			}
		}, 1L, 1000L);
	    
	    FocusListener fl = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				timer.cancel();
				timer.purge();
			}
		};
	    
	    jsneh.getTextField().addFocusListener(fl);
	    jsnem.getTextField().addFocusListener(fl);
	    jsnes.getTextField().addFocusListener(fl);
	    
		baseInfoPanel.add(timePanel);
	}
	
	private void initialTabPanel(){
		tabPanel = new JPanel(new BorderLayout());
		
		tabTPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,1,0));
		tabTPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.RED), BorderFactory.createEmptyBorder(0, 0, 2, 0)));
		tabTPanel.setPreferredSize(new Dimension(getWidth(),30));
		tabPanel.add(tabTPanel,BorderLayout.NORTH);
		
		tabCPanels = new ArrayList<JComponent>();
		
		initialTaskType1Panel();
		taskType1Panel.setName("type1");
		tabPanel.add(taskType1Panel);
		tabCPanels.add(taskType1Panel);
		
		initialTaskType2Panel();
		taskType2Panel.setName("type2");
		tabCPanels.add(taskType2Panel);

		initialTaskType3Panel();
		taskType3Panel.setName("type3");
		tabCPanels.add(taskType3Panel);

		initialTaskType4Panel();
		taskType4Panel.setName("type4");
		tabCPanels.add(taskType4Panel);

		initialTaskType5Panel();
		taskType5Panel.setName("type5");
		tabCPanels.add(taskType5Panel);
		
		currentTab = "type1";
		tabTPanel.add(getTabLabel("Type1","current"));
		tabTPanel.add(getTabLabel("Type2","type2"));
		tabTPanel.add(getTabLabel("Type3","type3"));
		tabTPanel.add(getTabLabel("Type4","type4"));
		tabTPanel.add(getTabLabel("Type5","type5"));
	}
	
	private void initialTaskType1Panel(){
		taskType1Panel = new JPanel(new BorderLayout());
		
		JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		tmpPanel.setPreferredSize(new Dimension(getWidth()-6,45));
		tmpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Date"));
		tmpPanel.add(new JLabel("Every Day"));
		
		taskType1Panel.add(tmpPanel);
	}
	
	private void initialTaskType2Panel(){
		taskType2Panel = new JPanel(new BorderLayout());
		
		JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		datePanel.setPreferredSize(new Dimension(getWidth()-6,45));
		datePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Date"));
		
		type2Y = new JSpinner();
		datePanel.add(new JLabel("Year:"));
		datePanel.add(type2Y);
		SpinnerNumberModel snmy = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())), 2000, 2099, 1);
		type2Y.setModel(snmy);
	    JSpinner.NumberEditor jsney = new JSpinner.NumberEditor(type2Y,"####");
	    type2Y.setEditor(jsney);
	    
	    type2M = new JSpinner();
	    datePanel.add(new JLabel("Month:"));
	    datePanel.add(type2M);
	    SpinnerNumberModel snmm = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("MM").format(new Date())), 01, 12, 1);
	    type2M.setModel(snmm);
	    JSpinner.NumberEditor jsnem = new JSpinner.NumberEditor(type2M,"##");
	    type2M.setEditor(jsnem);
	    
	    type2D = new JSpinner();
	    datePanel.add(new JLabel("Day:"));
	    datePanel.add(type2D);
	    SpinnerNumberModel snmd = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("dd").format(new Date())), 01, 31, 1);
	    type2D.setModel(snmd);
	    JSpinner.NumberEditor jsned = new JSpinner.NumberEditor(type2D,"##");
	    type2D.setEditor(jsned);
	    
	    taskType2Panel.add(datePanel);
	}
	
	private void initialTaskType3Panel(){
		taskType3Panel = new JPanel(new BorderLayout());
		
		weekPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		weekPanel.setPreferredSize(new Dimension(getWidth()-6,45));
		weekPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Week"));
		
		MouseListener ml = new MouseAdapter(){
			@Override
			public void mouseExited(MouseEvent e) {
				String name = e.getComponent().getName();
				if(name != null && name.startsWith("checked"))	return;
				((JLabel)e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				String name = e.getComponent().getName();
				if(name != null && name.startsWith("checked"))	return;
				((JLabel)e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.PINK));				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(isShow)	hideMessage();
				String name = e.getComponent().getName();
				if(name != null && name.startsWith("checked")){
					((JLabel)e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.PINK));
					e.getComponent().setName(name.split("|")[1]);
				}else{
					((JLabel)e.getComponent()).setBorder(BorderFactory.createLineBorder(Color.RED));
					e.getComponent().setName("checked|"+name);
				}
			}
		};
		
		JLabel tmpLabel = new JLabel("   Mon   ");
		tmpLabel.setName("1");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Tue   ");
		tmpLabel.setName("2");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Wed   ");
		tmpLabel.setName("3");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Thu   ");
		tmpLabel.setName("4");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Fri   ");
		tmpLabel.setName("5");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Sat   ");
		tmpLabel.setName("6");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("   Sun   ");
		tmpLabel.setName("7");
		tmpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tmpLabel.addMouseListener(ml);
		weekPanel.add(tmpLabel);
		
		taskType3Panel.add(weekPanel);
	}
	
	private void initialTaskType4Panel(){
		taskType4Panel = new JPanel(new BorderLayout());
		
		JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		datePanel.setPreferredSize(new Dimension(getWidth()-6,45));
		datePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Date"));
		
		type4D = new JSpinner();
	    datePanel.add(new JLabel("Date:"));
	    datePanel.add(type4D);
	    SpinnerNumberModel snmd = new SpinnerNumberModel(Integer.parseInt(new SimpleDateFormat("dd").format(new Date())), 01, 31, 1);
	    type4D.setModel(snmd);
	    JSpinner.NumberEditor jsned = new JSpinner.NumberEditor(type4D,"##");
	    type4D.setEditor(jsned);
		
		taskType4Panel.add(datePanel);
	}
	
	private void initialTaskType5Panel(){
		taskType5Panel = new JPanel(new BorderLayout());
		
		JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		tmpPanel.setPreferredSize(new Dimension(getWidth()-6,45));
		tmpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Interval"));
		
		type5I = new JSpinner();
		tmpPanel.add(new JLabel("Type:"));
		tmpPanel.add(type5I);
		SpinnerListModel slmi = new SpinnerListModel(new String[]{"Hour","Min","Sec"});
		type5I.setModel(slmi);
		
		type5V = new JSpinner();
		tmpPanel.add(new JLabel("Value:"));
		tmpPanel.add(type5V);
	    SpinnerNumberModel snmd = new SpinnerNumberModel(5, 01, 99, 1);
	    type5V.setModel(snmd);
	    JSpinner.NumberEditor jsned = new JSpinner.NumberEditor(type5V,"##");
	    type5V.setEditor(jsned);
		
		taskType5Panel.add(tmpPanel);
	}
	
	private JLabel getTabLabel(String labeltext, String tabname){
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(src.getName()))	return;
				src.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(src.getName()))	return;
				src.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.PINK));
				src.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.RED), BorderFactory.createMatteBorder(0, 0, 2, 0, Color.PINK)));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = null;
				JLabel src = (JLabel)e.getComponent();
				if("current".equals(name = src.getName()))	return;
				changeTab(src,name);
			}
		};
		JLabel label = new JLabel(labeltext);
		label.setName(tabname);
		label.setFont(new Font(label.getFont().getName(),Font.BOLD,14));
		label.setPreferredSize(new Dimension(80,tabTPanel.getPreferredSize().height));
		if("current".equals(tabname)){
			label.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.RED));
		}else{
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		}
		label.setName(tabname);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.addMouseListener(ml);
		return label;
	}
	
	private void changeTab(JLabel src,String name){
		Component[] cs = tabTPanel.getComponents();
		for(Component c : cs){
			if("current".equals(c.getName()))	c.setName(tabPanel.getComponent(tabPanel.getComponents().length - 1).getName());
			((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		}
		for(Component c : tabCPanels){
			if(name.equals(c.getName())){
				currentTab= name;
				tabPanel.add(c);
				validate();
			}else{
				tabPanel.remove(c);
				repaint();
			}	
		}
		src.setName("current");
		src.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.RED));
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
	
	class OnOffPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private boolean status;
		private String onText;
		private String offText;
		private JLabel onLabel;
		private JLabel offLabel;
		private Color enabledColor;
		private Color disabledColor;
		
		public OnOffPanel(){
			status = true;
			onText = "on";
			onLabel = new JLabel(onText);
			onLabel.addMouseListener(getMouseListener());
			
			offText = "off";
			offLabel = new JLabel(offText);
			offLabel.addMouseListener(getMouseListener());
			onOper();
			
			enabledColor = Color.GRAY;
			disabledColor = Color.WHITE;
			
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			setLayout(new GridLayout(1, 2, 0, 0));
			add(onLabel);
			add(offLabel);
		}
		
		public OnOffPanel(String onText, String offText){
			this();
			onLabel.setText(onText);
			offLabel.setText(offText);
		}
		
		private MouseListener getMouseListener(){
			MouseListener ml = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JLabel srcLabel = (JLabel)e.getComponent();
					if(status && srcLabel.equals(onLabel))	return;
					if(!status && srcLabel.equals(offLabel))	return;
					if(status){
						offOper();
					}else{
						onOper();
					}
				}
			};
			return ml;
		}
		
		private void onOper(){
			status = true;
			onLabel.setBackground(enabledColor);
			onLabel.setEnabled(false);
			onLabel.setOpaque(true);
			onLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			offLabel.setBackground(disabledColor);
			offLabel.setEnabled(true);
			offLabel.setOpaque(true);
			offLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		private void offOper(){
			status = false;
			offLabel.setBackground(enabledColor);
			offLabel.setEnabled(false);
			offLabel.setOpaque(true);
			offLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			onLabel.setBackground(disabledColor);
			onLabel.setEnabled(true);
			onLabel.setOpaque(true);
			onLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		public void setStatus(boolean status){
			if(status){
				onOper();
			}else{
				offOper();
			}
		}
		
		public void setEnabledColor(Color color){
			enabledColor = color;
			if(status){
				onLabel.setBackground(enabledColor);
			}else{
				offLabel.setBackground(enabledColor);
			}
		}
		
		public void setDisabledColor(Color color){
			disabledColor = color;
			if(status){
				offLabel.setBackground(disabledColor);
			}else{
				onLabel.setBackground(disabledColor);
			}
		}
		
		public JLabel getOnLabel(){
			return onLabel;
		}
		
		public JLabel getOffLabel(){
			return offLabel;
		}
		
		public boolean isOn(){
			return status;
		}
		
		public boolean isOff(){
			return status ? false : true;
		}
		
	}
	
}
