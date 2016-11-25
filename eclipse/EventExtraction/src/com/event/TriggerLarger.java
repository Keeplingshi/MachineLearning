package com.event;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.model.EventEnum;

/**
 * 触发词扩展
 */
public class TriggerLarger {

	/**
	 * 扩展触发词表，事件类型，事件触发词
	 */
    private static Map<EventEnum, Set<String>> type2wordsMap = Maps.newHashMap();
    
    /**
     * 扩展触发词表，触发词对应的类型
     */
    private static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    static{
    	
        //同义词
    	Map<String, List<String>> sampleWordNum2WordsMap = SampWordParse.label2wordMap;
    	Map<String, String> word2NumMap = SampWordParse.word2labelMap;
        
        //获取统计的触发词
		String triggerWordPath=System.getProperty("user.dir") + "/corpous/cec/CEC_Train_Corpous/triggerWord.txt";
		Map<EventEnum, List<String>> triggerMap=CECTxtDeal.readTriggerWord(triggerWordPath);

        for(Map.Entry<EventEnum,  List<String>> entity : triggerMap.entrySet()){
        	List<String> wordList = entity.getValue();
            EventEnum type = entity.getKey();
            
            for(String word:wordList)
            {
            	word2typeMap.put(word, type);
            	Set<String> wordsSet = type2wordsMap.get(type);
                if(null == wordsSet){
                    wordsSet = Sets.newHashSet();
                }
                wordsSet.add(word);
            	
                //找到相应的触发词====同义词词林
                String num = word2NumMap.get(word);

                //如果同义词不为空，将同义词加入到Map中
                List<String> sampleList = sampleWordNum2WordsMap.get(num);
                if(sampleList!=null){
                    for(String str : sampleList){
                    	wordsSet.add(str);
                        word2typeMap.put(str, type);
                    }
                }

                type2wordsMap.put(type, wordsSet);
            }
        }
		
    }

    /**
     * 扩展触发词表，事件类型，事件触发词
     * @return 扩展触发词表
     */
	public static Map<EventEnum, Set<String>> getType2wordsMap() {
		return type2wordsMap;
	}

	public static void setType2wordsMap(Map<EventEnum, Set<String>> type2wordsMap) {
		TriggerLarger.type2wordsMap = type2wordsMap;
	}

	/**
	 * 扩展触发词表，触发词对应的类型
	 * @return 扩展触发词表
	 */
	public static Map<String, EventEnum> getWord2typeMap() {
		return word2typeMap;
	}

	public static void setWord2typeMap(Map<String, EventEnum> word2typeMap) {
		TriggerLarger.word2typeMap = word2typeMap;
	}

}
