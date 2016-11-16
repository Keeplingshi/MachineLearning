package com.feng.fresh.train;

import com.feng.fresh.lineprocessor.EventProcessor;
import com.feng.fresh.model.Event;
import com.feng.fresh.model.EventEnum;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 2016/8/25.
 */
public class EventParser {

    static final Logger LOGGER = LoggerFactory.getLogger(EventParser.class);
    /**
     * 根据训练语料获取触发词（事件）
     * @param file
     * @return
     */
    public static Map<EventEnum, Map<String, Integer>> parseEvent(File file){

        if(null==file || !file.exists())  return null;

        LOGGER.debug("开始处理文件：{}", file.getName());
        LineProcessor<Map<EventEnum, Map<String, Integer>>> linePorcessor = new EventProcessor();
        try {
            Files.readLines(file, Charsets.UTF_8, linePorcessor);
        } catch (IOException e) {
            LOGGER.error("读取文件失败", e);
        }
        return linePorcessor.getResult();
    }
}
