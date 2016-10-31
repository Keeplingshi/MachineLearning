package Java_EventDetection_News.Label;
/*
Created on 2015年10月19日 上午10:40:20

@author: GreatShang
*/

public class LabelItem 
{
	/*
	 * 标注数据
	 * */
	public String labelID;			//数据库中该条标注的ID
	
	public String newsSource;		//新闻来源
	public String newsID;			//新闻ID
	public String newsTitle;		//新闻标题
	public String newsContent;		//新闻正文
	
	public boolean ifEvent = false;	//是否事件相关
	public int eventType = -1;		//事件类型（1-20）
	
	public String sourceActor;		//事件发出者
	public String targetActor;		//事件承受者
	public String triggerWord;		//事件触发词
	
	public String eventTime;		//事件发生时间
	public String eventLocation;	//事件发生地点
	
	public LabelItem(String labelID,String newsSource,String newsID,String newsTitle)
	{
		//与事件无关新闻标题标注构造函数
		this.newsSource = newsSource;
		this.labelID = labelID;
		this.ifEvent = false;
		this.newsID = newsID;
		this.newsTitle = newsTitle;
	}
	
	public LabelItem(String labelID,String newsSource,String newsID,String newsTitle,
			int eventType,String sourceActor,String targetActor,String triggerWord,
			String eventTime,String eventLocation)
	{
		//事件有关新闻标题标注构造函数
		this.newsSource = newsSource;
		this.labelID = labelID;
		this.ifEvent = true;
		this.newsID = newsID;
		this.newsTitle = newsTitle;
		this.eventType = eventType;
		this.sourceActor = sourceActor;
		this.targetActor = targetActor;
		this.triggerWord = triggerWord;
		this.eventTime = eventTime;
		this.eventLocation = eventLocation;
	}
	public void Print()
	{
		System.out.println("labelID "+labelID);
		System.out.println("newsSource "+newsSource);
		System.out.println("ifEvent "+ifEvent);
		System.out.println("newsID "+newsID);
		System.out.println("newsTitle "+newsTitle);
		System.out.println("eventType "+eventType);
		System.out.println("sourceActor "+sourceActor);
		System.out.println("targetActor "+targetActor);
		System.out.println("triggerWord "+triggerWord);
		System.out.println("eventTime "+eventTime);
		System.out.println("eventLocation "+eventLocation);
	}
	public String toDemoString()
	{
		if (this.ifEvent == false)
			return this.newsTitle+
					"\nNot event related.";
		else
			return this.newsTitle+
				"\nEventType: "+this.eventType+
				"\nSource: "+this.sourceActor+
				"\nTrigger:	"+this.triggerWord+
				"\nTarget: "+this.targetActor+
				"\nLocation: "+this.eventLocation+
				"\nTime: "+this.eventTime;
	}
	public String toString()
	{
		return this.labelID+this.newsSource+" "+this.newsID+" " +this.newsTitle;
	}
	
}
