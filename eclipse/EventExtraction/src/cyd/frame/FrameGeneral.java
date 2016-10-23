package cyd.frame;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

import cyd.frame.prf.*;
import cyd.maxent.MeDecoder;
import cyd.maxent.MeTrainer;

public class FrameGeneral
{
	/*
	 * step 1 平均分配6种主题的文档为10份，第二行为前面9份，后面1份的数量 bn : 226 wl : 119 un : 49 cts : 39 bc : 60
	 * nw :106 分成10分，取%10的余
	 */
	public static void file_distribution(String filelist_path,
			String file_distribution_path) throws Exception
	{
		HashMap<String, HashSet<String>> type_fileset_map = new HashMap<String, HashSet<String>>();

		BufferedReader br = new BufferedReader(new FileReader(filelist_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String type = line.substring(0, line.indexOf("/"));
			String filename = line.substring(line.lastIndexOf("/") + 1);
			if (type_fileset_map.containsKey(type) == false)
			{
				HashSet<String> set = new HashSet<String>();
				set.add(filename);
				type_fileset_map.put(type, set);
			}
			else
			{
				HashSet<String> set = type_fileset_map.get(type);
				set.add(filename);
			}
		}
		br.close();

		ArrayList<HashSet<String>> fileset_list = new ArrayList<HashSet<String>>();
		for (int i = 0; i <= 9; ++i)
		{
			fileset_list.add(new HashSet<String>());
		}

		for (Map.Entry entry : type_fileset_map.entrySet())
		{
			HashSet<String> set = (HashSet<String>) entry.getValue();

			int index = 0;
			for (String filename : set)
			{
				int mod = index++ % 10;
				HashSet<String> fileset = fileset_list.get(mod);
				fileset.add(filename);
			}
		}

		int cnt = 0;
		FileWriter fw = new FileWriter(file_distribution_path);
		for (int i = 0; i <= 9; ++i)
		{
			HashSet<String> set = fileset_list.get(i);
			cnt += set.size();
			System.out.println(set.size());
			for (String filename : set)
			{
				fw.write(filename + " ");
			}
			fw.write("\n");
		}
		fw.close();
		System.out.println(cnt);
	}

	// 从frame annotator中提取frame
	public static void extract_frame_feature(String annotator_dir_path,
			String frame_feature_dir_path) throws Exception
	{
		File frame_feature_dir = new File(frame_feature_dir_path);
		if (frame_feature_dir.exists() == false)
		{
			frame_feature_dir.mkdir();
		}

		SAXReader reader = new SAXReader();
		File annotator_dir = new File(annotator_dir_path);
		File[] files = annotator_dir.listFiles();
		int index = 0;
		for (File file : files)
		{
			Document document = reader.read(file);
			List<Element> sentence_list = document
					.selectNodes("/corpus/documents/document/paragraphs/paragraph/sentences/sentence");

			String filename = file.getName();
			filename = filename.substring(0, filename.indexOf(".out"));
			FileWriter fw = new FileWriter(frame_feature_dir_path + filename);
			for (Element senteceElement : sentence_list)
			{
				Element textElement = senteceElement.element("text");
				String text = textElement.getText();
				String[] words = text.split(" ");
				HashMap<String, Integer> wordIndexMap = new HashMap<String, Integer>();
				int start = 0, len = 0, end = 0;
				// 先不考虑短语或词组是触发词的
				for (int i = 0; i < words.length; ++i)
				{
					len = words[i].length();
					end = start + len - 1;
					wordIndexMap.put(start + " " + end, i);
					start += len + 1;
				}

				Element annotationSetsElement = senteceElement
						.element("annotationSets");
				List<Element> annotationSetElementList = annotationSetsElement
						.elements("annotationSet");
				for (Element annotationSetElement : annotationSetElementList)
				{
					String frame = annotationSetElement
							.attributeValue("frameName");
					Element layersElement = annotationSetElement
							.element("layers");
					List<Element> layerElementList = layersElement
							.elements("layer");
					Element layerElement = layerElementList.get(0);
					Element labelsElement = layerElement.element("labels");
					Element labelElement = labelsElement.element("label");
					String startIndex = labelElement.attributeValue("start");
					String endIndex = labelElement.attributeValue("end");
					if (wordIndexMap.containsKey(startIndex + " " + endIndex))
					{
						int wordIndex = wordIndexMap.get(startIndex + " "
								+ endIndex);

						words[wordIndex] += " " + frame;
					} // 否则应该是短语或词组，所以才找不到下标
				}

				for (String word : words)
				{
					fw.write(word + "\n");
				}
			}
			fw.close();
			System.out.println(++index);
		}
	}

	// 比较ace中的words数目是否与用Frame标注后的字数一致，然后贪婪的往feature中加入frame特征
	public static void frame_addto_feature(String annotator_dir_path,
			String feature_dir, String addFrameFeature_dir_path)
			throws Exception
	{
		File addFrameFeature_dir = new File(addFrameFeature_dir_path);
		if (addFrameFeature_dir.exists() == false)
		{
			addFrameFeature_dir.mkdir();
		}
		File annotator_dir = new File(annotator_dir_path);
		File[] files = annotator_dir.listFiles();
		for (File file : files)
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;

			ArrayList<String> list = new ArrayList<String>();
			while ((line = br.readLine()) != null)
			{
				list.add(line);
			}
			br.close();

			br = new BufferedReader(
					new FileReader(feature_dir + file.getName()));
			ArrayList<String> feature_list = new ArrayList<String>();
			while ((line = br.readLine()) != null)
			{
				feature_list.add(line);
			}
			br.close();

			/** 结果显示，list普遍要比feature_list大  看能否用贪婪方法将feature_list所有的与list中的word对应起来**/
			int pointer = 0;
			int i = 0;
			while (i < feature_list.size())
			{
				line = feature_list.get(i);

				String[] strs = line.split(" ");
				String feature_word = strs[2];
				feature_word = feature_word.substring(
						feature_word.indexOf("=") + 1,
						feature_word.lastIndexOf(":"));

				strs = list.get(pointer).split(" ");
				String word = null;
				String frame = null;
				if (strs.length == 2)
				{
					word = strs[0];
					frame = strs[1];
				}
				else
				{
					word = strs[0];
				}
				// 原文中可能为'' very，而在annotator处理后的文本中可能为''very，所以用indexOf，而不用equal
				if (feature_word.indexOf(word) != -1)
				{
					++i;
					++pointer;
					if (frame != null)
					{
						feature_list
								.set(i - 1, line + " Frame=" + frame + ":1");
					}
				}
				else
				{

					++pointer;
				}
			}

			FileWriter fw = new FileWriter(addFrameFeature_dir_path
					+ file.getName());
			for (String feature_line : feature_list)
			{
				fw.write(feature_line + "\n");
			}
			fw.close();
		}
	}

