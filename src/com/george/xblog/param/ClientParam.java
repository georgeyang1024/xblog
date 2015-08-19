package com.george.xblog.param;

import java.util.Map;


/**
 * 访问者的参数,包括ip信息，cookie等
 * @author george.yang
 *
 */
public class ClientParam {
	public String ip;
	public int currPage;//当前页码
	public String currTag;//当前选择的tag
	public String currYearStart;//选择的年份
	public String currYearEnd;//选择的年份
	public String currMonthStart;//选择的月份
	public String currMonthEnd;//选择的月份
	public String currMdFile;//当前打开的文章的md文件
	
	public Map prams;
	public Map header;
	
}
