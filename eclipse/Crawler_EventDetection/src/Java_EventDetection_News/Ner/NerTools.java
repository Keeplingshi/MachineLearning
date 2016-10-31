
package Java_EventDetection_News.Ner;
import java.util.ArrayList;
import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

/**
  * 创建时间：2015年9月1日  
  * 项目名称：Java_EventDetection_News  
  * @author fangqian  
  * @version 1.0   
  * @since JDK 1.6  
  * 文件名称：NerTools.java  
  * 系统信息：Win7
  * 类说明： 	
  * 功能描述： 封装hanlp工具中的分词、词性标注、命名实体识别这三个功能
  */


public class NerTools{
	/**封装Hanlp分词功能
	  * @param text 	待分词的字符串
	  * @return segResult 	分词结果
	  */
	public String seg(String text) {
		// TODO Auto-generated method stub
		HanLP.Config.ShowTermNature = false;//不需要词性
		Segment segment = HanLP.newSegment();//hanlp分词
		List<Term> list = segment.seg(text);
		String segResult="";
		for(Term t:list)
		{
			segResult = segResult+t.word+" ";
		}
		return segResult;
	}
	/**封装Hanlp词性标注功能
	  * @param text 待词性标注的字符串
	  * @return re 词性标注结果               example:[在=p, 冲绳=nsf, 大战=n, 当中=f, ，=w, 有=vyou, 一场=mq, 战斗=vn]
	  */
	public List<Pair<String,String>> posTagging(String text) {
		// TODO Auto-generated method stub
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true).enableNumberQuantifierRecognize(true).enableJapaneseNameRecognize(false);
		List<Term> list = segment.seg(text);
		List<Pair<String, String>> re = new ArrayList<Pair<String,String>>();
		for(Term term:list)
		{
			re.add(new Pair<String, String>(term.word, term.nature.toString()));
		}
		return re;
	}
	/**封装Hanlp Ner功能
	  * @param text 待命名实体识别的字符串
	  * @return re 识别结果                example:[地名=冲绳, 地名=日本, 时间=1945年, 机构名=第62师团, 地名=中国, 地名=山西省, 地名=冲绳]
	  */
	public List<Pair<String,String>> ner(String text) {
		// TODO Auto-generated method stub
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true).enableNumberQuantifierRecognize(true).enableJapaneseNameRecognize(false);
		List<Term> list = segment.seg(text);
		List<Pair<String,String>> re = new ArrayList<Pair<String,String>>();
		int termId = 0;
		for(termId = 0;termId<list.size();termId++)
		{
			Term term = list.get(termId);
			if(term.nature.toString().matches("nr.*"))
			{
				re.add(new Pair<String, String>("人名", term.word));
			}else 
				if(term.nature.toString().matches("ns.*"))
				{
					re.add(new Pair<String, String>("地名", term.word));
				}else
					if(term.nature.toString().matches("nt.*"))
					{
						re.add(new Pair<String, String>("机构名", term.word));
					}else
						if((term.nature.toString().matches("mq")&&(term.word.matches("\\d{4}年")||list.get(termId).word.matches(".*月")))||term.nature.toString().matches("t"))
						{
							if(term.nature.toString().matches("t"))
								re.add(new Pair<String, String>("时间", term.word));
							else
							{
								String time = term.word;
								if(termId>=list.size()-1)//以防此种情况“1992年，乌兹别克斯坦庆祝独立2444年”-》2444年作为最后一个词
								{
									re.add(new Pair<String, String>("时间", time));
									break;
								}
								while(list.get(termId+1).nature.toString().matches("mq")&&(list.get(termId+1).word.matches("\\d{4}年")||list.get(termId+1).word.matches(".*月")||list.get(termId+1).word.matches(".*日")||list.get(termId+1).word.matches(".*号")))
								{
									time += list.get(termId+1).word;
									++termId;
									if(termId>=list.size()-1) break;
								}
								re.add(new Pair<String, String>("时间", time));
							}
							
						}
		}
		return re;
	}
	
}
