package org.kerwin.weibo.dao.bin.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kerwin.weibo.dao.UserDao;
import org.kerwin.weibo.mode.User;
import org.kerwin.weibo.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import weibo4j.util.WeiboConfig;

public class UserDaoBinImpl implements UserDao {
	
	private Document doc;	//文档对象
	private File userInfo;	//保存用户信息的文件
	private NodeList users;	//所有用户节点的集合
	private Class<User> userClass;	//User类Class对象
	private Field[] userFields;	//User类所有字段对象
	private String[] userFieldsName;	//User类所有字段名称
	private Class<?>[] userFieldsClass;	//User类所有字段的Class对象
	private Oauth oauth;	//授权对象
	
	/**
	 * 构造方法
	 */
	public UserDaoBinImpl(){
		userInfo = getFile(WeiboConfig.getValue("userInfoPath"));	//得到存储用户信息的文件对象
		doc = XMLUtil.getDocument(getUserFile());	//将文件转换为文档对象
		users = doc.getElementsByTagName("User");	//得到所有用户集合
		oauth = new Oauth();
		
		userClass = User.class;
		List<Field> fields = Arrays.asList(userClass.getDeclaredFields());	//得到User类所有字段对象集合
		
		/*
		 * 初始化数组并填值
		 * 由于数组长度未知、故用Arrays.copyOf()方法动态增加数组长度
		 * 本想用List集合。可是到后面class.getConstructor()时需要数组做为参数。我试过List.toArray()将集合转为数组（须强制转换）会报错。
		 * */
		userFields = new Field[]{};
		userFieldsName = new String[]{};
		userFieldsClass = new Class<?>[]{};
		for(int i = 0; i < fields.size(); i++){
			if(fields.get(i).getModifiers()==Modifier.PRIVATE+Modifier.STATIC+Modifier.FINAL)	continue;
			userFields = Arrays.copyOf(userFields, userFields.length+1);
			userFields[userFields.length-1] = fields.get(i);
			userFieldsName = Arrays.copyOf(userFieldsName, userFieldsName.length+1);
			userFieldsName[userFieldsName.length-1] = fields.get(i).getName();
			userFieldsClass = Arrays.copyOf(userFieldsClass, userFieldsClass.length+1);
			userFieldsClass[userFieldsClass.length-1] = fields.get(i).getType();
		}
	}
	
	/**
	 * 获得文件对象、文件路径若有不存在的文件夹则自动创建
	 * @param fileName	文件路径
	 * @return
	 */
	private File getFile(String fileName){
		new File(fileName.substring(0, fileName.lastIndexOf("/"))).mkdirs();
		return new File(fileName);
	}
	
