package com.george.xblog.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
	public static Map<String, String> getRequestMap(HttpServletRequest request) {
//		System.out.println("getmap:" + request.getParameter("file"));
		Map<String,Object> temp = request.getParameterMap();
		Map<String,String> ret  = new HashMap<String,String>();
		for (String key:temp.keySet()) {
			Object obj = temp.get(key);
			if (obj instanceof String) {
				ret.put(key,obj.toString());
			} else if (obj instanceof String[]) {
				String[] r =  (String[])obj;
				if (!(r==null || r.length==0)) {
					try {
						ret.put(key,new String(r[0].getBytes("iso-8859-1"), "utf-8"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//默认值
		if (!ret.containsKey("page")) {
			ret.put("page","1");
		}
		return ret;
	}
}
