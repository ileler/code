package coderr.kerwin.arstat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

class StreamToStringS{  
    public static String ConvertToString(InputStream inputStream){  
    	StringBuilder result = new StringBuilder();  
        InputStreamReader inputStreamReader = null;  
        BufferedReader bufferedReader = null;  
        String line = null;  
        try {  
        	inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        	bufferedReader = new BufferedReader(inputStreamReader);
            while((line = bufferedReader.readLine()) != null){  
                result.append(line + "\n");  
            }  
            return result.toString();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try{  
            	if (inputStreamReader != null)
            		inputStreamReader.close();
        		if (bufferedReader != null)
        			bufferedReader.close();  
        		if (inputStream != null)
        			inputStream.close();  
            }catch(IOException e){  
                e.printStackTrace();  
            }  
        } 
        return null;
    }  
  
  
    public static String ConvertToString(FileInputStream inputStream){  
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);  
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
        StringBuilder result = new StringBuilder();  
        String line = null;  
        try {  
            while((line = bufferedReader.readLine()) != null){  
                result.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try{  
                inputStreamReader.close();  
                inputStream.close();  
                bufferedReader.close();  
            }catch(IOException e){  
                e.printStackTrace();  
            }  
        }  
        return result.toString();  
    }  
}

/**
 * 考勤信息数据操作抽象类
 * @author kerwin612
 */
public abstract class DayTagsDAO {
	
	/**
	 * 文件名称模板
	 */
	private static final String FILENAMEPATTERN = "yyyy";
	
	/**
	 * 数据ID模板 
	 */
	private static final String DATAIDPATTERN = "yyyyMMdd";
	
	/**
	 * 数据后缀 
	 */
	private static final String FILESUFFIX = ".dat";
	
	/**
	 * 根据日期返回数据所属文件名称
	 * @param date
	 * @return
	 */
	protected String getDatFileName(Date date) {
		return (date == null) ? null : new SimpleDateFormat(FILENAMEPATTERN).format(date) + FILESUFFIX;
	}
	
	/**
	 * 根据日期返回数据所属实体ID
	 * @param date
	 * @return
	 */
	protected Integer getBeanId(Date date) {
		return (date == null) ? null : Integer.valueOf(new SimpleDateFormat(DATAIDPATTERN).format(date));
	}
	
	/**
	 * 根据考勤信息实体类返回数据所属文件名称
	 * @param bean
	 * @return
	 */
	protected String getDatFileName(DayTags bean) {
		return (bean == null || bean.getDate() == null) ? null : getDatFileName(bean.getDate());
	}
	
	/**
	 * 保存考勤数据的方法、由具体子类实现
	 * @param bean
	 */
	public abstract void save(List<DayTags> beans);
	
	/**
	 * 根据日期返回当月考勤数据Map。key为数据ID
	 * @param date
	 * @return
	 */
	public abstract Map<Integer, DayTags> getDayTagsMap(Date date);
	
	/**
	 * 根据日期返回当月考勤数据List
	 * @param date
	 * @return
	 */
	public List<DayTags> getDayTagsList(Date date) {
		Map<Integer, DayTags> map = null;
		return (map = getDayTagsMap(date)) == null ? null : new ArrayList<DayTags>(map.values());
	}
	
	/**
	 * 根据日期返回当天考勤数据
	 * @param date
	 * @return
	 */
	public DayTags getDayTags(Date date) {
		Integer id = getBeanId(date);
		Map<Integer, DayTags> map = getDayTagsMap(date);
		return (map == null || map.size() < 1 || id == null || !map.containsKey(id)) ? new DayTags(date) : map.get(id);
	}
	
	public void synDayTags(Date date) {
		if (date == null) 	return;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			List<DayTags> list = new ArrayList<DayTags>();
			URL url = new URL("http://coderr.sinaapp.com/daytags.php?api&zl&g&gt=" + (year+"0101") + "&lt=" + (year+"1231"));
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();  
			//GET Request Define:   
			urlConnection.setRequestMethod("GET");  
			urlConnection.connect();  
			//Connection Response From Test Servlet  
			InputStream inputStream = urlConnection.getInputStream();  
			
			//Convert Stream to String  
			String responseStr = StreamToStringS.ConvertToString(inputStream);  
			if (responseStr != null && !"".equals(responseStr) && !"none".equalsIgnoreCase(responseStr.trim())) {
				String[] ss = (responseStr = responseStr.substring(1)).substring(0,responseStr.length() - 2).split("<<,>>");
				for (int i = 0, j = ss.length; i < j; i++) {
					String[] _ss = ss[i].split("<<>>");
					if (ss[i] == null || (_ss = ss[i].split("<<>>")).length != 4) continue;
					DayTags dayTags = new DayTags(new SimpleDateFormat(DATAIDPATTERN).parse(_ss[0]), Integer.valueOf(_ss[0]), _ss[1].trim(), "1".equals(_ss[2]), "1".equals(_ss[3]));
					list.add(dayTags);
				}
				save(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
