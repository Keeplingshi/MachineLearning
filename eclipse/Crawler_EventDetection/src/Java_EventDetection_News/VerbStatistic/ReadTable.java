package Java_EventDetection_News.VerbStatistic;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


class Node
{
	Map<String,List<Integer>> myMap = null;
	Node()
	{
		myMap = new HashMap<>();
	}
}
class Pair<A,B>
{
	A firstItem;
	B secondItem;
	public Pair(A firstItem,B secondItem)
	{
		this.firstItem = firstItem;
		this.secondItem = secondItem;
	}
}
/**  
 * 创建时间：2015年10月12日  
 * 项目名称：Java_EventDetection_News  
 * @author fangqian  
 * @version 1.0   
 * @since JDK 1.6  
 * 文件名称：ReadTable.java  
 * 系统信息：Win7
 * 类说明： 	
 * 功能描述：1、读取event.xlsx(已经标好触发词以及其类别的标题),2、统计每一类出现的触发词以及个数，3、统计每个触发词所对应的类别，以及相应的标题
 */
public class ReadTable {
	XSSFWorkbook readwb =null;
	Map<String,Integer> docNumOfClass = null;//每一类对应的文档数目
	Node[] twWordNumofClass= null;//每一类的触发词以及触发词所对应的标题ID
	Map<String,Integer> triggerWordNum = null;//触发词的数目 如“冒号：100次”
	List<Pair<String,List<Integer>>> trigger = null;//如“冒号：20,30”
	public ReadTable()
	{
		docNumOfClass = new HashMap<>();
		twWordNumofClass = new Node[22];
		for(int i=0;i<22;i++)
		{
			twWordNumofClass[i] = new Node();
		}
		triggerWordNum = new HashMap<>();
	}
	
	/**读取event.xlsx
	  * 统计每一类的触发词以及触发词所对应的标题ID到twWordNumofClass, 如“[{"举行",[1,3,4,6]},{"访问",[3,4,6,7]}]”
	  * 统计触发词的数目到triggerWordNum 如“[“举行”,“100”]”
	  * 统计每一类对应的标题个数到docNumOfClass 如["第一类","100"]
	  */
	
	public void read() throws  IOException
	{
		InputStream is = new FileInputStream(new File("event.xlsx"));//读取event.xlsx
		readwb = new XSSFWorkbook(is);
		XSSFSheet sheet = readwb.getSheet("Sheet1");//读取event.xlsx的sheet1表
		
		int rsRow = sheet.getLastRowNum();//行数
		//System.out.println(rsRow);
		
		for(int i=1;i<=rsRow;i++)
		{
			XSSFRow xr = sheet.getRow(i);
			if(xr!=null)
			{
				int rsColumn = xr.getLastCellNum();
				String key1="",key2="",key3="";
				if(xr.getCell(1)!=null) key1 = xr.getCell(1).getRawValue();//22类
				String value1="",value2="",value3="";
				if(xr.getCell(1)!=null&&xr.getCell(2)!=null)
				{
					key1 = xr.getCell(1).getRawValue();
					value1 = xr.getCell(2).toString();
					if(!twWordNumofClass[Integer.parseInt(key1)-1].myMap.containsKey(value1))
					{
						List<Integer> titleId = new ArrayList<>();
						titleId.add(i);
						twWordNumofClass[Integer.parseInt(key1)-1].myMap.put(value1, titleId);
					}
					else
						twWordNumofClass[Integer.parseInt(key1)-1].myMap.get(value1).add(i);
					if(!triggerWordNum.containsKey(value1))
					{
						triggerWordNum.put(value1, 1);
					}else
						triggerWordNum.put(value1, triggerWordNum.get(value1)+1);
				}
				if(xr.getCell(3)!=null&&xr.getCell(4)!=null)
				{
					key2 = xr.getCell(3).getRawValue();
					value2 = xr.getCell(4).toString();
					if(twWordNumofClass[Integer.parseInt(key2)-1].myMap.get(value2)==null)
					{
						List<Integer> titleId = new ArrayList<>();
						titleId.add(i);
						twWordNumofClass[Integer.parseInt(key2)-1].myMap.put(value2, titleId);
					}
					else
						twWordNumofClass[Integer.parseInt(key2)-1].myMap.get(value2).add(i);
					if(!triggerWordNum.containsKey(value2))
					{
						triggerWordNum.put(value2, 1);
					}else
						triggerWordNum.put(value2, triggerWordNum.get(value2)+1);
				}
				if(xr.getCell(5)!=null&&xr.getCell(6)!=null)
				{
					key3 = xr.getCell(5).getRawValue();
					value3 = xr.getCell(6).toString();
					if(twWordNumofClass[Integer.parseInt(key3)-1].myMap.get(value3)==null)
					{
						List<Integer> titleId = new ArrayList<>();
						titleId.add(i);
						twWordNumofClass[Integer.parseInt(key3)-1].myMap.put(value3, titleId);
					}
					else
						twWordNumofClass[Integer.parseInt(key3)-1].myMap.get(value3).add(i);
					if(!triggerWordNum.containsKey(value3))
					{
						triggerWordNum.put(value3, 1);
					}else
						triggerWordNum.put(value3, triggerWordNum.get(value3)+1);
				}
				if(key1!=null)
				{
					if(docNumOfClass.get(key1)==null)
					{
						docNumOfClass.put(key1, 1);
					}
					else
						docNumOfClass.put(key1, docNumOfClass.get(key1)+1);
					if(key2!=null&&!key2.equals(key1))
					{
						if(docNumOfClass.get(key2)==null)
							docNumOfClass.put(key2, 1);
						else
							docNumOfClass.put(key2, docNumOfClass.get(key2)+1);
					}
					if(key3!=null&&!key3.equals(key2)&&!key3.equals(key1))
					{
						if(docNumOfClass.get(key3)==null)
							docNumOfClass.put(key3, 1);
						else
							docNumOfClass.put(key3, docNumOfClass.get(key3)+1);
					}
				}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
			}
		}
		readwb.close();
	}
	
