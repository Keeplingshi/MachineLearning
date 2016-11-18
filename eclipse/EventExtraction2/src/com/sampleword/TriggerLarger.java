package com.sampleword;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.model.EventEnum;
import com.train.EventParser;

/**
 * 触发词扩展
 */
public class TriggerLarger {

    public static Map<EventEnum, Set<String>> type2wordsMap = Maps.newHashMap();
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    public static Map<EventEnum, Map<String, Integer>> originType2WordAndCountMap;
    public static Map<String, List<String>>  sampleWordNum2WordsMap;
    public static Map<String, String> word2NumMap;

    static{
    	
        //同义词
        sampleWordNum2WordsMap = SampWordParse.label2wordMap;
        word2NumMap = SampWordParse.word2labelMap;
        
        String path=System.getProperty("user.dir")+"/data/cec/CEC_Train_Corpous/";
        originType2WordAndCountMap = EventParser.parseEvent(path);
        

        for(Map.Entry<EventEnum,  Map<String, Integer>> entity : originType2WordAndCountMap.entrySet()){
            Map<String, Integer> wordCountMap = entity.getValue();
            EventEnum type = entity.getKey();
            for(Map.Entry<String, Integer> triggerEntity : wordCountMap.entrySet()){
                String word = triggerEntity.getKey();
                word2typeMap.put(word, type);
                Set<String> wordsSet = type2wordsMap.get(type);
                if(null == wordsSet){
                    wordsSet = Sets.newHashSet();
                }
                wordsSet.add(word);

                //找到相应的触发词====同义词词林
                String num = word2NumMap.get(word);

                List<String> sampleList = sampleWordNum2WordsMap.get(num);
                if(null == sampleList){
                    type2wordsMap.put(type, wordsSet);
                    continue;
                }

                for(String str : sampleList){
                    wordsSet.add(str+":"+word);
                    word2typeMap.put(str, type);
                }

                type2wordsMap.put(type, wordsSet);
            }
        }
    }

    public static Map<EventEnum, Set<String>> getType2wordsMap() {
        return type2wordsMap;
    }

    public static void setType2wordsMap(Map<EventEnum, Set<String>> type2wordsMap) {
        TriggerLarger.type2wordsMap = type2wordsMap;
    }

    public static Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }

    public static void setWord2typeMap(Map<String, EventEnum> word2typeMap) {
        TriggerLarger.word2typeMap = word2typeMap;
    }
}
