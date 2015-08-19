package com.george.xblog.entity;

import java.util.List;

/**
 * 每个目录都有的配置文件,进入目录后第一个读取的文件
 * @author george.yang
 *
 */
public class Config {
	public String htmlName;
	public List<Type> params;
}
/**
 params包含以下字段:
 	currType=3
 	condition : RunParam.currPage=param
	true:numberSelect
	false:numberUnSelect
 **/


/**
type=1
直接写入内容
value:BlogParam.blogName

type=2
写入下一个文件夹的生成的内容
dir:tag

type=3
condition : RunParam.currPage=RunParam.pageCount
true:numberSelect
false:numberUnSelect
判断true\false生成对应的文件夹的内容

type=4
values : RunParam.tagList
dir:tag
list重复创建,下一个文件夹获取参数:RunParam.value

**/