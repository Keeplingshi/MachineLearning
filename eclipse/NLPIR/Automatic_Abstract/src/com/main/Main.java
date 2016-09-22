package com.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;

import com.nlpir.NlpirMethod;
import com.word2vec.Word2VEC;
import com.word2vec.WordKmeans;
import com.word2vec.WordKmeans.Classes;

public class Main {
	
	public static void main(String[] args) {

		doNlpir();

	}
	
	/**
	 * 中文分词
	 */
	public static void doNlpir()
	{
		String content="中广网唐山６月１２日消息（记者汤一亮　庄胜春）据中国之声《新闻晚高峰》报道，今天（１２日）上午，公安机关"
				+ "２０１２年缉枪制爆专项行动“统一销毁非法枪爆物品活动”在河北唐山正式启动，１０万余只非法枪支、２５０余吨炸药在全国"
				+ "１５０个城市被统一销毁。黄明：现在我宣布，全国缉枪制爆统一销毁行动开始！随着公安部副部长黄明一声令下，大量仿制式枪"
				+ "以及猎枪、火药枪、气枪在河北唐山钢铁厂被投入炼钢炉。与此同时，在全国各省区市１５０个城市，破案追缴和群众主动上缴的"
				+ "１０万余支非法枪支被集中销毁，在全国各指定场所，２５０余吨炸药被分别销毁。公安部治安局局长刘绍武介绍，这次销毁的非法"
				+ "枪支来源于三个方面。刘绍武：打击破案包括涉黑、涉恶的团伙犯罪、毒品犯罪，还有从境外非法走私的枪支爆炸物。在销毁现场，"
				+ "记者看到了被追缴和上缴的各式各样的枪支。刘绍武：也包括制式枪，有的是军用枪、仿制的制式抢，还有猎枪、私制的火药枪等等。"
				+ "按照我国的枪支管理法，这些都是严厉禁止个人非法持有的。中国是世界上持枪犯罪的犯罪率最低的国家之一。中美联手破获特大跨国"
				+ "走私武器弹药案近日，中美执法部门联手成功破获特大跨国走私武器弹药案，在中国抓获犯罪嫌疑人２３名，缴获各类枪支９３支、"
				+ "子弹５万余发及大量枪支配件。在美国抓获犯罪嫌疑人３名，缴获各类枪支１２支。这是公安部与美国移民海关执法局通过联合"
				+ "调查方式侦破重大跨国案件的又一成功案例。２０１１年８月２５日，上海浦东国际机场海关在对美国纽约发往浙江台州，申报品"
				+ "名为扩音器（音箱）的快件进行查验时，发现货物内藏有手枪９支，枪支配件９件，长枪部件７件。经检验，这些都是具有杀伤力的"
				+ "制式枪支及其配件。这引起了公安部和海关总署的高度重视。公安部刑侦局局长刘安成：因为是从海关进口的货物中检查出来夹带，说明"
				+ "来源地是境外，或是说国外，这应该是一起特大跨国走私武器弹药的案件。上海市公安局和上海海关缉私局成立联合专案组，迅速开展案件侦查。"
				+ "专案组于８月２６日在浙江台州ＵＰＳ取件处将犯罪嫌疑人王挺（男，３２岁，台州市人）抓获。王挺交代，他通过一境外网站上认识了上家林志富，"
				+ "２００９年１１月以来，林志富长期居住美国，他通过互联网组建了一个走私、贩卖、私藏枪支弹药的群体，通过网络在国内寻找枪支弹药买家，"
				+ "并通过美国ＵＰＳ联邦速递公司将枪支弹药从纽约快递给多名类似王挺的中间人，再通过中间人发送给国内买家。此案中，犯罪分子依托虚拟网络进行"
				+ "犯罪交易，隐蔽性强，涉案人员使用的身份、地址、联系方式都是虚构的，侦查难度很大。刘安成说，此案体现了是新型犯罪，特别是现代犯罪的新特点。"
				+ "刘安成：他不受距离的限制、经常是跨国跨境，甚至是跨一个、数个、甚至数十个国家。这种犯罪手法的改变和新型犯罪的特点，要求我们各国警方充分合作。作者：汤一亮　庄胜春";
		
		//获取中文分词结果
		String result=NlpirMethod.NLPIR_ParagraphProcess(content,0);
		//去除分类结果中的标点符号和多个空格
		result=result.replaceAll("[\\pP\\p{Punct}]", "").replaceAll("\\s{1,}", " ");
		//获取句子中的所有词语
		String[] resultArr=result.split(" ");
		
		File fw = null;
		BufferedReader bw=null;
		try {
			fw=new File("D:/Data/vectorsSougouResult.txt");
			bw=new BufferedReader(new InputStreamReader(new FileInputStream(fw),"UTF-8"));
			
			//获取文件中的聚类结果
			String lineTxt=null;
			while((lineTxt = bw.readLine()) != null){
				String[] lineArr=lineTxt.split("\t");
				for(String str:lineArr)
				{
					System.out.println(str);
				}
			}
			
			bw.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
		
		for(String str:resultArr)
		{
			System.out.println(str);
		}
		
		
	}
	
	/**
	 * 词向量处理
	 */
	public static void doword2vec()
	{
	       Word2VEC vec = new Word2VEC();  
	        try {
				vec.loadModel("D:/Data/vectorsSougou.bin");
				//迭代200次，基本可以得到最优结果
				WordKmeans wordKmeans=new WordKmeans(vec.getWordMap(), 50, 200);
				Classes[] explain=wordKmeans.explain();
				
		        File fw = new File("D:/Data/vectorsSougouResult.txt");  
		        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fw), "UTF-8"));  

		        for (int i = 0; i < explain.length; i++) {  
		            List<Entry<String, Double>> result=explain[i].getMember();  
		            StringBuffer buf = new StringBuffer();  
		            for (int j = 0; j < result.size(); j++) {  
		                buf.append(i+"\t"+result.get(j).getKey()+"\t"+result.get(j).getValue().toString()+"\n");  
		            }  
		            bw.write(buf.toString());  
		            bw.flush();
		        }
		        bw.close();  
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
}
