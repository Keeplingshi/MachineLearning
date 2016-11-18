package com.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

	private static String REG_XMLNODE="<.*?>";
	
	/**
	 * 将CEC语料中的文本提取出来
	 * @param path
	 * @param txtPath
	 */
	public static void cecCorpousExtract(String path,String txtPath)
	{
		File xmlFolder=new File(path);
		FileWriter writer=null;
		
		if(xmlFolder.isDirectory()){
			try {
				writer = new FileWriter(txtPath);
				
				for(File folder:xmlFolder.listFiles())
				{
					if(folder.isDirectory())
					{
						for(File xml:folder.listFiles())
						{
							//读出每一个xml中的文本内容，写入txt中
							String xmlStr=xmlToStr(xml);
							writer.write(xmlStr+"\r\n");
						}
					}
				}
				
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 读取xml中内容，以字符串形式返回
	 * @param xml
	 * @return
	 */
	public static String xmlToStr(File xml)
	{
        BufferedReader reader = null;
        String xmlStr=null;
        try {
        	StringBuffer xmlContent=new StringBuffer();
            reader = new BufferedReader(new FileReader(xml));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	if(tempString.contains("</Title>")||tempString.contains("</ReportTime>"))
            	{
            		continue;
            	}
            	xmlContent.append(tempString);
            }
            
            xmlStr=xmlContent.toString().replaceAll("\r|\n|\t", "");
            //xmlStr=xmlContent.toString().replaceAll(REG_XMLNODE, "").replaceAll("\r|\n|\t", "").replaceAll(" ", "");
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		
        return xmlStr;
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
