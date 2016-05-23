package org.kerwin.shutdownui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.kerwin.shutdownui.bean.PictureInfo;
import org.kerwin.shutdownui.service.SelfMouseAdapter1;
import org.kerwin.shutdownui.service.Theme;
import org.kerwin.shutdownui.util.ImageUtil;
import org.kerwin.shutdownui.util.UIUtil;

import com.sun.awt.AWTUtilities;

public class PictureDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel; // 内容面板
	private JLabel pictureLabel; // 图片标签
	private JLabel closeLabel; // 关闭按钮
	private JLabel prevLabel; // 上一张按钮
	private JLabel nextLabel; // 下一张按钮
	private int arc; // 圆角幅度
	private Color borderColor; // 边框颜色
	private boolean isFullScreen; // 是否全屏
	private static PictureDialog self; // 自身实例
	private PictureInfo[] pis;
	private int index;
	private ImageIcon cii;

	private PictureDialog() {
		arc = 13; // 圆角弧度
		borderColor = Theme.getTransparentColor(120); // 边框颜色
		setUndecorated(true); // 设置窗体无边款
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE); // 设置窗体关闭动作
		setSize((UIUtil.screenWidth / 3) * 2, (UIUtil.screenHeight / 3) * 2); // 设置窗体大小
		setResizable(false); // 设置窗体大小不可变
		UIUtil.locationToScreenCenter(this); // 设置窗体定位
		// AWTUtilities.setWindowOpacity(this, 0.5f);
		/** 设置圆角 */
		AWTUtilities.setWindowShape(this, new RoundRectangle2D.Double(0.0D,
				0.0D, getWidth(), getHeight(), arc, arc));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Window win = (Window) e.getSource();
				/** 设置圆角 */
				AWTUtilities.setWindowShape(win, new RoundRectangle2D.Double(
						0.0D, 0.0D, win.getWidth(), win.getHeight(), arc, arc));
			}
		});

		// 主内容面板
		contentPanel = new JPanel(null);
		contentPanel.setBackground(null);
		contentPanel.addMouseListener(new SelfMouseAdapter1(this)); // 添加鼠标事件
		contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2
						&& e.getButton() != MouseEvent.BUTTON3) {
					setDialogState(isFullScreen);
				}
			}
		});
		contentPanel.setBorder(BorderFactory.createCompoundBorder(new Border() {// 设置主面板的边框
					@Override
					public void paintBorder(Component c, Graphics g, int x,
							int y, int width, int height) {
						Graphics2D g2d = (Graphics2D) g.create();
						Shape shape = g2d.getClip();
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g2d.setClip(shape);
						g2d.setColor(borderColor);
						g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3,
								arc - 2, arc - 2);
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
				}, BorderFactory.createLineBorder(Theme.getTransparentColor(),
						1))); // 设置圆角边框
		// 设置关闭标签永远在最顶端、越小越靠上。最小为0、最大为容器所有子组件个数
		contentPanel.addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				contentPanel.setComponentZOrder(closeLabel, 0);
				if (pictureLabel != null
						&& pictureLabel.equals(contentPanel
								.getComponentAt(pictureLabel.getLocation())))
					contentPanel.setComponentZOrder(pictureLabel,
							contentPanel.getComponentCount() - 1);
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				contentPanel.setComponentZOrder(closeLabel, 0);
				if (pictureLabel != null
						&& pictureLabel.equals(contentPanel
								.getComponentAt(pictureLabel.getLocation())))
					contentPanel.setComponentZOrder(pictureLabel,
							contentPanel.getComponentCount() - 1);
			}
		});

		closeLabel = new JLabel();
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				closeLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdcl1
								: Theme.SysRec.pdcs1));
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				closeLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdcl2
								: Theme.SysRec.pdcs2));
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		contentPanel.add(closeLabel);

		prevLabel = new JLabel();
		prevLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				prevLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdll1
								: Theme.SysRec.pdls1));
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				prevLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdll2
								: Theme.SysRec.pdls2));
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		contentPanel.add(prevLabel);

		nextLabel = new JLabel();
		nextLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				nextLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdrl1
								: Theme.SysRec.pdrs1));
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				nextLabel.setIcon(Theme
						.getThemeRec(isFullScreen ? Theme.SysRec.pdrl2
								: Theme.SysRec.pdrs2));
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		contentPanel.add(nextLabel);

		// 设置内容面板
		setContentPane(contentPanel);

		getRootPane().setBackground(null);

		// UIUtil.setAllOpaque(, false);
		// addWindowListener(new WindowAdapter() {
		// @Override
		// public void windowDeactivated(WindowEvent arg0) {
		// setVisible(false);
		// }
		// });
	}

	private void setDialogState(boolean isFull) {
		closeLabel.setVisible(false);
		prevLabel.setVisible(false);
		nextLabel.setVisible(false);
		setVisible(false);
		if (pictureLabel != null)
			pictureLabel.setVisible(false);
		if (isFull) {
			isFullScreen = false;
			setSize((UIUtil.screenWidth / 3) * 2, (UIUtil.screenHeight / 3) * 2); // 设置窗体大小
			UIUtil.locationToScreenCenter(this); // 设置窗体定位
			closeLabel.setBounds(getWidth() - 28, 3, 25, 25);
			closeLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdcs1));
			prevLabel.setBounds(3, getHeight() / 2 - 30, 60, 60);
			prevLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdls1));
			nextLabel.setBounds(getWidth() - 63, getHeight() / 2 - 30, 60, 60);
			nextLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdrs1));
		} else {
			isFullScreen = true;
			setSize(UIUtil.screenWidth, UIUtil.screenHeight); // 设置窗体大小
			UIUtil.locationToScreenCenter(this); // 设置窗体定位
			closeLabel.setBounds(getWidth() - 43, 3, 40, 40);
			closeLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdcl1));
			prevLabel.setBounds(3, getHeight() / 2 - 50, 100, 100);
			prevLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdll1));
			nextLabel.setBounds(getWidth() - 103, getHeight() / 2 - 50, 100,
					100);
			nextLabel.setIcon(Theme.getThemeRec(Theme.SysRec.pdrl1));
		}
		if (pictureLabel != null)
			setPictureLabel();
		closeLabel.setVisible(true);
		// prevLabel.setVisible(true);
		// nextLabel.setVisible(true);
		if (pictureLabel != null)
			pictureLabel.setVisible(true);
		repaint();
		setVisible(true);
	}

	public static PictureDialog showPictureDialog(PictureInfo[] pis, int index) {
		return self == null ? (self = new PictureDialog()).setPictureInfo(pis,
				index) : self.setPictureInfo(pis, index);
	}

	private PictureDialog setPictureInfo(PictureInfo[] pis, int index) {
		if (pis == null || pis.length < 1)
			return null;
		this.pis = pis;
		this.index = index;
		setVisible(false);
		setDialogState(true);
		setPictureLabel();
		setVisible(true);
		setFocusable(true);
		return this;
	}

	private void setPictureLabel() {
		if (pictureLabel != null) {
			contentPanel.remove(pictureLabel);
			repaint();
		}
		cii = Theme.getThemeRec(Theme.SysRec.loadingImage);
		pictureLabel = new JLabel(cii);
		pictureLabel.setOpaque(false);
		pictureLabel.setBounds((getWidth() - cii.getIconWidth()) / 2,
				(getHeight() - cii.getIconHeight()) / 2, cii.getIconWidth(),
				cii.getIconHeight());
		// pictureLabel.setMaximumSize(new
		// Dimension(getWidth()-20,getHeight()-20));
		contentPanel.add(pictureLabel);
		validate();
		new Thread(new Runnable() {
			public void run() {
				ImageIcon ii = null;
				PictureInfo pi = null;
				if (self.index >= self.pis.length) {
					pi = self.pis[self.pis.length - 1];
				} else if (self.index < 0) {
					pi = self.pis[0];
				} else {
					pi = self.pis[self.index];
				}
				String tmpstr = null;
				if (isFullScreen) {
					if ((tmpstr = pi.getOriginalPic()) == null) {
						if ((tmpstr = pi.getBmiddlePic()) == null) {
							cii = Theme.getThumbnailPic((tmpstr = pi
									.getThumbnailPic()));
						} else {
							cii = Theme.getBmiddlePic(tmpstr);
						}
					} else {
						cii = Theme.getOriginalPic(tmpstr);
					}
				} else {
					if ((tmpstr = pi.getBmiddlePic()) == null) {
						cii = Theme.getThumbnailPic((tmpstr = pi
								.getThumbnailPic()));
					} else {
						cii = Theme.getBmiddlePic(tmpstr);
					}
				}
				if (cii.getIconHeight() > getHeight()) {
					BufferedImage bi = ImageUtil.resize(
							ImageUtil.toBufferedImage(cii.getImage()),
							(cii.getIconWidth() / cii.getIconHeight())
									* (getHeight() - 10), getHeight() - 10);
					ii = new ImageIcon(bi);
				} else if (cii.getIconWidth() > getWidth()) {
					BufferedImage bi = ImageUtil.resize(
							ImageUtil.toBufferedImage(cii.getImage()),
							getWidth() - 10,
							(cii.getIconHeight() / cii.getIconWidth())
									* (getWidth() - 10));
					ii = new ImageIcon(bi);
				} else {
					ii = cii;
				}
				pictureLabel.setIcon(ii);
				pictureLabel.setBounds((getWidth() - ii.getIconWidth()) / 2,
						(getHeight() - ii.getIconHeight()) / 2,
						ii.getIconWidth(), ii.getIconHeight());
				pictureLabel.addMouseListener(new SelfMouseAdapter1(
						pictureLabel));
				pictureLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2
								&& e.getButton() != MouseEvent.BUTTON3) {
							setDialogState(isFullScreen);
						}
					}
				});
				if (tmpstr != null
				// && !"".equals(tmpstr)
				// && !"gif".equalsIgnoreCase(tmpstr.substring(tmpstr
				// .lastIndexOf(".") + 1))
				) {
					// 添加鼠标滚动事件
					pictureLabel
							.addMouseWheelListener(new MouseWheelListener() {
								@Override
								public void mouseWheelMoved(MouseWheelEvent arg0) {
									int units = arg0.getUnitsToScroll();
									units = units < 0 ? 10 : -10; // 滚动一次算10个像素
									ImageIcon ii = (ImageIcon) pictureLabel
											.getIcon(); // 获得当前显示的图片
									Point p = pictureLabel.getLocation(); // 得到当前显示的图片的定位
									int nh = ii.getIconHeight() + units; // 即将改变的新高度（根据现有图片尺寸计算）
									int nw = (ii.getIconWidth() / ii
											.getIconHeight()) * nh; // 即将改变的新宽度（根据现有图片尺寸计算）
									if (nh > getHeight()) {
										nw = (cii.getIconWidth() / cii
												.getIconHeight()) * nh; // 如果新高度大于窗体高度、则新宽度根据原始图片计算
									} else if (nw > getWidth()) {
										nh = (cii.getIconHeight() / cii
												.getIconWidth()) * nw; // 如果新宽度大于窗体宽度、则新高度根据原始图片计算
									}
									BufferedImage bi = ImageUtil.resize(
											ImageUtil.toBufferedImage(cii
													.getImage()), nw, nh);
									ImageIcon nii = new ImageIcon(bi);
									pictureLabel.setIcon(nii);
									Point np = new Point();
									np.setLocation(
											(p.getX() - ((double) (nii
													.getIconWidth() - ii
													.getIconWidth()) / 2)), (p
													.getY() - ((double) (nii
													.getIconHeight() - ii
													.getIconHeight()) / 2)));
									pictureLabel.setBounds(new Rectangle(np,
											new Dimension(nii.getIconWidth(),
													nii.getIconHeight())));
									repaint();
								}
							});
				}
				repaint();
			}
		}).start();
	}

}
