package com.main;

import com.txt.CECTxtDeal;
import com.txt.WeiBoTxtDeal;

public class Main {

	public static void main(String[] args) {

		//WeiBoTxtDeal.weiboTxtDeal("E:/机器学习/data/weibo/weibo.txt","E:/机器学习/data/weibo/weiboResult.txt");
		
		String xmlPath="E:/temp/CEC_Train_Corpous/17岁少女殒命搅拌车下.xml";
		CECTxtDeal.readXml(xmlPath);
		
	}


}
