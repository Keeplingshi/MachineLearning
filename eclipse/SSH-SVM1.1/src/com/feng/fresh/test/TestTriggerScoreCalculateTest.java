package com.feng.fresh.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.feng.fresh.tools.FileUtils;

/**
 * Created by feng on 2016/9/11.
 */
public class TestTriggerScoreCalculateTest {

    @Test
    public void caculateTest(){
    	
    	String path=FileUtils.corpousPath+"/seq/test-seq.txt";
        //String path = this.getClass().getClassLoader().getResource("seq/test-seq.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        TestTriggerScoreCalculate.parseTest(file);
    }

}