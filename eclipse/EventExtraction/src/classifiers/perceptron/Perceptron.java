package classifiers.perceptron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.SerializationUtils;

import util.Controller;

import commons.Alphabets;
import commons.FeatureVector;


/**
 * This class implements the learning/decoding part of perceptron, 
 * as well as serialization/deserialization
 * @author che
 *
 */
public class Perceptron implements java.io.Serializable
{
	private static final long serialVersionUID = -8870655270637917361L;

	public Alphabets alphabets;
	
	// the settings of the perceptron
	public Controller controller = new Controller();
	
	// the weights of features, however, 
	protected FeatureVector weights;
	protected FeatureVector avg_weights;
	protected FeatureVector avg_weights_base; // for average weights update
	// intermediate factor for avg weights 
	private double c = 0;
	private int error_num = 0;
	
	// the beam searcher / interence algorithm
	protected AbstractBeamSearch beamSearcher;
	
	// the evaluator for dev set
	AbstractEvaluator evaluator;
	
	// default constructor 
	public Perceptron(Alphabets alphabets,
			Controller controller, AbstractBeamSearch beamSearcher, AbstractEvaluator eval)
	{
		this.alphabets = alphabets;
		this.controller = controller;
		
		// specify the beam searcher
		this.beamSearcher = beamSearcher;
		beamSearcher.SetModel(this);
		this.evaluator = eval;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
	}
	
	// default constructor 
	public Perceptron(Controller controller)
	{
		this.alphabets = new Alphabets();
		this.controller = controller;
		
		// create weights vector
		this.setWeights(new FeatureVector());
		this.avg_weights_base = new FeatureVector();
	}
	
	/**
	 *  given a single instance, decode, and give the best assignment
	 * @param instance
	 * @return
	 */
	public AbstractAssignment decoding(AbstractInstance inst)
	{
		List<? extends AbstractAssignment> assns = beamSearcher.beamSearch(inst, controller.beamSize, false, 0);
		return assns.get(0);
	}
	
	/**
	 *  given an instanceList, decode, and give the best assignmentList
	 * @param instance
	 * @return
	 */
	public List<AbstractAssignment> decoding(List<? extends AbstractInstance> instanceList)
	{
		List<AbstractAssignment> ret = new ArrayList<AbstractAssignment>();
		for(AbstractInstance inst : instanceList)
		{
			List<? extends AbstractAssignment> assns = beamSearcher.beamSearch(inst, controller.beamSize, false, 0);
			ret.add(assns.get(0));
		}
		return ret;
	}
	
	public void learning(List<? extends AbstractInstance> trainingList, int maxIter)
	{
		learning(trainingList, null, 0);
	}
	
	/**
	 * given an training instance list, and max number of iterations, learn weights by perceptron
	 * in each iteration, use current weights to test the dev instance list, and in each peak, save the model to file
	 * @param trainingList
	 * @param maxIter
	 */
	public void learning(List<? extends AbstractInstance> trainingList, List<? extends AbstractInstance> devList, int cutoff)
	{	
		// traverse the training instance to collect some statistics
		beamSearcher.collectStatistics(trainingList);
		
		System.out.print("Parameter size: " + this.weights.size() + "\t");
		System.out.println("Node target alphabet:" + this.alphabets.nodeTargetAlphabet);
		System.out.println("edge target alphabet:" + this.alphabets.edgeTargetAlphabet);
		System.out.println("instance num: " + trainingList.size());
		
		// online learning with beam search and early update
		long totalTime = 0;
		AbstractEvaluator.Score max_score = new AbstractEvaluator.Score()
		{
			@Override
			public double getFinalScore()
			{
				return 0.0;
			}	
		};
		
		int best_iter = 0;
		FeatureVector best_weights = new FeatureVector();
		FeatureVector best_avg_weights = new FeatureVector();
		int iter = 0;
		this.c = 1; // for averaged parameter
		for(iter=0; iter<this.controller.maxIterNum; iter++)
		{
			long startTime = System.currentTimeMillis();	
			this.error_num = 0;
			
			for(AbstractInstance instance : trainingList)
			{
				List<? extends AbstractAssignment> assns = beamSearcher.beamSearch(instance, controller.beamSize, true, iter);
				if(controller.miraUpdate != null && controller.miraUpdate)
				{
					// note: MIRA is only available for ere
					updateMIRA(assns, instance.target_prefix);
				}
				else
				{
					update(assns.get(0), instance.target);
				}
			}
			
			long endTime = System.currentTimeMillis();
			long iterTime = endTime - startTime;
			totalTime += iterTime;
			System.out.println("\nIter " + iter + "\t error num: " + error_num + "\t time:" + iterTime + "\t feature size:" + this.weights.size());
			
			// use current weight to decode and evaluate developement instances
			if(devList != null)
			{
				if(controller.avgArguments)
				{
					makeAveragedWeights(c);
				}
				
				List<AbstractAssignment> devResult = decoding(devList);
				AbstractEvaluator.Score dev_score = evaluator.evaluate(devResult, getCanonicalInstanceList(devList));
				
				System.out.println("Dev " + dev_score);
				//if((dev_score.trigger_F1 - max_score.trigger_F1) >= 0.001 || 
				//		(Math.abs(dev_score.trigger_F1 - max_score.trigger_F1) < 0.001) && (dev_score.arg_F1 - max_score.arg_F1) >= 0.001)
//				if((dev_score.getFinalScore() - max_score.getFinalScore()) >= 0.001)
//				{
//					// dump the model as best one
//					best_weights = this.weights.clone();
//					if(this.controller.avgArguments)
//					{
//						best_avg_weights = this.avg_weights.clone();
//					}
//					best_iter = iter;
//					max_score = dev_score;
//				}
			}
			
			if(error_num == 0)
			{
				// converge
				break;
			}
		}
		
		if(iter < this.controller.maxIterNum)
		{
			// converge
			System.out.println("converge in iter " + iter + "\t time:" + totalTime);
			iter++;
		}
		else
		{
			// stop without convergency
			System.out.println("Stop without convergency" + "\t time:" + totalTime);
		}
		
//		if(devList != null)
//		{
//			this.weights = best_weights;
//			if(this.controller.avgArguments)
//			{
//				this.avg_weights = best_avg_weights;
//			}
//			System.out.println("best performance on dev set: iter " + best_iter + " :" + max_score);
//		}
//		else 
		if(this.controller.avgArguments)
		{
			makeAveragedWeights(c);
		}
		
		// print out num of invalid update
		beamSearcher.print_num_update(System.out);
		
		return;
	}
	
