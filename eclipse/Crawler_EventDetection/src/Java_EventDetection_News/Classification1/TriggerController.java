/*  
 * 创建时间：2015年10月25日 下午10:51:16  
 * 项目名称：Java_EventDetection_News  
 * @author GreatShang  
 * @version 1.0   
 * @since JDK 1.8.0_21  
 * 文件名称：TriggerController.java  
 * 系统信息：Windows Server 2008
 * 类说明：   触发词模板的控制类
 * 功能描述： 模板的读取，修改，更新，以及根据模板进行分类
 */
package Java_EventDetection_News.Classification1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Java_EventDetection_News.Label.LabelItem;

public class TriggerController 
{
	public String templateFilePath;
	public TriggerTemplate template;

//	public TriggerController(HashMap<String,int[]> map)
//	{
//		this.template = new TriggerTemplate(map);
//	}
	public TriggerController(String filePath)
	{
		//this(TriggerController.getMapfromFile(filePath));
		this.templateFilePath = filePath;
		this.template = new TriggerTemplate(TriggerController.getMapfromFile(filePath));
	}
	public TriggerTemplate getTemplate()
	{
		return this.template;
	}
	public String setEventType(String newsID,String[]newsWords,LabelItem result)
	{
		if(result == null || newsWords == null || this.template == null)
		{
			System.out.println("TriggerController.setEventType: There is some thing wrong");
			return null;
		}
		String triggerWord = null;
		int triggerEventType = 0;
		int triggerEventSum = -1;
		
		for(String word:newsWords)
		{
			if (this.template.isTriggerWord(word))
			{
				int tempSum = this.template.getEventSum(word);
				if(tempSum>triggerEventSum)
				{
					triggerEventSum = tempSum;
					triggerEventType = this.template.getEventType(word);
					triggerWord = word;
				}
			}
		}
		if (triggerWord == null)
			return null;
		result.triggerWord = triggerWord;
		result.eventType =triggerEventType;
		return triggerWord;
	}
	public void writeMaptoFile(String filePath)
	{
		HashMap<String,int[]> items = this.template.triggerNumber;
		try {
			FileWriter writer = new FileWriter(filePath);
			BufferedWriter bw  = new BufferedWriter(writer);
			for (Entry<String, int[]> entry : items.entrySet()) 
			{
			    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    StringBuffer line = new StringBuffer(entry.getKey());
			    int[] numbers = entry.getValue();
			    for(int i =0;i<20;i++)
			    {
			    	line.append(",");
			    	line.append(numbers[i]);
			    }
			    line.append("\n");
			    bw.write(line.toString());
			}
			bw.flush();
			bw.close();
			writer.close();
		} catch (IOException e) {
			System.out.println("writing error");
			e.printStackTrace();
		}
		
		
	}
	public void updateTemplate(ArrayList<LabelItem> trainingData)
	{
		if (trainingData == null || trainingData.size()== 0)
		{
			System.out.println("TriggerController.updateTemplate: there is something null.");
		}
		HashMap<String,int[]> map = TriggerController.getTrainingData(trainingData);
		this.template.updateTemplate(map);
		this.writeMaptoFile(this.templateFilePath);
	}
	public static HashMap<String,int[]> getMapfromFile(String filePath)
	{
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		try {
			FileReader reader = new FileReader(filePath);
		    BufferedReader br = new BufferedReader(reader);
		    String str = null;
		    while((str = br.readLine()) != null) 
		    {
//		        System.out.println(str);
		        String[] items = str.split(",");
		        int[] numbers = new int[20];
		        for(int i =0;i<20;i++)
		        	numbers[i] = Integer.parseInt(items[i+1]);
		        map.put(items[0], numbers);
		    }
		    br.close();
		    reader.close();
		} catch (FileNotFoundException e) 
		{
			System.out.println("File not found.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	public static  HashMap<String,int[]> getTrainingData(ArrayList<LabelItem> trainingData)
	{
		if (trainingData == null || trainingData.size()==0)
		{
			System.out.println("there is something null.");
			return null;
		}
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		for(LabelItem trainingItem :trainingData)
		{
			if (trainingItem.ifEvent== false) continue;
			String triggerWord = trainingItem.triggerWord;
			int eventType = trainingItem.eventType;
			if (triggerWord == null || eventType ==0) continue;
			if (map.containsKey(triggerWord))
			{
				map.get(triggerWord)[eventType-1]++;
			}
			else
			{
				int[] newNumbers = new int[20];
				newNumbers[eventType-1]++;
				map.put(triggerWord, newNumbers);
			}
		}
		return map;
	}
	
	public static void main(String[]args)
	{
//		LabelItem item1 = new LabelItem("1","001","你个大傻子");
//		LabelItem item2 = new LabelItem("2","002","你个大傻子2");
//		LabelItem item3 = new LabelItem("3","003","你个大傻子3");
//		LabelItem item4 = new LabelItem("4","004","昨日以色列又攻击巴勒斯坦了",19,"以色列","巴勒斯坦","攻击","昨日","");
//		LabelItem item5 = new LabelItem("5","005","在北海道，韩国和日本干起来了",12,"韩国","日本","干","","北海道");
//		LabelItem item6 = new LabelItem("6","006","在北海道，韩国和日本干起来了",11,"韩国","日本","袭击","","北海道");
//		LabelItem item7 = new LabelItem("7","007","在北海道，韩国和日本干起来了",1,"韩国","日本","打架","","北海道");
//		LabelItem item8 = new LabelItem("8","008","在北海道，韩国和日本干起来了",9,"韩国","日本","打架","","北海道");
//		LabelItem item9 = new LabelItem("9","009","在北海道，韩国和日本干起来了",19,"韩国","日本","干","","北海道");
//		LabelItem item10 = new LabelItem("10","010","在北海道，韩国和日本干起来了",9,"韩国","日本","攻击","","北海道");
//		
//		ArrayList<LabelItem> trainingData = new ArrayList<LabelItem> ();
//		trainingData.add(item1);
//		trainingData.add(item2);
//		trainingData.add(item3);
//		trainingData.add(item4);
//		trainingData.add(item5);
//		trainingData.add(item6);
//		trainingData.add(item7);
//		trainingData.add(item8);
//		trainingData.add(item9);
//		trainingData.add(item10);
//		
//		HashMap<String,int[]> tempMap = TriggerController.getTrainingData(trainingData);
//		//TriggerController testController = new TriggerController(tempMap);
		TriggerController testController = new TriggerController("cue.csv");
		
		System.out.println(testController.template);
//		System.out.println(testController.template.getEventType("干"));
//		System.out.println(testController.template.getEventType("攻击"));
//		System.out.println(testController.template.getEventType("袭击"));
//		//testController.writeMaptoFile("../Data/test.csv");
//		testController.template.updateTemplate(tempMap);
//		System.out.println(testController.template);
//		System.out.println(testController.template.getEventType("干"));
//		System.out.println(testController.template.getEventType("攻击"));
//		System.out.println(testController.template.getEventType("袭击"));
		
//		testController.writeMaptoFile("test.csv");
		
	}
}
