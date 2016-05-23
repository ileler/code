package org.kerwin.tools.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.kerwin.task.KerwinTasker;

public class CMDPanel extends JScrollPane {
	
	private static final long serialVersionUID = 1L;
	private String title;
	private JTextArea cmdArea;

	public CMDPanel(String str){
		title = str;
		cmdArea = new JTextArea(title);
		cmdArea.setEditable(false);					//不允许编辑
//		cmdArea.setLineWrap(true);        			//激活自动换行功能 
//		cmdArea.setWrapStyleWord(true);            	//激活断行不断字功能
		cmdArea.setForeground(Color.LIGHT_GRAY);	//字体颜色
		cmdArea.setSelectionColor(Color.WHITE);		//选中颜色
		cmdArea.setBackground(Color.BLACK);			//背景颜色
		cmdArea.setFont(new Font("Microsoft YaHei",Font.PLAIN,14));	//设置字体
		cmdArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2){
					cmdArea.setText(title);
				}
			}
		});
		
		setOpaque(false);
		setViewportView(cmdArea);
		getViewport().setOpaque(false);
		getVerticalScrollBar().setUnitIncrement(10);
		setBorder(BorderFactory.createEmptyBorder());
		getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	//设置水平滚动条从不显示
	}
	
	public void exec(final String command) throws Exception{
		if(command == null || command.isEmpty())	return;
		cmdArea.append(command);
		cmdArea.repaint();
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> cmdarray = KerwinTasker.formatCommand(command);
				try{
//					File dir = new File(getClass().getResource("/").toURI().getPath());
//					Process process = Runtime.getRuntime().exec(cmdarray.toArray(new String[cmdarray.size()]),null,dir);
					Process process = new ProcessBuilder(cmdarray).redirectErrorStream(true).start();
					BufferedReader rbr = new BufferedReader(new InputStreamReader(process.getInputStream(),System.getProperty("sun.jnu.encoding")));
					String str = null;
					while((str = rbr.readLine()) != null){
						cmdArea.append("\n"+str);
						new Thread(new Runnable() {
							@Override
							public void run() {
								cmdArea.repaint();
							}
						}).start();
					}
					process.waitFor();
				}catch(Exception e){
					cmdArea.append("\n'"+cmdarray.get(0)+"' is not an internal or external command, not running program or batch file.");
					e.printStackTrace();
				}
				cmdArea.append("\n\n"+title);
			}
		}).start();
	}

}
