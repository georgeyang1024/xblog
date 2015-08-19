package com.george.xblog.utils;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.tautua.markdownpapers.Markdown;

import com.alibaba.fastjson.JSONObject;
import com.george.xblog.entity.BlogVo;
import com.george.xblog.entity.TagVo;
import com.george.xblog.param.BlogParam;


public class MarkDownUtil {
	public static final ArrayList<BlogVo> getMDBlogList (BlogParam param,File dir,String suffix) throws Exception  {
		ArrayList<BlogVo> blogList = new ArrayList<BlogVo>();
		if (dir != null) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file:files) {
					if (file.isFile() && file.getAbsolutePath().endsWith(suffix)) {
						BlogVo vo = new BlogVo();
						
						String path = FileUtil.getLastFolder(dir.getPath());
						if (param.mdDir.equals(path)) {
							vo.classify = "Other";
						} else {
							vo.classify = path;
						}
						
						vo.filePath = file.getAbsolutePath();
						
						vo.name = FileUtil.getFileName(file.getName());
						
						vo.fileTime = file.lastModified();
						System.out.println("文件时间:" + vo.name + ">>" + vo.fileTime);		
						
						vo.content = FileUtil.readContent(file);
						vo.introduction = getMardDownIntroduction(vo.content);
						
						vo.tags = new ArrayList<TagVo>();
						List<String> noEffectContents = HtmlUtil.getHtmlContent(vo.content,"<!--","-->");
						
						for (int i=0;i<noEffectContents.size();i++){
							String epyline = noEffectContents.get(i);
							String[] epyLines = epyline.split("\n");
							for (String eline : epyLines) {
								System.out.println("eline:" + eline);
								if (eline.trim().indexOf("tags")==0) {
									String[] temptags  = StringUtil.getTagsfromLine(eline);
									System.out.println(JSONObject.toJSONString(temptags));
									System.out.println("temptags:" + temptags.toString());
									if (!(temptags==null || temptags.length==0)) {
										for (String tag:temptags) {
											TagVo tagvo = new TagVo();
											tagvo.name =  tag;
											vo.tags.add(tagvo);
										}
									}
								}
							}
						}
						
						
						StringReader sr = new StringReader(vo.content);
						StringWriter sw = new StringWriter();
						Markdown md = new Markdown();
						md.transform(sr, sw);
						
						vo.contentHtml = sw.toString();
						
						vo.contentEscape = StringUtil.getEscapeString(vo.content);
						
						Date createDate = new Date(file.lastModified());
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
						vo.createTime = formatter.format(createDate);

						blogList.add(vo);
					} else {
						List<BlogVo> subList = getMDBlogList(param,file,suffix);
						blogList.addAll(subList);
					}
				}				
			}
		}
		
		
		Collections.sort(blogList);
//		System.out.println("排序完成:" + JSONObject.toJSON(blogList));		
		return blogList;
	}
	
	public static String getMardDownIntroduction(String mdContent) {
		int startIndex = mdContent.indexOf("-->");
		if (startIndex!=-1) {
			mdContent = mdContent.substring(startIndex+3,mdContent.length());
		}
		String ret = mdContent.replace("#","")
				.replace("*","")
				.replace("~","")
				.replace(">","")
				.replace("<","")
				.replace("[","")
				.replace("]","")
				.replace("{","")
				.replace("}","")
				.replace("$","")
				.replace("^","")
				.replace("`","")
				;
		if (ret.length()>200) {
			ret = ret.substring(0,200) + " ...";
		}
		return ret;
	}
}
