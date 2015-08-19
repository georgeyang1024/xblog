package com.george.xblog.param;

public class MainParam {
	public ServerParam serverParam;
	public ClientParam clientParam;
	public BlogParam blogParam;
	public RunParam runParam;
	public String value;
	
	public MainParam () {
		
	}
	
	public MainParam(ServerParam serverParam, ClientParam clientParam,BlogParam blogParam, RunParam runParam) {
		this.serverParam = serverParam;
		this.clientParam = clientParam;
		this.blogParam = blogParam;
		this.runParam = runParam;
	}
	
}
