package com.main;

import java.io.File;

import com.test.CreateResult;
import com.test.TestTriggerScoreCalculate;

public class Main {

	public static void main(String[] args) {
		
        //new TestTriggerScoreCalculate();
        
        String test_seq_path=System.getProperty("user.dir")+"/data/cec/CEC_Test_Corpous/cecTestResult.txt";
        File file = new File(test_seq_path);
        TestTriggerScoreCalculate.parseTest(file);
        CreateResult.resultFactory(file);
		
	}
}
