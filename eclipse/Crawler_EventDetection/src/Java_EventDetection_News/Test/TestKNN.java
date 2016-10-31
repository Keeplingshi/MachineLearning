/*  
* 创建时间：2015年12月7日 下午7:58:42  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：TestKNN.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.Test;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

public class TestKNN 
{
	public static void main(String []args) throws IOException
	{
		Dataset data = FileHandler.loadDataset(new File("iris.data"), 4, ",");
		 /* Contruct a KNN classifier that uses 5 neighbors to make a
		  *decision. */
		Classifier knn = new KNearestNeighbors(5);
		knn.buildClassifier(data);
		Dataset dataForClassification = FileHandler.loadDataset(new File ("iris.data"), 4, ",");
		/* Counters for correct and wrong predictions. */
		int correct = 0, wrong = 0;
		/* Classify all instances and check with the correct class values */
		for (Instance inst : dataForClassification) {
		    Object  predictedClassValue = knn.classify(inst);
		    Object  realClassValue = inst.classValue();
		    System.out.print(predictedClassValue.toString()+"\t");
		    System.out.println(realClassValue.toString());
		    if (predictedClassValue.equals(realClassValue))
		        correct++;
		    else
		        wrong++;
		}
		System.out.println(correct);
		System.out.println(wrong);
	}
	
}
