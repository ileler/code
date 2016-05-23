package org.kerwin.shutdownui.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kerwin.shutdownui.bean.User;
import org.kerwin.shutdownui.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4j.Account;
import weibo4j.Users;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

public class UserDao {

	private Document doc; // 文档对象
	private NodeList users; // 所有用户节点的集合
	private Class<User> userClass; // User类Class对象
	private Field[] userFields; // User类所有字段对象
	private String[] userFieldsName; // User类所有字段名称
	private Class<?>[] userFieldsClass; // User类所有字段的Class对象

	private InputStream getUserInfo() {
		return getClass().getResourceAsStream("/users.kerwin"); // 得到存储用户信息的文件对象
	}

	/**
	 * 构造方法
	 */
	public UserDao() {
		doc = XMLUtil.getDocument(getUserInfo()); // 将文件转换为文档对象
		users = doc.getElementsByTagName("User"); // 得到所有用户集合

		userClass = User.class;
		List<Field> fields = Arrays.asList(userClass.getDeclaredFields()); // 得到User类所有字段对象集合

		/*
		 * 初始化数组并填值 由于数组长度未知、故用Arrays.copyOf()方法动态增加数组长度
		 * 本想用List集合。可是到后面class.getConstructor
		 * ()时需要数组做为参数。我试过List.toArray()将集合转为数组（须强制转换）会报错。
		 */
		userFields = new Field[] {};
		userFieldsName = new String[] {};
		userFieldsClass = new Class<?>[] {};
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getModifiers() == Modifier.PRIVATE
					+ Modifier.STATIC + Modifier.FINAL)
				continue;
			userFields = Arrays.copyOf(userFields, userFields.length + 1);
			userFields[userFields.length - 1] = fields.get(i);
			userFieldsName = Arrays.copyOf(userFieldsName,
					userFieldsName.length + 1);
			userFieldsName[userFieldsName.length - 1] = fields.get(i).getName();
			userFieldsClass = Arrays.copyOf(userFieldsClass,
					userFieldsClass.length + 1);
			userFieldsClass[userFieldsClass.length - 1] = fields.get(i)
					.getType();
		}
	}

	public List<User> selectAllUser() {
		List<User> us = new ArrayList<User>(); // 用户集合
		doc = XMLUtil.getDocument(getUserInfo()); // 将文件转换为文档对象
		users = doc.getElementsByTagName("User"); // 得到所有User节点
		if (users == null || users.getLength() == 0)
			return us; // 判断是否包含User节点
		for (int i = 0, length = users.getLength(); i < length; i++) { // 循环所有User节点。得到所需信息
			if (users.item(i) instanceof Element) {
				Element ue = (Element) users.item(i); // 转化节点为Element对象

				Object[] obj = new Object[userFieldsName.length]; // 数组用来存储字段对应值

				for (int j = 0; j < userFieldsName.length; j++) { // 循环字段名数组
					NodeList nl = ue.getElementsByTagName(userFieldsName[j]); // 得到当前User节点下名为该字段名的节点的集合（只有一个）
					if (nl == null || nl.getLength() == 0)
						continue;
					if (nl.item(0) instanceof Element) {
						Element ae = (Element) nl.item(0);
						try {
							// 得到对应属性的值
							String str = java.net.URLDecoder.decode(
									ae.getAttribute("value"), "UTF-8");
							// 将得到的值加载到字节输入流
							ByteArrayInputStream bis = new ByteArrayInputStream(
									str.getBytes("ISO-8859-1"));
							// 字节输入流和对象输入流交接
							ObjectInputStream ois = new ObjectInputStream(bis);
							Object o = ois.readObject(); // 将值输入到对象
							// 关闭流
							ois.close();
							bis.close();
							obj[j] = Class.forName(ae.getAttribute("type"))
									.cast(o); // 通过反射转换类型
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				User u = null;
				try {
					// 通过反射得到构造方法并通过参数调用得到对象
					u = userClass.getConstructor(userFieldsClass).newInstance(
							obj);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (u != null)
					us.add(u);
			}
		}
		Collections.sort(us);
		return us;
	}

	public User selectUserById(long id) {
		List<User> us = selectAllUser();
		if (us == null || us.size() == 0)
			return null;
		Iterator<User> iterator = us.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			if (u.getUid() == id)
				return u;
		}
		return null;
	}

	public weibo4j.model.User getWeiboUserByUser(User user)
			throws JSONException, WeiboException {
		if (user == null)
			return null;
		return getWeiboUserByAccessToken(user.getAccessToken());
	}

	public weibo4j.model.User getWeiboUserByAccessToken(String accessToken)
			throws JSONException, WeiboException {
		Account am = new Account();
		am.client.setToken(accessToken);
		Long uid = am.getUid().getLong("uid");
		Users um = new Users();
		um.setToken(accessToken);
		return um.showUserById(uid.toString());
	}

}
