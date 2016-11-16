package com.feng.fresh.train;

import com.feng.fresh.model.EventEnum;
import com.feng.fresh.tools.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.PathResource;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by feng on 2016/8/25.
 */
public class EventParserTest {

    static final Logger LOGGER = LoggerFactory.getLogger(EventParserTest.class);
    /**
     * 获取train中所有触发词
     * @throws Exception
     */
    @Test
    public void parseEvent() throws Exception {

        String path = this.getClass().getClassLoader().getResource("train/train-arg.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        Map<EventEnum, Map<String, Integer>> map = EventParser.parseEvent(file);
        Assert.assertTrue(map != null);
        LOGGER.info(map.toString());
        FileUtils.loadMapStringMap(map, new File("trigger-counter.txt"));

    }

    @Test
    public void parseTestCorpusEvent() throws Exception {

        String path = this.getClass().getClassLoader().getResource("train/test-arg.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        Map<EventEnum, Map<String, Integer>> map = EventParser.parseEvent(file);
        Assert.assertTrue(map != null);
        LOGGER.info(map.toString());
        FileUtils.loadMapStringMap(map, new File("trigger-counter.txt"));

    }

}