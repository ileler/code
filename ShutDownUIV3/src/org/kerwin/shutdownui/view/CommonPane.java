package org.kerwin.shutdownui.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.kerwin.shutdownui.bean.UserWithToken;
import org.kerwin.shutdownui.service.Session;
import org.kerwin.shutdownui.service.Theme;

public abstract class CommonPane extends JScrollPane implements Runnable {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	protected UserWithToken user;
	protected JPanel headPanel;
	protected JPanel bodyPanel;
	protected JPanel floorPanel;
	protected JLabel loadLabel;
	protected JPanel mainPanel;
	protected JLabel moreLabel;
	protected GridBagConstraints gbc;
	protected Thread thread;

	public CommonPane() {
		setOpaque(false); // 设置透明
		setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0,
				Theme.getTransparentColor(100)));
		getVerticalScrollBar().setOpaque(false); // 设置垂直滚动条透明//貌似毫无效果。原因未知
		getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 设置水平滚动条从不显示
		contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(false);
		setViewportView(contentPane);
		getViewport().setOpaque(false); // 滚动面板透明。与上面代码合起来才能使滚动面板透明
		getVerticalScrollBar().setUnitIncrement(10);

		user = (UserWithToken) Session.get("currentUser");
		if (user == null) {
			JLabel jl = new JLabel("无法获得信息", JLabel.CENTER);
			jl.setOpaque(false);
			contentPane.add(jl);
		} else
			initial();
	}

	public JPanel getContentPanel() {
		return contentPane;
	}

	public JPanel getBodyPanel() {
		return bodyPanel;
	}

	private void initial() {
		loadLabel = new JLabel(Theme.getThemeRec(Theme.SysRec.loadingImage),
				JLabel.CENTER);
		loadLabel.setOpaque(false);
		headPanel = new JPanel(new BorderLayout(0, 0));
		headPanel.setOpaque(false);
		headPanel.add(loadLabel);

		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setOpaque(false);
		bodyPanel = new JPanel(new BorderLayout(0, 0));
		bodyPanel.setOpaque(false);
		bodyPanel.add(mainPanel);

		moreLabel = initMoreLabel();
		floorPanel = new JPanel(new BorderLayout(0, 0));
		floorPanel.setOpaque(false);
		floorPanel.add(moreLabel);

		contentPane.add(headPanel, BorderLayout.NORTH);
		contentPane.add(bodyPanel, BorderLayout.CENTER);
		contentPane.add(floorPanel, BorderLayout.SOUTH);

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 2, 5, 2);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridy = 1;

		thread = new Thread(this);
		setIsLoading(false);
		floorPanel.setVisible(false);
	}

	public void setIsLoading(boolean flag) {
		if (loadLabel != null)
			loadLabel.setVisible(flag);
	}

	public void refresh() {
		gbc.gridy = 1;
		mainPanel.removeAll();
		mainPanel.repaint();
		addList();
	}

	public void addList() {
		if (thread != null && !thread.isInterrupted())
			thread.interrupt();
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * @return 初始化更多标签
	 */
	private JLabel initMoreLabel() {
		moreLabel = new JLabel("更多", JLabel.CENTER);
		moreLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
		moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		moreLabel.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				addList();
			}

		});
		return moreLabel;
	}

}
