package ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;

import util.Span;


/**
 * This scorer combines the scorer for 
 * event extraction and relation extraction all together
 * @author qli
 *
 */
public class EREScorer
{
	public static class Stats
	{
		public Stats(String type)
		{
			this.type = type;
		}
		
		// confusion matrix
		Map<String, Map<String, Integer>> confusion = new HashMap<String, Map<String, Integer>>();
		
		// add confusion
		public void addConfusion(String gold, String predicted)
		{
			Map<String, Integer> row = confusion.get(gold);
			if(row == null)
			{
				row = new HashMap<String, Integer>();
				confusion.put(gold, row);
			}
			Integer count = row.get(predicted);
			if(count == null)
			{
				count = 0;
			}
			count++;
			row.put(predicted, count);
		}
		
		// print confusion 
		public String printConfusion()
		{
			StringBuilder buff = new StringBuilder("");
			for(String gold : confusion.keySet())
			{
				Map<String, Integer> row = confusion.get(gold);
				for(String predicted : row.keySet())
				{
					String str = gold + "\t" + predicted + "\t" + row.get(predicted) + "\n";
					buff.append(str);
				}
			}
			return buff.toString();
		}
		
		// the type of this stats
		public String type;
		
		// basic numbers
		public double num_ans = 0.0;
		public double num_gold = 0.0;
		public double num_correct = 0.0;
		
		public double prec;
		public double recall;
		public double f1;
		
		/**
		 * calculate the performance
		 */
		public void calc()
		{
			prec = num_correct / num_ans;
			recall = num_correct / num_gold;
			f1 = 2 * (prec * recall) / (prec + recall);
		}
		
		@Override
		public String toString()
		{
			String ret = String.format("%s \t F1 %.3f Prec %.3f Recall %.3f #Correct %.0f / %.0f",
					type, f1, prec, recall, num_correct, num_gold);
			return ret;
		}
	}
	
