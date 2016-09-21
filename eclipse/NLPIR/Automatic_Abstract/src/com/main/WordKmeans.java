package com.main;

import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStreamWriter;  
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
      
    //total 659624 words each is a 200 vector  
    //args[0] is the word vectors csv file  
    //args[1] is the output file   
    //args[2] is the cluster number  
    //args[3] is the iterator number  
    public static void main(String[] args) throws IOException {  
        Word2VEC vec = new Word2VEC();  
        vec.loadModel("D:/Data/vectorsSougou.bin");
        // vec.loadVectorFile(args[0]);  
        System.out.println("load data ok!");  
          
        //input cluster number and iterator number  
        WordKmeans wordKmeans = new WordKmeans(vec.getWordMap(), Integer.parseInt(args[2]),Integer.parseInt(args[3]));  
        Classes[] explain = wordKmeans.explain();  
  
        File fw = new File(args[1]);  
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
    }  
  
  
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
                for (Classes classes : cArray) {  
                    tempScore = classes.distance(next.getValue());  
                    if (miniScore > tempScore) {  
                        miniScore = tempScore;  
                        classesId = classes.id;  
                    }  
                }  
                cArray[classesId].putValue(next.getKey(), miniScore);  
            }  
            for (Classes classes : cArray) {  
                classes.updateCenter(wordMap);  
            }  
            System.out.println("iter " + i + " ok!");  
        }  
        return cArray;  
    }  
  
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