package com.main;

import java.sql.SQLException;
import java.text.ParseException;

import com.event.RunDetection;
import com.event.label.LabelItem;

public class Main {

	public static void main(String[] args) throws SQLException, ParseException {

		RunDetection runDetection=new RunDetection();
		
		String eventStr = "习近平于2016年4月在北京会见奥巴马。";
		//String eventStr = "日本举行近来规模最大抗议集会 要求废除安保法案。";
		
		LabelItem extractResult = runDetection.GetEventInforfromText(eventStr);
		extractResult.Print();
		
	}
	
}