	public List<? extends AbstractInstance> getCanonicalInstanceList(
			List<? extends AbstractInstance> devList)
	{
		return devList;
	}

	/**
	 *  w_0 - w_a/c (w_0 is standard weights, w_a is the base of averaged weights) 	
	 * @param c
	 */
	private void makeAveragedWeights(double c)
	{
		this.avg_weights = new FeatureVector();
		for(Object feat : this.weights.getMap().keySet())
		{
			double value = this.weights.get(feat); // w_0
			double value_a = this.avg_weights_base.get(feat); // w_a
			value = value - value_a / c; // w_0 - w_a/c
			this.avg_weights.add(feat, value);
		}
	}

	/**
	 * 1-best MIRA
	 * @param assns
	 * @param target
	 */
	public void updateMIRA(AbstractAssignment assn, AbstractAssignment target_prefix)
	{
		List<AbstractAssignment> assns = new ArrayList<AbstractAssignment>();
		assns.add(assn);
		updateMIRA(assns, target_prefix);
	}
	
	/**
	 * k-best MIRA
	 * @param assns
	 * @param target it's actually target_prefix during beam search
	 */
	public void updateMIRA(List<? extends AbstractAssignment> assns, AbstractAssignment target_prefix)
	{
//		if(assns.get(0).getViolate())
		{
			int K = controller.miraK;
			target_prefix.getFeatureVectorSequence();
			FeatureVector[] dist = new FeatureVector[K];
			double b[] = new double[K];
			
			// calculate dist and b
			// dist = f(y*) - f(y_k)
			// b = w_0 * (f(y*) - f(y_k))
			for(int k=0; k<K; k++)
			{
				// f(y*) - f(y_k)
				dist[k] = new FeatureVector();
				for(int j=0; j<= assns.get(k).getState(); j++)
				{
					dist[k].addDelta(target_prefix.getFV(j), assns.get(k).getFV(j), 1.0);
				}
				
				// sore(y*) - score(y_k) = w_0 * (f(y*) - f(y_k))
				double dist_score = this.weights.dotProduct(dist[k]);
				double loss_k = target_prefix.loss(assns.get(k));
				b[k] = loss_k - dist_score;
			}
			
			// get alpha by maximing the MIRA objective
			double[] alpha = hildreth(dist, b);
			
			// w = w_0 + alpha * dist
			for(int k = 0; k < K; k++) 
			{
				FeatureVector fv = dist[k];
				// update weights based on fv and alpha[k]
				for(Entry<Object, Double> feat : fv.getMap().entrySet())
				{
					this.weights.add(feat.getKey(), alpha[k] * feat.getValue());
					if(this.controller.avgArguments)
					{
						this.avg_weights_base.add(feat.getKey(), this.c * alpha[k] * feat.getValue());
					}
				}
			}
		}
		
		if(assns.get(0).getViolate())
		{
			this.error_num++;
		}
		
		if(this.controller.avgArguments)
		{
			// for avg parameters
			this.c++;
		}
	}
	
