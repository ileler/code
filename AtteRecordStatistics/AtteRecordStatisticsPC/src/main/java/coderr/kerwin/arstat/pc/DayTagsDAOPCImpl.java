package coderr.kerwin.arstat.pc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import coderr.kerwin.arstat.DayTags;
import coderr.kerwin.arstat.DayTagsDAO;

/**
 * 考勤信息数据操作PC端实现类
 * @author kerwin612
 */
public class DayTagsDAOPCImpl extends DayTagsDAO {
    
	/**
	 * 考勤数据文件所存路径
	 */
	private static final String DATPATH = "./dat/" + new SimpleDateFormat("yyyy/").format(new Date());
	
	/**
	 * 考勤数据Map，key为文件名、value的map为当月考勤数据 
	 */
	private static Map<String, Map<Integer, DayTags>> datas;
	
	static {
		datas = new Hashtable<String, Map<Integer, DayTags>>();
	}
	
	/**
	 * 根据指定日期加载考勤数据到Map
	 * @param date
	 */
	private void load(Date date) {
		String fileName = getDatFileName(date);
		datas.put(fileName, read(DATPATH + fileName));
	}
	
	/* (non-Javadoc)
	 * @see coderr.kerwin.arstat.AtteRecordDAO#save(coderr.kerwin.arstat.AtteRecordBean)
	 */
	@Override
	public void save(List<DayTags> beans) {
		String fileName = null;
		Integer dataId = null;
		if (beans == null || beans.size() < 1) 	return;
		for (int i = 0,j = beans.size(); i < j; i++) {
			DayTags bean = beans.get(i);
			if ((fileName = getDatFileName(bean)) == null || (dataId = bean.getId()) == null)	return;
			if (!datas.containsKey(fileName))	load(bean.getDate());
			datas.get(fileName).put(dataId, bean);
			write(DATPATH + fileName, datas.get(fileName));
		}
	}
	
	/* (non-Javadoc)
	 * @see coderr.kerwin.arstat.AtteRecordDAO#getAtteRecordBeanMap(java.util.Date)
	 */
	@Override
	public Map<Integer, DayTags> getDayTagsMap(Date date) {
		String fileName = null;
		if ((fileName = getDatFileName(date)) == null)	return null;
		if (!datas.containsKey(fileName))	load(date);
		return datas.get(fileName);
	}
	
	/**
	 * 根据文件名读取数据集合
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<Integer, DayTags> read(String fileName) {
		ObjectInputStream ois = null;
		File file = null;
		try {
			if (fileName == null || !(file = new File(fileName)).exists())	return new Hashtable<Integer, DayTags>(0);
			ois = new ObjectInputStream(new FileInputStream(file));
			Object obj = ois.readObject();
			if (obj == null)	return new Hashtable<Integer, DayTags>(0);
			return (Map<Integer, DayTags>) obj;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return new Hashtable<Integer, DayTags>(0);
	}
	
	/**
	 * 将数据保存到指定的文件中
	 * @param fileName
	 * @param datas
	 * @return
	 */
	private static boolean write(String fileName, Map<Integer, DayTags> datas) {
		ObjectOutputStream oos = null;
		File file = null;
		try {
			if (fileName == null)	return false;
			if (!(file = new File(fileName)).getParentFile().exists())	file.getParentFile().mkdirs();
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(datas);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}

}
