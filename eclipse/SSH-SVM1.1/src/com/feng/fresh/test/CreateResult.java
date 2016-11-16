package com.feng.fresh.test;

import com.feng.fresh.model.EventEnum;
import com.feng.fresh.tools.StringUtils;
import com.feng.fresh.train.TriggerScorePre;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by feng on 2016/9/12.
 */
public class CreateResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateResult.class);

    //阈值
    private static final Double THREAD = 0.7;
    //触发词候选--》得分
    private static Map<String, Double> sorceMap = Maps.newHashMap();
    //词==》类型
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    static{
        sorceMap = TestTriggerScoreCalculate.getSorceMap();
        word2typeMap = TestTriggerScoreCalculate.getWord2typeMap();

    }

    /**
     * 先看分词的结果
     * @param file
     */
    public static void resultFactory(File file){
        if(null==file) return;

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.txt")));

            String line = null;
            while((line=bufferedReader.readLine()) != null) {
                line = line.replaceAll("/[a-zA-Z^(//s)]*", "");
                Iterable<String> list = Splitter.on(Pattern.compile(StringUtils.SEPORETOR)).trimResults().omitEmptyStrings().split(line);
                for (String str : list) {
                    Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(str);
                    for (String token : tokens) {

                        if(sorceMap.containsKey(token) && sorceMap.get(token)>THREAD){
                            bufferedWriter.write(str.replaceAll("(\\s)+", "")+"\t"+token+"\t"+word2typeMap.get(token));
                            bufferedWriter.newLine();
                        }
                    }

                }
            }



        } catch (FileNotFoundException e) {
            LOGGER.error("文件不能发现", e );
        } catch (IOException e) {
            LOGGER.error("IO异常", e );
        }
        finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error("IO异常", e );
                }
            }

            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    LOGGER.error("IO异常", e );
                }
            }


        }
        return ;
    }


}
