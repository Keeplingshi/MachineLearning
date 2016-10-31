/**
 * @author nlp_daij
 * 提供对几个 实体数据库的表进行查找，修改，添加的接口
 */
package Java_EventDetection_News.RoleExtract;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//这是一个用来做为调用数据库接口的类
//提供的功能：
//1、实现查询功能，查找total中是否有指定nername，返回true或false
//2、实现修改功能，修改数据库pi、ci、ri、oi以及total
//3、实现增加功能，并加以判断。判断发生在total中，添加针对于其他表
//4、建立DBtools2，放入公用的一些连接、查询、更新的语句
//5、建立一个快速查询
//6、建立一个查询，根据实体名显示其在数据库中的详细信息，供修改使用

//继续：1、封装成接口
//2、增强表之间的联系


public class Actors_Db_Methods{
	//说明：实现修改数据库中参与者的几个表的一些接口
	//提供快速查询，查询，添加的接口
	
	public String sqlURL = "jdbc:mysql://127.0.0.1:3306/ActorTables";
	public String sqlUser = "dj";
	public String sqlPasswd = "12345678";
	
	static String regioncode=null;
	static String countrycode=null;
	
	static String rolecode=null;
	static String rolecountry=null;
	static String roleregion=null;
	static String roleinfor=null;

	static String date = null;
	static String personid = null;
	static String personcountry=null;
	static String personregion=null;
	static String personinfor=null;
	static String personposition=null;
	static String personrolenull;
	
	public  static ResultSet sn=null;//定义指向total表返回结果集的第一行
	public  static ResultSet sr=null;//指向personinfor\countryinfor\regioninfor\orginfor结果集的第一行

	static PreparedStatement updatener;
	static  PreparedStatement searchregion3 ;
    static PreparedStatement searchcountry3  ;
	static PreparedStatement searchrole3;
	static PreparedStatement searchperson3;
	
	static private Connection connection;
	static PreparedStatement searchner=null;
	static PreparedStatement addner=null;
	static PreparedStatement searchactor=null;
	static PreparedStatement searchregion=null;
	static PreparedStatement searchregion2=null;
	static PreparedStatement searchcountry=null;
	static PreparedStatement searchcountry2=null;
	static PreparedStatement searchrole=null;
	static PreparedStatement searchrole2=null;
	static PreparedStatement searchperson=null;
	static PreparedStatement searchperson2=null;
	
