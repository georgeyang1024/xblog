package com.george.xblog;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class test extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String str = new String(request.getParameter("file").getBytes("iso-8859-1"), "utf-8"); 
		System.out.println("str:" +  str);
		System.out.println("getmap:" + request.getParameter("file"));
		printHtml(response,request.getParameter("file"));
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
