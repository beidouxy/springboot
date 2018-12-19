/*
 * MD5Utils.java
 *
 * Created Date: 2016年6月21日
 *				
 * Copyright (c)  Centling Technologies Co., Ltd.
 *
 * This software is the confidential and proprietary information of
 *  Centling Technologies Co., Ltd. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * Centling Technologies Co., Ltd.
 */

package com.beidou.springsecurity.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author ted.ma
 * @version  <br>
 * <p>类的描述</p>
 */

public class MD5Utils {

		/**
		 * @param str
		 * @return
		 * @Date: 2013-9-6  
		 * @Author: lulei  
		 * @Description:  32位小写MD5
		 */
		public static String parseStrToMd5L32(String str){
			String reStr = null;
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] bytes = md5.digest(str.getBytes());
				StringBuffer stringBuffer = new StringBuffer();
				for (byte b : bytes){
					int bt = b&0xff;
					if (bt < 16){
						stringBuffer.append(0);
					} 
					stringBuffer.append(Integer.toHexString(bt));
				}
				reStr = stringBuffer.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return reStr;
		}
		
		/**
		 * @param str
		 * @return
		 * @Date: 2013-9-6  
		 * @Author: lulei  
		 * @Description: 32位大写MD5
		 */
		public static String parseStrToMd5U32(String str){
			String reStr = parseStrToMd5L32(str);
			if (reStr != null){
				reStr = reStr.toUpperCase();
			}
			return reStr;
		}
		
		/**
		 * @param str
		 * @return
		 * @Date: 2013-9-6  
		 * @Author: lulei  
		 * @Description: 16位小写MD5
		 */
		public static String parseStrToMd5U16(String str){
			String reStr = parseStrToMd5L32(str);
			if (reStr != null){
				reStr = reStr.toUpperCase().substring(8, 24);
			}
			return reStr;
		}
		
		/**
		 * @param str
		 * @return
		 * @Date: 2013-9-6  
		 * @Author: lulei  
		 * @Description: 16位大写MD5
		 */
		public static String parseStrToMd5L16(String str){
			String reStr = parseStrToMd5L32(str);
			if (reStr != null){
				reStr = reStr.substring(8, 24);
			}
			return reStr;
		}

}
