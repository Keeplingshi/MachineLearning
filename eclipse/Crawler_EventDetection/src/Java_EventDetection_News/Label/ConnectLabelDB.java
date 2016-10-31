package Java_EventDetection_News.Label;
//package com.Label;
/*
Created on 2015年10月19日 上午10:40:20

@author: GreatShang
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
//import org.apache.tomcat.jdbc.pool.DataSource;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class ConnectLabelDB 
{
	//////sql setting//////
	public static String mysqlUser = "root";
	public static String mysqlPassword = "123456";
	public static String databasePath = "jdbc:mysql://localhost/test";
	
	///////sql local//////
	private Statement psmt;
	private String queryString ;
	private ResultSet rset;
	private Connection connection ;
	
/*数据库建表代码：
CREATE TABLE `label_temp_data` (
  `label_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `news_source` varchar(20) NOT NULL DEFAULT 'fenghuang',
  `news_id` bigint(20) NOT NULL DEFAULT 0,
  `news_title` varchar(80) NOT NULL DEFAULT '',
  `if_event` tinyint(1) NOT NULL DEFAULT 0,
  `source_actor` varchar(50) NOT NULL DEFAULT '',
  `trigger_word` varchar(30) NOT NULL DEFAULT '',
  `target_actor` varchar(50) NOT NULL DEFAULT '',
  `event_type` int(10) NOT NULL DEFAULT 1,
  `event_date` varchar(50) NOT NULL DEFAULT '',
  `event_location` varchar(50) NOT NULL DEFAULT '',
  `mark_time` varchar(50) NOT NULL DEFAULT '',
  `if_remark` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `label_formal_data` (
  `label_id` bigint(20) NOT NULL DEFAULT 0,
  `news_source` varchar(20) NOT NULL DEFAULT 'fenghuang',
  `news_id` bigint(20) NOT NULL DEFAULT 0,
  `news_title` varchar(80) NOT NULL DEFAULT '',
  `if_event` tinyint(1) NOt NULL DEFAULT 0,
  `source_actor` varchar(50) NOT NULL DEFAULT '',
  `trigger_word` varchar(30) NOT NULL DEFAULT '',
  `target_actor` varchar(50) NOT NULL DEFAULT '',
  `event_type` int(10) NOT NULL DEFAULT 1,
  `event_date` varchar(50) NOT NULL DEFAULT '',
  `event_location` varchar(50) NOT NULL DEFAULT '',
  
  `marker_name` varchar(20) NOT NULL DEFAULT '',
  `mark_time` varchar(50) NOT NULL DEFAULT '',
  
  `if_confirmed` tinyint(1) NOt NULL DEFAULT 0,
  `confirmed_name` varchar(20) NOT NULL DEFAULT '',
  `confirmed_time` varchar(50) NOT NULL DEFAULT '',
  
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

*/

	public ConnectLabelDB(Connection connection)
	{
		this.connection = connection;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**构造函数
	 * @param 
	 */
	public ConnectLabelDB()
	{
		try {
			InitializeDB();
		} catch (Exception e) {
			System.out.println("Initialize failed");
			e.printStackTrace();
		}
	}
	/**有目标数据库指向的的构造函数
	 * @param 
	 */
	public ConnectLabelDB(String mysqlUser,String mysqlPassword,String databasePath)
	{
		ConnectLabelDB.mysqlUser = mysqlUser;
		ConnectLabelDB.mysqlPassword = mysqlPassword;
		ConnectLabelDB.databasePath = databasePath;
		try {
			InitializeDB();
		} catch (Exception e) {
			System.out.println("Initialize failed");
			e.printStackTrace();
		}
	}

	/**数据库连接初始化
	 * 
	 */
	public void InitializeDB() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		connection  = DriverManager.getConnection(databasePath,mysqlUser,mysqlPassword);
	}
	/**断开数据库连接函数
	 * 
	 */
	public void CloseDB()
	{
		if(connection != null)
		{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Database close");
		}
	}
	

	/**向临时标注表添加自动化抽取结果
	 * @LabelItem
	 */
	public void AddLabeltoTempTable(LabelItem item)
	{
		try 
		{
			java.util.Date date=new java.util.Date();
			date.getTime();
			if(item.ifEvent)
			{
				queryString =	
				"insert into label_temp_data(news_source,news_id,news_title,if_event,source_actor,trigger_word,target_actor,event_type,event_date,event_location,mark_time)"+
				"VALUES('"+item.newsSource+"','"+item.newsID+"','"+item.newsTitle+"',1,'"+item.sourceActor+"','"+item.triggerWord+"','"+item.targetActor
				+"',"+item.eventType+",'"+item.eventTime+"','"+item.eventLocation+"','"+date.toString()+"')";
				
				psmt = connection.prepareStatement(queryString);
				psmt.executeUpdate(queryString);
				
				
			}
			else
			{
				queryString =
				"insert into label_temp_data(news_source,news_id,news_title,if_event,mark_time)"+
				"VALUES('"+item.newsSource+"','"+item.newsID+"','"+item.newsTitle+"', 0 ,'"+date.toString()+"')"; 
				psmt = connection.prepareStatement(queryString);
				psmt.executeUpdate(queryString);
			}
		}
		catch(MySQLSyntaxErrorException e)
		{
			System.out.println("insert failed");
		}
		catch (SQLException e) 
		{
			System.out.println("Insert failed");
			e.printStackTrace();
		}
	}
	/**向正式表中添加标注结果
	 * 
	 * To Song Yang: 该接口可用
	 * @item 标注系统中，用户标注的结果
	 * @loginUser 标注系统中登录的标注员ID
	 */
	public void AddLabeltoFormalTable(LabelItem item,String loginUser)
	{
		try 
		{
			java.util.Date date=new java.util.Date();
			date.getTime();
			if(item.ifEvent)
			{
				queryString =	
				"INSERT INTO label_formal_data (label_id,news_source,news_id,news_title,if_event,source_actor,trigger_word,target_actor,event_type,event_date,event_location,marker_name,mark_time) "+
				"VALUES("+item.labelID+",'"+item.newsSource+"',"+item.newsID+",'"+item.newsTitle+"',1,'"+item.sourceActor+"','"+item.triggerWord+"','"+item.targetActor
				+"',"+item.eventType+",'"+item.eventTime+"','"+item.eventLocation+"','"+loginUser+"','"+date.toString()+"')";
				psmt = connection.prepareStatement(queryString);
				psmt.executeUpdate(queryString);
			}
			else
			{
				queryString =
				"insert into label_formal_data(label_id,news_source,news_id,news_title,if_event,marker_name,mark_time) values "
				+ "("+item.labelID+",'"+item.newsSource+"',"+item.newsID+",'"+item.newsTitle+"', 0 ,'"+loginUser+"','"+date.toString()+"')"; 
				psmt = connection.prepareStatement(queryString);
				psmt.executeUpdate(queryString);
			}  
		queryString = 
		"UPDATE label_temp_data SET if_remark= 1 "+
		"WHERE label_temp_data.label_id= "+item.labelID;
		psmt = connection.prepareStatement(queryString);
		psmt.executeUpdate(queryString);
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("Add failed.");
		}
	}
	/**正式表中标注确认
	 * @
	 * done
	 */
	public void ConfirmLabel(int labelID, String loginUser)
	{
		java.util.Date date=new java.util.Date();
		date.getTime();
		queryString =	"UPDATE label_formal_data SET if_confirmed=1, confirmed_name = '"+loginUser+"',confirmed_time = '"+date.toString()+"'"+
				"WHERE label_formal_data.label_id= "+labelID;
		try 
		{
			psmt = connection.prepareStatement(queryString);
			psmt.executeUpdate(queryString);

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	/**从临时表中快速获取已经自动标注过的新闻编号
	 * @
	 * done
	 */
	public HashMap<String,ArrayList<String>> GetNewsIDTempData()
	{
		HashMap<String,ArrayList<String>> newsIDMap = new HashMap<String,ArrayList<String>>();
		queryString =	"select news_source,news_id  "+
				"from label_temp_data ";
		try
		{
			psmt = connection.prepareStatement(queryString);
			rset = psmt.executeQuery(queryString);
			String newsSource;
			String newsID;
			while(rset.next())
			{
				newsSource = rset.getString(1);
				newsID = Integer.toString(rset.getInt(2));
				if( !newsIDMap.containsKey(newsSource))
				{
					ArrayList<String> tempList = new ArrayList<>();
					tempList.add(newsID);
					newsIDMap.put(newsSource, tempList);
				}
				else
					newsIDMap.get(newsSource).add(newsID);
			}
		}
		catch (SQLException e) 
		{
			System.out.println("Select error.");
			e.printStackTrace();
		}
		return newsIDMap;
	}
	/**从临时表中获取全部自动标注的数据
	 * To Song Yang: 借口可用，从零时表中获取待标注数据，
	 */
	public ArrayList<LabelItem> GetAllTempData()
	{
		ArrayList<LabelItem>  seachResult = new ArrayList<LabelItem>(); 
		queryString =	"select *  "+
				"from label_temp_data ";
		try 
		{
			psmt = connection.prepareStatement(queryString);
			rset = psmt.executeQuery(queryString);
			int isEvent = 0;
			String newsSource;
			String newsID ;
			String newsTitle ;
			String labelID;
			while(rset.next())
			{				
				labelID = rset.getString(1);
				newsSource = rset.getString(2);
				newsID 	= Integer.toString(rset.getInt(3));
				newsTitle = rset.getString(4);
				isEvent = rset.getInt(5);
				
				if(newsSource == null||newsID == null || newsTitle == null)
					System.out.println("Something null from DB");
				LabelItem label;
				if(isEvent == 0)
					label= new LabelItem(labelID,newsSource,newsID,newsTitle);
				else
				//	public LabelItem(String labelID,String newsSource,String newsID,String newsTitle,int eventType,
				//			String sourceActor,String targetActor,String triggerWord,
				//			String eventTime,String eventLocation)
				{
					label = new LabelItem(labelID,newsSource,newsID, newsTitle, rset.getInt(9),
							rset.getString(6), rset.getString(8), rset.getString(7),
							rset.getString(10), rset.getString(11));
				}
				seachResult.add(label);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return seachResult;
	}
	/**正式表中获取全部标注数据(无论是否标注被管理员确认   均获取)
	 * To Song Yang: 借口可用，从正式表中获取已标注的数据，
	 * 可对比零时表中数据，若在正式标注不存在，可在标注系统中展示给标注者
	 */
	public ArrayList<LabelItem> GetAllTrainingData()
	{
		ArrayList<LabelItem>  seachResult = new ArrayList<LabelItem>(); 
		queryString =	"select *  "+
				"from label_formal_data ";
		try 
		{
			psmt = connection.prepareStatement(queryString);
			rset = psmt.executeQuery(queryString);
			int isEvent = 0;
			String newsSource;
			String newsID ;
			String newsTitle ;
			String labelID;
			while(rset.next())
			{				
				labelID = rset.getString(1);
				newsSource = rset.getString(2);
				newsID 	= Integer.toString(rset.getInt(3));
				newsTitle = rset.getString(4);
				isEvent = rset.getInt(5);
				
				if(newsSource == null||newsID == null || newsTitle == null)
					System.out.println("Something null from DB");
				LabelItem label;
				if(isEvent == 0)
					label= new LabelItem(labelID,newsSource,newsID,newsTitle);
				else
				//	public LabelItem(String labelID,String newsSource,String newsID,String newsTitle,int eventType,
				//			String sourceActor,String targetActor,String triggerWord,
				//			String eventTime,String eventLocation)
				{
					label = new LabelItem(labelID,newsSource,newsID, newsTitle, rset.getInt(9),
							rset.getString(6), rset.getString(8), rset.getString(7),
							rset.getString(10), rset.getString(11));
				}
				seachResult.add(label);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return seachResult;
	}
	/**正式表中获取全部未确认的标注数据
	 * @
	 */
	public ArrayList<LabelItem> GetUnconfirmedData()
	{
		ArrayList<LabelItem>  seachResult = new ArrayList<LabelItem>(); 
		queryString =	"select *  "+
				"from label_formal_data ";
		try 
		{
			psmt = connection.prepareStatement(queryString);
			rset = psmt.executeQuery(queryString);
			int isEvent = 0;
			String newsID ;
			String newsSource;
			String newsTitle ;
			String labelID;
			while(rset.next())
			{
				if (rset.getInt(14) !=0)//已确认的标注跳过
					continue;
				labelID = Integer.toString(rset.getInt(1));
				newsSource = rset.getString(2);
				newsID 	= Integer.toString(rset.getInt(3));
				newsTitle = rset.getString(4);
				
				isEvent = rset.getInt(5);
				if(newsSource == null || newsID == null || newsTitle == null)
					System.out.println("Something null from DB");
				LabelItem label;
				if(isEvent == 0)
					label= new LabelItem(labelID,newsSource,newsID,newsTitle);
				else
				{
					label = new LabelItem(labelID,newsSource,newsID, newsTitle, rset.getInt(9),
							rset.getString(6), rset.getString(8), rset.getString(7),
							rset.getString(10), rset.getString(11));
				}
				seachResult.add(label);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return seachResult;
	}
	public LabelItem GetLabelByNewsID(long news_id,String news_source)
	{
		queryString = "SELECT * FROM label_temp_data where news_id="+news_id+" and news_source=\""+news_source+"\"";
		System.out.println(queryString);
		try{
			psmt = connection.prepareStatement(queryString);
			rset = psmt.executeQuery(queryString);
			int isEvent = 0;
			String newsSource;
			String newsID ;
			String newsTitle ;
			String labelID;
			if(rset.next()){
				labelID = rset.getString(1);
				newsSource = rset.getString(2);
				newsID 	= Integer.toString(rset.getInt(3));
				newsTitle = rset.getString(4);
				isEvent = rset.getInt(5);
				
				if(newsSource == null||newsID == null || newsTitle == null)
					System.out.println("Something null from DB");
				LabelItem label;
				if(isEvent == 0)
					label= new LabelItem(labelID,newsSource,newsID,newsTitle);
				else
				//	public LabelItem(String labelID,String newsSource,String newsID,String newsTitle,int eventType,
				//			String sourceActor,String targetActor,String triggerWord,
				//			String eventTime,String eventLocation)
				{
					label = new LabelItem(labelID,newsSource,newsID, newsTitle, rset.getInt(9),
							rset.getString(6), rset.getString(8), rset.getString(7),
							rset.getString(10), rset.getString(11));
				}
				return label;
			}
		}
		catch(SQLException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String []args)
	{
		ConnectLabelDB test = new ConnectLabelDB("root","123456","jdbc:mysql://localhost/test");
//		LabelItem item1 = new LabelItem("","fenghuang","001","你个大傻子");
//		LabelItem item2 = new LabelItem("","fenghuang","002","你个大傻子2");
//		LabelItem item3 = new LabelItem("","fenghuang","003","你个大傻子3");
//		LabelItem item4 = new LabelItem("","fenghuang","004","昨日以色列又攻击巴勒斯坦了",19,"以色列","巴勒斯坦","攻击","昨日","");
//		LabelItem item5 = new LabelItem("","fenghuang","005","在北海道，韩国和日本干起来了",19,"韩国","日本","干","","北海道");
//		LabelItem item6 = new LabelItem("","fenghuang","005","在北海道，韩国和日本干起来了",19,"韩国","日本","干","","北海道");
//		//LabelItem item7 = new LabelItem("","xinhua","005","在北海道，韩国和日本干起来了",19,"韩国","日本","干","","北海道");
//		//LabelItem item8 = new LabelItem("","xinhua","005","在北海道，韩国和日本干起来了",19,"韩国","日本","干","","北海道");
//		
//		test.AddLabeltoTempTable(item1);
//		test.AddLabeltoTempTable(item2);
//		test.AddLabeltoTempTable(item3);
//		test.AddLabeltoTempTable(item4);
//		test.AddLabeltoTempTable(item5);
//		test.AddLabeltoTempTable(item6);
//		test.AddLabeltoTempTable(item7);
//		test.AddLabeltoTempTable(item8);
		
		HashMap<String,ArrayList<String>> testMap = test.GetNewsIDTempData();
		for(String key:testMap.keySet())
		{
			System.out.println(key);
			for(String item:testMap.get(key))
			{
				System.out.println(key+": "+item);
			}
		}
//		ArrayList<LabelItem> testResult =  test.GetAllTempData();
//		for(LabelItem temp: testResult)
//		{
//			test.AddLabeltoFormalTable(temp, "admin");
//		}
		
	}
}
