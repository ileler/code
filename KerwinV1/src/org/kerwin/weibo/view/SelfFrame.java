package org.kerwin.weibo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.kerwin.weibo.service.Theme;
import org.kerwin.weibo.util.FrameUtil;

import com.sun.awt.AWTUtilities;

public class SelfFrame extends JFrame implements MouseListener,MouseMotionListener{

	private static final long serialVersionUID = 1L;
	
	private BackgroundPanel bgPanel;
	private JPanel mainPanel;
	private SelfPanel contentPanel;
	private SelfPanel topPanel;
	private SelfPanel operPanel;
	private SelfPanel topTmpPanel;
	private JLabel titleLabel;
	private JLabel minLabel;
	private JLabel maxLabel;
	private JLabel closeLabel;
	private boolean isMin;
	private boolean isMax;
	private boolean isClose;
	private boolean max;
	private Rectangle tmpBound;
	private boolean isReSize;
	private boolean isPress;
	private int arc;
	private Color borderColor;
	private int pressX;	//鼠标按下时的X坐标位置
	private int pressY;	//鼠标按下时的Y坐标位置
	private int pressW;	//鼠标按下时窗体的宽
	private int pressH;	//鼠标按下时窗体的高
	private int pressLX;//鼠标按下时窗体X坐标位置
	private int pressLY;//鼠标按下时窗体Y坐标位置
	private int minW;
	private int minH;
	
	public static void main(String[] args){
		JFrame frame = new SelfFrame();
		JPanel jp = new JPanel();
		jp.setBackground(Color.BLACK);
		frame.setContentPane(jp);
		frame.setVisible(true);
	}
	
	public SelfFrame(){
		super();
		initial();
	}
	
	public SelfFrame(String title){
		this();
		titleLabel.setText(title);
	}
	
	private void initial(){
		arc = 11;	//圆角弧度
		isReSize = true;	//是否允许修改大小
		borderColor = Theme.getTransparentColor(120);	//边框颜色
		
		topTmpPanel = getTopPane();
		contentPanel = new SelfPanel(new BorderLayout());	//实例化内容面板
		mainPanel = getMainPane();
		mainPanel.add(contentPanel);	//将内容面板加入到主面板的中间
		mainPanel.add(topTmpPanel,BorderLayout.NORTH);	//将顶部面板加入到主面板顶部
		super.setContentPane(mainPanel);	//设置窗体的内容面板为此主面板
		
		setView();
		setTitle("Title");
		addComponentListener();
		super.setUndecorated(true);
		
	    pack();	//窗口自适应大小
	    setMinimumSize(getSize());	//设置窗体最小的大小
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//设置窗体的默认关闭效果
	    FrameUtil.locationToScreenCenter(this);	//定位窗体至屏幕中央
	    FrameUtil.setAllOpaque(mainPanel, false);	//设置指定组件及其所有子组件背景色为透明
		mainPanel.setBackground(Color.PINK);
		mainPanel.setOpaque(true);
	}
	
	/**
	 * 设置全局背景色和字体
	 */
	private void setView(){
		//设置全局背景色和字体
		Enumeration<Object> keys = UIManager.getDefaults().keys();  
	    Object key = null;  
	    Object value = null;  
	    while (keys.hasMoreElements()) {  
	        key = keys.nextElement();  
	        value = UIManager.get(key);  
	        if(key instanceof String ) {  
	            /**设置全局的背景色*/  
	            if(((String) key).endsWith(".background")) {  
	                UIManager.put(key, Color.white);  
	            }  
	        }  
	          
	        /**设置全局的字体*/  
	        if(value instanceof Font) {  
	            UIManager.put(key, new Font(Font.DIALOG,Font.PLAIN,12));
	        }  
	    }
	}
	
	/**
	 * @return 返回主面板
	 */
	private JPanel getMainPane(){
		//实例化主面板
		JPanel mainPanel = new JPanel(new BorderLayout());	
		//给主面板添加鼠标事件
		mainPanel.addMouseListener(this);	
		mainPanel.addMouseMotionListener(this);
		//设置主面板的边框
		mainPanel.setBorder(BorderFactory.createCompoundBorder(new Border() {
			@Override
			public void paintBorder(Component c, Graphics g, int x, int y,
					int width, int height) {
				 Graphics2D g2d = (Graphics2D)g.create(); 
				 Shape shape = g2d.getClip();  
				 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
				 g2d.setClip(shape);
				 g2d.setColor(borderColor);
				 g2d.drawRoundRect(x, y, width-1, height-1, arc, arc);
				 g2d.dispose();  
			}
			@Override
			public boolean isBorderOpaque() {
				return false;
			}
			@Override
			public Insets getBorderInsets(Component c) {
				return getInsets();
			}
		},BorderFactory.createLineBorder(Theme.getTransparentColor(), 2)));
		return mainPanel;
	}
	
	
	/**
	 * @return 返回顶部面板
	 */
	private SelfPanel getTopPane(){
		isMin = true;
		isMax = true;
		isClose = true;
		minLabel = getMinLabel();
		maxLabel = getMaxLabel();
		closeLabel = getCloseLabel();
		operPanel = new SelfPanel(new FlowLayout(FlowLayout.RIGHT,0,0));	//实例化操作面板、用来放置最小化和关闭等按钮
		titleLabel = new JLabel();	//实例化标题标签
		titleLabel.setFont(new Font(Font.DIALOG,Font.PLAIN,12));	//设置标题字体
		JPanel tp = new JPanel(new FlowLayout(FlowLayout.RIGHT));	//实例化临时面板
		tp.add(titleLabel);
		topPanel = new SelfPanel();
		SelfPanel ttp = new SelfPanel(new BorderLayout());	//实例化顶部面板
		ttp.add(tp,BorderLayout.WEST);	//将标题标签加入到顶部面板的左方
		ttp.add(topPanel);
		ttp.add(operPanel,BorderLayout.EAST);	//将操作面板加入到顶部面板的右方
		refreshOperPanel();
		return ttp;
	}
	
