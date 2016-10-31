package Crawler.Common;
/*	struct format: 
	  `title` varchar(511) NOT NULL,
	  `agent` varchar(255),
	  `url` varchar(255) NOT NULL,
	  `content` mediumblob,
	  `picture` varchar(10240) DEFAULT NULL,  
	  `updateTime` datetime DEFAULT NULL,
	  `saveTime` datetime DEFAULT NULL,
 */
public class News {/*	获取的新闻规范化，其标准与上面的数据库的创建有关	*/
//	private long id;
	private String title;
	private String agent;
//	private String author;
	private String url;
	private String content;
	private String picture;
	private String updateTime;
	private String saveTime;
	
	public News() {
		
	}
	
	public News(String title, String agent, 
			String url, String content, String picture, String updateTime) {
//		this.id = id;
		this.title = title;
		this.agent = agent;
//		this.author = author;
		this.content = content;
		this.url = url;
		this.picture = picture;
		this.updateTime = updateTime;
		java.util.Date d = new java.util.Date();
		java.sql.Timestamp ts = new java.sql.Timestamp(d.getTime());
		System.out.println(ts.toString());
		
	}
	
	//Setters
	public void setId(long id) {
//		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setAuthor(String author) {
//		this.author = author;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public void setSaveTime(String saveTime) {
		this.saveTime = saveTime;
	}
	
	
	//Getters
	public long getId() {
//		return id;
		return 0;
	}	
	
	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
//		return author;
		return null;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public String getSaveTime() {
		return saveTime;
	}
}
