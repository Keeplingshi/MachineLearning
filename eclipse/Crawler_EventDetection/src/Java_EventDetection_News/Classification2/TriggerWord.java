/*  
* 创建时间：2015年12月4日 下午3:18:13  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：TriggerWord.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.Classification2;

import java.util.ArrayList;

import Java_EventDetection_News.Label.LabelItem;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;

public class TriggerWord 
{
	public String word;
	public int mainType;
	public boolean ambiguous;
	public Classifier knn ;
	public ArrayList<LabelItem> trainingCorpus;
	public TrainDataControler trainControler;
	
	
	public TriggerWord(String word,int mainType)
	{
		this.word = word;
		this.mainType = mainType;
		this.ambiguous = false;
		trainingCorpus = new ArrayList<LabelItem>();
	}
	

	public void addTrainingData(LabelItem label)
	{		
		trainingCorpus.add(label);
	}
	public void setModel()
	{
		//TODO 判断是否需要分类器
		
		
		//TODO 通过trainingCorpus ，构造 trainControler . This method is very important.
		this.ambiguous = true;
		int sampleNumber = trainingCorpus.size();
		
		
		//make knn
		knn = new KNearestNeighbors(sampleNumber/2);
		knn.buildClassifier(trainControler.data);
		
	}

	public int getEventType(String[] tokens)
	{
		if(this.ambiguous == false)
			return this.mainType;
		Object  predictedClassValue = knn.classify(trainControler.toInstance(tokens));
		return Integer.parseInt(predictedClassValue.toString());
		
	}
	
	
	
}
