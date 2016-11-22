package com.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.event.TestTriggerScoreCalculate;
import com.event.TriggerLarger;
import com.event.TriggerScorePre;
import com.nlpir.NlpirMethod;
import com.util.FileUtil;

public class Main {
	
	public static void main(String[] args) {

//		String excelPath="C:\\Users\\chenbin\\Desktop\\eventOutOut.xls";
//		String outPath="C:\\Users\\chenbin\\Desktop\\eventOut.xls";
//		CECTxtDeal.readExcel(excelPath,outPath);
		
		
//		String projectRootPath=System.getProperty("user.dir")+"/corpous/cec/";
//////		
//		String xmlFolderPath=projectRootPath+"/CEC_Train_Corpous/";
//		String cecCorpous=projectRootPath+"/CEC_Train_Corpous/cecTestCorpous.txt";
//		String sResultFilename=projectRootPath+"/CEC_Train_Corpous/cecTestResult.txt";
//		
//		CECTxtDeal.cecXmlDeal(xmlFolderPath);
////		
		//CECTxtDeal.cecCorpousExtract(xmlFolderPath,cecCorpous);
		
//		String content="曾经有一份真挚的感情摆在我的面前我没有珍惜，等我失去的时候才追悔莫及，人间最痛苦的事莫过于此，你的剑在我的咽喉上刺下去吧，不用在犹豫了！如果上天能给我一次再来一次的机会，我会对哪个女孩说三个字：我爱你，如果非要在这份爱上加一个期限，我希望是一万年！";
//		String result=NlpirMethod.NLPIR_ParagraphProcess(content,1);
//		System.out.println(result);
		
		
		//NlpirMethod.NLPIR_FileProcess(cecCorpous, sResultFilename, 1);
		
//		String denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
//		CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
	
//		HashMap<String, List<String>> denoterMap=FileUtil.orderTxtFile(denoterTxt, "UTF-8");
//		
//		Iterator<String> iterator = denoterMap.keySet().iterator();  
//		while (iterator.hasNext()){  
//			String key=(String)iterator.next();
//			System.out.println(key);
//			System.out.println(denoterMap.get(key));
//		}
		
		
		
		//new TriggerLarger();
		//System.out.println(TriggerLarger.word2typeMap);
		
//        String train_seq_path=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
//        //System.out.println("3\t"+train_seq_path);
//        File file = new File(train_seq_path);
//        TriggerScorePre.splitSentece(file);

        String test_seq_path=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
        File file = new File(test_seq_path);
        TestTriggerScoreCalculate.parseTest(file);
		
	}
	

}
