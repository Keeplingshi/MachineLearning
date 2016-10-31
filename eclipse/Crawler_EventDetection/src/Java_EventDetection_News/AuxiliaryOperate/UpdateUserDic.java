/*  
* 创建时间：2015年12月23日 上午9:28:26  
* 项目名称：Java_EventDetection_News  
* @author GreatShang  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：UpdateUserDic.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.AuxiliaryOperate;

import java.io.*;
import java.util.Properties;

/**
 * 
 * @author qianf
 *
 */
public class UpdateUserDic {
	final static String fileBin = "/data/dictionary/custom/CustomDictionary.txt.bin";
	final static String personPath = "/data/dictionary/custom/军事新闻人名辞典.txt";
	final static String locationPath = "/data/dictionary/custom/军事新闻地名辞典.txt";
	final static String organizationPath = "/data/dictionary/custom/军事新闻机构名辞典.txt";
	private static String root = "";
	static
	{
		Properties p = new Properties();
		try {
			p.load(new InputStreamReader(new FileInputStream("src/hanlp.properties"), "utf-8"));
			root = p.getProperty("root");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void updateUserDic(int updateObjectId,String word)
	{
		if(updateObjectId == 1)
		{
			update(root+personPath,word);
		}
		if(updateObjectId == 2)
		{
			update(root+locationPath,word);
		}
		if(updateObjectId == 3)
		{
			update(root+organizationPath,word);
		}
		
	}
	public static void update(String path,String word)
	{
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true)));
			String lineInDic = "";
			while((lineInDic=br.readLine())!=null)
			{
				int splitWithBlankId = lineInDic.trim().indexOf("\\s+");
				String wordInDic = "";
				if(splitWithBlankId==-1) 
					wordInDic = lineInDic;
				else
					wordInDic = lineInDic.substring(0, splitWithBlankId);
				if(wordInDic.equals(word))
				{
					System.out.println("词典中已有此单词");
					return;
				}
			}
			bw.write(word+"\n");
			System.out.println("单词已添加");
			//System.out.println(root+fileBin);
			File file = new File(root+fileBin);
			if(!file.exists())
			{
				System.out.println("bin文件不存在");
			}else
			{
				System.out.println("bin文件已删除");
				file.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			UpdateUserDic.updateUserDic(1, "钱昉5");
		
	}
}
