package com.main;

import com.event.CECTxtDeal;
import com.nlpir.NlpirMethod;

public class Main {
	
	public static void main(String[] args) {

		String projectRootPath=System.getProperty("user.dir")+"/corpous/cec/";
////		
		String xmlFolderPath=projectRootPath+"/CEC_Train_Corpous/";
		String cecCorpous=projectRootPath+"/CEC_Train_Corpous/cecCorpous.txt";
		String sResultFilename=projectRootPath+"/CEC_Train_Corpous/cecResult.txt";
		
////		
		CECTxtDeal.cecCorpousExtract(xmlFolderPath,cecCorpous);
		
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
		
		
	}
	
}
