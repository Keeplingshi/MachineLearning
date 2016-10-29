package com.main;

import com.txt.CECTxtDeal;

public class Main {

	public static void main(String[] args) {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
		//获取项目根路径
		String projectRootPath=System.getProperty("user.dir");
		
		String xmlFolderPath=projectRootPath+"/corpous/CEC_Train_Corpous/";
		String denoterTxt=projectRootPath+"/corpous/CEC_Train_Corpous/denoter.txt";
		CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
//		
//		FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		
	}


}
