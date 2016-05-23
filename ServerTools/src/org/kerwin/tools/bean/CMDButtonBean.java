package org.kerwin.tools.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.kerwin.tools.util.BeanUtil;

public class CMDButtonBean extends BeanUtil<CMDButtonBean> {
	
	private final static String CMDBTNS = "./cfgfs/cmdbtns.kerwin";
	private static CMDButtonBean staticbean;
	private String btnName;
	private String btnCommand;
	public CMDButtonBean(){}
	
	static{
		staticbean = new CMDButtonBean();
	}

	public CMDButtonBean(String btnName, String btnCommand) {
		super();
		this.btnName = btnName;
		this.btnCommand = btnCommand;
	}
	
	public String getBtnName() {
		return btnName;
	}

	public void setBtnName(String btnName) {
		this.btnName = btnName;
	}

	public String getBtnCommand() {
		return btnCommand;
	}

	public void setBtnCommand(String btnCommand) {
		this.btnCommand = btnCommand;
	}

	@Override
	protected void initial() {
		bean = this;
		beanName = "CMDBtn";
		beanClass = CMDButtonBean.class;
	}
	
	@Override
	protected boolean insertCheck() throws Exception{
		if(this.btnName == null || this.btnName.isEmpty())	throw new Exception("Name is Empty");
		if(this.btnCommand == null || this.btnCommand.isEmpty())	throw new Exception("Name is Empty");
		return true;
	}

	@Override
	protected File getBeanFile() {
		File cfgFile = null;
		try {
			if(!(cfgFile = new File(CMDBTNS)).exists()){
				new File(CMDBTNS.substring(0, CMDBTNS.lastIndexOf("/"))).mkdirs();
				//如果文件不存在。则创建一个带默认节点的文件
				PrintStream out = new PrintStream(cfgFile);
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><CMDBtns></CMDBtns>");
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cfgFile;
	}

	public static boolean deleteById(String id){
		return staticbean.delete(id);
	}
	
	public static List<CMDButtonBean> selectAll(){
		return staticbean.select();
	}
	
	public static CMDButtonBean selectById(String id){
		return staticbean.selectBeanById(id);
	}
	
}
