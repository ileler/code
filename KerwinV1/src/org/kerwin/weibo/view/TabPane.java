package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.kerwin.weibo.service.Theme;

/**
 * @author O0O
 * 自定义标签面板
 */
public class TabPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel headPane;	//标签头部面板
	private Vector<JLabel> heads;	//标签头部所包含的JLabel
	private Map<String,JComponent> bodys;	//标签头部所对应内容
	private JPanel bodyPane;	//标签身体面板
	private JLabel currentLabel;	//当前面板
	private ImageIcon tabPaneHover;
	private ImageIcon tabPaneClick;

	public TabPane(){
		setLayout(new BorderLayout(0,0));		//设置当前面板的布局样式
		setOpaque(false);		//设置当前面板透明
		tabPaneHover = Theme.getTabPaneHover();
		tabPaneClick = Theme.getTabPaneClick();
		heads = new Vector<JLabel>();		//初始化头部容器
		headPane = new JPanel(new BorderLayout(0,0));		//初始化头部面板
		headPane.setFont(new Font(Font.DIALOG,Font.PLAIN,18));	//设置头部面板的字体
		bodys = new Hashtable<String,JComponent>();		//实例化身体容器
		bodyPane = new JPanel(new BorderLayout(0,0));		//实例化身体面板
		headPane.setOpaque(false);		//设置头部面板透明
		bodyPane.setOpaque(false);		//设置身体面板透明
		add(this.headPane,BorderLayout.NORTH);		//将头部面板添加到当前面板的上方
		add(this.bodyPane,BorderLayout.CENTER);	//将身体面板添加到当前面板的中心
	}
	
	/**
	 * @param tabName	需要添加的标签的名字
	 * @return		返回是否添加成功
	 * 此方法是用来添加标签
	 */
	public boolean addTab(String tabName){
		//实例化一个空的面板对应此头部标签
		JPanel body = new JPanel(new BorderLayout());
		body.setOpaque(false);		//设置身体面板透明
		body.add(new JLabel(tabName+"内容",JLabel.CENTER));	//给身体面板添加默认的标签
		return addTab(tabName, body);
	}
	
	/**
	 * @param tabName	需要添加的标签的名字
	 * @return		返回是否添加成功
	 * 此方法是用来添加标签
	 */
	public boolean addTab(String tabName, JComponent body){
		if(tabName == null)
			return false;
		if(body == null)
			addTab(tabName);
		Iterator<JLabel> iterator = heads.iterator();
		//遍历标签头部容器
		while(iterator.hasNext()){
			JLabel jl = iterator.next();
			//如果头部容器包含跟tabName名字一样的
			if(jl.getText().equals(tabName)){
				iterator.remove();		//移除该标签
				bodys.remove(jl);	//并移除对应的身体
				break;
			}
		}
		//根据名字实例化标签组件
		JLabel head = new JLabel(tabName,JLabel.CENTER);
		head.setFont(new Font("System",this.getFont().getStyle(),16)); //设置标签的字体
		head.setOpaque(false);		//设置标签透明
		head.setHorizontalTextPosition(JLabel.CENTER);		//设置标签文本的水平对齐方式
		head.setVerticalTextPosition(JLabel.CENTER);	//设置标签文本的垂直对齐方式
		head.setCursor(new Cursor(Cursor.HAND_CURSOR));		//设置标签的鼠标样式
		
		heads.add(head);	//将标签添加到头部容器
		bodys.put(tabName,body);	//将身体添加到内容面板
		
		addListener(head);	//给该标签添加事件
		
		refreshComponent();	//刷新组件
		
		return true; 	//返回
	}
	
	public JComponent getCurrentBody(){
		return getTabContent(currentLabel.getText());
	}
	
	/**
	 * @param tabName	标签头名字
	 * @return		返回标签身体
	 * 根据标签头名字得到标签身体
	 */
	public JComponent getTabContent(String tabName){
		return bodys.get(tabName);
	}
	
	/**
	 * @param index
	 * @return		返回标签身体
	 * 根据标签头索引得到标签身体
	 */
	public JComponent getTabContent(int index){
		if(heads.isEmpty() || index < 0 || index >= heads.size())
			return null;
		return bodys.get(heads.get(index).getText());
	}
	
	/**
	 * 刷新组件
	 * 默认第一个标签被选中
	 */
	public void refreshComponent(){
		refreshComponent(heads.get(0));
	}
	
	/**
	 * @param jl 需要被设置选择的标签
	 * 刷新组件并设置选择标签
	 */
	public void refreshComponent(JLabel jl){
		headPane.removeAll();		//清空头部内容
		Iterator<JLabel> iterator = heads.iterator();
		while(iterator.hasNext()){
			JLabel j = iterator.next();
			//将头部内容容器标签添加到头部面板
			this.headPane.add(j);
		};
		//设置头部面板布局管理
		headPane.setLayout(new GridLayout(1,headPane.getComponentCount(),1,0));
		//设置被选择标签
		getFocus(jl.getText());
	}
	
	//根据索引移除标签
	public void remove(int index){
		if(heads != null && heads.size() >= index)	heads.remove(index);
		if(bodys != null && bodys.size() >= index)	bodys.remove(index);
		refreshComponent();
	}
	
	//移除所有标签
	public void removeAll(){
		if(heads != null)	heads.clear();
		if(bodys != null)	bodys.clear();
		refreshComponent();
	}
	
	/**
	 * @param index	设置标签的索引
	 * @param name	//设置标签的名字
	 */
	private void setTab(int index,String tabName){
		JComponent body = bodys.get(tabName);
		if(!addTab(tabName,body))
			return;
		heads.add(index, heads.get(heads.size()-1));
		heads.remove(heads.size()-1);
	}
	
	/**
	 * @param head	给标签头部添加事件
	 */
	private void addListener(JLabel head){
		head.addMouseListener(new MouseAdapter(){
			
			private int startX;	//记录鼠标按下时的X坐标
			private int endX;		//记录鼠标松开时的X坐标
			
			//鼠标进入组件时响应
			public void mouseEntered(MouseEvent e){
				JLabel src = ((JLabel)e.getSource());
				if(src.equals(currentLabel))	//如果事件源等于当前标签、则不处理
					return;
				//设置事件源的图标
				src.setIcon(tabPaneHover);
			}
			
			//鼠标离开组件时响应
			public void mouseExited(MouseEvent e){
				JLabel src = ((JLabel)e.getSource());
				if(src.equals(currentLabel))	//如果事件源等于当前标签、则不处理
					return;
				//设置事件源的图标
				src.setIcon(null);
			}
			
			//下面处理使该组件支持拖拽移动位置
			
			//鼠标在组件上按下时响应
			public void mousePressed(MouseEvent e){
				startX = e.getX();	//记录按下的X坐标
			}
			
			//鼠标在组件上松开时响应
			public void mouseReleased(MouseEvent e){
				JLabel src = (JLabel)e.getSource();
				endX = e.getX();	//记录松开的X坐标
				int index = 0;
				//计算需要移动到的地方的索引
				if(endX < 0){
					//向左移动
					index = (startX-endX)/(getWidth()/heads.size());
					index = (heads.indexOf(src)-index);
				}else{
					//向右移动
					index = (endX-startX)/(getWidth()/heads.size());
					index = (heads.indexOf(src)+index);
				}
				setTab(index, src.getText());
				//刷新组件
				refreshComponent(src);
			}
			
		});
	}
	
	/**
	 * @param tabName	需要被选中的标签的名字
	 */
	private void getFocus(String tabName){
		if(tabName == null)
			return;
		for(Component c : this.headPane.getComponents()){
			JLabel jl = (JLabel)c;
			if(jl.getText().equals(tabName)){
				jl.setIcon(tabPaneClick);
				currentLabel = jl;
				bodyPane.removeAll();
				repaint();
				bodyPane.add(bodys.get(jl.getText()));
				validate();
			}else{
				jl.setIcon(null);
			}
		}
	}
}
