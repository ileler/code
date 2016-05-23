package org.kerwin.shutdownui.bean;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 1L;

	private Long uid;
	private Long pid;
	private String name;
	private String accessToken;

	public User() {
		super();
	}

	public User(Long uid, Long pid, String name, String accessToken) {
		super();
		this.uid = uid;
		this.pid = pid;
		this.name = name;
		this.accessToken = accessToken;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public int compareTo(User o) {
		if (o.getPid() > getPid())
			return -1;
		return 1;
	}

}
