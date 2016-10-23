package cyd.maxent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;
import cc.mallet.util.FileUtils;

public class PrintFeatureWeights
{
	
	/**
	 * print the feature weights for the MaxEnt model
	 * @param maxEnt
	 * @param out
	 */
	public static void printFeatureWeights(MaxEnt maxEnt, File out)
	{
		PrintStream weights_out;
		try
		{
			weights_out = new PrintStream(out);
			maxEnt.print(weights_out);
			weights_out.close();
			
			String[] lines = FileUtils.readFile(out);
			weights_out = new PrintStream(out); 
			Map<String, Double> map = null;
			String label = null;
			for(String line : lines)
			{
				if(line.startsWith("FEATURES"))
				{
					if(map != null)
					{
						// sort map
						List<Entry<String, Double>> entrylist = new ArrayList<Entry<String, Double>>();
						entrylist.addAll(map.entrySet());
						Collections.sort(entrylist, new Comparator<Entry<String, Double>>()
								{
									@Override
									public int compare(
											Entry<String, Double> o1,
											Entry<String, Double> o2)
									{
										return -1 * o1.getValue().compareTo(o2.getValue());
									}
								}
								);
						// print out a feature table for a class
						for(Map.Entry<String, Double> entry : entrylist)
						{
							weights_out.println(label + "\t" + entry.getKey() + "\t" + entry.getValue());
						}
						weights_out.println();
					}
					// create a new map
					map = new HashMap<String, Double>();
					String[] cols = line.split("\\s+");
					label = cols[cols.length - 1];
				}
				else
				{
					if(map != null)
					{
						String[] cols = line.trim().split("\\s+");
						if(cols.length == 2)
						{
							String feat = cols[0];
							Double weight = Double.parseDouble(cols[1]);
							map.put(feat, weight);
						}
					}
				}
			}
			if(map != null)
			{
				// sort map
				List<Entry<String, Double>> entrylist = new ArrayList<Entry<String, Double>>();
				entrylist.addAll(map.entrySet());
				Collections.sort(entrylist, new Comparator<Entry<String, Double>>()
						{
							@Override
							public int compare(
									Entry<String, Double> o1,
									Entry<String, Double> o2)
							{
								return 1 * o1.getValue().compareTo(o2.getValue());
							}
						}
						);
				// print out a feature table for a class
				for(Map.Entry<String, Double> entry : entrylist)
				{
					weights_out.println(label + "\t" + entry.getKey() + "\t" + entry.getValue());
				}
				weights_out.println();
			}
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
	
	public static void print(String modelPath, String weightPath) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream oos = new ObjectInputStream(new FileInputStream(modelPath));
		Classifier classifier = (Classifier) oos.readObject();
		oos.close();
		printFeatureWeights((MaxEnt) classifier, new File(weightPath));
	}

	public static void printWithClassifier(Classifier classifier, String weightPath) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		printFeatureWeights((MaxEnt) classifier, new File(weightPath));
	}
	
	static public void main(String[] args) throws Exception
	{
		String modelPath = "./tmp/model.txt";
		String weightPath = "./tmp/weight.txt";
		PrintFeatureWeights.print(modelPath, weightPath);	
	}
}
