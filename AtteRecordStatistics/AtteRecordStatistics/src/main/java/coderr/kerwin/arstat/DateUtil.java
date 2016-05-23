package coderr.kerwin.arstat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * @author kerwin612
 */
public class DateUtil {
	
	public static boolean isSameDay(Date day1, Date day2) {
		if (day1 == null || day2 == null)	return false;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(day1).equals(sdf.format(day2));
	}
	
	/**
	 * 返回当前年份
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();  
        return cal.get(Calendar.YEAR);
	}
	
	/**
	 * 返回当前月份
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();  
        return cal.get(Calendar.MONTH) + 1;
	}
	
    /**
     * 获取当月的天数 
     * @return
     */
    public static int getDaysByCurrentMonth() {  
        Calendar cal = Calendar.getInstance();  
        cal.set(Calendar.DATE, 1);  
        cal.roll(Calendar.DATE, -1);  
        return cal.get(Calendar.DATE);  
    }  
	
	/**
	 * 根据年月获取对应的月份天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysByYearMonth(int year, int month) {
		if (year == 0 || month == 0)	return 0;
		Calendar cal = Calendar.getInstance(); 
        cal.set(Calendar.YEAR, year);  
        cal.set(Calendar.MONTH, month - 1);  
        cal.set(Calendar.DATE, 1);  
        cal.roll(Calendar.DATE, -1);  
        return cal.get(Calendar.DATE);
	}
	
	/**
	 * 根据日期找到对应日期的星期 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeekByDate(int year, int month) {  
        try {
			return getDayOfWeekByDate(new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-01"));
		} catch (ParseException e) {
			e.printStackTrace();
		}  
        return 0;  
    } 
	
	/**
	 * 根据日期找到对应日期的星期 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeekByDate(String date) {  
		try {
			return getDayOfWeekByDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return 0;  
	} 

	/**
	 * 根据日期找到对应日期的星期
	 * @param date
	 * @return
	 */
	public static int getDayOfWeekByDate(Date date) { 
		if (date == null)	return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
	
	/**
	 * 根据时间返回日期对象
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date getDateByTime(Date date, String time) {
		if (date == null || time == null)	return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd").format(date) + " " +time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isWeekend(Date date) {
		if (date == null) return false;
		int i = getDayOfWeekByDate(date);
		return i == 7 || i == 1;
	}
	
}
