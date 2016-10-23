package cyd.maxent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.types.*;
import cc.mallet.util.FileUtils;

public class MeTrainer
{

	/**
	 * Use information gain to rank and select features
	 * @param instances
	 * @return 
	 */
	public static void pruneByInfoGain(InstanceList instances)
	{
		InfoGain ig = new InfoGain(instances);
		int numfeatures = (int) (instances.getDataAlphabet().size() * 1.0);
		FeatureSelection fs = new FeatureSelection(ig, numfeatures);
		for (int ii = 0; ii < instances.size(); ii++)
		{
			Instance instance = instances.get(ii);
			FeatureVector fv = (FeatureVector) instance.getData();
			FeatureVector fv2 = FeatureVector.newFeatureVector(fv, instances.getDataAlphabet(), fs);
			instance.unLock();
			instance.setData(fv2);
		}
	}

	// change this accordingly
	static double Gaussian_Variance = 1.0;
                                                                                   
	/** in the training feature table, Lines should be formatted as:
	 *     [name] [label] [data ...]
	 * @param trainPath
	 * @param modelPath
	 */
	public static Classifier TrainMaxent(String trainPath, String modelPath) throws IOException
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
		FileReader training_file_reader = new FileReader(trainPath);
		CsvIterator reader = new CsvIterator(training_file_reader, "(\\w+)\\s+(\\S+)\\s+(.*)", 3, 2, 1); // (data, label, name) field indices    
		trainingInstances.addThruPipe(reader);
		training_file_reader.close();

		// prune by info gain
//		pruneByInfoGain(trainingInstances);

		PrintStream temp = System.err;
		System.setErr(System.out);
		
		long startTime = System.currentTimeMillis();
		// train a Maxent classifier (could be other classifiers)
		ClassifierTrainer trainer = new MaxEntTrainer(0.9);
		Classifier classifier = trainer.train(trainingInstances);
		// calculate running time
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total training time: " + totalTime);

		System.setErr(temp);
		
		// write model
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath));
		oos.writeObject(classifier);
		oos.close();

		return classifier;
	}

	public static void feature_table(String trainFileList, String featDir, String featPath) throws IOException
	{
		HashSet<String> trainFileSet = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(trainFileList));
		String line = null;
		while ( (line = br.readLine()) != null )
		{
			line = line.substring(line.lastIndexOf("/")+1);
			trainFileSet.add(line);
		}
		br.close();
		
		File dir = new File(featDir);
		File[] files = dir.listFiles();
		
		FileWriter fw = new FileWriter(featPath);
		for (File file : files)
		{
			if (trainFileSet.contains(file.getName()) == false)
			{
				continue;
			}
			br = new BufferedReader(new FileReader(file));
			while ( (line = br.readLine()) != null )
			{
				String[] strs = line.split(" ");
				int len = strs.length;
				for (int i = 0; i < len-1; ++i)
				{
					fw.write(strs[i] + " ");	
				}
				fw.write(strs[len-1] + "\n");
			}
			br.close();
		}
		fw.close();
	}
	
	public static void printFeatureWeights(MaxEnt maxEnt, PrintStream out)
	{
		maxEnt.print(out);
	}
	
	public static void main(String[] args) throws IOException
	{
		String trainFilelist = "./cyd/filelist/new_filelist_ACE_training";
		String featDir = "./tmp/featDir/";
//		String feat_path = "./cyd/trigger_train_feature";
//		feature_table(trainFilelist, featDir, feat_path);
	
//		String feat_path = "./cyd/only_trigger_feat/train_feat_only_train_lu.txt";
//		String feat_path = "./cyd/wordnet_expand/feat_train.txt";
		String feat_path = "./cyd/train_feature_path.txt";
		String modelPath = "./cyd/model";
		TrainMaxent(feat_path, modelPath);
	}

}
