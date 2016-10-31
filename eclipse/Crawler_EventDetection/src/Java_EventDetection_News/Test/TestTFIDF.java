/*  
* 创建时间：2015年12月8日 下午1:08:49  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：TestTFIDF.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.Test;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class TestTFIDF {

    /**
     * @param args
     */    
    private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file

    //get list of file for the directory, including sub-directory of it
    public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException
    {
        try
        {
            File file = new File(filepath);
            if(!file.isDirectory())
            {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            }
            else
            {
                String[] flist = file.list();
                for(int i = 0; i < flist.length; i++)
                {
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        readDirs(filepath + "\\" + flist[i]);
                    }                    
                }
            }
        }catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return FileList;
    }
    
    //read file
    public static String readFile(String file) throws FileNotFoundException, IOException
    {
        StringBuffer strSb = new StringBuffer(); //String is constant， StringBuffer can be changed.
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "gbk"); //byte streams to character streams
        BufferedReader br = new BufferedReader(inStrR); 
        String line = br.readLine();
        while(line != null){
            strSb.append(line).append("\r\n");
            line = br.readLine();    
        }
        
        return strSb.toString();
    }
    
    //word segmentation
    public static ArrayList<String> cutWords(String file) throws IOException{
        
        ArrayList<String> words = new ArrayList<String>();
//        String text = ReadFiles.readFile(file);
//        IKAnalyzer analyzer = new IKAnalyzer();
//        words = analyzer.split(text);
        
        return words;
    }
    
    public static String[] _cutWords(String file) throws IOException
    {
        String text = TestTFIDF.readFile(file);
//        IKAnalyzer analyzer = new IKAnalyzer();
//        words = analyzer.split(text);
        return text.split(" ");
    }
    
    //term frequency in a file, times for each word
    public static HashMap<String, Integer> _normalTF(String[] cutwords){
        HashMap<String, Integer> resTF = new HashMap<String, Integer>();
        
        for(String word : cutwords){
            if(resTF.get(word) == null){
                resTF.put(word, 1);
                System.out.println(word);
            }
            else{
                resTF.put(word, resTF.get(word) + 1);
                System.out.println(word.toString());
            }
        }
        return resTF;
    }
    
    //term frequency in a file, times for each word
    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords){
        HashMap<String, Integer> resTF = new HashMap<String, Integer>();
        
        for(String word : cutwords){
            if(resTF.get(word) == null){
                resTF.put(word, 1);
                System.out.println(word);
            }
            else{
                resTF.put(word, resTF.get(word) + 1);
                System.out.println(word.toString());
            }
        }
        return resTF;
    }
    
    //term frequency in a file, frequency of each word
    public static HashMap<String, Float> tf(ArrayList<String> cutwords){
        HashMap<String, Float> resTF = new HashMap<String, Float>();
        
        int wordLen = cutwords.size();
        HashMap<String, Integer> intTF = TestTFIDF.normalTF(cutwords); 
        
        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
            System.out.println(entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTF;
    } 
  //term frequency in a file, frequency of each word
    public static HashMap<String, Float> _tf(String[] cutwords)
    {
        HashMap<String, Float> resTF = new HashMap<String, Float>();
        
        int wordLen = cutwords.length;
        HashMap<String, Integer> intTF = TestTFIDF._normalTF(cutwords); 
        
        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTF;
    } 
    //tf times for file
    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException{
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String,Integer>>();
        
        List<String> filelist = TestTFIDF.readDirs(dirc);
        for(String file : filelist){
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            ArrayList<String> cutwords = TestTFIDF.cutWords(file); //get cut word for one file
            
            dict = TestTFIDF.normalTF(cutwords);
            allNormalTF.put(file, dict);
        }    
        return allNormalTF;
    }
    
    //tf for all file
    public static HashMap<String,HashMap<String, Float>> tfAllFiles(String dirc) throws IOException
    {
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        List<String> filelist = TestTFIDF.readDirs(dirc);
        
        for(String file : filelist){
            HashMap<String, Float> dict = new HashMap<String, Float>();
            ArrayList<String> cutwords = TestTFIDF.cutWords(file); //get cut words for one file
            
            dict = TestTFIDF.tf(cutwords);
            allTF.put(file, dict);
        }
        return allTF;
    }
    
    public static ArrayList<HashMap<String, Float>> _tfAllFiles(String dirc) throws IOException
    {
        ArrayList<HashMap<String, Float>> allTF = new ArrayList<HashMap<String, Float>>();
        List<String> filelist = TestTFIDF.readDirs(dirc);
        
        for(String file : filelist){
            HashMap<String, Float> dict = new HashMap<String, Float>();
            String[] cutwords = TestTFIDF._cutWords(file); //get cut words for one file
            dict = TestTFIDF._tf(cutwords);
            allTF.add( dict);
        }
        return allTF;
    }
    
    public static ArrayList<HashMap<String, Float>> tfAllDocs(ArrayList<String[]> docs)
    {
        ArrayList<HashMap<String, Float>> allTF = new ArrayList<HashMap<String, Float>>();
        
        for(String[] docWords : docs){
            HashMap<String, Float> dict = new HashMap<String, Float>();
            dict = TestTFIDF._tf(docWords);
            allTF.add( dict);
        }
        return allTF;
    }
    
    @SuppressWarnings("rawtypes")
	public static HashMap<String, Float> _idf(ArrayList<HashMap<String, Float>>  all_tf)
    {
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        int docNum = all_tf.size();
        
        for(HashMap<String, Float> docTF :all_tf)
        {
            Iterator<Entry<String, Float>> iter = docTF.entrySet().iterator();
            while(iter.hasNext()){
                Entry entry = (Entry)iter.next();
                String word = entry.getKey().toString();
                if(dict.get(word) == null){
                    dict.put(word, 1);
                }else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        Iterator<Entry<String, Integer>> iter_dict = dict.entrySet().iterator();
        while(iter_dict.hasNext()){
            Entry entry = (Entry)iter_dict.next();
            float value = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
        }
        return resIdf;
    }
    
    public static HashMap<String, Float> idf(HashMap<String,HashMap<String, Float>> all_tf)
    {
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        int docNum = FileList.size();
        
        for(int i = 0; i < docNum; i++){
            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                if(dict.get(word) == null){
                    dict.put(word, 1);
                }else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        System.out.println("IDF for every word is:");
        Iterator iter_dict = dict.entrySet().iterator();
        while(iter_dict.hasNext()){
            Map.Entry entry = (Map.Entry)iter_dict.next();
            float value = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
            System.out.println(entry.getKey().toString() + " = " + value);
        }
        return resIdf;
    }
    
    public static ArrayList<HashMap<String, Float>> _tf_idf
    (ArrayList<HashMap<String, Float>> all_tf, HashMap<String, Float> idfs)
    {
    	ArrayList<HashMap<String, Float>> resTfIdf = new ArrayList<HashMap<String, Float>>();
            
        for(HashMap<String, Float> docTF:all_tf)
        {
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            Iterator<Entry<String, Float>> iter = docTF.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word); 
                tfidf.put(word, value);
            }
            resTfIdf.add(tfidf);
        }
//        System.out.println("TF-IDF for Every file is :");
//        DisTfIdf(resTfIdf);
        return resTfIdf;
    }

    public static void tf_idf(HashMap<String,HashMap<String, Float>> all_tf,HashMap<String, Float> idfs)
    {
        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<String, HashMap<String, Float>>();
            
        int docNum = FileList.size();
        for(int i = 0; i < docNum; i++){
            String filepath = FileList.get(i);
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String word = entry.getKey().toString();
                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word); 
                tfidf.put(word, value);
            }
            resTfIdf.put(filepath, tfidf);
        }
        System.out.println("TF-IDF for Every file is :");
        DisTfIdf(resTfIdf);
    }
    
    @SuppressWarnings("rawtypes" )
	public static void _DisTfIdf(ArrayList<HashMap<String, Float>> tfidf)
    {
        for(HashMap<String, Float> eachTFIDF:tfidf)
        {
            Iterator iter2 = eachTFIDF.entrySet().iterator();
            while(iter2.hasNext()){
                Map.Entry entry = (Map.Entry)iter2.next(); 
                System.out.print(entry.getKey().toString() + " = " + entry.getValue().toString() + ", ");
            }
            System.out.println("}");
        }
        
    }

    public static void DisTfIdf(HashMap<String, HashMap<String, Float>> tfidf){
        Iterator iter1 = tfidf.entrySet().iterator();
        while(iter1.hasNext()){
            Map.Entry entrys = (Map.Entry)iter1.next();
            System.out.println("FileName: " + entrys.getKey().toString());
            System.out.print("{");
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            Iterator iter2 = temp.entrySet().iterator();
            while(iter2.hasNext()){
                Map.Entry entry = (Map.Entry)iter2.next(); 
                System.out.print(entry.getKey().toString() + " = " + entry.getValue().toString() + ", ");
            }
            System.out.println("}");
        }
        
    }
    
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        String file = "D:/testfiles";

//        HashMap<String,HashMap<String, Float>> all_tf = tfAllFiles(file);
//        System.out.println();
//        HashMap<String, Float> idfs = idf(all_tf);
//        System.out.println();
//        tf_idf(all_tf, idfs);
        
        ArrayList<HashMap<String, Float>> all_tf = _tfAllFiles(file);
        System.out.println();
        HashMap<String, Float> idfs = _idf(all_tf);
        System.out.println();
        _tf_idf(all_tf, idfs);
        
    }

}