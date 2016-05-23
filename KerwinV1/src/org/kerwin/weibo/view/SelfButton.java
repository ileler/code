package org.kerwin.weibo.view;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class SelfButton extends JButton {

	private static final long serialVersionUID = 1L;

	public SelfButton(){
		super();
		self();
	}
	
	public SelfButton(String text){
		super(text);
		self();
	}
	
	public SelfButton(ImageIcon icon){
		super(icon);
		self();
	}
	
	public SelfButton(String text, ImageIcon icon){
		super(text, icon);
		self();
	}

	public SelfButton(ImageIcon icon, int hp, int vp){
		super(icon);
		self();
		setHorizontalTextPosition(hp); //水平方向文本定位至图片的中间
		setVerticalTextPosition(vp); //垂直方向文本定位至图片的下面
	}

	public SelfButton(String text, ImageIcon icon, int hp, int vp){
		super(text, icon);
		self();
		setHorizontalTextPosition(hp); //水平方向文本定位至图片的中间
		setVerticalTextPosition(vp); //垂直方向文本定位至图片的下面
	}
	
	private void self(){
		setBorder(null);	//设置边框
//		setOpaque(false);	//设置组件背景透明。貌似对Button无效
		setContentAreaFilled(false);	//Button用该方法设置透明
		setCursor(new Cursor(Cursor.HAND_CURSOR));	//设置Button的鼠标样式
		setFocusPainted(false);		//去掉Button文本周围边框
	}
}
