//package com.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.poi.hwpf.HWPFDocument;
//import org.apache.poi.hwpf.usermodel.Table;
//import org.apache.poi.hwpf.usermodel.TableCell;
//import org.apache.poi.hwpf.usermodel.TableIterator;
//import org.apache.poi.hwpf.usermodel.TableRow;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFTable;
//import org.apache.poi.xwpf.usermodel.XWPFTableCell;
//import org.apache.poi.xwpf.usermodel.XWPFTableRow;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.dom4j.io.OutputFormat;
//import org.dom4j.io.SAXReader;
//import org.dom4j.io.XMLWriter;
//
///**
// * 瀵煎叆word妯℃澘
// * @author chen
// *
// */
//public class WordTemplet {
//	
//	private static final String templetFlag="read";
//	public static final String wordPropertiesXmlName="wordProperties.xml";
//
//	/**
//	 * 妯℃澘淇℃伅淇濆瓨
//	 * @param wordPath word妯℃澘璺緞
//	 * @param xmlSavePath xml鐨勪繚瀛樿矾寰�
//	 * @throws IOException 
//	 */
//	@SuppressWarnings("resource")
//	public static boolean templetXmlSave(String wordPath,String xmlSavePath) throws IOException {
//
//		boolean isSuccess=true;
//		
//		//璇诲彇word鍐呭锛屽啓鍏ml涓�
//		/** 1. 璇诲彇WORD琛ㄦ牸鍐呭 */
//		File file=new File(wordPath);
//		FileInputStream fis=new FileInputStream(file);
//		
//		Document doc = DocumentHelper.createDocument();
//		Element root = doc.addElement("root");
//		
//		//濡傛灉鏄痙oc鏍煎紡鐨剋ord鏂囦欢
//		if("doc".equals(FileUtil.getExtension(file.getName())))
//		{
//			HWPFDocument hwpfDocument = new HWPFDocument(fis);
//
//			// 閬嶅巻range鑼冨洿鍐呯殑table銆�
//			TableIterator tableIter = new TableIterator(hwpfDocument.getRange());
//			Table table;
//			TableRow row;
//			TableCell cell;
//			
//			while (tableIter.hasNext()) {
//				Element tableElement = root.addElement("table");
//				int index=0;
//				// 鑾峰彇褰撳墠鐨勮〃鏍煎璞�
//				table = tableIter.next();
//				int rowNum = table.numRows(); // 鑾峰彇琛ㄦ牸鏈夊灏戣
//				for (int j = 0; j < rowNum; j++) {
//					// 鑾峰彇姣忎竴琛岃〃鏍�
//					row = table.getRow(j);
//					// 鑾峰彇璇ヨ琛ㄦ牸涓殑琛ㄦ牸鍗曞厓
//					int cellNum = row.numCells();
//					//鑾峰彇姣忎竴鏍煎唴瀹�
//					for (int k = 0; k < cellNum; k++) 
//					{
//						cell = row.getCell(k);
//						//濡傛灉闇�璁板綍
//						if(templetFlag.equals(cell.text().trim()))
//						{
//							Element tdElement = tableElement.addElement("td");
//							tdElement.setText(String.valueOf(index));
//						}
//						index++;
//					}
//				}
//			}
//			//鍏抽棴鏂囦欢娴�
//			fis.close();
//		}else if("docx".equals(FileUtil.getExtension(file.getName()))){
//			//濡傛灉鏄痙ocx鏂囦欢
//			XWPFDocument xwpfDocument = new XWPFDocument(fis);
//			
//			// 鑾峰彇鏂囨。涓墍鏈夌殑琛ㄦ牸
//			List<XWPFTable> tables = xwpfDocument.getTables();
//			List<XWPFTableRow> rows;
//			List<XWPFTableCell> cells;
//			
//			for (XWPFTable table : tables) {
//				Element tableElement = root.addElement("table");
//				int index=0;
//				// 鑾峰彇琛ㄦ牸瀵瑰簲鐨勮
//				rows = table.getRows();
//				for (XWPFTableRow row : rows) 
//				{
//					//鑾峰彇琛屽搴旂殑鍗曞厓鏍�
//					cells = row.getTableCells();
//					for (XWPFTableCell cell : cells) 
//					{
//						//濡傛灉闇�璁板綍
//						if(templetFlag.equals(cell.getText().trim()))
//						{
//							Element tdElement = tableElement.addElement("td");
//							tdElement.setText(String.valueOf(index));
//						}
//						index++;
//					}
//				}
//			}
//			//鍏抽棴鏂囦欢娴�
//			fis.close();
//		}else{
//			isSuccess=false;
//		}
//		
//		//淇濆瓨word琛ㄦ牸淇℃伅
//		if(isSuccess){
//			isSuccess=saveDocumentToFile(doc, xmlSavePath, wordPropertiesXmlName);
//		}
//
//		return isSuccess;
//	}
//
//	/**
//	 * 淇濆瓨xml
//	 * @param document
//	 * @param xmlFilePath 璺緞
//	 * @param xmlFileName 淇濆瓨鏂囦欢鍚�
//	 */
//	private static boolean saveDocumentToFile(Document document,
//			String xmlFilePath, String xmlFileName) {
//		
//		if (document == null || xmlFilePath == null || "".equals(xmlFileName)) {
//			return false;
//		}
//
//		boolean isSuccess=true;
//		
//		File file = new File(xmlFilePath);
//		// 鍒ゆ柇璺緞鏄惁瀛樺湪锛屼笉瀛樺湪鍒涘缓
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		// 淇濆瓨鏂囦欢
//		OutputFormat format = null;
//		format = OutputFormat.createPrettyPrint();
//		format.setEncoding("UTF-8");
//
//		try {
//			XMLWriter xmlWriter = new XMLWriter(
//					new FileOutputStream(xmlFilePath + xmlFileName), format);
//			xmlWriter.write(document);
//			xmlWriter.close();
//		} catch (IOException e) {
//			isSuccess=false;
//		}
//
//		return isSuccess;
//	}
//
//	/**
//	 * 璇诲彇xml鏂囦欢鍐呭
//	 * 
//	 * @param xmlPath
//	 * @return
//	 */
//	public static HashMap<Integer, List<String>> readTempletXML(String xmlPath) {
//
//		boolean isSuccess = true;
//		// 瀛樻斁xml鏂囦欢鍐呭
//		HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();
//
//		SAXReader saxReader = new SAXReader();// 鑾峰彇璇诲彇xml鐨勫璞°�
//
//		try {
//			File xmlFile = new File(xmlPath);
//			Document document = saxReader.read(xmlFile);
//			// 鍚戝鍙栨暟鎹紝鑾峰彇xml鐨勬牴鑺傜偣銆�
//			Element root = document.getRootElement();
//			// 浠庢牴鑺傜偣涓嬩緷娆￠亶鍘嗭紝鑾峰彇鏍硅妭鐐逛笅鎵�湁瀛愯妭鐐�
//			Iterator<?> iterator = root.elementIterator();
//
//			// 閬嶅巻瀛愯妭鐐�
//			int index = 0;
//			while (iterator.hasNext()) {
//
//				// 鑾峰彇table鑺傜偣
//				Element table_root = (Element) iterator.next();
//				// 閬嶅巻table鑺傜偣瀛愬厓绱�
//				Iterator<?> td_root = table_root.elementIterator();
//				List<String> list = new ArrayList<>();
//				while (td_root.hasNext()) {
//					Element td = (Element) td_root.next();
//					list.add(td.getData().toString());
//				}
//				// 鏀惧叆map涓�
//				map.put(index, list);
//				index++;
//			}
//
//		} catch (DocumentException e) {
//			isSuccess = false;
//		}
//
//		return isSuccess ? map : null;
//	}
//
//}
