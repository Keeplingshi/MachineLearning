 /*  
* 创建时间：2015年12月18日 上午11.00 
* 修改时间：2016年1月4日 上午10:00
* 项目名称：Java_EventDetection_News  
*  @author nlp_daij 
* @version 1.0   
* @since JDK 1.8.0_21  
* 文件名称：RunDetection.java  
* 系统信息：Windows Server 2008
* 类说明： 
* 功能描述：1、根据依存句法分析结果，返回动词排序表
* 		   2、根据句子和触发词，返回事件元素抽取结果
*          3、返回词性转换的函数
* 修改点：动态设置主谓语的个数
*/
package Java_EventDetection_News.RoleExtract;
import java.awt.List;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.util.exception.LoadModelException;

import Java_EventDetection_News.Ner.NerExtract;

public class Trigger_Role{
	public static void main(String[] args) throws IOException, LoadModelException, SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Trigger_Role tg = new Trigger_Role();
		tg.setActor("普京参观俄最新型'阿玛特'坦克 赞'举世无双（组图） 国际频道","赞");	
		//tg.sort_vector("普京参观俄最新型'阿玛特'坦克 赞'举世无双（组图） 国际频道");
	}
	
	/**
	 * 对输入句子调用接口，进行分词、词性识别的预处理
	 * @param newsInput ：新闻标题内容
	 * @return 预处理结果
	 */
	public static String[] dependency_parse(String newsInput){
		NerExtract ne = new NerExtract();
	    if(newsInput.endsWith("频道")){
	    	newsInput = newsInput.substring(0,newsInput.indexOf("频道")-2).trim();
		}
		if(newsInput.contains("（高清组图）")){
			newsInput = newsInput.replace("（高清组图）", "");
		}
		if(newsInput.contains("（组图）")){
			newsInput = newsInput.replace("（组图）", "");
		}
		if(newsInput.contains("（图）")){
			newsInput = newsInput.replace("（图）", "");
		}
		if(newsInput.toString().trim() == null||newsInput.toString().trim().equals("")){
			return null;
		}else{
			newsInput = ne.tagResult(newsInput).toString();		
			String word_pos[]=newsInput.substring(newsInput.indexOf("[")+1,newsInput.length()-1).split(", ");
			return word_pos;
		}
	}
	/**
	 * 从新闻文本中抽取参与者
	 * @param newsInput：新闻标题内容
	 * @param triggerWord：触发词
	 * @return：事件元素识别结果
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws LoadModelException
	 * @throws IOException
	 */
	public static String[] setActor(String newsInput,String triggerWord) throws ClassNotFoundException, SQLException, LoadModelException, IOException
	{
		String[] tempresult = {null,null};	
		String word_pos[]= dependency_parse(newsInput);
		//System.out.println(newsInput);
		if(word_pos!=null){
			String word[]=new String[word_pos.length];
			String pos[]=new String[word_pos.length];
			for(int j=0;j<word_pos.length;j++){
				 word[j]=word_pos[j].substring(0,word_pos[j].indexOf("="));
				 pos[j]=word_pos[j].substring(word_pos[j].indexOf("=")+1,word_pos[j].length());
			}			
			changepos(pos);					
			CNFactory fac=CNFactory.getInstance("models");
			DependencyTree dep=fac.parse2T(word,pos);	

			tempresult = parse(triggerWord,dep.toList());
			
		}
		System.out.println(tempresult[0]);
		System.out.println(tempresult[1]);

		return tempresult;
	}
	
	/**
	 * 返回对当前新闻标题内容进行依存句法分析后的动词排序结果
	 * @param newsInput：新闻标题内容
	 * @return 根结点和所有动词按照句法中的重要性进行排序的结果
	 * @throws LoadModelException
	 */
	public static ArrayList sort_vector(String newsInput) throws LoadModelException 
	{
		String word_pos[]= dependency_parse(newsInput);
		if(word_pos!=null){
			String word[]=new String[word_pos.length];
			String pos[]=new String[word_pos.length];
			for(int j=0;j<word_pos.length;j++){
				 word[j]=word_pos[j].substring(0,word_pos[j].indexOf("="));
				 pos[j]=word_pos[j].substring(word_pos[j].indexOf("=")+1,word_pos[j].length());
			}			
			changepos(pos);	
			CNFactory fac=CNFactory.getInstance("models");
			DependencyTree dep=fac.parse2T(word,pos);
			int parse[] = fac.parse(word, pos);

			ArrayList dependencytree = dep.toList();
			ArrayList vectorset = new ArrayList();
			for(int i=0;i<dependencytree.size();i++){
				ArrayList temp = new ArrayList();
				if(Integer.parseInt((String) ((ArrayList)dependencytree.get(i)).get(2))==-1){
					temp.add((String)((ArrayList)dependencytree.get(i)).get(0));
					temp.add(-1);
					vectorset.add(temp);

				}else if(Integer.parseInt((String) ((ArrayList)dependencytree.get(i)).get(2))!=-1&&
						((String)((ArrayList)dependencytree.get(i)).get(1)).equals("动词")){
					temp.add((String)((ArrayList)dependencytree.get(i)).get(0));
					temp.add(i);
					vectorset.add(temp);
				}	
			}
			int count[] = new int[vectorset.size()];
			ArrayList VectorList = new ArrayList();
			for(int i=0;i<vectorset.size();i++){
				ArrayList temp = new ArrayList();
				int index = (int) ((ArrayList)vectorset.get(i)).get(1);
				if(index == -1){
					count[i] = 100;
				}else{
					for(int j=0;j<parse.length;j++){
						if(index == parse[j]){
							count[i]++;
						}
					}
				}
				temp.add((String) ((ArrayList)vectorset.get(i)).get(0));
				temp.add(count[i]);
				VectorList.add(temp);
			}
			Collections.sort(VectorList, new Comparator<ArrayList>() {     
				@Override
				public int compare(ArrayList o1, ArrayList o2) {
					// TODO Auto-generated method stub
					return (int)o2.get(1)-(int)o1.get(1);
				}
			}); 	
			System.out.println(VectorList);
			return VectorList;
		}else
			return null;
	}
	
	/**
	 * 根据句子trigger和句子的依存语法树
	 * 
	 * 1、首先抽取trriger的所有的直接主语，若主语是名词，则寻找主语位于之前的，作为主语的定语的第一个属性为人名/地名/机构名的词语，和主语一起作为source返回
	 * 2、若trigger的所有直接主语均未找到，则寻找trigger的最左孩子作为source返回
	 * 3、若最左孩子未找到，则寻找位于trigger前面的第一个属性为人名/地名/机构名的词语作为source返回
	 * 4、若trigger的宾语未在trriger后面找到，则在从0开始寻找到主语之前
	 * 
	 * 5、修改，只抽取实体名为人名地名国家名
	 * @param trigger：核心词
	 * @param dep：依存句法树
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static String[] parse(String trigger,ArrayList dep) throws SQLException, ClassNotFoundException{
		String []result = {null,null};
		int index=-1;
		for(int i=0;i<dep.size();i++){	
			if((((ArrayList)dep.get(i)).get(0).toString().trim()).equals(trigger)){
				index=i;
				break;
			}
		}
		//寻找默认核心词的位置
		if(index==-1){
			for(int i=0;i<dep.size();i++){	
				if(Integer.parseInt(((ArrayList)dep.get(i)).get(2).toString().trim())==-1){
					index=i;
					break;
				}
			}
		}

		ArrayList sub = new ArrayList();
		ArrayList before_v_sub = new ArrayList();//前一个分句的谓语动词的主语

		String trigger_left_child = " ";//trigger的最左孩子
		String before_trigger_n= " ";//trigger前面的第一个名词
		int sub_tag = -1;
		
		ArrayList obj = new ArrayList();
		ArrayList before_v_obj = new ArrayList();//前一个分句的谓语动词的宾语

		String after_trigger_n= " ";//trigger后面的第一个名词
		String trigger_right_child = " ";//trigger的最右孩子
		int obj_tag = -1;
		for(int i=0,k=0,m=0;i<dep.size();i++){
			if(index==Integer.parseInt((((ArrayList)dep.get(i)).get(2).toString().trim()))){	
				
				if((((ArrayList)dep.get(i)).get(3).toString().trim()).equals("主语")){
					String subcontent = actor_befor_n(i,dep);
					sub.add(subcontent);
					k++;
				}
				if((((ArrayList)dep.get(i)).get(3).toString().trim()).equals("宾语")){
					String objcontent = actor_befor_n(i,dep);
					obj.add(objcontent);
					m++;
				}
			}
		}
	
		if(sub.size()>0){
			result[0] = sub.get(0).toString();
			if(sub.size()>1){
				for(int i=1;i<sub.size();i++){
					result[0] = result[0]+"_"+sub.get(i).toString();
				}
			}
			
		}else if(before_v_sub.size()!=0){
			result[0] = before_v_sub.get(0).toString();
			if(before_v_sub.size()>1){
				for(int i=1;i<before_v_sub.size();i++){
					result[0] = result[0]+"_"+before_v_sub.get(i).toString();
				}
			}		
		}else if(!(trigger_left_child = find_left_child(index,dep)).equals(" ")){
			result[0] = trigger_left_child;
		}else{
				for(int i=index-1;i>=0;i--){
					if((((ArrayList)dep.get(i)).get(1).toString().trim()).equals("名词")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("人名")||
							(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("地名")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("机构名")||
							(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("国家名")){
						sub_tag = i;
						break;
					}
				}
				if(sub_tag!=-1){
					before_trigger_n = actor_befor_n(sub_tag,dep);
				}
				if(!before_trigger_n.equals(" ")){
					result[0] = before_trigger_n;
				}
		}
		
		//找target
		if(obj.size()>0){	
			result[1] = obj.get(0).toString();
			if(obj.size()>1){
				for(int i=1;i<obj.size();i++){
					result[1] = result[1]+"_"+obj.get(i).toString();
				}
			}	
		
		}else if(before_v_obj.size()!=0){
			result[1] = before_v_obj.get(0).toString();
			if(before_v_obj.size()>1){
				for(int i=1;i<before_v_obj.size();i++){
					result[1] = result[1]+"_"+before_v_obj.get(i).toString();
				}
			}				
		}else if(!(trigger_right_child = find_right_child(index,dep)).equals(" ")){
			result[1] = trigger_right_child;
		}else{	
			for(int i=index+1;i<dep.size();i++){
				if((((ArrayList)dep.get(i)).get(1).toString().trim()).equals("名词")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("人名")||
						(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("地名")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("机构名")||
						(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("国家名")){
					obj_tag = i;
					break;
				}
			}
			if(obj_tag!=-1){
				after_trigger_n = actor_befor_n(obj_tag,dep);
			}else{
				for(int i=0;i<dep.size();i++){
					if(((((ArrayList)dep.get(i)).get(1).toString().trim()).equals("名词")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("人名")||
									(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("地名")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("机构名")||
									(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("国家名"))){
						after_trigger_n = ((ArrayList)dep.get(i)).get(0).toString().trim();
						break;
					}
				}
			}
				
			if(!after_trigger_n.equals(" ")){
				result[1] = after_trigger_n;
			}
		}
		return result;
	}
	
	public static String actor_befor_n(int index,ArrayList dep){
		String actor = (((ArrayList)dep.get(index)).get(0).toString().trim());
		for(int i=index;i>=0;i--){
			if(((((ArrayList)dep.get(i)).get(1).toString().trim()).equals("人名")||(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("地名")||
					(((ArrayList)dep.get(i)).get(1).toString().trim()).equals("机构名"))&&(index==Integer.parseInt((((ArrayList)dep.get(i)).get(2).toString().trim())))){
				actor = ((ArrayList)dep.get(i)).get(0).toString().trim();
				break;
			}
		}
		return actor;
	}

	public static String find_left_child(int rootindex,ArrayList dep){
		String left_child = " ";
		for(int i=0;i<rootindex;i++){
			if(rootindex==Integer.parseInt((((ArrayList)dep.get(i)).get(2).toString().trim()))&&
					((((ArrayList)dep.get(i)).get(2).toString().trim()).equals("名词")||(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("人名")||
							(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("地名")||(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("机构名")||
							(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("国家名"))){
				
				left_child = ((ArrayList)dep.get(i)).get(0).toString().trim();
				break;
			}
		}
		return left_child;
	}

	public static String find_right_child(int rootindex,ArrayList dep){
		String right_child = " ";
		for(int i=rootindex+1;i<dep.size();i++){
			if(rootindex==Integer.parseInt((((ArrayList)dep.get(i)).get(2).toString().trim()))&&
					((((ArrayList)dep.get(i)).get(2).toString().trim()).equals("名词")||(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("人名")||
							(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("地名")||(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("机构名")||
							(((ArrayList)dep.get(i)).get(2).toString().trim()).equals("国家名"))){				
				right_child = ((ArrayList)dep.get(i)).get(0).toString().trim();
				break;
			}
		}
		return right_child;
	}
	/**
	 * 词性转换为中文
	 * @param pos
	 */
	public static void changepos(String[]pos){
		for(int i=0;i<pos.length;i++){
			if(pos[i].equals("n")||pos[i].equals("nba")||pos[i].equals("nbc")||
					pos[i].equals("nbp")||pos[i].equals("nf")||pos[i].equals("ng")||pos[i].equals("nhd")||pos[i].equals("ni")||pos[i].equals("nic")||
					pos[i].equals("nl")||pos[i].equals("nm")||pos[i].equals("nmc")||pos[i].equals("nn")||pos[i].equals("nnd")||pos[i].equals("nnt")){
				pos[i]="名词";
			}else if(pos[i].equals("g")||pos[i].equals("gb")||pos[i].equals("gbc")||pos[i].equals("gc")||pos[i].equals("gg")||pos[i].equals("gi")||pos[i].equals("gm")||pos[i].equals("gp")
					||pos[i].equals("nh")||pos[i].equals("nhm")){
				pos[i]="专有名词";
			}else if(pos[i].equals("nb")||pos[i].equals("nr")||pos[i].equals("nr1")||pos[i].equals("nr2")||pos[i].equals("nrf")||pos[i].equals("nrj")){
				pos[i]="人名";
			}else if(pos[i].equals("ns")||pos[i].equals("nsf")){
				pos[i]="地名";
			}else if(pos[i].equals("nt")||pos[i].equals("nit")||pos[i].equals("ntc")||pos[i].equals("ntcb")||pos[i].equals("ntch")||pos[i].equals("ntcf")||pos[i].equals("nth")||pos[i].equals("nto")||pos[i].equals("nts")){
				pos[i]="机构名";
			}else if(pos[i].equals("xu")){
				pos[i]="网址";
			}else if(pos[i].equals("a")||pos[i].equals("ad")||pos[i].equals("al")||pos[i].equals("ag")){
				pos[i]="形容词";
			}else if(pos[i].equals("d")||pos[i].equals("dg")){
				pos[i]="副词";
			}else if(pos[i].equals("r")||pos[i].equals("rg")||pos[i].equals("Rg")){
				pos[i]="代词";
			}else if(pos[i].equals("rr")){
				pos[i]="人称代词";
			}else if(pos[i].equals("rz")||pos[i].equals("rzs")||pos[i].equals("rzt")||pos[i].equals("rzv")){
				pos[i]="指示代词";
			}else if(pos[i].equals("ry")||pos[i].equals("rys")||pos[i].equals("ryt")||pos[i].equals("ryv")){
				pos[i]="疑问代词";
			}else if(pos[i].equals("c")||pos[i].equals("cc")||pos[i].equals("dl")){
				pos[i]="从属连词";
			}else if(pos[i].equals("ul")){
				pos[i]="并列连词 ";
			}else if(pos[i].equals("ud")){
				pos[i]="结构助词";
			}else if(pos[i].equals("p")||pos[i].equals("pba")||pos[i].equals("pbei")){
				pos[i]="介词";
			}else if(pos[i].equals("m")||pos[i].equals("mg")||pos[i].equals("mq")||pos[i].equals("Mq")){
				pos[i]="数词";
			}else if(pos[i].equals("q")||pos[i].equals("qg")||pos[i].equals("qv")){
				pos[i]="量词";
			}else if(pos[i].equals("v")||pos[i].equals("vd")||pos[i].equals("vg")||pos[i].equals("vi")||
					pos[i].equals("vl")||pos[i].equals("vn")||pos[i].equals("vshi")||pos[i].equals("vx")||pos[i].equals("vyou")||pos[i].equals("nz")){
				pos[i]="动词";
			}else if(pos[i].equals("vf")){
				pos[i]="趋向动词";
			}else if(pos[i].equals("t")||pos[i].equals("tg")){
				pos[i]="时间短语";
			}else if(pos[i].equals("o")){
				pos[i]="拟声词";
			}else if(pos[i].equals("y")||pos[i].equals("yg")){
				pos[i]="语气语";
			}else if(pos[i].equals("q't")){
				pos[i]="时态词";
			}else if(pos[i].equals("f")){
				pos[i]="方位词";
			}else if(pos[i].equals("e")){
				pos[i]="叹词";
			}else if(pos[i].equals("uu")||pos[i].equals("ug")){
				pos[i]="动态助词";
			}else if(pos[i].equals("w")||pos[i].equals("wb")||pos[i].equals("wd")||pos[i].equals("wf")||pos[i].equals("wh")||pos[i].equals("wj")||pos[i].equals("wky")||pos[i].equals("wkz")||pos[i].equals("wm")||
					pos[i].equals("wn")||pos[i].equals("wp")||pos[i].equals("ws")||pos[i].equals("wt")||pos[i].equals("ww")||pos[i].equals("wyy")||pos[i].equals("wyz")){
				pos[i]="标点";
			}else{
				pos[i]="未知词";
			}
		}
	}
}
