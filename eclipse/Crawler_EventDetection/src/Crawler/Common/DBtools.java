package Crawler.Common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Java_EventDetection_News.Label.LabelItem;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;


public class DBtools {/*	本类用于将爬去的新闻插入数据库		*/
	private Connection connection;
	PreparedStatement pStmt=null;
	PreparedStatement pStmt2=null;
	PreparedStatement pStmt3=null;
	PreparedStatement pwStmt=null;
	PreparedStatement pwStmt2=null;
	PreparedStatement pwStmt3=null;
	PreparedStatement fStmt=null;
	PreparedStatement fStmt2=null;
	PreparedStatement fStmt3=null;
	PreparedStatement xStmt=null;
	PreparedStatement xStmt2=null;
	PreparedStatement xStmt3=null;
	PreparedStatement xwStmt=null;
	PreparedStatement xwStmt2=null;
	PreparedStatement xwStmt3=null;
	PreparedStatement xlStmt=null;
	PreparedStatement xlStmt2=null;
	PreparedStatement xlStmt3=null;
	
	/*
	 * add by qianf
	 */
	PreparedStatement id_xinhua = null;
	PreparedStatement id_xinlang = null;
	PreparedStatement id_xinhua_world = null;
	PreparedStatement id_fenghuang = null;
	PreparedStatement id_huanqiu_china = null;
	PreparedStatement id_huanqiu_world = null;
	/*
	 * add by qianf
	 */
	PreparedStatement delete_xinhua = null;//删除 ==maxid == 'xinhua'的temp中的记录
	PreparedStatement delete_xinlang = null;
	PreparedStatement delete_xinhua_world = null;
	PreparedStatement delete_fenghuang = null;
	PreparedStatement delete_huanqiu_china = null;
	PreparedStatement delete_huanqiu_world = null;
	public DBtools() {
		try {
			linkDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			System.out.println("Database initial fail");
			System.exit(0);
		}
	}
	
