package com.george.xblog.entity;

import java.util.List;

public class BlogVo implements Comparable<BlogVo> {
	public String name;
	public String filePath;
	public String classify;//分类
	public String simpleName;
	public String createTime;
	public String lastEditTime;
	public String introduction;
	public String content;
	public String contentHtml;
	public long fileTime;//文件修改时间，用于排序
	public String contentEscape;//转义的内容
	public List<TagVo> tags;
	
	public int compareTo(BlogVo o) {
		System.out.println("compareTo:" + o);
		if (fileTime>o.fileTime) {
			return -1;
		} else {
			return 1;
		}
	}
}
