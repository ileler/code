package org.kerwin.shutdownui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.kerwin.shutdownui.bean.PictureInfo;
import org.kerwin.shutdownui.service.Theme;

import weibo4j.model.Status;
import weibo4j.model.User;

public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Status status;
	private JPanel firstLeftPane; // 第一层左边面板
	private JPanel firstCenterPane; // 第一层中间面板
	private JPanel secondPane; // 第二层面板
	private JPanel secondTopPane; // 第二层顶部面板
	private JPanel secondCenterPane; // 第二层中间面板
	private JPanel secondBottomPane; // 第二层底部面板
	private JPanel secondBottomOperPane; // 第二层底部操作面板
	private JLabel nameLabel; // 昵称标签
	private JLabel dateLabel; // 日期标签
	private JPanel contentPanel; // 内容父面板
	private JTextPane contentPane; // 内容面板
	private JLabel imgLabel; // 头像标签
	private int nameLength;

	/**
	 * 构造函数、
	 * 
	 * @param status
	 */
	public StatusPanel(Status status) throws Exception {
		this(status, false);
	}

	/**
	 * 构造函数
	 * 
	 * @param status
	 * @param isRt
	 *            是否是放在被被转发的
	 */
	public StatusPanel(Status status, boolean isRt) throws Exception {
		this.status = status;
		if (status == null)
			throw new NullPointerException("status is null");
		nameLength = isRt ? 12 : 15;
		setLayout(new BorderLayout());
		initial();
		initTopPanel();
		initSecondCenterPane();
		if (isRt) {
			setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createEmptyBorder(5, 20, 5, 0), BorderFactory
					.createLineBorder(Theme.getTransparentColor(40))));
		} else {
			imgLabel = new JLabel(Theme.getThemeRec(Theme.SysRec.avatarSImage),
					JLabel.LEFT);
			new Thread(new Runnable() {
				public void run() {
					ImageIcon ii = null;
					do {
						ii = Theme.getAvatarSImage(StatusPanel.this.status
								.getUser().getProfileImageURL());
					} while (ii != null
							&& (ii.getIconWidth() < imgLabel.getIcon()
									.getIconWidth() || ii.getIconHeight() < imgLabel
									.getIcon().getIconHeight()));
					imgLabel.setIcon(ii);
					validate();
				}
			}).start();
			firstLeftPane = new JPanel(new FlowLayout());
			firstLeftPane.add(imgLabel);
			add(firstLeftPane, BorderLayout.WEST);
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0,
							Theme.getTransparentColor(80)),
					BorderFactory.createEmptyBorder(0, 0, 5, 0)));
			Status rt = status.getRetweetedStatus();
			if (rt != null) {
				secondCenterPane.add(new StatusPanel(rt, true),
						BorderLayout.SOUTH);
			}
		}
		// FrameUtil.setAllOpaque(this,false);
	}

	/**
	 * 初始化
	 */
	private void initial() {
		secondPane = new JPanel(new BorderLayout());
		firstCenterPane = new JPanel(new BorderLayout());
		firstCenterPane.add(secondPane);
		add(firstCenterPane);

		secondTopPane = new JPanel(new BorderLayout());
		secondPane.add(secondTopPane, BorderLayout.NORTH);

		secondCenterPane = new JPanel(new BorderLayout());
		secondPane.add(secondCenterPane);

		secondBottomOperPane = new JPanel(
				new FlowLayout(FlowLayout.RIGHT, 5, 0));
		secondBottomPane = new JPanel(new BorderLayout());
		secondBottomPane.add(secondBottomOperPane);
		secondPane.add(secondBottomPane, BorderLayout.SOUTH);

	}

	private void initTopPanel() {
		secondTopPane.add(getDateLabel(), BorderLayout.EAST);
		secondTopPane.add(getNameLabel(), BorderLayout.WEST);
	}

	/**
	 * @return 返回主面板
	 */
	private void initSecondCenterPane() {
		contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPane = new JTextPane();
		contentPane.setOpaque(false);
		contentPane.setEditable(false);
		GroupLayout gl = new GroupLayout(contentPanel);
		gl.setHorizontalGroup(gl.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(contentPane,
				GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE));
		gl.setVerticalGroup(gl.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(contentPane));
		contentPanel.setLayout(gl);
		contentPane.setText(status.getText());
		String _s = null;
		String[] thumbnailPic = (_s = status.getThumbnailPic()) == null ? null
				: _s.split(",");
		String[] bmiddlePic = (_s = status.getBmiddlePic()) == null ? null : _s
				.split(",");
		String[] originalPic = (_s = status.getOriginalPic()) == null ? null
				: _s.split(",");
		final List<PictureInfo> pis = new ArrayList<PictureInfo>();
		for (int i = 0, j = thumbnailPic.length; i < j; i++) {
			String thumb = thumbnailPic[i];
			if (thumb == null || "".equals(thumb))
				continue;
			pis.add(new PictureInfo(thumb, bmiddlePic[i], originalPic[i]));
		}
		secondCenterPane.add(contentPanel);
		if (pis != null && pis.size() > 0) {
			final JPanel tp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			secondCenterPane.add(tp, BorderLayout.SOUTH);
			for (final PictureInfo pi : pis) {
				final JLabel tl = new JLabel(
						Theme.getThemeRec(Theme.SysRec.loadingImage));
				tl.setBorder(BorderFactory.createLineBorder(
						Theme.getTransparentColor(Color.ORANGE, 0), 1));
				tl.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseExited(MouseEvent arg0) {
						tl.setBorder(BorderFactory.createLineBorder(
								Theme.getTransparentColor(Color.ORANGE, 0), 1));
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						tl.setBorder(BorderFactory.createLineBorder(
								Theme.getTransparentColor(Color.ORANGE, 155), 1));
					}

					@Override
					public void mouseClicked(MouseEvent arg0) {
						PictureDialog.showPictureDialog(
								pis.toArray(new PictureInfo[pis.size()]),
								pis.indexOf(pi));
					}
				});
				new Thread(new Runnable() {
					public void run() {
						try {
							tl.setIcon(Theme.getThumbnailPic(pi
									.getThumbnailPic()));
						} catch (Exception e) {
							tl.setIcon(Theme
									.getThemeRec(Theme.SysRec.avatarSImage));
						}
					}
				}).start();
				tp.add(tl);
				validate();
			}
		}
	}

	/**
	 * @return 返回昵称标签
	 */
	private JLabel getNameLabel() {
		nameLabel = new JLabel("姓名未知");
		User user = null;
		if (status == null || (user = status.getUser()) == null)
			return nameLabel;
		String name = user.getName();
		try {
			String tmp = new String(name.getBytes("GBK"), "ISO8859_1");
			if (tmp.length() > nameLength) {
				name = Theme.bSubstring(name, nameLength) + "...";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		nameLabel.setText(name);
		nameLabel.setToolTipText(status.getUser().getName());
		nameLabel.setForeground(Color.RED);
		nameLabel.addMouseListener(new SelfMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {

			}
		});
		return nameLabel;
	}

	/**
	 * @return 返回时间标签
	 */
	private JLabel getDateLabel() {
		dateLabel = new JLabel("时间未知");
		Date date = null;
		if (status == null || (date = status.getCreatedAt()) == null)
			return dateLabel;
		Calendar cc = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (cc.get(Calendar.YEAR) == c.get(Calendar.YEAR)
				&& cc.get(Calendar.MONTH) == c.get(Calendar.MONTH)
				&& Calendar.getInstance().get(Calendar.DATE) == c
						.get(Calendar.DATE)
				&& cc.get(Calendar.HOUR_OF_DAY) == c.get(Calendar.HOUR_OF_DAY)) {
			if (cc.get(Calendar.MINUTE) - c.get(Calendar.MINUTE) == 0)
				dateLabel.setText("刚刚");
			else
				dateLabel.setText((cc.get(Calendar.MINUTE) - c
						.get(Calendar.MINUTE)) + "分钟前");
		} else if (cc.get(Calendar.YEAR) == c.get(Calendar.YEAR)
				&& cc.get(Calendar.MONTH) == c.get(Calendar.MONTH)
				&& cc.get(Calendar.DATE) < c.get(Calendar.DATE) + 3) {
			String text = "";
			if (cc.get(Calendar.DATE) == c.get(Calendar.DATE))
				text = "今天 ";
			else if (cc.get(Calendar.DATE) == c.get(Calendar.DATE) + 1)
				text = "昨天 ";
			else if (cc.get(Calendar.DATE) == c.get(Calendar.DATE) + 2)
				text = "前天 ";
			text += c.get(Calendar.HOUR_OF_DAY)
					+ ":"
					+ (c.get(Calendar.MINUTE) < 10 ? "0"
							+ c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE));
			dateLabel.setText(text);
		} else
			dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.format(status.getCreatedAt()));
		return dateLabel;
	}

	private abstract class SelfMouseAdapter extends MouseAdapter {

		private Color foreground;

		@Override
		public void mouseEntered(MouseEvent event) {
			foreground = event.getComponent().getForeground();
			event.getComponent().setForeground(Color.MAGENTA);
			event.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent event) {
			event.getComponent().setForeground(foreground);
		}

	}

}
