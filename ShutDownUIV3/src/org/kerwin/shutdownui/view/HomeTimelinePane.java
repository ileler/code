package org.kerwin.shutdownui.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;

import org.kerwin.shutdownui.service.Theme;

import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class HomeTimelinePane extends TimelinePane {

	private static final long serialVersionUID = 1L;
	private Set<String> uns;
	private Set<String> gjz;
	private List<Status> tss;
	private Set<String> ass;
	private int count;
	private JLabel refreshLabel;

	public static String ConvertToString(InputStream inputStream) {
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder result = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStreamReader.close();
				inputStream.close();
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	public HomeTimelinePane() {
		super();
		count = 10;
		refreshLabel = new JLabel("刷新", JLabel.CENTER);
		refreshLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				initial();
			}
		});
		getContentPanel().add(refreshLabel);
		initial();
	}

	public void initial() {
		refreshLabel.setText(null);
		refreshLabel.setIcon(Theme.getThemeRec(Theme.SysRec.loadingImage));
		refreshLabel.setVisible(true);
		String responseStr = null;
		try {
			URL url = new URL("http://coderr.sinaapp.com/gx");
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			InputStream inputStream = urlConnection.getInputStream();
			responseStr = HomeTimelinePane.ConvertToString(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (responseStr == null || "".equals(responseStr)) {
			refreshLabel.setText("刷新");
			refreshLabel.setIcon(null);
			return;
		}
		refreshLabel.setVisible(false);
		getContentPanel().add(getBodyPanel());
		getContentPanel().remove(refreshLabel);
		String[] userNames = responseStr.split("<br />");
		if (userNames != null && userNames.length > 0) {
			tss = new ArrayList<Status>();
			ass = new HashSet<String>();
			uns = new HashSet<String>();
			uns.addAll(Arrays.asList(userNames));
			gjz = new HashSet<String>();
			String[] ss = new String[]{"评论中找链接","链接","地址","抢购","点击","天猫","京东"};
			gjz.addAll(Arrays.asList(ss));
		}
	}

	@Override
	protected Status[] getStatusWapper() {
		if (uns == null || uns.size() < 1) {
			return null;
		}
		List<Status> ss = new ArrayList<Status>();
		while (ss.size() < count) {
			if (tss.size() > 0) {
				Iterator<Status> iter = tss.iterator();
				while (iter.hasNext()) {
					Status s = iter.next();
					if (ss.size() < count) {
						if (!ass.contains(s.getId())) {
							ass.add(s.getId());
							ss.add(s);
						}
						iter.remove();
					} else {
						break;
					}
				}
			}
			StatusWapper sw = getStatusWapper(50);
			if (sw == null) {
				continue;
			}
			List<Status> _tss = sw.getStatuses();
			for (Status s : _tss) {
				if (uns.contains(s.getUser().getName())) {
					boolean flag = false;
					for(String str : gjz){
						if(s.getText().contains(str)){
							flag = true;
							break;
						}
					}
					if(flag)	continue;
					if (ss.size() < count) {
						if (!ass.contains(s.getId())) {
							ass.add(s.getId());
							ss.add(s);
						}
					} else {
						tss.add(s);
					}
				}
			}
		}
		return ss.toArray(new Status[ss.size()]);
	}

	private StatusWapper getStatusWapper(int count) {
		try {
			Paging paging = null;
			if (this.lastId == Long.MAX_VALUE) {
				paging = new Paging(1, count);
			} else {
				paging = new Paging(1, count, 1, this.lastId);
			}
			return timeLine.getFriendsTimeline(0, 0, paging);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return null;
	}

}
