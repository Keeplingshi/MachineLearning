/*  
* 创建时间：2015年11月20日 下午6:40:16  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：RunDB.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
* Get news Title from newsDB.
* Detection event message from these titles,
* and add the results into temp label DB.
*/
/**
 * 
 */
package Java_EventDetection_News.Program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import Java_EventDetection_News.Label.LabelItem;
import Java_EventDetection_News.RoleExtract.News_Actor_Methods;

/**
 * @author shangd
 *
 */
@SuppressWarnings("unused")
public class RunDB 
{
	
	public RunDetection detec ;
	public News_Actor_Methods newsDB;
	
	public String lastNewsID;
	public LinkedList<String> unfinishedNewsID;
	private BufferedReader reader;

	private BufferedWriter writer;
	
	public RunDB(String sqlName,String sqlPasswd,String sqlURL,String templatePath)
	{
		detec = new RunDetection();
		detec.LoadLabelDB(sqlName, sqlPasswd, sqlURL);
		detec.LoadTemplateController(templatePath);
		newsDB = new News_Actor_Methods(sqlURL,sqlName,sqlPasswd);

	}
	/**
	 * Find the last finished newsID.
	 * With the last finished newsID,
	 * get all unfinished newsID.
	 * With every unfinished newsID,
	 * get newsTitle and detect event,
	 * then save the result into temp labelDB.
	 * {"huanqiu_china","huanqiu_world","fenghuang",
	 * "xinhua","xinhua_world","xinlang"};
	 * @throws ParseException 
	 * @throws SQLException 
	 * 
	 */
	public void detectEventDemo(String demoFile,String outputFile) throws SQLException, ParseException
	{
		try {
			reader = new BufferedReader(
					new FileReader(new File(demoFile)));
//			writer = new BufferedWriter(
//					new FileWriter(new File(outputFile)));
			String line = reader.readLine();
			while(line!=null)
			{
				LabelItem extractResult = detec.GetEventInforfromNews
						(null,null,null,line);
//				writer.write(extractResult.toDemoString()+"\n\n");
				System.out.println(extractResult.toDemoString());
				line  = reader.readLine();
			}
			reader.close();
//			writer.flush();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Reading failed.");
			e.printStackTrace();
		}
		
	}
	public void detectEvent(HashMap<String,Integer> numbers) throws ParseException
	{
		
		HashMap<String,ArrayList<String>> existedNewsID =
				detec.labelDB.GetNewsIDTempData();
		if( numbers == null || existedNewsID == null)
		{
			System.out.println("Something null. Detect failed.");
			return;
		}
		String [] newsSourceList = 
			{"huanqiu_china","huanqiu_world","fenghuang","xinhua","xinhua_world","xinlang"};
		int targetNumber=0;
		for(String newsSource:newsSourceList)
		{
			ArrayList<String> curExistedNewsID = existedNewsID.get(newsSource);
			targetNumber = numbers.get(newsSource);
			if (targetNumber <=0)continue;
			//get message from db.
			
			try {
				ArrayList<ArrayList<String>> newsList = 
						newsDB.select_content(newsDB.select_news(newsSource));
				for(ArrayList<String>  newsItem: newsList)
				{
					String newsID = newsItem.get(0);
					if((curExistedNewsID == null || !curExistedNewsID.contains(newsID))  
							&& targetNumber>0)
					{
						String newsTitle = newsItem.get(1);
						String newsTime = newsItem.get(2);
						
						LabelItem extractResult = detec.GetEventInforfromNews
								(newsSource,newsID,newsTime,newsTitle);
						//向临时标注数据库中添加标注数据
						extractResult.Print();
						detec.labelDB.AddLabeltoTempTable(extractResult);
						targetNumber--;
					}
					if (targetNumber<=0)
						break;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			

		}
		detec.labelDB.CloseDB();

		
	}

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws ParseException, InterruptedException 
	{
//		RunDB runing = 
//				new RunDB("root", "123456", "jdbc:mysql://114.212.190.58/webnews","cue.csv");
		HashMap<String,Integer> numbers = new HashMap<String,Integer>();  
		numbers.put("huanqiu_china", 	50);
		numbers.put("huanqiu_world", 	50);
		numbers.put("fenghuang", 		50);
		numbers.put("xinhua", 			50);
		numbers.put("xinhua_world", 	50);
		numbers.put("xinlang",          50);
		long diff = 0;
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = format.format(date);
		int currentHour = Integer.parseInt(currentTime.split("\\s+")[1].split(":")[0]);
		if(currentHour<24&&currentHour>=4)
		{
			String time = currentTime.split("\\s+")[0] + " " + "23:59:59";
			diff = format.parse(time).getTime() - format.parse(currentTime).getTime() + 4*3600*1000;
		}else
		{
			String time = currentTime.split("\\s+")[0] + " " + "04:00:00";
			diff = format.parse(time).getTime() - format.parse(currentTime).getTime();
		}
		Thread.sleep(diff);
		int updateDic = 0;
		Date lastUpdateTime = new Date();
		while(true)
		{
			RunDB runing = 
					new RunDB("root", "root", "jdbc:mysql://localhost/webnews","cue.csv");
			runing.detectEvent(numbers);
			Thread.sleep(24*3600*1000);
			++updateDic;
			if(updateDic == 7)
			{
				updateDic = 0;
				runing.detec.updateDictionary("root", "root", "jdbc:mysql://localhost/webnews",lastUpdateTime);
				lastUpdateTime = new Date();
			}
		}
		
		//runing.detectEventDemo("test.txt", "output.txt");
		
//		update
		
//		runing.detec.updateTemplate();
	}

}
