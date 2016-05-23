package org.kerwin.weibo.util;

import weibo4j.model.User;

public class UserUtil {
	
	public static String getFriendsCount(User user){
		if(user == null)
			return null;
		return getCount(user.getFriendsCount());
	}
	
	public static String getStatusesCount(User user){
		if(user == null)
			return null;
		return getCount(user.getStatusesCount());
	}
	
	public static String getFollowersCount(User user){
		if(user == null)
			return null;
		return getCount(user.getFollowersCount());
	}
	
	public static String getGender(User user){
		if(user == null)
			return null;
		return user.getGender().equalsIgnoreCase("m") ? "男" : "女";
	}
	
	private static String getCount(int count){
		int num = count / 10000;
		if(num != 0){
			return Integer.toString(num) + "万";
		}else{
			return Integer.toString(count);
		}
	}
}
