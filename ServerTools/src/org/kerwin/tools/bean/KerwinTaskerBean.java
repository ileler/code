package org.kerwin.tools.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.kerwin.task.KerwinTasker;
import org.kerwin.task.KerwinTasker.KerwinTaskerType1;
import org.kerwin.task.KerwinTasker.KerwinTaskerType2;
import org.kerwin.task.KerwinTasker.KerwinTaskerType3;
import org.kerwin.task.KerwinTasker.KerwinTaskerType4;
import org.kerwin.task.KerwinTasker.KerwinTaskerType5;
import org.kerwin.tools.util.BeanUtil;

public class KerwinTaskerBean extends BeanUtil<KerwinTaskerBean>{
	
	private final static String KTS = "./cfgfs/kerwintasker.kerwin";
	private static KerwinTaskerBean staticbean;
	private KerwinTasker kt;
	
	static{
		staticbean = new KerwinTaskerBean();
	}
	
	public KerwinTaskerBean() {
		super();
	}
	
	public KerwinTaskerBean(KerwinTasker kt) {
		this();
		this.kt = kt;
		this.setId(this.kt.getId());
	}

	public KerwinTasker getKt() {
		return this.kt;
	}

	public void setKt(KerwinTasker kt) {
		this.kt = kt;
		this.kt.setId(this.kt.getId());
	}

	@Override
	protected void initial() {
		bean = this;
		beanName = "KerwinTasker";
		beanClass = KerwinTaskerBean.class;
	}

	@Override
	protected File getBeanFile() {
		File cfgFile = null;
		try {
			if(!(cfgFile = new File(KTS)).exists()){
				new File(KTS.substring(0, KTS.lastIndexOf("/"))).mkdirs();
				//如果文件不存在。则创建一个带默认节点的文件
				PrintStream out = new PrintStream(cfgFile);
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><KerwinTaskers></KerwinTaskers>");
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cfgFile;
	}

	@Override
	protected boolean insertCheck() throws Exception {
		if(this.kt == null)	throw new Exception("Kt is Empty");
		return true;
	}
	
	@Override
	public String toString(){
		if(this.kt != null){
			String str = "";
			KerwinTasker kt = (KerwinTasker)this.kt;
			str+="Name:"+kt.getName()+"     |     ";
			str+="TaskerClass:"+kt.getTaskerClass()+"     ";
			str+="ExecTime:"+kt.getExecTime()+"     ";
			str+="NextExecTime:"+KerwinTasker.getDatetime(kt.getNextExecTime());
			if(this.kt instanceof KerwinTaskerType1Impl){
			}else if(this.kt instanceof KerwinTaskerType2Impl){
				str+="     |     Extend:"+((KerwinTaskerType2Impl)kt).getDate();
			}else if(this.kt instanceof KerwinTaskerType3Impl){
				Integer[] weeks = ((KerwinTaskerType3Impl)kt).getWeeks();
				if(weeks == null || weeks.length < 1){
					str+="     |     Extend:"+"null";
				}else{
					String _s = null;
					for(Integer i : weeks){
						_s = _s == null ? i+"" : _s+","+i;
					}
					str+="     |     Extend:"+_s;
				}	
			}else if(this.kt instanceof KerwinTaskerType4Impl){
				str+="     |     Extend:"+((KerwinTaskerType4Impl)kt).getDay();
			}else if(this.kt instanceof KerwinTaskerType5Impl){
				str+="     |     Extend:"+((KerwinTaskerType5Impl)kt).getIntervalType()+"-";
				str+=((KerwinTaskerType5Impl)kt).getInterval();
			}
			return str;
		}else{
			return super.toString();
		}
	}
	
	public static boolean deleteById(String id){
		return staticbean.delete(id);
	}
	
	public static List<KerwinTaskerBean> selectAll(){
		return staticbean.select();
	}
	
	public static KerwinTaskerBean selectById(String id){
		return staticbean.selectBeanById(id);
	}
	
	public static class KerwinTaskerType1Impl extends KerwinTaskerType1{

		private static final long serialVersionUID = 1L;

		@Override
		public void updateKerwinTasker() {
			KerwinTaskerBean ktb = KerwinTaskerBean.selectById(getId());
			if(ktb != null){
				ktb.setKt(this);
				try {
					ktb.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static class KerwinTaskerType2Impl extends KerwinTaskerType2{

		private static final long serialVersionUID = 1L;

		@Override
		public void updateKerwinTasker() {
			KerwinTaskerBean ktb = KerwinTaskerBean.selectById(getId());
			if(ktb != null){
				ktb.setKt(this);
				try {
					ktb.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static class KerwinTaskerType3Impl extends KerwinTaskerType3{

		private static final long serialVersionUID = 1L;

		@Override
		public void updateKerwinTasker() {
			KerwinTaskerBean ktb = KerwinTaskerBean.selectById(getId());
			if(ktb != null){
				ktb.setKt(this);
				try {
					ktb.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static class KerwinTaskerType4Impl extends KerwinTaskerType4{

		private static final long serialVersionUID = 1L;

		@Override
		public void updateKerwinTasker() {
			KerwinTaskerBean ktb = KerwinTaskerBean.selectById(getId());
			if(ktb != null){
				ktb.setKt(this);
				try {
					ktb.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static class KerwinTaskerType5Impl extends KerwinTaskerType5{

		private static final long serialVersionUID = 1L;

		@Override
		public void updateKerwinTasker() {
			KerwinTaskerBean ktb = KerwinTaskerBean.selectById(getId());
			if(ktb != null){
				ktb.setKt(this);
				try {
					ktb.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
