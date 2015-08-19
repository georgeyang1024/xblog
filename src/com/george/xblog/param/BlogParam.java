package com.george.xblog.param;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.george.xblog.entity.ArchiveVo;
import com.george.xblog.entity.BlogVo;
import com.george.xblog.entity.CategorieVo;
import com.george.xblog.entity.TagVo;
import com.george.xblog.utils.FileUtil;
import com.george.xblog.utils.LruCache;

/**
 * 初始化博客的参数，文章列表、标签列表、日期列表等
 * 
 * @author george.yang
 * 
 */
public class BlogParam {
	private int articleCount;// 文章数量
	private String articleList;// 文章列表

	private int port = 1;// 开启的端口
	public String mdDir = "";// md文件路径
	public String theme = "";// 网站主题
	private String blogName = "";// 博客名
	private String introduction = "";// 简介
	private int themeType = 1;// 主题类型
	private String logo = "";// logo url
	private String sinaLink = "";
	public String archivesFormatl;// 分类的时间格式
	private String emailLink = "";
	private String githubLink = "";
	private String facebookLink = "";
	private int onePageCount = 3;
	public String indexHtml = "index.html";
	public String aboutHtml = "about/about.html";

	public Map<String, BlogVo> blogMap;
	public ArrayList<BlogVo> blogList;

	public List<TagVo> tagList;
	public List<CategorieVo> categorieList;
	public List<ArchiveVo> archiveList;

	public Map<String, List<BlogVo>> tagMap;
	public Map<String, List<BlogVo>> categorieMap;
	public Map<String, List<BlogVo>> archivesMap;
	public Map<String, Map<String, Long>> fileDateMap;//

	public boolean hasNextPage;
	public int currPage;
	public boolean hasPrevPage;

	public ArrayList<BlogVo> getBlogList() {
		return blogList;
	}

	public Map<String, Map<String, Long>> getFileDateMap (MainParam param) {
		//JSONObject.parseObject(blogConfig,BlogParam.class);
		System.out.println("getFileDateMap!!");
		
		fileDateMap = LruCache.getInstance().get("getFileDateMap");
		if (fileDateMap == null) {
			if (blogList == null) {
				blogList = getBlogList();
			}
			
			//updateFileTime;
			String fileDateContent = FileUtil.readContent(new File(param.serverParam.rootPath + File.separator + param.blogParam.mdDir,"fileDate.json"));
			System.out.println("json:" + fileDateContent);
			try {
				fileDateMap = JSONObject.parseObject(fileDateContent,Map.class);;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fileDateMap==null) {
				fileDateMap = new HashMap<String,Map<String,Long>>();
			}
			
			for (BlogVo vo:blogList) {
				Map dateMap = fileDateMap.get(vo.name);
				File blogFile = new File(vo.filePath);
				if (dateMap==null) {
					dateMap = new HashMap<String,Long>();
					long minTime = blogFile.lastModified()<System.currentTimeMillis()?blogFile.lastModified():System.currentTimeMillis();
					dateMap.put("createTime",minTime);
					
					Date createDate = new Date(minTime);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
					String month = formatter.format(createDate);
					dateMap.put("createTimeFormat",month);
					
					fileDateMap.put(vo.name,dateMap);
				}
				dateMap.put("updateTime",blogFile.lastModified());
				
				Date createDate = new Date(blogFile.lastModified());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
				String month = formatter.format(createDate);
				dateMap.put("createTimeFormat",month);
			}
			
			LruCache.getInstance().put("getFileDateMap", 5 * 60 * 1000, fileDateMap);
		}
		
		System.out.println("getFileDateMap:" + fileDateMap.toString());
		
		return fileDateMap;
	}
	
	// public List<BlogVo> searchResult;
	public Map<String, BlogVo> getBlogMap() {
		blogMap = LruCache.getInstance().get("getBlogMap");
		if (blogMap == null) {
			blogMap = new HashMap<String, BlogVo>();
			if (blogList == null) {
				blogList = getBlogList();
			}

			for (BlogVo vo : blogList) {
				blogMap.put(vo.name, vo);
			}

			LruCache.getInstance().put("getBlogMap", 5 * 60 * 1000, blogMap);
		}
		return blogMap;
	}

	public List<TagVo> getTagList() {
		tagList = LruCache.getInstance().get("getTagList");
		if (tagList == null) {
			if (blogList == null) {
				blogList = getBlogList();
			}
			Map<String, Integer> tags = new HashMap<String, Integer>();
			for (BlogVo vo : blogList) {
				for (TagVo tvo : vo.tags) {
					Integer inttt = tags.get(tvo.name);
					if (inttt == null) {
						inttt = new Integer(0);
					}
					tags.put(tvo.name, inttt + 1);
				}
			}

			tagList = new ArrayList<TagVo>();
			for (String key : tags.keySet()) {
				TagVo tagvo = new TagVo();
				tagvo.name = key;
				tagvo.count = tags.get(key);
				tagList.add(tagvo);
			}

			LruCache.getInstance().put("getTagList", 5 * 60 * 1000, tagList);
		}
		return tagList;
	}

