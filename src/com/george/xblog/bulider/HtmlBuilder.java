package com.george.xblog.bulider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;


import com.alibaba.fastjson.JSONObject;
import com.george.xblog.entity.Config;
import com.george.xblog.param.MainParam;
import com.george.xblog.utils.FileUtil;
import com.george.xblog.utils.HtmlUtil;

public class HtmlBuilder implements BlogBuilder {
	public MainParam param;
	public HtmlBuilder parent;
	public String dir;;
	
	public HtmlBuilder(MainParam param, HtmlBuilder parent,String dir) {
		this.param = param;
		this.parent = parent;
		this.dir = dir;
	}

	public StringBuffer build () throws Exception {
		System.out.println("测试");
		
		if (dir == null || "".equals(dir) || "null".equals(dir)) {
			return new StringBuffer();
		}
		
		File tagDir = null;
		if (parent==null) {
			tagDir = new File(param.serverParam.rootPath,dir);
		} else {
			tagDir = new File(parent.dir,dir);
		}
		
		if (!tagDir.exists()) {
			tagDir = new File(param.serverParam.rootPath + File.separator + param.blogParam.theme,dir);
			if (!tagDir.exists()) {
				throw new FileNotFoundException("tag dir not exist:" + tagDir);
			}
		} 
		
		dir = tagDir.getAbsolutePath();
		
		if (tagDir.isFile()) {
			throw new FileNotFoundException("tag dir is no a floder~:" +dir);
		}
		
		File configFile = new File(tagDir.getAbsolutePath(),"config.json");
		if (!configFile.exists()) {
			throw new FileNotFoundException("config.json not exist in this path:" +dir);
		} else if (!configFile.isFile()) {
			throw new FileNotFoundException("config.json should be a file in this path:" +dir);
		}
		
		
		System.out.println("start build:" + dir);
		
		String content = FileUtil.readContent(configFile);
		Config config = JSONObject.parseObject(content,Config.class);
		
		String html = FileUtil.readContent(new File(dir,config.htmlName));
		List<String> htmlContent = HtmlUtil.getHtmlSource(param,html);
		
		if (htmlContent.size()-1>config.params.size()) {
			throw new Exception("Too few parameters in config file:" + configFile.getAbsolutePath() + ",html need:" +htmlContent.size() + " parameters" );
		} else if (htmlContent.size()-1<config.params.size()) {
			throw new Exception("Too many parameters in config file:" + configFile.getAbsolutePath()+ ",html need:" +htmlContent.size() + " parameters");
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i=0;i<config.params.size();i++) {
				sb.append(htmlContent.get(i));
				
				TypeBuilder tBuilder = new TypeBuilder(config.params.get(i),this);
				sb.append(tBuilder.build());
				
				tBuilder = null;
			}
			sb.append(htmlContent.get(htmlContent.size()-1));
			htmlContent = null;
			return sb;
		}
	}
}
