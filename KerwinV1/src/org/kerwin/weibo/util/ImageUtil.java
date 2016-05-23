package org.kerwin.weibo.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;

public class ImageUtil {
	
	/**
	 * 将Image对象转为BufferedImage
	 * @param image 要转化的对象
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent Pixels
//		boolean hasAlpha = true;

		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
//		    if (hasAlpha) {
//		    	transparency = Transparency.BITMASK;
//		    }

		    // Create the buffered image
		    GraphicsDevice gs = ge.getDefaultScreenDevice();
		    GraphicsConfiguration gc = gs.getDefaultConfiguration();
		    bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
		    // The system does not have a screen
		}

		if (bimage == null) {
		    // Create a buffered image using the default color model
		    int type = BufferedImage.TYPE_INT_RGB;
		    //int type = BufferedImage.TYPE_3BYTE_BGR;
//		    if (hasAlpha) {
//		    	type = BufferedImage.TYPE_INT_ARGB;
//		    }
		    bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}  

	
	/**
	 * 将指定的图片的大小设为指定的大小
	 * @param source	目标图片
	 * @param targetW	指定图片宽
	 * @param targetH	指定图片高
	 * @return	返回修改后的图片
	 */
	public static BufferedImage resize(BufferedImage source, int targetW, int targetH){
		if(source == null)	return null;	
		//targetW,targetH 目标宽
		int type = source.getType();
		BufferedImage target = null;	//定义目标图片
		double sx = (double) targetW / source.getWidth();	//宽的缩放比例
		double sy = (double) targetH / source.getHeight();	//高的缩放比例
		
		//实现等比缩放
		if(sx < sy){
			sx = sy;
			targetW = (int) (sx*source.getWidth());
		}else{
			sy = sx;
			targetH = (int) (sy*source.getHeight());
		}
		
		//根据图片类型做修改
		if(type == BufferedImage.TYPE_CUSTOM){
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
			target = new BufferedImage(cm,raster,cm.isAlphaPremultiplied(),null);
		}else{
			target = new BufferedImage(targetW,targetH,type);
			Graphics2D g = target.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
			g.dispose();
		}
		return target;
	}
	
//	public static void saveImageAsJPG(String inFilePath, String outFilePath, int width, int height, boolean proportion) throws IOException{
//		File file = new File(inFilePath);
//		InputStream in = new FileInputStream(file);
//		File saveFile = new File(outFilePath);
//		BufferedImage srcImage = ImageIO.read(in);
//		if(width > 0 || height > 0){
//			int sw = srcImage.getWidth();
//			int sh = srcImage.getHeight();
//			// 如果原图像的大小小于要缩放的图像大小，直接将要缩放的图像复制过去
//			if(sw > width && sh > height){
//				srcImage = resize(srcImage, width, height);
//			}else{
//				String fileName = saveFile.getName();
//				String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
//				ImageIO.write(srcImage, formatName, saveFile);
//				return;
//			}
//		}
//		int w = srcImage.getWidth();
//		int h = srcImage.getHeight();
//		if(w == width){
//			int x = 0;
//			int y = h / 2 - height / 2;
//			saveSubImage(srcImage, new Rectangle(x, y, width, height), saveFile);
//		}else if(h == height){
//			int x = w / 2 - width / 2;
//			int y = 0;
//			saveSubImage(srcImage, new Rectangle(x , y, width, height), saveFile);
//		}
//		in.close();
//	}
//	
//	private static void saveSubImage(BufferedImage image, Rectangle subImageBounds, File subImageFile) throws IOException{
//		if(subImageBounds.x < 0 || subImageBounds.y < 0 || subImageBounds.width - subImageBounds.x > image.getWidth() || subImageBounds.height - subImageBounds.y > image.getHeight()){
//			return;
//		}
//		BufferedImage subImage = image.getSubimage(subImageBounds.x, subImageBounds.y, subImageBounds.width, subImageBounds.height);
//		String fileName = subImageFile.getName();
//		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
//		ImageIO.write(subImage,formatName,subImageFile);
//	}
//	
//	public static void main(String[] args) throws IOException{
//		saveImageAsJPG("C:\\Users\\kerwin\\Desktop\\1.jpg", "C:\\Users\\kerwin\\Desktop\\2.jpg", 100, 100, true);
//	}
}
