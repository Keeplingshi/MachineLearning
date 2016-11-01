package com.main;

import java.sql.SQLException;
import java.text.ParseException;
import com.event.RunDetection;
import com.event.label.LabelItem;

public class Main {

	public static void main(String[] args) throws SQLException, ParseException {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
//		//获取项目根路径
//		String projectRootPath=System.getProperty("user.dir");
//		
//		String xmlFolderPath=projectRootPath+"/corpous/CEC_Train_Corpous/";
//		String denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
//		//CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
////		
//		HashMap<String, List<String>> denoterMap=FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		
		RunDetection runDetection=new RunDetection();
		
		runDetection.LoadTemplateController("E:/Github/MachineLearning/eclipse/Crawler_EventDetection/src/cue.csv");
		
		String eventStr = "孟建柱于2016年4月在北京会见奥巴马。";
		
		LabelItem extractResult = runDetection.GetEventInforfromText(eventStr);
		extractResult.Print();
		
	}


}
