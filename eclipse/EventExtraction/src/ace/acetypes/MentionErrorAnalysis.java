package ace.acetypes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import util.Span;

import commons.Document;
import commons.Sentence;
import commons.TextFeatureGenerator;

import cc.mallet.util.FileUtils;

public class MentionErrorAnalysis
{
	private static final String NONE_ENTITY = "NONE";

	// global line number 
	static int lineNum = 0;
	static boolean relationOnly = true;
	
	public static void printHtml(File ansDir, File goldDir, File filelist, PrintStream writer, String year) throws IOException
	{
		String htmlHead = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body><div>";
		String htmlTail = "</div></body></html>"; 
		String htmlBar = "<br><hr><br>";
		
		writer.println(htmlHead);
		
		String[] files = FileUtils.readFile(filelist); 
		lineNum = 0;
		for(String filename : files)
		{
			File gold_child = new File(goldDir, filename + ".apf.xml");
			File ans_child = new File(ansDir, filename + ".apf.xml");
			File txtFile = new File(goldDir, filename + ".sgm");
			writer.println("Document: " + filename);
			writer.println(htmlBar);
			processDocument(gold_child, ans_child, txtFile, writer, year);
		}
		
		writer.println(htmlTail);
	}
	
	private static void processDocument(File goldFile, File ansFile, File textFile, PrintStream writer, String year) throws IOException 
	{
		AceDocument doc_ans = new AceDocument(textFile.getAbsolutePath(), ansFile.getAbsolutePath(), year);
		AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), goldFile.getAbsolutePath(), year);
		boolean monoCase = goldFile.getAbsolutePath().contains("bnews/") ? true : false;
		Document doc = new Document(goldFile.getAbsolutePath().replaceAll("\\.apf\\.xml$", ""), true, monoCase, year);
		TextFeatureGenerator.doPreprocessCheap(doc);
		List<Sentence> sents = doc.getSentences();
		
		for(int sent = 0; sent < sents.size(); sent++)
		{
			List<AceEntityMention> mentions_gold = getMentionsFromSent(sents.get(sent), doc_gold.entityMentions);
			List<AceEntityMention> mentions_ans = getMentionsFromSent(sents.get(sent), doc_ans.entityMentions);
		
			if(mentions_gold.equals(mentions_ans))
			{
				// only print sents that contain incorrect answers
				continue;
			}
			
			if(relationOnly && sents.get(sent).relationMentions.size() == 0)
			{
				// skip those don't have relation mentions
				continue;
			}
			
			lineNum++;
			writer.println("<li>");
			// print gold
			writer.print(lineNum +" gold: ");
			for(AceEntityMention entity_gold : mentions_gold)
			{
				boolean missed = !correctEntityAns(entity_gold, mentions_ans);
				if(missed)
				{
					writer.print("<font color=\"red\">");
				}
				writer.print(printEntity(entity_gold));
				if(missed)
				{
					writer.print("</font>");
				}
				writer.print(" <WBR> ");
			}
			writer.println("<br/>");
			
			// print ans
			writer.print("&nbsp;&nbsp;&nbsp;&nbsp; ans: ");
			for(int indx_ans=0; indx_ans < mentions_ans.size(); indx_ans++)
			{
				AceEntityMention entity_ans =  mentions_ans.get(indx_ans);
				
				boolean correct = correctEntityAns(entity_ans, mentions_gold);
				
				if(!correct)
				{
					writer.print("<font color=\"red\">");
				}
				writer.print(printEntity(entity_ans));
				if(!correct)
				{
					writer.print("</font>");
				}
				writer.print(" <WBR> ");
			}
			writer.println("<br/>");
			writer.println("</li>");
			writer.println("<br/>");
		}
		writer.println("</ul>");
	}

	private static boolean correctEntityAns(AceEntityMention entity_ans,
			List<AceEntityMention> mentions_gold)
	{
		if(entity_ans.getType().equals(NONE_ENTITY))
		{
			return true;
		}
		for(AceEntityMention gold : mentions_gold)
		{
			if(gold.equals(entity_ans) && gold.getType().equals(entity_ans.getType()))
			{
				return true;
			}
		}
		return false;
	}

	private static List<AceEntityMention> getMentionsFromSent(
			Sentence sentence, List<AceEntityMention> entityMentions)
	{
		List<AceEntityMention> ret = new ArrayList<AceEntityMention>();
		Span extent = sentence.getExtent();
		for(AceEntityMention mention : entityMentions)
		{
			if(mention.head.within(extent))
			{
				ret.add(mention);
			}
		}
	
		int begin = -1;
		for(int i=extent.start(); i<=extent.end(); i++)
		{
			boolean in = false;
			for(AceEntityMention mention : ret)
			{
				if(mention.head.contains(i))
				{
					in = true;
					break;
				}
			}
			if(!in)
			{
				if(begin == - 1)
				{
					begin = i;
				}
			}
			else
			{
				if(begin != -1)
				{
					int end = i-1;
					AceEntity new_entity = new AceEntity("", NONE_ENTITY, NONE_ENTITY, false);
					AceEntityMention new_mention = new AceEntityMention("", "", new Span(begin, end), 
							new Span(begin, end), sentence.doc.allText);
					new_mention.entity = new_entity;
					ret.add(new_mention);
					begin = -1;
				}
			}
		}
		if(begin != -1)
		{
			int end = extent.end();
			AceEntity new_entity = new AceEntity("", NONE_ENTITY, NONE_ENTITY, false);
			AceEntityMention new_mention = new AceEntityMention("", "", new Span(begin, end), 
					new Span(begin, end), sentence.doc.allText);
			new_mention.entity = new_entity;
			ret.add(new_mention);
			begin = -1;
		}
		
		Collections.sort(ret, new Comparator<AceEntityMention>()
			{
				@Override
				public int compare(AceEntityMention arg0,
						AceEntityMention arg1)
				{
					return arg0.head.compareTo(arg1.head);
				}
			});
		
		return ret;
	}

	static protected String printEntity(AceEntityMention entity)
	{
		String ret;
		if(!entity.getType().equals(NONE_ENTITY))
		{
			ret = "<b>[";
			ret+= entity.headText;
			ret += "]</b>";
			ret += "<font size=\"2\">";
			ret += "/";
			ret += entity.getType();
			ret += "</font>";
		}
		else
		{
			ret = "";
			ret+= entity.getHeadText();
		}
		return ret;
			
	}
	
	static public void main(String[] args) throws IOException
	{
		if(args.length < 5)
		{
			System.out.println("Usage:");
			System.out.println("args[0]: the dir of gold");
			System.out.println("args[1]: the dir of output");
			System.out.println("args[2]: the filelist");
			System.out.println("args[3]: the file name of html analysis file");
			System.out.println("args[4]: year (one of 2004 and 2005)");
			System.exit(-1);
		}
		
		// gold standard
		File htmlFile = new File(args[3] + ".html");
		File outputDir = new File(args[1]);
		File goldDir = new File(args[0]);
		File filelist = new File(args[2]);
		String year = args[4];
		
		PrintStream writer = new PrintStream(htmlFile);
		
		printHtml(outputDir, goldDir, filelist, writer, year);
		writer.close();
	}
}
