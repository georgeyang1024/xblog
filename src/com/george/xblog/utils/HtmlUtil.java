package com.george.xblog.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.george.xblog.param.MainParam;


public class HtmlUtil {
	/**
	 * get tag inside content form html file
	 * @param param
	 * @param html
	 * @param tag_start
	 * @param tag_end
	 * @return
	 * @throws Exception
	 */
	public static List<String> getHtmlContent (String html,String tag_start,String tag_end) throws Exception  {
		List<String> contentList = new ArrayList<String>();
		Stack<Integer> sk = new Stack<Integer>();
		
		boolean end = false;//匹配完毕
		int cutIndexStart=0,cutIndexEnd=0;//有效字符的索引
		int currIndex = 0;//游标位置
		
		while (!end) {
			int tag_start_index = html.indexOf(tag_start,currIndex);
			tag_start_index = tag_start_index == -1 ? Integer.MAX_VALUE : tag_start_index;
			
			int tag_end_index = html.indexOf(tag_end,currIndex);
			tag_end_index = tag_end_index == -1?Integer.MAX_VALUE:tag_end_index;

			//结束条件,全部结束了
			if (tag_start_index==tag_end_index && tag_start_index == Integer.MAX_VALUE) {//都是无限大
				end = true;
			} else if (tag_start_index<tag_end_index) {//开头
				if (sk.isEmpty()) {//一个域的开头
					cutIndexEnd = tag_start_index;
				}
				sk.push(tag_start_index);
				
				currIndex = tag_start_index+tag_start.length();
			} else if (tag_start_index>tag_end_index) {
				if (!sk.isEmpty()) {
					sk.pop();
				} else {
					throw new Exception("tag no start,but it end:"+tag_end);
				}
				
				currIndex = tag_end_index + tag_end.length();
				
				if (sk.isEmpty()) {//一个域的结束
					//标签里面的内容：
					String content = html.substring(cutIndexEnd + tag_start.length(),tag_end_index);
					contentList.add(content);
				}
			}
		}
		return contentList;		
	}
	
	public static List<String> getHtmlSource (MainParam param,String html) throws Exception  {
		List<String> contentList = getHtmlSource(param,html,"<xblogIgnore>","</xblogIgnore>");
		StringBuffer sb = new StringBuffer();
		for (String str:contentList) {
			sb.append(str);
		}
		
		contentList = getHtmlSource(param,sb.toString(),"<xblogVar>","</xblogVar>");
		sb = new StringBuffer();
		for (String str:contentList) {
			sb.append(str);
		}
		
		contentList = getHtmlSource(param,sb.toString(),"<xblogReplace>","</xblogReplace>");
		sb = null;
		return contentList;
	}
	
	//contentList = getHtmlSource(sb.toString(),"<xblogVar>","</xblogVar>");
	
	/**
	 * how to express this?
	 * @param param
	 * @param html
	 * @param tag_start
	 * @param tag_end
	 * @return
	 * @throws Exception
	 */
	private static List<String> getHtmlSource (MainParam param,String html,String tag_start,String tag_end) throws Exception  {
		List<String> contentList = new ArrayList<String>();
		Stack<Integer> sk = new Stack<Integer>();
		
		boolean end = false;//匹配完毕
		int cutIndexStart=0,cutIndexEnd=0;//有效字符的索引
		int currIndex = 0;//游标位置
		
		while (!end) {
			int tag_start_index = html.indexOf(tag_start,currIndex);
			tag_start_index = tag_start_index == -1 ? Integer.MAX_VALUE : tag_start_index;
			
			int tag_end_index = html.indexOf(tag_end,currIndex);
			tag_end_index = tag_end_index == -1?Integer.MAX_VALUE:tag_end_index;

			//结束条件,全部结束了
			if (tag_start_index==tag_end_index && tag_start_index == Integer.MAX_VALUE) {//都是无限大
				end = true;
			} else if (tag_start_index<tag_end_index) {//开头
				if (sk.isEmpty()) {//一个域的开头
					cutIndexEnd = tag_start_index;
				}
				sk.push(tag_start_index);
				
				currIndex = tag_start_index+tag_start.length();
			} else if (tag_start_index>tag_end_index) {
				if (!sk.isEmpty()) {
					sk.pop();
				} else {
					throw new Exception("tag no start,but it end:"+tag_end);
				}
				
				
				currIndex = tag_end_index + tag_end.length();
				if (sk.isEmpty()) {//一个域的结束
					String content = html.substring(cutIndexStart,cutIndexEnd);
					contentList.add(content);
					
					
					if ("<xblogVar>".equals(tag_start)) {
						//标签里面的内容：
						String tagContent = html.substring(cutIndexEnd + tag_start.length(),tag_end_index);
						contentList.add(ParamUtil.getValueFormObject(param,param,tagContent).toString());
					}
					
					cutIndexStart = currIndex;
				}
			}
		}
		if (!sk.isEmpty()) {
			throw new Exception("tag no end:" + tag_start);
		}
		String content = html.substring(currIndex,html.length());
		contentList.add(content);
		return contentList;
	}

}