	/**
	 * 添加ComponentListener监听。且在窗体大小改变时，同步修正窗体的形状为圆角边框 
	 */
	private void addComponentListener(){
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Window win = (Window) e.getSource();  
				Frame frame = (win instanceof Frame) ? (Frame) win : null;  
				if ((frame != null) && ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0)) {  
					AWTUtilities.setWindowShape(win, null);  
				} else {  
				/** 设置圆角 */  
				AWTUtilities.setWindowShape(win,  
				    new RoundRectangle2D.Double(0.0D, 0.0D, win.getWidth(),  
				        win.getHeight(), arc, arc));  //arc貌似为单数比较圆滑一点
				}  
			}
		});
	}
	
	/**
	 * @return 返回最小化按钮
	 */
	private JLabel getMinLabel(){
		minLabel = new JLabel(Theme.getMinImage());
		minLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				minLabel.setIcon(Theme.getMinImage());
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				minLabel.setIcon(Theme.getOnMinImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		return minLabel;
	}
	
	/**
	 * @return 返回关闭按钮
	 */
	private JLabel getCloseLabel(){
		closeLabel = new JLabel(Theme.getCloseImage());
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				closeLabel.setIcon(Theme.getCloseImage());
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				closeLabel.setIcon(Theme.getOnCloseImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setVisible(false);
				switch (getDefaultCloseOperation()) {
				case JFrame.EXIT_ON_CLOSE:
					System.exit(0);
					break;
				case JFrame.DISPOSE_ON_CLOSE:
					dispose();
					break;
				default:
					break;
				}
			}
		});
		return closeLabel;
	}
	
	/**
	 * @return 返回关闭按钮
	 */
	private JLabel getMaxLabel(){
		maxLabel = new JLabel(Theme.getMaxImage());
		maxLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				maxLabel.setIcon(Theme.getMaxImage());
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				maxLabel.setIcon(Theme.getOnMaxImage());
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
//				setExtendedState(JFrame.MAXIMIZED_BOTH);
				 if(max){
					 max = false;
					 setBounds(tmpBound);
					 repaint();
					 setVisible(true);
				 }else{
					 Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
					 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					 int width = screenSize.width - insets.left - insets.right;
					 int height = screenSize.height - insets.top - insets.bottom;
					 int x = insets.left;
					 int y = insets.top;
					 tmpBound = getBounds();
					 max = true;
					 setBounds(x, y, width, height);
					 repaint();
					 setVisible(true);
				 }
			}
		});
		return maxLabel;
	}
	
	
	/**
	 * 设置是否显示最小化按钮
	 * @param flag
	 */
	public void setMinLabelEnabled(boolean flag){
		isMin = flag;
		refreshOperPanel();
	}
	
	/**
	 * 设置是否显示最大化面板
	 * @param flag
	 */
	public void setMaxLabelEnabled(boolean flag){
		isMax = flag;
		refreshOperPanel();
	}
	
	/**
	 * 设置是否显示关闭按钮
	 * @param flag
	 */
	public void setCloseLabelEnabled(boolean flag){
		isClose = flag;
		refreshOperPanel();
	}
	
	/**
	 * 刷新操作面板
	 */
	private void refreshOperPanel(){
		operPanel.removeAll();
		if(isMin)
			operPanel.add(minLabel);
		if(isMax)
			operPanel.add(maxLabel);
		if(isClose)
			operPanel.add(closeLabel);
		validate();
	}
	
	/**
	 * @return 返回窗体顶部面板
	 */
	public JPanel getTopPanel() {
		return topPanel;
	}
	
	@Override
	public void setTitle(String title){
		super.setTitle(title);
		if(titleLabel != null)	titleLabel.setText(title);
	}
	
	@Deprecated
	@Override
	public void setUndecorated(boolean flag){
		setUndecorated(true);	//设置不需要装饰、即去掉窗体自身边框
	}
	
	@Deprecated
	@Override
	public void setContentPane(Container container){
		if(container instanceof JPanel)
			setContentPane((JPanel)container);
		else
			throw new IllegalArgumentException("need JPanel");
	}
	
	/**
	 * 设置内容面板
	 * @param contentPane
	 */
	public void setContentPane(JPanel pane){
		contentPanel.removeAll();
		contentPanel.add(pane);
	}
	
	@Override
	public JPanel getContentPane(){
		return contentPanel;
	}
	
	@Override
	public void setResizable(boolean resizable){
		isReSize = resizable;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		if(!isPress)
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isPress = true;
		pressX = e.getLocationOnScreen().x;
		pressY = e.getLocationOnScreen().y;
		pressW = getWidth();
		pressH = getHeight();
		pressLX = getLocation().x;
		pressLY = getLocation().y;
		minW = getMinimumSize().width;
		minH = getMinimumSize().height;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isPress = false;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x=e.getXOnScreen(),y=e.getYOnScreen(),tw=0,th=0;
		switch (getCursor().getType()) {
			case Cursor.NW_RESIZE_CURSOR:	//左上角
				if(x-pressX>0&&pressW==minW&&y-pressY>0&&pressH==minH)	return;
				int tx=0,ty=0;
				if(x-pressX>0&&getWidth()==minW)	tx=getLocation().x;
				else	tx=pressX+(x-pressX);
				if(y-pressY>0&&getHeight()==minH)	ty=getLocation().y;
				else	ty=pressY+(y-pressY);
				tw = (tw = pressW-(x-pressX)) <= minW ? minW : tw;
				th = (th = pressH-(y-pressY)) <= minH ? minH : th;
				setSize(tw, th);
				setLocation(tx, ty);
				break;
			case Cursor.NE_RESIZE_CURSOR:	//右上角
				if(x-pressX<0&&pressW==minW&&y-pressY>0&&pressH==minH)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				th = (th = pressH-(y-pressY)) <= minH ? minH : th;
				setSize(tw, th);
				setLocation(pressLX, pressY+(y-pressY)>=pressLY?pressLY:pressY+(y-pressY));
				break;
			case Cursor.SE_RESIZE_CURSOR:	//右下角
				if(x-pressX<0&&pressW==minW&&y-pressY<0&&pressH==minH)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				setSize(tw, th);
				break;
			case Cursor.SW_RESIZE_CURSOR:	//左下角
				if(y-pressY<0&&pressH==minH&&x-pressX>0&&pressW==minW)	return;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				tw = (tw = pressW-(x-pressX)) <= minW ? minW : tw;
				setSize(tw, th);
				setLocation((pressX+(x-pressX)>=pressLX) ? pressLX : pressX+(x-pressX),pressLY);
				break;
			case Cursor.W_RESIZE_CURSOR:	//左边
				if(x-pressX>0&&pressW==minW)	return;
				tw = (tw = pressW-(x-pressX)) <= minW ? minW : tw;
				setSize(tw, pressH);
				setLocation(pressX+(x-pressX),pressLY);
				break;
			case Cursor.N_RESIZE_CURSOR:	//上边
				if(y-pressY>0&&pressH==minH)	return;
				th = (th = pressH-(y-pressY)) <= minH ? minH : th;
				setSize(pressW, th);
				setLocation(pressLX, pressY+(y-pressY));
				break;
			case Cursor.E_RESIZE_CURSOR:	//右边
				if(x-pressX<0&&pressW==minW)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				setSize(tw, pressH);
				break;
			case Cursor.S_RESIZE_CURSOR:	//下边
				if(y-pressY<0&&pressH==minH)	return;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				setSize(pressW, th);
				break;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!isReSize || isPress)	return;
		int w = getWidth();
		int h = getHeight();
		int x = e.getX();
		int y = e.getY();
		if(x<10 && y<10){
			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));	//左上角
		}else if(y<10 && w-x<10){
			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));	//右上角
		}else if(w-x<10 && h-y<10){
			setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));	//右下角
		}else if(x<10 && h-y<10){
			setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));	//左下角
		}else if(x<5){
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));	//左
		}else if(y<5){
			setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));	//上
		}else if(w-x<5){
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));	//右
		}else if(h-y<5){
			setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));	//下
		}
	}
	
	/**
	 * 此方法是用来设置窗体的背景图片的
	 * @param bgImg 背景图片
	 */
	public void setBgImage(ImageIcon bgImage){
		if(bgImage == null)	return;
		//实例化背景图片类
		if(bgPanel != null)	getLayeredPane().remove(bgPanel);
		bgPanel = new BackgroundPanel(bgImage.getImage());
		((JPanel)super.getContentPane()).setOpaque(false);
		getLayeredPane().setLayout(null);
		getLayeredPane().add(bgPanel,new Integer(Integer.MIN_VALUE));
		repaint();
	}
	
	class BackgroundPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		private Image img;
		
		public BackgroundPanel(){}
		
		public BackgroundPanel(Image img){
			this.img = img;
		}
		
		public void setImg(Image img){
			this.img = img;
			this.repaint();
		}
		
		public Image getImg(){
			return this.img;
		}
		
		public void paint(Graphics g){
			super.paint(g);
			g.drawImage(this.img,0,0,this.getWidth(),this.getHeight(),this);
		}
		
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(bgPanel!=null)	bgPanel.setBounds(0, 0, super.getContentPane().getWidth(), super.getContentPane().getHeight());
	}

	
}
