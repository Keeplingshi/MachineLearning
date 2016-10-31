package Crawler.Web_new;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.ParseException;

import Java_EventDetection_News.Program.RunDetection;

public class Four_webpage {
	public static RunDetection running = new RunDetection();
	static
	{
		running.LoadLabelDB("root", "root", "jdbc:mysql://localhost/webnews");
		running.LoadTemplateController("src/cue.csv");
	}
	public static xinhua xin=new xinhua();
	public static xinhua_world xin_world=new xinhua_world();
	public static fenghuang feng=new fenghuang();
	public static xinlang xinl=new xinlang();
	public static CrawlerWorld clw=new CrawlerWorld();
	public static CrawlerChina cla=new CrawlerChina();
	
	public static String []xin_url = new String[100];
	public static String []xinworld_url = new String[100];
	public static String []feng_url = new String[100];
	public static String []xinl_url = new String[100];
	
	public static void main(String[] args) throws SQLException, IOException, ParseException {/*主类，循环调用5个爬虫*/
//		if(args.length != 6){
//			System.err.println("Usage: java -jar crawler.jar xinhuaworld_img huanqiu_world_img huanqiu_china_img fenghuang_img xinhua_img xinlang_img");
//			System.exit(0);
//		}
//		xinhua_world.NEW_IMG_PATH = args[0];
//		CrawlerWorld.NEW_IMG_PATH = args[1];
//		CrawlerChina.NEW_IMG_PATH = args[2];
//		fenghuang.NEW_IMG_PATH = args[3];
//		xinhua.NEW_IMG_PATH = args[4];
//		xinlang.NEW_IMG_PATH = args[5];
		
		xinhua_world.NEW_IMG_PATH = "src/xinhuaworld_img";
		CrawlerWorld.NEW_IMG_PATH = "src/huanqiuworld_img";
		CrawlerChina.NEW_IMG_PATH = "src/huanqiuchina_img";
		fenghuang.NEW_IMG_PATH = "src/fenghuang_img";
		xinhua.NEW_IMG_PATH = "src/_xinhua_img";
		xinlang.NEW_IMG_PATH = "src/xinlang_img";
		
		xinhua_world.runDetection = running;
		CrawlerWorld.runDetection = running;
		CrawlerChina.runDetection = running;
		fenghuang.runDetection = running;
		xinhua.runDetection = running;
		xinlang.runDetection = running;
		
		while(true){
			xinhua();	
			xinhua_world();
			fenghuang();
			xinlang();
			
	//		xinl.dbtool.selectValuesxinlang("http://news.sina.com.cn/c/2015-12-16/doc-ifxmttck8107534.shtml");
			try {
				clw.runCrawler();
				cla.runCrawler();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}/**/
System.out.println("wait for 10 min");
			try {
				Thread.sleep(600000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}//暂停1h后程序继续执行	
System.out.println("Let's begin");			
		}
		
	}
	
	public static void xinhua() throws SQLException{/*	新华网 国内新闻	*/
		URL url;
		try {
			 url = new URL("http://qc.wa.news.cn/nodeart/list?nid=1175672&pgnum=1&cnt=35&tp=1&orderby=1?callback=jQuery111305762210746761411_1452849698390&_=1452849698391");

	         HttpURLConnection http = (HttpURLConnection) url.openConnection();
	         http.setDoOutput(true);  
	         http.setDoInput(true);  
	         http.setRequestMethod("GET");  
	         http.connect();  
	        
	         int length = (int) http.getContentLength();
	          BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(),"UTF8"));
	          String line;
	          StringBuffer buffer = new StringBuffer();
	          while ((line = reader.readLine()) != null) {
	              buffer.append(line+"\n");
	          }
	          reader.close();
	          http.disconnect();
	          System.out.println(buffer.toString());
	          
	          String s=buffer.toString();
	          int b=s.indexOf("http");
	          int i=0;
	          while(b>0){
	        	  xin_url[i]=s.substring(b,s.indexOf("htm", b)+3);
	        	  i++;
	        	  b=s.indexOf("http", s.indexOf("htm", b)+3);
	          }
	          
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for(int i=0;i<100&&xin_url[i]!=null;i++){
			try {
				xin.find_information(xin_url[i]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void xinhua_world() throws SQLException, ParseException{/*	新华网 国际新闻	*/
		URL url;
		try {
			url = new URL("http://www.news.cn/world/qqbb.htm");
			URLConnection context =url.openConnection();
			InputStream input=context.getInputStream();
			BufferedReader read=new BufferedReader(new InputStreamReader(input,"UTF-8"));
			
			String s;
			int i=0;
			while((s = read.readLine()) != null){
				if(s.matches("<h3>.*")&&s.matches(".*http.*")){
					xinworld_url[i]=s.substring(s.indexOf("\"")+1,s.indexOf("htm")+3);
					i++;
				}				
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for(int i=0;i<100&&xinworld_url[i]!=null;i++){	
			xin_world.find_information(xinworld_url[i]);
		}
		
	}

	public static void fenghuang() throws SQLException, ParseException{/*	凤凰新闻	*/
		URL url;
		try {
			url = new URL("http://news.ifeng.com/listpage/4550/0/1/rtlist.shtml");
			URLConnection context =url.openConnection();
			InputStream input=context.getInputStream();
			BufferedReader read=new BufferedReader(new InputStreamReader(input,"UTF-8"));
			
			String s;
			int i=0;
			while((s = read.readLine()) != null){
				if(s.matches(".*<h4>.*")&&s.matches(".*shtml.*")){
					feng_url[i]=s.substring(s.indexOf("\"")+1,s.indexOf("shtml")+5);
					i++;
				}				
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for(int i=0;i<100&&feng_url[i]!=null;i++){
			
			feng.find_information(feng_url[i]);
		}

	}
	
	public static void xinlang() throws SQLException, ParseException{/*	新浪网 国内新闻	*/
		URL url;
		try {
			url = new URL("http://roll.mil.news.sina.com.cn/col/zgjq/index.shtml");
			URLConnection context =url.openConnection();
			InputStream input=context.getInputStream();
			BufferedReader read=new BufferedReader(new InputStreamReader(input,"UTF-8"));
			
			String s;
			int i=0;
			while((s = read.readLine()) != null){
				
				if(s.matches(".*http://mil.news.sina.com.cn/china.*")){
					xinl_url[i]=s.substring(s.indexOf("http"),s.indexOf("html")+4);
					i++;
				}				
			}
			
			url = new URL("http://roll.mil.news.sina.com.cn/col/gjjq/index.shtml");
			context =url.openConnection();
			input=context.getInputStream();
			read=new BufferedReader(new InputStreamReader(input,"UTF-8"));
			
			while((s = read.readLine()) != null){
				
				if(s.matches(".*http://mil.news.sina.com.cn/world.*")){
					xinl_url[i]=s.substring(s.indexOf("http"),s.indexOf("html")+4);
					i++;
				}				
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for(int i=0;i<100&&xinl_url[i]!=null;i++){
			xinl.find_information(xinl_url[i]);
//			System.out.println(xinl_url[i]);
		}
		
	}
}
