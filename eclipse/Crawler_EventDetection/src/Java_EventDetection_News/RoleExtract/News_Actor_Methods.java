package Java_EventDetection_News.RoleExtract;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * 提供从数据库存储新闻的几个表中提取新闻标题及新闻的时间的接口
 * @author nlp_daij
 *
 */
public class News_Actor_Methods {
	private Connection connection;
	public String sqlURL = "jdbc:mysql://localhost:3306/webnews";
	public String sqlUser = "root";
	public String sqlPasswd = "123456";
	PreparedStatement pStmt[]=new PreparedStatement[6];
	String news_tables []= {"huanqiu_china","huanqiu_world","fenghuang","xinhua","xinhua_world","xinlang"};

	public News_Actor_Methods(String sqlURL,String sqlUser,String sqlPasswd) 
	{
		this.sqlURL = sqlURL;
		this.sqlUser = sqlUser;
		this.sqlPasswd = sqlPasswd;
		linkDatabase();
	}
	
	private void linkDatabase() 
	{
		System.out.println("wqe");	
		try {
			Class.forName("com.mysql.jdbc.Driver");
//			System.out.println("Driver loaded");
			connection = DriverManager.getConnection(this.sqlURL,this.sqlUser,this.sqlPasswd);
			pStmt[0] = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_china`");
			pStmt[1] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `huanqiu_world`");
			pStmt[2] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `fenghuang`");
			pStmt[3] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinhua`");
			pStmt[4] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinhua_world`");
			pStmt[5] = (PreparedStatement) connection.prepareStatement("SELECT * FROM  `xinlang`");
		} catch (SQLException e) {
			System.out.println("Database initial fail");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Database initial fail");
			e.printStackTrace();
		}

	}
	/**
	 * 
	 * @param newstable要抽取许数据的表名称：{"huanqiu_china","huanqiu_world","fenghuang","xinhua","xinhua_world","xinlang"}
	 * @return返回查找到的数据集
	 * @throws SQLException
	 */
	public ResultSet select_news(String newstable) throws SQLException {
		ResultSet rs = null;
		System.out.println("查询的新闻表："+newstable);
		for(int i=0;i<6;i++){
			if(newstable.equals(news_tables[i])){
				rs = pStmt[i].executeQuery();
				break;
			}			
		}
		return rs;		
	}
	/**
	 * 
	 * @param rs：查询数据库中新闻表后返回的结果集
	 * @return 返回结果集中的新闻标题和更新时间
	 * @throws SQLException
	 */
	public ArrayList<ArrayList<String>> select_content(ResultSet rs) throws SQLException {
		ArrayList<ArrayList<String>> content=  new ArrayList<ArrayList<String>>();
		while(rs.next()){
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(rs.getString("id"));
			temp.add(rs.getString("title"));
			temp.add(rs.getString("updateTime"));
			content.add(temp);
		}
		return content;
	}
	
	
}
