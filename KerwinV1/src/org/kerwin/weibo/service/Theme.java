package org.kerwin.weibo.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.kerwin.weibo.util.FrameUtil;
import org.kerwin.weibo.util.ImageUtil;

public class Theme {
	
	private static final String DEFAULTTHEMEPKG = "res/theme/default/";
	private static ImageIcon bgImage;
	private static ImageIcon trayImage;
	private static ImageIcon loadingImage;
	private static ImageIcon versionImage;
	private static ImageIcon loginAddImage;
	private static ImageIcon loginCancelImage;
	private static ImageIcon loginTicklingImage;
	private static ImageIcon loginEnterImage;
	private static ImageIcon loginDelImage;
	private static ImageIcon loginNewImage;
	private static ImageIcon oauthLoadingImage;
	private static ImageIcon tabPaneHoverImage;
	private static ImageIcon tabPaneClickImage;
	private static ImageIcon statusImage;
	private static ImageIcon onstatusImage;
	private static ImageIcon refreshImage;
	private static ImageIcon onrefreshImage;
	private static ImageIcon searchImage;
	private static ImageIcon onsearchImage;
	private static ImageIcon avatarSImage;
	private static ImageIcon avatarMImage;
	private static ImageIcon avatarLImage;
	private static ImageIcon moreImage;
	private static ImageIcon onMoreImage;
	private static ImageIcon minImage;
	private static ImageIcon onMinImage;
	private static ImageIcon maxImage;
	private static ImageIcon onMaxImage;
	private static ImageIcon closeImage;
	private static ImageIcon onCloseImage;
	
	/**
	 * 主题资源获取失败退出程序
	 */
	private static void exit(){
		JOptionPane.showConfirmDialog(FrameUtil.getCurrentFrame(), "主题资源获取失败", "提示", JOptionPane.WARNING_MESSAGE);
		System.exit(0);
	}
	
	/**
	 * 判断资源文件是否存在
	 * @param name 资源文件名称
	 */
	private static void exists(String name){
		File f = new File(name);
		if(!f.isFile() || !f.exists()){
			exit();
		}
	}
	
	/**
	 * 根据图片文件名得到图片
	 * @param name 图片名称
	 * @return 返回ImageIcon类型的图片资源
	 */
	private static ImageIcon getImage(String name){
		exists(name);	//判断文件是否存在
		return new ImageIcon(name);
	}
	
	/**
	 * 获得图片。且文件必须大于指定的宽、高。否则退出程序
	 * @param name 图片文件名称
	 * @param width 图片宽
	 * @param height 图片高
	 * @return 返回ImageIcon类型的图片资源
	 */
	private static ImageIcon getImage(String name,float width,float height){
		exists(name);
		ImageIcon ii = new ImageIcon(name);
		if(ii.toString().isEmpty() || ii.getIconWidth() != width || ii.getIconHeight() != height){
			exit();
		}
		return ii;
	}
	
	/**
	 * 获得系统托盘图片
	 * @return 返回ImageIcon类型系统托盘图片
	 */
	public static ImageIcon getTrayImage(){
		return trayImage != null ? trayImage : (trayImage = getImage("res/zl32.png",32,32));
	}
	
	/**
	 * 获得版本背景图片。即登录窗口的背景图
	 * @return 返回ImageIcon类型背景图片
	 */
	public static ImageIcon getVersionImage(){
		return versionImage != null ? versionImage : (versionImage = getImage("res/version.png"));
	}
	
	/**
	 * 获得背景图片
	 * @return 返回ImageIcon类型背景图片
	 */
	public static ImageIcon getBgImage(){
		return bgImage != null ? bgImage : (bgImage = getImage(DEFAULTTHEMEPKG+"bg.png"));
	}
	
	/**
	 * 获得加载图片
	 * @return 返回ImageIcon类型加载图片
	 */
	public static ImageIcon getLoadingImage(){
		return loadingImage != null ? loadingImage : (loadingImage = getImage(DEFAULTTHEMEPKG+"loading.gif"));
	}

	public static ImageIcon getLoginAddImage(){
		return loginAddImage != null ? loginAddImage : (loginAddImage = getImage(DEFAULTTHEMEPKG+"loginAdd.png"));
	}
	
