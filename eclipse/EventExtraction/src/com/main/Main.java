package com.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.event.CECTxtDeal;

public class Main {
	
	public static void main(String[] args) {

		String projectRootPath=System.getProperty("user.dir");
//		
		String xmlFolderPath=projectRootPath+"/corpous/CEC_Train_Corpous/";
		String cecCorpous=projectRootPath+"/corpous/CEC_Train_Corpous/cecCorpous.txt";
//		
		CECTxtDeal.cecCorpousExtract(xmlFolderPath,cecCorpous);
		
		
		
		
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
		
		
//        String a = "<doc>abc<title>3232</title>156135</doc><doc>中国</doc>";  
//        getContext(a);  
        //System.out.println(list);  
	}
	
	public static void getContext(String str) {

        str=str.replaceAll("<.*?>", "");
        System.out.println(str);
//        while(m.find())
//        {
//            String tmp = m.group();
//    		System.out.println(tmp);
//        }
        
//        List resultList = new ArrayList();  
//        Pattern p = Pattern.compile("<[^>]+>");
//        Matcher m = p.matcher(html );//  
//        while (m.find()) {  
//            resultList.add(m.group(1));//  
//        }  
//        return resultList;  
    }  
//	
}
