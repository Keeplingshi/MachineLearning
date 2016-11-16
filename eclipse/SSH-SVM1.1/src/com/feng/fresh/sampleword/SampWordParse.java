package com.feng.fresh.sampleword;

import com.feng.fresh.tools.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * /**
 * 同义词词林操作类
 * 现在有五级+标志
 * 取4级相等+标记=的词
 * Created by feng on 2016/8/30.
 */
public class SampWordParse {

    final static Logger LOGGER = LoggerFactory.getLogger(SampWordParse.class);

    private static final int LABEL_LEN= 5; //代表4级的长度为5

    public static Map<String, List<String>> label2wordMap = Maps.newHashMap();
    public static Map<String, String> word2labelMap = Maps.newHashMap();

   static{
	   
	   String path=FileUtils.corpousPath+"/samplewordtree/sampleword.txt";
       //String path =  SampWordParse.class.getClassLoader().getResource("samplewordtree/sampleword.txt").getPath();
        BufferedReader br = null;
       try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
           String line = null;
           while((line=br.readLine()) != null){
               String[] tokens = line.trim().split("=");
               if(tokens.length == 2){
                    String label = tokens[0].substring(0,LABEL_LEN);
                    String[] strs = tokens[1].trim().split("(\\s)+");
                   List list = label2wordMap.get(label);
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
