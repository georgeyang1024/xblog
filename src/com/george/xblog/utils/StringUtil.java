package com.george.xblog.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	public static String getEscapeString(String str) {
		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char chr : chars) {
			switch (chr) {
			case '\n':
				sb.append("\\n");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\'':
				sb.append("\\'");
				break;
			case '|'://why cant'n support this char？
				sb.append("l");
				break;
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			default:
				sb.append(chr);
				break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param line 格式 tags:ssss,erer,shgf
	 * @return
	 */
	public static String[] getTagsfromLine (String line) {
		
		line = line.trim().replace("\t","").replace("\b","");
		
		int startIndex = line.indexOf(":");
		line = line.substring(startIndex+1,line.length());
		return  line.split(",");
		
//		System.out.println("getTagsfromLine:" + line);
//		int startIndex = line.indexOf(":");
//		line = line.substring(startIndex,line.length());
//		String[] ret = line.split(",");
//		if (ret.length==1) {
//			ret = new String[]{line.substring(startIndex,line.length())};
//		}
	}
}
