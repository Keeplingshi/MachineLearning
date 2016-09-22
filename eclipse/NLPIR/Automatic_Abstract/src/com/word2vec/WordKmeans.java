package com.word2vec;

import java.util.ArrayList;  
import java.util.Collections;  
import java.util.Comparator;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;  
  
public class WordKmeans {  
    private HashMap<String, float[]> wordMap = null;  
    private int iter;  
    private Classes[] cArray = null;  
  
    /**
     * 
     * @param wordMap 词向量
     * @param clcn 聚类数
     * @param iter 迭代数
     */
    public WordKmeans(HashMap<String, float[]> wordMap, int clcn, int iter) {  
        this.wordMap = wordMap;  
        this.iter = iter;
        cArray = new Classes[clcn];  
    }
  
    public Classes[] explain() {  
        Iterator<Entry<String, float[]>> iterator = wordMap.entrySet().iterator();
        for (int i = 0; i < cArray.length; i++) {
            Entry<String, float[]> next = iterator.next();  
            cArray[i] = new Classes(i, next.getValue());  
        }
        for (int i = 0; i < iter; i++) {  
            for (Classes classes : cArray) {  
                classes.clean();  
            }
            iterator = wordMap.entrySet().iterator();  
            int cnt = 0;
            //对数据集中的每个点
            while (iterator.hasNext()) {
                if(cnt % 10000 ==0)  
                {
                    System.out.println("Iter:"+i+"\tword:"+(cnt));  
                }
                cnt++;  
                Entry<String, float[]> next = iterator.next();  
                double miniScore = Double.MAX_VALUE;  
                double tempScore;
                int classesId = 0;
                //对每个质心，计算质心与数据点之间距离，找到最近的
                for (Classes classes : cArray) {  
                    tempScore = classes.distance(next.getValue());  
                    if (miniScore > tempScore) {  
                        miniScore = tempScore;  
                        classesId = classes.id;  
                    }  
                }
                //将数据点分配到据其最近的簇
                cArray[classesId].putValue(next.getKey(), miniScore); 
            }  
            //更新每个簇的质心
            for (Classes classes : cArray) {  
                classes.updateCenter(wordMap);  
            }  
            System.out.println("iter " + i + " ok!");  
        }
        return cArray;  
    }  
  
    /**
     * 内部类
     * @author chenbin
     *
     */
    public static class Classes {
    	
        private int id;  
        private float[] center;  
        public Classes(int id, float[] center) {  
            this.id = id;  
            this.center = center.clone();  
        }  
  
        Map<String, Double> values = new HashMap<>();  
        
        public double distance(float[] value) {  
            double sum = 0;  
            for (int i = 0; i < value.length; i++) {  
                sum += (center[i] - value[i])*(center[i] - value[i]) ;  
            }
            return sum ;  
        }  
  
        public void putValue(String word, double score) {  
            values.put(word, score);  
        }
  
        public void updateCenter(HashMap<String, float[]> wordMap) {  
            for (int i = 0; i < center.length; i++) {  
                center[i] = 0;  
            }  
            float[] value = null;  
            for (String keyWord : values.keySet()) {  
                value = wordMap.get(keyWord);  
                for (int i = 0; i < value.length; i++) {  
                    center[i] += value[i];  
                }  
            }  
            for (int i = 0; i < center.length; i++) {  
                center[i] = center[i] / values.size();  
            }  
        }  
  
        public void clean() {  
            values.clear();  
        }  
  
        public List<Entry<String, Double>> getTop(int n) {  
            List<Map.Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(  
                values.entrySet());  
            Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {  
                @Override  
                public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {  
                    return o1.getValue() > o2.getValue() ? 1 : -1;  
                }  
            });  
            int min = Math.min(n, arrayList.size() - 1);  
            if(min<=1){  
                return Collections.emptyList() ;  
            }  
            return arrayList.subList(0, min);  
        }  
          
        public List<Entry<String, Double>> getMember() {  
            List<Map.Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(  
                values.entrySet());  
            Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {  
                @Override  
                public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {  
                    return o1.getValue() > o2.getValue() ? 1 : -1;  
                }  
            });  
            int count=arrayList.size() - 1;  
            if(count<=1){  
                return Collections.emptyList() ;  
            }  
            return arrayList.subList(0, count);  
        }  
    }  
}  