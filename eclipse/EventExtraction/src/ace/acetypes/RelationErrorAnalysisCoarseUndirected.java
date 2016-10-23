package ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.dom4j.DocumentException;

import util.Span;


/*
 * This class is to do coarse-grained, undirected, error analysis
 * in this version, we assume the mention detection is from gold standard
 */
public class RelationErrorAnalysisCoarseUndirected
{
	static String htmlHead = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body><div>";
	static String htmlTail = "</div></body></html>"; 
	static String htmlBar = "<br><hr>";
	
	static void doAnalysisForFile(File textFile, File apf_ans, File apf_gold, PrintStream out,
			PrintStream out2, PrintStream out3, PrintStream out4, String year) throws DocumentException, IOException
	{
		AceDocument doc_ans = new AceDocument(textFile.getAbsolutePath(), apf_ans.getAbsolutePath(), year);
		AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), apf_gold.getAbsolutePath(), year);
		
		List<AceRelationMention> mentions_ans = doc_ans.relationMentions;
		List<AceRelationMention> mentions_gold = doc_gold.relationMentions;
		
		for(AceRelationMention mention_gold : mentions_gold)
		{
			boolean missing = true;
			
			AceEntityMention gold_arg1 = mention_gold.arg1;
			AceEntityMention gold_arg2 = mention_gold.arg2;
			String gold_type = mention_gold.getType();
			
			for(AceRelationMention mention_ans : mentions_ans)
			{
				AceEntityMention ans_arg1 = mention_ans.arg1;
				AceEntityMention ans_arg2 = mention_ans.arg2;
				String ans_type = mention_ans.getType();
				
				// undirected, coarse-grained
				if((relationArgCorrect(gold_arg1, ans_arg1) && relationArgCorrect(gold_arg2, ans_arg2) 
						|| relationArgCorrect(gold_arg2, ans_arg1) && relationArgCorrect(gold_arg1, ans_arg2))
						&& gold_type.equals(ans_type))
				{
					missing = false;
					break;
				}
				if((relationArgCorrect(gold_arg1, ans_arg1) && relationArgCorrect(gold_arg2, ans_arg2) 
						|| relationArgCorrect(gold_arg2, ans_arg1) && relationArgCorrect(gold_arg1, ans_arg2))
						&& !gold_type.equals(ans_type))
				{
					missing = false;
					out2.print("<br><br>");
					out2.println("<b>relation mention type error</b>" + "\t" + toStringRelationMention(mention_gold) + "\t" +  "<font color=\"red\">" + mention_ans.getType() +  "</font>" 
							+ "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
					break;
				}
			}
			
			if(missing)
			{
				out.print("<br><br>");
				out.println("<b>relation mention missing</b>" + "\t" + toStringRelationMention(mention_gold) + "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
			}
		}
		
		for(AceRelationMention mention_ans : mentions_ans)
		{
			AceEntityMention ans_arg1 = mention_ans.arg1;
			AceEntityMention ans_arg2 = mention_ans.arg2;
			
			boolean event_mention_false_positive = true;
			
			for(AceRelationMention mention_gold : mentions_gold)
			{
				AceEntityMention gold_arg1 = mention_gold.arg1;
				AceEntityMention gold_arg2 = mention_gold.arg2;
				
				if(gold_arg1.head.overlap(ans_arg1.head) && gold_arg2.head.overlap(ans_arg2.head)
						|| gold_arg1.head.overlap(ans_arg2.head) && gold_arg2.head.overlap(ans_arg1.head))
				{
					event_mention_false_positive = false;
					break;
				}
			}
			if(event_mention_false_positive)
			{
				out4.print("<br><br>");
				out4.println("<b>relation mention_false positive</b>" + "\t" + "<font color=\"red\">" + toStringRelationMention(mention_ans) + "</font>" + "\t" + "<font color=\"blue\">" + mention_ans.text + "</font>");
			}
		}
	}
	
	/**
	 * check if entity mention exists (only check scop) in the given list
	 * @param entityMentions
	 * @param arg1
	 */
	private static boolean entityMentionExist(
			List<AceEntityMention> mentions, AceEntityMention arg)
	{
		for(AceEntityMention mention : mentions)
		{
			if(mention.head.overlap(arg.head))
			{
				return true;
			}
		}
		return false;
	}

	static public String toStringRelationMention(AceRelationMention mention)
	{
		String ret = "";
		ret += mention.getType() + "(" + "arg1: " + mention.arg1.headText + "_" + mention.arg1.getType() + " arg2: " + mention.arg2.headText+ "_" + mention.arg2.getType() + ")";
		return ret;
	}
	
	static public String toStringEntityMention(AceEntityMention mention)
	{
		String ret = "";
		ret += mention.getType() + "(" + mention.headText + ")";
		return ret;
	}
	
	/**
	 * check if two entity are same coreference
	 * @param value
	 * @param value2
	 * @return
	 */
	private static boolean relationArgCorrect(AceEntityMention value, AceEntityMention value2)
	{
		Span head = value.head;
		Span head2 = value2.head;
		
		return head.overlap(head2);
	}

	public static void doAnalysis(File goldDir, File ansDir, File file_list, PrintStream out,
			PrintStream out2, PrintStream out3, PrintStream out4, String year) throws IOException, DocumentException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		
		out.println(htmlHead);
		out2.println(htmlHead);
		out3.println(htmlHead);
		out4.println(htmlHead);
		while((line = reader.readLine()) != null)
		{
			out.println("<br><br><br><br>");
			out2.println("<br><br><br><br>");
			out3.println("<br><br><br><br>");
			out4.println("<br><br><br><br>");
			out.println("\nDocument: + " + line + "\n");
			out2.println("\nDocument: + " + line + "\n");
			out3.println("\nDocument: + " + line + "\n");
			out4.println("\nDocument: + " + line + "\n");
			out.println(htmlBar);
			out2.println(htmlBar);
			out3.println(htmlBar);
			out4.println(htmlBar);
			
			File apf_ans = new File(ansDir + File.separator + line + ".sgm.apf");
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line + ".apf.xml");
			}
			int idx = line.indexOf("/");
			String new_line = line.substring(0, idx+1) + "timex2norm" + File.separator + line.substring(idx+1);
			File apf_gold = new File(goldDir + File.separator + new_line + ".apf.xml");
			File text_file = new File(goldDir + File.separator + new_line + ".sgm");
			if(!apf_gold.exists())
			{
				apf_gold = new File(goldDir + File.separator + line + ".apf.xml");
				text_file = new File(goldDir + File.separator + line + ".sgm");
			}
			doAnalysisForFile(text_file, apf_ans, apf_gold, 
					out, out2, out3, out4, year);
		}
		
		reader.close();
		
		out.println("\n\n---------------------------");
		out2.println("\n\n---------------------------");
		out3.println("\n\n---------------------------");
		out4.println("\n\n---------------------------");
		
		out.println(htmlTail);
		out2.println(htmlTail);
		out3.println(htmlTail);
		out4.println(htmlTail);	
	}
	
	static public void main(String[] args) throws DocumentException, IOException
	{	
		if(args.length < 4)
		{
			System.out.println("Automatic error analysis Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output filename");
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir = new File(args[1]);
		File filelist = new File(args[2]);		
		PrintStream out = new PrintStream(new File(args[3]) + "1.html");
		PrintStream out2 = new PrintStream(new File(args[3]) + "2.html");
		PrintStream out3 = new PrintStream(new File(args[3]) + "3.html");
		PrintStream out4 = new PrintStream(new File(args[3]) + "4.html");
		
		doAnalysis(goldDir, ansDir, filelist, out, out2, out3, out4, "2004");
		
		out.close();
		out2.close();
		out3.close();
		out3.close();
		out4.close();
	}
}
