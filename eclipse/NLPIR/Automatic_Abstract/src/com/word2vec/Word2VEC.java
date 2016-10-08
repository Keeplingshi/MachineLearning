package com.word2vec;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Word2VEC {

    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();  
    
    private int words;  
    private int size;  
    private int topNSize = 40;
    private static final int MAX_SIZE = 50;  
    
    /** 
     * 加载模型 
     *  
     * @param path 
     *            模型的路径 
     * @throws IOException 
     */  
    public void loadModel(String path) throws IOException {  
        DataInputStream dis = null;  
        BufferedInputStream bis = null;  
        double len = 0;  
        float vector = 0;  
        try {  
            bis = new BufferedInputStream(new FileInputStream(path));  
            dis = new DataInputStream(bis);  
            // //读取词数  
            words = Integer.parseInt(readString(dis));  
            // //大小  
            size = Integer.parseInt(readString(dis));  
  
            String word;  
            float[] vectors = null;  
            for (int i = 0; i < words; i++) {  
                word = readString(dis);  
                vectors = new float[size];  
                len = 0;  
                for (int j = 0; j < size; j++) {  
                    vector = readFloat(dis);  
                    len += vector * vector;  
                    vectors[j] = (float) vector;  
                }  
                len = Math.sqrt(len);  
  
                for (int j = 0; j < vectors.length; j++) {  
                    vectors[j] = (float) (vectors[j] / len);  
                }  
                wordMap.put(word, vectors);
                dis.read();  
            }  
  
        } finally {  
            bis.close();  
            dis.close();  
        }  
    }  
    
    /** 
     * 读取一个字符串 
     *  
     * @param dis 
     * @return 
     * @throws IOException 
     */  
    private static String readString(DataInputStream dis) throws IOException {  
        byte[] bytes = new byte[MAX_SIZE];  
        byte b = dis.readByte();  
        int i = -1;  
        StringBuilder sb = new StringBuilder();  
        while (b != 32 && b != 10) {  
            i++;  
            bytes[i] = b;  
            b = dis.readByte();  
            if (i == 49) {  
                sb.append(new String(bytes));  
                i = -1;  
                bytes = new byte[MAX_SIZE];  
            }  
        }  
        sb.append(new String(bytes, 0, i + 1));  
        return sb.toString();  
    }  
    
    /**
     * 读取浮点型数据
     * @param is
     * @return
     * @throws IOException
     */
    public static float readFloat(InputStream is) throws IOException {  
        byte[] bytes = new byte[4];  
        is.read(bytes);  
        return getFloat(bytes);  
    }  
    
    /** 
     * 读取一个float 
     *  
     * @param b 
     * @return 
     */  
    public static float getFloat(byte[] b) {  
        int accum = 0;  
        accum = accum | (b[0] & 0xff) << 0;  
        accum = accum | (b[1] & 0xff) << 8;  
        accum = accum | (b[2] & 0xff) << 16;  
        accum = accum | (b[3] & 0xff) << 24;  
        return Float.intBitsToFloat(accum);  
    }  
	
    
    public int getTopNSize() {  
        return topNSize;  
    }  
  
    public void setTopNSize(int topNSize) {  
        this.topNSize = topNSize;  
    }  
  
    public HashMap<String, float[]> getWordMap() {  
        return wordMap;  
    }  
  
    public int getWords() {  
        return words;  
    }  
  
    public int getSize() {  
        return size;  
    }  
    
    /** 
     * 得到词向量 
     *  
     * @param word 
     * @return 
     */  
    public float[] getWordVector(String word) {  
        return wordMap.get(word);  
    }  
}
