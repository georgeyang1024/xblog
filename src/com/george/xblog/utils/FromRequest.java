package com.george.xblog.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 处理提交enctype="multipart/form-data"的表单，文件则保存FileItem数据
 * 
 * @author user
 * 
 */
public class FromRequest {
	private Map<String, Object> data = new HashMap<String, Object>();

	public Map<String, Object> toMap() {
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		return data;
	}
	
	public Object getParameter(String key) {
		if (data != null) {
			return data.get(key);
		} else {
			return null;
		}
	}

	public String getParameterString(String key) {
		Object obj = getParameter(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	public void setParameter(String key, Object object) {
		// if (object!=null)
		// System.out.println("setParameter:" + key +"=" +object.toString());
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(key, object);
	}

	/**
	 * fileitem保存到web指定目录下
	 * 
	 * @param sc
	 * @param fileitem
	 * @param path
	 * @param filename
	 * @return
	 */
	public static boolean save(ServletContext sc, FileItem fileitem,
			String path, String filename) {
		try {
			File dir = new File(sc.getRealPath("/") + path);// 构造临时对象
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(sc.getRealPath("/") + path, filename);

			InputStream in = fileitem.getInputStream();// 获得输入数据流文件
			// 将该数据流写入到指定文件中
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 从表当获取新数据
	public FromRequest(HttpServletRequest request, HttpServletResponse response) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);// 上传文件解析
			Iterator itr = items.iterator();// 枚举方法
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {// 判断是文件还是文本信息
					this.setParameter(item.getFieldName(), item.getString());
				} else {
					// 未排除出现多个文件的可能
					if (item.getName() != null && !item.getName().equals("")) {// 判断是否选择了文件
						this.setParameter(item.getFieldName(), item);
					} else {
						this.setParameter(item.getFieldName(), null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
