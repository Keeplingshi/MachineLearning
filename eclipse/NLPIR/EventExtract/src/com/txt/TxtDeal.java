package com.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本预处理
 * @author chenbin
 *
 */
public class TxtDeal {
	
	//URL正则表达式
    public static Pattern URL_Pattern = Pattern.compile("(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*"); 
    //@用户 正则表达式
    public static Pattern AT_PATTERN = Pattern.compile("@[\\u4e00-\\u9fa5\\w\\-]+");
	
	/**
	 * 文本处理，去除噪声
	 * @param originPath 源文件
	 * @param outputPath 输出文件
	 */
	public static void weiboTxtDeal(String originPath,String outputPath)
	{
        Matcher matcher=null;
		
        //读入文件
		File file = null;
		BufferedReader br=null;
		//写入文件
		FileWriter writer=null;
		
		try {
			file=new File(originPath);
			br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			
			writer = new FileWriter(outputPath);
			
			String lineTxt=null;
			while((lineTxt = br.readLine()) != null){
				String[] lineArr=lineTxt.split("\t");
				if(lineArr.length==25)
				{
					//获取当前句
					String str=lineArr[18];

					//含有URL的
			        matcher = URL_Pattern.matcher(str);
			        if(matcher.find()){
			        	continue;
			        }
			        //@用户的
			        matcher=AT_PATTERN.matcher(str);
			        if(matcher.find()){
			        	continue;
			        }
			        //删除str中的表情，即中括号括起来的[]
			        str=str.replaceAll("\\[([^\\[\\]]+)\\]", "");
			        //删除微博中的话题
			        str=str.replaceAll("#([^\\#|.]+)#", "");
			        //删除转发微博，分享图片
			        str=str.replaceAll("转发微博", "");
			        str=str.replaceAll("分享图片", "");
					
					//筛选掉字数太少的，少于10个汉字的，
					if(str.length()<10)
					{
						continue;
					}
			        
					writer.write(str+"\r\n");
				}
			}
			System.out.println("处理结束\t");
			
            writer.flush();
            writer.close();
			br.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
		}
	}
	
}
