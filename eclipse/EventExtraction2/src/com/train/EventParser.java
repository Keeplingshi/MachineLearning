package com.train;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.lineprocessor.EventProcessor;
import com.model.EventEnum;

/**
 * 事件处理过程
 */
public class EventParser {

    private static String reg_pattern = "<Denoter.*?>([^</]+)</Denoter>";
    static Pattern pattern = Pattern.compile(reg_pattern);
	
//    /**
//     * 根据训练语料获取触发词（事件）
//     * @param file
//     * @return
//     */
//    public static Map<EventEnum, Map<String, Integer>> parseEvent(File file){
//
//        if(null==file || !file.exists()){
//        	return null;
//        }
//
//        LineProcessor<Map<EventEnum, Map<String, Integer>>> linePorcessor = new EventProcessor();
//        try {
//            Files.readLines(file, Charsets.UTF_8, linePorcessor);
//        } catch (IOException e) {
//
//        }
//        return linePorcessor.getResult();
//    }
    
    public static Map<EventEnum, Map<String, Integer>> parseEvent(String path){

    	Map<EventEnum, Map<String, Integer>> triggerMap=new HashMap<EventEnum, Map<String,Integer>>();
    	
		File xmlFolder=new File(path);
		
		if(xmlFolder.isDirectory()){
			for(File folder:xmlFolder.listFiles())
			{
				if(folder.isDirectory())
				{
					String type=null;
		        	if(folder.getName().equals("地震")){
		        		type="Earthquake";
		        	}else if(folder.getName().equals("火灾")){
		        		type="Fire";
		        	}else if(folder.getName().equals("交通事故")){
		        		type="Accident";
		        	}else if(folder.getName().equals("恐怖袭击")){
		        		type="Terror";
		        	}else{
		        		type="FoodPoison";
		        	}
					
					for(File xml:folder.listFiles())
					{
						try {
							List<String> xmlList=Files.readLines(xml, Charsets.UTF_8);
							for(String str:xmlList)
							{
						        Matcher matcher = pattern.matcher(str);
						        while(matcher.find())
						        {
						        	//找到触发词
						        	String triggerWord=matcher.group(1);
						        	EventEnum eventEnum=EventEnum.valueOf(type);
						        	
						        	Map<String, Integer> triggerCountMap = triggerMap.get(eventEnum);
						            if(null == triggerCountMap){
						                triggerCountMap = Maps.newHashMap();
						                triggerCountMap.put(triggerWord, 1);
						            }
						            else{
						                if(triggerCountMap.containsKey(triggerWord)){
						                    triggerCountMap.put(triggerWord, triggerCountMap.get(triggerWord)+1);
						                }
						                else{
						                    triggerCountMap.put(triggerWord, 1);
						                }
						            }
						            triggerMap.put(eventEnum, triggerCountMap);
						        	
						        }
						        
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}

        return triggerMap;
    }
}