	public static ImageIcon getLoginCancelImage(){
		return loginCancelImage != null ? loginCancelImage : (loginCancelImage = getImage(DEFAULTTHEMEPKG+"loginCancel.png"));
	}
	
	public static ImageIcon getLoginTicklingImage(){
		return loginTicklingImage != null ? loginTicklingImage : (loginTicklingImage = getImage(DEFAULTTHEMEPKG+"loginTickling.png"));
	}
	
	public static ImageIcon getLoginEnterImage(){
		return loginEnterImage != null ? loginEnterImage : (loginEnterImage = getImage(DEFAULTTHEMEPKG+"loginEnter.png"));
	}
	
	public static ImageIcon getLoginDelImage(){
		return loginDelImage != null ? loginDelImage : (loginDelImage = getImage(DEFAULTTHEMEPKG+"loginDel.png"));
	}
	
	public static ImageIcon getLoginNewImage(){
		return loginNewImage != null ? loginNewImage : (loginNewImage = getImage(DEFAULTTHEMEPKG+"loginNew.png"));
	}
	
	/**
	 * 获得透明色
	 * @return	返回透明色
	 */
	public static Color getTransparentColor(){
		return getTransparentColor(0);
	}
	
	/**
	 * 获得透明色
	 * @param transparency 透明度
	 * @return 返回透明色
	 */
	public static Color getTransparentColor(int transparency){
		return getTransparentColor(null,transparency);
	}
	
	/**
	 * 获得指定颜色的透明色
	 * @param color 需要透明的颜色
	 * @param transparency 透明度
	 * @return
	 */
	public static Color getTransparentColor(Color color, int transparency){
		if(color == null)	color = new Color(0,0,0);
		return new Color(color.getRed(),color.getGreen(),color.getBlue(),transparency);
	}
	
	public static ImageIcon getOauthLoadingImage(){
		return oauthLoadingImage != null ? oauthLoadingImage : (oauthLoadingImage = getImage(DEFAULTTHEMEPKG+"oauthLoading.gif"));
	}
	
	public static ImageIcon getTabPaneHover(){
		return tabPaneHoverImage != null ? tabPaneHoverImage : (tabPaneHoverImage = getImage(DEFAULTTHEMEPKG+"tabPaneHover.png",35,25));
	}
	
	public static ImageIcon getTabPaneClick(){
		return tabPaneClickImage != null ? tabPaneClickImage : (tabPaneClickImage = getImage(DEFAULTTHEMEPKG+"tabPaneClick.png",35,25));
	}
	
	public static ImageIcon getStatusImage(){
		return statusImage != null ? statusImage : (statusImage = getImage(DEFAULTTHEMEPKG+"status.png",20,20));
	}
	
	public static ImageIcon getOnStatusImage(){
		return onstatusImage != null ? onstatusImage : (onstatusImage = getImage(DEFAULTTHEMEPKG+"onstatus.png",20,20));
	}
	
	public static ImageIcon getRefreshImage(){
		return refreshImage != null ? refreshImage : (refreshImage = getImage(DEFAULTTHEMEPKG+"refresh.png",20,20));
	}
	
	public static ImageIcon getOnRefreshImage(){
		return onrefreshImage != null ? onrefreshImage : (onrefreshImage = getImage(DEFAULTTHEMEPKG+"onrefresh.png",20,20));
	}
	
	public static ImageIcon getSearchImage(){
		return searchImage != null ? searchImage : (searchImage = getImage(DEFAULTTHEMEPKG+"search.png",20,20));
	}
	
	public static ImageIcon getOnSearchImage(){
		return onsearchImage != null ? onsearchImage : (onsearchImage = getImage(DEFAULTTHEMEPKG+"onsearch.png",20,20));
	}
	
	public static ImageIcon getMoreImage(){
		return moreImage != null ? moreImage : (moreImage = getImage(DEFAULTTHEMEPKG+"more.png"));
	}
	
	public static ImageIcon getOnMoreImage(){
		return onMoreImage != null ? onMoreImage : (onMoreImage = getImage(DEFAULTTHEMEPKG+"onmore.png"));
	}
	
	public static ImageIcon getAvatarSImage(){
		return avatarSImage != null ? avatarSImage : (avatarSImage = getImage(DEFAULTTHEMEPKG+"as.png"));
	}
	
