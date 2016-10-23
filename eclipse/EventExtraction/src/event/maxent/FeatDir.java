package event.maxent;

import java.io.BufferedReader;
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.DocumentException;

import util.Controller;

import commons.Alphabets;
import commons.Document;
import commons.Sentence;
import commons.TextFeatureGenerator;

import classifiers.maxent.MaxentTrainer;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;
import event.types.SentenceAssignment;
import event.types.SentenceInstance;
import event.types.SentenceInstance.InstanceAnnotations;

public class FeatDir
{
	public static String featTableFileName = "tmp/FeatureTableTrigger";
	public static String featDir = "tmp/featDirAllSents/";

	public Classifier trainClassifier(File srcDir, File trainingFileList,
			File modelFile, Controller controller)
	{
		// print whole featureTable for the training instances
		printFeatureTableToFile(srcDir, trainingFileList, controller);

		// maxent training
		//		try
		//		{
		//			Classifier model = MaxentTrainer.TrainMaxent(new File(
		//					featTableFileName), modelFile);
		//			return model;
		//		}
		//		catch (IOException e)
		//		{
		//			e.printStackTrace();
		//			return null;
		//		}
		return null;
	}

	protected void printFeatureTableToFile(File srcDir, File trainingFileList,
			Controller controller)
	{
		Alphabets alphabets = new Alphabets();

		try
		{
			List<SentenceInstance> trainInstanceList = readInstanceList(srcDir,
					trainingFileList, alphabets, controller, true);

			/* 改写为写入到具体文件中 */
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * give a file list and home dir, get an instance list
	 * @param srcDir
	 * @param file_list
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static List<SentenceInstance> readInstanceList(File srcDir,
			File file_list, Alphabets alphabets, Controller controller,
			boolean learnable) throws IOException, DocumentException
	{
		System.out.println("Reading training instance ...");

		PrintWriter writer = new PrintWriter(new File(featTableFileName));

		File feat_dir = new File(featDir);
		if (feat_dir.exists())
		{
			feat_dir.delete();
		}
		feat_dir.mkdir();

		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while ((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;

			FileWriter fw = new FileWriter(featDir
					+ fileName.substring(fileName.lastIndexOf("/") + 1));
			System.out.println(fileName);

			/* Document已经提供ACE各种文本的信息, 已经过POS, chunking和切句子和分词等 */
			Document doc = new Document(fileName, true, monoCase);

			// fill in text feature vector for each token
			/* 特征生成好之后 是往doc中存储 */
			featGen.fillTextFeatures(doc);

			for (int sent_id = 0; sent_id < doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				// during learning, skip instances that do not have event mentions
				/* 训练过程中，跳过没有event mention的句子 */

				SentenceInstance inst = new SentenceInstance(sent, alphabets,
						controller, learnable);
				instancelist.add(inst);

				SentenceAssignment target = (SentenceAssignment) inst.target;
				for (int i = 0; i < inst.size(); i++)
				{
					String instanceName = "Trigger" + i;
					String triggerLabel = target.getLabelAtToken(i);
					writer.print(instanceName);
					writer.print(" ");
					writer.print(triggerLabel);

					fw.write(instanceName);
					fw.write(" ");
					fw.write(triggerLabel);

					// output features for one trigger candidate
					List<String> features = ((List<List<String>>) inst
							.get(InstanceAnnotations.NodeTextFeatureVectors))
							.get(i);
					for (String feature : features)
					{
						writer.print(" ");
						writer.print(feature + ":1");

						fw.write(" ");
						fw.write(feature + ":1");
					}

					writer.println();

					fw.write("\n");
				}

			}

			fw.close();
		}

		writer.close();

		System.out.println("done");
		return instancelist;
	}

	/**
	 * print the feature weights for the MaxEnt model
	 * @param maxEnt
	 * @param out
	 */
	public static void printFeatureWeights(MaxEnt maxEnt, PrintStream out)
	{
		maxEnt.print(out);
	}

	/**
	 * This is a very simple pipeline
	 * @param args
	 * @throws IOException
	 */
	static public void main(String[] args) throws IOException
	{
		if (args.length < 3)
		{
			System.out.println("Training perceptron Usage:");
			System.out.println("args[0]: source dir of training data");
			System.out.println("args[1]: file list of training data");
			System.out.println("args[2]: model file to be saved");
			System.out.println("args[3+]: controller arguments");
			System.exit(-1);
		}

		File srcDir = new File(args[0]);
		File trainingFileList = new File(args[1]);
		File modelFile = new File(args[2]);

		// set settings
		Controller controller = new Controller();
		if (args.length > 3)
		{
			String[] settings = Arrays.copyOfRange(args, 3, args.length);
			controller.setValueFromArguments(settings);
		}
		System.out.println(controller.toString());

		// train model
		FeatDir trainer = new FeatDir();
		Classifier model = trainer.trainClassifier(srcDir, trainingFileList,
				modelFile, controller);

		// print out weights or any other detail
		//		PrintStream out = new PrintStream(modelFile.getAbsoluteFile() + ".weights");
		//		printFeatureWeights((MaxEnt) model, out);
		//		out.close();
	}
}
