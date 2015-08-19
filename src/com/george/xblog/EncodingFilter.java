package com.george.xblog;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain arg2) throws IOException, ServletException {
//		System.out.println("getCharacterEncoding:" + response.getCharacterEncoding());
//		request.setCharacterEncoding("ISO-8859-1");
////		request.setCharacterEncoding(response.getCharacterEncoding());
		response.setCharacterEncoding("utf-8");
		// 传递控制到下一个过滤器
		arg2.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
	
}
