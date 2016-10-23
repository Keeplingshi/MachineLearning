package cyd.frame.expand;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;

import cyd.frame.TMP;
import cyd.util.*;

public class FrameNetExpand
{
	public static void frame_lu_map(String result_path) throws Exception
	{
		File dir = new File("F:\\研究\\目标词识别\\corpus\\fndata-1.5\\frame");
		File[] files = dir.listFiles();

		HashMap<String, String> namespace = new HashMap<String, String>();
		namespace.put("edu", "http://framenet.icsi.berkeley.edu");
		SAXReader reader = new SAXReader();
		reader.getDocumentFactory().setXPathNamespaceURIs(namespace);

		FileWriter fw = new FileWriter(result_path);
		for (File file : files)
		{
			if (file.getName().endsWith(".xsl"))
			{
				continue;
			}
			String file_name = file.getName();
			String frame = file_name.substring(0, file_name.lastIndexOf("."));
			Document document = reader.read(file);
			List<Element> list = document
					.selectNodes("//edu:frame/edu:lexUnit");

			fw.write(frame);
			for (Element luElement : list)
			{
				String lu = luElement.attributeValue("name");
				fw.write(" || " + lu);
			}
			fw.write("\n");
		}
		fw.close();
	}

	public static void event_map_to_frame_wordnet(String event_path,
			String frame_path, String result_path) throws Exception
	{
		HashMap<String, HashSet<String>> frame_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(frame_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}
			frame_map.put(strs[0], set);
		}
		br.close();

		FileWriter fw = new FileWriter(result_path);

		int num = 0;
		br = new BufferedReader(new FileReader(event_path));
		while ((line = br.readLine()) != null)
		{
			++num;
			String[] strs = line.split(" : ");
			String event = strs[0];
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}

			HashMap<String, Double> frame_sim_map = new HashMap<String, Double>();

			int index = 0;
			for (Map.Entry entry : frame_map.entrySet())
			{
				++index;
				String frame = (String) entry.getKey();
				HashSet<String> lu_set = (HashSet<String>) entry.getValue();
				if (lu_set.size() == 0)
				{
					continue;
				}

				double sum = 0.0;
				for (String a : set)
				{
					String a_word = a.substring(0, a.lastIndexOf("."));
					String a_pos = a.substring(a.lastIndexOf(".") + 1);

					for (String b : lu_set)
					{
						String b_word = b.substring(0, b.lastIndexOf("."));
						String b_pos = b.substring(b.lastIndexOf(".") + 1);
						if (b_pos.equals(a_pos))
						{
							double sc = cyd.frame.sim.Similarity.getSingleton()
									.get_sim(a_word, b_word, a_pos);
							sum += sc;
						}
					}
				}

				double similarity = sum / (set.size() * lu_set.size());
				frame_sim_map.put(frame, similarity);
				System.out.println(num + " : " + event + " : " + index + " : "
						+ frame + " : " + similarity);
			}

