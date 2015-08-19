package com.george.xblog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.george.xblog.utils.FileUtil;
import com.george.xblog.utils.FromRequest;



public class FileUpload extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("doPost!!!");
		FromRequest fr = new FromRequest(request, response);
		Map<String, Object> map = fr.toMap();
		
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			if (obj instanceof FileItem) {
				FileItem fileitem = (FileItem) obj;
				String itemFileName = fileitem.getName();
				String filename = FileUtil.getFileName(itemFileName) + "." + FileUtil.getExtensionName(itemFileName);
				File file = new File(getServletContext().getRealPath("/") + "blog", filename);
				System.out.println("file:" + file);
				
				if (!FromRequest.save(getServletContext(), fileitem,"blog", filename)) {
					file.delete();
					printHtml(response,"fail");
					return;
				} else {
					printHtml(response,"success");
					return;
				}
			} else {
				//不是文件，不处理
			}
		}
		printHtml(response,"error");
	}
	
	private void printHtml (HttpServletResponse response,String content) {
		try {
			PrintWriter out = response.getWriter();
			out.print(content);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
