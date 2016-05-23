package org.kerwin.tools.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public abstract class BeanUtil<T extends BeanUtil<?>>{

	private String id;
	private Document doc;					//文档对象
	private NodeList beans;					//所有Bean节点的集合
	private Field[] beanFields;				//Bean类所有字段对象
	private String[] beanFieldsName;		//Bean类所有字段名称
	private Class<?>[] beanFieldsClass;		//Bean类所有字段的Class对象
	protected T bean;
	protected String beanName;				//Bean节点名称
	protected Class<T> beanClass;			//Bean类Class对象
	
	public BeanUtil(){
		initial();
		if(bean == null || beanName == null || beanClass == null){
			try {
				throw new Exception("Bean hasn't implements [initial] method.(initial method need to initial fields(bean,beanName,beanClass))");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		doc = XMLUtil.getDocument(getBeanFile());	//将文件转换为文档对象
		beans = doc.getElementsByTagName(beanName == null ? "Bean" : beanName);	//得到所有用户集合
		beanFields = new Field[]{};
		beanFieldsName = new String[]{};
		beanFieldsClass = new Class<?>[]{};
		List<Field> fields = Arrays.asList(beanClass.getDeclaredFields());
		for(int i = 0; i < fields.size(); i++){
			if(fields.get(i).getModifiers()==Modifier.PRIVATE+Modifier.STATIC || fields.get(i).getModifiers()==Modifier.PRIVATE+Modifier.STATIC+Modifier.FINAL)	continue;
			beanFields = Arrays.copyOf(beanFields, beanFields.length+1);
			beanFields[beanFields.length-1] = fields.get(i);
			beanFieldsName = Arrays.copyOf(beanFieldsName, beanFieldsName.length+1);
			beanFieldsName[beanFieldsName.length-1] = fields.get(i).getName();
			beanFieldsClass = Arrays.copyOf(beanFieldsClass, beanFieldsClass.length+1);
			beanFieldsClass[beanFieldsClass.length-1] = fields.get(i).getType();
		}
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	protected abstract void initial();
	protected abstract File getBeanFile();
	protected abstract boolean insertCheck() throws Exception;
	
	public String insert() throws Exception{
		//验空
		if(bean == null || !(beanFields != null && beanFields.length > 0))	throw new Exception("arg is null.");
		if(bean.getId() == null || bean.getId().isEmpty()){
			bean.setId(BeanUtil.generateID(bean.getClass()));
		}
		if(selectBeanById(bean.getId()) != null)	throw new Exception("ID:"+bean.getId()+" is exists.");
		if(!insertCheck())	throw new Exception("Check return false.");
		Map<String,String> attrs = new Hashtable<String,String>();	//存放属性
		attrs.put("id", bean.getId());	//为User节点添加ID标识
		//向文档添加一个Bean节点
		Element element = XMLUtil.addElement(doc, getBeanFile(), beanName == null ? "Bean" : beanName, attrs);
		
		//循环Bean字段集合、给Bean节点新增子节点
		for(int i = 0; i < beanFields.length; i++){
			Field field = beanFields[i];	
			try {
				attrs = new Hashtable<String,String>();
				attrs.put("type", field.getType().getName());	//字段的类型
				//反射调用对象的getter方法得到对应字段值
				Object obj = beanClass.getMethod("get"+toUpperCaseFirstOne(field.getName())).invoke(bean);
				ByteArrayOutputStream bao = new ByteArrayOutputStream();	//字节输出流
				ObjectOutputStream oos = new ObjectOutputStream(bao);	//对象输出流字节流对接
				oos.writeObject(obj);	//将对象输出到字节流
				String str = java.net.URLEncoder.encode(bao.toString("ISO-8859-1"), "UTF-8");	//将字节流转换为字符串
				//关闭流
				oos.close();
				bao.close();
				attrs.put("value", str);	//字段的值
				//在Bean节点下新增节点名为字段名的节点。并给类型和值属性
				XMLUtil.addElement(doc, element, getBeanFile(), beanFields[i].getName(), attrs);
			} catch (Exception e) {
				throw e;
			}
		}
		return bean.getId();
	};
	
	public boolean delete(){
		if(bean == null)	return false;
		T b = selectBeanById(bean.getId());
		if(b != null && beans != null){
			for(int i = 0,j = beans.getLength(); i < j; i++){
				if(b.getId() != null && beans.item(i).getAttributes() != null && beans.item(i).getAttributes().getNamedItem("id")!= null && b.getId().equals(beans.item(i).getAttributes().getNamedItem("id").getNodeValue())){
					XMLUtil.delElement(doc, (Element)beans.item(i), getBeanFile());
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean update() throws Exception{
		if(delete()){
			try {
				if(insert() != null) return true;
				return false;
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new Exception("ID:"+bean.getId()+" is not exists.");
		}
	}
	
	protected boolean delete(String id){
		T b = selectBeanById(id);
		if(b != null && beans != null){
			for(int i = 0,j = beans.getLength(); i < j; i++){
				if(b.getId() != null && beans.item(i).getAttributes() != null && beans.item(i).getAttributes().getNamedItem("id")!= null && b.getId().equals(beans.item(i).getAttributes().getNamedItem("id").getNodeValue())){
					XMLUtil.delElement(doc, (Element)beans.item(i), getBeanFile());
					return true;
				}
			}
		}
		return false;
	}
	
	protected List<T> select(){
		List<T> bs = new ArrayList<T>();	//Bean集合
		doc = XMLUtil.getDocument(getBeanFile());	//将文件转换为文档对象
		beans = doc.getElementsByTagName(beanName == null ? "Bean" : beanName);	//得到所有Bean节点
		if(beans == null || beans.getLength() == 0) return bs;	//判断是否包含Bean节点
		for(int i = 0, length = beans.getLength(); i < length; i++){	//循环所有Bean节点。得到所需信息
			if(beans.item(i) instanceof Element){
				Element be = (Element) beans.item(i);	//转化节点为Element对象
				String id = be.getAttribute("id");
				
				Object[] obj = new Object[beanFieldsName.length];	//数组用来存储字段对应值
				
				for(int j = 0; j < beanFieldsName.length; j++){	//循环字段名数组
					NodeList nl = be.getElementsByTagName(beanFieldsName[j]);	//得到当前Bean节点下名为该字段名的节点的集合（只有一个）
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
				T u = null;
				try {
					//通过反射得到构造方法并通过参数调用得到对象
					u = beanClass.getConstructor(beanFieldsClass).newInstance(obj);	
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				if(u != null){
					u.setId(id);
					bs.add(u);
				}	
			}
		}
		return bs;
	}
	
	protected T selectBeanById(String id) {
		if(id == null || id.isEmpty())	return null;
		List<T> bs = select();
		if(bs == null || bs.size() == 0)	return null;
		Iterator<T> iterator = bs.iterator();
		while(iterator.hasNext()){
			T u = iterator.next();
			if(id.equals(u.getId()))	return u;
		}
		return null;
	}

	//首字母转小写
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    //首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    public static String generateID(){
    	return generateID(BeanUtil.class,32);
    }
    
    public static String generateID(Class<?> cs){
    	return generateID(cs,32);
    }
    
    public static String generateID(Class<?> cs, int length){
    	StringBuffer sb = new StringBuffer(String.valueOf(new Date().getTime()*new Random().nextInt(cs.getSimpleName().length())));
    	char[] ns = cs.getSimpleName().toCharArray();
    	for(char n : ns){
    		if(n == '.')	continue;
    		sb.insert(new Random().nextInt(sb.length()), n);
    	}
    	String id = null;
    	if(sb.length() < length){
    		ns = getRandomStr(length - sb.length()).toCharArray();
    		for(char n : ns){
        		if(n == '.')	continue;
        		sb.insert(new Random().nextInt(sb.length()), n);
        	}
    		id =  sb.toString();
    	}else{
    		id = (sb.length() == length ? sb.toString() : sb.substring(0,length));
    	}
    	return id.toUpperCase();
    }
    
    private static String getRandomStr(int length){
    	String s = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	String str = "";
    	int i = 0;
    	Random random = new Random();
    	while(length-->0){
    		str+=s.substring((i = random.nextInt(s.length()-1)), i+1);
    	}
    	return str;
    }
	
}
