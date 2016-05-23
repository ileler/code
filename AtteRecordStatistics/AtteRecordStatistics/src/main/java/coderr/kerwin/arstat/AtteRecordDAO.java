package coderr.kerwin.arstat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


/**
 * 考勤信息数据操作抽象类
 * @author kerwin612
 */
public abstract class AtteRecordDAO {
    
	/**
	 * 文件名称模板
	 */
	private static final String FILENAMEPATTERN = "yyyyMM";
	
	/**
	 * 数据ID模板 
	 */
	private static final String DATAIDPATTERN = "yyyyMMdd";
	
	/**
	 * 数据后缀 
	 */
	private static final String FILESUFFIX = ".dat";
	
	private DayTagsDAO dayTagsDAO = null;
	
	public AtteRecordDAO(DayTagsDAO dayTagsDAO) {
		this.dayTagsDAO = dayTagsDAO;
	}
	
	/**
	 * 根据日期返回数据所属文件名称
	 * @param date
	 * @return
	 */
	protected String getDatFileName(Date date) {
		return (date == null) ? null : new SimpleDateFormat("yyyy/").format(date) + new SimpleDateFormat(FILENAMEPATTERN).format(date) + FILESUFFIX;
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
	protected String getDatFileName(AtteRecordBean bean) {
		return (bean == null || bean.getDate() == null) ? null : getDatFileName(bean.getDate());
	}
	
	/**
	 * 根据考勤信息实体类返回数据ID
	 * @param bean
	 * @return
	 */
	protected Integer getBeanId(AtteRecordBean bean) {
		return (bean == null || bean.getDate() == null) ? null : getBeanId(bean.getDate());
	}
	
	/**
	 * 保存考勤数据的方法、由具体子类实现
	 * @param bean
	 */
	public abstract void save(AtteRecordBean bean);
	
	/**
	 * 根据日期返回当月考勤数据Map。key为数据ID
	 * @param date
	 * @return
	 */
	public abstract Map<Integer, AtteRecordBean> getAtteRecordBeanMap(Date date);
	
	/**
	 * 根据日期返回当月考勤数据List
	 * @param date
	 * @return
	 */
	public List<AtteRecordBean> getAtteRecordBeanList(Date date) {
		Map<Integer, AtteRecordBean> map = null;
		return (map = getAtteRecordBeanMap(date)) == null ? null : new ArrayList<AtteRecordBean>(map.values());
	}
	
	/**
	 * 根据日期返回当天考勤数据
	 * @param date
	 * @return
	 */
	public AtteRecordBean getAtteRecordBean(Date date) {
		Integer id = getBeanId(date);
		Map<Integer, AtteRecordBean> map = getAtteRecordBeanMap(date);
		return (map == null || map.size() < 1 || id == null || !map.containsKey(id)) ? new AtteRecordBean(date) : map.get(id);
	}
	
	/**
	 * 根据指定年月返回当月(1号开始算、且头尾会填充上下月数据)考勤统计数据集合
	 * @param year
	 * @param month
	 * @return
	 */
	public List<AtteRecordStatBean> getAtteRecordCalendar(int year, int month) {
		return getAtteRecordCalendar(year, month, false, true);
	}
	
	/**
	 * 根据指定年月返回当月(1号开始算、且头尾会填充上下月数据)考勤统计数据集合
	 * @param year
	 * @param month
	 * @return
	 */
	public List<AtteRecordStatBean> getAtteRecordCalendar(int year, int month, boolean isRuleSDay) {
		return getAtteRecordCalendar(year, month, isRuleSDay, true);
	}
	
	/**
	 * 根据指定年月返回当月考勤统计数据集合
	 * @param year		年份
	 * @param month		月份
	 * @param isRuleSDay	是否从规则考勤开始日算
	 * @param hasGray		是否需要填充头尾数据
	 * @return
	 */
	public List<AtteRecordStatBean> getAtteRecordCalendar(int year, int month, boolean isRuleSDay, boolean hasGray) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-" + (isRuleSDay ? AtteRuleInfo.getSDay() : "01"));
			AtteRecordBean arb = null;
			Calendar cal = Calendar.getInstance();
			int dayOfWeek = DateUtil.getDayOfWeekByDate(date);
			List<AtteRecordStatBean> list = new ArrayList<AtteRecordStatBean>();
			for (int i = 1, j = dayOfWeek; hasGray && i < j; i++) {
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_YEAR, -(j - i));
				arb = getAtteRecordBean(cal.getTime());
				arb.setDayTags(dayTagsDAO.getDayTags(arb.getDate()));
				list.add(AtteRecordStatBean.getInstance(arb, true));
			}
			cal.setTime(date);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date edate = cal.getTime();
			cal.setTime(date);
			do {
				arb = getAtteRecordBean(date = cal.getTime());
				arb.setDayTags(dayTagsDAO.getDayTags(arb.getDate()));
				list.add(AtteRecordStatBean.getInstance(arb));
				cal.add(Calendar.DAY_OF_YEAR, 1);
			} while(date.before(edate));
//			System.out.println((7 - (list.size() % 7)) % 7);
			for (int i = 1, j = 6*7 - list.size(); hasGray && i <= j; i++) {
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_YEAR, i);
				arb = getAtteRecordBean(cal.getTime());
				arb.setDayTags(dayTagsDAO.getDayTags(arb.getDate()));
				list.add(AtteRecordStatBean.getInstance(arb, true));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File exportData(Date date, List<AtteRecordStatBean> list) {
		if (date == null || list == null || list.size() < 1)	return null;
		String[] headers = {"日期","签到时间","签退时间","迟到","早退","加班","备注"};
		String title = new SimpleDateFormat("yyyy年MM月考勤统计").format(date);
		File file = new File(title + ".xls");
		OutputStream os = null;
		HSSFWorkbook workbook = null;
		try {
			os = new FileOutputStream(file);
			// 声明一个工作薄
			workbook = new HSSFWorkbook();
			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet(title);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth(15);
			// 生成一个样式
			HSSFCellStyle headerStyle = workbook.createCellStyle();
			// 设置这些样式
			headerStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			// 把字体应用到当前的样式
			headerStyle.setFont(font);
			
			// 生成另一个字体
			HSSFFont weightFont = workbook.createFont();
			weightFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			
			// 生成并设置另一个样式
			HSSFCellStyle yellowStyle = workbook.createCellStyle();
			yellowStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
			yellowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			yellowStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			yellowStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			yellowStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			yellowStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			yellowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			yellowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 把字体应用到当前的样式
			yellowStyle.setFont(weightFont);
			
			// 生成并设置另一个样式
			HSSFCellStyle greyStyle = workbook.createCellStyle();
			greyStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			greyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			greyStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			greyStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			greyStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			greyStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			greyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			greyStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 把字体应用到当前的样式
			yellowStyle.setFont(weightFont);
			
			// 生成并设置另一个样式
			HSSFCellStyle whiteStyle = workbook.createCellStyle();
			whiteStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			whiteStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			whiteStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			whiteStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			whiteStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			whiteStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			whiteStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			whiteStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			
			HSSFFont pinkFont = workbook.createFont();
			pinkFont.setColor(HSSFColor.PINK.index);
			
			HSSFFont greyFont = workbook.createFont();
			greyFont.setColor(HSSFColor.GREY_80_PERCENT.index);
		 
			//产生表格标题行
			HSSFRow row = sheet.createRow(0);
	      	for (int i = 0; i < headers.length; i++) {
	      		HSSFCell cell = row.createCell(i);
	      		cell.setCellStyle(headerStyle);
	      		HSSFRichTextString text = new HSSFRichTextString(headers[i]);
	      		cell.setCellValue(text);
	      	}
	      	
	      	HSSFCell cell = null;
	      	for (AtteRecordStatBean bean : list) {
	      		row = sheet.createRow(list.indexOf(bean) + 1);
	      		
	      		String remark = bean.getArBean().getRemark();
	      		remark = remark == null ? "" : remark;
	      		
	      		cell = row.createCell(0);
	  			cell.setCellStyle(whiteStyle);
	            HSSFRichTextString richString = new HSSFRichTextString(new SimpleDateFormat("yyyy年MM月dd日").format(bean.getArBean().getDate()));
	            HSSFFont blueFont = workbook.createFont();
	            blueFont.setColor(HSSFColor.BLUE.index);
	            richString.applyFont(blueFont);
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
	            	cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	            
	            cell = row.createCell(1);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(AtteRecordStatus.N.equals(bean.getArStatus()) ? "" : new SimpleDateFormat("HH:mm:ss").format(bean.getArBean().getSTime()));
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	            
	            cell = row.createCell(2);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(!AtteRecordStatus.E.equals(bean.getArStatus()) ? "" : new SimpleDateFormat(new SimpleDateFormat("dd").format(bean.getArBean().getETime()).equals(new SimpleDateFormat("dd").format(bean.getArBean().getDate())) ? "HH:mm:ss" : "yyyy年MM月dd日 HH:mm:ss").format(bean.getArBean().getETime()));
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);

	            cell = row.createCell(3);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(bean.getAtDuration() != 0 ? bean.getAtDurationDesc() : "");
	            if (bean.getAtDuration() != 0)
	            	cell.setCellStyle(yellowStyle);
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	            
	            cell = row.createCell(4);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(bean.getLtDuration() != 0 ? bean.getLtDurationDesc() : "");
	            if (bean.getLtDuration() != 0)
	            	cell.setCellStyle(yellowStyle);
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	            
	            cell = row.createCell(5);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(bean.getOtDuration() != 0 ? bean.getOtDurationDesc() : "");
	            if (bean.getOtDuration() != 0)
	            	cell.setCellStyle(yellowStyle);
	            if (bean.getArBean().isHoliday()) 
	            	richString.applyFont(pinkFont);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	            
	            cell = row.createCell(6);
	            cell.setCellStyle(whiteStyle);
	            richString = new HSSFRichTextString(remark);
	            if (bean.isGrey())
					cell.setCellStyle(greyStyle);
	            cell.setCellValue(richString);
	      	}
	      	workbook.write(os);
	      	return file.getAbsoluteFile();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (workbook != null)
				try {
					os.close();
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
}