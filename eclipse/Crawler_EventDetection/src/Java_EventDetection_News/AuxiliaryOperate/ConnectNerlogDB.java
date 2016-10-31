package Java_EventDetection_News.AuxiliaryOperate;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;

public class ConnectNerlogDB {
	public static String mysqlUser = "root";
	public static String mysqlPassword = "root";
	public static String databasePath = "jdbc:mysql://localhost/webnews";
	private Connection connection;
	private String queryString = "";
	private PreparedStatement psmt;
	private ResultSet rs;
	public ConnectNerlogDB(String mysqlUser,String mysqlPassword,String databasePath)
	{
		ConnectNerlogDB.mysqlUser = mysqlUser;
		ConnectNerlogDB.mysqlPassword = mysqlPassword;
		ConnectNerlogDB.databasePath = databasePath;
		try {
			initializeDB();
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ConnectNerlogDB()
	{
		try {
			initializeDB();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void initializeDB() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver load");
		connection = DriverManager.getConnection(databasePath, mysqlUser, mysqlPassword);
		System.out.println("Database connected");
	}
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
	public List<String> readTable(Date currentDatatime) throws ParseException
	{
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> ners = new ArrayList<>();
		queryString = "select * from nerlog";
		try {
			psmt = connection.prepareStatement(queryString);
			rs = psmt.executeQuery(queryString);
			String changeFlag="";
			String changeNer = "";
			Date changeTime;
			while(rs.next())
			{
				changeNer = rs.getString(4);
				changeFlag = rs.getString(5);
				//System.out.println(changeFlag);
				
				
				if(changeFlag.equals("1"))
				{
					changeTime = format.parse(rs.getDate(7).toString()+" "+rs.getTime(7).toString());
					if(changeTime.compareTo(currentDatatime)>0)
					{
						if(changeNer.indexOf(",")!=-1)
						{
							String[] changeNerSplit = changeNer.substring(1, changeNer.length()-1).split(",");
							for(String ner:changeNerSplit)
							{
								ners.add(ner);
							}
						}else
						{
							ners.add(changeNer.substring(1, changeNer.length()-1));
						}
						
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ners;
		
	}
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		ConnectNerlogDB nc = new ConnectNerlogDB("root","123456","jdbc:mysql://114.212.190.58/webnews");
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(date));
		Date currentDate = format.parse(format.format(date));
		System.out.println(currentDate);
		//nc.readTable(currentDate);
	}

}
