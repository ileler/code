package org.kerwin.weibo.dao;

import java.util.List;

import org.kerwin.weibo.mode.User;

import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

public interface UserDao {
	public boolean addUser(User user);
	public boolean deleteUser(User user);
	public boolean updateUser(User user);
	public User selectUserById(long id);
	public List<User> selectAllUser();
	public List<String> selectAllUserName();
	public String getOauthURL();
	public User getUserByCode(String code) throws WeiboException, JSONException;
	public weibo4j.model.User getWeiboUserByUser(User user) throws JSONException, WeiboException;
	public weibo4j.model.User getWeiboUserByAccessToken(String accessToken) throws JSONException, WeiboException;
}
