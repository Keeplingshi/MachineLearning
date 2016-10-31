package com.main;

import java.util.HashMap;
import java.util.List;

import com.txt.CECTxtDeal;
import com.txt.FileUtil;

public class Main {

	public static void main(String[] args) {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
		//获取项目根路径
		String projectRootPath=System.getProperty("user.dir");
		
		String xmlFolderPath=projectRootPath+"/corpous/CEC_Train_Corpous/";
		String denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
		//CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
//		
		HashMap<String, List<String>> denoterMap=FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		
	}


}
