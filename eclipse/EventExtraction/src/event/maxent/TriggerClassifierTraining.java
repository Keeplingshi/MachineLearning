package event.maxent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import ace.acetypes.AceMention;

import util.Controller;

import commons.Alphabets;
import commons.Document;
import commons.Sentence;
import commons.TextFeatureGenerator;

import classifiers.maxent.MaxentTrainer;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEnt;
import event.perceptron.featureGenerator.EdgeFeatureGenerator;
import event.types.SentenceAssignment;
import event.types.SentenceInstance;
import event.types.SentenceInstance.InstanceAnnotations;

public class TriggerClassifierTraining
{
	public static String featTableFileName = "Temp/FeatureTableEvent";

	public Classifier trainClassifier(File srcDir, File trainingFileList,
			File modelFile, Controller controller)
	{
		// print whole featureTable for the training instances
		printFeatureTableToFile(srcDir, trainingFileList, controller);
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

			PrintWriter writer = new PrintWriter(new File(featTableFileName));
			for (SentenceInstance inst : trainInstanceList)
			{
				SentenceAssignment target = (SentenceAssignment) inst.target;
				for (int i = 0; i < inst.size(); i++)
				{
					String instanceName = "Trigger" + i;
					String triggerLabel = target.getLabelAtToken(i);
					writer.print(instanceName);
					writer.print(" ");
					writer.print(triggerLabel);

					// output features for one trigger candidate
					List<String> features = ((List<List<String>>) inst
							.get(InstanceAnnotations.NodeTextFeatureVectors))
							.get(i);
					for (String feature : features)
					{
						writer.print(" ");
						writer.print(feature + ":1");
					}

					writer.println();
				}
			}
			writer.close();
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

		List<SentenceInstance> instancelist = new ArrayList<SentenceInstance>();
		BufferedReader reader = new BufferedReader(new FileReader(file_list));
		String line = "";
		TextFeatureGenerator featGen = new TextFeatureGenerator();
		while ((line = reader.readLine()) != null)
		{
			boolean monoCase = line.contains("bn/") ? true : false;
			String fileName = srcDir + File.separator + line;

			//cyd start 
			PrintWriter writer = new PrintWriter(
					new File(ArgumentFeature.arg_dir_path
							+ fileName.substring(fileName.lastIndexOf("/") + 1)));
			//cyd end

			System.out.println(fileName);

			Document doc = new Document(fileName, true, monoCase);
			// fill in text feature vector for each token
			featGen.fillTextFeatures(doc);
			for (int sent_id = 0; sent_id < doc.getSentences().size(); sent_id++)
			{
				Sentence sent = doc.getSentences().get(sent_id);
				// during learning, skip instances that do not have event mentions 
				if (learnable && controller.skipNonEventSent)
				{
					if (sent.eventMentions != null
							&& sent.eventMentions.size() > 0)
					{
						SentenceInstance inst = new SentenceInstance(sent,
								alphabets, controller, learnable);
						
						SentenceAssignment target = (SentenceAssignment) inst.target;
						Map<Integer, Map<Integer, Integer>> edgeAssn = target
								.getEdgeAssignment();
						for (int i = 0; i < inst.size(); i++)
						{
							String triggerLabel = target.getLabelAtToken(i);
							// this is a trigger
							if (!triggerLabel
									.equals(SentenceAssignment.Default_Trigger_Label))
							{
								Map<Integer, Integer> edgeAssnTrigger = null;
								if (edgeAssn != null)
								{
									edgeAssnTrigger = edgeAssn.get(i);
								}
								// print feature table for each argument
								for (int k = 0; k < inst.eventArgCandidates
										.size(); k++)
								{
									String argRole = SentenceAssignment.Default_Argument_Label;
									if (edgeAssnTrigger != null
											&& edgeAssnTrigger.get(k) != null)
									{
										argRole = (String) inst.alphabets.edgeTargetAlphabet
												.lookupObject(edgeAssnTrigger
														.get(k));
									}

									String argName = "Arg" + "#" + i + "#" + k;
									writer.print(argName);
									writer.print(" ");
									writer.print(argRole);

									// output features for one argument candidate
									AceMention mention = inst.eventArgCandidates
											.get(k);
									List<String> features = EdgeFeatureGenerator
											.get_edge_text_features(inst, i,
													mention);
									for (String feature : features)
									{
										writer.print(" ");
										writer.print(feature + ":1");
									}
									writer.println();
								}
							}
						}
					}
				}

			}
			writer.close();
		}

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
		TriggerClassifierTraining trainer = new TriggerClassifierTraining();
		Classifier model = trainer.trainClassifier(srcDir, trainingFileList,
				modelFile, controller);

		// print out weights or any other detail
		PrintStream out = new PrintStream(modelFile.getAbsoluteFile()
				+ ".weights");
		printFeatureWeights((MaxEnt) model, out);
		out.close();
	}
}
