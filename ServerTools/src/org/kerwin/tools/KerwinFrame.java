package org.kerwin.tools;

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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.sun.awt.AWTUtilities;

public class KerwinFrame extends JFrame implements MouseListener,MouseMotionListener{

	private static final long serialVersionUID = 1L;
	
	private BackgroundPanel bgPanel;
	private JPanel mainPanel;
	private JPanel contentPanel;
	private JPanel topPanel;
	private JPanel operPanel;
	private JPanel topTmpPanel;
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
	private int borderArc;
	private int borderWidth;
	private Color borderColor;
	private Point mousePoint;
	private int pressX;	//鼠标按下时的X坐标位置
	private int pressY;	//鼠标按下时的Y坐标位置
	private int pressW;	//鼠标按下时窗体的宽
	private int pressH;	//鼠标按下时窗体的高
	private int pressLX;//鼠标按下时窗体X坐标位置
	private int pressLY;//鼠标按下时窗体Y坐标位置
	private int minW;
	private int minH;
	
	public static void main(String[] args){
		KerwinFrame kf = new KerwinFrame();
		JPanel jp = new JPanel();
		jp.setBackground(Color.BLACK);
		kf.setContentPane(jp);
		kf.setVisible(true);
	}
	
	public KerwinFrame(){
		super();
		initial();
	}
	
	public KerwinFrame(String title){
		this();
		titleLabel.setText(title);
	}
	
