package ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.dom4j.DocumentException;

import util.Span;


public class RelationErrorAnalysis
{
	static String htmlHead = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body><div>";
	static String htmlTail = "</div></body></html>"; 
	static String htmlBar = "<br><hr>";

	static int num_missing = 0;
	static int num_missing_arg = 0;
	
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
				
				if(relationArgCorrect(gold_arg1, ans_arg1) 
						&& relationArgCorrect(gold_arg2, ans_arg2) && gold_type.equals(ans_type))
				{
					missing = false;
					break;
				}
				else if(relationArgCorrect(gold_arg1, ans_arg1) && relationArgCorrect(gold_arg2, ans_arg2)
						|| relationArgCorrect(gold_arg2, ans_arg1) && relationArgCorrect(gold_arg1, ans_arg2))
				{
					missing = false;
					out2.print("<br><br>");
					out2.println("<b>relation mention type error</b>" + "\t" + toStringRelationMention(mention_gold) + "\t" +  "<font color=\"red\">" + mention_ans.getType() +  "</font>" 
							+ "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
				}
				else if(gold_arg1.head.overlap(ans_arg1.head) && gold_arg2.head.overlap(ans_arg2.head)
						|| gold_arg1.head.overlap(ans_arg2.head) && gold_arg2.head.overlap(ans_arg1.head))
				{
					// entity mention type/scope error
					missing = false;
					out3.print("<br><br>");
					out3.println("<b>entity mention error</b>" + "\t" + toStringRelationMention(mention_gold) + "\t" + 
							"<font color=\"red\">" + toStringRelationMention(mention_ans) +  "</font>" 
							+ "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
				}
			}
			
			if(missing)
			{
				// try to find if the mentions of args are in ans
				AceEntityMention arg1 = mention_gold.arg1;
				AceEntityMention arg2 = mention_gold.arg2;
				
				out.print("<br><br>");
				out.println("<b>relation mention missing</b>" + "\t" + toStringRelationMention(mention_gold) + "\t" + "<font color=\"blue\">" + mention_gold.text + "</font>");
				num_missing++;
				
				// check if the missing of relation is caused by missing of entity mention
				if(!entityMentionExist(doc_ans.entityMentions, arg1))
				{
					out.println("<br><font color=\"red\">missing:" + toStringEntityMention(arg1)  + "</font>");
				}
				if(!entityMentionExist(doc_ans.entityMentions, arg2))
				{
					out.println("<br><font color=\"red\">missing:" + toStringEntityMention(arg2)  + "</font>");
				}
				
				if(!entityMentionExist(doc_ans.entityMentions, arg1) || !entityMentionExist(doc_ans.entityMentions, arg2))
				{
					num_missing_arg++;
				}
			}
		}
		
		for(AceRelationMention mention_ans : mentions_ans)
		{
			AceEntityMention ans_arg1 = mention_ans.arg1;
			AceEntityMention ans_arg2 = mention_ans.arg2;
			
			boolean relation_mention_false_positive = true;
			
			for(AceRelationMention mention_gold : mentions_gold)
			{
				AceEntityMention gold_arg1 = mention_gold.arg1;
				AceEntityMention gold_arg2 = mention_gold.arg2;
				
				if(gold_arg1.head.overlap(ans_arg1.head) && gold_arg2.head.overlap(ans_arg2.head)
						|| gold_arg1.head.overlap(ans_arg2.head) && gold_arg2.head.overlap(ans_arg1.head))
				{
					relation_mention_false_positive = false;
					break;
				}
			}
			if(relation_mention_false_positive)
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
		ret += mention.getType() + ":" + mention.getSubType() + "(" + "arg1: " + mention.arg1.headText + "_" + mention.arg1.getType() + " arg2: " + mention.arg2.headText+ "_" + mention.arg2.getType() + ")";
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
		if(args.length < 5)
		{
			System.out.println("Automatic error analysis Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: output filename");
			System.out.println("args[4]: year (2004 or 2005)");
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir = new File(args[1]);
		File filelist = new File(args[2]);		
		PrintStream out = new PrintStream(new File(args[3]) + "1.html");
		PrintStream out2 = new PrintStream(new File(args[3]) + "2.html");
		PrintStream out3 = new PrintStream(new File(args[3]) + "3.html");
		PrintStream out4 = new PrintStream(new File(args[3]) + "4.html");
		String year = args[4];
		
		doAnalysis(goldDir, ansDir, filelist, out, out2, out3, out4, year);
		
		out.close();
		out2.close();
		out3.close();
		out3.close();
		out4.close();
		
		System.out.println("\n\n # Missing relation mentions");
		System.out.println(num_missing);
		System.out.println("\n\n # Missing arguments");
		System.out.println(num_missing_arg);
	}
}
