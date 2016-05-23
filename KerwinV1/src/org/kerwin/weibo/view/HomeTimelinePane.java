package org.kerwin.weibo.view;

import weibo4j.model.Paging;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class HomeTimelinePane extends TimelinePane {
	
	private static final long serialVersionUID = 1L;

	public HomeTimelinePane() {
		super();
	}

	@Override
	protected StatusWapper getStatusWapper() {
		StatusWapper sw = null;
		try {
			if(this.lastId == Long.MAX_VALUE){
				sw = timeLine.getFriendsTimeline(0, 0, new Paging(1));
			}else{
				sw = timeLine.getFriendsTimeline(0, 0, new Paging(1,20,1,this.lastId));
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return sw;
	}

}
