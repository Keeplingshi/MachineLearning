package com.event.label;
/*
Created on 2015年10月19日 上午10:40:20

@author: chenbin
*/

public class LabelItem 
{
	/*
	 * 标注数据
	 * */
	
	public String text;
	
	public boolean ifEvent = false;	//是否是事件
	public int eventType = -1;		//事件类型（1-20）
	
	public String sourceActor;		//事件发出者
	public String targetActor;		//事件承受者
	public String triggerWord;		//事件触发词
	
	public String eventTime;		//事件发生时间
	public String eventLocation;	//事件发生地点
	
	/**
	 * 构造函数，输入文本
	 * @param text
	 */
	public LabelItem(String text) {
		super();
		
		this.text = text;
	}

	public LabelItem(String text, boolean ifEvent, int eventType, String sourceActor, String targetActor,
			String triggerWord, String eventTime, String eventLocation) {
		super();
		this.text = text;
		this.ifEvent = ifEvent;
		this.eventType = eventType;
		this.sourceActor = sourceActor;
		this.targetActor = targetActor;
		this.triggerWord = triggerWord;
		this.eventTime = eventTime;
		this.eventLocation = eventLocation;
	}

	public String toString()
	{
		return this.text;
	}
	
	public void Print()
	{
		System.out.println("text\t"+text);
		System.out.println("ifEvent\t"+ifEvent);
		System.out.println("eventType\t"+eventType);
		System.out.println("sourceActor\t"+sourceActor);
		System.out.println("targetActor\t"+targetActor);
		System.out.println("triggerWord\t"+triggerWord);
		System.out.println("eventTime\t"+eventTime);
		System.out.println("eventLocation\t"+eventLocation);
	}
}
