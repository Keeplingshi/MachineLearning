/*  
* 创建时间：2015年12月8日 下午12:43:01  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：corpusVector.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.Classification2;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

public class TrainDataControler 
{
	public Dataset data;
	public Instance toInstance(String [] tokens)
	{
		Instance sample = new SparseInstance(10);
		//TODO tf-idf vector
		return sample;
	}
	public TrainDataControler()
	{
		
	}
}
