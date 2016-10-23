package cyd.maxent;

import java.io.*;

import java.util.*;
import java.util.regex.*;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.pipe.iterator.SelectiveFileLineIterator;
import cc.mallet.pipe.iterator.SimpleFileLineIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;

import cyd.frame.TMP;
import cyd.frame.prf.PRF;

public class MeDecoder
{
	// the model
	Classifier classifier;

	public MeDecoder(String modelFile) throws IOException
	{
		// read model
		ObjectInputStream oos = new ObjectInputStream(new FileInputStream(
				new File(modelFile)));
		try
		{
			classifier = (Classifier) oos.readObject();
			oos.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public PRF decodeOnFeatureTable(String testPath, String resultPath)
			throws Exception
	{
		
		CsvIterator reader = new CsvIterator(new FileReader(testPath),
				"(\\w+)\\s+(\\S+)\\s+(.*)", 3, 2, 1); // (data, label, name) field indices               

		InstanceList testingInstances = new InstanceList(
				classifier.getInstancePipe());
		testingInstances.addThruPipe(reader);

		//		ArrayList<Classification> list = classifier.classify(testingInstances);
		int correct = 0, p = 0, r = 0;
		FileWriter fw = new FileWriter(resultPath);

		for (int i = 0; i < testingInstances.size(); i++)
		{
			Instance instance = testingInstances.get(i);
			Labeling labeling = classifier.classify(instance).getLabeling();

			// P R F
			String label = instance.getTarget().toString();
			String test = labeling.getBestLabel().toString();

			fw.write(label + " : " + test + "\n");
			if (test.equals(label) && label.equals("O") == false)
			{
				++correct;
			}
			if (test.equals("O") == false)
			{
				++p;
			}
			if (label.equals("O") == false)
			{
				++r;
			}
		}
		fw.close();
		System.out.println(testingInstances.size());
		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.println("P: " + String.format("%.2f", P * 100));
		System.out.println("R: " + String.format("%.2f", R * 100));
		System.out.println("F: " + String.format("%.2f", F * 100));
		
		PRF prf = new PRF(P, R, F);
		return prf;
	}
	
	public PRF DistinctJointdecodeOnFeatureTable(String testPath, String resultPath)
			throws Exception
	{
		
		CsvIterator reader = new CsvIterator(new FileReader(testPath),
				"(\\w+)\\s+(\\S+)\\s+(.*)", 3, 2, 1); // (data, label, name) field indices               

		InstanceList testingInstances = new InstanceList(
				classifier.getInstancePipe());
		testingInstances.addThruPipe(reader);

		int correct = 0, p = 0, r = 0;
		FileWriter fw = new FileWriter(resultPath);

		for (int i = 0; i < testingInstances.size(); i++)
		{
			Instance instance = testingInstances.get(i);
			Labeling labeling = classifier.classify(instance).getLabeling();

			// P R F
			String label = instance.getTarget().toString();
			String test = labeling.getBestLabel().toString();

			fw.write(label + " : " + test + "\n");
			if (test.equals(label) && label.equals("0") == false)
			{
				++correct;
			}
			if (test.equals("0") == false)
			{
				++p;
			}
			if (label.equals("0") == false)
			{
				++r;
			}
		}
		fw.close();
		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.println("P: " + String.format("%.2f", P * 100));
		System.out.println("R: " + String.format("%.2f", R * 100));
		System.out.println("F: " + String.format("%.2f", F * 100));
		
		PRF prf = new PRF(P, R, F);
		return prf;
	}
	
	public PRF SFVdecodeOnFeatureTable(String testPath, String resultPath)
			throws Exception
	{
		
		CsvIterator reader = new CsvIterator(new FileReader(testPath),
				"(\\w+)\\s+(\\S+)\\s+(.*)", 3, 2, 1); // (data, label, name) field indices               

		InstanceList testingInstances = new InstanceList(
				classifier.getInstancePipe());
		testingInstances.addThruPipe(reader);

		int correct = 0, p = 0, r = 0;
		FileWriter fw = new FileWriter(resultPath);

		for (int i = 0; i < testingInstances.size(); i++)
		{

			Instance instance = testingInstances.get(i);
			Labeling labeling = classifier.classify(instance).getLabeling();

			// P R F
			String label = instance.getTarget().toString();
			String test = labeling.getBestLabel().toString();

			fw.write(label + " : " + test + "\n");
			if (test.equals(label) && label.equals("1"))
			{
				++correct;
			}
			if (test.equals("1"))
			{
				++p;
			}
			if (label.equals("1"))
			{
				++r;
			}
		}
		fw.close();
		System.out.println("size : " + testingInstances.size());
		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.println("P: " + String.format("%.2f", P * 100));
		System.out.println("R: " + String.format("%.2f", R * 100));
		System.out.println("F: " + String.format("%.2f", F * 100));
		
		PRF prf = new PRF(P, R, F);
		return prf;
	}

	public static void prf_post(String resultPath, String feat_path,
			String testFilelist, String featDir) throws Exception
	{
		int correct = 0, p = 0, r = 0;

		BufferedReader br = new BufferedReader(new FileReader(resultPath));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" : ");
			String label = strs[0];
			String test = strs[1];
			if (test.equals(label) && label.equals("O") == false)
			{
				++correct;
			}
			if (test.equals("O") == false)
			{
				++p;
			}
			if (label.equals("O") == false)
			{
				++r;
			}

		}
		br.close();

		HashSet<String> test_feat_set = new HashSet<String>();

		br = new BufferedReader(new FileReader(feat_path));
		while ((line = br.readLine()) != null)
		{
			test_feat_set.add(line);
		}
		br.close();

		br = new BufferedReader(new FileReader(testFilelist));
		while ((line = br.readLine()) != null)
		{
			String fileName = line.substring(line.lastIndexOf("/") + 1);

			BufferedReader file_br = new BufferedReader(new FileReader(featDir
					+ fileName));

			while ((line = file_br.readLine()) != null)
			{
				if (test_feat_set.contains(line) == false)
				{
					String[] strs = line.split(" ");
					if (strs[1].equals("O") == false)
					{
						++r;
					}
				}
			}
			file_br.close();
		}
		br.close();

		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		System.out.println("P: " + String.format("%.2f", P * 100));
		System.out.println("R: " + String.format("%.2f", R * 100));
		System.out.println("F: " + String.format("%.2f", F * 100));
	}

	
	
	static public void main(String[] args) throws Exception
	{
		String testFilelist = "./cyd/filelist/new_filelist_ACE_test";
		String featDir = "./tmp/featDir/";
		//		String testFeatPath = "./cyd/trigger_test_feature";
//				MeTrainer.feature_table(testFilelist, featDir, testFeatPath);

//		String feat_path = "./cyd/only_trigger_feat/test_feat_only_train_lu.txt";
		//		String feat_path = "./cyd/wordnet_expand/feat_test.txt";
		//		String feat_path = "./cyd/trigger_test_feature";
		String feat_path = "./cyd/test_feature_path.txt";
		String modelPath = "./cyd/model";
		String resultPath = "./cyd/framenet_expand/trigger_result";
		MeDecoder decoder = new MeDecoder(modelPath);
		decoder.decodeOnFeatureTable(feat_path, resultPath);

//		prf_post(resultPath, feat_path, testFilelist, featDir);
	}
}

