package com.txt;

import java.io.File;
import java.util.HashMap;
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

	/**
	 * 将处理的触发词语料写入文档
	 * @param denoterMap 触发词
	 * @param denoterFile 触发词文档
	 */
	public static void cecDenoter(HashMap<String, List<String>> denoterMap,String denoterFile)
	{
		StringBuffer content = new StringBuffer();
		Iterator<String> iterator = denoterMap.keySet().iterator();  
		while (iterator.hasNext()){
			
			String key=(String)iterator.next();
			List<String> list=denoterMap.get(key);
			
		}
		
	}
	
	/**
	 * 获取CEC语料触发词
	 * @param xmlFolderPath
	 * @param denoterTxt
	 */
	public static void getDenoter(String xmlFolderPath,String denoterTxt)
	{
		File xmlFolder=new File(xmlFolderPath);
		StringBuffer content = new StringBuffer();
		if(xmlFolder.isDirectory()){
			//遍历文件夹下所有文件
			for(File file:xmlFolder.listFiles())
			{
				if(file.isDirectory())
				{
					content.append(file.getName()+"\r\n");
					for(File xml:file.listFiles())
					{
						content.append(CECTxtDeal.readXml(xml));
					}
					content.append("\r\n");
					FileUtil.writeTxtFile(content, denoterTxt);
				}
			}
		}
	}
	
	/**
	 * 读取xml文件
	 * @param xmlPath
	 */
	public static StringBuffer readXml(File xmlFile)
	{
		SAXReader saxReader = new SAXReader();
		StringBuffer str=new StringBuffer();
		try {
			Document document = saxReader.read(xmlFile);
			
			Element root = document.getRootElement();
			str=getElement(root,"Denoter",null);
		} catch (DocumentException e) {
			
		}
		return str;
	}
	
	/**
	 * 获取固定子节点
	 * @param element
	 * @param elementName
	 * @return
	 */
	public static StringBuffer getElement(Element element,String elementName,StringBuffer str)
	{
		if(str==null){
			str=new StringBuffer();
		}
		
		if(elementName.equals(element.getName())){
			str.append(element.getData()+"\t");
		}else{
			Iterator<?> iterator = element.elementIterator();
			while (iterator.hasNext()) {
				Element child = (Element) iterator.next();
				getElement(child,elementName,str);
			}
		}
		return str;
	}
	
}
