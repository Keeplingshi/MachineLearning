package com.feng.fresh.sampleword;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by feng on 2016/9/1.
 */
public class SampWordParseTest {

    Logger LOGGER = LoggerFactory.getLogger(SampWordParseTest.class);

    @Test
    public void sampleTest(){

        LOGGER.info(SampWordParse.label2wordMap.toString());
        LOGGER.info(SampWordParse.word2labelMap.toString());
    }

}