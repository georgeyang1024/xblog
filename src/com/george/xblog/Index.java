package com.george.xblog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.george.xblog.bulider.HtmlBuilder;
import com.george.xblog.entity.BlogVo;
import com.george.xblog.entity.CategorieVo;
import com.george.xblog.entity.TagVo;
import com.george.xblog.param.BlogParam;
import com.george.xblog.param.ClientParam;
import com.george.xblog.param.MainParam;
import com.george.xblog.param.RunParam;
import com.george.xblog.param.ServerParam;
import com.george.xblog.utils.FileUtil;
import com.george.xblog.utils.LruCache;
import com.george.xblog.utils.MarkDownUtil;
import com.george.xblog.utils.RequestUtil;

public class Index extends HttpServlet {
	private static ServerParam serverParam;
	private static BlogParam blogParam;
	private static String rootPath;
	
	@Override
	public void destroy() {
		super.destroy();
		///Users/touch_ping/Code/MyEclipse/.metadata/.me_tcat/webapps/xblog/blog/
		FileUtil.writeContent(new File(rootPath,"server.json"),JSONObject.toJSONString(serverParam));
		FileUtil.writeContent(new File(rootPath + File.separator + blogParam.mdDir,"fileDate.json"),JSONObject.toJSONString(blogParam.fileDateMap));
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if (rootPath==null) {
			rootPath = getServletContext().getRealPath("/");
		}
		
		serverParam = LruCache.getInstance().get("serverParam");
		if (serverParam==null) {
			String serverJsonContent = FileUtil.readContent(new File(rootPath,"server.json"));
			try {
				serverParam = JSONObject.parseObject(serverJsonContent,ServerParam.class);
			} catch (Exception e) {
				e.printStackTrace();
				serverParam = new ServerParam();
			}
			serverParam.rootPath = rootPath;
			
			LruCache.getInstance().put("serverParam",60*60*1000,serverParam);
		}
		
		blogParam = LruCache.getInstance().get("blogParam");
		if (blogParam==null) {
			String blogConfig = FileUtil.readContent(new File(rootPath,"blog.json"));
			System.out.println("blogConfig:" + blogConfig);
			blogParam = JSONObject.parseObject(blogConfig,BlogParam.class);
			
			try {
				blogParam.blogList = MarkDownUtil.getMDBlogList(blogParam,new File(rootPath,blogParam.mdDir),".md");
				
			} catch (Exception e) {
				e.printStackTrace();
				blogParam.blogList = new ArrayList<BlogVo>();
			}
			
			LruCache.getInstance().put("blogParam",5*60*1000,blogParam);
		}
		

		ClientParam clientParam = new ClientParam();
		clientParam.prams = RequestUtil.getRequestMap(request);
		
		
		MainParam param = new MainParam(serverParam,clientParam,blogParam,new RunParam());
		
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		try {
			String action = request.getParameter("action");
			HtmlBuilder builder = null;
			if (action == null || "".equals(action)) {
				builder = new HtmlBuilder(param,null,blogParam.theme);
			} else {
				builder = new HtmlBuilder(param,null,blogParam.theme + File.separator+"_" + action);
			}
			out.print(builder.build().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		out.flush();
		out.close();
	}

}