	/**
	 * coordinate assent algorithm adopted from Ryan McDonalds cold
	 * @param a  \delta (f',f*)
	 * @param b w_0 \delta (f',f*) - Loss
	 * @return
	 */
    private double[] hildreth(FeatureVector[] a, double[] b) 
    {
		int i;
		int max_iter = 10000;
		double eps = 0.00000001;
		double zero = 0.000000000001;
		double[] alpha = new double[b.length];
	
		double[] F = new double[b.length];
		double[] kkt = new double[b.length];
		double max_kkt = Double.NEGATIVE_INFINITY;
	
		int K = a.length;
		double[][] A = new double[K][K];
		boolean[] is_computed = new boolean[K];
		for(i = 0; i < K; i++) {
		    A[i][i] = a[i].dotProduct(a[i]);
		    is_computed[i] = false;
		}
					
		int max_kkt_i = -1;
		for(i = 0; i < F.length; i++) {
		    F[i] = b[i];
		    kkt[i] = F[i];
		    if(kkt[i] > max_kkt) { max_kkt = kkt[i]; max_kkt_i = i; }
		}
	
		int iter = 0;
		double diff_alpha;
		double try_alpha;
		double add_alpha;
		while(max_kkt >= eps && iter < max_iter) {
		    diff_alpha = A[max_kkt_i][max_kkt_i] <= zero ? 0.0 : F[max_kkt_i]/A[max_kkt_i][max_kkt_i];
		    try_alpha = alpha[max_kkt_i] + diff_alpha;
		    add_alpha = 0.0;
	
		    if(try_alpha < 0.0)
			add_alpha = -1.0 * alpha[max_kkt_i];
		    else
			add_alpha = diff_alpha;
	
		    alpha[max_kkt_i] = alpha[max_kkt_i] + add_alpha;
	
		    if (!is_computed[max_kkt_i]) {
				for(i = 0; i < K; i++) {
				    A[i][max_kkt_i] = a[i].dotProduct(a[max_kkt_i]); // for version 1
				    is_computed[max_kkt_i] = true;
				}
		    }
	
		    for(i = 0; i < F.length; i++) {
			F[i] -= add_alpha * A[i][max_kkt_i];
			kkt[i] = F[i];
			if(alpha[i] > zero)
			    kkt[i] = Math.abs(F[i]);
		    }
	
		    max_kkt = Double.NEGATIVE_INFINITY;
		    max_kkt_i = -1;
		    for(i = 0; i < F.length; i++)
			if(kkt[i] > max_kkt) { max_kkt = kkt[i]; max_kkt_i = i; }
		    iter++;
		}
		return alpha;
    }
	
	/**
	 * given an assignment, and the gold-standard, update the weights
	 * @param assn
	 * @param target
	 * @param c 
	 * @return return true if it's updated, i.e. the assn is not correct
	 */
	public void update(AbstractAssignment assn, AbstractAssignment target)
	{
		// do update
		if(assn.getViolate())
		{
			// the beam search may return a early assignment, and we only update the prefix
			for(int i=0; i <= assn.getState(); i++)
			{
				// weights = \phi(y*) - \phi(y)
				this.getWeights().addDelta(target.getFeatureVectorSequence().get(i), assn.getFeatureVectorSequence().get(i), 1.0);

				if(this.controller.avgArguments)
				{
					this.avg_weights_base.addDelta(target.getFeatureVectorSequence().get(i), 
							assn.getFeatureVectorSequence().get(i), this.c);
				}
			}
			
			this.error_num++;
		}
		
		if(this.controller.avgArguments)
		{
			// for avg parameters
			this.c++;
		}
	}
	
	public void setWeights(FeatureVector weights)
	{
		this.weights = weights;
	}

	public FeatureVector getWeights()
	{
		return weights;
	}
	
	/**
	 * serialize the model (mainly weights/alphabets) to the file
	 * @param modelFile
	 */
	static public void serializeObject(Serializable model, File modelFile)
	{
		try
		{
			OutputStream stream = new FileOutputStream(modelFile);
			SerializationUtils.serialize(model, stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * deserialize a saved model from a file
	 * @param modelFile
	 * @return
	 */
	public static Perceptron deserializeObject(File modelFile)
	{
		Perceptron model = null;
		try
		{
			InputStream stream = new FileInputStream(modelFile);
			model = (Perceptron) SerializationUtils.deserialize(stream);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return model;
	}

	public FeatureVector getAvg_weights()
	{
		return avg_weights;
	}
}
