package com.feng.fresh.test;

import com.feng.fresh.model.EventEnum;
import com.feng.fresh.tools.FileUtils;
import com.feng.fresh.train.TriggerScorePre;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by feng on 2016/9/11.
 */
public class TestTriggerScoreCalculate {

    static final Logger LOGGER = LoggerFactory.getLogger(TestTriggerScoreCalculate.class);

    /**
     * 使用已有的数据
     */
    //词==》类型
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();
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
    private static Set<String> triggerSet = Sets.newHashSet();

    public static Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }

    public static void setWord2typeMap(Map<String, EventEnum> word2typeMap) {
        TestTriggerScoreCalculate.word2typeMap = word2typeMap;
    }

    public static Map<String, Double> getSorceMap() {
        return sorceMap;
    }

    public static void setSorceMap(Map<String, Double> sorceMap) {
        TestTriggerScoreCalculate.sorceMap = sorceMap;
    }

    //触发词候选--》得分
    private static Map<String, Double> sorceMap = Maps.newHashMap();

    static{

        word2typeMap = TriggerScorePre.getWord2typeMap();
        trigger2CounterMap = TriggerScorePre.getTrigger2CounterMap();
        trigger2SentenceCounterMap = TriggerScorePre.getTrigger2SentenceCounterMap();
        sentenceTotalNum = TriggerScorePre.getSentenceTotalNum();
        type2CounterMap = TriggerScorePre.getType2CounterMap();

        String path=FileUtils.corpousPath+"/seq/test-seq.txt";
        //String path = TestTriggerScoreCalculate.class.getClassLoader().getResource("seq/test-seq.txt").getPath();
        File file = new File(path);
        TestTriggerScoreCalculate.parseTest(file);
    }

    /**
     * 进行分句，抽取候选词，并进行计算
     * @param file
     */
    public static void parseTest(File file) {

        if(null==file) return;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while((line=bufferedReader.readLine()) != null){
                //筛选出动词和名词，（没有动名词标志）
                Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(line);
                for(String token : tokens ){
                    String[] strs = token.split("/");
                    if(strs.length>1 && (strs[1].equals("n") ||strs[1].equals("v")) && trigger2CounterMap.containsKey(strs[0])){

                        triggerSet.add(strs[0]);
                    }
                }

            }

            calculate();
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(sorceMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o1.getValue()>o2.getValue()?-1:1;
                }
            });
            LOGGER.info("【计算结果】,{},{}",list.size(), list);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }

    /**
     * 计算值
     */
    private static void calculate() {

        Double result;

        for(String word : triggerSet){
            result = 0.0;
            Double e1 = 1.0*trigger2CounterMap.get(word)/type2CounterMap.get(word2typeMap.get(word));
            Double e2 = Math.log(1.0*sentenceTotalNum/trigger2SentenceCounterMap.get(word));
            result = e1 *e2;
            sorceMap.put(word, result);
        }
    }


}