	private void linkDatabase() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loaded");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/webnews?characterEncoding=utf-8","root","root");
		pStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `huanqiu_china` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		pStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_china` WHERE `url`=?");
		pStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_china` WHERE `title`=?");
		pwStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `huanqiu_world` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		pwStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_world` WHERE `url`=?");
		pwStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `huanqiu_world` WHERE `title`=?");
		fStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `fenghuang` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		fStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `fenghuang` WHERE `url`=?");
		fStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `fenghuang` WHERE `title`=?");
		xStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `xinhua` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		xStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinhua` WHERE `url`=?");
		xStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinhua` WHERE `title`=?");
		xwStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `xinhua_world` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		xwStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinhua_world` WHERE `url`=?");
		xwStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinhua_world` WHERE `title`=?");
		xlStmt = (PreparedStatement) connection.prepareStatement("INSERT INTO `xinlang` VALUES (null,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		xlStmt2 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinlang` WHERE `url`=?");
		xlStmt3 = (PreparedStatement) connection.prepareStatement("SELECT * FROM `xinlang` WHERE `title`=?");
		
		/*
		 * add by qianf
		 */
		id_xinhua = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `xinhua`");
		id_xinhua_world = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `xinhua_world`");
		id_xinlang = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `xinlang`");
		id_fenghuang = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `fenghuang`");
		id_huanqiu_china = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `huanqiu_china`");
		id_huanqiu_world = (PreparedStatement) connection.prepareStatement("SELECT max(id) FROM `huanqiu_world`");
		/*
		 * add by qianf 
		 */
		delete_xinhua = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
		delete_xinhua_world = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
		delete_xinlang = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
		delete_fenghuang = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
		delete_huanqiu_china = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
		delete_huanqiu_world = (PreparedStatement) connection.prepareStatement("Delete FROM `label_temp_data` where `news_source`=? AND `news_id`=?");
	}
	
	public int insertValuesChina(News n) throws SQLException {/*	插入环球中国新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		pStmt2.setString(1, uniqueUrl);
		ResultSet rs = pStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		pStmt3.setString(1, n.getTitle());
		ResultSet rst = pStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news(title)");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		pStmt.setString(1, n.getTitle());
		pStmt.setString(2, n.getAgent());
		pStmt.setString(3, n.getAuthor());
		pStmt.setString(4, uniqueUrl);
		//Check if existed!
		pStmt.setString(5, n.getContent());
		pStmt.setString(6, n.getPicture());
		pStmt.setString(7, n.getUpdateTime());
		pStmt.setTimestamp(8, tt);	
		pStmt.setInt(9, 0);
		int i = pStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = pStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
		
	}	
	
	/**
	 * 将数据库中的Blob类型转换为java的String
	 * @param t
	 * @throws SQLException
	 * @throws IOException
	 */
	public void selectValuesChina(int t) throws SQLException, IOException {/*	插入环球中国新闻		*/
		pStmt3.setInt(1, t);
		ResultSet rs = pStmt3.executeQuery();
		if(rs.next()) {
			System.out.println("select OK");
			java.sql.Blob contentBlob = rs.getBlob("content");
			BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
			byte [] buf = new byte [contentData.available()];
			contentData.read(buf, 0, buf.length);
			String content = new String(buf, "UTF-8");
			System.out.println(content);
		}
		else {
			System.out.println("select error");
			System.exit(0);
		}
	}
	
	public int insertValuesWorld(News n) throws SQLException {/*	插入环球国际新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		pwStmt2.setString(1, uniqueUrl);
		ResultSet rs = pwStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		pwStmt3.setString(1, n.getTitle());
		ResultSet rst = pwStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news(title)");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		pwStmt.setString(1, n.getTitle());
		pwStmt.setString(2, n.getAgent());
		pwStmt.setString(3, n.getAuthor());
		pwStmt.setString(4, uniqueUrl);
		//Check if existed!
		pwStmt.setString(5, n.getContent());
		pwStmt.setString(6, n.getPicture());
		pwStmt.setString(7, n.getUpdateTime());
		pwStmt.setTimestamp(8, tt);	
		pwStmt.setInt(9, 0);
		int i = pwStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = pwStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
	}	
	
	public void selectValuesWorld(int t) throws SQLException, IOException {
		pwStmt3.setInt(1, t);
		ResultSet rs = pwStmt3.executeQuery();
		if(rs.next()) {
			System.out.println("select OK");
			java.sql.Blob contentBlob = rs.getBlob("content");
			BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
			byte [] buf = new byte [contentData.available()];
			contentData.read(buf, 0, buf.length);
			String content = new String(buf, "UTF-8");
			System.out.println(content);
		}
		else {
			System.out.println("select error");
			System.exit(0);
		}
	}
	
	public int insertValuesfenghuang(News n) throws SQLException {/*	插入凤凰新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		fStmt2.setString(1, uniqueUrl);
		ResultSet rs = fStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		fStmt3.setString(1, n.getTitle());
		ResultSet rst = fStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news(title)");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		fStmt.setString(1, n.getTitle());
		fStmt.setString(2, n.getAgent());
		fStmt.setString(3, n.getAuthor());
		fStmt.setString(4, uniqueUrl);
		//Check if existed!
		fStmt.setString(5, n.getContent());
		fStmt.setString(6, n.getPicture());
		fStmt.setString(7, n.getUpdateTime());
		fStmt.setTimestamp(8, tt);	
		fStmt.setInt(9, 0);
		int i = fStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = fStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
		
	}
	
	public int insertValuesxinhua(News n) throws SQLException {/*	插入新华中国新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		xStmt2.setString(1, uniqueUrl);
		ResultSet rs = xStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		xStmt3.setString(1, n.getTitle());
		ResultSet rst = xStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news(title)");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		xStmt.setString(1, n.getTitle());
		xStmt.setString(2, n.getAgent());
		xStmt.setString(3, n.getAuthor());
		xStmt.setString(4, uniqueUrl);
		//Check if existed!
		xStmt.setString(5, n.getContent());
		xStmt.setString(6, n.getPicture());
		xStmt.setString(7, n.getUpdateTime());
		xStmt.setTimestamp(8, tt);	
		xStmt.setInt(9, 0);
		int i = xStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
			//System.exit(0);
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = xStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
	}
	
	public int insertValuesxinhuaworld(News n) throws SQLException {/*	插入新华国际新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		xwStmt2.setString(1, uniqueUrl);
		ResultSet rs = xwStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		xwStmt3.setString(1, n.getTitle());
		ResultSet rst = xwStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news(title)");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		xwStmt.setString(1, n.getTitle());
		xwStmt.setString(2, n.getAgent());
		xwStmt.setString(3, n.getAuthor());
		xwStmt.setString(4, uniqueUrl);
		//Check if existed!
		xwStmt.setString(5, n.getContent());
		xwStmt.setString(6, n.getPicture());
		xwStmt.setString(7, n.getUpdateTime());
		xwStmt.setTimestamp(8, tt);	
		xwStmt.setInt(9, 0);
		int i = xwStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = xwStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
	}
	
	public int insertValuesxinlang(News n) throws SQLException {/*	插入新浪新闻		*/
		String uniqueUrl = n.getUrl();
		if(uniqueUrl==null||n.getTitle().equalsIgnoreCase("")||n.getTitle()==null
				||n.getContent().equalsIgnoreCase("")||n.getContent()==null){
			System.out.println("empty or no url");
			return -1;
		}
		xlStmt2.setString(1, uniqueUrl);
		ResultSet rs = xlStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		xlStmt3.setString(1, n.getTitle());
		ResultSet rst = xlStmt3.executeQuery();
		if(rst.next()) {
			System.out.println("Ignore repeated news");
			return -1;
		}
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp tt=new java.sql.Timestamp(date.getTime());
		xlStmt.setString(1, n.getTitle());
		xlStmt.setString(2, n.getAgent());
		xlStmt.setString(3, n.getAuthor());
		xlStmt.setString(4, uniqueUrl);
		//Check if existed!
		xlStmt.setString(5, n.getContent());
		xlStmt.setString(6, n.getPicture());
		xlStmt.setString(7, n.getUpdateTime());
		xlStmt.setTimestamp(8, tt);	
		xlStmt.setInt(9, 0);
		int i = xlStmt.executeUpdate();
		if(i<=0) {
			System.out.println("Insert fail!");
			return -1;
		}
		else {
			System.out.println("Insert ok!");
			ResultSet rsTemp = xlStmt.getGeneratedKeys();
			int newsId = -1;
			if(rsTemp.next())
				newsId = rsTemp.getInt(1);
			//LabelItem extractResult = runDetection.GetEventInforfromNews(nInsert.getAgent(), newsID, newsTime, newsTitle);
			return newsId;
			
		}
	}
	
	public void selectValuesxinlang(String t) throws SQLException, IOException {
		xlStmt2.setString(1, t);
		ResultSet rs = xlStmt2.executeQuery();
		if(rs.next()) {
			System.out.println("select OK");
			java.sql.Blob contentBlob = rs.getBlob("content");
			BufferedInputStream contentData = new BufferedInputStream (contentBlob.getBinaryStream());
			byte [] buf = new byte [contentData.available()];
			contentData.read(buf, 0, buf.length);
			String content = new String(buf, "UTF-8");
			System.out.println(content);
		}
		else {
			System.out.println("select error");
			System.exit(0);
		}
	}
	/*
	 * add by qianf 查找id
	 */
	public int selectMaxIdxinhua() throws SQLException
	{
		ResultSet rs = id_xinhua.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
		
	}
	public int selectMaxIdxinhua_world() throws SQLException
	{
		ResultSet rs = id_xinhua_world.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
		
	}
	public int selectMaxIdxinlang() throws SQLException
	{
		ResultSet rs = id_xinlang.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
		
	}
	public int selectMaxIdfenghuang() throws SQLException
	{
		ResultSet rs = id_fenghuang.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
		
	}
	public int selectMaxIdhuanqiu_china() throws SQLException
	{
		ResultSet rs = id_huanqiu_china.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
		
	}
	public int selectMaxIdhuanqiu_world() throws SQLException
	{
		ResultSet rs = id_huanqiu_world.executeQuery();
		if(rs==null) return -1;
		if(rs.next())
		{
			return rs.getInt(1);
		}
		return -1;
	}
	public void deleteExcessXinhua(String news_source,int maxId) throws SQLException
	{
		delete_xinhua.setString(1, news_source);
		delete_xinhua.setInt(2, maxId);
		delete_xinhua.executeUpdate();
	}
	public void deleteExcessXinhua_world(String news_source,int maxId) throws SQLException
	{
		delete_xinhua_world.setString(1, news_source);
		delete_xinhua_world.setInt(2, maxId);
		delete_xinhua_world.executeUpdate();
	}
	public void deleteExcessXinlang(String news_source,int maxId) throws SQLException
	{
		delete_xinlang.setString(1, news_source);
		delete_xinlang.setInt(2, maxId);
		delete_xinlang.executeUpdate();
	}
	public void deleteExcessFenghuang(String news_source,int maxId) throws SQLException
	{
		delete_fenghuang.setString(1, news_source);
		delete_fenghuang.setInt(2, maxId);
		delete_fenghuang.executeUpdate();
	}
	public void deleteExcessHuanqiu_world(String news_source,int maxId) throws SQLException
	{
		delete_huanqiu_world.setString(1, news_source);
		delete_huanqiu_world.setInt(2, maxId);
		delete_huanqiu_world.executeUpdate();
	}
	public void deleteExcessHuanqiu_china(String news_source,int maxId) throws SQLException
	{
		delete_huanqiu_china.setString(1, news_source);
		delete_huanqiu_china.setInt(2, maxId);
		delete_huanqiu_china.executeUpdate();
	}
	
}
