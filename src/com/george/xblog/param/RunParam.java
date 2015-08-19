package com.george.xblog.param;


/**
 * 运行时的cookie
 * 保存临时的参数
 * @author george.yang
 *
 */
public class RunParam {
	public String string;
	public int integer;
	public boolean bool;
	
	public int index;//list index
	public Object key;//map key
	public Object value;//value of list or map
	
	public String param;//htmlParams;
}
