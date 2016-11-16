package com.feng.fresh.tools;

import com.feng.fresh.model.EventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * Created by feng on 2016/8/29.
 */
public class FileUtils {

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
}
