package com.george.xblog.utils;

public class IntegerUtil {
	public static int string2int(String str,int def) {
		int ret = def;
		try {
			ret = Integer.valueOf(str);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return ret;
	}
}
