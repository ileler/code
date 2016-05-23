package coderr.kerwin.arstat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 考勤信息实体类
 * @author kewrin612
 */
public class AtteRecordBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 工作日期
	 */
	private Date date;
	
	/**
	 * 上班时间
	 */
	private Date sTime;
	
	/**
	 * 下班时间
	 */
	private Date eTime;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 是否节假日
	 */
	private boolean isHoliday;
	
	/**
	 * DayTags
	 */
	private DayTags dayTags;
	
	public AtteRecordBean() {}

	public AtteRecordBean(Date date) {
		this(date, null, null, null);
	}
	
	public AtteRecordBean(Date date, Date sTime, Date eTime, String remark) {
		this(date, sTime, eTime, remark, null);
	}
	
	public AtteRecordBean(Date date, Date sTime, Date eTime, String remark, DayTags dayTags) {
		super();
		this.date = date;
		this.sTime = sTime;
		this.eTime = eTime;
		this.remark = remark;
		this.dayTags = dayTags;
		this.isHoliday = DateUtil.isWeekend(date);
	}

	public Date getDate() {
		return date;
	}
	
	public String getDateDesc() {
		return date == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getSTime() {
		return sTime;
	}
	
	public String getSTimeDesc() {
		return sTime == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sTime);
	}

	public void setSTime(Date sTime) {
		this.sTime = sTime;
	}

	public Date getETime() {
		return eTime;
	}
	
	public String getETimeDesc() {
		return eTime == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eTime);
	}

	public void setETime(Date eTime) {
		this.eTime = eTime;
	}

	public String getRemark() {
		String dt = (dayTags == null || dayTags.getTitle() == null || "".equals(dayTags.getTitle()) ? "" : "[" + dayTags.getTitle() + "]");
		return dt + (remark == null ? "" : (dt == null || "".equals(dt) ? "" : " | ") + remark);
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	
	public boolean isHoliday() {
		return dayTags == null || dayTags.getHoliday() == null ? isHoliday : (isHoliday = dayTags.getHoliday());
	}

	public DayTags getDayTags() {
		return dayTags;
	}

	public void setDayTags(DayTags dayTags) {
		this.dayTags = dayTags;
	}

	@Override
	public String toString() {
		return "date:"+(getDate() == null ? "" : getDateDesc())+",sTime:"+(getSTime() == null ? "" : getSTimeDesc())+",eTime:"+(getETime() == null ? "" : getETimeDesc())+",DayTags:"+(dayTags == null ? "" : dayTags)+",remark:"+(getRemark() == null ? "" : getRemark())+"";
	}
	
}
