package org.kerwin.shutdownui.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.kerwin.shutdownui.util.ImageUtil;
import org.kerwin.shutdownui.view.ShutDownUI;

/**
 * 主题类
 * 
 * @author Kerwin Bryant
 * 
 */
public class Theme {

	private Theme() {
	};

	public enum SysRec {

		newImage("new.png"), trayImage("logo.png"), loadingImage("loading.gif"), avatarSImage(
				"as.png"), avatarMImage("am.png"), avatarLImage("al.png"), pdcl1(
				"pdcl1.png"), pdcl2("pdcl2.png"), pdrl1("pdrl1.png"), pdrl2(
				"pdrl2.png"), pdll1("pdll1.png"), pdll2("pdll2.png"), pdcs1(
				"pdcs1.png"), pdcs2("pdcs2.png"), pdrs1("pdrs1.png"), pdrs2(
				"pdrs2.png"), pdls1("pdls1.png"), pdls2("pdls2.png");

		private String recName;

		private SysRec(String recName) {
			this.recName = recName;
		}

		public String getRecName() {
			return recName;
		}

	}

	private static final String DEFAULTTHEMEPKG = "/res/theme/default/";
	private static final String AVATARSPATH = "/data/avatar/s/";
	private static final String AVATARMPATH = "/data/avatar/m/";
	private static final String AVATARLPATH = "/data/avatar/l/";
	private static final String IMAGESSPATH = "/data/images/s/";
	private static final String IMAGESMPATH = "/data/images/m/";
	private static final String IMAGESLPATH = "/data/images/l/";
	private static String themePkg = DEFAULTTHEMEPKG;

	/**
	 * 主题资源获取失败退出程序
	 */
	private static void exit(String name) {
		JOptionPane.showConfirmDialog(ShutDownUI.self,
				"主题资源获取失败[" + name + "]", "提示", JOptionPane.WARNING_MESSAGE);
		System.exit(0);
	}

	/**
	 * 判断资源文件是否存在
	 * 
	 * @param name
	 *            资源文件名称
	 */
	private static void exists(String pkgPath, String recName) {
		if (pkgPath == null || "".equals(pkgPath) || recName == null
				|| "".equals(recName))
			return;
		if (DEFAULTTHEMEPKG.equalsIgnoreCase(pkgPath))
			return;
		File f = new File(pkgPath + recName);
		if (!f.isFile() || !f.exists()) {
			exit(pkgPath + recName);
		}
	}

	/**
	 * 根据图片文件名得到图片
	 * 
	 * @param name
	 *            图片名称
	 * @return 返回ImageIcon类型的图片资源
	 */
	private static ImageIcon getImage(String pkgPath, String recName) {
		exists(pkgPath, recName); // 判断文件是否存在
		return new ImageIcon(Theme.class.getResource(pkgPath + recName));
	}

	/**
	 * 获得图片。且文件必须大于指定的宽、高。否则退出程序
	 * 
	 * @param name
	 *            图片文件名称
	 * @param width
	 *            图片宽
	 * @param height
	 *            图片高
	 * @return 返回ImageIcon类型的图片资源
	 */
	private static ImageIcon getImage(String pkgPath, String recName,
			float width, float height) {
		exists(pkgPath, recName);
		ImageIcon ii = new ImageIcon(Theme.class.getResource(pkgPath + recName));
		if (ii.toString().isEmpty() || ii.getIconWidth() != width
				|| ii.getIconHeight() != height) {
			exit(pkgPath + recName);
		}
		return ii;
	}

	/**
	 * 得到系统资源图片
	 * 
	 * @param recName
	 * @return
	 */
	public static ImageIcon getThemeRec(SysRec recName) {
		if (recName == null)
			return null;
		if (recName.equals(SysRec.trayImage))
			return recName == null ? null : getImage(themePkg,
					recName.getRecName(), 64, 64);
		else
			return recName == null ? null : getImage(themePkg,
					recName.getRecName());
	}

