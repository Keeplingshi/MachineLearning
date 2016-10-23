package cyd.frame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TMP
{

	public static void event_trigger(String filelist_path, String dir_path,
			String result_path) throws Exception
	{
		HashMap<String, HashSet<String>> event_triggers_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(filelist_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String fileName = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ fileName));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				if (strs[1].equals("O") == false)
				{
					String event = strs[1];
					String word = strs[2];
					word = word.substring(word.indexOf("=") + 1,
							word.lastIndexOf(":"));
					String lemma = strs[3];
					lemma = lemma.substring(lemma.indexOf("=") + 1,
							lemma.lastIndexOf(":"));
					String pos = null;
					for (String str : strs)
					{
						if (str.startsWith("POS="))
						{
							pos = str;
							break;
						}
					}
					pos = pos.substring(pos.indexOf("=") + 1,
							pos.lastIndexOf(":"));

					if (event_triggers_map.containsKey(event) == false)
					{
						HashSet<String> set = new HashSet<String>();
						set.add(word + " || " + lemma + " || " + pos);
						event_triggers_map.put(event, set);
					}
					else
					{
						HashSet<String> set = event_triggers_map.get(event);
						set.add(word + " || " + lemma + " || " + pos);
						event_triggers_map.put(event, set);
					}
				}
			}
		}
		br.close();

		FileWriter fw = new FileWriter(result_path);
		for (Map.Entry entry : event_triggers_map.entrySet())
		{
			String event = (String) entry.getKey();
			fw.write(event);
			HashSet<String> set = (HashSet<String>) entry.getValue();
			for (String val : set)
			{
				fw.write(" : " + val);
			}
			fw.write("\n");
		}
		fw.close();
	}

	public static String get_pos(String pos) throws Exception
	{
		if (pos.startsWith("N"))
		{
			pos = "n";
		}
		else if (pos.startsWith("V"))
		{
			pos = "v";
		}
		else if (pos.startsWith("J"))
		{
			pos = "a";
		}
		else if (pos.startsWith("RB"))
		{
			pos = "adv";
		}
		else if (pos.equals("IN") || pos.equals("TO"))
		{
			pos = "prep";
		}
		return pos;
	}

	public static void trigger_allpos(String dir_path, String result_path)
			throws Exception
	{
		HashSet<String> pos_set = new HashSet<String>();
		HashMap<String, Integer> pos_cnt_map = new HashMap<String, Integer>();

		int total = 0;

		File dir = new File(dir_path);
		File[] files = dir.listFiles();
		for (File file : files)
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				String event_type = strs[1];
				if (event_type.equals("O") == false)
				{
					String pos = null;
					for (String str : strs)
					{
						if (str.startsWith("POS="))
						{
							pos = str;
							break;
						}
					}

					pos = pos.substring(pos.indexOf("=") + 1,
							pos.lastIndexOf(":"));

					pos_set.add(pos);

					pos = get_pos(pos);

					if (pos_cnt_map.containsKey(pos) == false)
					{
						pos_cnt_map.put(pos, 1);
					}
					else
					{
						pos_cnt_map.put(pos, pos_cnt_map.get(pos) + 1);
					}

					++total;
				}
			}
			br.close();
		}

		FileWriter fw = new FileWriter(result_path);
		for (String pos : pos_set)
		{
			System.out.println(pos);
			fw.write(pos + "\n");
		}
		fw.close();

		for (Map.Entry entry : pos_cnt_map.entrySet())
		{
			String pos = (String) entry.getKey();
			int cnt = (Integer) entry.getValue();
			double val = (double) cnt / total;
			System.out.println(pos + " : " + String.format("%.4f", val));
		}
	}

	public static void trigger_not_in_train(String filelist_path,
			String dir_path, String train_path, String result_path)
			throws Exception
	{
		HashMap<String, HashSet<String>> event_triggers_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			String event_type = strs[0];
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				String[] tmps = strs[i].split(" \\|\\| ");
				String pos = tmps[2];
				pos = get_pos(pos);
				set.add(tmps[1] + "." + pos);
			}
			event_triggers_map.put(event_type, set);
		}
		br.close();

		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();

		br = new BufferedReader(new FileReader(filelist_path));
		while ((line = br.readLine()) != null)
		{
			String fileName = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ fileName));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				if (strs[1].equals("O") == false)
				{
					String event = strs[1];
					String word = strs[2];
					word = word.substring(word.indexOf("=") + 1,
							word.lastIndexOf(":"));
					String lemma = strs[3];
					lemma = lemma.substring(lemma.indexOf("=") + 1,
							lemma.lastIndexOf(":"));

					String pos = null;
					for (String str : strs)
					{
						if (str.startsWith("POS="))
						{
							pos = str;
							break;
						}
					}
					pos = pos.substring(pos.indexOf("=") + 1,
							pos.lastIndexOf(":"));
					pos = get_pos(pos);

					HashSet<String> set = event_triggers_map.get(event);
					if (set.contains(lemma + "." + pos) == false)
					{
						if (map.containsKey(event) == false)
						{
							HashSet<String> tmp_set = new HashSet<String>();
							tmp_set.add(lemma + "." + pos);
							map.put(event, tmp_set);
						}
						else
						{
							HashSet<String> tmp_set = map.get(event);
							tmp_set.add(lemma + "." + pos);
						}
					}

				}
			}
		}
		br.close();

		FileWriter fw = new FileWriter(result_path);
		for (Map.Entry entry : map.entrySet())
		{
			String event = (String) entry.getKey();
			fw.write(event);
			HashSet<String> set = (HashSet<String>) entry.getValue();
			for (String val : set)
			{
				fw.write(" : " + val);
			}
			fw.write("\n");
		}
		fw.close();

	}

	public static void change_trigger_pos_format(String src_path,
			String dst_path) throws Exception
	{
		HashMap<String, HashSet<String>> event_triggers_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(src_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			String event_type = strs[0];
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				String[] tmps = strs[i].split(" \\|\\| ");
				String pos = tmps[2];
				pos = get_pos(pos);
				set.add(tmps[1] + "." + pos);
			}
			event_triggers_map.put(event_type, set);
		}
		br.close();

		FileWriter fw = new FileWriter(dst_path);
		for (Map.Entry entry : event_triggers_map.entrySet())
		{
			String event = (String) entry.getKey();
			fw.write(event);
			HashSet<String> set = (HashSet<String>) entry.getValue();
			for (String val : set)
			{
				fw.write(" : " + val);
			}
			fw.write("\n");
		}
		fw.close();
	}

	// 计算每个触发词（包括词性）的事件类型分布，分布中包含空事件
	public static void trigger_rate(String src_path, String trainlist_path,
			String dir_path, String dst_path) throws Exception
	{
		HashMap<String, HashMap<String, Integer>> trigger_eventCnt_map = new HashMap<String, HashMap<String, Integer>>();

		BufferedReader br = new BufferedReader(new FileReader(src_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				trigger_eventCnt_map.put(strs[i], map);
			}
		}
		br.close();

		br = new BufferedReader(new FileReader(trainlist_path));
		while ((line = br.readLine()) != null)
		{
			line = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ line));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");

				String event_type = strs[1];
				String lemma = strs[3];
				lemma = lemma.substring(lemma.indexOf("=") + 1,
						lemma.lastIndexOf(":"));

				String pos = null;
				for (String str : strs)
				{
					if (str.startsWith("POS="))
					{
						pos = str.substring(str.indexOf("=") + 1,
								str.lastIndexOf(":"));
					}
				}
				pos = get_pos(pos);

				String lu = lemma + "." + pos;
				if (trigger_eventCnt_map.containsKey(lu))
				{
					HashMap<String, Integer> map = trigger_eventCnt_map.get(lu);
					if (map.containsKey(event_type) == false)
					{
						map.put(event_type, 1);
					}
					else
					{
						map.put(event_type, map.get(event_type) + 1);
					}
				}
			}
			file_br.close();
		}
		br.close();

		FileWriter fw = new FileWriter(dst_path);
		for (Map.Entry entry : trigger_eventCnt_map.entrySet())
		{
			String lu = (String) entry.getKey();
			fw.write(lu);
			HashMap<String, Integer> map = (HashMap<String, Integer>) entry
					.getValue();
			int total = 0;
			for (Map.Entry child_entry : map.entrySet())
			{
				int cnt = (Integer) child_entry.getValue();
				total += cnt;
			}
			List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
					map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
			{
				public int compare(Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2)
				{
					return (o2.getValue() - o1.getValue());
				}
			});
			for (Map.Entry child_entry : list)
			{
				String event_type = (String) child_entry.getKey();
				int cnt = (Integer) child_entry.getValue();
				double val = (double) cnt / total;
				String val_str = String.format("%.4f", val);
				fw.write(" || " + event_type + " : " + val_str);
			}
			fw.write("\n");
		}
		fw.close();
	}

	// 1. 取测试集中最大概率的n, v, a, adv的值    2.取非O的值
	public static void maxlikely_prf(String rate_path, String dir_path,
			String test_path) throws Exception
	{
		HashSet<String> pos_set = new HashSet<String>();
		pos_set.add("n");
		pos_set.add("v");
		pos_set.add("a");
		pos_set.add("adv");

		HashMap<String, ArrayList<String>> trigger_eventRate_map = new HashMap<String, ArrayList<String>>();

		BufferedReader br = new BufferedReader(new FileReader(rate_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");

			ArrayList<String> list = new ArrayList<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				list.add(strs[i]);
			}
			trigger_eventRate_map.put(strs[0], list);
		}
		br.close();

		int correct = 0, p = 0, r = 0;

		br = new BufferedReader(new FileReader(test_path));
		while ((line = br.readLine()) != null)
		{
			line = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ line));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");

				String event_type = strs[1];
				String lemma = strs[3];
				lemma = lemma.substring(lemma.indexOf("=") + 1,
						lemma.lastIndexOf(":"));

				String pos = null;
				for (String str : strs)
				{
					if (str.startsWith("POS="))
					{
						pos = str.substring(str.indexOf("=") + 1,
								str.lastIndexOf(":"));
					}
				}
				pos = get_pos(pos);

				String lu = lemma + "." + pos;
				if (trigger_eventRate_map.containsKey(lu)
						&& pos_set.contains(pos))
				{

					ArrayList<String> list = trigger_eventRate_map.get(lu);
					String tmp = list.get(0);
					String event = tmp.substring(0, tmp.indexOf(" :"));
					if (event.equals("O") && list.size() >= 2)
					{
						tmp = list.get(1);
						event = tmp.substring(0, tmp.indexOf(" :"));
					}

					if (event.equals(event_type)
							&& event_type.equals("O") == false)
					{
						++correct;
					}
					++p;
				}

				if (event_type.equals("O") == false)
				{
					++r;
				}

			}
			file_br.close();
		}
		br.close();

		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.print("P: " + String.format("%.2f", P * 100));
		System.out.print("  R: " + String.format("%.2f", R * 100));
		System.out.print("  F: " + String.format("%.2f", F * 100));
	}

	// 未登录词占总数的比率，类型比率
	public static void unknow_trigger_rate_type(String train_path,
			String not_train_path) throws Exception
	{
		int total = 0, unknown = 0;

		HashSet<String> trigger_set = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
			}
			total += strs.length - 1;
		}
		br.close();

		br = new BufferedReader(new FileReader(not_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
			}
			unknown += strs.length - 1;
		}
		br.close();

		System.out.println((double) unknown / (unknown + total));
	}

	// 未登录词占总数的比率，类型比率
	public static void unknow_trigger_rate_num(String dir_path,
			String train_path, String test_path) throws Exception
	{
		HashMap<String, Integer> train_map = new HashMap<String, Integer>();

		BufferedReader br = new BufferedReader(new FileReader(train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ line));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				String event_type = strs[1];
				if (event_type.equals("O"))
				{
					continue;
				}
				String lemma = strs[3];
				lemma = lemma.substring(lemma.indexOf("=") + 1,
						lemma.lastIndexOf(":"));

				String pos = null;
				for (String str : strs)
				{
					if (str.startsWith("POS="))
					{
						pos = str.substring(str.indexOf("=") + 1,
								str.lastIndexOf(":"));
					}
				}
				pos = get_pos(pos);

				String lu = lemma + "." + pos;
				if (train_map.containsKey(lu) == false)
				{
					train_map.put(lu, 1);
				}
				else
				{
					train_map.put(lu, train_map.get(lu) + 1);
				}
			}
			file_br.close();
		}
		br.close();

		int unknown = 0;

		br = new BufferedReader(new FileReader(test_path));
		while ((line = br.readLine()) != null)
		{
			line = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(dir_path
					+ line));
			while ((line = file_br.readLine()) != null)
			{
				String[] strs = line.split(" ");
				String event_type = strs[1];
				if (event_type.equals("O"))
				{
					continue;
				}
				String lemma = strs[3];
				lemma = lemma.substring(lemma.indexOf("=") + 1,
						lemma.lastIndexOf(":"));

				String pos = null;
				for (String str : strs)
				{
					if (str.startsWith("POS="))
					{
						pos = str.substring(str.indexOf("=") + 1,
								str.lastIndexOf(":"));
					}
				}
				pos = get_pos(pos);

				String lu = lemma + "." + pos;
				if (train_map.containsKey(lu) == false)
				{
					++unknown;
				}
			}
			file_br.close();
		}
		br.close();

		int total = 0;
		for (Map.Entry entry : train_map.entrySet())
		{
			int cnt = (Integer) entry.getValue();
			total += cnt;
		}
		System.out.println((double) unknown / (total + unknown));
	}

	public static void main(String[] args) throws Exception
	{
		//				event_trigger("./cyd/filelist/new_filelist_ACE_training",
		//						"./tmp/featDir/", "./cyd/event_triggers.txt");
		//		trigger_not_in_train("./cyd/filelist/new_filelist_ACE_test",
		//				"./tmp/featDir/", "./cyd/event_triggers.txt",
		//				"./cyd/trigger_not_in_train.txt");
		//		change_trigger_pos_format("./cyd/event_triggers.txt", "./cyd/triggers_in_train.txt");
		//		trigger_allpos("./tmp/featDir/", "./cyd/sta/trigger_allpos.txt");
		//		trigger_rate("./cyd/triggers_in_train.txt",
		//				"./cyd/filelist/new_filelist_ACE_training", "./tmp/featDir/",
		//				"./cyd/trigger_rate.txt");

		String rate_path = "./cyd/trigger_rate.txt";
		String dir_path = "./tmp/featDir/";
		String train_path = "./cyd/filelist/new_filelist_ACE_training";
		String test_path = "./cyd/filelist/new_filelist_ACE_test";
		//		maxlikely_prf(rate_path, dir_path, test_path);

		String trigger_train_path = "./cyd/triggers_in_train.txt";
		String trigger_not_in_train_path = "./cyd/trigger_not_in_train.txt";
		//		unknow_trigger_rate_type(trigger_train_path, trigger_not_in_train_path);
		unknow_trigger_rate_num(dir_path, train_path, test_path);

		System.exit(0);
	}
}
