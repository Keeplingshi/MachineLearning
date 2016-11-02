package com.main;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.event.RunDetection;
import com.event.label.LabelItem;
import com.txt.CECTxtDeal;
import com.txt.FileUtil;

public class Main {

	public static void main(String[] args) throws SQLException, ParseException {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
		//获取项目根路径
		String projectRootPath=System.getProperty("user.dir");
		
		String xmlFolderPath=projectRootPath+"/corpous/CEC_Train_Corpous/";
		String denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
		CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
	
		HashMap<String, List<String>> denoterMap=FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		
		Iterator<String> iterator = denoterMap.keySet().iterator();  
		while (iterator.hasNext()){  
			String key=(String)iterator.next();
			System.out.println(key);
			System.out.println(denoterMap.get(key));
		}
		
		//f1();
	}

	public static void f1() throws SQLException, ParseException
	{
		RunDetection runDetection=new RunDetection();
		
		runDetection.LoadTemplateController("E:/Github/MachineLearning/eclipse/Crawler_EventDetection/src/cue.csv");
		
		//String eventStr = "孟建柱于2016年4月在北京会见奥巴马。";
		String eventStr = "今天天气不错。";
		
		LabelItem extractResult = runDetection.GetEventInforfromText(eventStr);
		extractResult.Print();
	}
	
}
