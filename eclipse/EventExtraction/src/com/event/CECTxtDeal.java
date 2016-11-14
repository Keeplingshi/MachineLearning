package com.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultText;

import com.util.FileUtil;

public class CECTxtDeal {

	public static void test(String path)
	{
		File xmlFolder=new File(path);
		
		if(xmlFolder.isDirectory()){
			for(File folder:xmlFolder.listFiles())
			{
				if(folder.isDirectory())
				{
					for(File xml:folder.listFiles())
					{
						cecXmlToExcel(xml);
					}
				}
			}
		}
		
	}
	
	public static StringBuffer cecXmlToExcel(File xml)
	{
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(xml);
			
			Element root = document.getRootElement();
			Element content_element=root.element("Content");
			Iterator<?> paragraph_iterator=content_element.elementIterator("Paragraph");
			
			while(paragraph_iterator.hasNext())
			{
				Element paragraph_element = (Element) paragraph_iterator.next();
				Iterator<?> sentence_iterator=paragraph_element.elementIterator("Sentence");
				while(sentence_iterator.hasNext())
				{
					Element sentence_element = (Element) sentence_iterator.next();
					Iterator<?> event_iterator=sentence_element.elementIterator("Event");
					while(event_iterator.hasNext())
					{
						Element event_element = (Element) event_iterator.next();
						List<?> list=event_element.content();
						
						StringBuffer sentenceBuffer=new StringBuffer();
						for(Object object:list)
						{
							try {
								Element element=(Element)object;
								String elementStr=element.getData().toString();

								if("Participant".equals(element.getName())){
									
								}else if("Time".equals(element.getName())){
									
								}else if("Location".equals(element.getName())){
									
								}else if("Denoter".equals(element.getName())){
									
								}else if("Object".equals(element.getName())){
									
								}
								
								sentenceBuffer.append(elementStr);
							} catch (Exception e) {
								DefaultText defaultText=(DefaultText)object;
								sentenceBuffer.append(defaultText.getText().replaceAll("\r|\n|\t", ""));
							}
						}

					}
					
				}
				
			}
			
		} catch (DocumentException e) {
			
		}
		return null;
	}
	
	/**
	 * 
	 * @param path
	 */
	public static void cecXmlDeal(String path)
	{
		File xmlFolder=new File(path);
		
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("event");

		HSSFRow row = sheet.createRow(0);
		HSSFCell[] cells=new HSSFCell[8];
		String[] headers={"序号","事件句","参与者","时间","地点","触发词","对象","事件类型"};
		for(int i=0;i<8;i++)
		{
			String header=headers[i];
			cells[i]=row.createCell(i);
			cells[i].setCellValue(header);
		}
		
		if(xmlFolder.isDirectory()){
			for(File folder:xmlFolder.listFiles())
			{
				if(folder.isDirectory())
				{
					String folderName=folder.getName();
					for(File xml:folder.listFiles())
					{
						xmlWriteToExcel(xml,sheet,folderName);
					}
				}
			}
		}
		
		try {
			String excelPath=path+File.separator+"event.xls";
			if(FileUtil.createFile(excelPath)){
				OutputStream out = new FileOutputStream(excelPath);
				workbook.write(out);
				out.close();
			}
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * @param xml
	 * @param sheet
	 */
	public static void xmlWriteToExcel(File xml,HSSFSheet sheet,String eventType)
	{
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(xml);
			
			Element root = document.getRootElement();
			Element content_element=root.element("Content");
			Iterator<?> paragraph_iterator=content_element.elementIterator("Paragraph");
			
			while(paragraph_iterator.hasNext())
			{
				Element paragraph_element = (Element) paragraph_iterator.next();
				Iterator<?> sentence_iterator=paragraph_element.elementIterator("Sentence");
				while(sentence_iterator.hasNext())
				{
					Element sentence_element = (Element) sentence_iterator.next();
					Iterator<?> event_iterator=sentence_element.elementIterator("Event");
					while(event_iterator.hasNext())
					{
						Element event_element = (Element) event_iterator.next();
						List<?> list=event_element.content();
					
						int lastRowNum=sheet.getLastRowNum()+1;
						HSSFRow row = sheet.createRow(lastRowNum);

						HSSFCell cell=row.createCell(0);
						cell.setCellValue(lastRowNum);
						
						cell=row.createCell(7);
						cell.setCellValue(eventType);
						
						StringBuffer sentenceBuffer=new StringBuffer();
						for(Object object:list)
						{
							cell=null;
							try {
								Element element=(Element)object;
								String elementStr=element.getData().toString();

								if("Participant".equals(element.getName())){
									cell=row.createCell(2);
								}else if("Time".equals(element.getName())){
									cell=row.createCell(3);
								}else if("Location".equals(element.getName())){
									cell=row.createCell(4);
								}else if("Denoter".equals(element.getName())){
									cell=row.createCell(5);
								}else if("Object".equals(element.getName())){
									cell=row.createCell(6);
								}
								if(cell!=null)
								{
									cell.setCellValue(elementStr);
								}
								
								sentenceBuffer.append(elementStr);
							} catch (Exception e) {
								DefaultText defaultText=(DefaultText)object;
								sentenceBuffer.append(defaultText.getText().replaceAll("\r|\n|\t", ""));
							}
						}
						cell=row.createCell(1);
						cell.setCellValue(sentenceBuffer.toString());
					}
					
				}
				
			}
			
		} catch (DocumentException e) {
			
		}
	}
	
	
	/**
	 * @param xmlFolderPath
	 * @param denoterTxt
	 */
	public static void getDenoter(String xmlFolderPath,String denoterTxt)
	{
		File xmlFolder=new File(xmlFolderPath);
		StringBuffer content = new StringBuffer();
		if(xmlFolder.isDirectory()){

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
