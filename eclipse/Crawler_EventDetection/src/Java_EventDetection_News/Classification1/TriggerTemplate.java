/*  
 * 创建时间：2015年10月25日 下午10:52:13  
 * 项目名称：Java_EventDetection_News  
 * @author GreatShang  
 * @version 1.0   
 * @since JDK 1.8.0_21  
 * 文件名称：TriggerTemplate.java  
 * 系统信息：Windows Server 2008
 * 类说明：  触发词模板的实体类
 * 功能描述：存储触发此模板
 */
package Java_EventDetection_News.Classification1;

import java.util.HashMap;
import java.util.Map.Entry;

public class TriggerTemplate
{
	public HashMap<String,int[]> triggerNumber ;
	public HashMap<String,Integer> triggerType ;
	
	public TriggerTemplate(HashMap<String,int[]> triggerNumber )
	{
		
		this.triggerNumber = triggerNumber;
		this.setTemplate(triggerNumber);
	}
	public void setTemplate(HashMap<String,int[]> triggerNumber )
	{
		if (triggerNumber == null || triggerNumber.size() == 0)
		{
			System.out.println("There is something null.");
			return;
		}
		this.triggerType = new HashMap<String,Integer>();
		for (Entry<String, int[]> entry : triggerNumber.entrySet()) 
		{			
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    String triggerWord = entry.getKey();
		    int[] numbers = entry.getValue();
		    int maxNumber = 0;
		    int maxType  = 1;
		    for(int i = 0;i<20;i++)
		    {
		    	if(numbers[i]> maxNumber)
		    	{
		    		maxType = i+1;
		    		maxNumber = numbers[i];
		    	}
		    }
		    this.triggerType.put(triggerWord, maxType);
		}
	}
	public void updateTemplate(HashMap<String,int[]> newTriggerNumber)
	{
		if (newTriggerNumber == null || newTriggerNumber.size() == 0)
		{
			System.out.println("There is something null.");
			return;
		}
		for (Entry<String, int[]> entry : newTriggerNumber.entrySet()) 
		{
		    String triggerWord = entry.getKey();
		    int[] numbers = entry.getValue();
		    if (this.triggerNumber.containsKey(triggerWord))
		    {
		    	int[] existedNumbers = this.triggerNumber.get(triggerWord);
		    	for(int i =0;i<20;i++)
		    		existedNumbers[i]+=numbers[i];
		    }
		    else
		    {
		    	this.triggerNumber.put(triggerWord, numbers);
		    }
		}
		this.setTemplate(this.triggerNumber);
	}
	public int getEventType(String triggerWord)
	{
		//before use this function getEventSum ,
		//please make sure that isTriggerWord is ture!!!
		if (triggerWord == null )
		{
			System.out.println("There is something null.");
			return 0;
		}
		int eventType = 0;
		//if (triggerType.containsKey(triggerWord))
		eventType =  triggerType.get(triggerWord);
		return eventType;
	}
	public int getEventSum(String triggerWord)
	{
		//before use this function getEventSum ,
		//please make sure that isTriggerWord is ture!!!
		if (triggerWord == null)
			return 0;
		int eventType = triggerType.get(triggerWord);
		if (eventType == 0)
			return 1;
		return this.triggerNumber.get(triggerWord)[eventType-1];
	}
	public boolean isTriggerWord(String Word)
	{
		if (Word == null)
			return false;
		if (this.triggerType.containsKey(Word))
			return true;
		return false;
	}
	public String toString()
	{
		for (Entry<String, int[]> entry : this.triggerNumber.entrySet())
		{
			System.out.print("Key = " + entry.getKey() + ", Value = ");
			for(int item:entry.getValue())
				System.out.print(item+"   ");
			System.out.println();
		}
		return this.triggerType.toString();
	}
	public static void main(String []args)
	{
		
	}
	
}