	public List<CategorieVo> getCategorieList() {
		categorieList = LruCache.getInstance().get("getCategorieList");
		if (categorieList==null) {
			if (blogList == null) {
				blogList = getBlogList();
			}
			
			Map<String,Integer> types = new HashMap<String,Integer>();
			for (BlogVo vo:blogList) {
				Integer typeCount = types.get(vo.classify);
				if (typeCount==null) {
					typeCount = new Integer(0);
				}
				types.put(vo.classify,typeCount+1);
			}
			
			categorieList = new ArrayList<CategorieVo>();
			for (String key:types.keySet()) {
				CategorieVo vo = new CategorieVo();
				vo.name = key;
				vo.count = types.get(key);
				categorieList.add(vo);
			}
			
			LruCache.getInstance().put("getCategorieList", 5 * 60 * 1000, categorieList);
		}
		return categorieList;
	}

	public List<BlogVo> getSearchResult(MainParam param) {
		if (blogList != null) {
			String key = param.clientParam.prams.get("key").toString();

			List<BlogVo> list = LruCache.getInstance().get("search:key" + key);

			if (list == null) {
				for (BlogVo vo : blogList) {
					if (vo.name.indexOf(key) != -1) {
						list.add(vo);
					}
				}

				LruCache.getInstance().put("search:key" + key, 5 * 60 * 1000,
						list);
			}

			return list;
		}
		return new ArrayList<BlogVo>();
	}

	public List<ArchiveVo> getArchiveList(MainParam param) {
		if (fileDateMap == null) {
			fileDateMap = getFileDateMap(param);
		}

		archiveList = LruCache.getInstance().get("getArchiveList");
		if (archiveList == null) {
			Map<String, Integer> countMap = new HashMap<String, Integer>();
			for (String key : fileDateMap.keySet()) {
				Long createTime = fileDateMap.get(key).get("createTime");
				if (createTime != null) {
					Date createDate = new Date(createTime);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM");
					String month = formatter.format(createDate);

					Integer count = countMap.get(month);
					if (count == null) {
						count = 0;
					}
					countMap.put(month, count + 1);
				}
			}

			archiveList = new ArrayList<ArchiveVo>();
			for (String month : countMap.keySet()) {
				ArchiveVo vo = new ArchiveVo();
				vo.name = month;
				vo.count = countMap.get(month);
				archiveList.add(vo);
			}

			LruCache.getInstance().put("getArchiveList", 5 * 60 * 1000,
					archiveList);
		}
		return archiveList;
	}

	public Map<String, List<BlogVo>> getArchivesMap(MainParam param) {
		if (fileDateMap == null) {
			fileDateMap = getFileDateMap(param);
		}

		archivesMap = LruCache.getInstance().get("getArchivesMap");

		if (archivesMap == null) {
			archivesMap = new HashMap<String, List<BlogVo>>();
			for (String key : fileDateMap.keySet()) {
				Long createTime = fileDateMap.get(key).get("createTime");
				if (createTime != null) {
					Date createDate = new Date(createTime);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM");
					String month = formatter.format(createDate);

					List<BlogVo> monthList = archivesMap.get(month);
					if (monthList == null) {
						monthList = new ArrayList<BlogVo>();
						archivesMap.put(month, monthList);
					}
					monthList.add(blogMap.get(key));
				}
			}

			LruCache.getInstance().put("getArchivesMap", 5 * 60 * 1000,
					archivesMap);
		}
		return archivesMap;
	}

	public Map<String, List<BlogVo>> getCategorieMap() {
		categorieMap = LruCache.getInstance().get("getCategorieMap");
		if (categorieMap == null) {

			categorieMap = new HashMap<String, List<BlogVo>>();
			for (BlogVo vo : blogList) {
				List tagList = categorieMap.get(vo.classify);
				if (tagList == null) {
					tagList = new ArrayList<BlogVo>();
					categorieMap.put(vo.classify, tagList);
				}
				tagList.add(vo);
			}

			LruCache.getInstance().put("getCategorieMap", 5 * 60 * 1000,
					categorieMap);
		}
		return categorieMap;
	}

	public Map<String, List<BlogVo>> getTagMap() {
		tagMap = LruCache.getInstance().get("getTagMap");

		if (tagMap == null) {
			tagMap = new HashMap<String, List<BlogVo>>();
			for (BlogVo vo : blogList) {
				List<TagVo> tags = vo.tags;
				if (tags != null) {
					for (TagVo tag : tags) {
						List<BlogVo> list = tagMap.get(tag.name);
						if (list == null) {
							list = new ArrayList<BlogVo>();
							tagMap.put(tag.name, list);
						}
						list.add(vo);
					}
				}
			}

			LruCache.getInstance().put("getTagMap", 5 * 60 * 1000, tagMap);
		}
		return tagMap;
	}

	public int getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public String getArticleList() {
		return articleList;
	}

	public void setArticleList(String articleList) {
		this.articleList = articleList;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	

	public String getBlogName() {
		return blogName;
	}

	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getThemeType() {
		return themeType;
	}

	public void setThemeType(int themeType) {
		this.themeType = themeType;
	}


	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSinaLink() {
		return sinaLink;
	}

	public void setSinaLink(String sinaLink) {
		this.sinaLink = sinaLink;
	}

	public String getEmailLink() {
		return emailLink;
	}

	public void setEmailLink(String emailLink) {
		this.emailLink = emailLink;
	}

	public String getGithubLink() {
		return githubLink;
	}

	public void setGithubLink(String githubLink) {
		this.githubLink = githubLink;
	}

	public String getFacebookLink() {
		return facebookLink;
	}

	public void setFacebookLink(String facebookLink) {
		this.facebookLink = facebookLink;
	}

	public int getOnePageCount() {
		return onePageCount;
	}

	public void setOnePageCount(int onePageCount) {
		this.onePageCount = onePageCount;
	}

}
