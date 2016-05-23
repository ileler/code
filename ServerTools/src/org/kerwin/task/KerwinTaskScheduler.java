package org.kerwin.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public final class KerwinTaskScheduler {
	
	private final static String LOG = "./log/";
	private static Timer timer;
	private static boolean isStart;
	private static List<String> cids;
	private static SimpleDateFormat sdfymd;
	private static Map<String,KerwinTasker> kts;
	private KerwinTaskScheduler(){}
	
	static{
		sdfymd = new SimpleDateFormat("yyyy/MM/dd");
		cids = new ArrayList<String>();
		timer = new Timer("KerwinTasker");
		kts = new Hashtable<String,KerwinTasker>();
	}
	
	public static synchronized void setKerwinTaskers(KerwinTasker[] kts){
		if(kts == null || kts.length < 1)	return;
		KerwinTaskScheduler.kts.clear();
		for(KerwinTasker kt : kts){
			KerwinTaskScheduler.kts.put(kt.getId(), kt);
		}
	}
	
	public static synchronized void addKerwinTasker(KerwinTasker kt){
		kts.put(kt.getId(), kt);
		System.out.println("[KerwinTaskScheduler][addKerwinTasker:"+kt.getName()+"]["+KerwinTasker.getDatetime(new Date())+"]");
	}
	
	public static synchronized void delKerwinTasker(String id){
		if(id == null || id.isEmpty())	return;
		KerwinTasker kt = kts.remove(id);
		System.out.println("[KerwinTaskScheduler][delKerwinTasker:"+kt.getName()+"]["+KerwinTasker.getDatetime(new Date())+"]");
	}
	
	public static synchronized void updateKerwinTasker(KerwinTasker kt){
		delKerwinTasker(kt.getId());
		addKerwinTasker(kt);
		System.out.println("[KerwinTaskScheduler][updateKerwinTasker:"+kt.getName()+"]["+KerwinTasker.getDatetime(new Date())+"]");
	}
	
	public static synchronized boolean isStart(){
		return isStart;
	}
	
	public static synchronized KerwinTasker[] getKTS(){
		return kts.values().toArray(new KerwinTasker[kts.size()]);
	}
	
	/**
	 * 开始执行任务
	 */
	public static synchronized void exec(){
		if(isStart)	return; isStart = true;
		System.out.println("[KerwinTaskScheduler][start]");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(kts == null || kts.size() < 1)	return;
				for(final KerwinTasker kt : kts.values()){
					synchronized(cids){		//同步代码块
						if(cids.contains(kt.getId()))	continue;
						cids.add(kt.getId());
						//新开线程执行任务、任务管理器内不能出现费时操作、以免阻塞后面任务不能如期执行
						new Thread(new Runnable() {
							@Override
							public void run() {
								//关闭状态的任务直接返回
								if(kt.getStatus() == false){
									if(kt.getNextExecTime() != null){
										kt.setNextExecTime(null);
										kt.updateKerwinTasker();
									}
									return;
								}	
								//任务下次执行时间为空或者任务下次执行时间不等于当前时间
								if(kt.getNextExecTime() == null || KerwinTasker.compareDatetime(kt.getNextExecTime()) != 0){
									//任务下次执行时间如果小于当前时间就再次计算下次的任务执行时间
									if(kt.getNextExecTime() == null || KerwinTasker.compareDatetime(kt.getNextExecTime()) < 0){
										try {
											kt.setNextExecTime(kt.calculateNextExecTime());
										} catch (Exception e) {
											e.printStackTrace();
											kt.setLastExecStatus(e.getMessage());
										}
										kt.updateKerwinTasker();
									}
									cids.remove(kt.getId());
									return;
								}
								//开始新线程开始执行任务、并且计算下一次任务开始执行时间
								System.out.println("[KerwinTaskScheduler]["+kt.getName()+" is start]["+KerwinTasker.getDatetime(new Date())+"]");
								try {
									kt.setNextExecTime(kt.calculateNextExecTime());
								} catch (Exception e) {
									e.printStackTrace();
									kt.setLastExecStatus(e.getMessage());
								}
								kt.updateKerwinTasker();
								new Thread(new Runnable() {
									@Override
									public void run() {
										kt.setLastExecBeginTime(new Date());
										kt.setLastExecTime(new Date());
										kt.setUpdatetime(new Date());
										kt.setExecCount(kt.getExecCount()+1);
										kt.setLastExecEndTime(new Date());
										kt.updateKerwinTasker();
									}
								}).start();
								String msg = null;
								String className = kt.getTaskerClass();
								if(className != null && !className.isEmpty()){
									String dirName = sdfymd.format(new Date());
									new File(LOG+dirName).mkdirs();
									BufferedWriter bw = null;
									try {
										bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOG+dirName+"/"+kt.getName(), true)));
										if(className.startsWith("Command:")){
											//如果任务是命令、则去执行命令
											String command = className.substring(8);
											execCommand(kt,bw,command);
											msg = "success";
										}else{
											//如果任务是Runnable子类则调用run方法
											bw.append("\ntasker["+kt.getName()+"]is stt["+KerwinTasker.getDatetime(new Date())+"]");
											bw.flush();
											Class<?> taskerClass = ClassLoader.getSystemClassLoader().loadClass(className);
											List<Class<?>> interfaces = Arrays.asList(taskerClass.getInterfaces());
											if(interfaces == null || interfaces.size() < 1 || !interfaces.contains(Runnable.class)){
												msg = "taskerClass hasn't implements [Runnable]";
												bw.append("\n"+msg);
												bw.append("\ntasker["+kt.getName()+"]is end["+KerwinTasker.getDatetime(new Date())+"]");
												bw.flush();
											}else{
												Method runMethod = taskerClass.getMethod("run", new Class[0]);
												Object taskerObject = taskerClass.newInstance();
												runMethod.invoke(taskerObject, new Object[0]);
												msg = "success";
												bw.append("\n"+msg);
												bw.append("\ntasker["+kt.getName()+"]is end["+KerwinTasker.getDatetime(new Date())+"]");
												bw.flush();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										msg = e.getMessage();
										if(bw != null){
											PrintWriter pw = new PrintWriter(bw, true);
											e.printStackTrace(pw);
										}
									} finally {
										if(bw != null){
											try {
												bw.newLine();
												bw.flush();
												bw.close();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								}else{
									msg = "taskerClass is null";
								}
								final String _msg = msg;
								new Thread(new Runnable() {
									@Override
									public void run() {
										kt.setLastExecEndTime(new Date());
										kt.setUpdatetime(new Date());
										kt.setLastExecStatus(_msg);
										kt.updateKerwinTasker();
									}
								}).start();
								System.out.println("[KerwinTaskScheduler]["+kt.getName()+" is end]["+KerwinTasker.getDatetime(new Date())+"]");
								cids.remove(kt.getId());
							}
						}).start();
					}
				}
			}
		}, 1L, 1000L);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run(){
				Set<String> ks = kts.keySet();
				Iterator<String> iter = ks.iterator();
				System.out.println("#####kts stt#####");
				while(iter.hasNext()){
					String key = iter.next();
					KerwinTasker value = kts.get(key);
					System.out.println("kts:"+key+"->execTime:"+value.getExecTime()+",lastExecTime:"+KerwinTasker.getDatetime(value.getLastExecTime())+",nextExecTime:"+KerwinTasker.getDatetime(value.getNextExecTime())+"["+KerwinTasker.getDatetime(new Date())+"]");
				}
				System.out.println("#####kts end#####");
			}
		},1L,5000L);
	}
	
	/**
	 * 执行命令
	 * @param kt		任务实体类
	 * @param bw		输出流、输出运行命令返回结果
	 * @param command	命令字符串
	 * @throws Exception
	 */
	private static void execCommand(KerwinTasker kt, BufferedWriter bw, String command) throws Exception{
		if(kt == null)	throw new Exception("KerwinTasker is null");
		if(bw == null)	throw new Exception("LogFile is null");
		if(command == null || command.isEmpty())	throw new Exception("Command is null");
		bw.append("\ntasker["+kt.getName()+"]is stt["+KerwinTasker.getDatetime(new Date())+"]");
		bw.append("\n"+command);
		bw.flush();
		List<String> cmdarray = KerwinTasker.formatCommand(command);
		try{
//			File dir = new File(KerwinTaskScheduler.class.getResource("/").toURI().getPath());
//			Process process = Runtime.getRuntime().exec(cmdarray.toArray(new String[cmdarray.size()]),null,dir);
			Process process = new ProcessBuilder(cmdarray).redirectErrorStream(true).start();
			BufferedReader rbr = new BufferedReader(new InputStreamReader(process.getInputStream(),System.getProperty("sun.jnu.encoding")));
			String str = null;
			while((str = rbr.readLine()) != null){
				bw.append("\n"+str);
				bw.flush();
			}
			process.waitFor();
		}catch(Exception e){
			bw.append("\n'"+cmdarray.get(0)+"' is not an internal or external command, not running program or batch file.");
			bw.flush();
			e.printStackTrace();
		}
		bw.append("\n\ntasker["+kt.getName()+"]is end["+KerwinTasker.getDatetime(new Date())+"]");
	}
	
}
