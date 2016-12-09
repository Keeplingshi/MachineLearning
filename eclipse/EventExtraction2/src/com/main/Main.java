package com.main;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.model.EventEnum;
import com.test.CreateResult;
import com.test.TestTriggerScoreCalculate;
import com.tools.FileUtils;
import com.train.EventParser;

public class Main {

	public static void main(String[] args) {
		
//		String path=System.getProperty("user.dir")+"/data/cec/CEC_Train_Corpous/";
//		//System.out.println(EventParser.parseEvent(path));
//		
//		Map<EventEnum, Map<String, Integer>> triggerMap=EventParser.parseEvent(path);
//        for(Map.Entry<EventEnum,  Map<String, Integer>> entity : triggerMap.entrySet()){
//            Map<String, Integer> wordCountMap = entity.getValue();
//            
//            EventEnum type = entity.getKey();
//            System.out.println(type.name());
//            StringBuffer buffer=new StringBuffer("111");
//            for(Map.Entry<String, Integer> triggerEntity : wordCountMap.entrySet()){
//                String word = triggerEntity.getKey();
//                
//                System.out.println(word);
////                word2typeMap.put(word, type);
////                Set<String> wordsSet = type2wordsMap.get(type);
////                if(null == wordsSet){
////                    wordsSet = Sets.newHashSet();
////                }
////                wordsSet.add(word);
////
////                //找到相应的触发词====同义词词林
////                String num = word2NumMap.get(word);
////
////                List<String> sampleList = sampleWordNum2WordsMap.get(num);
////                if(null == sampleList){
////                    type2wordsMap.put(type, wordsSet);
////                    continue;
////                }
////
////                for(String str : sampleList){
////                    wordsSet.add(str+":"+word);
////                    word2typeMap.put(str, type);
////                }
////
////                type2wordsMap.put(type, wordsSet);
//            }
//            
//            String txtPath=path+"/trigger/"+type.name()+".txt";
//            FileUtils.writeTxtFile(buffer, txtPath);
//        }
		
        new TestTriggerScoreCalculate();
        
//        String test_seq_path=System.getProperty("user.dir")+"/data/cec/CEC_Test_Corpous/cecTestResult.txt";
//        File file = new File(test_seq_path);
//        TestTriggerScoreCalculate.parseTest(file);
//        CreateResult.resultFactory(file);
		
	}
}
