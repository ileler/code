package coderr.kerwin.arstat;

import java.io.Serializable;
import java.util.Date;

/**
 * 考勤统计信息实体类
 * @author kerwin612
 */
public class AtteRecordStatBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 是否灰色（月历表头尾填充的日期为灰色）
	 */
	private boolean isGrey;
	
	/**
	 * 迟到时长（单位：分） 
	 */
	private long atDuration;
	
	/**
	 * 早退时长（单位：分） 
	 */
	private long ltDuration;
	
	/**
	 * 加班时长（单位：分） 
	 */
	private long otDuration;
	
	/**
	 * 考勤信息
	 */
	private AtteRecordBean arBean;
	
	/**
	 * 考勤状态 
	 */
	private AtteRecordStatus arStatus;

	private AtteRecordStatBean() {}
	
	public boolean isGrey() {
		return isGrey;
	}

	public void setGrey(boolean isGrey) {
		this.isGrey = isGrey;
	}

	public long getAtDuration() {
		return atDuration;
	}
	
	/**
	 * 返回格式化后的迟到时长[h.m]
	 * @return
	 */
	public String getAtDurationDesc() {
		long hour = atDuration / 60;
        long minute = atDuration % 60;
		return hour + "." + minute;
	}

	public void setAtDuration(long atDuration) {
		this.atDuration = atDuration;
	}
	
	public long getLtDuration() {
		return ltDuration;
	}
	
	/**
	 * 返回格式化后的早退时长[h.m]
	 * @return
	 */
	public String getLtDurationDesc() {
		long hour = ltDuration / 60;
		long minute = ltDuration % 60;
		return hour + "." + minute;
	}
	
	public void setLtDuration(long ltDuration) {
		this.ltDuration = ltDuration;
	}

	public long getOtDuration() {
		return otDuration;
	}
	
	/**
	 * 返回格式化后的加班时长[h.m]
	 * @return
	 */
	public String getOtDurationDesc() {
		long hour = otDuration / 60;
        long minute = otDuration % 60;
		return hour + "." + minute;
	}

	public void setOtDuration(long otDuration) {
		this.otDuration = otDuration;
	}

	public AtteRecordStatus getArStatus() {
		return arStatus;
	}
	
	public AtteRecordBean getArBean() {
		return arBean;
	}
	
	public void setArBean(AtteRecordBean arBean) {
		this.arBean = arBean;
	}

	public void setArStatus(AtteRecordStatus arStatus) {
		this.arStatus = arStatus;
	}

	/**
	 * 根据考勤信息返回考勤统计信息
	 * @param bean	考勤信息
	 * @return
	 */
	public static AtteRecordStatBean getInstance(AtteRecordBean bean) {
		return getInstance(bean, false);
	}

	/**
	 * 根据考勤信息返回考勤统计信息
	 * @param bean	考勤信息
	 * @param isGrey	是否灰色
	 * @return
	 */
	public static AtteRecordStatBean getInstance(AtteRecordBean bean, boolean isGrey) {
		if (bean == null)
			try {
				throw new Exception("Parameter is not valid.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		AtteRecordStatBean extBean = new AtteRecordStatBean();
		extBean.setArBean(bean);	//设置考勤信息
		extBean.setGrey(isGrey);	//设置是否灰色
		if (bean.getSTime() == null) {
			//如果没有上班时间、则考勤状态为[无考勤]
			extBean.setArStatus(AtteRecordStatus.N);
		} else if (bean.getETime() == null) {
			//如果有上班时间、无下班时间、则考勤状态为[已签到]
			extBean.setArStatus(AtteRecordStatus.S);
		} else {
			//如果有上下班时间、则考勤状态为[已签退]
			extBean.setArStatus(AtteRecordStatus.E);
			//规则上班时间
			Date ruleSDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getSTime());
			//规则下班时间
			Date ruleEDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getETime());
			//规则开始算加班的时间
			Date ruleODate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getOTime());
			//规则开始休息的时间
			Date rulePDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getPTime());
			//规则结束休息的时间
			Date ruleRDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getRTime());
			if (!bean.isHoliday() && bean.getSTime().after(ruleSDate)) {	//非节假日才计算迟到
				//迟到
				long interval = bean.getSTime().getTime() - ruleSDate.getTime();
				extBean.setAtDuration(interval /= 1000 * 60);
			}
			if (bean.isHoliday() || bean.getETime().after(ruleODate)) {
				//加班
				long interval = 0;
				if (bean.isHoliday()) {
					interval = extBean.getOTInterval1(bean);
				} else {
					interval = bean.getETime().getTime() - ruleODate.getTime();
				}
				extBean.setOtDuration(interval /= 1000 * 60);
			} else if (!bean.isHoliday() && bean.getETime().before(ruleEDate)) {	//非节假日才计算早退
				//早退
				long interval = 0;
				if (bean.getETime().after(ruleRDate)) {
					//下午早退
					interval = ruleEDate.getTime() - bean.getETime().getTime();
				} else if (bean.getETime().after(rulePDate)) {
					//中午午休走的
					interval = ruleEDate.getTime() - ruleRDate.getTime();
				} else {
					//上午早退
					interval = (rulePDate.getTime() - bean.getETime().getTime()) + (ruleEDate.getTime() - bean.getETime().getTime());
				}
				extBean.setLtDuration(interval /= 1000 * 60);
			}
		}
		return extBean;
	}
	
	/**
	 * 节假日加班时间规则1
	 * 按正常工作时间算加班时长
	 * @param bean
	 * @return
	 */
	private long getOTInterval1(AtteRecordBean bean) {
		if (bean == null || bean.getDate() == null || bean.getSTime() == null || bean.getETime() == null)	return 0;	
		Date ruleSDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getSTime());
		Date rulePDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getPTime());
		Date ruleRDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getRTime());
		Date ruleEDate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getETime());
		Date ruleODate = DateUtil.getDateByTime(bean.getDate(), AtteRuleInfo.getOTime());
		if (bean.getSTime().before(ruleSDate)) {
			//早到
			if (bean.getETime().before(rulePDate)) {
				//上午就走
				return bean.getETime().getTime() - ruleSDate.getTime();
			} else if (bean.getETime().after(ruleRDate)) {
				if (bean.getETime().before(ruleEDate)) {
					//下午走的
					return (rulePDate.getTime() - ruleSDate.getTime()) + (bean.getETime().getTime() - ruleRDate.getTime());
				} else {
					if (bean.getETime().before(ruleODate)) {
						//正常下班点
						return (rulePDate.getTime() - ruleSDate.getTime()) + (ruleEDate.getTime() - ruleRDate.getTime()); 
					} else {
						//晚上加班
						return (rulePDate.getTime() - ruleSDate.getTime()) + (ruleEDate.getTime() - ruleRDate.getTime()) + (bean.getETime().getTime() - ruleODate.getTime());
					}
				}
			} else {
				//上午上完就走
				return rulePDate.getTime() - ruleSDate.getTime();
			}
		} else if (bean.getSTime().before(rulePDate)) {
			//迟到、上午才到
			if (bean.getETime().before(rulePDate)) {
				//上午就走
				return bean.getETime().getTime() - bean.getSTime().getTime();
			} else if (bean.getETime().after(ruleRDate)) {
				if (bean.getETime().before(ruleEDate)) {
					//下午走的
					return (rulePDate.getTime() - bean.getSTime().getTime()) + (bean.getETime().getTime() - ruleRDate.getTime());
				} else {
					if (bean.getETime().before(ruleODate)) {
						//正常下班点
						return (rulePDate.getTime() - bean.getSTime().getTime()) + (ruleEDate.getTime() - ruleRDate.getTime()); 
					} else {
						//晚上加班
						return (rulePDate.getTime() - bean.getSTime().getTime()) + (ruleEDate.getTime() - ruleRDate.getTime()) + (bean.getETime().getTime() - ruleODate.getTime());
					}
				}
			} else {
				//上午上完就走
				return rulePDate.getTime() - bean.getSTime().getTime();
			}
		} else if (bean.getSTime().before(ruleRDate)) {
			//迟到、上午未到
			if (bean.getETime().before(ruleEDate)) {
				//下午走的
				return (bean.getETime().getTime() - ruleRDate.getTime());
			} else {
				if (bean.getETime().before(ruleODate)) {
					//正常下班点
					return (ruleEDate.getTime() - ruleRDate.getTime()); 
				} else {
					//晚上加班
					return (ruleEDate.getTime() - ruleRDate.getTime()) + (bean.getETime().getTime() - ruleODate.getTime());
				}
			}
		} else if (bean.getSTime().before(ruleEDate)) {
			//迟到、下午才到
			if (bean.getETime().before(ruleEDate)) {
				//下午走的
				return (bean.getETime().getTime() - bean.getSTime().getTime());
			} else {
				if (bean.getETime().before(ruleODate)) {
					//正常下班点
					return (ruleEDate.getTime() - bean.getSTime().getTime()); 
				} else {
					//晚上加班
					return (ruleEDate.getTime() - bean.getSTime().getTime()) + (bean.getETime().getTime() - ruleODate.getTime());
				}
			}
		} else {
			//下班后才来加班
			return bean.getETime().getTime() - bean.getSTime().getTime(); 
		}
	}

}
