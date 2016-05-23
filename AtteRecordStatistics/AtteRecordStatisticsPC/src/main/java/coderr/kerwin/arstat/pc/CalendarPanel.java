package coderr.kerwin.arstat.pc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import coderr.kerwin.arstat.AtteRecordDAO;
import coderr.kerwin.arstat.AtteRecordStatBean;
import coderr.kerwin.arstat.DateUtil;

/**
 * 月历表面板
 * @author kerwin612
 */
public class CalendarPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel mainPanel;
	private MainFrame mainFrame;
	
	private AtteRecordDAO arDAO;
	
	public CalendarPanel(AtteRecordDAO arDAO, List<AtteRecordStatBean> list, MainFrame mainFrame){
		if (arDAO == null || list == null || list.size() < 42 || mainFrame == null)
			try {
				throw new Exception("Parameter is not valid.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		this.arDAO = arDAO;
		this.mainFrame = mainFrame;  
		setLayout(new BorderLayout());
		topPanel = new JPanel(new GridLayout(1, 7));
		topPanel.add(new JLabel("日", JLabel.CENTER));
		topPanel.add(new JLabel("一", JLabel.CENTER));
		topPanel.add(new JLabel("二", JLabel.CENTER));
		topPanel.add(new JLabel("三", JLabel.CENTER));
		topPanel.add(new JLabel("四", JLabel.CENTER));
		topPanel.add(new JLabel("五", JLabel.CENTER));
		topPanel.add(new JLabel("六", JLabel.CENTER));
		add(topPanel, BorderLayout.NORTH);
		mainPanel = new JPanel(new GridLayout(6, 7));
		add(mainPanel);
		for (int i = 0; i < 42; i++) {
			mainPanel.add(new DayPanel(list.get(i)));
		}
		
		//底部颜色说明面板。说明各颜色意义
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JLabel tmpLabel = new JLabel("节假日", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.PINK);
		bottomPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("无考勤", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.CYAN);
		bottomPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("未签退", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.MAGENTA);
		bottomPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("迟到", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.GREEN);
		bottomPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("加班", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.RED);
		bottomPanel.add(tmpLabel);
		
		tmpLabel = new JLabel("早退", JLabel.CENTER);
		tmpLabel.setOpaque(true);
		tmpLabel.setBackground(Color.ORANGE);
		bottomPanel.add(tmpLabel);
		add(bottomPanel, BorderLayout.SOUTH);
		
		setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0)));
	}
	
	/**
	 * 天面板
	 * @author kerwin612
	 */
	class DayPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		/**
		 * 考勤统计数据
		 */
		private AtteRecordStatBean bean;
		
		public DayPanel(AtteRecordStatBean bean) {
			if (bean == null)
				try {
					throw new Exception("Parameter is not valid.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			this.bean = bean;
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			add(initialDayLabel());
			if (bean.getAtDuration() != 0) {
				//迟到
				JLabel label = new JLabel("<html><span style='font-size:8px;'>"+bean.getAtDurationDesc()+"</span></html>", JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.GREEN);
				label.setPreferredSize(new Dimension(getWidth(), 10));
				add(label, BorderLayout.NORTH);
			}
			if (bean.getOtDuration() != 0) {
				//加班
				JLabel label = new JLabel("<html><span style='font-size:8px;'>"+bean.getOtDurationDesc()+"</span></html>", JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.RED);
				label.setPreferredSize(new Dimension(getWidth(), 10));
				add(label, BorderLayout.SOUTH);
			}
			if (bean.getLtDuration() != 0) {
				//早退
				JLabel label = new JLabel("<html><span style='font-size:8px;'>"+bean.getLtDurationDesc()+"</span></html>", JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.ORANGE);
				label.setPreferredSize(new Dimension(getWidth(), 10));
				add(label, BorderLayout.SOUTH);
			}
			String remark = bean.getArBean().getRemark();
			remark = remark == null || "".equals(remark) ? "" : remark;
			if (!"".equals(remark))
				setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}
		
		private JLabel initialDayLabel() {
			Calendar cal = Calendar.getInstance();
			cal.setTime(bean.getArBean().getDate());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			JLabel dayLabel = new JLabel("<html><span "+(!bean.isGrey()&&format.format(cal.getTime()).equals(format.format(new Date())) ? "style='color:blue;font-weight:bold;'" : "")+">"+String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+"</span></html>", JLabel.CENTER);
			dayLabel.setOpaque(true);
			String remark = bean.getArBean().getRemark();
			remark = remark == null || "".equals(remark) ? "" : "|备注:" + remark;
			if (bean.getArBean().isHoliday()) 
				dayLabel.setBackground(Color.PINK);
			if (bean.isGrey())
				dayLabel.setForeground(Color.GRAY);
			switch (bean.getArStatus()) {
				case N:
					if (bean.getArBean().isHoliday()) {
						dayLabel.setToolTipText("节假日"+remark);
					} else if (bean.getArBean().getDate().before(new Date())) {
						dayLabel.setToolTipText("无考勤记录"+remark);
						dayLabel.setBackground(Color.CYAN);
					}
					break;
				case S:
					dayLabel.setToolTipText("签到时间:"+bean.getArBean().getSTimeDesc()+remark);
					if (!DateUtil.isSameDay(bean.getArBean().getDate(), new Date())) {
						dayLabel.setToolTipText("签到时间:"+bean.getArBean().getSTimeDesc()+"|未签退"+remark);
						dayLabel.setBackground(Color.MAGENTA);
					}
					break;
				case E:
					dayLabel.setToolTipText("签到时间:"+bean.getArBean().getSTimeDesc() + "|签退时间:"+bean.getArBean().getETimeDesc()
							+ (bean.getAtDuration() != 0 ? "|迟到时间:"+bean.getAtDurationDesc() : "")
							+ (bean.getOtDuration() != 0 ? "|加班时间:"+bean.getOtDurationDesc() : "")
							+ (bean.getLtDuration() != 0 ? "|早退时间:"+bean.getLtDurationDesc() : "")+remark);
					break;
			}
			dayLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					//如果所点天在当前日期之后则返回
					if (bean.getArBean().getDate().after(new Date())) return;
					//弹出所点天考勤Dialog
					new AtteRecordBeanDialog(CalendarPanel.this.arDAO, bean.getArBean()).setVisible(true);
					mainFrame.refresh();
				}
			});
			return dayLabel;
		}
		
	}
	
}