			List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
					frame_sim_map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
			{
				public int compare(Map.Entry<String, Double> o1,
						Map.Entry<String, Double> o2)
				{
					return o2.getValue().compareTo(o1.getValue());
				}
			});

			fw.write(event);
			for (int i = 0; i < list.size(); ++i)
			{
				Map.Entry<String, Double> entry = list.get(i);
				String frame = entry.getKey();
				double sim = entry.getValue();
				fw.write(" || " + frame + " : " + sim);
			}
			fw.write("\n");
			if (list.size() > 0)
			{
				System.out.println(event + " : " + list.get(0).getKey() + " : "
						+ list.get(0).getValue());
			}
		}
		br.close();
		fw.close();
	}

	public static void event_map_to_frame_jaccard(String event_path,
			String frame_path, String result_path) throws Exception
	{
		HashMap<String, HashSet<String>> frame_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(frame_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}
			frame_map.put(strs[0], set);
		}
		br.close();

		FileWriter fw = new FileWriter(result_path);

		int num = 0;
		br = new BufferedReader(new FileReader(event_path));
		while ((line = br.readLine()) != null)
		{
			++num;
			String[] strs = line.split(" : ");
			String event = strs[0];
			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}

			HashMap<String, Double> frame_sim_map = new HashMap<String, Double>();

			int index = 0;
			for (Map.Entry entry : frame_map.entrySet())
			{
				++index;
				String frame = (String) entry.getKey();
				HashSet<String> lu_set = (HashSet<String>) entry.getValue();
				if (lu_set.size() == 0)
				{
					continue;
				}

				int jaccard = 0;
				for (String a : set)
				{
					if (lu_set.contains(a))
					{
						++jaccard;
					}
				}
				for (String a : set)
				{
					lu_set.add(a);
				}

				double similarity = (double) jaccard / lu_set.size();
				frame_sim_map.put(frame, similarity);
				System.out.println(num + " : " + event + " : " + index + " : "
						+ frame + " : " + similarity);
			}

			List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
					frame_sim_map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
			{
				public int compare(Map.Entry<String, Double> o1,
						Map.Entry<String, Double> o2)
				{
					return o2.getValue().compareTo(o1.getValue());
				}
			});

			fw.write(event);
			for (int i = 0; i < list.size(); ++i)
			{
				Map.Entry<String, Double> entry = list.get(i);
				String frame = entry.getKey();
				double sim = entry.getValue();
				fw.write(" || " + frame + " : " + sim);
			}
			fw.write("\n");
			if (list.size() > 0)
			{
				System.out.println(event + " : " + list.get(0).getKey() + " : "
						+ list.get(0).getValue());
			}
		}
		br.close();
		fw.close();
	}

	public static void cover_unknown(String not_in_train_path,
			String event_frame_path, String frame_lu_path, int top)
			throws Exception
	{
		HashMap<String, HashSet<String>> frame_lu_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(frame_lu_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String frame = strs[0];

			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}
			frame_lu_map.put(frame, set);
		}
		br.close();

		HashMap<String, HashSet<String>> event_frame_map = new HashMap<String, HashSet<String>>();

		br = new BufferedReader(new FileReader(event_frame_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String event = strs[0];
			HashSet<String> frame_set = new HashSet<String>();
			for (int i = 1; i <= top; ++i)
			{
				String frame = strs[i];
				frame = frame.substring(0, frame.indexOf(" :"));
				frame_set.add(frame);
			}

			event_frame_map.put(event, frame_set);
		}
		br.close();

		int child = 0, parent = 0;
		br = new BufferedReader(new FileReader(not_in_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			String event = strs[0];
			HashSet<String> frame_set = event_frame_map.get(event);

			HashSet<String> lu_set = new HashSet<String>();
			for (String frame : frame_set)
			{
				HashSet<String> set = frame_lu_map.get(frame);
				lu_set.addAll(set);
			}

			for (int i = 1; i < strs.length; ++i)
			{
				if (lu_set.contains(strs[i]))
				{
					++child;
					System.out.println(strs[i]);
				}
			}
			parent += strs.length - 1;
		}
		br.close();

		System.out.println(child + " : " + parent + " : " + (double) child
				/ parent);
	}
	
	public static void cover_unknown_lus(String not_in_train_path,
			String event_frame_path, String frame_lu_path, int top)
			throws Exception
	{
		HashMap<String, HashSet<String>> frame_lu_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(frame_lu_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String frame = strs[0];

			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}
			frame_lu_map.put(frame, set);
		}
		br.close();

		HashMap<String, HashSet<String>> event_frame_map = new HashMap<String, HashSet<String>>();

		br = new BufferedReader(new FileReader(event_frame_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String event = strs[0];
			HashSet<String> frame_set = new HashSet<String>();
			for (int i = 1; i <= top; ++i)
			{
				String frame = strs[i];
				frame = frame.substring(0, frame.indexOf(" :"));
				frame_set.add(frame);
			}

			event_frame_map.put(event, frame_set);
		}
		br.close();

		HashSet<String> lu_set = new HashSet<String>();
		for (Map.Entry entry : event_frame_map.entrySet())
		{
			HashSet<String> frame_set = (HashSet<String>) entry.getValue();
			for (String frame : frame_set)
			{
				HashSet<String> set =  frame_lu_map.get(frame);
				lu_set.addAll(set);
			}
		}
		
		int child = 0, parent = 0;
		br = new BufferedReader(new FileReader(not_in_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				if (lu_set.contains(strs[i]))
				{
					++child;
					System.out.println(strs[0] + " : "+ strs[i]);
				}
			}
			parent += strs.length - 1;
		}
		br.close();

		System.out.println(child + " : " + parent + " : " + (double) child
				/ parent);

	}

	public static void trigger_not_in_train_wrong(String not_in_train_path,
			String trigger_result_path, String triiger_result_feat_path)
			throws Exception
	{
		HashSet<String> trigger_set = new HashSet<String>();

		BufferedReader br = new BufferedReader(
				new FileReader(not_in_train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
			}
		}
		br.close();

		ArrayList<String> answer_list = new ArrayList<String>();
		br = new BufferedReader(new FileReader(trigger_result_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			answer_list.add(strs[1]);
		}
		br.close();

		int index = 0, p = 0, r = 0, parent = 0, child = 0;

		br = new BufferedReader(new FileReader(triiger_result_feat_path));
		while ((line = br.readLine()) != null)
		{
			String answer = answer_list.get(index++);
			String[] strs = line.split(" ");
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
					pos = TMP.get_pos(pos);
				}
			}
			String lu = lemma + "." + pos;

			if (trigger_set.contains(lu))
			{
				if (answer.equals("O") == false)
				{
					++p;
				}
				if (strs[1].equals("O") == false)
				{
					++r;
				}
				//				if (answer.equals("O") == false && strs[1].equals("O") == false)
				if (answer.equals(strs[1]) && answer.equals("O") == false)
				{
					++child;
				}
				++parent;
			}
		}
		br.close();

		System.out.println(child + " : " + p + " : " + r + " : " + parent);
	}

	public static void trigger_in_train_wrong(String not_in_train_path,
			String trigger_result_path, String triiger_result_feat_path)
			throws Exception
	{
		HashSet<String> trigger_set = new HashSet<String>();

		BufferedReader br = new BufferedReader(
				new FileReader(not_in_train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
			}
		}
		br.close();

		ArrayList<String> answer_list = new ArrayList<String>();
		br = new BufferedReader(new FileReader(trigger_result_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			answer_list.add(strs[1]);
		}
		br.close();

		int index = 0, p = 0, r = 0, parent = 0, child = 0;

		br = new BufferedReader(new FileReader(triiger_result_feat_path));
		while ((line = br.readLine()) != null)
		{
			String answer = answer_list.get(index++);
			String[] strs = line.split(" ");
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
					pos = TMP.get_pos(pos);
				}
			}
			String lu = lemma + "." + pos;

			if (trigger_set.contains(lu))
			{
				if (answer.equals("O") == false)
				{
					++p;
				}
				if (strs[1].equals("O") == false)
				{
					++r;
				}
				//				if (answer.equals("O") == false && strs[1].equals("O") == false)
				if (answer.equals(strs[1]) && answer.equals("O") == false)
				{
					++child;
				}
				++parent;
			}
		}
		br.close();

		System.out.println(child + " : " + p + " : " + r + " : " + parent);
	}

	public static void feat_only_train_lu(String train_filelist_path,
			String dir_path, String in_train_path,
			String train_trigger_feat_path) throws Exception
	{
		HashSet<String> trigger_set = new HashSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(in_train_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
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

				//				if (StopWord.getSingleTon().is_stopword(lemma) == true)
				//				{
				//					continue;
				//				}

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

	public static void feat_only_train_lu_framenet(String train_filelist_path,
			String dir_path, String in_train_path,
			String train_trigger_feat_path, String event_frame_path,
			String frame_lu_path, int top) throws Exception
	{
		// 读取frame和lu的映射
		HashMap<String, HashSet<String>> frame_lu_map = new HashMap<String, HashSet<String>>();
		BufferedReader br = new BufferedReader(new FileReader(frame_lu_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String frame = strs[0];

			HashSet<String> set = new HashSet<String>();
			for (int i = 1; i < strs.length; ++i)
			{
				set.add(strs[i]);
			}
			frame_lu_map.put(frame, set);
		}
		br.close();

		// 读取event 与frame的映射
		HashMap<String, HashSet<String>> event_frame_map = new HashMap<String, HashSet<String>>();
		br = new BufferedReader(new FileReader(event_frame_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" \\|\\| ");
			String event = strs[0];
			HashSet<String> frame_set = new HashSet<String>();
			for (int i = 1; i <= top; ++i)
			{
				String frame = strs[i];
				frame = frame.substring(0, frame.indexOf(" :"));
				frame_set.add(frame);
			}

			event_frame_map.put(event, frame_set);
		}
		br.close();

		// 加入train中所有trigger
		// 再加入最相似5个frame的trigger
		HashSet<String> trigger_set = new HashSet<String>();
		br = new BufferedReader(new FileReader(in_train_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");

			// 加入event自身的trigger
			for (int i = 1; i < strs.length; ++i)
			{
				trigger_set.add(strs[i]);
			}

			// 加入event扩展后的trigger
			HashSet<String> frame_set = event_frame_map.get(strs[0]);
			for (String frame : frame_set)
			{
				HashSet<String> set = frame_lu_map.get(frame);
				trigger_set.addAll(set);
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
		//		frame_lu_map("./cyd/frame/frame_lu_map.txt");

		String frame_lu_path = "./cyd/frame/frame_lu_map.txt";
		String event_frame_map_path = "./cyd/frame/event_frame_map_jaccard.txt";
		String not_in_train_path = "./cyd/triggers_not_in_train.txt";

		//		event_map_to_frame_jaccard(event_path, frame_lu_path, result_path);

		String trigger_result_path = "./cyd/trigger_result_allsents";
		String triiger_result_feat_path = "./cyd/trigger_test_feature_allsents";

		//		trigger_not_in_train_wrong(not_in_train_path, trigger_result_path,
		//				triiger_result_feat_path);

		String in_train_path = "./cyd/triggers_in_train.txt";

		//		trigger_in_train_wrong(in_train_path, trigger_result_path,
		//				triiger_result_feat_path);

//		cover_unknown(not_in_train_path, event_frame_map_path, frame_lu_path, 5);
		
		// 与上述区别在于，将所有扩展后的lu放入trigger_set，然后判断未登录，而非对每种类型判断未登录
//		cover_unknown_lus(not_in_train_path, event_frame_map_path, frame_lu_path, 5);

		String dir_path = "./tmp/featDir/";
		String train_filelist_path = "./cyd/filelist/new_filelist_ACE_training";
		String train_trigger_feat_path = "./cyd/only_trigger_feat/train_feat_only_train_lu.txt";

		//		feat_only_train_lu(train_filelist_path, dir_path, in_train_path, 
		//				train_trigger_feat_path);

				feat_only_train_lu_framenet(train_filelist_path, dir_path,
						in_train_path, train_trigger_feat_path, event_frame_map_path,
						frame_lu_path, 5);

		String test_filelist_path = "./cyd/filelist/new_filelist_ACE_test";
		String test_trigger_feat_path = "./cyd/only_trigger_feat/test_feat_only_train_lu.txt";

				feat_only_train_lu_framenet(test_filelist_path, dir_path,
						in_train_path, test_trigger_feat_path, event_frame_map_path,
						frame_lu_path, 5);

		System.exit(0);
	}
}
