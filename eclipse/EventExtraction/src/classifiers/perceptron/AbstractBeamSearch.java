package classifiers.perceptron;

import java.io.PrintStream;
import java.util.List;

import commons.FeatureVector;


/**
 * This class defines an Abstract class of the beam search
 * Implementation of this class is used in Perceptron class
 * @author qli
 *
 */
public abstract class AbstractBeamSearch implements java.io.Serializable
{
	private static final long serialVersionUID = 1286867352979041136L;
	
	protected Perceptron model;
	
	public AbstractBeamSearch()
	{
		;
	}
	
	public void SetModel(Perceptron model)
	{
		this.model = model;
	}
	
	protected FeatureVector getWeights(boolean isLearning)
	{
		if(!isLearning && model.controller.avgArguments)
		{
			return model.getAvg_weights();
		}
		else
		{
			return model.getWeights();
		}
	}
	
	/**
	 * given the beam size and problem (search space), search it
	 * @param problem
	 * @param beamSize
	 * @param isLearning
	 * @return
	 */
	public abstract List<? extends AbstractAssignment> beamSearch(AbstractInstance problem, int beamSize, boolean isLearning
			, int iter);

	/**
	 * Collect the statistics (e.g. bigrams) from the training set
	 * before the process of the training 
	 * @param traininglist
	 */
	protected void collectStatistics(
			List<? extends AbstractInstance> traininglist)
	{
		; // do nothing by default
	}
	
	/**
	 * this is to evaluate the cost (credits/score/probability) of a partial results in the beam search
	 * @param partial
	 * @param problem
	 * @return
	 */
	protected abstract double evaluate(AbstractAssignment assn, FeatureVector weights);
	
	/**
	 * print num of invalid update / valid updates
	 * @param out
	 */
	public void print_num_update(PrintStream out)
	{
		; // do nothing by default
	}
}
