 /*  
* 创建时间：2015年12.22 上午11.00 
* 修改时间：2016年01.04 上午10:11 
* 项目名称：Java_EventDetection_News  
*  @author nlp_daij 
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：RunDetection.java  
* 系统信息：Windows Server 2008
* 类说明： 
* 功能描述：从数据库存储新闻的几个表中，提取新闻标题分析，返回分词、命名实体识别、词性标注结果，分别存储到数据库中三个log表中
* 
*/
package Java_EventDetection_News.RoleExtract;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Java_EventDetection_News.Ner.NerExtract;

public class News_Log_Methods{
	private static  Connection connection;
	public static String sqlURL;
	public static String sqlUser ;
	public static String sqlPasswd ;
	static PreparedStatement pStmt[]=new PreparedStatement[6];
	static PreparedStatement nersearch,nerinsert,ner_search_id,possearch,posinsert,pos_search_id,segsearch,seginsert,seg_search_id;
	static String news_tables []= {"huanqiu_china","fenghuang","xinhua","xinhua_world","huanqiu_world","xinlang"};
	static String log_tables []= {"poslog","seglog","nerlog"};

//	public static void main(String[]args) throws SQLException, UnsupportedEncodingException{
//		String sqlurl = "jdbc:mysql://114.212.190.58:3306/webnews";
//		String sqluser = "root";
//		String sqlpasswd = "123456";
//		
//		News_Log_Methods nlm = new News_Log_Methods(sqlurl,sqluser,sqlpasswd);
//		nlm.tolog();
//		
//	}
	
	
	public News_Log_Methods(String sqlURL,String sqlUser,String sqlPasswd) 
	{
		this.sqlURL = sqlURL;
		this.sqlUser = sqlUser;
		this.sqlPasswd = sqlPasswd;
		linkDatabase();
	}
	
	private static void linkDatabase() 
	{
		System.out.println("wqe");	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(sqlURL,sqlUser,sqlPasswd);
			pStmt[0] = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_china`");
			pStmt[1] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `fenghuang`");
			pStmt[2] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinhua`");
			pStmt[3] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinhua_world`");
			pStmt[4] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `huanqiu_world`");
			pStmt[5] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinlang`");
			
			nersearch = (PreparedStatement) connection.prepareStatement("SELECT * FROM `nerlog`");
			ner_search_id = (PreparedStatement) connection.prepareStatement("SELECT * FROM `nerlog` where id =?");
			nerinsert = (PreparedStatement) connection.prepareStatement("insert into  `nerlog` (id,sentence,original_ner,change_ner,changeflag,originaltime,changetime) values(?,?,?,?,?,?,?)");
			
			possearch = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `poslog`");
			posinsert = (PreparedStatement) connection.prepareStatement("insert into  `poslog` (id,sentence,original_pos,change_pos,changeflag,originaltime,changetime) values(?,?,?,?,?,?,?)");
			pos_search_id = (PreparedStatement) connection.prepareStatement("SELECT * FROM `poslog` where id =?");

			segsearch = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `seglog`");
			seginsert = (PreparedStatement) connection.prepareStatement("insert into  `seglog` (id,sentence,original_seg,change_seg,changeflag,originaltime,changetime) values(?,?,?,?,?,?,?)");
			seg_search_id = (PreparedStatement) connection.prepareStatement("SELECT * FROM `seglog` where id =?");
			System.out.println("Database initial success");

		} catch (SQLException e) {
			System.out.println("Database initial fail");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Database initial fail");
			e.printStackTrace();
		}

	}
	
	
	
	/**
	 * 分析从从新闻表中取出的一条数据进行分词、词性标注、命名实体识别，并将结果放入Log表
	 * @param newsSource：新闻表名
	 * @param newsID：新闻ID
	 * @param newsInput：新闻内容
	 * @param newsTime：新闻事件
	 * @throws SQLException
	 * @throws ParseException
	 */
	public static void newstolog(String newsSource,String newsID,String newsInput,String newsTime) throws SQLException, ParseException{
		System.out.println("newsSource:"+newsSource);
	    System.out.println("newsID:"+newsID);
	    System.out.println("newsTitle:"+newsInput);
	    System.out.println("newsTime:"+newsTime);
	    String date = newsTime;
		NerExtract ne = new NerExtract();
		String pos = null;
		String seg = null;
		String ner = null;
		
		pos = ne.tagResult(newsInput).toString();	
		seg = ne.segResult(newsInput).toString();
		ner =ne.nerResult(newsInput).toString();
	    newsID = newsSource+"_"+newsID;
	    
	    ner_search_id.setString(1,newsID);
		if(!ner_search_id.executeQuery().next()){
			nerinsert.setString(1,newsID);
			nerinsert.setString(2,newsInput);
			nerinsert.setString(3,ner);
			nerinsert.setString(4,"");
			nerinsert.setString (5, "0"); 	      
			nerinsert.setString(6,date);
			nerinsert.setString(7,date);
			nerinsert.execute();	
		}
		
		pos_search_id.setString(1,newsID);
		if(!pos_search_id.executeQuery().next()){
			posinsert.setString (1, newsID);
			posinsert.setString (2,newsInput);
			posinsert.setString (3,pos);
			posinsert.setString(4,"");
			posinsert.setString (5, "0"); 	      
			posinsert.setString(6,date);
			posinsert.setString(7,date);
			posinsert.execute();
		}
		
		seg_search_id.setString(1,newsID);
		if(!seg_search_id.executeQuery().next()){
			seginsert.setString (1, newsID);
			seginsert.setString (2,newsInput);
			seginsert.setString (3,seg);
			seginsert.setString(4,"");
			seginsert.setString (5, "0"); 	      
			seginsert.setString(6,date);
			seginsert.setString(7,date);	      
			seginsert.execute();
		}
		
	}
}
