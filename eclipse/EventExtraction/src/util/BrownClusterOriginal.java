package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BrownClusterOriginal
{
	static private BrownClusterOriginal dict = null;
	
	static public BrownClusterOriginal getSingleton()
	{
		if(dict == null)
		{
			try
			{
				File dict_path = new File("data/aceAllAndKDD.brownCluster"); 			
				dict = new BrownClusterOriginal(dict_path);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
		}
		return dict;
	}
	
	
	Map<String, String> map = new HashMap<String, String>();
	
	public BrownClusterOriginal(File dict_path) throws FileNotFoundException,IOException 
	{
		initializeDict(dict_path);
	}

	/**
	 * read the dictionary to memory data structure
	 * @param dictFile
	 */
	protected void initializeDict(File dictFile) 
	{
		System.out.print("loading brown cluster...");
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dictFile));
		
			String line = "";
			while((line = reader.readLine()) != null)
			{
				// convert tokens into lowercase
				line = line.trim();
				String[] fields = line.split("\\s");
				if(fields.length < 2)
				{
					continue;
				}
				String prefix = fields[0];
				String token = fields[1];
				map.put(token, prefix);
			}
			reader.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("done");
	}
	
	public String getBrownClusterPrefix(String token)
	{
		return map.get(token);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		File dict_path = new File("data/aceAllAndKDD.brownCluster"); 
		
		BrownClusterOriginal dicts = new BrownClusterOriginal(dict_path);
		System.out.println(dicts.getBrownClusterPrefix("city"));
		System.out.println(dicts.getBrownClusterPrefix("province"));
		System.out.println(dicts.getBrownClusterPrefix("state"));
		System.out.println(dicts.getBrownClusterPrefix("border"));
		System.out.println(dicts.getBrownClusterPrefix("of"));
		System.out.println(dicts.getBrownClusterPrefix("in"));
	}
}
