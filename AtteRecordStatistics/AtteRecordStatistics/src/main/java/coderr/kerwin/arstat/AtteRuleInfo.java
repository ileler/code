package coderr.kerwin.arstat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * 考勤规则信息类
 * @author kewrin612
 */
public class AtteRuleInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 月考勤开始日
	 */
	private static final String SDAY = "sDay";
	private static final Integer SDAYVALUE = 1;
	
	/**
	 * 考勤上班时间
	 */
	private static final String STIME = "sTime";
	private static final String STIMEVALUE = "08:30";
	
	/**
	 * 休息开始时间
	 */
	private static final String PTIME = "pTime";
	private static final String PTIMEVALUE = "12:00";
	
	/**
	 * 休息结束时间
	 */
	private static final String RTIME = "rTime";
	private static final String RTIMEVALUE = "13:30";
	
	/**
	 * 考勤下班时间
	 */
	private static final String ETIME = "eTime";
	private static final String ETIMEVALUE = "18:00";
	
	/**
	 * 加班开始时间
	 */
	private static final String OTIME = "oTime";
	private static final String OTIMEVALUE = "19:00";
	
	/**
	 * 配置文件
	 */
	private static final String FILEPATH = "./res/AtteRule.zl";
	
	private static Properties properties;
	
	static {
		load();
	}
	
	private AtteRuleInfo() {}
	
	/**
	 * 加载配置信息
	 */
	public static void load() {
		try {
			if (properties == null)	properties = new Properties();
			File file = new File(FILEPATH);
			if (!file.exists())	{
				file.getParentFile().mkdirs();
				setSDay(SDAYVALUE);
				setSTime(STIMEVALUE);
				setPTime(PTIMEVALUE);
				setRTime(RTIMEVALUE);
				setETime(ETIMEVALUE);
				setOTime(OTIMEVALUE);
				save();
			} else {
				properties.load(new FileInputStream(file));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存配置信息
	 */
	public static void save() {
		try {
			properties.store(new FileOutputStream(new File(FILEPATH)), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回月考勤开始日
	 * @return
	 */
	public static Integer getSDay() {
		String str = properties.getProperty(SDAY);
		return isEmpty(str) ? SDAYVALUE : Integer.valueOf(str);
	}

	/**
	 * 设置月考勤开始日
	 * @param sDay
	 */
	public static void setSDay(Integer sDay) {
		properties.setProperty(SDAY, String.valueOf(sDay == null ? SDAYVALUE : sDay));
	}

	/**
	 * 返回考勤上班时间
	 * @return
	 */
	public static String getSTime() {
		String str = properties.getProperty(STIME);
		return (isEmpty(str) ? STIMEVALUE : str) + ":00";
	}

	/**
	 * 设置考勤上班时间
	 * @param sTime
	 */
	public static void setSTime(String sTime) {
		properties.setProperty(STIME, isTime(sTime, STIMEVALUE));
	}

	/**
	 * 返回休息开始时间
	 * @return
	 */
	public static String getPTime() {
		String str = properties.getProperty(PTIME);
		return (isEmpty(str) ? PTIMEVALUE : str) + ":00";
	}

	/**
	 * 设置休息开始时间
	 * @param pTime
	 */
	public static void setPTime(String pTime) {
		properties.setProperty(PTIME, isTime(pTime, PTIMEVALUE));
	}

	/**
	 * 返回休息结束时间
	 * @return
	 */
	public static String getRTime() {
		String str = properties.getProperty(RTIME);
		return (isEmpty(str) ? RTIMEVALUE : str) + ":00";
	}

	/**
	 * 设置休息结束时间
	 * @param rTime
	 */
	public static void setRTime(String rTime) {
		properties.setProperty(RTIME, isTime(rTime, RTIMEVALUE));
	}

	/**
	 * 返回考勤下班时间
	 * @return
	 */
	public static String getETime() {
		String str = properties.getProperty(ETIME);
		return (isEmpty(str) ? ETIMEVALUE : str) + ":00";
	}

	/**
	 * 设置考勤下班时间
	 * @param eTime
	 */
	public static void setETime(String eTime) {
		properties.setProperty(ETIME, isTime(eTime, ETIMEVALUE));
	}

	/**
	 * 返回加班开始时间
	 * @return
	 */
	public static String getOTime() {
		String str = properties.getProperty(OTIME);
		return (isEmpty(str) ? OTIMEVALUE : str) + ":00";
	}

	/**
	 * 设置加班开始时间
	 * @param oTime
	 */
	public static void setOTime(String oTime) {
		properties.setProperty(OTIME, isTime(oTime, OTIMEVALUE));
	}
	
	private static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	
	private static String isTime(String time, String defa) {
		String[] ss = null;
		if (time == null || (ss = time.split(":")).length < 2)	return defa;
		if (Integer.valueOf(ss[0]) < 0 || Integer.valueOf(ss[0]) > 59)	return defa;
		if (Integer.valueOf(ss[1]) < 0 || Integer.valueOf(ss[1]) > 59)	return defa;
		return ss[0] + ":" + ss[1];
	}
	
}
