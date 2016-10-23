package cyd.util;

import java.io.*;
import java.util.*;

public class HTML
{

	/*
	 * 蓝色是完全正确 红色是p 橙色是r brown是都有事件类型，但是两者类型不同
	 */
	public static String add_to_context_list(String word, String type)
			throws Exception
	{
		String tmp = "";
		if (type.equals("O : O") == false)
		{
			String[] tmps = type.split(" : ");
			String gold = tmps[0];
			String predict = tmps[1];

			if (gold.equals("O") == false && predict.equals("O") == false)
			{
				if (gold.equals(predict))
				{
					tmp += "<font color=\"blue\">" + word + "</font>";
				}
				else
				{
					tmp += "<font color=\"brown\">" + word + "</font>";
				}
			}
			else if (gold.equals("O") == true && predict.equals("O") == false)
			{
				tmp += "<font color=\"red\">" + word + "</font>";
			}
			else if (gold.equals("O") == false && predict.equals("O") == true)
			{
				tmp += "<font color=\"orange\">" + word + "</font>";
			}
		}
		else
		{
			tmp += word;
		}
		return tmp;
	}

	public static void html(String feat_path, String type_path, String html_path)
			throws Exception
	{
		FileWriter fw = new FileWriter(html_path);
		fw.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>\n");
		ArrayList<String> type_list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(type_path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			type_list.add(line);
		}
		br.close();

		br = new BufferedReader(new FileReader(feat_path));
		int index = 0;
		ArrayList<String> context_list = new ArrayList<String>();
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" ");
			String word = strs[2];
			word = word.substring(word.indexOf("=") + 1, word.indexOf(":1"));
			String type = type_list.get(index++);

			if (line.startsWith("Trigger0"))
			{
				if (context_list.size() == 0)
				{
					context_list.add(add_to_context_list(word, type));
					continue;
				}
				else
				{
					fw.write("<p>");
					for (int i = 0; i < context_list.size() - 1; ++i)
					{
						fw.write(context_list.get(i) + " ");
					}
					fw.write(context_list.get(context_list.size() - 1));
					fw.write("</p>\n");
					context_list.clear();
				}
			}

			context_list.add(add_to_context_list(word, type));
		}
		br.close();
		fw.write("</body></html>\n");
		fw.close();
	}

	public static void file_to_sents(String sents_path, String dir_path)
			throws Exception
	{
		File dir = new File(dir_path);
		if (dir.exists() == true)
		{
			dir.delete();
		}
		dir.mkdir();

		BufferedReader br = new BufferedReader(new FileReader(sents_path));
		String line = null;
		int index = 0;
		while ((line = br.readLine()) != null)
		{
			if (line.equals(""))
			{
				continue;
			}
			FileWriter fw = new FileWriter(dir_path + "/" + ++index);
			fw.write(line + "\n");
			fw.close();
		}
		br.close();
	}

	public static void main(String[] args) throws Exception
	{
		String type_path = "./cyd/trigger_result_allsents";
		String feat_path = "./cyd/trigger_test_feature_allsents";
		String html_path = "./cyd/trigger_allsents.html";
		html(feat_path, type_path, html_path);

		//		file_to_sents("./cyd/trigger_test_sents.txt", "./cyd/sents");
		System.exit(0);
	}

}
