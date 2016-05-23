package org.kerwin.shutdownui.view;

import weibo4j.Timeline;
import weibo4j.model.Status;

public abstract class TimelinePane extends CommonPane {

	private static final long serialVersionUID = 1L;
	protected Timeline timeLine;
	protected Long lastId;

	public TimelinePane() {
		super();
		lastId = Long.MAX_VALUE;
		if (user != null) {
			timeLine = new Timeline();
			timeLine.client.setToken(user.getAccessToken());
		}
	}

	public void refresh() {
		this.lastId = Long.MAX_VALUE;
		super.refresh();
	}

	@Override
	public void run() {
		setIsLoading(true);
		Status[] ss = null;
		if (timeLine == null || timeLine.client.getToken() == null
				|| timeLine.client.getToken().isEmpty()
				|| (ss = getStatusWapper()) == null) {
			setIsLoading(false);
			return;
		}
		for (Status s : ss) {
			long id = Long.parseLong(s.getId());
			if (id == lastId) {
				continue;
			}
			lastId = id;
			StatusPanel sp;
			try {
				sp = new StatusPanel(s);
			} catch (Exception e) {
				continue;
			}
			mainPanel.add(sp, gbc);
			gbc.gridy++;
			validate();
		}
		setIsLoading(false);
		if (getVerticalScrollBar().isShowing())
			floorPanel.setVisible(true);
		else
			floorPanel.setVisible(false);
	}

	protected abstract Status[] getStatusWapper();

}
