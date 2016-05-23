package org.kerwin.weibo.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import weibo4j.model.Paging;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class MentionTimelinePane extends TimelinePane {
	
	private static final long serialVersionUID = 1L;

	public MentionTimelinePane() {
		super();
		headPanel.removeAll();
		headPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		headPanel.add(loadLabel,gbc);
		gbc.gridy=1;
	}

	@Override
	protected StatusWapper getStatusWapper() {
		StatusWapper sw = null;
		try {
			if(this.lastId == Long.MAX_VALUE){
				sw = timeLine.getMentions(new Paging(1), 0, 0, 0);
			}else{
				sw = timeLine.getMentions(new Paging(1,20,1,this.lastId), 0, 0, 0);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return sw;
	}

}