	public static ImageIcon getAvatarMImage(){
		return avatarMImage != null ? avatarMImage : (avatarMImage = getImage(DEFAULTTHEMEPKG+"am.png"));
	}
	
	public static ImageIcon getAvatarLImage(){
		return avatarLImage != null ? avatarLImage : (avatarLImage = getImage(DEFAULTTHEMEPKG+"al.png"));
	}
	
	public static ImageIcon getMinImage(){
		return minImage != null ? minImage : (minImage = getImage(DEFAULTTHEMEPKG+"min.png"));
	}
	
	public static ImageIcon getOnMinImage(){
		return onMinImage != null ? onMinImage : (onMinImage = getImage(DEFAULTTHEMEPKG+"onmin.png"));
	}
	
	public static ImageIcon getMaxImage(){
		return maxImage != null ? maxImage : (maxImage = getImage(DEFAULTTHEMEPKG+"max.png"));
	}
	
	public static ImageIcon getOnMaxImage(){
		return onMaxImage != null ? onMaxImage : (onMaxImage = getImage(DEFAULTTHEMEPKG+"onmax.png"));
	}
	
	public static ImageIcon getCloseImage(){
		return closeImage != null ? closeImage : (closeImage = getImage(DEFAULTTHEMEPKG+"close.png"));
	}
	
	public static ImageIcon getOnCloseImage(){
		return onCloseImage != null ? onCloseImage : (onCloseImage = getImage(DEFAULTTHEMEPKG+"onclose.png"));
	}
	
	public static ImageIcon getAvatarSImage(String url){
		ImageIcon imageIcon = getImageByString(url);
		return imageIcon == null ? getAvatarSImage() : imageIcon;
	}
	
	public static ImageIcon getAvatarSImage(URL url){
		ImageIcon imageIcon = new ImageIcon(url);
		return imageIcon == null ? getAvatarSImage() : imageIcon;
	}
	
	public static ImageIcon getAvatarMImage(String url){
		ImageIcon imageIcon = null;
		try {
			imageIcon = getAvatarMImage(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getAvatarMImage() : imageIcon;
	}
	
	public static ImageIcon getAvatarMImage(URL url){
		ImageIcon imageIcon = null;
		try {
			imageIcon = new ImageIcon(ImageUtil.resize(ImageIO.read(url),80,80));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getAvatarMImage() : imageIcon;
	}
	
	public static ImageIcon getAvatarLImage(String url){
		ImageIcon imageIcon = getImageByString(url);
		return imageIcon == null ? getAvatarLImage() : imageIcon;
	}
	
	public static ImageIcon getAvatarLImage(URL url){
		ImageIcon imageIcon = new ImageIcon(url);
		return imageIcon == null ? getAvatarLImage() : imageIcon;
	}
	
	private static ImageIcon getImageByString(String url){
		ImageIcon imageIcon = null;
		try {
			return imageIcon = new ImageIcon(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return imageIcon;
	}
	
	/**
	 * 解决问题方法：
		1. 如果是纯中英文，很简单，不解释。
		2. 如果是中英文混合写，需要考虑到转换成byte数组来处理时，可能会碰到乱码的问题。比如你刚好截取到中文的一半。。。
		给出一个方法：【利用java中文是用Unicode编码即UCS2编码来制作一个byte数组，利用内部机制来凑齐字符】 

	 * @param s
	 * @param length
	 * @return
	 */
	public static String bSubstring(String s, int length) {
		try {
			byte[] bytes = s.getBytes("Unicode");
			int n = 0; // 表示当前的字节数
	        int i = 2; // 前两个字节是标志位，bytes[0] = -2，bytes[1] = -1。所以从第3位开始截取。
	        for (; i < bytes.length && n < length; i++){
	            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
	            if (i % 2 == 1)
	                n++; // 在UCS2第二个字节时n加1
	            else
	                if (bytes[i] != 0)	// 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
	                    n++;
	        }
	        // 如果i为奇数时，处理成偶数
	        if (i % 2 == 1){
	            if (bytes[i - 1] != 0)	// 该UCS2字符是汉字时，去掉这个截一半的汉字
	                i = i - 1;
	            else	// 该UCS2字符是字母或数字，则保留该字符
	                i = i + 1;
	        }
	        return new String(bytes, 0, i, "Unicode");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
    }

}
