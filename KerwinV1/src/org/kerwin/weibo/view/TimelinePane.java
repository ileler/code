package org.kerwin.weibo.view;

import java.util.List;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

public abstract class TimelinePane extends CommonPane {
	
	private static final long serialVersionUID = 1L;
	protected Timeline timeLine;
	protected Long lastId;

	public TimelinePane() {
		super();
		lastId = Long.MAX_VALUE;
		if(user != null){
			timeLine = new Timeline();
			timeLine.client.setToken(user.getAccessToken());
		}
	}
	
	public void refresh(){
		this.lastId = Long.MAX_VALUE;
		super.refresh();
	}
	
	@Override
	public void run(){
		setIsLoading(true);
		StatusWapper sw = null;
		if(timeLine == null || timeLine.client.getToken() == null || timeLine.client.getToken().isEmpty() || (sw = getStatusWapper()) == null){
			setIsLoading(false);
			return;
		}
		List<Status> ss = sw.getStatuses(); 
		for(Status s : ss){
			long id = Long.parseLong(s.getId());
			if(id == lastId){
				continue;
			}
			lastId = id;
			StatusPanel sp;
			try {
				sp = new StatusPanel(s);
				mainPanel.add(sp,gbc);
				gbc.gridy++;
				validate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setIsLoading(false);
		if(getVerticalScrollBar().isShowing())
			floorPanel.setVisible(true);
		else
			floorPanel.setVisible(false);
	}

//	@Override
//	public void run(){
//		setIsLoading(true);
//		SwingUtilities.invokeLater(new Runnable(){
//			public void run(){
//				StatusWapper sw = null;
//				if(timeLine == null || timeLine.client.getToken() == null || timeLine.client.getToken().isEmpty() || (sw = getStatusWapper()) == null){
//					setIsLoading(false);
//					return;
//				}
//				List<Status> ss = sw.getStatuses(); 
//				gbc.gridy+=ss.size();
//				for(int i = ss.size()-1; i > -1; i--){
//					gbc.gridy--;
//					long id = Long.parseLong(ss.get(i).getId());
//					if(i == ss.size()-1){
//						lastId = id;
//					}
//					if(id == lastId){
//						continue;
//					}
//					StatusPanel sp = new StatusPanel(ss.get(i));
//					mainPanel.add(sp,gbc);
//					validate();
//				}
//				gbc.gridy+=ss.size();
//				setIsLoading(false);
//				if(getVerticalScrollBar().isShowing())
//					floorPanel.setVisible(true);
//				else
//					floorPanel.setVisible(false);
//			}
//		});
//	}

	protected abstract StatusWapper getStatusWapper();
	
}
