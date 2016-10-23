package classifiers.perceptron;

import java.util.List;

/**
 * This is an abstract evaluator used 
 * int the perceptron algorithm to evaluate the performance on dev set
 * @author qli
 *
 */
public abstract class AbstractEvaluator implements java.io.Serializable
{
	private static final long serialVersionUID = -3504843239076359412L;

	/**
	 * This is an interface for the score to be 
	 * returned for the Evaluator
	 * @author qli
	 */
	public static abstract class Score
	{
		public abstract double getFinalScore();
	}
	
	public abstract Score evaluate(List<? extends AbstractAssignment> devResult,
			List<? extends AbstractInstance> canonicalInstanceList);

}
