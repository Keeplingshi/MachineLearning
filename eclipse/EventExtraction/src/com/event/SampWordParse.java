package com.event;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 同义词词林操作类
 * 现在有五级+标志
 * 取4级相等+标记=的词
 * 将整个同义词林放入map中
 */
public class SampWordParse {

    final static Logger LOGGER = LoggerFactory.getLogger(SampWordParse.class);

    private static final int LABEL_LEN= 7; //代表5级

    public static Map<String, List<String>> label2wordMap = Maps.newHashMap();
    public static Map<String, String> word2labelMap = Maps.newHashMap();

   static{
	   
	   String sampleword_path=System.getProperty("user.dir")+"/corpous/samplewordtree/sampleword.txt";
       BufferedReader br = null;
       try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sampleword_path)));
           String line = null;
           while((line=br.readLine()) != null){
               String[] tokens = line.trim().split("=");
               if(tokens.length == 2){
            	   String label = tokens[0].substring(0,LABEL_LEN);
            	   String[] strs = tokens[1].trim().split("(\\s)+");
                   List<String> list = label2wordMap.get(label);
                   for(String str : strs){
                       if(null == list){
                           list = Lists.newArrayList();
                       }
                       list.add(str);
                       word2labelMap.put(str, label);
                   }
                   label2wordMap.put(label, list);
               }
           }
       } catch (FileNotFoundException e) {
           LOGGER.error("不能发现文件",e);
       } catch (IOException e) {
           LOGGER.error("不能发现文件",e);
       }
   }
}
