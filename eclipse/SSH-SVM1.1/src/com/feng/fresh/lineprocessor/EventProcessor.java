package com.feng.fresh.lineprocessor;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.feng.fresh.model.EventEnum;
import com.google.common.collect.Maps;
import com.google.common.io.LineProcessor;

/**
 * 获取每行中的触发词
 * Created by feng on 2016/8/25.
 */
public class EventProcessor implements LineProcessor<Map<EventEnum, Map<String, Integer>>>{

    private Map<EventEnum, Map<String, Integer>> triggerMap = Maps.newHashMap();

    public boolean processLine(String s) throws IOException {

        filterEvent(s);
        return true;
    }

    private void filterEvent(String s) {
        if(StringUtils.isEmpty(s)) return ;

        String reg_pattern = "(?<=<((\\w){1,20})-trigger>)[^<>]*(?=<\\/\\1-trigger>)";
        Pattern pattern = Pattern.compile(reg_pattern);
        Matcher matcher = pattern.matcher(s);
        //System.out.println(str);
        while(matcher.find())
        {
            String triggerWord = matcher.group();
            EventEnum type = EventEnum.valueOf(matcher.group(1));

            Map<String, Integer> triggerCountMap = triggerMap.get(type);
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

            triggerMap.put(type, triggerCountMap);
        }
    }

    public Map<EventEnum, Map<String, Integer>> getResult() {
        return triggerMap;
    }
}
