package com.test;

import java.io.IOException;
import java.util.HashMap;

public class Kmeans {
    
	public static void main(String[] args) {
        Word2VEC vec = new Word2VEC();  
        try {
			vec.loadModel("D:/Data/vectorsSougou.bin");
			System.out.println(vec.getWordMap().size());
			//vec.getSize()
			//WordKmeans wordKmeans=new WordKmeans(vec.getWordMap(), 50, 30);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * 为给定数据集构建一个包含k个随机质心的集合
	 * @return
	 */
	public float[][] randcent(HashMap<String, float[]> wordMap,int vecsize,int k)
	{
		int n=wordMap.size();
		float[][] centroids=new float[k][n];
		float[] min=new float[vecsize];
		float[] max=new float[vecsize];
		
/*	    n=shape(dataSet)[1]
	    	    centroids=mat(zeros((k,n)))
	    	    for j in range(n):
	    	        minJ=min(dataSet[:,j])
	    	        maxJ=max(dataSet[:,j])
	    	        rangeJ=float(maxJ-minJ)     #最大-最小的差值
	    	        centroids[:,j] = minJ + rangeJ * random.rand(k,1)   #保证随机点在数据的边界之中
	    	    return centroids*/
		return null;
	}

}