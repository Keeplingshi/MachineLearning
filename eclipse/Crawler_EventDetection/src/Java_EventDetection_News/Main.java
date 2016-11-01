package Java_EventDetection_News;

import java.sql.SQLException;
import java.text.ParseException;

import Java_EventDetection_News.Label.LabelItem;
import Java_EventDetection_News.Program.RunDetection;

public class Main {

	public static void main(String[] args) throws SQLException, ParseException {

		RunDetection runDetection=new RunDetection();
		
		runDetection.LoadLabelDB("root", "root", "jdbc:mysql://localhost/webnews");
		runDetection.LoadTemplateController("E:/Github/MachineLearning/eclipse/Crawler_EventDetection/src/cue.csv");
		
		String newsSource = "xinhua";
		String newsID = "1";
		String newsTime = "2015年5月1日";
		String newsTitle = "BBC称英国原本可搭纳粹火箭上太空：比美早十年";
		
		LabelItem extractResult = runDetection.GetEventInforfromNews(newsSource, newsID, newsTime, newsTitle);
		extractResult.Print();
	}

}
