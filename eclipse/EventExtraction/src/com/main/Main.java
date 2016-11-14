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
//		
		CECTxtDeal.cecXmlDeal(xmlFolderPath);
		
		
		
		
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
		
		
//        String a = "<doc>abc<title>3232</title></doc><doc>ֻҪ����</doc>";  
//        List list = getContext(a);  
//        System.out.println(list);  
	}
	
//	public static List getContext(String html) {  
//        List resultList = new ArrayList();  
//        Pattern p = Pattern.compile(">([\\S]+)</");//������ʽ commend by danielinbiti    >([\\S]+)</
//        Matcher m = p.matcher(html );//  
//        while (m.find()) {  
//            resultList.add(m.group(1));//  
//        }  
//        return resultList;  
//    }  
//	
}