	public static void doAnalysis(File goldDir, File ansDir, File file_list, PrintStream out, 
			String year) throws IOException, DocumentException
	{
		Map<String, Stats> relation_scores_types = new HashMap<String, Stats>();
		Map<String, Stats> entity_scores_types = new HashMap<String, Stats>();
		Map<String, Stats> trigger_scores_types = new HashMap<String, Stats>();
		Map<String, Stats> argument_scores_types = new HashMap<String, Stats>();
		Stats relation_scores_strict = new Stats("RelationStrict");
		Stats relation_scores = new Stats("Relation");
		Stats entity_scores = new Stats("Entity");
		Stats entity_idf_scores = new Stats("Entity_IDF");
		Stats trigger_scores = new Stats("Trigger");
		Stats trigger_idf_scores = new Stats("Trigger_IDF");
		Stats argument_scores = new Stats("Argument");
		Stats argument_idf_scores = new Stats("Argument_IDF");
		
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_ans = new File(ansDir + File.separator + line + ".sgm.apf");
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line);
			}
			if(!apf_ans.exists())
			{
				apf_ans = new File(ansDir + File.separator + line + ".apf.xml");
			}
			int idx = line.indexOf("/");
			String new_line = line.substring(0, idx+1) + "timex2norm" + File.separator + line.substring(idx+1);
			File apf_gold = new File(goldDir + File.separator + new_line + ".apf.xml");
			File textFile = new File(goldDir + File.separator + new_line + ".sgm");
			if(!apf_gold.exists())
			{
				apf_gold = new File(goldDir + File.separator + line + ".apf.xml");
				textFile = new File(goldDir + File.separator + line + ".sgm");
			}
			AceDocument doc_ans = new AceDocument(textFile.getAbsolutePath(), apf_ans.getAbsolutePath(), year);
			AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), apf_gold.getAbsolutePath(), year);
			
			System.out.println("Evaluating " + doc_gold.docID);
			
			// evalute relation mentions
			List<AceRelationMention> mentions_ans = doc_ans.relationMentions;
			List<AceRelationMention> mentions_gold = doc_gold.relationMentions;
			out.println("NUM: "  + doc_ans.docID + "\t" + mentions_ans.size() + "\t" + mentions_gold.size() + "\t" + relation_scores.num_ans + "\t " + relation_scores.num_gold);
			evaluateRelation(relation_scores_strict, relation_scores, 
					relation_scores_types, mentions_ans, mentions_gold);
			
			// evaluate entity mentions
			List<AceEntityMention> entity_mentions_ans = doc_ans.entityMentions;
			List<AceEntityMention> entity_mentions_gold = doc_gold.entityMentions;
			evaluateEntity(entity_scores, entity_idf_scores,
					entity_scores_types, entity_mentions_ans, entity_mentions_gold);
			
			// evaluation events (trigger and arguments)
			List<AceEventMention> event_mentions_ans = doc_ans.eventMentions;
			List<AceEventMention> event_mentions_gold = doc_gold.eventMentions;
			evaluateTriggers(trigger_scores, trigger_idf_scores,
					trigger_scores_types, event_mentions_ans, event_mentions_gold);
			evaluateArguments(argument_scores, argument_idf_scores,
					argument_scores_types, event_mentions_ans, event_mentions_gold);
		}
		
		relation_scores.calc();
		entity_scores.calc();
		trigger_scores.calc();
		argument_scores.calc();
		
		out.println("\n\n---------------------------");
		out.println("Summary:\n");
		out.println(relation_scores);
		out.println(entity_scores);
		out.println(trigger_scores);
		out.println(argument_scores);
		
		out.println("\n\nrelation strict :\n");
		relation_scores_strict.calc();
		out.println(relation_scores_strict);
		
		out.println("\n\nBreakdown scores for relation :\n");
		for(Entry<String, Stats> entry : relation_scores_types.entrySet())
		{
			entry.getValue().calc();
			out.println(entry.getValue());
		}
		
		out.println("\n\nBreakdown scores for entity :\n");
		for(Entry<String, Stats> entry : entity_scores_types.entrySet())
		{
			entry.getValue().calc();
			out.println(entry.getValue());
		}
		
		out.println("\n\nBreakdown scores for triggers :\n");
		for(Entry<String, Stats> entry : trigger_scores_types.entrySet())
		{
			entry.getValue().calc();
			out.println(entry.getValue());
		}
		
		out.println("\n\nIdentification scores for entity :\n");
		entity_idf_scores.calc();
		out.println(entity_idf_scores);
		
		out.println("\n\nIdentification scores for trigger :\n");
		trigger_idf_scores.calc();
		out.println(trigger_idf_scores);
		
		out.println("\n\nIdentification scores for argument :\n");
		argument_idf_scores.calc();
		out.println(argument_idf_scores);
		
		reader.close();
	}

	private static void evaluateTriggers(Stats trigger_scores,
			Stats trigger_idf_scores, Map<String, Stats> trigger_scores_types,
			List<AceEventMention> event_mentions_ans,
			List<AceEventMention> event_mentions_gold)
	{
		// evalute triggers
		trigger_scores.num_ans += event_mentions_ans.size();
		trigger_scores.num_gold += event_mentions_gold.size();
		trigger_idf_scores.num_ans += event_mentions_ans.size();
		trigger_idf_scores.num_gold += event_mentions_gold.size();
		for(AceEventMention mention : event_mentions_ans)
		{
			for(AceEventMention mention_gold : event_mentions_gold)
			{
				if(mention.anchorExtent.overlap(mention_gold.anchorExtent))
				{
					if(mention.getSubType().equals(mention_gold.getSubType()))
					{
						trigger_scores.num_correct++;
					}
					
					// for identification scoring
					trigger_idf_scores.num_correct++;
					break;
				}
			}
		}
	}

	/**
	 * evaluate the event (trigger and argument part of the output)
	 * @param event_scores
	 * @param event_idf_scores
	 * @param event_scores_types
	 * @param event_mentions_ans
	 * @param event_mentions_gold
	 */
	private static void evaluateArguments(Stats arg_scores,
			Stats arg_idf_scores, Map<String, Stats> arg_scores_types,
			List<AceEventMention> event_mentions_ans,
			List<AceEventMention> event_mentions_gold)
	{
		// evalute arguments
		List<AceEventMentionArgument> args_ans = getArguments(event_mentions_ans);
		List<AceEventMentionArgument> args_gold = getArguments(event_mentions_gold);
		
		arg_scores.num_ans += args_ans.size();
		arg_scores.num_gold += args_gold.size();
		arg_idf_scores.num_ans += args_ans.size();
		arg_idf_scores.num_gold += args_gold.size();
		
		// check number of correct
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.role.equals(temp.role) && arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrect(arg.value, temp.value))
					{
						arg_scores.num_correct++;
						break;
					}
				}
				
			}
		}
		
		// check number of correct in terms of identification
		for(AceEventMentionArgument arg : args_ans)
		{
			for(AceEventMentionArgument temp : args_gold)
			{
				if(arg.mention.getSubType().equals(temp.mention.getSubType()))
				{
					if(headCorrect(arg.value, temp.value))
					{
						arg_idf_scores.num_correct++;
						break;
					}
				}
				
			}
		}
	}

	/**
	 * check if two entity are same coreference
	 * @param value
	 * @param value2
	 * @return
	 */
	private static boolean headCorrect(AceMention value, AceMention value2)
	{
		if(value == null || value2 == null)
		{
			return false;
		}
		Span head = value.extent;
		Span head2 = value2.extent;
		if(value instanceof AceEntityMention)
		{
			head = ((AceEntityMention) value).head;
		}
		if(value2 instanceof AceEntityMention)
		{
			head2 = ((AceEntityMention) value2).head;
		}
		
		if(head.equals(head2))
		{
			return true;
		}
		
		if(value2.getParent() instanceof AceEntity)
		{
			AceEntity parent2 = (AceEntity) value2.getParent();
			for(AceMention coref : parent2.mentions)
			{
				if(coref instanceof AceEntityMention)
				{
					head2 = ((AceEntityMention) coref).head;
				}
				else
				{
					head2 = coref.extent;
				}
				if(head.equals(head2))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static List<AceEventMentionArgument> getArguments(List<AceEventMention> mentions)
	{
		List<AceEventMentionArgument> ret = new ArrayList<AceEventMentionArgument>();
		for(AceEventMention mention : mentions)
		{		
			for(AceEventMentionArgument arg : mention.arguments)
			{
				// remove timex and value
				// later on we may need add them back
				if(arg.value instanceof AceTimexMention || 
						arg.value instanceof AceValueMention)
				{
					;
				}
				else
				{
					ret.add(arg);
				}
			}
		}
		return ret;
	}
	
	/**
	 * evaluate entity mentions (including NAM PRO NOM)
	 * @param stats
	 * @param entity_idf_scores 
	 * @param entity_scores 
	 * @param entity_scores_types 
	 * @param mentions_ans
	 * @param mentions_gold
	 */
	public static void evaluateEntity(Stats stats,
			Stats entity_idf_scores, 
			Map<String, Stats> entity_scores_types, List<AceEntityMention> mentions_ans, List<AceEntityMention> mentions_gold)
	{
		stats.num_gold += mentions_gold.size();
		stats.num_ans += mentions_ans.size();
		entity_idf_scores.num_gold += mentions_gold.size();
		entity_idf_scores.num_ans += mentions_ans.size();
		
		// for breakdown information
		for(AceEntityMention ans : mentions_ans)
		{
			String type = ans.getType();
			Stats score = entity_scores_types.get(type);
			if(score == null)
			{
				score = new Stats(type);
				entity_scores_types.put(type, score);
			}
			score.num_ans++;
		}
		// for breakdown information
		for(AceEntityMention gold : mentions_gold)
		{
			String type = gold.getType();
			Stats score = entity_scores_types.get(type);
			if(score == null)
			{
				score = new Stats(type);
				entity_scores_types.put(type, score);
			}
			score.num_gold++;
		}
		
		for(AceEntityMention ans : mentions_ans)
		{
			boolean flag = false;
			for(AceEntityMention gold : mentions_gold)
			{
				if(gold.head.overlap(ans.head))
				{
					stats.addConfusion(gold.getType(), ans.getType());
				}
				
				if(entityMentionCorrect(gold, ans))
				{
					stats.num_correct++;
					flag = true;
					
					// for breakdown information
					String type = gold.getType();
					Stats score = entity_scores_types.get(type);
					if(score == null)
					{
						score = new Stats(type);
						entity_scores_types.put(type, score);
					}
					score.num_correct++;
					
					break;
				}
			}
			
			if(flag == false)
			{
				// This is for debugging. probably there is an error
				System.out.println("probably gold standard scope error: " + ans.headText);
			}
			
			// for identification scores
			for(AceEntityMention gold : mentions_gold)
			{
				if(entityMentionCorrectIDF(gold, ans))
				{
					entity_idf_scores.num_correct++;
					break;
				}
			}
		}
	}
	
	/**
	 * evaluate relation mentions
	 * adopt the evaluation metric in Chan and Roth (ACL 2011)
	 * @param stats
	 * @param relation_scores_subtype 
	 * @param mentions_ans
	 * @param mentions_gold
	 */
	public static void evaluateRelation(Stats stats_strict, Stats stats, Map<String, Stats> scores_subtype, 
			List<AceRelationMention> mentions_ans, List<AceRelationMention> mentions_gold)
	{
		// remove "DISC" if any, as in (Chan and Roth, ACL 2011)
		for(Iterator<AceRelationMention> iter = mentions_ans.iterator(); iter.hasNext();)
		{
			if(iter.next().getType().equals("DISC"))
			{
				iter.remove();
				System.out.println("removed DISC in ans ");
			}
		}
		for(Iterator<AceRelationMention> iter = mentions_gold.iterator(); iter.hasNext();)
		{
			if(iter.next().getType().equals("DISC"))
			{
				iter.remove();
				System.out.println("removed DISC in gold ");
			}
		}
		
		// remove implicit relation mention as in (Chan and Roth, ACL 2011)
		List<AceRelationMention> to_remove = new ArrayList<AceRelationMention>();
		for(AceRelationMention ans : mentions_ans)
		{
			boolean false_pos = true;
			boolean remove = false;
			for(AceRelationMention gold : mentions_gold)
			{
				if(gold.arg1.head.equals(ans.arg1.head)
						&& gold.arg2.head.equals(ans.arg2.head) || gold.arg1.head.equals(ans.arg2.head)
							&& gold.arg2.head.equals(ans.arg1.head))
				{
					false_pos = false;
					break;
				}
			}
			if(false_pos)
			{
				for(AceRelationMention gold : mentions_gold)
				{
					AceEntity gold_entity = null;
					AceEntityMention ans_entity_mention = null;
					if(gold.arg1.head.equals(ans.arg1.head) 
							&& gold.getType().equals(ans.getType()))
					{
						gold_entity = gold.arg2.entity;
						ans_entity_mention = ans.arg2;
					}
					else if(gold.arg2.head.equals(ans.arg2.head) 
							&& gold.getType().equals(ans.getType()))
					{
						gold_entity = gold.arg1.entity;
						ans_entity_mention = ans.arg1;
					}
					if(gold_entity != null)
					{
						for(AceEntityMention corf : gold_entity.mentions)
						{
							if(corf.head.equals(ans_entity_mention.head))
							{
								to_remove.add(ans);
								remove = true;
								break;
							}
						}
					}
					if(remove)
					{
						break;
					}
				}
			}
		}
		mentions_ans.removeAll(to_remove);
		System.out.println("log: removed " + to_remove.size());
		
		// count total number
		stats.num_gold += mentions_gold.size();
		stats.num_ans += mentions_ans.size();
		
		stats_strict.num_gold += mentions_gold.size();
		stats_strict.num_ans += mentions_ans.size();
		
		// count total num for subtypes
		for(AceRelationMention ans : mentions_ans)
		{
			String ans_type = ans.getType();
			Stats temp = scores_subtype.get(ans_type);
			if(temp == null)
			{
				temp = new Stats(ans_type);
				scores_subtype.put(ans_type, temp);
			}
			temp.num_ans++;
		}
		for(AceRelationMention gold : mentions_gold)
		{
			String gold_type = gold.getType();
			Stats temp = scores_subtype.get(gold_type);
			if(temp == null)
			{
				temp = new Stats(gold_type);
				scores_subtype.put(gold_type, temp);
			}
			temp.num_gold++;
		}
		
		// count # of correct
		for(AceRelationMention ans : mentions_ans)
		{
			String ans_type = ans.getType();
			
			for(AceRelationMention gold : mentions_gold)
			{
				if(gold.arg1.head.overlap(ans.arg1.head) && gold.arg2.head.overlap(ans.arg2.head)
						|| gold.arg1.head.overlap(ans.arg2.head) && gold.arg2.head.overlap(ans.arg1.head))
				{
					stats.addConfusion(gold.getType(), ans.getType());
				}
				
				if(isCorrect(gold, ans))
				{
					stats.num_correct++;
					
					// count correct for the subtype
					Stats temp = scores_subtype.get(ans_type);
					if(temp == null)
					{
						scores_subtype.put(ans_type, new Stats(ans_type));
					}
					temp.num_correct++;
					
					break;
				}
			}
		}
		
		// count # of correct strict
		for(AceRelationMention ans : mentions_ans)
		{
			for(AceRelationMention gold : mentions_gold)
			{
				if(isCorrectStrict(gold, ans))
				{
					stats_strict.num_correct++;
					break;
				}
			}
		}
	}
	
	protected static boolean isCorrectStrict(AceRelationMention r1, AceRelationMention r2)
	{
		boolean ret = false;
		// coarse-grained directed
		ret = entityMentionCorrect(r1.arg1, r2.arg1) 
				&& entityMentionCorrect(r1.arg2, r2.arg2) && r1.getType().equals(r2.getType());
		return ret;
	}
	
	protected static boolean isCorrect(AceRelationMention r1, AceRelationMention r2)
	{
		boolean ret = false;
		// coarse-grained directed
		ret = relationArgCorrect(r1.arg1, r2.arg1) 
		&& relationArgCorrect(r1.arg2, r2.arg2) && r1.getType().equals(r2.getType());
		return ret;
	}
	
	private static boolean relationArgCorrect(AceEntityMention gold,
			AceEntityMention ans)
	{
		return gold.head.equals(ans.head);
	}

	/**
	 * check if two entity are same coreference
	 * @param value
	 * @param value2
	 * @return
	 */
	private static boolean entityMentionCorrect(AceEntityMention value, AceEntityMention value2)
	{
		if(value == null || value2 == null)
		{
			return false;
		}
		Span head = value.head;
		Span head2 = value2.head;
		
		if(head.equals(head2) && value.getType().equals(value2.getType()))
		{
			return true;
		}
		return false;
	}
	
	private static boolean entityMentionCorrectIDF(AceEntityMention value, AceEntityMention value2)
	{
		if(value == null || value2 == null)
		{
			return false;
		}
		Span head = value.head;
		Span head2 = value2.head;
		
		if(head.equals(head2))
		{
			return true;
		}
		return false;
	}

	static public void main(String[] args) throws DocumentException, IOException
	{	
		if(args.length < 4)
		{
			System.out.println("Scorer Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans Dir");
			System.out.println("args[2]: file list");
			System.out.println("args[3]: year (2004 or 2005)");
			System.out.println("args[4]: output file");
			
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir = new File(args[1]);
		File filelist = new File(args[2]);
		String year = args[3];
		
		PrintStream out = System.out;
		if(args.length >= 5)
		{
			File output = new File(args[4]);
			out = new PrintStream(output);
		}
		doAnalysis(goldDir, ansDir, filelist, out, year);
		if(out != System.out)
		{
			out.close();
		}
	}
}
