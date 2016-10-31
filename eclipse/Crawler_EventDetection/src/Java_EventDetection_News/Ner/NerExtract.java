
package Java_EventDetection_News.Ner;
import java.util.ArrayList;
import java.util.List;

/**  
  * 创建时间：2015年9月20日  
  * 项目名称：Java_EventDetection_News  
  * @author fangqian  
  * @version 1.0   
  * @since JDK 1.6  
  * 文件名称：NerExtract.java  
  * 系统信息：Win7
  * 类说明： 	
  * 功能描述： 调用NerTools的三个方法seg、posTagging、ner，并且根据词性标注结果识别出text中的名词与动词
  */
public class NerExtract {
	NerTools nertools=null;
	public NerExtract() 
	{
		nertools = new NerTools();
	}
	/**调用NerTools中的分词方法
	  * @param text 待分词的字符串
	  */
	public String segResult(String text)//分词结果
	{
		return nertools.seg(text);
	}
	/**调用NerTools中的命名实体识别方法
	  * @param text 待分词的字符串
	  */
	public List<Java_EventDetection_News.Ner.Pair<String,String>> nerResult(String text)
	{
		return nertools.ner(text);
	}
	/**调用NerTools中的词性标注方法
	  * @param text 待分词的字符串
	  */
	public List<Java_EventDetection_News.Ner.Pair<String,String>> tagResult(String text)
	{
		return nertools.posTagging(text);
	}
	/**根据词性标注结果识别出text中的名词与动词
	  * @param text 字符串
	  * @return re 输出字符串中的名词与动词    example:[在=0, 冲绳=-1, 大战=-1, 当中=0, ，=0, 有=1, 一场=0, 战斗=1]//-1:名词    1:动词    0:其他
	  */
	public List<Pair<String,Integer>> getN_V(String text)
	{
		List<Java_EventDetection_News.Ner.Pair<String,String>> list = nertools.posTagging(text);
		List<Java_EventDetection_News.Ner.Pair<String,Integer>> re = new ArrayList<Java_EventDetection_News.Ner.Pair<String,Integer>>();
		/*
		 * -1:名词      1:动词     0:其他
		 */
		for(Pair<String,String> pairs:list)
		{
			if(pairs.second.matches("n.*"))
			{
				re.add(new Pair<String, Integer>(pairs.first, -1));
			}else
				if(pairs.second.matches("v.*"))
				{
					re.add(new Pair<String, Integer>(pairs.first, 1));
				}else
				{
					re.add(new Pair<String, Integer>(pairs.first, 0));
				}
		}
		return re;
		
	}
	public static void main(String[] args)
	{
		String text = "在冲绳大战当中，有一场战斗被载入军史，那就是嘉数高地反坦克战，太平洋战争美军反攻阶段，日本步兵战果最大的反坦克战。嘉数高地实际上是一个体育场大的地方，高度也不高。1945年4月，据守嘉数高地的日军主要来自第62师团，这个第62师团是从中国山西省调到冲绳的，冲绳日军有2个师团，第62 师团就是其中之一。实际上第62师团，都算不上是野战师团，而是治安师团。平时据守在山西省的几百个据点炮楼里，没有步兵联队编制，也没有炮兵编制。他们 在中国的作战对象，主要是各路游击队。到冲绳，美军的强大立体化攻势和大量先进武器，也算是让第62师团开了眼界。";
		NerExtract ne = new NerExtract();
		System.out.println(ne.segResult(text));//分词结果，输出是String
		System.out.println(ne.nerResult(text));//命名实体抽取结果，输出是List<Pair<String,String>>，如[地名=冲绳, 人名=嘉数, 地名=日本]
		System.out.println(ne.getN_V(text));
		System.out.println(ne.tagResult(text));//词性标注结果，输出是List<Pair<String,String>>，如[在=p, 冲绳=nsf, 大战=n, 当中=f, ，=w, 有=vyou, 一场=mq, 战斗=vn]
	}
}
