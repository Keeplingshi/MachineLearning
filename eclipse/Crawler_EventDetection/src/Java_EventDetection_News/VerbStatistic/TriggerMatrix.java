package Java_EventDetection_News.VerbStatistic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/** 
 * 创建时间：2015年10月12日  
* 项目名称：Java_EventDetection_News  
* @author fangqian  
* @version 1.0   
* @since JDK 1.6  
* 文件名称：TriggerMatrix.java  
* 系统信息：Win7	
* 功能描述： 生成每一个触发词特征向量，特征为1-19类，特征值为触发词出现的次数
*/
public class TriggerMatrix {

	public void getCueMatrix() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("cue.csv")));
		ReadTable rt = new ReadTable();
		try {
			rt.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, Integer>> triggerList = new ArrayList<>(rt.triggerWordNum.entrySet());
		for(Map.Entry<String, Integer> cue_list : triggerList)
		{
			/*触发词所对应的类别以及标题，如触发词“举行”的trigger是[<"1类",[1,4,5,6]>,<"10类",[28,222,555]>]
			 * 
			 */
			List<Pair<String,List<Integer>>> trigger = new ArrayList<>();
			String triggerName = cue_list.getKey();
			System.out.println(triggerName);
			for(int j=0;j<20;j++)//如果第j类存在该触发词，则将该触发词在j类的信息添加到trigger中
			{
				if(rt.twWordNumofClass[j].myMap.containsKey(triggerName))
				{
					trigger.add(new Pair<String, List<Integer>>(String.valueOf(j+1), rt.twWordNumofClass[j].myMap.get(triggerName)));
				}
			}
			Collections.sort(trigger, new Comparator<Pair<String,List<Integer>>>() {
				public int compare(Pair<String,List<Integer>> p1,Pair<String,List<Integer>> p2)
				{
					return Integer.parseInt(p1.firstItem)- Integer.parseInt(p1.firstItem);
				}
			});
			bw.write(triggerName+",");
				int i=1;
				for(Pair<String,List<Integer>> p:trigger)
				{
					int id = Integer.parseInt(p.firstItem);
					int j;
					for(j=i;j<id;j++)
					{
						bw.write("0"+",");
					}
					bw.write(String.valueOf(p.secondItem.size())+",");;
					
					i = ++j;
				}
				for(int j=i;j<20;j++)
				{
					bw.write("0"+",");
				}
				bw.write("0");
			
			
			bw.write("\n");
		}
		bw.close();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TriggerMatrix tm = new TriggerMatrix();
		try {
			tm.getCueMatrix();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
