package org.kerwin.task;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Kerwin Bryant
 * 任务实体类
 */
public abstract class KerwinTasker implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;				//ID
	private String name;			//任务名称
	private String execTime;		//执行时间
	private int execCount;			//执行次数
	private boolean status;			//任务状态
	private Date createtime;		//创建时间
	private Date updatetime;		//更新时间
	private Date nextExecTime;		//下一次执行时间
	private Date lastExecTime;		//最近一次执行时间
	private String taskerClass;		//任务执行类
	private Date lastExecEndTime;	//最近一次执行结束时间
	private String lastExecStatus;	//最近一次执行状态
	private Date lastExecBeginTime;	//最近一次执行开始时间
	private static final String HMS = "HH:mm:ss";
	private static final String YMD = "yyyy-MM-dd";
	private static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
//	public abstract boolean checkExecTime();
	
	/**
	 * 更新任务实体类
	 */
	public abstract void updateKerwinTasker();
	
	/**
	 * @return 返回下一次执行时间
	 * @throws Exception
	 */
	public abstract Date calculateNextExecTime() throws Exception;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getTaskerClass() {
		return taskerClass;
	}
	public void setTaskerClass(String taskerClass) {
		this.taskerClass = taskerClass;
	}
	public String getExecTime() {
		return execTime;
	}
	public void setExecTime(String execTime) {
		if(execTime != null && !execTime.isEmpty()){
			if((execTime = getTime(execTime)) != null){
				this.execTime = execTime;
			}
		}
	}
	public int getExecCount() {
		return execCount;
	}
	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}
	public Date getNextExecTime() {
		return nextExecTime;
	}
	public void setNextExecTime(Date nextExecTime) {
		this.nextExecTime = nextExecTime;
	}
	public Date getLastExecTime() {
		return lastExecTime;
	}
	public void setLastExecTime(Date lastExecTime) {
		this.lastExecTime = lastExecTime;
	}
	public Date getLastExecBeginTime() {
		return lastExecBeginTime;
	}
	public void setLastExecBeginTime(Date lastExecBeginTime) {
		this.lastExecBeginTime = lastExecBeginTime;
	}
	public Date getLastExecEndTime() {
		return lastExecEndTime;
	}
	public void setLastExecEndTime(Date lastExecEndTime) {
		this.lastExecEndTime = lastExecEndTime;
	}
	public String getLastExecStatus() {
		return lastExecStatus;
	}
	public void setLastExecStatus(String lastExecStatus) {
		this.lastExecStatus = lastExecStatus;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	@Override
	public String toString(){
		return  "##############################\n"+
				"#\tKerwinTasker:["+this.getName()+"]\n" +
				"#\t\tid:                      "+this.getId()+"\n"+
				"#\t\tname:                    "+this.getName()+"\n"+
				"#\t\tstatus:                  "+this.getStatus()+"\n"+
				"#\t\texecTime:                "+this.getExecTime()+"\n"+
				"#\t\texecCount:               "+this.getExecCount()+"\n"+
				"#\t\ttaskerClass:             "+this.getTaskerClass()+"\n"+
				"#\t\tlastExecStatus:          "+this.getLastExecStatus()+"\n"+
				"#\t\tnextExecTime:            "+KerwinTasker.getDatetime(this.getNextExecTime())+"\n"+
				"#\t\tlastExecTime:            "+KerwinTasker.getDatetime(this.getLastExecTime())+"\n"+
				"#\t\tlastExecBeginTime:       "+KerwinTasker.getDatetime(this.getLastExecBeginTime())+"\n"+
				"#\t\tlastExecEndTime:         "+KerwinTasker.getDatetime(this.getLastExecEndTime())+"\n"+
				"#\t\tcreatetime:              "+KerwinTasker.getDatetime(this.getCreatetime())+"\n"+
				"#\t\tupdatetime:              "+KerwinTasker.getDatetime(this.getUpdatetime())+"\n"+
				"##############################";
	}
	
	/**
	 * 转换KerwinTasker子类类型为另一个子类、会丢弃扩展信息
	 * @param cs
	 * @param kt
	 * @return
	 */
	public static KerwinTasker convertKerwinTasker(Class<? extends KerwinTasker> cs,KerwinTasker kt){
		if(cs == null || kt == null)	return null;
		KerwinTasker tmpKT = null;
		try {
			tmpKT = cs.newInstance();
			tmpKT.setId(kt.getId());
			tmpKT.setName(kt.getName());
			tmpKT.setStatus(kt.getStatus());
			tmpKT.setTaskerClass(kt.getTaskerClass());
			tmpKT.setExecTime(kt.getExecTime());
			tmpKT.setExecCount(kt.getExecCount());
			tmpKT.setNextExecTime(kt.getNextExecTime());
			tmpKT.setLastExecTime(kt.getLastExecTime());
			tmpKT.setLastExecBeginTime(kt.getLastExecBeginTime());
			tmpKT.setLastExecEndTime(kt.getLastExecEndTime());
			tmpKT.setLastExecStatus(kt.getLastExecStatus());
			tmpKT.setCreatetime(kt.getCreatetime());
			tmpKT.setUpdatetime(kt.getUpdatetime());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return tmpKT;
	}
	
	public static String getTime(String time){
		try {
			return (time != null && !time.isEmpty()) ? new SimpleDateFormat(HMS).format(new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(new Date()) + " " + time)) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getDatetime(String datetime){
		try {
			return (datetime != null && !datetime.isEmpty()) ? new SimpleDateFormat(YMDHMS).parse(datetime) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDatetime(Date datetime){
		return (datetime != null) ? new SimpleDateFormat(YMDHMS).format(datetime) : null;
	}
	
	public static int compareDatetime(Date datetime){
		return compareDatetime(KerwinTasker.getDatetime(datetime),new SimpleDateFormat(YMDHMS).format(new Date()));
	}
	
	public static int compareDatetime(String datetime){
		return compareDatetime(datetime,new SimpleDateFormat(YMDHMS).format(new Date()));
	}
	
	public static int compareDatetime(String datetime1, String datetime2){
		if(datetime1 == null || datetime1.isEmpty() || datetime2 == null || datetime2.isEmpty())	return -99;
		try {
			return new SimpleDateFormat(YMDHMS).parse(datetime1).compareTo(new SimpleDateFormat(YMDHMS).parse(datetime2));
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		return -99;
	}
	
	/**
	 * 格式化命令字符串
	 * @param command
	 * @return	返回格式化后的命令集
	 */
	public static List<String> formatCommand(String command){
		/* 处理命令成exec所需的格式-开始 */
		List<String> cmdarray = new ArrayList<String>();
		int i = -1;
		while((i = command.indexOf(" ")) != -1){
			if(command.substring(0,i).indexOf("\"")!=-1){
				if(command.substring(0,i).indexOf("\"")!=command.substring(0,i).lastIndexOf("\"")){
					cmdarray.add(command.substring(command.substring(0,i).indexOf("\""),command.substring(0,i).lastIndexOf("\"")+1));
					if(command.length() > command.substring(0,i).lastIndexOf("\"")+1)
						command = command.substring(command.substring(0,i).lastIndexOf("\"")+2);
					else
						command = "";
				}else{
					cmdarray.add(command.substring(command.substring(0,i).indexOf("\""),command.substring(i).indexOf("\"")+i+1));
					if(command.length() > command.substring(i).indexOf("\"")+i+1)
						command = command.substring(command.substring(i).indexOf("\"")+i+2);
					else
						command = "";
				}
				continue;
			}
			cmdarray.add(command.substring(0,i));
			if(command.length() > i)
				command = command.substring(i+1);
			else
				command = "";
		}
		if(command.length() > 0)
			cmdarray.add(command);
		/* 处理命令成exec所需的格式-结束 */
		return cmdarray;
	}
	
	/**
	 * @author kerwin
	 * 任务类型：指定每天
	 */
	public static abstract class KerwinTaskerType1 extends KerwinTasker{
		private static final long serialVersionUID = 1L;
		@Override
		public Date calculateNextExecTime() throws Exception{
			if(super.execTime == null)	throw new Exception("execTime is null");
			Date nextExecTime = new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(new Date()) + " " + super.execTime);
			if(nextExecTime == null)	return null;
			if(KerwinTasker.compareDatetime(nextExecTime) < 0){
				Calendar c = Calendar.getInstance();
				c.setTime(nextExecTime);
		        c.add(Calendar.DAY_OF_YEAR, 1);
				return c.getTime();
			}
			return nextExecTime;
		}
//		@Override
//		public boolean checkExecTime() {
//			return KerwinTasker.compareDatetime(new SimpleDateFormat(YMD).format(new Date()) + " " + getExecTime()) == 0 ? true : false;
//		}
	}
	
	/**
	 * @author kerwin
	 * 任务类型：指定某天
	 */
	public static abstract class KerwinTaskerType2 extends KerwinTasker{
		private static final long serialVersionUID = 1L;
		private String date;
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		@Override
		public Date calculateNextExecTime() throws Exception{
			if(super.execTime == null)	throw new Exception("execTime is null");
			if(date == null || date.isEmpty())	throw new Exception("date is null");
			Date nextExecTime = KerwinTasker.getDatetime(date+" "+super.execTime);
			if(nextExecTime == null)	return null;
			if(KerwinTasker.compareDatetime(nextExecTime) < 0){
				return null;
			}
			return nextExecTime;
		}
//		@Override
//		public boolean checkExecTime() {
//			if(KerwinTasker.compareDatetime(new SimpleDateFormat(YMD).format(new Date()) + " " + getExecTime()) == 0 && (month > 0) && (day > 0)){
//				return Calendar.getInstance().get(Calendar.MONTH) == month && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day ? true : false;
//			}
//			return false;
//		}
	}
	
	/**
	 * @author kerwin
	 * 任务类型：指定星期
	 */
	public static abstract class KerwinTaskerType3 extends KerwinTasker{
		private static final long serialVersionUID = 1L;
		private Integer[] weeks;
		public Integer[] getWeeks() {
			return weeks;
		}
		public void setWeeks(Integer[] weeks) {
			this.weeks = weeks;
		}
		@Override
		public Date calculateNextExecTime() throws Exception{
			if(super.execTime == null)	throw new Exception("execTime is null");
			if(weeks == null || weeks.length < 1)	throw new Exception("weeks is null");
			Date nextExecTime = new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(new Date()) + " " + super.execTime);
			if(nextExecTime == null)	return null;
			Arrays.sort(weeks);
			Calendar c = Calendar.getInstance();
			int cw = c.get(Calendar.DAY_OF_WEEK)-1;
			for(int i = 0, j = weeks.length; i < j; i++){
				if(weeks[i] < cw && i != j-1)	continue;
				if(weeks[i] < cw && i == j-1){
					c.add(Calendar.DAY_OF_YEAR, 7 - cw + weeks[0]);
					return KerwinTasker.getDatetime(new SimpleDateFormat(YMD).format(c.getTime())+" "+super.execTime);
				}
				c.add(Calendar.DAY_OF_YEAR, weeks[i]-cw);
				if(KerwinTasker.compareDatetime(nextExecTime = KerwinTasker.getDatetime(new SimpleDateFormat(YMD).format(c.getTime())+" "+super.execTime)) < 0){
					if(i != j-1)	continue;
					c.add(Calendar.DAY_OF_YEAR, 7 - cw + weeks[0]);
					return KerwinTasker.getDatetime(new SimpleDateFormat(YMD).format(c.getTime())+" "+super.execTime);
				}
				return nextExecTime;
			}
			return null;
		}
//		@Override
//		public boolean checkExecTime() {
//			if(KerwinTasker.compareDatetime(new SimpleDateFormat(YMD).format(new Date()) + " " + getExecTime()) == 0 && (weeks != null && !weeks.isEmpty())){
//				return Arrays.asList(weeks.split(",")).contains(Calendar.getInstance().get(Calendar.WEDNESDAY));
//			}
//			return false;
//		}
	}
	
	/**
	 * @author kerwin
	 * 任务类型：指定每号
	 */
	public static abstract class KerwinTaskerType4 extends KerwinTasker{
		private static final long serialVersionUID = 1L;
		private int day;
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		@Override
		public Date calculateNextExecTime() throws Exception{
			if(super.execTime == null)	throw new Exception("execTime is null");
			if(day < 0 || day > 31)	throw new Exception("day is error");
			Date nextExecTime = new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(new Date()) + " " + super.execTime);
			if(nextExecTime == null)	return null;
			Calendar c = Calendar.getInstance();
			int cd = c.get(Calendar.DAY_OF_MONTH);
			if(cd <= day && KerwinTasker.compareDatetime(nextExecTime) > -1){
				return nextExecTime;
			}
			for(int i = c.get(Calendar.MONTH) + 1 > 11 ? 0 : c.get(Calendar.MONTH) + 1; i < 13; i++){
				if(i == 0){
					c.add(Calendar.YEAR, 1);
				}
				c.set(Calendar.MONTH, i);
				c.set(Calendar.DAY_OF_MONTH, day);
				if(KerwinTasker.compareDatetime(nextExecTime = new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(c.getTime()) + " " + super.execTime)) > -1)	return nextExecTime;
				if(i == 12)	i = 1;
			}
			return null;
		}
//		@Override
//		public boolean checkExecTime() {
//			if(KerwinTasker.compareDatetime(new SimpleDateFormat(YMD).format(new Date()) + " " + getExecTime()) == 0 && (day > 0)){
//				return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day ? true : false;
//			}
//			return false;
//		}
	}
	
	/**
	 * @author kerwin
	 * 任务类型：间隔时间
	 */
	public static abstract class KerwinTaskerType5 extends KerwinTasker{
		private static final long serialVersionUID = 1L;
		public enum IntervalType{
			H,M,S;
		}
		private IntervalType intervalType;
		private int interval;
		public IntervalType getIntervalType() {
			return intervalType;
		}
		public void setIntervalType(IntervalType intervalType) {
			this.intervalType = intervalType;
		}
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
		@Override
		public Date calculateNextExecTime() throws Exception{
			if(interval < 0)	throw new Exception("interval is null");
			if(super.execTime == null)	throw new Exception("execTime is null");
			if(intervalType == null)	throw new Exception("intervalType is null");
			Date nextExecTime = null;
			if(getNextExecTime() == null){
				nextExecTime = new SimpleDateFormat(YMDHMS).parse(new SimpleDateFormat(YMD).format(new Date()) + " " + super.execTime);
			}else{
				nextExecTime = getNextExecTime();
			}
			if(nextExecTime == null)	return null;
			if(KerwinTasker.compareDatetime(nextExecTime) < 0){
				Calendar c = Calendar.getInstance();
				c.setTime(nextExecTime);
				int i;
				switch(intervalType){
					case H:
						i = Calendar.HOUR;
						c.add(Calendar.HOUR, interval);
						break;
					case M:
						i = Calendar.MINUTE;
						c.add(Calendar.MINUTE, interval);
						break;
					case S:
					default:
						i = Calendar.SECOND;
						c.add(Calendar.SECOND, interval);
				}
				while(KerwinTasker.compareDatetime(nextExecTime = c.getTime()) < 0){
					c.setTime(nextExecTime);
					c.add(i, interval);
					nextExecTime = c.getTime();
				}
				return nextExecTime;
			}
			return nextExecTime;
		}
//		@Override
//		public boolean checkExecTime() {
//			Calendar oc = Calendar.getInstance();
//			Calendar nc = Calendar.getInstance();
//			Date lastExecTime,updatetime,createtime;
//			if((lastExecTime = getLastExecTime()) != null){
//				nc.setTimeInMillis(lastExecTime.getTime());
//			}else if((updatetime = getUpdatetime()) != null){
//				nc.setTimeInMillis(updatetime.getTime());
//			}else if((createtime = getCreatetime()) != null){
//				nc.setTimeInMillis(createtime.getTime());
//			}else{
//				return false;
//			}
//			if(intervalType == IntervalType.H && (oc.getTimeInMillis() - nc.getTimeInMillis() >= interval * 60 * 60 * 1000)){
//				return true;
//			}else if(intervalType == IntervalType.M && (oc.getTimeInMillis() - nc.getTimeInMillis() >= interval * 60 * 1000)){
//				return true;
//			}else if(intervalType == IntervalType.S && (oc.getTimeInMillis() - nc.getTimeInMillis() >= interval * 1000)){
//				return true;
//			}else{
//				return false;
//			}
//		}
	}
	
}
