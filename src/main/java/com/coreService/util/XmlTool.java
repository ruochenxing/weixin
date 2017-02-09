package com.coreService.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlTool {
	
	private static XStream xstream = new XStream(new DomDriver());
	/**
	 * xml转为对象
	 * @param xml
	 * @return
	 */
	public static Object parseXmlToObj(String xml, @SuppressWarnings("rawtypes") Class type){
		xstream.alias("xml", type);
		return xstream.fromXML(xml);
	}
	
	/**
	 * 对象转为xml
	 * @param obj
	 * @return
	 */
	public static String parseObjToXml(Object obj){
		xstream.alias("xml", obj.getClass());
		return xstream.toXML(obj);
	}
}
