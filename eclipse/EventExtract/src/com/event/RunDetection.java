package com.event;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.event.label.LabelItem;
import com.event.ner.NerExtract;
import com.event.ner.Pair;
import com.event.role_extract.Trigger_Role;
import com.event.tools.GetTimeAndLocation;
import com.event.trigger.TriggerController;

/**
 * 抽取文本中事件信息，放入LabelItem
 * 
 * @author chenbin
 *
 */
public class RunDetection {

	public TriggerController templateController;
	public NerExtract ne;
	public Trigger_Role tr;

	/**
	 * 构造函数，初始化命名体识别
	 */
	public RunDetection() {
		ne = new NerExtract();
		tr = new Trigger_Role();
	}

	/**
	 * 载入事件分类模板
	 * 
	 * @templateFile 模板存放的文本文件地址 for Shangd
	 */
	public void LoadTemplateController(String templateFile) {
		this.templateController = new TriggerController(templateFile);
	}

	/**
	 * 从新闻文本中抽取时间 地点实体 对labelresult中的事件元素赋值 result.eventTime
	 * result.eventLocation
	 * NER识别标题中的事件时间和时间地点，未识别到可不填冲，请赋值为null，eg：result.eventTime = null;
	 * 
	 * @newsInput 新闻输入文本
	 * @labelresult 抽取结果临时存储对象
	 * 
	 */
	public void setTimeandLocation(List<Pair<String, String>> nerResult, List<Pair<String, String>> tagResult,
			LabelItem labelresult) {
		/*
		 * 1、只有一个时间、一个地点好办 2、多个时间，选择第一个 3、多个地点，选择1、p+ns 2、nr/nt + ns 3、ns + n
		 */
		// List<Pair<String,String>> nerResult = ne.nerResult(newsInput);
		// List<Pair<String,Integer>> nvResult = ne.getN_V(newsInput);
		// List<Pair<String,String>> tagResult = ne.tagResult(newsInput);
		labelresult.eventTime = GetTimeAndLocation.getTime(nerResult);
		labelresult.eventLocation = GetTimeAndLocation.getLocation(tagResult);

	}

	/**
	 * 触发词词典中没有匹配 从句子中，通过词性标注工具找到最有可能是触发词的动词作为返回
	 */
	public String findTriggerVerb(String newsInput) {
		/*
		 * 1、判断是否有动词，如果没有return null 2、如果只有一个动词，那好办 3、如果有多个动词，默认最后一个
		 */
		List<Pair<String, Integer>> result = ne.getN_V(newsInput);
		List<Pair<String, Integer>> nv = new ArrayList<>();
		for (Pair<String, Integer> re : result) {
			if (re.getValue() == 1)// 动词：1
			{
				nv.add(re);
			}
		}
		String triggerVerb = "";
		if (nv.size() == 0) {
			return triggerVerb;
		}
		triggerVerb = nv.get(nv.size() - 1).getKey();

		return triggerVerb;
	}

	public void setActor(String text, String triggerWord, LabelItem result) {
		String[] temp = new String[2];
		if (text != null || "".equals(text)) {

			try {
				temp = tr.setActor(text, triggerWord);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.sourceActor = temp[0];
		result.targetActor = temp[1];
	}

	/**
	 * 
	 * @param eventStr
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public LabelItem GetEventInforfromText(String eventStr) throws SQLException, ParseException {
		LabelItem result = new LabelItem(eventStr);// 标注结果存储对象
		List<Pair<String, String>> nerResult = ne.nerResult(eventStr);
		List<Pair<String, String>> tagResult = ne.tagResult(eventStr);
		String[] segWords = this.ne.segResult(eventStr).split(" ");// 分词结果

		result.ifEvent = true;
		this.setTimeandLocation(nerResult, tagResult, result);

		String triggerWord = this.templateController.setEventType(segWords, result);
		if (result.eventType == -1) {
			result.ifEvent = false;
		}

		// 设置事件类别，在触发词表中找到对应的触发词
		if (triggerWord == null)// 触发此列表中触发词无一匹配
		{
			triggerWord = this.findTriggerVerb(eventStr);// 词性标注工具找出对应的主动词作为触发词
			result.triggerWord = triggerWord;
		}

		this.setActor(eventStr, triggerWord, result);// 设置事件元素识别的抽取结果
		return result;
	}

}
