package com.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class DealText {

	public static void main(String[] args) {
		
		DealText dealText=new DealText();
		dealText.deal("C:/Users/chenbin/Desktop/resultbig.txt", "C:/Users/chenbin/Desktop/resultSougou.txt");
		
	}
	
	public void deal(String path,String output)
	{
		System.out.println("开始");
		try {
			File file=new File(path);
			File targetFile = new File(output);
            if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    if(lineTxt.contains("< content > ")){
                    	lineTxt=lineTxt.replaceAll("< content > ", "");
                    }
                    if(lineTxt.contains("< / content >")){
                    	lineTxt=lineTxt.replaceAll("< / content >", "");
                    }
                    lineTxt=lineTxt.replaceAll("[\\pP\\p{Punct}]", "");
                    bw.write(lineTxt);
                    bw.newLine();
                }
                read.close();
                bw.flush();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("结束");
	}

}