	/**
	 * 根据图片URL和指定路径、将图片下载到路径后返回图片对象、下次获取该图片时直接从所给路径中获取无须再次下载
	 * 
	 * @param url
	 *            图片URL
	 * @param path
	 *            图片所存路径
	 * @return
	 */
	private static ImageIcon getImageByPath(URL url, String path) {
		if (url == null)
			return null;
		ImageIcon imageIcon = null;
		String fn = url.getPath();
		int x = fn.lastIndexOf("/");
		int d = fn.lastIndexOf(".");
		if (x < d) {
			fn = path + fn.substring(x + 1);
		} else {
			fn = path
					+ fn.substring(fn.substring(0, x).lastIndexOf("/") + 1, x);
		}
		// try {
		// fn =
		// URLDecoder.decode(Theme.class.getProtectionDomain().getCodeSource().getLocation().getFile(),"UTF-8")
		// + fn;
		fn = System.getProperty("user.dir") + fn;
		// } catch (UnsupportedEncodingException e1) {
		// e1.printStackTrace();
		// }
		// fn = Theme.class.getResource("/").getPath() + fn;
		if (new File(fn).exists()) {
			imageIcon = new ImageIcon(fn);
		} else {
			int i = -1;
			try {
				new File(fn).getParentFile().mkdirs();
				InputStream is = url.openStream();
				OutputStream os = new FileOutputStream(fn);
				while ((i = is.read()) != -1) {
					os.write(i);
				}
				os.close();
				is.close();
				imageIcon = new ImageIcon(fn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imageIcon;
	}

	/**
	 * 获得透明色
	 * 
	 * @return 返回透明色
	 */
	public static Color getTransparentColor() {
		return getTransparentColor(0);
	}

	/**
	 * 获得透明色
	 * 
	 * @param transparency
	 *            透明度
	 * @return 返回透明色
	 */
	public static Color getTransparentColor(int transparency) {
		return getTransparentColor(null, transparency);
	}

	/**
	 * 获得指定颜色的透明色
	 * 
	 * @param color
	 *            需要透明的颜色
	 * @param transparency
	 *            透明度
	 * @return
	 */
	public static Color getTransparentColor(Color color, int transparency) {
		if (color == null)
			color = new Color(0, 0, 0);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(),
				transparency);
	}

	/**
	 * 得到微博用户头像(小图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getAvatarSImage(URL url) {
		if (url == null) {
			return null;
		}
		ImageIcon imageIcon = getImageByPath(url, AVATARSPATH);
		return imageIcon == null ? getThemeRec(SysRec.avatarSImage) : imageIcon;
	}

	/**
	 * 得到微博用户头像(中图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getAvatarMImage(URL url) {
		if (url == null) {
			return null;
		}
		ImageIcon imageIcon = getImageByPath(url, AVATARMPATH);
		try {
			imageIcon = new ImageIcon(ImageUtil.resize(
					ImageUtil.toBufferedImage(imageIcon.getImage()), 80, 80));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getThemeRec(SysRec.avatarMImage) : imageIcon;
	}

	/**
	 * 得到微博用户头像(大图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getAvatarLImage(URL url) {
		if (url == null) {
			return null;
		}
		ImageIcon imageIcon = getImageByPath(url, AVATARLPATH);
		return imageIcon == null ? getThemeRec(SysRec.avatarLImage) : imageIcon;
	}

	/**
	 * 得到微博图片(小图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getThumbnailPic(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ImageIcon imageIcon = null;
		try {
			imageIcon = getImageByPath(new URL(url), IMAGESSPATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getThemeRec(SysRec.loadingImage) : imageIcon;
	}

	/**
	 * 得到微博图片(中图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getBmiddlePic(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ImageIcon imageIcon = null;
		try {
			imageIcon = getImageByPath(new URL(url), IMAGESMPATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getThemeRec(SysRec.loadingImage) : imageIcon;
	}

	/**
	 * 得到微博图片(大图)
	 * 
	 * @param url
	 *            图片URL
	 * @return
	 */
	public static ImageIcon getOriginalPic(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}
		ImageIcon imageIcon = null;
		try {
			imageIcon = getImageByPath(new URL(url), IMAGESLPATH);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return imageIcon == null ? getThemeRec(SysRec.loadingImage) : imageIcon;
	}

	/**
	 * 解决问题方法： 1. 如果是纯中英文，很简单，不解释。 2.
	 * 如果是中英文混合写，需要考虑到转换成byte数组来处理时，可能会碰到乱码的问题。比如你刚好截取到中文的一半。。。
	 * 给出一个方法：【利用java中文是用Unicode编码即UCS2编码来制作一个byte数组，利用内部机制来凑齐字符】
	 * 
	 * @param s
	 * @param length
	 * @return
	 */
	public static String bSubstring(String s, int length) {
		try {
			byte[] bytes = s.getBytes("Unicode");
			int n = 0; // 表示当前的字节数
			int i = 2; // 前两个字节是标志位，bytes[0] = -2，bytes[1] = -1。所以从第3位开始截取。
			for (; i < bytes.length && n < length; i++) {
				// 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
				if (i % 2 == 1)
					n++; // 在UCS2第二个字节时n加1
				else if (bytes[i] != 0) // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
					n++;
			}
			// 如果i为奇数时，处理成偶数
			if (i % 2 == 1) {
				if (bytes[i - 1] != 0) // 该UCS2字符是汉字时，去掉这个截一半的汉字
					i = i - 1;
				else
					// 该UCS2字符是字母或数字，则保留该字符
					i = i + 1;
			}
			return new String(bytes, 0, i, "Unicode");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

}
