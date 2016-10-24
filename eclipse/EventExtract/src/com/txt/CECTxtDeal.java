package com.txt;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * CEC语料处理
 * @author chenbin
 *
 */
public class CECTxtDeal {

	public static void readXml(String xmlPath)
	{
		SAXReader saxReader = new SAXReader();

		try {
			File xmlFile = new File(xmlPath);
			Document document = saxReader.read(xmlFile);
			
			Element root = document.getRootElement();
			//System.out.println(root.getName());
			
			getElement(root,"Denoter");

		} catch (DocumentException e) {
			
		}
	}
	
	/**
	 * 获取固定子节点
	 * @param element
	 * @param elementName
	 * @return
	 */
	public static void getElement(Element element,String elementName)
	{
		if(elementName.equals(element.getName())){
			System.out.println(element.getData()+"\t"+element.attributeValue("type"));
		}else{
			Iterator<?> iterator = element.elementIterator();
			while (iterator.hasNext()) {
				Element child = (Element) iterator.next();
				getElement(child,elementName);
			}
		}
	}
	
}
