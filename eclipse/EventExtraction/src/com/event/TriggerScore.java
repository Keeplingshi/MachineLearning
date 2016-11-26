package com.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.model.EventEnum;

public class TriggerScore {

	public static final String SEPORETOR = "[。,!，?]";
	
	public static Map<String, EventEnum> word2typeMap = TriggerLarger.getWord2typeMap();
	
	/**
	 * 事件句按事件分类
	 */
	public static Map<EventEnum,List<String>> eventSentenceMap=Maps.newHashMap();
	
	public static void main(String[] args) {

		String resultPath=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
		File file=new File(resultPath);
		countSentence(file);
		
	}

    /**
     * 获取train的句子，词性对我们没有用
     * @param file
     * @return
     */
    public static List<String> countSentence(File file){
        if(null==file) return null;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while((line=bufferedReader.readLine()) != null){
                line = line.replaceAll("/[a-zA-Z^(//s)]*","");
            	
                Iterable<String> list = Splitter.on(Pattern.compile(SEPORETOR)).trimResults().omitEmptyStrings().split(line);
                for(String str : list){
                	
                	System.out.println(str);
                	
//                    sentenceList.add(str);
//                    Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(str);
//                    for(String token : tokens){
//                        if(word2typeMap.containsKey(token)){
//                            increaseMap(trigger2CounterMap, token); //一个句子可以统计多个相同的词
//                            increaseMap(type2CounterMap, word2typeMap.get(token));
//                        }
//                    }
//
//                    //Set<String> set = Sets.newHashSet(tokens);
//                    for(String token : tokens){
//                        if(word2typeMap.containsKey(token)){
//                            increaseMap(trigger2SentenceCounterMap, token);   //一个句子只能统计一个相同的词
//                        }
//                    }
                }
                
            }

//            sentenceTotalNum = sentenceList.size();
//            LOGGER.info("【触发词触发事件的次数】,{}", trigger2CounterMap);
//            LOGGER.info("【触发词类型的次数】,{}", type2CounterMap);
//            LOGGER.info("【句子个数】,{}", sentenceList.size());
//            LOGGER.info("【触发词触发出现在句子中次数】,{}", trigger2SentenceCounterMap);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}
