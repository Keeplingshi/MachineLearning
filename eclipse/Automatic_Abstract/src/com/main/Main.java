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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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
//		String content="中广网唐山６月１２日消息（记者汤一亮　庄胜春）据中国之声《新闻晚高峰》报道，今天（１２日）上午，公安机关"
//				+ "２０１２年缉枪制爆专项行动“统一销毁非法枪爆物品活动”在河北唐山正式启动，１０万余只非法枪支、２５０余吨炸药在全国"
//				+ "１５０个城市被统一销毁。黄明：现在我宣布，全国缉枪制爆统一销毁行动开始！随着公安部副部长黄明一声令下，大量仿制式枪"
//				+ "以及猎枪、火药枪、气枪在河北唐山钢铁厂被投入炼钢炉。与此同时，在全国各省区市１５０个城市，破案追缴和群众主动上缴的"
//				+ "１０万余支非法枪支被集中销毁，在全国各指定场所，２５０余吨炸药被分别销毁。公安部治安局局长刘绍武介绍，这次销毁的非法"
//				+ "枪支来源于三个方面。刘绍武：打击破案包括涉黑、涉恶的团伙犯罪、毒品犯罪，还有从境外非法走私的枪支爆炸物。在销毁现场，"
//				+ "记者看到了被追缴和上缴的各式各样的枪支。刘绍武：也包括制式枪，有的是军用枪、仿制的制式抢，还有猎枪、私制的火药枪等等。"
//				+ "按照我国的枪支管理法，这些都是严厉禁止个人非法持有的。中国是世界上持枪犯罪的犯罪率最低的国家之一。中美联手破获特大跨国"
//				+ "走私武器弹药案近日，中美执法部门联手成功破获特大跨国走私武器弹药案，在中国抓获犯罪嫌疑人２３名，缴获各类枪支９３支、"
//				+ "子弹５万余发及大量枪支配件。在美国抓获犯罪嫌疑人３名，缴获各类枪支１２支。这是公安部与美国移民海关执法局通过联合"
//				+ "调查方式侦破重大跨国案件的又一成功案例。２０１１年８月２５日，上海浦东国际机场海关在对美国纽约发往浙江台州，申报品"
//				+ "名为扩音器（音箱）的快件进行查验时，发现货物内藏有手枪９支，枪支配件９件，长枪部件７件。经检验，这些都是具有杀伤力的"
//				+ "制式枪支及其配件。这引起了公安部和海关总署的高度重视。公安部刑侦局局长刘安成：因为是从海关进口的货物中检查出来夹带，说明"
//				+ "来源地是境外，或是说国外，这应该是一起特大跨国走私武器弹药的案件。上海市公安局和上海海关缉私局成立联合专案组，迅速开展案件侦查。"
//				+ "专案组于８月２６日在浙江台州ＵＰＳ取件处将犯罪嫌疑人王挺（男，３２岁，台州市人）抓获。王挺交代，他通过一境外网站上认识了上家林志富，"
//				+ "２００９年１１月以来，林志富长期居住美国，他通过互联网组建了一个走私、贩卖、私藏枪支弹药的群体，通过网络在国内寻找枪支弹药买家，"
//				+ "并通过美国ＵＰＳ联邦速递公司将枪支弹药从纽约快递给多名类似王挺的中间人，再通过中间人发送给国内买家。此案中，犯罪分子依托虚拟网络进行"
//				+ "犯罪交易，隐蔽性强，涉案人员使用的身份、地址、联系方式都是虚构的，侦查难度很大。刘安成说，此案体现了是新型犯罪，特别是现代犯罪的新特点。"
//				+ "刘安成：他不受距离的限制、经常是跨国跨境，甚至是跨一个、数个、甚至数十个国家。这种犯罪手法的改变和新型犯罪的特点，要求我们各国警方充分合作。作者：汤一亮　庄胜春";
		
