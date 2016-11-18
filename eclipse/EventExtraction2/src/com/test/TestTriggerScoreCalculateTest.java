package com.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by feng on 2016/9/11.
 */
public class TestTriggerScoreCalculateTest {

	public static final String test_seq_path=System.getProperty("user.dir")+"/data/medicine/seq/test-seq.txt";
	
    @Test
    public void caculateTest(){
    	
        File file = new File(test_seq_path);
        Assert.assertTrue(null!=file && file.exists());
        TestTriggerScoreCalculate.parseTest(file);
    }

}