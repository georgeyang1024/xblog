package com.george.xblog.bulider;

import java.util.List;
import java.util.Map;

import com.george.xblog.entity.Type;
import com.george.xblog.utils.ParamUtil;


public class TypeBuilder implements BlogBuilder {
	public Type type;
	private HtmlBuilder htmlBuilder;
	
	class BuildType {
		public static final int CONTENT = 1;
		public static final int DIR = 2;
		public static final int CONDITION = 3;
		public static final int VALUES  = 4;
		public static final int VALUE  = 5;
		public static final int NUMBERFOR = 6;
	}
	
	public TypeBuilder(Type type,HtmlBuilder htmlBuilder) {
		this.type= type;
		this.htmlBuilder=htmlBuilder;
	}
	
	public StringBuffer build() throws Exception {
		switch (type.type) {
			case BuildType.CONTENT:
				String value = ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.value).toString();
				return new StringBuffer(value);
			case BuildType.VALUE:
				htmlBuilder.param.runParam.value = ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.value);
				HtmlBuilder hb5 = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
				return hb5.build();
			case BuildType.DIR:
				HtmlBuilder hb = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
				return hb.build();
			case BuildType.CONDITION:
				String[] condition = null;
				int mathMethod = -1;
				if (type.condition.indexOf(">=")>0) {
					mathMethod = 0;
					condition = type.condition.split(">=");
					
				} else if (type.condition.indexOf("<=")>0) {
					mathMethod = 1;
					condition = type.condition.split("<=");
					
				} else if (type.condition.indexOf("=")>0) {
					mathMethod = 2;
					condition = type.condition.split("=");
					
				} else if (type.condition.indexOf(">")>0) {
					mathMethod = 3;
					condition = type.condition.split(">");
					
				} else if (type.condition.indexOf("<")>0) {
					mathMethod = 4;
					condition = type.condition.split("<");
				}
				
				String condition_value_left = "",condition_value_right = "";
				condition_value_left = ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,condition[0]).toString();
				if ("".equals(condition_value_left)){
					condition_value_left=condition[0];
				}
				condition_value_right = ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,condition[1]).toString();
				if ("".equals(condition_value_right)){
					condition_value_right=condition[1];
				}
				boolean boolRet  = false;
				if (mathMethod==0) {
					int left = Integer.valueOf(condition_value_left);
					int rigth = Integer.valueOf(condition_value_right);
					boolRet = (left>=rigth);
				} else if (mathMethod ==1) {
					int left = Integer.valueOf(condition_value_left);
					int rigth = Integer.valueOf(condition_value_right);
					boolRet = (left<=rigth);
				} else if (mathMethod ==2) {
					boolRet = (condition_value_left.equals(condition_value_right));
				} else if (mathMethod ==3) {
					int left = Integer.valueOf(condition_value_left);
					int rigth = Integer.valueOf(condition_value_right);
					boolRet = (left>rigth);
				}else if (mathMethod == 4) {
					int left = Integer.valueOf(condition_value_left);
					int rigth = Integer.valueOf(condition_value_right);
					boolRet = (left<rigth);
				}
				
				if (boolRet) {
					return new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.yes).build();
				} else {
					return new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.no).build();
				}
			case BuildType.VALUES:
				StringBuffer sbValues = new StringBuffer();
				Object values = ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.values);
				
				if (values instanceof String[]) {
					for (String str:(String[])values) {
						htmlBuilder.param.runParam.string = str;
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				} else if (values instanceof int[]) {
					for (int integer:(int[])values) {
						htmlBuilder.param.runParam.integer = integer;
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				} else if (values instanceof Integer[]) {
					for (Integer integer:(Integer[])values) {
						htmlBuilder.param.runParam.integer = integer.intValue();
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
					
				} else if (values instanceof boolean[]) {
					for (boolean bool:(boolean[])values) {
						htmlBuilder.param.runParam.bool = bool;
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				} else if (values instanceof Boolean[]) {
					for (Boolean bool:(Boolean[])values) {
						htmlBuilder.param.runParam.bool = bool.booleanValue();
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
					
				} else if (values instanceof Object[]) {
					for (Object object:(Object[])values) {
						htmlBuilder.param.runParam.value = object;
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param,htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				} else if (values instanceof List) {
					List list = (List)values;
					for (int i=0;i<list.size();i++) {
						htmlBuilder.param.runParam.index = i;
						htmlBuilder.param.runParam.value = list.get(i);
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				} else if (values instanceof Map) {
					Map map = ((Map)values);
					for (Object object:map.keySet()) {
						htmlBuilder.param.runParam.key = object;
						htmlBuilder.param.runParam.value = ((Map)values).get(object);
						HtmlBuilder hbValue = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
						sbValues.append(hbValue.build());
					}
				}
				return sbValues;
			case BuildType.NUMBERFOR:
				StringBuffer ret_number = new StringBuffer();
				
				int from = 1;
				if (!(type.from==null || "".equals(type.from))) {
//					from = Integer.valueOf(type.from);
					from = Integer.valueOf(ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.from).toString());
				}
				
				int to = 1;
				if (!(type.to==null || "".equals(type.to))) {
//					to = Integer.valueOf(type.to);
					to = Integer.valueOf(ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.to).toString());
				}
				
				int interval = 1;
				if (!(type.interval==null || "".equals(type.interval))) {
//					interval = Integer.valueOf(type.interval);
					interval = Integer.valueOf(ParamUtil.getValueFormObject(htmlBuilder.param,htmlBuilder.param,type.interval).toString());
				}
				
				for (int i=from;i<=to;i=i+interval) {
					htmlBuilder.param.runParam.integer = i;
					HtmlBuilder hbnumber = new HtmlBuilder(htmlBuilder.param, htmlBuilder,type.dir);
					ret_number.append(hbnumber.build());
				}
				return ret_number;
		}
		return new StringBuffer();
	}
	
}
