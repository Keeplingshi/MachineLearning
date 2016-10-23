package cyd.frame.expand;

import java.io.*;
import java.util.*;

import util.WordNetWrapper;
import cyd.frame.TMP;
import cyd.util.StopWord;

public class WordNetExpand
{

	// n->NN  v->VB  a->JJ  adv->RB
	public static String pos_small_to_big(String pos) throws Exception
	{
		if (pos.equals("n"))
		{
			pos = "NN";
		}
		else if (pos.equals("v"))
		{
			pos = "VB";
		}
		else if (pos.equals("a"))
		{
			pos = "JJ";
		}
		else if (pos.equals("adv"))
		{
			pos = "RB";
		}
		return pos;
	}

	public static void wordnet_expand_recall_cnt(String in_train_path,
			String not_in_train_path) throws Exception
	{
		HashSet<String> trigger_set = new HashSet<String>();

		// 读取训练集中的lu和用最相似wordnet同义词集的lu
		BufferedReader br = new BufferedReader(new FileReader(in_train_path));
		String line = null;
		br = new BufferedReader(new FileReader(in_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");

			// 加入event自身的trigger
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);

				String word = strs[i].substring(0, strs[i].lastIndexOf("."));
				String small_pos = strs[i]
						.substring(strs[i].lastIndexOf(".") + 1);
				String pos = pos_small_to_big(small_pos);
				List<String> synonyms = WordNetWrapper.getSingleTon()
						.getSynonyms(word, pos);
				if (synonyms != null)
				{
					for (String str : synonyms)
					{
						trigger_set.add(str + "." + small_pos);
					}
				}
			}
		}
		br.close();

		br = new BufferedReader(new FileReader(not_in_train_path));

		int child = 0, parent = 0;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");

			// 加入event自身的trigger
			for (int i = 1; i < strs.length; ++i)
			{
				if (trigger_set.contains(strs[i]) == true)
				{
					System.out.println(strs[0] + " : " + strs[i]);
					++child;
				}
				++parent;
			}
		}
		br.close();

		System.out.println(child + " : " + parent);
	}

	public static void wordnet_expand(String train_filelist_path,
			String dir_path, String in_train_path,
			String train_trigger_feat_path) throws Exception
	{
		HashSet<String> trigger_set = new HashSet<String>();

		// 读取训练集中的lu和用最相似wordnet同义词集的lu
		BufferedReader br = new BufferedReader(new FileReader(in_train_path));
		String line = null;
		br = new BufferedReader(new FileReader(in_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");

			// 加入event自身的trigger
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);

				String word = strs[i].substring(0, strs[i].lastIndexOf("."));
				String small_pos = strs[i]
						.substring(strs[i].lastIndexOf(".") + 1);
				String pos = pos_small_to_big(small_pos);
				List<String> synonyms = WordNetWrapper.getSingleTon()
						.getSynonyms(word, pos);
				if (synonyms != null)
				{
					for (String str : synonyms)
					{
						trigger_set.add(str + "." + small_pos);
					}
				}
			}
		}
		br.close();

		FileWriter fw = new FileWriter(train_trigger_feat_path);

		br = new BufferedReader(new FileReader(train_filelist_path));

		while ((line = br.readLine()) != null)
		{
			String fileName = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ fileName));

			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				String lemma = strs[3];
				lemma = lemma.substring(lemma.indexOf("=") + 1,
						lemma.lastIndexOf(":"));

				if (StopWord.getSingleTon().is_stopword(lemma) == true)
				{
					continue;
				}

				String pos = null;
				for (String str : strs)
				{
					if (str.startsWith("POS="))
					{
						pos = str.substring(str.indexOf("=") + 1,
								str.lastIndexOf(":"));
						pos = TMP.get_pos(pos);
					}
				}

				String lu = lemma + "." + pos;

				if (trigger_set.contains(lu))
				{
					fw.write(line + "\n");
				}

			}
			file_br.close();
		}
		br.close();
		fw.close();
	}

	public static void main(String[] args) throws Exception
	{

		String in_train_path = "./cyd/triggers_in_train.txt";
		String not_in_train_path = "./cyd/triggers_not_in_train.txt";

		String dir_path = "./tmp/featDir/";
		String train_filelist_path = "./cyd/filelist/new_filelist_ACE_training";
		String test_filelist_path = "./cyd/filelist/new_filelist_ACE_test";

		String train_trigger_feat_path = "./cyd/wordnet_expand/feat_train.txt";

		//		wordnet_expand_recall_cnt(in_train_path, not_in_train_path);

//		wordnet_expand(train_filelist_path, dir_path, in_train_path,
//				train_trigger_feat_path);

		String test_trigger_feat_path = "./cyd/wordnet_expand/feat_test.txt";

//		wordnet_expand(test_filelist_path, dir_path, in_train_path,
//				test_trigger_feat_path);
		
		List<String> synonyms = WordNetWrapper.getSingleTon()
				.getSynonyms("move", "VB");
		if (synonyms != null)
		{
			for (String str : synonyms)
			{
				System.out.println(str);
			}
		}
		
		System.exit(0);
	}
}
