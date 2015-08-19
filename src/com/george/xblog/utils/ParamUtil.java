package com.george.xblog.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.george.xblog.param.MainParam;

public class ParamUtil {
	/**
	 * 根据json的key获取值
	 * @param param
	 * @param key
	 *            如: testValue runParam.currPage blogParam.blogName
	 *            clientParam.ip blogParam.blogList //error bolgParam.map.name
	 */
	public static Object getValueFormObject(MainParam params, Object obj,String key) throws Exception {
		System.out.println("getValueFormObject:obj=" + obj + " >> key=" + key);
		Object result = null;
		if (key!=null) {
			//reset key name
			int leftIndex = key.indexOf("{");
			if (leftIndex != -1) {
				int rightIndex = findVarLastIndex(key,'{','}');
				if (rightIndex <= 0) {
					throw new Exception("{} no matching");
				}
				
				String varName = key.substring(leftIndex+1, rightIndex);

				System.out.println("varName:" + varName);
				Object ret = getValueFormObject(params, params, varName);
				System.out.println("key:" + key);
				System.out.println("varName ret:" + ret.toString());
				
				StringBuffer sb = new StringBuffer();
				sb.append(key.substring(0,leftIndex));
				sb.append(ret.toString());
				sb.append(key.substring(rightIndex+1,key.length()));
				key = sb.toString();
				System.out.println("key2:" + key);
			}
			
			//[0-100]
			int fromIndex = key.indexOf("[");
			if (fromIndex!=-1) {
				System.out.println("[] find :" + key);
				int toIndex = findVarLastIndex(key,'[',']');
				if (toIndex <= 0) {
					throw new Exception("[] no matching");
				}
				
				//get ?[]left value
				String tempKey = key.substring(0,fromIndex);
				System.out.println("tempKey:" + tempKey);
				obj = getValueFormObject(params,obj,tempKey);
				
				int centerIndex = key.indexOf("~");
				if (centerIndex==-1) {
					String indexKey =  key.substring(fromIndex+1,key.length());
					System.out.println("indexKey:" + indexKey);
					int getIndex = Integer.valueOf(getValueFormObject(params,obj,indexKey).toString());
					
					if (obj instanceof List) {
						List list = (List)obj;
						List newList = new ArrayList();
						newList.add(list.get(getIndex));
						return newList;
					} else if (obj instanceof Object[]) {
						Object[] objs = (Object[])obj;
						Object[] newObjs = new Object[]{objs[getIndex]};
						return newObjs;
					} else {
						throw new Exception(tempKey + "is not List or array[]");
					}
				} else {
					String fromKey = key.substring(fromIndex+1, centerIndex);
					String toKey = key.substring(centerIndex+1,key.length()-1);
					System.out.println("fromKey:" + fromKey);
					System.out.println("toKey:" + toKey);
					int from = IntegerUtil.string2int(fromKey,-1);
					if (from < 0) {
						from = Integer.valueOf(getValueFormObject(params,params,fromKey).toString());
					}
					int to = IntegerUtil.string2int(toKey,-1);
					if (to < 0) {
						to = Integer.valueOf(getValueFormObject(params,params,toKey).toString());
					}
					System.out.println("from:" + from);
					System.out.println("to:" + to);
					
					if (obj instanceof List) {
						List list = (List)obj;
						List newList = new ArrayList();
						for (int i=from;i<=to && i<list.size();i++) {
							newList.add(list.get(i));
						}
						return newList;
					} else if (obj instanceof Object[]) {
						Object[] objs = (Object[])obj;
						List newList = new ArrayList();
						for (int i=from;i<=to && i<objs.length;i++) {
							newList.add(objs[i]);
						}
						return newList.toArray(new Object[newList.size()]);
					} else {
						throw new Exception(tempKey + "is not List or array[]");
					}
				}
				
			}
			
			
			//math +-*/
			int mathIndex = 0;
			mathIndex = key.indexOf("+");
			if (mathIndex>0) {
				String leftMath = key.substring(0,mathIndex);
				String rightMath = key.substring(mathIndex+1,key.length());
				int left = Integer.valueOf(getValueFormObject(params,obj,leftMath).toString());
				int right = Integer.valueOf(getValueFormObject(params,obj,rightMath).toString());
				return left+right;
			}
			
			mathIndex = key.indexOf("-");
			if (mathIndex>0) {
				System.out.println("======-=======");
				String leftMath = key.substring(0,mathIndex);
				String rightMath = key.substring(mathIndex+1,key.length());
				System.out.println("leftMath:" + leftMath);
				System.out.println("rightMath:" + rightMath);
				int left = Integer.valueOf(getValueFormObject(params,obj,leftMath).toString());
				int right = Integer.valueOf(getValueFormObject(params,obj,rightMath).toString());
				System.out.println("left:" + left);
				System.out.println("right:" + right);
				return left-right;
			}
			
			mathIndex = key.indexOf("*");
			if (mathIndex>0) {
				String leftMath = key.substring(0,mathIndex);
				String rightMath = key.substring(mathIndex+1,key.length());
				System.out.println("======*=======");
				System.out.println("leftMath:" + leftMath);
				System.out.println("rightMath:" + rightMath);
				int left = Integer.valueOf(getValueFormObject(params,obj,leftMath).toString());
				int right = Integer.valueOf(getValueFormObject(params,obj,rightMath).toString());
				System.out.println("left:" + left);
				System.out.println("right:" + right);
				return (int)(left*right);
			}
			
			mathIndex = key.indexOf("/");
			if (mathIndex>0) {
				String leftMath = key.substring(0,mathIndex);
				String rightMath = key.substring(mathIndex+1,key.length());
				int left = Integer.valueOf(getValueFormObject(params,obj,leftMath).toString());
				int right = Integer.valueOf(getValueFormObject(params,obj,rightMath).toString());
				if (left%right==0) {
					return (int)(left/right);
				} else {
					return (int)(left/right)+1;
				}
			}
			
			
			//get key value
			int index = key.indexOf('.');
			String key0 = "", key1 = "";
			if (index == -1) {
				key0 = key;
			} else {
				key0 = key.substring(0, index);
				key1 = key.substring(index + 1, key.length());
			}
			
			if (obj instanceof String) {
				throw new Exception("obj is string,cannot get value from:" + key);
			} else if (obj instanceof Map) {
				Map tempMap = ((Map) obj);
				if (index == -1) {
					result = tempMap.get(key);
				} else {
					result = getValueFormObject(params, tempMap.get(key0), key1);
				}
			} else if (obj instanceof List) {
				List tempList = (List)obj;
				if ("size".equals(key)) {
					return tempList.size();
				}
				
				int getIndex = IntegerUtil.string2int(key,-1);
				if (getIndex < 0) {
					getIndex = Integer.valueOf(getValueFormObject(params,params,key).toString());
				}
				if (index == -1) {
					result = tempList.get(getIndex);
				} else {
					result = getValueFormObject(params, tempList.get(getIndex), key1);
				}
			} else if (obj != null) {
				try {
					Method[] methods = obj.getClass().getDeclaredMethods();
					for (Method method : methods) {
						method.setAccessible(true);
						String name = method.getName();
						if (("get" + key0).toLowerCase().equals(name.toLowerCase())) {
							if (index == -1) {
								try {
									result = method.invoke(obj, new Object[]{params});
								} catch (Exception e) {
									//no params need
//									 e.printStackTrace();
									result = method.invoke(obj, null);
								}
							} else {
								Object methodRet = null;
								try {
									methodRet = method.invoke(obj, new Object[]{params});
								} catch (Exception e) {
									//no params need
//									 e.printStackTrace();
									 methodRet = method.invoke(obj, null);
								}
								result = getValueFormObject(params, methodRet,key1);
							}
							
						}
					}
				} catch (Exception e) {
					// e.p
				}

				if (result == null) {
					Field[] fields = obj.getClass().getDeclaredFields();
					for (Field field : fields) {
						field.setAccessible(true);
						String name = field.getName();
						if (key0.equals(name)) {
							if (index == -1) {
								result = field.get(obj);
							} else {
								result = getValueFormObject(params, field.get(obj),
										key1);
							}
						}
					}
				}
			} else {
				if (index==-1) {
					//maby has math~ 
					return key;
				} else {
					//has . but obj is null,can not get evething
					throw new Exception("obj is null,when try get value from:" + key);
				}
			}

			if (result == null) {
				result = key;
			}
		} else {
			throw new Exception("key is null");
		}
		
		System.out.println("getValueFormObject:result=" + result);
		return result;
	}

	/**
	 * 输入 {ddd.ddfd.dfdsa}.name 输入 {ddd.{type.name}.dfdsa}.name 输出 }的索引 输出 -1不匹配
	 * 
	 * @param str
	 * @return
	 */
	public static int findVarLastIndex(String str,char startChar,char endChar) {
		int leftCount = 0;
		boolean isStarted = false;
//		char[] chars = str.toCharArray();
		int count = str.length();
		for (int i = 0; i < count; i++) {
			char chr = str.charAt(i);
			if (chr == startChar) {
				leftCount++;
				isStarted = true;
			} else if (chr == endChar) {
				leftCount--;
			}

			if (leftCount == 0 && isStarted) {
				return i;
			}
		}
		return -1;
	}
}