	private void initial(){
		setSize(300,200);	//窗体大小
		setTitle("Title");	//窗体标题
		setMinimumSize(getSize());	//设置窗体最小的大小
		super.setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//设置窗体的默认关闭效果
		isReSize = true;	//是否允许修改大小
		setView();
		
        //初始化主面板
		mainPanel = initMainPane();
        setMouseDraggedToMoveWindow(mainPanel);
		
		topTmpPanel = initTopPane();	//初始化顶部面板
		mainPanel.add(topTmpPanel,BorderLayout.NORTH);	//将顶部面板加入到主面板顶部
		
		contentPanel = new JPanel(new BorderLayout());	//初始化内容面板
		mainPanel.add(contentPanel);	//将内容面板加入到主面板的中间
		
		//窗体居中
		setLocation((int)((getToolkit().getScreenSize().getWidth() - getSize().getWidth()) / 2),(int)((getToolkit().getScreenSize().getHeight() - getSize().getHeight()) / 2));
		
		borderArc = 13;		//圆角弧度
		borderWidth = 5;	//边框宽度
		addComponentListener();
		setBorder(new Color(232,94,8,80));	//边框颜色
//		com.sun.awt.AWTUtilities.setWindowOpacity(this, 0f);
		try{
			com.sun.awt.AWTUtilities.setWindowOpaque(this, false);
		}catch(Exception e){
			e.printStackTrace();
		}
		com.sun.awt.AWTUtilities.setWindowShape(this, new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), borderArc+0.5, borderArc+0.5));
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
	
	public static void setFontToChildren(JComponent component,Font font){
		setFontToChildren(component, font, true);
	}
	
	public static void setFontToChildren(JComponent component,Font font,boolean cycle){
		if(component == null || font == null)	return;
		Component[] cs = component.getComponents();
		for(int i = 0, j = cs.length; i < j; i++){
			cs[i].setFont(font);
			if(cs[i] instanceof JComponent && cycle)	setFontToChildren((JComponent)cs[i], font, cycle);
		}
	}
	
	/**
	 * @return 初始化主面板
	 */
	private JPanel initMainPane(){
		//实例化主面板
		JPanel mainPanel = new JPanel(new BorderLayout());	
//		//设置主面板的边框
//		mainPanel.setBorder(BorderFactory.createCompoundBorder(new Border() {
//			@Override
//			public void paintBorder(Component c, Graphics g, int x, int y,
//					int width, int height) {
//				 Graphics2D g2d = (Graphics2D)g.create(); 
//				 Shape shape = g2d.getClip();  
//				 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
//				 g2d.setClip(shape);
//				 g2d.setColor(borderColor);
//				 g2d.drawRoundRect(x, y, width-1, height-1, arc, arc);
//				 g2d.dispose();  
//			}
//			@Override
//			public boolean isBorderOpaque() {
//				return false;
//			}
//			@Override
//			public Insets getBorderInsets(Component c) {
//				return getInsets();
//			}
//		},BorderFactory.createLineBorder(new Color(0,0,0,0), 2)));
		return mainPanel;
	}
	
	/**
	 * @return 初始化顶部面板
	 */
	private JPanel initTopPane(){
		isMin = true;
		isMax = true;
		isClose = true;
		minLabel = initMinLabel();
		maxLabel = initMaxLabel();
		closeLabel = initCloseLabel();
		operPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));	//实例化操作面板、用来放置最小化和关闭等按钮
		titleLabel = new JLabel();	//实例化标题标签
		titleLabel.setFont(new Font(Font.DIALOG,Font.PLAIN,12));	//设置标题字体
		topPanel = new JPanel(new BorderLayout());	//实例化顶部面板
		topPanel.add(titleLabel,BorderLayout.WEST);	//将标题标签加入到顶部面板的左方
		topPanel.add(operPanel);	//将操作面板加入到顶部面板
		refreshOperPanel();
		return topPanel;
	}
	
	/**
	 * @return 初始化最小化按钮
	 */
	private JLabel initMinLabel(){
		minLabel = new JLabel("Min");
		minLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		return minLabel;
	}
	
	/**
	 * @return 初始化关闭按钮
	 */
	private JLabel initCloseLabel(){
		closeLabel = new JLabel("Close");
		closeLabel.addMouseListener(new MouseAdapter() {
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
	 * @return 初始化最大化按钮
	 */
	private JLabel initMaxLabel(){
		maxLabel = new JLabel("Max");
		maxLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
//				setExtendedState(JFrame.MAXIMIZED_BOTH);
				 if(max){
					 max = false;
					 setBounds(tmpBound);
				 }else{
					 Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
					 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					 int width = screenSize.width - insets.left - insets.right;
					 int height = screenSize.height - insets.top - insets.bottom;
					 int x = insets.left;
					 int y = insets.top;
					 tmpBound = getBounds();
					 max = true;
					 setVisible(false);
					 setBounds(x, y, width, height);
				 }
				 mainPanel.setBounds(borderWidth, borderWidth, getWidth()-(borderWidth*2), getHeight()-(borderWidth*2));	//将主面板绑定到边框面板
				 setVisible(true);
			}
		});
		return maxLabel;
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
	
	/**
	 * @return 返回关闭按钮
	 */
	public JLabel getCloseLabel(){
		return closeLabel;
	}
	
	/**
	 * @return 返回最大化按钮
	 */
	public JLabel getMaxLabel(){
		return maxLabel;
	}
	
	/**
	 * @return 返回最小化按钮
	 */
	public JLabel getMinLabel(){
		return minLabel;
	}
	
	/**
	 * @return 返回操作面板
	 */
	public JPanel getOperPanel(){
		return operPanel;
	}
	
	/**
	 * @return 返回标题标签
	 */
	public JLabel getTitleLabel(){
		return titleLabel;
	}
	
	/**
	 * @return 返回主面板
	 */
	public JPanel getMainPanel(){
		return mainPanel;
	}
	
	/**
	 * @return 返回内容面板
	 */
	@Deprecated
	@Override
	public Container getContentPane(){
		return contentPanel;
	}
	
	/**
	 * @return 得到内容面板
	 */
	public JPanel getContentPanel(){
		return contentPanel;
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
	
	public void setBorder(Color borderColor){
		this.borderColor = borderColor;
		setBorder(borderColor,borderArc,borderWidth);
	}
	
	public void setBorderArc(int borderArc){
		this.borderArc = borderArc;
		setBorder(borderColor,borderArc,borderWidth);
	}
	
	public void setBorderWidth(int borderWidth){
		this.borderWidth = borderWidth;
		setBorder(borderColor,borderArc,borderWidth);
	}
	
	public void setBorder(int borderArc, int borderWidth){
		this.borderArc = borderArc;
		this.borderWidth = borderWidth;
		setBorder(borderColor,borderArc,borderWidth);
	}
	
	public void setBorder(Color borderColor,int borderWidth){
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		setBorder(borderColor,borderArc,borderWidth);
	}
	
	public void setBorder(final Color borderColor,final int borderArc,int borderWidth){
		if(borderColor == null)	return;
		this.borderArc = borderArc;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		//初始化边框面板
		JPanel borderPanel = new JPanel(){   
            private static final long serialVersionUID = 1L;  
            public void paintComponent(Graphics g){  
                Graphics2D g2d = (Graphics2D) g; 
                
                g2d.setColor(new Color(borderColor.getRed(),borderColor.getGreen(),borderColor.getBlue(),borderColor.getAlpha()));  
                g2d.fillRect(0, 0, getWidth(), getHeight());
                  
                g2d.setColor(new Color(borderColor.getRed(),borderColor.getGreen(),borderColor.getBlue(),borderColor.getAlpha()+40 >= 255 ? 255 : borderColor.getAlpha()+30));  
                g2d.draw(new RoundRectangle2D.Double(0, 0, KerwinFrame.this.getWidth()-1, KerwinFrame.this.getHeight()-1, borderArc+0.5, borderArc+0.5));          
            }  
        };
        borderPanel.setLayout(null);
        borderPanel.addMouseListener(this);	
        borderPanel.addMouseMotionListener(this);
        super.getContentPane().removeAll();
        super.setContentPane(borderPanel);	//设置窗体的内容面板为边框面板
        
        borderPanel.add(mainPanel);
		mainPanel.setBounds(borderWidth, borderWidth, getWidth()-(borderWidth*2), getHeight()-(borderWidth*2));	//将主面板绑定到边框面板
		repaint();
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
	
	/* (non-Javadoc)
	 * @see javax.swing.JFrame#setContentPane(java.awt.Container)
	 * 废除原有设置内容面板方法
	 */
	@Deprecated
	@Override
	public void setContentPane(Container container){
		if(container instanceof JPanel)
			this.setContentPane((JPanel)container);
		else
			throw new IllegalArgumentException("need JPanel");
	}
	
	/**
	 * 设置内容面板
	 * @param pane
	 */
	public void setContentPane(JPanel pane){
		mainPanel.remove(contentPanel);
		validate();
		contentPanel = pane;
		mainPanel.add(contentPanel);
		repaint();
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
				reSize(tw, th);
				setLocation(tx, ty);
				pressY = y;
				pressH = th;
				pressX = x;
				pressW = tw;
				break;
			case Cursor.NE_RESIZE_CURSOR:	//右上角
				if(x-pressX<0&&pressW==minW&&y-pressY>0&&pressH==minH)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				th = (th = pressH-(y-pressY)) <= minH ? minH : th;
				reSize(tw, th);
				setLocation(pressLX, pressY+(y-pressY)>=pressLY?pressLY:pressY+(y-pressY));
				pressY = y;
				pressH = th;
				pressX = x;
				pressW = tw;
				break;
			case Cursor.SE_RESIZE_CURSOR:	//右下角
				if(x-pressX<0&&pressW==minW&&y-pressY<0&&pressH==minH)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				reSize(tw, th);
				pressY = y;
				pressH = th;
				pressX = x;
				pressW = tw;
				break;
			case Cursor.SW_RESIZE_CURSOR:	//左下角
				if(y-pressY<0&&pressH==minH&&x-pressX>0&&pressW==minW)	return;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				tw = (tw = pressW-(x-pressX)) <= minW ? minW : tw;
				reSize(tw, th);
				setLocation((pressX+(x-pressX)>=pressLX) ? pressLX : pressX+(x-pressX),pressLY);
				pressY = y;
				pressH = th;
				pressX = x;
				pressW = tw;
				break;
			case Cursor.W_RESIZE_CURSOR:	//左边
				if(x-pressX>0&&pressW==minW || getWidth()==minW&&x-pressX>0)	return;
				tw = (tw = pressW-(x-pressX)) <= minW ? minW : tw;
				reSize(tw, pressH);
				setLocation(pressX+(x-pressX),pressLY);
				pressX = x;
				pressW = tw;
				break;
			case Cursor.N_RESIZE_CURSOR:	//上边
				if(y-pressY>0&&pressH==minH || getHeight()==minH&&y-pressY>0)	return;
				th = (th = pressH-(y-pressY)) <= minH ? minH : th;
				reSize(pressW, th);
				setLocation(pressLX, pressY+(y-pressY));
				pressY = y;
				pressH = th;
				break;
			case Cursor.E_RESIZE_CURSOR:	//右边
				if(x-pressX<0&&pressW==minW)	return;
				tw = (tw = pressW+(x-pressX)) <= minW ? minW : tw;
				reSize(tw, pressH);
				pressX = x;
				pressW = tw;
				break;
			case Cursor.S_RESIZE_CURSOR:	//下边
				if(y-pressY<0&&pressH==minH)	return;
				th = (th = pressH+(y-pressY)) <= minH ? minH : th;
				reSize(pressW, th);
				pressY = y;
				pressH = th;
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
		if(x<6 && y<6){
			setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));	//左上角
		}else if(y<6 && w-x<6){
			setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));	//右上角
		}else if(w-x<6 && h-y<6){
			setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));	//右下角
		}else if(x<6 && h-y<6){
			setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));	//左下角
		}else if(x<3){
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));	//左
		}else if(y<3){
			setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));	//上
		}else if(w-x<3){
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));	//右
		}else if(h-y<3){
			setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));	//下
		}
	}
	
	private void reSize(int width, int height){
		reSize(new Dimension(width, height));
	}
	
	private void reSize(Dimension dimension){
		if(mainPanel != null){
			mainPanel.setBounds(borderWidth, borderWidth, (int)dimension.getWidth()-(borderWidth*2), (int)dimension.getHeight()-(borderWidth*2));	//将主面板绑定到边框面板
		}
		super.setSize(dimension);
		setVisible(true);
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
				        win.getHeight(), borderArc, borderArc));  //arc貌似为单数比较圆滑一点
				} 
			}
		});
	}
	
	/**
	 * 设置鼠标在组件上拖动时移动窗体
	 * @param component
	 */
	public void setMouseDraggedToMoveWindow(Component component){
		if(component == null)	return;
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePoint = e.getLocationOnScreen();
			}
		});
		component.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
//				if(!getCursor().equals(new Cursor(Cursor.DEFAULT_CURSOR)))	return;	
				setLocation(getLocation().x + (e.getXOnScreen() - mousePoint.x),getLocation().y + (e.getYOnScreen() - mousePoint.y));
				mousePoint = e.getLocationOnScreen();
			}
		});
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
	
	
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(bgPanel!=null)	bgPanel.setBounds(0, 0, super.getContentPane().getWidth(), super.getContentPane().getHeight());
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
	
}