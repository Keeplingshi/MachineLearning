package cyd.util;

import java.io.*;
import java.util.*;

public class StopWord
{
	static HashSet<String> stopword_set = null;
	
	static StopWord stopword = null;

	public static StopWord getSingleTon() throws Exception
	{
		if (stopword == null)
		{
			stopword = new StopWord("./data/stopwords.txt");
		}
		return stopword;
	}

	StopWord(String path) throws Exception
	{
		stopword_set = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			stopword_set.add(line);
		}
		br.close();
	}
	
	public boolean is_stopword(String word) throws Exception
	{
		if (stopword_set.contains(word))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
