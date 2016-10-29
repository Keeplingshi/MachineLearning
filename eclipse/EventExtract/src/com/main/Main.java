package com.main;

import com.txt.CECTxtDeal;
import com.txt.FileUtil;

public class Main {

	public static void main(String[] args) {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
		String xmlFolderPath="E:/temp/CEC_Train_Corpous/";
		String denoterTxt="E:/temp/CEC_Train_Corpous/denoter.txt";
		CECTxtDeal.getDenoter(xmlFolderPath, denoterTxt);
		
		FileUtil.orderTxtFile(denoterTxt, "UTF-8");
		
	}


}
