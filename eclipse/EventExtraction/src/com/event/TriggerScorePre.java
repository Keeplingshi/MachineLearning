package com.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.model.EventEnum;
import com.event.TriggerLarger;

/**
 * 
 * @author chenbin
 *
 */
public class TriggerScorePre {

    static final Logger LOGGER = LoggerFactory.getLogger(TriggerScorePre.class);
    public static final String SEPORETOR = "[。,!,?]";

    /**
     * 使用已有的数据
     */
    //词==》类型
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    /**
     * 需要计算的值
     */
    //触发词触发事件个数
    private static Map<String, Integer> trigger2CounterMap = Maps.newHashMap();
    //触发词类型出现的个数
    private static Map<EventEnum, Integer> type2CounterMap = Maps.newHashMap();
    //句子总数
    private static int sentenceTotalNum;
    //含有触发词的句子数, 与trigger2CounterMap差不多一样
    private static Map<String, Integer> trigger2SentenceCounterMap = Maps.newHashMap();

    //保存句子
    private static List<String> sentenceList = Lists.newArrayList();


    /**
     * 最终的结果
     */
    private static Map<String, Double> trigger2ScordMap;

    static{

        word2typeMap = TriggerLarger.getWord2typeMap();

        String train_seq_path=System.getProperty("user.dir")+"/corpous/cec/CEC_Train_Corpous/cecResult.txt";
        //System.out.println("3\t"+train_seq_path);
        File file = new File(train_seq_path);
        TriggerScorePre.splitSentece(file);
    }

    /**
     * 获取train的句子，词性对我们没有用
     * @param file
     * @return
     */
    public static List<String> splitSentece(File file){
        if(null==file) return null;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while((line=bufferedReader.readLine()) != null){
                line = line.replaceAll("/[a-zA-Z^(//s)]*","");
                parseLine(line);
            }

            sentenceTotalNum = sentenceList.size();
            LOGGER.info("【触发词触发事件的次数】,{}", trigger2CounterMap);
            LOGGER.info("【触发词类型的次数】,{}", type2CounterMap);
            LOGGER.info("【句子个数】,{}", sentenceList.size());
            LOGGER.info("【触发词触发出现在句子中次数】,{}", trigger2SentenceCounterMap);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 进行分句，并统计各个量
     * @param line
     */
    private static void parseLine(String line) {
        Iterable<String> list = Splitter.on(Pattern.compile(SEPORETOR)).trimResults().omitEmptyStrings().split(line);
        for(String str : list){
            sentenceList.add(str);
            Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(str);
            for(String token : tokens){
                if(word2typeMap.containsKey(token)){
                    increaseMap(trigger2CounterMap, token); //一个句子可以统计多个相同的词
                    increaseMap(type2CounterMap, word2typeMap.get(token));
                }
            }

            Set<String> set = Sets.newHashSet(tokens);
            for(String token : tokens){
                if(word2typeMap.containsKey(token)){
                    increaseMap(trigger2SentenceCounterMap, token);   //一个句子只能统计一个相同的词
                }
            }
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

    public static Map<String, Integer> getTrigger2CounterMap() {
        return trigger2CounterMap;
    }

    public static void setTrigger2CounterMap(Map<String, Integer> trigger2CounterMap) {
        TriggerScorePre.trigger2CounterMap = trigger2CounterMap;
    }

    public static Map<EventEnum, Integer> getType2CounterMap() {
        return type2CounterMap;
    }

    public static void setType2CounterMap(Map<EventEnum, Integer> type2CounterMap) {
        TriggerScorePre.type2CounterMap = type2CounterMap;
    }

    public static int getSentenceTotalNum() {
        return sentenceTotalNum;
    }

    public static void setSentenceTotalNum(int sentenceTotalNum) {
        sentenceTotalNum = sentenceTotalNum;
    }

    public static Map<String, Integer> getTrigger2SentenceCounterMap() {
        return trigger2SentenceCounterMap;
    }

    public static void setTrigger2SentenceCounterMap(Map<String, Integer> trigger2SentenceCounterMap) {
        TriggerScorePre.trigger2SentenceCounterMap = trigger2SentenceCounterMap;
    }

    public static List<String> getSentenceList() {
        return sentenceList;
    }

    public static void setSentenceList(List<String> sentenceList) {
        TriggerScorePre.sentenceList = sentenceList;
    }

    public static Map<String, Double> getTrigger2ScordMap() {
        return trigger2ScordMap;
    }

    public static void setTrigger2ScordMap(Map<String, Double> trigger2ScordMap) {
        TriggerScorePre.trigger2ScordMap = trigger2ScordMap;
    }

    public static Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }

    public static void setWord2typeMap(Map<String, EventEnum> word2typeMap) {
        TriggerScorePre.word2typeMap = word2typeMap;
    }
}
