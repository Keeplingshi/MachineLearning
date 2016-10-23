package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ds.tree.*;

/**
 * this dictionary reads dicts from data resource, 
 * and we can use it to query phrases
 * @author che
 *
 */
public class Dictionaries 
{	
	static public Dictionaries singleton_dictionaries;
		
	// make a dictionary data structure
	// key is "phrase/word" to be searched, value is type
	private Map<String, RadixTree<String>> dictionary;
	
	public Dictionaries(String dic_path) throws FileNotFoundException, IOException
	{
		// get properties
		File dict_file;
		dict_file = new File(dic_path);
		
		setDictionary(new HashMap<String, RadixTree<String>>());
		initializeDict(dict_file);
	}

	/**
	 * read the dictionary to memory data structure
	 * @param dictFile
	 */
	protected void initializeDict(File dictFile) 
	{
		System.out.println("loading dicts ... ");
		File[] children = dictFile.listFiles();
		
		for(File child : children)
		{
			if(child.isFile() && !child.isHidden())
			{
				String file_name = child.getName();
				String dict_name = file_name;
				if(file_name.indexOf('.') > 0)
				{
					dict_name = file_name.substring(0, file_name.indexOf('.'));
				}
				
				// insert the words in this file into dictionary as key, and file name as value
				try 
				{
					RadixTree<String> tree = new RadixTreeImpl<String>();
					insertFile(tree, child, dict_name);
					
					System.out.println("loading dicts ... " + child.getName() + "\t" + dict_name);
					
					this.getDictionary().put(dict_name, tree);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * get Radix tree from document in which each line corresponds to one entry 
	 * @param child
	 * @return
	 * @throws IOException 
	 */
	protected static RadixTree<String> insertFile(RadixTree<String> tree, File child, String dict_name) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(child));
		String line = "";
		while((line = reader.readLine()) != null)
		{
			// convert tokens into lowercase
			line = line.trim();
			line = line.toLowerCase();
			if(line.equals(""))
			{
				continue;
			}
			if(!tree.contains(line))
			{
				tree.insert(line, dict_name);
			}
		}
		reader.close();
		return tree;
	}

	// indicats that a token is a valid prefix in this dictionary
	static public final String HAS_PREFIX = "HAS_PREFIX"; 
	
	/**
	 * lookup a value in the dictionaries 
	 * @param value to string to be searched for 
	 * @return the list dictionaries that have a match
	 */
	public List<String> lookup(String value)
	{
		value = value.toLowerCase();
		List<String> ret = new ArrayList<String>();
		for(String key : this.dictionary.keySet())
		{
			RadixTree<String> tree = this.dictionary.get(key);
			String type = tree.find(value);
			if(type != null)
			{
				ret.add(type);
			}
		}
		return ret;
	}
	
	/**
	 * search the type of given token
	 * @param token
	 * @return return type if dictionary contains it, otherwise, 
	 * if it's a valid prefix, return HAS_PREFIX, otherwise, return null  
	 */
	public String searchToken(String key, String token)
	{
		RadixTree<String> tree = this.dictionary.get(key);
		if(tree == null)
		{
			return null;
		}
		String type = tree.find(token);
		if(type == null)
		{
			ArrayList<String> keys_has_prefix = tree.searchPrefix(token, 1);
			if(keys_has_prefix.size() > 0)
			{
				type = "HAS_PREFIX";
			}
		}
		return type;
	}

	void setDictionary(Map<String, RadixTree<String>> dictionary) 
	{
		this.dictionary = dictionary;
	}

	public Map<String, RadixTree<String>> getDictionary() 
	{
		return dictionary;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String dict_path = "data/mention_dict";
		
		Dictionaries dicts = new Dictionaries(dict_path);
		String type = dicts.searchToken("COUNTRY", "north korea");
		System.out.println(type);
		
		System.out.println(dicts.lookup("securities and exchange commission"));
	}

}
