package org.kerwin.shutdownui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Kerwin Bryant XML文件操作类
 */
public class XMLUtil {

	private static DocumentBuilderFactory builderFactory; // 文档构建工厂对象
	private static DocumentBuilder builder; // 文档构建对象
	private static TransformerFactory transFactory; // 文档转换工厂对象
	private static Transformer trans; // 文档转换对象

	// 静态代码块、为静态字段初始化
	static {
		builderFactory = DocumentBuilderFactory.newInstance();
		transFactory = TransformerFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		try {
			trans = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此方法用来根据文档构建对象的parse方法加载一个xml文件
	 * 
	 * @param file
	 *            将要转换的文件
	 * @return 返回转换后的文档对象
	 */
	public static Document getDocument(File file) {
		Document doc = null;
		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 此方法用来根据文档构建对象的parse方法加载一个xml文件
	 * 
	 * @param file
	 *            将要转换的文件
	 * @return 返回转换后的文档对象
	 */
	public static synchronized Document getDocument(InputStream file) {
		Document doc = null;
		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 为指定文档根节点添加子节点
	 * 
	 * @param doc
	 *            将要添加节点的文档对象
	 * @param file
	 *            将要存储文档内容的文件对象
	 * @param nodeName
	 *            节点名称
	 * @param attrs
	 *            节点属性集合
	 * @return 返回操作结果
	 */
	public static Element addElement(Document doc, File file, String nodeName,
			Map<String, String> attrs) {
		// doc.getDocumentElement() 获取文档根节点
		return addElement(doc, doc.getDocumentElement(), file, nodeName, attrs);
	}

	/**
	 * 为指定文档的指定节点添加子节点
	 * 
	 * @param doc
	 *            将要添加节点的文档对象
	 * @param e
	 *            将要添加子节点的节点
	 * @param file
	 *            将要存储文档内容的文件对象
	 * @param nodeName
	 *            节点名称
	 * @param attrs
	 *            节点属性集合
	 * @return 返回操作结果
	 */
	public static Element addElement(Document doc, Element e, File file,
			String nodeName, Map<String, String> attrs) {
		if (null == doc || null == e || null == file || null == nodeName)
			return null;
		// appendChild 添加节点并返回添加成功的节点
		Element newElement = doc.createElement(nodeName);
		e.appendChild(newElement);
		if (attrs != null && attrs.size() != 0) {
			// 得到属性集合的迭代器
			Iterator<Map.Entry<String, String>> iter = attrs.entrySet()
					.iterator();
			while (iter.hasNext()) {
				// 遍历集合、为节点设置属性
				Map.Entry<String, String> me = iter.next();
				newElement.setAttribute(me.getKey(), me.getValue());
			}
		}
		// 保存修改、并返回结果
		return saveAlter(doc, file) ? newElement : null;
	}

	public static boolean delElement(Document doc, Element e, File file) {
		if (null == doc || null == e || null == file)
			return false;
		return e.getParentNode().removeChild(e) != null ? saveAlter(doc, file)
				: false;
	}

	/**
	 * 保存Document文档内容到指定文件
	 * 
	 * @param doc
	 *            要保存的Document对象
	 * @param file
	 *            用来保存文档的文件对象
	 * @return 返回保存结果
	 */
	public static boolean saveAlter(Document doc, File file) {
		if (null == trans || null == doc || null == file || !file.isFile())
			return false;
		DOMSource domSource = new DOMSource(doc); // 根据Document对象实例化一个DOMSource对象
		StreamResult streamResult = new StreamResult(file); // 根据File对象实例化一个StreamResult对象
		try {
			trans.transform(domSource, streamResult); // 将Domcument文档内容写入File文件
			return true;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return false;
	}
}
