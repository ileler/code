package org.kerwin.shutdownui.service;

import java.util.Hashtable;
import java.util.Map;

public class Session {
	private static Map<Object, Object> session;

	static {
		session = new Hashtable<Object, Object>();
	}

	public static void set(Object key, Object value) {
		session.put(key, value);
	}

	public static Object get(Object key) {
		return session.get(key);
	}
}
