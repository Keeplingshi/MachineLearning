/*  
* 创建时间：2015年10月31日 下午1:55:29  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：RunDetection.java  
* 系统信息：Windows Server 2008
* 类说明： 
* 功能描述： 从标题中抽取事件各项信息存入LabelItem
* demo for run detection event.
*/
package Java_EventDetection_News.Program;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.util.exception.LoadModelException;

import java.io.IOException;
import java.sql.SQLException;

import Java_EventDetection_News.AuxiliaryOperate.ConnectNerlogDB;
import Java_EventDetection_News.AuxiliaryOperate.GetTimeAndLocation;
import Java_EventDetection_News.AuxiliaryOperate.UpdateUserDic;
import Java_EventDetection_News.Classification1.TriggerController;
import Java_EventDetection_News.Label.*;
import Java_EventDetection_News.Ner.*;
import Java_EventDetection_News.RoleExtract.Actors_Db_Methods;
import Java_EventDetection_News.RoleExtract.News_Log_Methods;
import Java_EventDetection_News.RoleExtract.Trigger_Role;

public class RunDetection 
{
	
	public TriggerController templateController;
	public ConnectLabelDB labelDB;
	public NerExtract ne;
	public Trigger_Role tr;
	public ConnectNerlogDB nerlogDB;
	public News_Log_Methods nlm;
	public RunDetection()
	{
		ne = new NerExtract();
		String sqlurl = "jdbc:mysql://localhost/webnews";
		String sqluser = "root";
		String sqlpasswd = "root";	
		nlm = new News_Log_Methods(sqlurl,sqluser,sqlpasswd);
		tr =new Trigger_Role();
	}
	/**连接标注数据库
	 * @sqlName mysql数据库用户名
	 * @sqlPassw mysql数据库密码
	 * @sqlURL mysql数据库地址
	 */
	public void LoadLabelDB(String sqlUserName,String sqlUserPassw,String sqlURL)
	{
		this.labelDB = new ConnectLabelDB(sqlUserName,sqlUserPassw,sqlURL);
		//this.labelDB = new ConnectLabelDB();//for debug
		//Trigger_Role.dm = new Actors_Db_Methods(sqlURL,sqlUserName,sqlUserPassw);
	}
	/**载入事件分类模板
	 * @templateFile 模板存放的文本文件地址
	 * for Shangd
	 */
	public void LoadTemplateController(String templateFile)
	{
		this.templateController = new TriggerController(templateFile);
	}
	/**更新模板，使用新的训练数据更新模板
	 * 存储模板的文本文件也会被修改
	 * 
	 */
	public void updateTemplate()
	{
		ArrayList<LabelItem> trainingData = this.labelDB.GetAllTrainingData();
		this.templateController.updateTemplate(trainingData);
	}
	/**从新闻文本中抽取时间 地点实体
	 * 对labelresult中的事件元素赋值
	 * result.eventTime
	 * result.eventLocation
	 * NER识别标题中的事件时间和时间地点，未识别到可不填冲，请赋值为null，eg：result.eventTime = null;
	 * @newsInput 新闻输入文本
	 * @labelresult 抽取结果临时存储对象
	 * 
	 * for QianF
	 */
	public void setTimeandLocation(List<Pair<String,String>> nerResult,List<Pair<String,String>> tagResult,LabelItem labelresult)
	{
		/*
		 * 1、只有一个时间、一个地点好办
		 * 2、多个时间，选择第一个
		 * 3、多个地点，选择1、p+ns  2、nr/nt + ns 3、ns + n
		 */
		//List<Pair<String,String>> nerResult = ne.nerResult(newsInput);
		//List<Pair<String,Integer>> nvResult = ne.getN_V(newsInput);
		//List<Pair<String,String>> tagResult = ne.tagResult(newsInput);
		labelresult.eventTime = GetTimeAndLocation.getTime(nerResult);
		labelresult.eventLocation = GetTimeAndLocation.getLocation(tagResult);
		
	}
	/**定期更新分词，NER词典
	 * 
	 */
	public void updateDictionary(String mysqlUser,String mysqlPassword,String databasePath,Date lastUpdateTime)//暂时只更新用户词典，即更新命名实体
	{
		//TODO:定期更新词典
		this.nerlogDB = new ConnectNerlogDB(mysqlUser, mysqlPassword, databasePath);
		List<String> updateNers = null;
		try {
			updateNers = nerlogDB.readTable(lastUpdateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nerlogDB.CloseDB();
		//UpdateUserDic update = new UpdateUserDic();
		for(String ner : updateNers)
		{
			if(ner.split("=")[0].equals("人名"))
			{
				//ExpandUserDic.
				UpdateUserDic.updateUserDic(1, ner.split("=")[1]);
			}
			if(ner.split("=")[0].equals("地名"))
			{
				//ExpandUserDic.
				UpdateUserDic.updateUserDic(2, ner.split("=")[1]);
			}
			if(ner.split("=")[0].equals("机构名"))
			{
				//ExpandUserDic.
				UpdateUserDic.updateUserDic(3, ner.split("=")[1]);
			}
		}
		
		
		
	}
	/**触发词词典中没有匹配
	 * 从新闻标题中，通过词性标注工具找到最有可能是触发词的动词作为返回
	 * for QianF
	 */
	public String findTriggerVerb(String newsInput)//好难啊！！！！！
	{
		/*
		 * 1、判断是否有动词，如果没有return null
		 * 2、如果只有一个动词，那好办
		 * 3、如果有多个动词，默认最后一个
		 */
		List<Pair<String,Integer>> result = ne.getN_V(newsInput);
		List<Pair<String,Integer>> nv = new ArrayList<>();
		for(Pair<String,Integer> re : result)
		{
			if(re.getValue()==1)//动词：1
			{
				nv.add(re);
			}
		}
		String triggerVerb="";
		if(nv.size()==0)
		{
			return triggerVerb;
		}
		triggerVerb = nv.get(nv.size()-1).getKey();
		
		return triggerVerb;
	}
	/**从新闻文本中抽取参与者
	 * 发出者和承受者
	 * 
	 * @newsInput 新闻输入文本
	 * @triggerWord 触发词
	 * @result 结果对象
	 * result.sourceActor
	 * result.targetActor
	 * 
	 * for DaiJ
	 */

	@SuppressWarnings("static-access")
	public void setActor(String newsSource,String newsID,String newsInput,String newsTime,String triggerWord,LabelItem result)
	{
		String[] temp = new String[2];
		if(newsInput!=null||newsInput.equals("")){
			
			try {
				temp = tr.setActor(newsInput,triggerWord);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LoadModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		result.sourceActor = temp[0];
		result.targetActor = temp[1];
	}
	public LabelItem GetEventInforfromNews(String newsSource,String newsID,String newsTime,String newsTitle) throws SQLException, ParseException//新闻标题数据库ID，数据库中新闻发布时间，新闻标题内容
	{
		LabelItem result  = new LabelItem("",newsSource,newsID,newsTitle);//标注结果存储对象
		List<Pair<String,String>> nerResult = ne.nerResult(newsTitle);
		List<Pair<String,String>> tagResult = ne.tagResult(newsTitle);
		String []segWords = this.ne.segResult(newsTitle).split(" ");//分词结果
		
//		if(QianF.isEventRelated(newsID,newsTitle)==false)return result;//二分类，判断新闻文本是否是军事政治新闻
		
		result.ifEvent = true;
		this.setTimeandLocation(nerResult,tagResult,result);

		if (result.eventTime == null)//未识别到时间，事件时间使用新闻发布时间填充
			result.eventTime = newsTime;
		
		
		String triggerWord = this.templateController.setEventType(newsID,segWords,result);
		if (result.eventType ==-1)
			result.ifEvent = false;
		
		//设置事件类别，在触发词表中找到对应的触发词
		if (triggerWord == null)//触发此列表中触发词无一匹配
		{
			triggerWord = this.findTriggerVerb(newsTitle);//词性标注工具找出对应的主动词作为触发词
			result.triggerWord = triggerWord;
		}
		
		nlm.newstolog(newsSource, newsID, newsTitle, newsTime);//将数据处理结果分别插入到三个Log表中
		this.setActor(newsSource, newsID,newsTitle, newsTime,triggerWord,result);//设置事件元素识别的抽取结果
		return result;
	}
	public static void main(String []args) throws SQLException, ParseException
	{
		RunDetection demoTest = new RunDetection();
		//初始化
		demoTest.LoadLabelDB("root", "123456", "jdbc:mysql://114.212.190.58/webnews");
		demoTest.LoadTemplateController("cue.csv");
//		System.out.println(demoTest.templateController.template.toString());
		//事件元素抽取
		String newsSource = "xinhua";
		String newsID = "1";
		String newsTime = "2015年5月1日";
		
		//test
//		String newsTitle = "BBC称英国原本可搭纳粹火箭上太空：比美早十年";
//		String newsTitle = "加拿大上半年经济衰退 为2008年以来最差一年";
//		String newsTitle = "乌兹别克斯坦庆祝独立24周年";
		String newsTitle = "各国如何纪念反法西斯战争胜利70周年";


		LabelItem extractResult = demoTest.GetEventInforfromNews(newsSource,newsID,newsTime,newsTitle);
		extractResult.Print();	
		
		//向临时标注数据库中添加标注数据
		//demoTest.labelDB.AddLabeltoTempTable(extractResult);
		//网页界面标注员对extractResult进行修正
		//String markerID = "";
		//extractResult.eventType = 1;
		//标注被确认或被修改，修改后的结果被添加进正式标注表
		//demoTest.labelDB.AddLabeltoFormalTable(extractResult,markerID);
		
	}
}
