package org.kerwin.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class SearchPlatformLinkPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_TEXT = "Input App Name";
	private JFileChooser jfChooser;
	private JTextField mainField;
	private JPopupMenu resultPopup;
	private JPanel resultPanel;
	private JButton selectBtn;
	private JScrollPane scrollPanel;
	private JButton offBtn;
	private JButton onBtn;
	private JButton refreshBtn;
	private Map<String, Link> links;
	private List<File> urls;
	private File linkFolder;
	private File linkXML;
	private File cfgFile;
	private File selfLinkFolder;
	private String citem;
	private Properties cfgPropertyFile;
	private Properties xmlPropertyFile;
	private JPanel setPanel;
	private JTextField ipf;
	private JTextField portf;
	private JTextField userf;
	private JTextField pwdf;
	private String linkXMLPath;
	private String cfgFilePath;
	private String selfLinkFolderPath;
	private Window parent;
	private int setPanelHeight;
	private JComboBox jcb;

	public SearchPlatformLinkPanel(Window parent) {
		this.parent = parent;
		this.parent.setSize(this.parent.getWidth(),
				this.parent.getHeight() + 23);
		linkXMLPath = "./Tools/linkXML";
		cfgFilePath = "./Tools/cfgFile";
		selfLinkFolderPath = "./Tools/平台地址";
		cfgPropertyFile = new Properties();
		try {
			initial(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initial(String arg) throws Exception {
		boolean flag = false;
		cfgFile = new File(cfgFilePath);
		if (cfgFile.exists()) {
			cfgPropertyFile.load(new FileInputStream(cfgFile));
			citem = arg != null && !"".equals(arg) ? arg : cfgPropertyFile
					.getProperty("citem");
			if ("offLine".equals(citem)) {
				flag = true;
				initialOffLine();
			} else if ("onLine".equals(citem)) {
				flag = true;
				initialOnLine();
			}
		}
		cfgFile.getParentFile().mkdirs();
		if (!flag) {
			offBtn = new JButton("offLine");
			offBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						citem = "offLine";
						cfgPropertyFile.setProperty("citem", "offLine");
						cfgPropertyFile.store(new FileOutputStream(cfgFile),
								"cfgFile");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					initialOffLine();
				}
			});
			onBtn = new JButton("onLine");
			onBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						citem = "onLine";
						cfgPropertyFile.setProperty("citem", "onLine");
						cfgPropertyFile.store(new FileOutputStream(cfgFile),
								"cfgFile");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					initialOnLine();
				}
			});
			removeAll();
			setLayout(new GridLayout(1, 2));
			add(offBtn);
			add(onBtn);
			validate();
		}
	}

	private void initialSetPanel() {
		setPanel = new JPanel(new BorderLayout());
		ipf = new JTextField();
		portf = new JTextField();
		userf = new JTextField();
		pwdf = new JTextField();
		JButton sBtn = new JButton("Submit");
		JButton cBtn = new JButton("Cancel");

		JPanel tjp = new JPanel(new GridLayout(4, 1));
		tjp.add(new JLabel("IP", JLabel.RIGHT));
		tjp.add(new JLabel("port", JLabel.RIGHT));
		tjp.add(new JLabel("user", JLabel.RIGHT));
		tjp.add(new JLabel("pwd", JLabel.RIGHT));
		setPanel.add(tjp, BorderLayout.WEST);

		tjp = new JPanel(new GridLayout(4, 1));
		tjp.add(ipf);
		tjp.add(portf);
		tjp.add(userf);
		tjp.add(pwdf);
		setPanel.add(tjp);

		tjp = new JPanel(new GridLayout(1, 2));
		tjp.add(sBtn);
		tjp.add(cBtn);

		sBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ipf.getText();
				String port = portf.getText();
				String user = userf.getText();
				String pwd = pwdf.getText();
				if (ip == null || "".equals(ip)) {
					JOptionPane.showMessageDialog(parent, "please input ip");
					ipf.grabFocus();
				} else if (port == null || "".equals(port)) {
					JOptionPane.showMessageDialog(parent, "please input port");
					portf.grabFocus();
				} else if (user == null || "".equals(user)) {
					JOptionPane.showMessageDialog(parent, "please input user");
					userf.grabFocus();
				} else if (pwd == null || "".equals(pwd)) {
					JOptionPane.showMessageDialog(parent, "please input pwd");
					pwdf.grabFocus();
				} else {
					try {
						cfgPropertyFile.setProperty("IP", ip);
						cfgPropertyFile.setProperty("PORT", port);
						cfgPropertyFile.setProperty("USER", user);
						cfgPropertyFile.setProperty("PWD", pwd);
						cfgPropertyFile.store(new FileOutputStream(cfgFile),
								"cfgFile");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					loadDBData();
				}
			}
		});

		cBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDBData();
			}
		});
		removeAll();
		setLayout(new BorderLayout());
		add(setPanel);
		add(tjp, BorderLayout.SOUTH);
		if (parent.getHeight() != setPanelHeight) {
			setPanelHeight = parent.getHeight() + 100;
			parent.setSize(parent.getWidth(), setPanelHeight);
		}
		validate();
	}

	private void createLink(String fileName, String URL) {
		if (fileName == null || "".equals(fileName) || URL == null
				|| "".equals(URL))
			return;
		try {
			/*
			 * String file =
			 * java.net.URLEncoder.encode(fileName+".url","UTF-8"); StringBuffer
			 * bf=new StringBuffer(); bf.append("[InternetShortcut]\r\n");
			 * bf.append("URL="+URL+"\n"); bf.append("IDList=\n");
			 * bf.append("IconFile="+URL+"favicon.ico\n");
			 * bf.append("IconIndex=1\r\n"); out.print(bf.toString());
			 */
			String templateContent = "[InternetShortcut]" + "\n" + "URL= "
					+ URL + "\n" + "IconIndex=0" + "\n" + "IconFile=" + URL
					+ "/favicon.ico";
			String realfilename = fileName + ".url";
			// FileSystemView fsv = FileSystemView.getFileSystemView();
			// String upurl = fsv.getHomeDirectory().toString();
			// String filename = upurl + "/" + realfilename;
			File linkFile = new File(realfilename);
			if (linkFile.exists()) {
				linkFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(realfilename);// 建立文件输出流
			byte tag_bytes[] = templateContent.getBytes();
			fos.write(tag_bytes);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDBData() {
		try {
			cfgPropertyFile.load(new FileInputStream(cfgFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ip = cfgPropertyFile.getProperty("IP");
		String port = cfgPropertyFile.getProperty("PORT");
		String user = cfgPropertyFile.getProperty("USER");
		String pwd = cfgPropertyFile.getProperty("PWD");
		boolean flag = true;
		if (ip == null || "".equals(ip) || port == null || "".equals(port)
				|| user == null || "".equals(user) || pwd == null
				|| "".equals(pwd)) {
			flag = false;
		} else {
			try {
				// 数据库驱动
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// 数据库连接字符串及登录的用户名及密码
				Connection conn = DriverManager.getConnection(
						"jdbc:sqlserver://" + ip + ":" + port
								+ ";DatabaseName=CSIP", user, pwd);
				if (conn == null) {
					flag = false;
				} else {
					String rstr = selfLinkFolderPath + "/外网";
					String lstr = selfLinkFolderPath + "/本地";
					File selfLinkFolderR = new File(rstr);
					File selfLinkFolderL = new File(lstr);
					if (!selfLinkFolderR.exists()) {
						selfLinkFolderR.mkdirs();
					}
					if (!selfLinkFolderL.exists()) {
						selfLinkFolderL.mkdirs();
					}
					xmlPropertyFile = new Properties();
					createLink(lstr + "/企业综合管理信息平台",
							"http://127.0.0.1:86/CSIP/LEAP/Login.html");
					xmlPropertyFile.setProperty(
							"http://127.0.0.1:86/CSIP/LEAP/Login.html",
							"[本地]企业综合管理信息平台");
					createLink(rstr + "/企业综合管理信息平台",
							"http://203.86.8.8/LEAP/Login.html");
					xmlPropertyFile.setProperty(
							"http://203.86.8.8/LEAP/Login.html",
							"[外网]企业综合管理信息平台");
					Statement stmt = conn.createStatement();
					ResultSet result = stmt
							.executeQuery("select area,systemname,systemshowname from leapareasystem where area is not null");
					while (result.next()) {
						String nurl = "http://127.0.0.1:86/CSIP/LEAP/Login/"
								+ result.getString("area") + "/"
								+ result.getString("systemname").trim()
								+ "/Login.html";
						String wurl = "http://203.86.8.8/LEAP/Login/"
								+ result.getString("area") + "/"
								+ result.getString("systemname").trim()
								+ "/Login.html";
						createLink(
								lstr
										+ "/"
										+ result.getString("systemshowname")
												.trim(), nurl);
						createLink(
								rstr
										+ "/"
										+ result.getString("systemshowname")
												.trim(), wurl);
						xmlPropertyFile.setProperty(nurl, "[本地]"
								+ result.getString("systemshowname").trim());
						xmlPropertyFile.setProperty(wurl, "[外网]"
								+ result.getString("systemshowname").trim());
					}
					result = stmt
							.executeQuery("select name,showname from leapresourcetable where syscode > 12.001012 and syscode < 12.001013 and sourcetype = 1");
					while (result.next()) {
						if ("7459fce952e042b3a80fe88fb5dabd51"
								.equalsIgnoreCase(result.getString("showname")))
							continue;
						String nurl = "http://127.0.0.1:86/CSIP/"
								+ result.getString("name").trim();
						String wurl = "http://203.86.8.8/"
								+ result.getString("name").trim();
						createLink(lstr + "/" + result.getString("showname"),
								nurl);
						createLink(rstr + "/" + result.getString("showname"),
								wurl);
						xmlPropertyFile.setProperty(nurl, "[本地]"
								+ result.getString("showname").trim());
						xmlPropertyFile.setProperty(wurl, "[外网]"
								+ result.getString("showname").trim());
					}
					if (result != null) {
						result.close();
					}
					if (stmt != null) {
						stmt.close();
					}
					conn.close();
					xmlPropertyFile.store(new FileOutputStream(linkXML),
							"linkXML");
				}
			} catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
		}
		if (!flag) {
			if (JOptionPane.showConfirmDialog(this,
					"input valid connect info.", "message",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				initialSetPanel();
			} else {
				flag = true;
			}
		}
		if (flag) {
			if (parent.getHeight() == setPanelHeight) {
				parent.setSize(parent.getWidth(), parent.getHeight() - 100);
			}
			loadLinkXML();
		}
	}

	private void changeToInit() {
		if ("onLine".equals(citem)) {
			linkXML = new File(linkXMLPath);
			selfLinkFolder = new File(selfLinkFolderPath);
			if (!linkXML.exists()) {
				try {
					selfLinkFolder.mkdirs();
					// 如果文件不存在。则创建一个带默认节点的文件
					PrintStream out = new PrintStream(linkXML);
					out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Links></Links>");
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				loadDBData();
			} else {
				loadLinkXML();
			}
		} else {
			jfChooser = new JFileChooser();
			jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			selectBtn = new JButton("Choose Links Path");
			selectBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (jfChooser.showOpenDialog((Component) (e.getSource())) == JFileChooser.APPROVE_OPTION) {
						linkFolder = jfChooser.getSelectedFile();
						if (linkFolder.exists() && !linkFolder.isFile()) {
							try {
								cfgPropertyFile.setProperty("linkFolder",
										linkFolder.getPath());
								cfgPropertyFile.store(new FileOutputStream(
										cfgFile), "cfgFile");
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							loadLinkFolder();
						}
					}
				}
			});
			removeAll();
			setLayout(new BorderLayout());
			add(selectBtn);
			validate();
		}
	}

	private void changeToMain() {
		mainField = new JTextField(DEFAULT_TEXT);
		mainField.setForeground(new Color(212, 208, 200));
		mainField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		mainField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (DEFAULT_TEXT.equals(mainField.getText())) {
					mainField.setText("");
					mainField.setForeground(Color.BLACK);
					updateResultPanel(null);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if ("".equals(mainField.getText())) {
					mainField.setText(DEFAULT_TEXT);
					mainField.setForeground(new Color(212, 208, 200));
				}
			}

		});

		mainField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {// 这是更改操作的处理
				updateResultPanel(mainField.getText().trim());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {// 这是插入操作的处理
				updateResultPanel(mainField.getText().trim());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {// 这是删除操作的处理
				updateResultPanel(mainField.getText().trim());
			}
		});

		removeAll();
		setLayout(new BorderLayout());
		add(mainField);
		jcb = new JComboBox(new String[] { "在线", "离线" });
		jcb.setSelectedIndex(("onLine".equals(citem)) ? 0 : 1);
		jcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (jcb.getSelectedIndex() == 0) {
						cfgPropertyFile.setProperty("citem", "onLine");
						cfgPropertyFile.store(new FileOutputStream(cfgFile),
								"cfgFile");
						initial("onLine");
					} else {
						cfgPropertyFile.setProperty("citem", "offLine");
						cfgPropertyFile.store(new FileOutputStream(cfgFile),
								"cfgFile");
						initial("offLine");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		add(jcb, BorderLayout.WEST);
		if ("onLine".equals(citem)) {
			refreshBtn = new JButton("刷新");
			refreshBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadDBData();
				}
			});
			add(refreshBtn, BorderLayout.EAST);
		}
		validate();
		mainField.requestFocus(true);
	}

	private void initialOffLine() {
		try {
			cfgPropertyFile.load(new FileInputStream(cfgFile));
			String str = cfgPropertyFile.getProperty("linkFolder");
			if (str != null && !"".equals(str)) {
				linkFolder = new File(str);
				if (linkFolder.exists() && !linkFolder.isFile()) {
					loadLinkFolder();
					return;
				}
			}
			changeToInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialOnLine() {
		try {
			linkXML = new File(linkXMLPath);
			if (linkXML.exists()) {
				loadLinkXML();
				return;
			}
			changeToInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadLinkFolder() {
		urls = new ArrayList<File>();
		links = new Hashtable<String, Link>();
		addFile(linkFolder);
		for (File url : urls) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(url)));
				String str = null;
				while ((str = br.readLine()) != null) {
					if (str.length() > 4) {
						if ("URL=".equalsIgnoreCase(str.substring(0, 4))) {
							String k = str.substring(4, str.length());
							if (links.containsKey(k)) {
								continue;
							}
							String name = url.getName().substring(0,
									url.getName().length() - 4);
							if (!linkFolder.getPath().equalsIgnoreCase(
									url.getParent())) {
								String tag = getNameTag(url.getParent());
								name = tag == null ? "" + name : "[" + tag
										+ "]" + name;
							}
							Link link = new Link(
									k,
									name,
									makeStringByStringSet(getPinyin(name, true)));
							links.put(k, link);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (links.size() < 1) {
			JOptionPane.showMessageDialog(parent, "workspace is null");
			changeToInit();
		} else {
			changeToMain();
		}
	}

	private void loadLinkXML() {
		try {
			xmlPropertyFile = new Properties();
			xmlPropertyFile.load(new FileInputStream(linkXML));
			Set<String> ss = xmlPropertyFile.stringPropertyNames();
			if (ss != null && ss.size() != 0) {
				links = new Hashtable<String, Link>();
				for (String s : ss) {
					if (links.containsKey(s)) {
						continue;
					}
					String name = xmlPropertyFile.getProperty(s);
					if (name != null && name.contains("<Links></Links>"))
						continue;
					Link link = new Link(s, name,
							makeStringByStringSet(getPinyin(name, true)));
					links.put(s, link);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		changeToMain();
	}

	private String getNameTag(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (file.getName().equalsIgnoreCase(linkFolder.getName())) {
			return null;
		} else {
			String name = getNameTag(file.getParent());
			return name == null ? "" + file.getName() : name + "、"
					+ file.getName();
		}
	}

	private void updateResultPanel(String name) {
		if (links == null || links.size() < 1) {
			return;
		}
		Link[] vs = links.values().toArray(new Link[links.size()]);
		Arrays.sort(vs, new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}

			@Override
			public boolean equals(Object obj) {
				return this.equals(((Link) obj).getName());
			}
		});
		List<Link> ls = new ArrayList<Link>();
		for (Link v : vs) {
			if (name == null || "".equals(name)) {
				ls.add(v);
				continue;
			}
			if (v.getSxzm()
					.toLowerCase()
					.contains(
							makeStringByStringSet(getPinyin(name, true))
									.toLowerCase())) {
				ls.add(v);
			}
		}
		if (ls.size() == 0) {
			return;
		}
		resultPopup = new JPopupMenu();
		resultPopup.setLayout(new BorderLayout());
		resultPanel = new JPanel(new GridLayout(ls.size(), 1, 0, 5));
		scrollPanel = new JScrollPane(resultPanel);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(10);
		scrollPanel.setBorder(BorderFactory.createEmptyBorder());
		scrollPanel
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultPopup.add(scrollPanel);
		for (Link l : ls) {
			JLabel jl = new JLabel(l.getName(), JLabel.LEFT);
			jl.setName(l.getUrl());
			jl.setCursor(new Cursor(Cursor.HAND_CURSOR));
			jl.addMouseListener(getPopupListItemMouseListener());
			resultPanel.add(jl);
		}
		int height = ls.size() * mainField.getHeight()
				+ (ls.size() > 0 ? ls.size() : 0) * 5;
		if (height >= 600) {
			height = 600;
		}
		resultPopup.setPopupSize(mainField.getWidth(), height);
		resultPopup.show(mainField, 0, mainField.getHeight());
		mainField.requestFocus(true);
	}

	private void addFile(File folder) {
		if (folder == null || !folder.exists()) {
			return;
		}
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				urls.add(file);
			} else {
				addFile(file);
			}
		}
	}

	private MouseListener getPopupListItemMouseListener() {
		return new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				Set<String> ks = links.keySet();
				String url = null;
				for (String k : ks) {
					if (k.trim().equals(e.getComponent().getName().trim())) {
						url = k.trim();
						break;
					}
				}
				if (url == null || "".equals(url))
					return;
				final String tu = url;
				if (e.getClickCount() == 1
						&& e.getButton() != MouseEvent.BUTTON3) {
					new Thread(new Runnable() {
						public void run() {
							try {
								if (java.awt.Desktop.isDesktopSupported()) {
									// 创建一个URI实例
									java.net.URI uri = java.net.URI.create(tu);
									// 获取当前系统桌面扩展
									java.awt.Desktop dp = java.awt.Desktop
											.getDesktop();
									// 判断系统桌面是否支持要执行的功能
									if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
										// 获取系统默认浏览器打开链接
										dp.browse(uri);
									}
								} else {
									Runtime.getRuntime().exec(
											"rundll32 url.dll,FileProtocolHandler "
													+ tu);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
					mainField.setText("");
					resultPopup.setVisible(false);
				} else {
					// 获得系统剪切板
					Clipboard clipboard = getToolkit().getSystemClipboard();
					// 复制到剪切板上
					StringSelection ss = new StringSelection(url);
					clipboard.setContents(ss, null);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JLabel jl = (JLabel) e.getSource();
				jl.setToolTipText(jl.getText());
			}

		};
	}

	class Link {

		private String url;
		private String name;
		private String sxzm;

		public Link(String url, String name, String sxzm) {
			this.url = url;
			this.name = name;
			this.sxzm = sxzm;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setSxzm(String sxzm) {
			this.sxzm = sxzm;
		}

		public String getSxzm() {
			return sxzm;
		}

	}

	public static String makeStringByStringSet(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append(s);
			} else {
				str.append(s + ",");
			}
			i++;
		}
		return str.toString().toLowerCase();
	}

	public static Set<String> getPinyin(String src) {
		return getPinyin(src, false);
	}

	public static Set<String> getPinyin(String src, boolean isSZM) {
		if (src == null || "".equals(src.trim())) {
			return null;
		}
		char[] srcChar;
		srcChar = src.toCharArray();
		// 汉语拼音格式输出类
		HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

		// 输出设置，大小写，音标方式等
		hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

		String[][] temp = new String[src.length()][];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			// 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
			if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
				try {
					temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i],
							hanYuPinOutputFormat);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else if (((int) c >= 65 && (int) c <= 90)
					|| ((int) c >= 97 && (int) c <= 122)) {
				temp[i] = new String[] { String.valueOf(srcChar[i]) };
			} else {
				temp[i] = new String[] { "" };
			}
		}
		String[][] temp1 = null;
		if (isSZM) {
			temp1 = new String[temp.length][];
			for (int i = 0; i < temp1.length; i++) {
				String[] ss = temp[i];
				String[] ss1 = new String[ss.length];
				int j = 0;
				for (; j < ss.length; j++) {
					if (ss[j] == null || ss[j].length() == 0) {
						continue;
					}
					ss1[j] = ss[j].substring(0, 1);
				}
				if (j == 1 && "".equals(temp[i][0])) {
					temp1[i] = new String[] { "" };
				} else {
					temp1[i] = ss1;
				}
			}
		} else {
			temp1 = temp;
		}
		String[] pingyinArray = Exchange(temp1);
		Set<String> pinyinSet = new HashSet<String>();
		for (int i = 0; i < pingyinArray.length; i++) {
			pinyinSet.add(pingyinArray[i]);
		}
		return pinyinSet;
	}

	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			return strJaggedArray;
		}
	}

}