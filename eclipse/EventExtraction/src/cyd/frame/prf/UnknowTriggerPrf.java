package cyd.frame.prf;

import java.io.*;
import java.util.*;

import util.WordNetWrapper;
import cyd.frame.TMP;
import cyd.util.StopWord;

public class UnknowTriggerPrf
{
	// 需要去除停用词哈
	public static void unknow_trigger_prf(String not_in_train_path,
			String test_feat_path, String answer_path) throws Exception
	{
		HashSet<String> unknown_trigger_set = new HashSet<String>();
		BufferedReader br = new BufferedReader(
				new FileReader(not_in_train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				unknown_trigger_set.add(strs[i]);
			}
		}
		br.close();

		ArrayList<String> answer_list = new ArrayList<String>();

		br = new BufferedReader(new FileReader(answer_path));
		while ((line = br.readLine()) != null)
		{
			answer_list.add(line);
		}
		br.close();

		int index = 0, correct = 0, p = 0, r = 0;
		br = new BufferedReader(new FileReader(test_feat_path));
		while ((line = br.readLine()) != null)
		{
			String answer = answer_list.get(index++);

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

			if (unknown_trigger_set.contains(lu))
			{
				String[] tmps = answer.split(" : ");
				String label = tmps[0];
				String predict = tmps[1];
				if (label.equals(predict) && label.equals("O") == false)
				{
					++correct;
				}

				if (label.equals("O") == false)
				{
					++r;
				}

				if (predict.equals("O") == false)
				{
					++p;
				}
			}
		}
		br.close();

		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.println("P: " + String.format("%.2f", P * 100));
		System.out.println("R: " + String.format("%.2f", R * 100));
		System.out.println("F: " + String.format("%.2f", F * 100));

	}

	public static void main(String[] args) throws Exception
	{

		String not_in_train_path = "./cyd/triggers_not_in_train.txt";
//		String test_feat_path = "./cyd/wordnet_expand/feat_test.txt";
//		String answer_path = "./cyd/wordnet_expand/trigger_result";
//		String test_feat_path = "./cyd/framenet_expand/feat_test";
//		String answer_path = "./cyd/framenet_expand/trigger_result";
		String test_feat_path = "./cyd/trigger_test_feature";
		String answer_path = "./cyd/trigger_result";
		
		unknow_trigger_prf(not_in_train_path, test_feat_path, answer_path);

		System.exit(0);
	}
}
