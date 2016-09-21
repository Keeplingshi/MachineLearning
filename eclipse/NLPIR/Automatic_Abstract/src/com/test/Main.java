package com.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map.Entry;

import com.test.WordKmeans;
import com.test.WordKmeans.Classes;

public class Main {
	
	public static void main(String[] args) {
        Word2VEC vec = new Word2VEC();  
        try {
			vec.loadModel("D:/Data/vectorsSougou.bin");
			//WordKmeans wordKmeans = new WordKmeans(vec.getWordMap(), Integer.parseInt(args[2]),Integer.parseInt(args[3]));  
			WordKmeans wordKmeans=new WordKmeans(vec.getWordMap(), 50, 30);
			Classes[] explain=wordKmeans.explain();
			
	        File fw = new File("D:/Data/vectorsSougouResult.txt");  
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fw), "UTF-8"));  
	          
	        //explain.length is the classes number  
	        for (int i = 0; i < explain.length; i++) {  
	            List<Entry<String, Double>> result=explain[i].getMember();  
	            StringBuffer buf = new StringBuffer();  
	            for (int j = 0; j < result.size(); j++) {  
	                buf.append(i+"\t"+result.get(j).getKey()+"\t"+result.get(j).getValue().toString()+"\n");  
	            }  
	            bw.write(buf.toString());  
	            bw.flush();  
	        }  
	        bw.close();  
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
