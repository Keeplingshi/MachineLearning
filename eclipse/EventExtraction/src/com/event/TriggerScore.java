package com.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.model.EventEnum;

import trash.TriggerScorePre;

/**
 * 触发词分值计算
 * @author chenbin
 *
 */
public class TriggerScore {

	static final Logger LOGGER = LoggerFactory.getLogger(TriggerScorePre.class);
	
	/**
	 * 句子分类
	 */
	public static final String SEPORETOR = "[。,!，?]";
	
	public static Map<String, EventEnum> word2typeMap = TriggerLarger.getWord2typeMap();
	
	/**
	 * 事件句按事件分类，可求出某类事件句总数
	 */
	public static Map<EventEnum,Set<String>> eventSentenceMap=Maps.newHashMap();
	
	/**
	 * 触发词数目
	 */
	public static Map<String, Integer> triggerCounterMap = Maps.newHashMap();
	
	/**
	 * 句子总数
	 */
	public static Integer totalNum=0;
	
	/**
	 * 触发词得分
	 */
	private static Map<EventEnum,Map<String, Double>> scoreMap = Maps.newHashMap();
	
	static {
		
		String resultPath=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
		File file=new File(resultPath);
		countSentence(file);
		
		//计算触发词的score
        Double result;
        for(String word : triggerCounterMap.keySet()){
            result = 0.0;
            Double tfw = 1.0*triggerCounterMap.get(word)/eventSentenceMap.get(word2typeMap.get(word)).size();
            Double idfw = Math.log((double)1.0*totalNum/triggerCounterMap.get(word))/Math.log((double)2);//Math.log(1.0*totalNum/triggerCounterMap.get(word));
            result = tfw *idfw;
            
            Map<String, Double> map=scoreMap.get(word2typeMap.get(word));
            if(map==null){
            	map=new HashMap<>();
            }
            map.put(word, result);
            map=sortByValue(map);
            scoreMap.put(word2typeMap.get(word), map);
        }
        
	}

    /**
     * 计算句子中的信息
     * @param file
     * @return
     */
    public static void countSentence(File file){
        if(null==file){
        	return ;
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while((line=bufferedReader.readLine()) != null){
                line = line.replaceAll("/[a-zA-Z^(//s)]*","");
            	
                Iterable<String> list = Splitter.on(Pattern.compile(SEPORETOR)).trimResults().omitEmptyStrings().split(line);
                for(String str : list){
                	totalNum++;
                	Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(str);
                	for(String token : tokens){
                		if(word2typeMap.containsKey(token)){
                			
                			EventEnum eventEnum=word2typeMap.get(token);
                			Set<String> sentenceSet=eventSentenceMap.get(eventEnum);
                			if(sentenceSet==null){
                				sentenceSet=new HashSet<>();
                			}
                			sentenceSet.add(str);
                			eventSentenceMap.put(eventEnum, sentenceSet);
                			
                			//统计触发词数目
                			increaseMap(triggerCounterMap,token);
                		}
                	}

                }
                
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * map的值递增
     * @param map
     */
    private static <K> void increaseMap(Map<K, Integer> map, K key) {

        if(map.containsKey(key)){
            map.put(key, map.get(key)+1);
        }
        else{
            map.put(key, 1);
        }
    }
	
    /**
     * 根据Value值对Map排序，从大到小
     * @param map
     * @return
     */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				//从大到小排，从小到大则o1，o2换位置
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static Map<EventEnum, Map<String, Double>> getSorceMap() {
		return scoreMap;
	}

	public static void setSorceMap(Map<EventEnum, Map<String, Double>> sorceMap) {
		TriggerScore.scoreMap = sorceMap;
	}
	
	
}
