package com.feng.fresh.train;

import org.junit.Assert;
import org.junit.Test;

import com.feng.fresh.tools.FileUtils;

import java.io.File;

/**
 * Created by feng on 2016/9/10.
 */
public class TriggerScordTest {

    @Test
    public void splitSentence(){
    	
    	//String path=FileUtils.corpousPath+"/cec/CEC_Train_Corpous/cecResult.txt";
    	String path=FileUtils.corpousPath+"/seq/train-seq.txt";
        //String path = this.getClass().getClassLoader().getResource("seq/train-seq.txt").getPath();
        File file = new File(path);
        Assert.assertTrue(null!=file && file.exists());
        TriggerScorePre.splitSentece(file);
    }

}