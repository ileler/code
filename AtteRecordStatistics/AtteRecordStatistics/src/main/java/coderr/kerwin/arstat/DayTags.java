package coderr.kerwin.arstat;

import java.io.Serializable;
import java.util.Date;

public class DayTags implements Serializable {

	private static final long serialVersionUID = 5876499490196571992L;
	
	private Date date;
	
	private Integer id;
	
	private String title;
	
	private Boolean holiday;
	
	private Boolean weekday;
	
	public DayTags() {}
	
	public DayTags(Date date) {
		super();
		this.date = date;
	}

	public DayTags(Date date, Integer id, String title, Boolean holiday, Boolean weekday) {
		super();
		this.id = id;
		this.date = date;
		this.title = title;
		this.holiday = holiday;
		this.weekday = weekday;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getHoliday() {
		return holiday;
	}

	public void setHoliday(Boolean holiday) {
		this.holiday = holiday;
	}

	public Boolean getWeekday() {
		return weekday;
	}

	public void setWeekday(Boolean weekday) {
		this.weekday = weekday;
	}
	
	@Override
	public String toString() {
		return "id:"+(getId() == null ? "" : getId())+",title:"+(getTitle() == null ? "" : getTitle())+",holiday:"+(getHoliday() == null ? "" : getHoliday())+",weekday:"+(getWeekday() == null ? "" : getWeekday())+"";
	}

}
