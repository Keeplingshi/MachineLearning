package com.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileUtil {

	/**
	 * 将内容写进txt文件
	 * 
	 * @param content
	 * @param denoterTxt
	 * @return
	 * @throws Exception
	 */
	public static boolean writeTxtFile(StringBuffer content, String denoterTxt) {
		boolean flag = false;
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(denoterTxt);
			outputStream.write(content.toString().getBytes("UTF-8"));
			outputStream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 整理txt文件内容
	 * @param filePath
	 * @param encoding
	 */
	public static HashMap<String, List<String>> orderTxtFile(String filePath, String encoding) {
		
		HashMap<String, List<String>> denoterMap=null;
		
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				
				denoterMap=new HashMap<>();
				
				while ((lineTxt = bufferedReader.readLine()) != null) {
					
					//如果是一个词，那么说明是事件类别
					if(lineTxt.split("\t").length==1){
						String denoterKey=lineTxt;
						List<String> denoterVal=new ArrayList<>();
						
						//读取该事件的触发词
						lineTxt = bufferedReader.readLine();
						String[] strArray=lineTxt.split("\t");
						
						//寻找触发词出现频率大于2的，如果大于2则设置为触发词
						HashMap<String, Integer> lineMap=new HashMap<>();
						for(String str:strArray)
						{
							Integer lineDenoterNum=lineMap.get(str);
							if(lineDenoterNum==null){
								lineMap.put(str, 1);
							}else{
								lineMap.put(str, lineMap.get(str)+1);
							}
						}
						
						Iterator<String> iterator = lineMap.keySet().iterator();  
						while (iterator.hasNext()){  
							String key=(String)iterator.next();
							if(lineMap.get(key)>1){
								denoterVal.add(key);
							}
						}
						
						denoterMap.put(denoterKey, denoterVal);
					}
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return denoterMap;

	}

}
