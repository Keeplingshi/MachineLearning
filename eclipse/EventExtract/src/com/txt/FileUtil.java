package com.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	public static void orderTxtFile(String filePath, String encoding) {
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					
					String[] strArray=lineTxt.split("\t");
					
					if(strArray.length==1){
						System.out.println(lineTxt);
					}else{
						Set<String> set = new HashSet<String>(Arrays.asList(strArray));
					}
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
