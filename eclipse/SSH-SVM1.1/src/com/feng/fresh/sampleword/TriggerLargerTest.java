package com.feng.fresh.sampleword;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by feng on 16-9-4.
 */
public class TriggerLargerTest {

    static final Logger LOGGER = LoggerFactory.getLogger(TriggerLarger.class);

    @Test
    public void getType2wordsMap() throws Exception {

        Assert.assertNotNull(TriggerLarger.getType2wordsMap().toString());
        LOGGER.info(TriggerLarger.getType2wordsMap().toString());

    }

    @Test
    public void getWord2typeMap() throws Exception {

        LOGGER.info(TriggerLarger.getWord2typeMap().toString());
    }

}