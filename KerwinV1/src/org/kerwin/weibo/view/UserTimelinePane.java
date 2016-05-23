package org.kerwin.weibo.view;

import weibo4j.model.Paging;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class UserTimelinePane extends TimelinePane {
	
	private static final long serialVersionUID = 1L;

	public UserTimelinePane() {
		super();
	}

	@Override
	protected StatusWapper getStatusWapper() {
		StatusWapper sw = null;
		try {
			if(lastId == Long.MAX_VALUE){
				sw = timeLine.getUserTimelineByUid(user.getUser().getId(), new Paging(1,20), 0, 0);
			}else{
				sw = timeLine.getUserTimelineByUid(user.getUser().getId(), new Paging(1,20,1,lastId), 0, 0);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return sw;
	}

}