	/**将twWordNumofClass、docNumOfClass写入到文件summarize.txt中,并且对twWordNumofClass中的触发词出现频率进行排序
	  * @param frontId 指定写入文件的每一类触发词个数
	  * @return 写入文件
	  */
	
	public void eventSummarize(int frontId)
	{
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("summarize.txt")));
			bw.write("类别"+"\t"+"相关文档"+"\t"+"相关触发数目"+"\n");
			Iterator<Entry<String, Integer>> itorDoc = docNumOfClass.entrySet().iterator();
			int classId = 1;
			for(classId = 1;classId<=22;classId++)
			{
				if(docNumOfClass.get(String.valueOf(classId))==null)
				{
					bw.write(classId+"\t"+0+"\t"+0+"\n");
					continue;
				}
				int value = docNumOfClass.get(String.valueOf(classId));
				List<Map.Entry<String, List<Integer>>> temp = new ArrayList<>(twWordNumofClass[classId-1].myMap.entrySet());
				Collections.sort(temp,new Comparator<Entry<String, List<Integer>>>() {
					public int compare(Map.Entry<String, List<Integer>> map1,Map.Entry<String, List<Integer>> map2)
					{
						return map2.getValue().size()-map1.getValue().size();
					}
				});
				String str = "";
				for(int i=0;i<Math.min(frontId, twWordNumofClass[classId-1].myMap.size());i++)
				{
					str = str+temp.get(i).getKey()+":"+temp.get(i).getValue().size()+"       ";
				}
				bw.write(classId+"\t"+value+"\t"+twWordNumofClass[classId-1].myMap.size()+"{"+str+"}"+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**统计triggerWordNum中每个触发词所对应的类别以及标题，存入trigger中
	  * @param frontId 指定触发词中出现次数最多的前frontId触发词，写入文件
	  * @return 写入文件
	  */
	
	public void verbSummarize(int frontID)
	{
		BufferedWriter bw =null;
		List<Map.Entry<String, Integer>> triggerList = new ArrayList<>(triggerWordNum.entrySet());
		Collections.sort(triggerList, new Comparator<Entry<String, Integer>>() {     
			public int compare(Entry<String, Integer> e1,Entry<String, Integer> e2)
			{
				return e2.getValue()-e1.getValue();
			}
		});
		for(int i=0;i<frontID;i++)
		{
			trigger = new ArrayList<>();
			String triggerName = triggerList.get(i).getKey();
			try {
				if(triggerName.equals(":")) /*由于文件名不能以：来命名，所以讲：转为汉字*/
					bw = new BufferedWriter(new FileWriter(new File("第"+(i+1)+"冒号")));
				else
					bw = new BufferedWriter(new FileWriter(new File("第"+(i+1)+triggerName)));
				int allNum = triggerList.get(i).getValue();//触发词出现总次数
				for(int j=0;j<22;j++)/*统计触发词在每个类中出现的次数*/
				{
					if(twWordNumofClass[j].myMap.containsKey(triggerName))
					{
						//System.out.println(j+1);
						trigger.add(new Pair<String, List<Integer>>(String.valueOf(j+1), twWordNumofClass[j].myMap.get(triggerName)));
					}
				}
				Collections.sort(trigger, new Comparator<Pair<String,List<Integer>>>() {
					public int compare(Pair<String,List<Integer>> p1,Pair<String,List<Integer>> p2)
					{
						return p2.secondItem.size()-p1.secondItem.size();
					}
				});
				
				bw.write(triggerName+":"+allNum+"\t");
				for(Pair<String,List<Integer>> itemP : trigger)
				{
					bw.write(itemP.firstItem+"类："+String.valueOf(itemP.secondItem.size())+"\t");
				}
				bw.write("\n");
				InputStream is = new FileInputStream(new File("event.xlsx"));
				readwb = new XSSFWorkbook(is);
				XSSFSheet sheet = readwb.getSheet("Sheet1");
				XSSFRow xr = null;
				for(Pair<String,List<Integer>> p :trigger)
				{
					bw.write(p.firstItem+"\n");
					List<String> lss = new ArrayList<>();
					for(int tid:p.secondItem)
					{
						String str =sheet.getRow(tid).getCell(0).toString();
						lss.add(str);
					}
					for(String str:lss)
					{
						bw.write(str+"\n");
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadTable rt = new ReadTable();
		try {
			rt.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rt.eventSummarize(20);
		rt.verbSummarize(20);
	}

}