	/**
	 * 用来获得存储用户信息的文件对象
	 * @return
	 */
	private File getUserFile(){
		if(!userInfo.exists()){
			try {
				//如果文件不存在。则创建一个带默认节点的文件
				PrintStream out = new PrintStream(userInfo);
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Users></Users>");
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return userInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#addUser(org.kerwin.weibo.mode.User)
	 */
	@Override
	public boolean addUser(User user) {
		//验空
		if(user == null || !(userFields != null && userFields.length > 0))	return false;
		if(updateUser(user))	return true;
		List<User> us = selectAllUser();
		user = new User(user.getUid(),(us.size() == 0 ? 0 : us.get(us.size()-1).getPid()+1),user.getName(),user.getAccessToken());
		Map<String,String> attrs = new Hashtable<String,String>();	//存放属性
		attrs.put("id", user.getUid().toString());	//为User节点添加ID标识
//		attrs.put("pid", (users.getLength() == 0 ? "0" : String.valueOf(Long.parseLong(users.item(users.getLength()-1).getAttributes().getNamedItem("pid").getNodeValue())+1)));
		//向文档添加一个用户节点
		Element element = XMLUtil.addElement(doc, getUserFile(), "User", attrs);
		
		//循环User字段集合、给User节点新增子节点
		for(int i = 0; i < userFields.length; i++){
			Field field = userFields[i];	
			try {
				attrs = new Hashtable<String,String>();
				attrs.put("type", field.getType().getName());	//字段的类型
				//反射调用对象的getter方法得到对应字段值
				Object obj = userClass.getMethod("get"+toUpperCaseFirstOne(field.getName())).invoke(user);
				ByteArrayOutputStream bao = new ByteArrayOutputStream();	//字节输出流
				ObjectOutputStream oos = new ObjectOutputStream(bao);	//对象输出流字节流对接
				oos.writeObject(obj);	//将对象输出到字节流
				String str = java.net.URLEncoder.encode(bao.toString("ISO-8859-1"), "UTF-8");	//将字节流转换为字符串
				//关闭流
				oos.close();
				bao.close();
				attrs.put("value", str);	//字段的值
				//在用户节点下新增节点名为字段名的节点。并给类型和值属性
				XMLUtil.addElement(doc, element, getUserFile(), userFields[i].getName(), attrs);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#deleteUser(org.kerwin.weibo.mode.User)
	 */
	@Override
	public boolean deleteUser(User user) {
		if(user == null)	return false;
		User u = selectUserById(user.getUid());
		if(u != null){
			for(int i = 0,j = users.getLength(); i < j; i++){
				if(Long.parseLong(users.item(i).getAttributes().getNamedItem("id").getNodeValue()) == u.getUid()){
					XMLUtil.delElement(doc, (Element)users.item(i), userInfo);
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#updateUser(org.kerwin.weibo.mode.User)
	 */
	@Override
	public boolean updateUser(User user) {
		if(deleteUser(user))
			if(addUser(user))
				return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#selectAllUser()
	 */
	@Override
	public List<User> selectAllUser() {
		List<User> us = new ArrayList<User>();	//用户集合
		doc = XMLUtil.getDocument(getUserFile());	//将文件转换为文档对象
		users = doc.getElementsByTagName("User");	//得到所有User节点
		if(users == null || users.getLength() == 0) return us;	//判断是否包含User节点
		for(int i = 0, length = users.getLength(); i < length; i++){	//循环所有User节点。得到所需信息
			if(users.item(i) instanceof Element){
				Element ue = (Element) users.item(i);	//转化节点为Element对象
				
				Object[] obj = new Object[userFieldsName.length];	//数组用来存储字段对应值
				
				for(int j = 0; j < userFieldsName.length; j++){	//循环字段名数组
					NodeList nl = ue.getElementsByTagName(userFieldsName[j]);	//得到当前User节点下名为该字段名的节点的集合（只有一个）
					if(nl == null || nl.getLength() == 0) continue;
					if(nl.item(0) instanceof Element){
						Element ae = (Element) nl.item(0);
						try {
							//得到对应属性的值
							String str = java.net.URLDecoder.decode(ae.getAttribute("value"), "UTF-8");
							//将得到的值加载到字节输入流
							ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));	
							//字节输入流和对象输入流交接
							ObjectInputStream ois = new ObjectInputStream(bis);
							Object o = ois.readObject();	//将值输入到对象
							//关闭流
							ois.close();
							bis.close();
							obj[j] = Class.forName(ae.getAttribute("type")).cast(o);	//通过反射转换类型
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				User u = null;
				try {
					//通过反射得到构造方法并通过参数调用得到对象
					u = userClass.getConstructor(userFieldsClass).newInstance(obj);	
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				if(u != null)	us.add(u);
			}
		}
		Collections.sort(us);
		return us;
	}
	
	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#selectAllUserName()
	 */
	public ArrayList<String> selectAllUserName(){
		List<User> us = selectAllUser();
		if(us == null || us.size() == 0)	return new ArrayList<String>();
		ArrayList<String> userName = new ArrayList<String>();
		Iterator<User> iterator = us.iterator();
		while(iterator.hasNext()){
			User u = iterator.next();
			userName.add(u.getName());
		}
		return userName;
	}

	/* (non-Javadoc)
	 * @see org.kerwin.weibo.dao.UserDao#selectUserById(long)
	 */
	@Override
	public User selectUserById(long id) {
		List<User> us = selectAllUser();
		if(us == null || us.size() == 0)	return null;
		Iterator<User> iterator = us.iterator();
		while(iterator.hasNext()){
			User u = iterator.next();
			if(u.getUid() == id)	return u;
		}
		return null;
	}
	
	//首字母转小写
    public static String toLowerCaseFirstOne(String s)
    {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    //首字母转大写
    public static String toUpperCaseFirstOne(String s)
    {
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    /* (non-Javadoc)
     * @see org.kerwin.weibo.dao.UserDao#getOauthURL()
     */
    public String getOauthURL(){
    	try {
			//得到授权页面地址
			return oauth.authorize("code",WeiboConfig.getValue("client_ID"),WeiboConfig.getValue("client_SERCRET"));	
		} catch (WeiboException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /* (non-Javadoc)
     * @see org.kerwin.weibo.dao.UserDao#getUserByCode(java.lang.String)
     */
    public User getUserByCode(String code) throws WeiboException, JSONException{
		AccessToken at = oauth.getAccessTokenByCode(code);
		String accessToken = at.getAccessToken();
		weibo4j.model.User user = getWeiboUserByAccessToken(accessToken);
		User u = new User(Long.parseLong(user.getId()),0l,user.getName(),accessToken);
		addUser(u);
		return u;
	}
    
    public weibo4j.model.User getWeiboUserByUser(User user) throws JSONException, WeiboException{
    	if(user == null)	return null;
    	return getWeiboUserByAccessToken(user.getAccessToken());
    }
    
    public weibo4j.model.User getWeiboUserByAccessToken(String accessToken) throws JSONException, WeiboException{
		Account am = new Account();
		am.client.setToken(accessToken);
		Long uid = am.getUid().getLong("uid");
		Users um = new Users();
		um.setToken(accessToken);
		return um.showUserById(uid.toString());
    }

}
