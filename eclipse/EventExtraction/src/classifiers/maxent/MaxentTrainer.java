package classifiers.maxent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.Csv2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InfoGain;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.FileUtils;

public class MaxentTrainer 
{
	
	/**
	 * Use information gain to rank and select features
	 * @param instances
	 * @return 
	 */
	public static void pruneByInfoGain(InstanceList instances)
	{
		InfoGain ig = new InfoGain (instances);
		int numfeatures = (int) (instances.getDataAlphabet().size() * 0.9);
		FeatureSelection fs = new FeatureSelection (ig, numfeatures);
		for (int ii = 0; ii < instances.size(); ii++) {
			Instance instance = instances.get(ii);
			FeatureVector fv = (FeatureVector) instance.getData();
			FeatureVector fv2 = FeatureVector.newFeatureVector (fv, instances.getDataAlphabet(), fs);
			instance.unLock();
			instance.setData(fv2);
		}
	}
	
	
	// change this accordingly
	static double Gaussian_Variance = 1.0;
	
	// in the training feature table
	// Lines should be formatted as:                                                                   
    //                                                                                                 
    //   [name] [label] [data ... ]                                                                    
    //                                                                                                          
	static public Classifier TrainMaxent(File trainingFilename, File modelFile) throws IOException 
	{
		// build data input pipe
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		// define pipe
		// the features in [data ...] should like: feature:value
		pipes.add(new Target2Label());
		pipes.add(new Csv2FeatureVector());
		
		Pipe pipe = new SerialPipes(pipes);
		pipe.setTargetProcessing(true);
		
		// read data
		InstanceList trainingInstances = new InstanceList(pipe);
		FileReader training_file_reader = new FileReader(trainingFilename);
		CsvIterator reader =
	            new CsvIterator(training_file_reader,
	                            "(\\w+)\\s+(\\S+)\\s+(.*)",
	                            3, 2, 1);  // (data, label, name) field indices    
		trainingInstances.addThruPipe(reader);
		training_file_reader.close();
		
		// prune by info gain
		// pruneByInfoGain(trainingInstances);
		
		// calculate running time
		long startTime = System.currentTimeMillis();
		PrintStream temp = System.err;
		System.setErr(System.out);
		
		// train a Maxent classifier (could be other classifiers)
		ClassifierTrainer trainer = new MaxEntTrainer(Gaussian_Variance);
		Classifier classifier = trainer.train(trainingInstances);
		
		System.setErr(temp);
		// calculate running time
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total training time: " + totalTime);
		
		// write model
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));
	    oos.writeObject(classifier);
	    oos.close();
	    
	    return classifier;
	}

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
	
	public static void main (String[] args) throws Exception 
	{
		if(args.length < 2)
		{
			System.out.println("Usage:");
			System.out.println("args[0] : feature table file");
			System.out.println("args[1] : model file name");
			System.exit(-1);
		}		
		
		File featureTable = new File(args[0]);
		File modelFile = new File(args[1]);
		
		// train the model
		TrainMaxent(featureTable, modelFile); 
	}
}