	// 根据文件名，组合成特征文件
	public static void generate_feature_path(ArrayList<String> train_file_list,
			String feature_dir, String feature_path) throws Exception
	{
		FileWriter fw = new FileWriter(feature_path);
		for (String line : train_file_list)
		{
			String fileName = line;
			BufferedReader file_br = new BufferedReader(new FileReader(
					feature_dir + fileName));
			while ((line = file_br.readLine()) != null)
			{
				fw.write(line + "\n");
			}
		}
		fw.close();
	}

	// step 2
	// 开始交叉验证，正常的包含所有词的情况，10次交叉验证
	public static void cross_validation_10(String file_distribution_path,
			String feature_dir_path, String result_dir_path, String result_path)
			throws Exception
	{
		ArrayList<ArrayList<String>> file_lists = new ArrayList<ArrayList<String>>();

		BufferedReader br = new BufferedReader(new FileReader(
				file_distribution_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" ");
			ArrayList<String> list = new ArrayList<String>();
			for (String str : strs)
			{
				list.add(str);
			}
			file_lists.add(list);
		}
		br.close();

		double p = 0, r = 0, f = 0;
		// 10倍交叉验证
		for (int i = 0; i <= 9; ++i)
		{
			System.out.println("iteration : " + i);
			ArrayList<String> train_file_list = new ArrayList<String>();
			ArrayList<String> test_file_list = new ArrayList<String>();

			for (int j = 0; j <= 9; ++j)
			{
				if (i == j)
				{
					test_file_list = file_lists.get(i);
				}
				else
				{
					train_file_list.addAll(file_lists.get(j));
				}
			}

			String train_feature_path = "./cyd/train_feature_path.txt";
			generate_feature_path(train_file_list, feature_dir_path,
					train_feature_path);
			String test_feature_path = "./cyd/test_feature_path.txt";
			generate_feature_path(test_file_list, feature_dir_path,
					test_feature_path);

			String modelPath = "./cyd/model_" + i;
			MeTrainer.TrainMaxent(train_feature_path, modelPath);

			String resultPath = result_dir_path + i;
			MeDecoder decoder = new MeDecoder(modelPath);
			cyd.frame.prf.PRF prf = decoder.decodeOnFeatureTable(test_feature_path,
					resultPath);
			p += prf.p;
			r += prf.r;
			f += prf.f;
		}

		FileWriter fw = new FileWriter(result_path);
		p /= 10;
		r /= 10;
		f /= 10;
		fw.write(p + " " + r + " " + f + "\n");
		fw.close();
	}

	public static void main(String[] args) throws Exception
	{
		String filelist_path = "./cyd/filelist/new_filelist_ACE_whole";
		String dir_path = "./tmp/featDir/";
		String file_distribution_path = "./cyd/frame_general/file_distribution.txt";

		//		file_distribution(filelist_path, file_distribution_path);

		String annotator_dir_path = "./cyd/ace/";
		String frame_feature_dir_path = "./cyd/frame_general/frame_feature/";
		//		extract_frame_feature(annotator_dir_path, frame_feature_dir_path);

		String feature_dir = "./tmp/featDir/";
		String addFrameFeature_dir_path = "./cyd/frame_general/addFrameFeature/";

		//		frame_addto_feature(frame_feature_dir_path, feature_dir,
		//				addFrameFeature_dir_path);

		String result_dir_path = "./cyd/frame_general/result_dir/";
		String feature_dir_path = feature_dir;
		String result_path = "./cyd/frame_general/result.txt";
		cross_validation_10(file_distribution_path, feature_dir_path,
				result_dir_path, result_path);

		System.exit(0);
	}

}
