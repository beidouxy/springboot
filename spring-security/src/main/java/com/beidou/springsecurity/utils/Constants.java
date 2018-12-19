
/*
 * YZConstants.java
 *
 * Created Date: 2016年5月24日
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

import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Constants {

	public static String isNull(String str) {
		if (str == null)
			return "";
		return str;
	}

	public static <T> String beanToXml(T t) throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(t.getClass());
		Marshaller m = context.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(t, sw);
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// 是否格式化
		return sw.toString();
	}

	/**
	 * 根据ids: 1,2,3 转成id数组[1,2,3]
	 * @param ids
	 * @return
	 */
	public static List<Long> getIdListByStr(String ids) {
		List<Long> idList = new ArrayList<>();
		if (StringUtils.isEmpty(ids))
			return idList;
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			idList.add(Long.valueOf(idStr));
		}
		return idList;
	}

	/**
	 * 判断运行程序的系统
	 * @return
	 */
	public static boolean isOSWindowns() {
		Properties prop = System.getProperties();

		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().startsWith("win")) {
			return true;
		} else {
			return false;
		}
	}

}