	//查询
	public static void main(String[]args) throws SQLException{
		//例子
		String nername = "中国";
		sn=quicksearchtotal(nername);
		if(sn.next()) {
			System.out.println("total表中有该命名实体存在");
			System.out.println("nername:"+sn.getString(1));
			System.out.println("actorid:"+sn.getString(2));
			System.out.println("relatingtable:"+sn.getString(3));
			System.out.println("totalinfor:"+sn.getString(4));
			System.out.println("other:"+sn.getString(5));
			String ner=sn.getString(1);
			String id=sn.getString(2);
			String table=sn.getString(3);
			String infor=sn.getString(4);
			String other=sn.getString(5);
			if(table.equals("regioninfor")){
	 			System.out.println("该命名实体对应的参与者是region");
	 			sr=searchregion(id);		
			}else if(table.equals("countryinfor"))
			{
				System.out.println("该命名实体对应的参与者是country");
	 			sr=searchcountry(id);		
				
			}else if(table.equals("roleinfor"))
			{
				System.out.println("该命名实体对应的参与者是roleinfor");
	 			sr=searchorg(id);		
				
			}else if(table.equals("personinfor"))
			{
				System.out.println("该命名实体对应的参与者是personinfor");
	 			sr=searchperson(id);		
				
			}
		}
	}
	public  Actors_Db_Methods(String sqlURL,String sqlUser,String sqlPasswd) 
	{
		this.sqlURL = sqlURL;
		this.sqlUser = sqlUser;
		this.sqlPasswd = sqlPasswd;
		linkDatabase();
	}
	public  Actors_Db_Methods() 
	{
		linkDatabase();
	}
	private void closeDatabase() throws SQLException{
		connection.close();
	}
	private void linkDatabase(){
		//System.out.println("wqe");	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(this.sqlURL,this.sqlUser,this.sqlPasswd);
			searchner = (PreparedStatement) connection.prepareStatement("SELECT * FROM totalinfor where nername = ?");
			addner = (PreparedStatement) connection.prepareStatement("INSERT INTO totalinfor VALUES (?,?,?,?,?)");

			searchregion = (PreparedStatement) connection.prepareStatement("SELECT * FROM regioninfor where regionid = ?");
			searchregion2 = (PreparedStatement) connection.prepareStatement("INSERT INTO regioninfor VALUES (?,?,?,?)");
			
			searchcountry = (PreparedStatement) connection.prepareStatement("SELECT * FROM countryinfor where countryid = ?");
			searchcountry2 = (PreparedStatement) connection.prepareStatement("INSERT INTO countryinfor VALUES (?,?,?)");
			
			searchrole = (PreparedStatement) connection.prepareStatement("SELECT * FROM roleinfor where roleid = ?");
			searchrole2 = (PreparedStatement) connection.prepareStatement("INSERT INTO roleinfor VALUES (?,?,?,?,?,?)");
			
			searchperson = (PreparedStatement) connection.prepareStatement("SELECT * FROM personinfor where personid = ?");
			searchperson2 = (PreparedStatement) connection.prepareStatement("INSERT INTO personinfor VALUES (?,?,?,?,?,?,?,?)");
			//修改
			 updatener = (PreparedStatement) connection.prepareStatement("UPDATE totalinfor SET actorid=?,relatingtable=?,totalinfor=?,other=? where nername=?");
			 searchregion3 = (PreparedStatement) connection.prepareStatement("UPDATE regioninfor SET regioncode=? where regionid=?");
			 searchcountry3 = (PreparedStatement) connection.prepareStatement("UPDATE countryinfor SET countrycode=? where countryid=?");
			 searchrole3 = (PreparedStatement) connection.prepareStatement("UPDATE orginfor SET rolecode=?,regionname=?,countryname=?,other=? where roleid=?");
			 searchperson3 = (PreparedStatement) connection.prepareStatement("UPDATE personinfor SET personname=?,countryname=?,regionname=?,rolename=?,position=?,time=?,other=? where personid=?");
			
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Database initial fail");
			e.printStackTrace();
		}

	}
	
	
	public static ResultSet quicksearchtotal(String nername) throws SQLException{
		//利用输入值nername判断是否其在total表中已存在,返回true或者false
		//用静态类成员变量(ResultSet)sn 存放返回的结果集，初始指向结果集第一行的前一行
		//用于快速查询
		ResultSet sn = null;
		searchner.setString(1, nername);
		sn = searchner.executeQuery();
		return sn;
	}
	

	public static void search(String nername) throws SQLException{
		//利用nername判断实体是否在数据表中有相应的参与者词条，没有则返回null，有的话根据返回相应结果集
		//结果集中存放该nername实体对应参与者的所有属性。
		boolean b=false;
		//快速查找totalinfor表
		
	}
		

	public static ResultSet searchregion(String regionid) throws SQLException {
	// TODO Auto-generated method stub

		searchregion.setString(1, regionid);
	    ResultSet s = searchregion.executeQuery();
	    
	    if(!s.next()) {//返回指向结果集这一行
			System.out.println("region表中还没有建立起相应参与者词条");
		}
	return s;
}

	public static ResultSet searchcountry(String countryid) throws SQLException {
		// TODO Auto-generated method stub

		searchcountry.setString(1, countryid);
	    ResultSet s = searchcountry.executeQuery();
	    if(!s.next())
		{
			System.out.println("country表中还没有建立起相应参与者词条");
		}
		return s;
	}
	public static ResultSet searchorg(String roleid) throws SQLException {
		// TODO Auto-generated method stub

		searchrole.setString(1, roleid);
		ResultSet s = searchrole.executeQuery();
		if(!s.next()) 
		{
			System.out.println("org表中还没有建立起相应参与者词条");

		}
		return s;
	}
	public static ResultSet searchperson(String personid) throws SQLException {
		// TODO Auto-generated method stub

		searchperson.setString(1, personid);
		ResultSet s = searchperson.executeQuery();
		if(!s.next())
		{
			System.out.println("person表中还没有建立起相应参与者词条");
		}
		return s;
	}
	
	public static void add(String nername,String actorid,String relatingtable,String totalinfor,String other) throws SQLException, IOException{
		//把实体条添加到total表中
		addner.setString(1, nername);
		addner.setString(2, actorid);
		addner.setString(3, relatingtable);
		addner.setString(4, totalinfor);
		addner.setString(5, other);	
	}
	
	public static  void addperson(String personid,String personname,String countryname,String regionname,String rolename,String position,String time,String other) throws SQLException {
		// TODO Auto-generated method stub
		    searchperson2.setString(1,personid);
			searchperson2.setString(2,personname);
			searchperson2.setString(3,countryname);
			searchperson2.setString(4,regionname);
			searchperson2.setString(5,rolename);
			searchperson2.setString(6,position);
			searchperson2.setString(7,time);
			searchperson2.setString(8,other);
			searchperson2.execute();

	}
	
	public static void addorg(String roleid, String rolename, String rolecode,
			String regionname, String countryname, String other) throws SQLException {
		// TODO Auto-generated method stub
			searchrole2.setString(1,roleid);
			searchrole2.setString(2,rolename);
			searchrole2.setString(3,rolecode);
			searchrole2.setString(4,regionname);
			searchrole2.setString(5,countryname);
			searchrole2.setString(5,other);
			searchrole2.execute();
	}

	public static void addcountry(String countryid,String countryname, String countrycode) throws SQLException {
		// TODO Auto-generated method stub
			searchcountry2.setString(1,countryid);
			searchcountry2.setString(2,countryname);
			searchcountry2.setString(3,countrycode);
			searchcountry2.execute();
	}

	public static void addregion(String regionid, String regionname, String regioncode, String regioninfor) throws IOException, SQLException {
	
			searchregion2.setString(1,regionid);
			searchregion2.setString(2,regionname);
			searchregion2.setString(3,regioncode);
			searchregion2.setString(4,regioninfor);
			searchregion2.execute();
	}

	public static void addtotal(String nername, String actorid,String actortable, String totalinfor, String other ) throws SQLException {
		// TODO Auto-generated method stub
		addner.setString(1, nername);
		addner.setString(2, actorid);
		addner.setString(3, actortable);
		addner.setString(4, totalinfor);
		addner.setString(5, other);
		addner.execute();
	}

}

	