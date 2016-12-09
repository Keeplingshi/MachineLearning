package example.edu.shu.auto.annotation;

import java.util.Set;

import edu.shu.auto.preprocess.PreProcess;

/**
 * 
 * <p>
 * ClassName Demo
 * </p>
 * <p>
 * Description 使用示例，要求text.txt文件名称是新闻报道的标题，text.txt文件中第一行内容是报道的时间，第一行之后为报道的具体内容
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年4月24日 下午7:22:00
 *         </p>
 * @version V1.0.0
 *
 */
public class Demo {
	public static void main(String[] args) {
		String filePath = "C:\\Users\\chenbin\\Desktop\\黑龙江依兰交警被曝设岗收钱 超载车给钱就过.txt";
		Set<String> s=PreProcess.getTreeSet();
		System.out.println(s);
//		StringBuilder text = PreProcess.getText(filePath);
//		System.out.println(text);
	}
}
