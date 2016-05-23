package org.kerwin.weibo.mode;

import weibo4j.model.User;


public class UserWithToken {
	private String accessToken;
	private weibo4j.model.User user;
	public UserWithToken() {
		super();
	}
	public UserWithToken(String accessToken, User user) {
		super();
		this.accessToken = accessToken;
		this.user = user;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public weibo4j.model.User getUser() {
		return user;
	}
	public void setUser(weibo4j.model.User user) {
		this.user = user;
	}
	
}