//		String content="北京2008年奥运会市场开发计划启动。";
		
		String content="本报记者　张忠德本报通讯员　张艳　苏婧城区“十分钟”从５月中旬开始，在胶南市珠海街道办事处的烟台东社区大舞台上，一台台京剧演出点燃了附近居民的热情，１００多人的演出队伍更是让几千人次的观众大呼过瘾。烟台东社区一位负责人告诉记者：“我们这个大舞台总共投资了６００多万元，可以承接很多大型演出。除了这个中心舞台外，还建设有古香古色的博物馆、村级阅览室、活动室等，每到夏天都会有很多演出。”２０１２年，胶南市继续对现有公共文化服务设施实施提升工程。其中，投资１５０万元对２０处城市社区文化中心实施功能建设完善；加大人民路、石桥大厦、珠海路三处文化广场的文化设施投资力度；在东部水城和灵山卫、北部隐珠和临港开发区、南部大学城、西部老城区等重点区域筹建具备一定演出能力的大型文化广场；加强市文化馆、博物馆、图书馆等公共文化服务设施建设和服务等，基本构建起城区“十分钟公共文化服务圈”。而在十分钟的文化圈里，一些地标性的文化工程也在逐步展开。总投资６亿元的胶南市市民文化中心目前已经通过规划，总占地达到７５亩，建筑面积１１．７万平方米，集会展中心、图书馆、博物馆、档案馆等文化馆所为一体，即将于近期开工。镇村“三公里”城区十分钟文化圈的基本建成，让胶南市将公共文化设施和服务体系建设的重心进一步延伸至基层。２０１２年，胶南市计划投资５１６万元加大基础薄弱村文化设施的建设力度：投资１８０万元加快建设经济开发区、度假区、王台镇等六个镇级特色文化广场，并根据每个特色文化广场的实际情况，配置流动舞台、灯光、音响等文化设施设备；投资５１６万元改扩建１７２处农村文化活动室，为其配置图书橱、ＤＶＤ、桌椅、音响、图书、投影仪、锣鼓等文化设施；采取以点带面、镇扶持的办法，重点培植市１４５个村（社区）特色化规范化文化活动场所，形成镇村“三公里公共文化服务圈”。胶南市在青岛市连续三年投资２９８０万元基础上，连续两年投资１１１６万元用于对镇村文化活动室的改善提升，到目前，全市所有镇街道已建成设施齐全、功能完善的综合文化站，其中一级站４处。全市１０１６个村（社区）全部建有文化活动室，其中高标准的８６０个，达到全市总村数８５％，基本能够满足镇村居民的“三公里文化需求”。传导效应显现２０１１年１０月，胶南市图书馆分馆在灵山卫街道办事处珠山文苑社区建立了第一个分馆，胶南市文广新局工作人员薛凯告诉记者：“可不要小瞧了这个分馆，虽然占地面积仅有１００多平方米，藏书也只有５０００多册，但纳入市图书馆统一目录的这个分馆实现了与胶南市图书馆的互联，村民可以实现统借统还，不必跑那么远的路去借书还书，目前这样的分馆还有一处在规划中。”下一步，为满足群众对电子文化信息的需求，胶南市将采取与党员干部现代远程教育、中小学校园网合作共建的办法，投资近１００万元推进文化信息资源共享工程建设。其中，投资７０万元在胶南市图书馆设立了文化信息资源共享工程支中心，在市级之中心示范带动下，全市建成镇村文化信息资源共享工程基层点９５５个，其中１８个达到规范化站点标准；投资２０万元建成４１个村级公共电子阅览室，２０１２年将大力实施公共电子阅览室建设工程，力争实现１０处镇街道和１９３个村公共电子资源阅览室标准化建设。为将有限的文化资源盘活起来，胶南市还坚持采用“送文化”与“种文化”相结合的方式，不断提升文化创新力。近年来，胶南持续开展了文化下乡和送欢乐下基层活动，每年财政投资３６０万元在全市所有镇（街道）和行政村（社区）开展送戏、送电影下乡活动，确保送戏下乡１５０场、放映公益电影１．２万场。除此之外，扎实推进农村益民书屋、流动文化服务进农村、进社区等文化惠民工程落到实处，保障基层特别是偏远地区群众的基本文化权益。“送文化”的同时，积极引导基层文化团体和文艺骨干“种文化”，按照鼓励发展，重点扶持的原则，着重培植１０个村级演出水平高、人员规模大、装备齐全的优秀庄户剧团，带动全市基层群众文化活动的提升，繁荣农村文化市场。　（来源：大众日报）";
		
        /*正则表达式：句子结束符*/
        String regEx="[。？！?.!]";
        Pattern p =Pattern.compile(regEx);

        /*按照句子结束符分割句子*/
        String[] substrs = p.split(content);
        
        //句子向量
        HashMap<String, float[]> sentenceMap = new HashMap<String, float[]>();  
        
        //获取到每条句子
        for(String sentence:substrs){
    		//获取中文分词结果
    		String result=NlpirMethod.NLPIR_ParagraphProcess(sentence,0);
    		//去除分类结果中的标点符号和多个空格
    		result=result.replaceAll("[\\pP\\p{Punct}]", "").replaceAll("\\s{1,}", " ");
    		//获取句子中的所有词语
    		String[] resultArr=result.split(" ");
    		
    		//句子向量，初始化为0
    		float[] vector=new float[50];
    		for(int i=0;i<vector.length;i++)
    		{
    			vector[i]=0;
    		}
    		//句子中的有效词语数量
    		int num=0;
    		
    		File fw = null;
    		BufferedReader bw=null;
    		try {
    			fw=new File("D:/Data/vectorsSougouResult.txt");
    			bw=new BufferedReader(new InputStreamReader(new FileInputStream(fw),"UTF-8"));
    			
    			//获取文件中的聚类结果
    			String lineTxt=null;
    			while((lineTxt = bw.readLine()) != null){
    				String[] lineArr=lineTxt.split("\t");
    				if(lineArr.length==3){
    					int id=Integer.parseInt(lineArr[0]);
    					String text=lineArr[1].trim();
    					float value=Float.parseFloat(lineArr[2]);
    					
    					//对语句循环，寻找聚类中有效词语
    					for(int i=0;i<resultArr.length;i++)
    					{
    						//如果在语料中存在
    						if(resultArr[i].trim().equals(text))
    						{
    							vector[id]=vector[id]+value;
    							num++;
    						}
    					}
    				}
    			}
    			
    			//归一化处理
    			if(num!=0){
    				for(int i=0;i<vector.length;i++)
    				{
    					vector[i]=vector[i]/num;
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
    		
    		sentenceMap.put(sentence, vector);
        }
		
        //迭代获取到的该文本的句子向量
        Iterator<Entry<String, float[]>> iterator = sentenceMap.entrySet().iterator();
        //全文向量中心
        float[] centerVectors=new float[50];
		for(int i=0;i<centerVectors.length;i++)
		{
			centerVectors[i]=0;
		}
		int len=0;
		//计算全文中心向量
        while (iterator.hasNext()) {
            Entry<String, float[]> next = iterator.next();
            for(int i=0;i<centerVectors.length;i++){
            	centerVectors[i]+=next.getValue()[i];
            }
            len++;
        }
		for(int i=0;i<centerVectors.length;i++)
		{
			centerVectors[i]=centerVectors[i]/len;
		}
		
		List<SentenceResult> resultList=new ArrayList<SentenceResult>();
		//重新给文本句子向量迭代
		iterator = sentenceMap.entrySet().iterator();
		//根据向量到中心向量距离排序
        while (iterator.hasNext()) {
            Entry<String, float[]> next = iterator.next();
            SentenceResult sentenceResult=new SentenceResult(distance(centerVectors,next.getValue()), next.getKey());
            resultList.add(sentenceResult);
        }
        
        /**
         * 根据List<T>中T的属性，给list排序
         */
        Collections.sort(resultList, new Comparator<SentenceResult>(){  
        	  
            /*  
             * int compare(SentenceResult o1, SentenceResult o2) 返回一个基本类型的整型，  
             * 返回负数表示：o1 小于o2，  
             * 返回0 表示：o1和o2相等，  
             * 返回正数表示：o1大于o2。  
             */  
            public int compare(SentenceResult o1, SentenceResult o2) {  
              
                if(o1.getDistance() > o2.getDistance()){  
                    return 1;  
                }  
                if(o1.getDistance() == o2.getDistance()){  
                    return 0;  
                }  
                return -1;  
            }
        });
        
        for(SentenceResult sentenceResult:resultList){
        	System.out.println(sentenceResult.getDistance()+" "+sentenceResult.getSentence());
        }
	}
	
	/**
	 * 计算向量距离
	 * @param center
	 * @param value
	 * @return
	 */
    public static double distance(float[] center, float[] value) {  
        double sum = 0;  
        for (int i = 0; i < value.length; i++) {  
            sum += (center[i] - value[i])*(center[i] - value[i]) ;  
        }
        return sum ;  
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
	
	/**
	 * 句子向量处理结果
	 * @author chenbin
	 *
	 */
	private static class SentenceResult{
		
		private double distance;
		private String sentence;
		
		public SentenceResult(double distance, String sentence) {
			super();
			this.distance = distance;
			this.sentence = sentence;
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public String getSentence() {
			return sentence;
		}

		public void setSentence(String sentence) {
			this.sentence = sentence;
		}
	}
	
}
