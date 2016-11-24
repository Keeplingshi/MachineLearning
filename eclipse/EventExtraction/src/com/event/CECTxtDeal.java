package com.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultText;

import com.model.EventEnum;
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
            
            //xmlStr=xmlContent.toString().replaceAll("\r|\n|\t", "");
            xmlStr=xmlContent.toString().replaceAll(REG_XMLNODE, "").replaceAll("\r|\n|\t", "").replaceAll(" ", "");
            
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
	
	
	public static void readExcel(String originPath,String outPath)
	{
		String[] removeWord={"报道","电","死","伤","死亡","重伤","送往","手术","表示","通知","治疗",
				"发稿","说","介绍","呼吁","了解","统计","前往","赶往","轻伤","讯","受伤","核查","处理","带领",
				"交谈","赶到","指示","组织","救援","亲临","指挥","送医","接受","调查","展开","重视","看望"
				,"做好","称","获悉","消息","参加","召集","闻讯","确定","救治","抢救","送到","到","事故","事"
				,"透露","接警","查看","报警","看到","关闭","宣布","抓获","证实","关闭","制造","报告","出动"
				,"帮助","批示","到达","派出","赶赴","接到","使用","采血","发现","调派","启动","成立","确保"
				,"检查","开展","追究","快讯","清理","排查","救出","调集","增援","稀释","转交","确认","观察了解"
				,"协查","解救","救出","安慰","鼓励","握着","给","决定","扩大","配合","装满","包扎","此次事故"
				,"伤亡","听到","丧生","到场","通报","召开","处置","整治","侦查","奋战","防止","疏散","安置"
				,"调查统计","接报","救","站","观望","善后","回家","抓着","保住","配带","休息","更新","核对"
				,"注意","求助","带离","正常","遇难","报"};
		List<String> list=new ArrayList<String>(0);
		Collections.addAll(list, removeWord);
		
		Workbook originWb=null;
		
		HSSFWorkbook outWorkbook = new HSSFWorkbook();
		HSSFSheet outSheet = outWorkbook.createSheet("event");
		
		try {
			originWb = WorkbookFactory.create(new File(originPath));
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		Sheet originSheet = originWb.getSheetAt(0);
		Cell cell=null;
		
		int i=1;
		HSSFRow outRow = null;
		HSSFCell outCell=null;
		
		for(Row row:originSheet)
		{
			if(row.getRowNum()==0){
				continue;
			}

			outRow = outSheet.createRow(i++);
			
			//序号
			cell=row.getCell(0);
			outCell=outRow.createCell(0);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			
			//句子
			cell=row.getCell(1);
			outCell=outRow.createCell(1);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			//参与者
			cell=row.getCell(2);
			outCell=outRow.createCell(2);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			//时间
			cell=row.getCell(3);
			outCell=outRow.createCell(3);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			//地点
			cell=row.getCell(4);
			outCell=outRow.createCell(4);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			//触发词
			cell=row.getCell(5);
			String triggerWord="";
			outCell=outRow.createCell(5);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
				triggerWord=cell.toString().trim();
			}

			//对象
			cell=row.getCell(6);
			outCell=outRow.createCell(6);
			if(cell!=null){
				outCell.setCellValue(cell.toString());
			}
			
			//事件类型
			cell=row.getCell(7);
			outCell=outRow.createCell(7);
			if(list.contains(triggerWord)){
				outCell.setCellValue("");
			}else{
				if(cell!=null){
					outCell.setCellValue(cell.toString());
				}
			}
			
			
		}
		
		try {
			if(FileUtil.createFile(outPath)){
				OutputStream out = new FileOutputStream(outPath);
				outWorkbook.write(out);
				out.close();
			}
		} catch (IOException e) {
			
		}
		
	}

	/**
	 * 读取触发词
	 * @param triggerWordPath
	 */
	public static Map<EventEnum, List<String>> readTriggerWord(String triggerWordPath) {

		Map<EventEnum, List<String>> triggerMap=new HashMap<EventEnum, List<String>>();
		
		File file=new File(triggerWordPath);
        InputStreamReader read;
		try {
			read = new InputStreamReader(new FileInputStream(file),"UTF-8");
	        BufferedReader bufferedReader = new BufferedReader(read);
	        String lineTxt = null;
	        int i=0;
	        EventEnum eventEnum=null;
	        while((lineTxt = bufferedReader.readLine()) != null){
	            if(i%2==0){
	            	eventEnum=EventEnum.valueOf(lineTxt.trim());
	            }else{
	            	List<String> list=new ArrayList<String>();
	            	String[] strArray=lineTxt.replace("{", "").replace("}", "").split(",");
	            	for(String str:strArray)
	            	{
	            		list.add(str.split("=")[0]);
	            	}
	            	
	            	triggerMap.put(eventEnum, list);
	            }
	        	i++;
	        }
	        read.close();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		return triggerMap;

		
	}
	
}
