package org.utils;

import java.util.Map;

public class MyUtil {
	/**
	 * 判断object是否为空，并且为空的时候设置为何值
	 * 
	 * @param obj
	 * @param val
	 * @return
	 */
	public static Object setEmpty(Object obj, Object val) {
		return isEmpty(obj) ? val : obj;
	}

	/**
	 * 判断object是否为空
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Object obj) {
		if (obj == null)
			return Boolean.TRUE;
		if (obj instanceof String) {
			if (obj.toString().trim().length() == 0
					|| obj.toString().trim().equalsIgnoreCase("null"))
				return Boolean.TRUE;
			else
				return Boolean.FALSE;
		} else if (obj instanceof Map) {
			return ((Map) obj).size() == 0 ? Boolean.TRUE : Boolean.FALSE;
		} else if (obj instanceof Object[]) {
			return ((Object[]) obj).length == 0 ? Boolean.TRUE : Boolean.FALSE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 返回网页字符串添加颜色设置
	 * 
	 * @param message
	 * @param color
	 * @return
	 */
	public static String returnMsg(String message, String color) {
		if (isEmpty(color))
			return message;
		return "<span style='color:" + color + ";'>" + message + "</span>";
	}

	/**
	 * 把数组用 splitstr 连接起来返回一个字符串
	 * 
	 * @param splitstr
	 * @param str
	 * @return
	 */
	public static String joinStr(String splitstr, String... str) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String st : str) {
			i++;
			sb.append(st);
			if (i != str.length)
				sb.append(splitstr);
		}
		return sb.toString();
	}
}
