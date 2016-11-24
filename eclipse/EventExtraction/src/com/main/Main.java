package com.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

import com.event.CECTxtDeal;
import com.event.EventParser;
import com.event.TestTriggerScoreCalculate;
import com.event.TriggerLarger;
import com.event.TriggerScorePre;
import com.model.EventEnum;
import com.nlpir.NlpirMethod;
import com.util.FileUtil;

public class Main {

	public static void main(String[] args) {

		// String excelPath="C:\\Users\\chenbin\\Desktop\\event.xls";
		// String outPath="C:\\Users\\chenbin\\Desktop\\eventOut.xls";
		String triggerWordPath=System.getProperty("user.dir") + "/corpous/cec/CEC_Train_Corpous/triggerWord.txt";
		Map<EventEnum, List<String>> triggerMap=CECTxtDeal.readTriggerWord(triggerWordPath);
		System.out.println(triggerMap);
		
//		String path = System.getProperty("user.dir") + "/corpous/cec/CEC_Train_Corpous/eventOut.xls";
//		Map<EventEnum, Map<String, Integer>> originType2WordAndCountMap = EventParser.parseEvent(path);
//		Iterator<EventEnum> iterator = originType2WordAndCountMap.keySet().iterator();
//		while (iterator.hasNext()) {
//
//			EventEnum key = iterator.next();
//			System.out.println(key);
//			Map<String, Integer> value = originType2WordAndCountMap.get(key);
//			//System.out.println(value);
//			
//			value=sortByValue(value);
//			System.out.println(value);
//
//			// String key=(String)iterator.next();
//			// System.out.println(key);
//			// System.out.println(denoterMap.get(key));
//		}

		// System.out.println(originType2WordAndCountMap);

		// String
		// projectRootPath=System.getProperty("user.dir")+"/corpous/cec/";
		//////
		// String xmlFolderPath=projectRootPath+"/CEC_Train_Corpous/";
		// String
		// cecCorpous=projectRootPath+"/CEC_Train_Corpous/cecTestCorpous.txt";
		// String
		// sResultFilename=projectRootPath+"/CEC_Train_Corpous/cecTestResult.txt";
		//
		// CECTxtDeal.cecXmlDeal(xmlFolderPath);
		////
		// CECTxtDeal.cecCorpousExtract(xmlFolderPath,cecCorpous);

		
//		String content="8月19日晚，315国道德令哈段(480KM+700M)发生一起交通事故";
//		String result=NlpirMethod.NLPIR_ParagraphProcess(content,1);
//		System.out.println(result);

		// NlpirMethod.NLPIR_FileProcess(cecCorpous, sResultFilename, 1);

		// String
		// denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
		// CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);

		// HashMap<String, List<String>>
		// denoterMap=FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		//
		// Iterator<String> iterator = denoterMap.keySet().iterator();
		// while (iterator.hasNext()){
		// String key=(String)iterator.next();
		// System.out.println(key);
		// System.out.println(denoterMap.get(key));
		// }

		// new TriggerLarger();
		// System.out.println(TriggerLarger.word2typeMap);

		// String
		// train_seq_path=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
		// //System.out.println("3\t"+train_seq_path);
		// File file = new File(train_seq_path);
		// TriggerScorePre.splitSentece(file);

		// String
		// test_seq_path=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
		// File file = new File(test_seq_path);
		// TestTriggerScoreCalculate.parseTest(file);

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
