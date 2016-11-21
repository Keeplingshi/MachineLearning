package com.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.EventEnum;

/**
 * Created by feng on 2016/8/29.
 */
public class FileUtils {

	/**
	 * 语料路径
	 */
	public static final String corpousPath=System.getProperty("user.dir")+"/data/";;
	
    static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static void loadMapStringMap(Map<EventEnum, Map<String, Integer>> map, File file){

        if(null==file || null==map) return;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for(Map.Entry<EventEnum, Map<String, Integer>> entry : map.entrySet()){
                bw.write(entry.getKey().toString());
                bw.newLine();
                int count = 0;
                for (Map.Entry<String, Integer> triggerEntry : entry.getValue().entrySet()){
                    bw.write(triggerEntry.getKey().toString()+":"+triggerEntry.getValue().toString());
                    bw.newLine();
                    count += triggerEntry.getValue();
                }
                bw.write("总数：" + count);
                bw.newLine();
                bw.write("==================================================");
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("文件读取出错", e );
        } catch (IOException e) {
            LOGGER.error("写文件出错", e );
        }
        finally {
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件出错", e );
                }
            }
        }
    }

    public <K, T> void loadMapSimpleType(Map<K, T> map, File file){
        if(null==map || null==file) return;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for(Map.Entry<K, T> entry : map.entrySet()){
                bw.write(entry.getKey().toString()+":"+entry.getValue().toString());
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("文件读取出错", e );
        } catch (IOException e) {
            LOGGER.error("写文件出错", e );
        }
        finally {
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件出错", e );
                }
            }
        }

    }
    
    /**
     * 将内容写入txt文件
     * @param content
     * @param path
     * @return
     */
	public static boolean writeTxtFile(StringBuffer content, String path) {
		boolean flag = false;
		
		File file=new File(path);
		if(!file.exists()){
			createFile(path);
		}
		
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(path);
			outputStream.write(content.toString().getBytes("UTF-8"));
			outputStream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 创建文件
	 * 
	 * @param filename
	 *            文件名称
	 * @return
	 */
	public static boolean createFile(String destFileName) {

		File file = new File(destFileName);
		// 如果存在，返回true
		if (file.exists()) {
			return true;
		}
		if (destFileName.endsWith(File.separator)) {
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			if (!file.getParentFile().mkdirs()) {
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}
	
}
