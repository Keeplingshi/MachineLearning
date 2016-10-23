package ace.acetypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;
import org.dom4j.DocumentException;

import ace.acetypes.RelationScorer.Stats;


/**
 * This class compare the paired t-test on two sets 
 * of relation classification results
 * @author qli
 *
 */
public class RelationScoreTTest
{
	static public void main(String[] args) throws DocumentException, IOException
	{	
		if(args.length < 6)
		{
			System.out.println("Scorer Usage:");
			System.out.println("args[0]: gold Dir");
			System.out.println("args[1]: ans1 Dir");
			System.out.println("args[2]: ans2 Dir");
			System.out.println("args[3]: file list");
			System.out.println("args[4]: year (2004 or 2005)");
			System.out.println("args[5]: granularity");
			System.exit(-1);
		}
		
		File goldDir = new File(args[0]);
		File ansDir1 = new File(args[1]);
		File ansDir2 = new File(args[2]);
		File filelist = new File(args[3]);
		String year = args[4];
		RelationScorer.granularity = Integer.parseInt(args[5]);
		
		doAnalysis(goldDir, ansDir1, ansDir2, filelist, year);
	}

	private static void doAnalysis(File goldDir, File ansDir1, File ansDir2,
			File filelist, String year) throws IOException
	{
		List<Double> scores1 = new ArrayList<Double>();
		List<Double> scores2 = new ArrayList<Double>();
		
		BufferedReader reader = new BufferedReader(new FileReader(filelist));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			File apf_ans1 = new File(ansDir1 + File.separator + line + ".apf.xml");
			File apf_ans2 = new File(ansDir2 + File.separator + line + ".apf.xml");
			File apf_gold = new File(goldDir + File.separator + line + ".apf.xml");
			File textFile = new File(goldDir + File.separator + line + ".sgm");
			
			AceDocument doc_ans1 = new AceDocument(textFile.getAbsolutePath(), apf_ans1.getAbsolutePath(), year);
			AceDocument doc_ans2 = new AceDocument(textFile.getAbsolutePath(), apf_ans2.getAbsolutePath(), year);
			AceDocument doc_gold = new AceDocument(textFile.getAbsolutePath(), apf_gold.getAbsolutePath(), year);
			
			// evaluate 1
			Stats relation_scores_strict1 = new Stats("RelationStrict");
			Stats relation_scores1 = new Stats("Relation");
			Stats entity_scores1 = new Stats("Entity");
			Map<String, Stats> relation_scores_types1 = new HashMap<String, Stats>();
			Map<String, Stats> entity_scores_types1 = new HashMap<String, Stats>();
			Stats entity_idf_scores1 = new Stats("Entity_IDF");
			
			// evalute relation mentions 1
			List<AceRelationMention> mentions_ans_1 = doc_ans1.relationMentions;
			List<AceRelationMention> mentions_gold = doc_gold.relationMentions;
			RelationScorer.evaluateRelation(relation_scores_strict1, relation_scores1, 
					relation_scores_types1, mentions_ans_1, mentions_gold);
			
			// evaluate entity mentions 1
			List<AceEntityMention> entity_mentions_ans_1 = doc_ans1.entityMentions;
			List<AceEntityMention> entity_mentions_gold = doc_gold.entityMentions;
			RelationScorer.evaluateEntity(entity_scores1, entity_idf_scores1,
					entity_scores_types1, entity_mentions_ans_1, entity_mentions_gold);
			
			Stats relation_scores_strict2 = new Stats("RelationStrict");
			Stats relation_scores2 = new Stats("Relation");
			Stats entity_scores2 = new Stats("Entity");
			Map<String, Stats> relation_scores_types2 = new HashMap<String, Stats>();
			Map<String, Stats> entity_scores_types2 = new HashMap<String, Stats>();
			Stats entity_idf_scores2 = new Stats("Entity_IDF");
			
			List<AceRelationMention> mentions_ans_2 = doc_ans2.relationMentions;
			RelationScorer.evaluateRelation(relation_scores_strict2, relation_scores2, 
					relation_scores_types2, mentions_ans_2, mentions_gold);
			
			// evaluate entity mentions 1
			List<AceEntityMention> entity_mentions_ans_2 = doc_ans2.entityMentions;
			RelationScorer.evaluateEntity(entity_scores2, entity_idf_scores2,
					entity_scores_types2, entity_mentions_ans_2, entity_mentions_gold);
			
			relation_scores1.calc();
			relation_scores2.calc();
			if(Double.isNaN(relation_scores1.f1))
			{
				relation_scores1.f1 = 0.0;
			}
			if(Double.isNaN(relation_scores2.f1))
			{
				relation_scores2.f1 = 0.0;
			}
			scores1.add(relation_scores1.f1);
			scores2.add(relation_scores2.f1);
			
		}
		reader.close();
	
		TTest ttest = new TTest(); 
		double pairedT_relationf = ttest.pairedTTest(convert(scores1), convert(scores2));
		System.out.println(pairedT_relationf);
	}
	
	static public double[] convert(List<Double> list)
	{
		double[] array = new double[list.size()];
		int i=0;
		for(double d : list)
		{
			array[i++] = d;
		}
		return array;
	}
}
