package cyd.frame;

import java.io.*;
import java.util.*;
import cyd.maxent.*;

public class ForwardAlgorithm
{

	public static void label_save_feature_file(HashSet<String> label_set,
			String feature_path, String feature_result) throws Exception
	{
		FileWriter fw = new FileWriter(feature_result);
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(feature_path));
		while ((line = br.readLine()) != null)
		{
			String[] strs = line.split(" ");
			StringBuilder sb = new StringBuilder(strs[0] + " " + strs[1]);

			for (int i = 2; i < strs.length; ++i)
			{
				String str = strs[i];
				String str_label = str.substring(0, str.indexOf("="));

				if (label_set.contains(str_label))
				{
					sb.append(" " + str);
				}
			}
			sb.append("\n");
			fw.write(sb.toString());
		}
		br.close();
		fw.close();
	}

	public static PRF prf(String path) throws Exception
	{
		PRF prf = new PRF();
		String line = null;

		int correct = 0, p = 0, r = 0;
		BufferedReader br = new BufferedReader(new FileReader(path));
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

		double P = (double) correct / p;
		double R = (double) correct / r;
		double F = 2 * P * R / (P + R);
		prf.p = P;
		prf.r = R;
		prf.f = F;
		return prf;
	}

	public static void forward_algorithm(String label_path,
			String train_feature_path, String train_feature_result,
			String test_feature_path, String test_feature_result,
			String final_result) throws Exception
	{
		HashSet<String> label_set = new HashSet<String>();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(label_path));
		while ((line = br.readLine()) != null)
		{
			label_set.add(line);
		}
		br.close();

		FileWriter fw = new FileWriter(final_result);

		HashSet<String> answer_label = new HashSet<String>();
		answer_label.add("Lem");

		int before_size = 0;
		
		double max_f = 0;
		String max_label = null;
		PRF max_prf = new PRF();
		do
		{
			before_size = answer_label.size();
			for (String label : label_set)
			{
				if (answer_label.contains(label) == false)
				{
					continue;
				}

				HashSet<String> ready_set = new HashSet<String>();
				ready_set.add(label);
				ready_set.addAll(answer_label);

				label_save_feature_file(ready_set, train_feature_path,
						train_feature_result);
				label_save_feature_file(ready_set, test_feature_path,
						test_feature_result);

				String model_path = "./cyd/forward_algorithm/model";

				try
				{
					MeTrainer.TrainMaxent(train_feature_result, model_path);
				}
				catch (IllegalStateException e)
				{
					continue;
				}

				String decoder_result = "./cyd/forward_algorithm/trigger_result";
				MeDecoder decoder = new MeDecoder(model_path);
				decoder.decodeOnFeatureTable(test_feature_result,
						decoder_result);

				PRF prf_result = prf(decoder_result);

				if (prf_result.f > max_f)
				{
					max_label = label;
					max_f = prf_result.f;
					max_prf = prf_result;
					System.out.println("max : " + max_label + " : "
							+ prf_result.p + " : " + prf_result.r + " : "
							+ prf_result.f);
				}
				System.out.println(label + " : " + prf_result.p + " : "
						+ prf_result.r + " : " + prf_result.f);
			}

			if (max_label != null)
			{
				answer_label.add(max_label);
			}
			for (String label : answer_label)
			{
				fw.write(label + " ");
			}
			fw.write(max_prf.p + " " + max_prf.r + " " + max_prf.f + "\n");
			
			break;
		} while (before_size != answer_label.size());

		fw.close();

		System.exit(0);
	}

	public static void main(String[] args) throws Exception
	{
		String label_path = "./cyd/forward_algorithm/feature_label.txt";
		String train_feature_path = "./cyd/forward_algorithm/trigger_train_feature";
		String test_feature_path = "./cyd/forward_algorithm/trigger_test_feature";

		String train_feature_result = "./cyd/forward_algorithm/train_feature_result";
		String test_feature_result = "./cyd/forward_algorithm/test_feature_result";

		String final_result = "./cyd/forward_algorithm/result.txt";

		forward_algorithm(label_path, train_feature_path, train_feature_result,
				test_feature_path, test_feature_result, final_result);

		System.exit(0);
	}
}

class PRF
{
	double p;
	double r;
	double f;

	PRF()
	{
		this.p = 0;
		this.r = 0;
		this.f = 0;
	}

	PRF(double p, double r, double f)
	{
		this.p = p;
		this.r = r;
		this.f = f;
	}
}