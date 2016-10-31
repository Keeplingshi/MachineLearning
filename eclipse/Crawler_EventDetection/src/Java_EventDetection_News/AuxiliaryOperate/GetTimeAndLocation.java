/*  
* 创建时间：2015年12月20日 下午1:01:27  
* 项目名称：Java_EventDetection_News  
* @author qianf  
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：GetLocation.java  
* 系统信息：Windows Server 2008
* 类说明：  
* 功能描述：
*/
package Java_EventDetection_News.AuxiliaryOperate;

import java.util.List;

import Java_EventDetection_News.Ner.Pair;

public class GetTimeAndLocation {
	final static String[] locations = {"美","中","日","法","德","韩","英","京","沪","澳","俄","朝"};
	/**
	 * 匹配到时间就return
	 * @param nerResult
	 * @return
	 */
	public static String getTime(List<Pair<String,String>> nerResult)
	{
		for(Pair<String,String> nerPair : nerResult)
		{
			if(nerPair.getKey().equals("时间"))
			{
				return nerPair.getValue();
			}
		}
		return null;
	}
	/**
	 * 三种获得地名的方法，按照优先级method1>method2>method3
	 * @param tagResult
	 * @return
	 */
	public static String getLocation(List<Pair<String,String>> tagResult)
	{
		if(getLocationInMethod1(tagResult)!=null)
		{
			return getLocationInMethod1(tagResult);
		}
		if(getLocationInMethod2(tagResult)!=null)
		{
			return getLocationInMethod2(tagResult);
		}
		if(getLocationInMethod3(tagResult)!=null)
		{
			return getLocationInMethod3(tagResult);
		}
		return null;
	}
	/**
	 * 地名的前一个词是人名或则机构名，一般也是要识别的地名
	 * @param tagResult
	 * @return
	 */
	private static String getLocationInMethod2(
			List<Pair<String, String>> tagResult) {//nt/nr + ns + v       -》省略介词了
		// TODO Auto-generated method stub
		int tagId = 1;//从第二个词开始
		for(Pair<String,String> tagPair : tagResult)
		{
			String word = tagPair.getKey();
			String tag = tagPair.getValue();
			if(tag.matches("ns.*"))
			{
				Pair<String, String> preTagPair = tagResult.get(tagId-1);
				//Pair<String, String> nextTagPair = tagResult.get(tagId+1);
				if(preTagPair.getValue().matches("nt.*")||preTagPair.getValue().matches("nr.*"))
				{
					return word;
				}
			}
			tagId++;
		}
		return null;
	}
	/**
	 * 地名作为名称的定语，一般也是要识别的地名。比如“巴勒斯坦的女战士”
	 * @param tagResult
	 * @return
	 */
	private static String getLocationInMethod3(
			List<Pair<String, String>> tagResult) {//ns + n       -》作为定语
		// TODO Auto-generated method stub
		int tagId = 0;
		for(Pair<String,String> tagPair : tagResult)
		{
			String word = tagPair.getKey();
			String tag = tagPair.getValue();
			if(tag.matches("ns.*"))
			{
				if(tagId+1>=tagResult.size()) 
				{
					break;
				}
				//Pair<String, String> preTagPair = tagResult.get(tagId-1);
				Pair<String, String> nextTagPair = tagResult.get(tagId+1);
				if(nextTagPair.getValue().matches("n.*"))
				{
					return word;
				}
			}
			tagId++;
		}
		return null;
	}
	/**
	 * p+{地名    或者    地名省略词}
	 * @param tagResult
	 * @return
	 */
	private static String getLocationInMethod1(List<Pair<String,String>> tagResult)//p+地名
	{
		//String location = null;
		int tagId = 0;
		for(Pair<String,String> tagPair : tagResult)
		{
			String word = tagPair.getKey();
			String tag = tagPair.getValue();
			if(tag.equals("p"))//介词
			{
				if(tagId+1>=tagResult.size()) break;
				Pair<String,String> nextTagPair = tagResult.get(tagId+1);
				if(nextTagPair.getValue().matches("ns.*"))
				{
					word = nextTagPair.getKey();
					return word;
				}else
				{
					for(String loc : locations)
					{
						if(loc.equals(nextTagPair.getKey()))
						{
							word = nextTagPair.getKey();
							return word;
						}
								
					}
				}
			}
			tagId++;
		}
		return null;
	}
}

