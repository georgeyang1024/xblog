package com.george.xblog.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {
	public static String readContent(File file) {
		String restring = LruCache.getInstance().get(file.getAbsolutePath());
		if (restring == null) {
			StringBuffer sb = new StringBuffer();
			try {
				InputStream in = new FileInputStream(file);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String mresu;
				while ((mresu = br.readLine()) != null) {
					sb.append(mresu + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			restring = sb.toString();
			
			LruCache.getInstance().put(file.getAbsolutePath(),1*60*1000,restring);
		}
		return restring;
	}

	public static boolean writeContent(File file, String content) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getLastFolder(String file) {
		int lastIndex = file.lastIndexOf(File.separator);
		if (lastIndex != -1) {
			return file.substring(lastIndex + 1, file.length());
		}
		return "";
	}

	public static String getLastFolder(File file) {
		return getLastFolder(file.getAbsolutePath());
	}

	/**
	 * 获取文件名
	 * 
	 * @return
	 */
	public static String getFileName(String fileName) {
		int index = fileName.indexOf(".");
		if (index == -1) {
			return "";
		} else {
			return fileName.substring(0, index);
		}
	}
	
	/**
	 * Java文件操作 获取文件扩展名,不包括"."
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
}
