package org.kerwin.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchPwdPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String DEFAULTUSER_TEXT = "Input User Name";
	private JComboBox jcb;
	private JTextField jtf;
	private JButton searchBtn;
	private Window parent;
	
	public SearchPwdPanel(Window parent){
		this.parent = parent;
		this.parent.setSize(this.parent.getWidth(), this.parent.getHeight() + 20);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(getWidth(), 20));
		
		jcb = new JComboBox(new String[] { "外网", "本地" });
		add(jcb, BorderLayout.WEST);
		
		jtf = new JTextField(DEFAULTUSER_TEXT);
		jtf.setForeground(new Color(212, 208, 200));
		jtf.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jtf.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (DEFAULTUSER_TEXT.equals(jtf.getText())) {
					jtf.setText("");
					jtf.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if ("".equals(jtf.getText())) {
					jtf.setText(DEFAULTUSER_TEXT);
					jtf.setForeground(new Color(212, 208, 200));
				}
			}

		});
		add(jtf);
		
		searchBtn = new JButton("查询");
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String url = null;
				if ("外网".equals(jcb.getSelectedItem())) {
					url = "http://203.86.8.8/7459fce952e042b3a80fe88fb5dabd51/?par1="
							+ jtf.getText() + "&par2=super";
				} else {
					url = "http://127.0.0.1:86/CSIP/7459fce952e042b3a80fe88fb5dabd51/?par1="
							+ jtf.getText() + "&par2=super";
				}
				try {
					if (java.awt.Desktop.isDesktopSupported()) {
						// 创建一个URI实例
						java.net.URI uri = java.net.URI.create(url);
						// 获取当前系统桌面扩展
						java.awt.Desktop dp = java.awt.Desktop.getDesktop();
						// 判断系统桌面是否支持要执行的功能
						if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
							// 获取系统默认浏览器打开链接
							dp.browse(uri);
						}
					} else {
						Runtime.getRuntime().exec(
								"rundll32 url.dll,FileProtocolHandler " + url);
					}
					jtf.setText(DEFAULTUSER_TEXT);
					jtf.setForeground(new Color(212, 208, 200));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		add(searchBtn, BorderLayout.EAST);
	}

}
