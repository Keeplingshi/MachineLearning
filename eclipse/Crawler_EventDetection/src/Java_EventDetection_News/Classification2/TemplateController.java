/*  
* 创建时间：2015年12月4日 下午3:18:46  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：TemplateController.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.Classification2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Java_EventDetection_News.Label.LabelItem;

public class TemplateController 
{
	public HashMap<String,TriggerWord> template;
	public String templateFilePath;
	
	public TemplateController(String templateFile)
	{
		this.templateFilePath = templateFile;
		this.readTriggerList(templateFile);
		
	}
	//从触发词列表文件读取简易分类器
	public void readTriggerList(String templateFile)
	{
		try {
			template = new HashMap<String,TriggerWord>();
			FileReader reader = new FileReader(templateFile);
		    BufferedReader br = new BufferedReader(reader);
		    String str = null;
		    while((str = br.readLine()) != null) 
		    {
		    	String [] items = str.split("\t");
		    	template.put(items[0], new TriggerWord(items[0],Integer.parseInt(items[1])));
		    }
		    br.close();
		    reader.close();
		} catch (FileNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
	//重写触发词列表文件
	public void writeTriggerList(String templateFile)
	{
		
	}
	//从训练数据构造精密分类器
	public void setTemplateDetail(ArrayList<LabelItem> trainingData)
	{
		for (LabelItem label : trainingData)
		{
			String triWord = label.triggerWord;
			if (!this.template.containsKey(triWord))
				this.template.put(triWord, new TriggerWord(triWord,-1));
			
		}
	}
	
}
