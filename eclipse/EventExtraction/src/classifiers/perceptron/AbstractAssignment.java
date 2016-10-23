package classifiers.perceptron;

import java.util.Comparator;
import java.util.List;

import util.Controller;

import commons.Alphabets;
import commons.FeatureVector;
import commons.FeatureVectorSequence;


/**
 * This is an abstract class for assignment
 * currently can be empty. 
 * Later we can extract some common parts into this class
 * @author qli
 *
 */
public abstract class AbstractAssignment implements Comparable<AbstractAssignment>
{
	// the alphabet of the label for each node (token), shared by the whole application
	// they should be consistent with SentenceInstance object
	public Alphabets alphabets;
	
	// controller of the program
	public Controller controller;
	
	/*
	 * indicates if it violates gold-standard, useful for learning
	 */
	protected boolean violate = false;
	
	// the feature vector of the current assignment
	public FeatureVectorSequence featVecSequence;
	
	// the score of the assignment, it can be partial score when the assignment is not complete
	protected double score = 0.0;
	
	public List<Double> partial_scores;
	
	public List<Double> getPartial_scores()
	{
		return partial_scores;
	}
	
	public void setScore(double sc)
	{
		score = sc;
	}
	
	/**
	 * get the score according to feature and weight 
	 * @return
	 */
	public double getScore()
	{
		return score;
	}
	
	public FeatureVector getFV(int index)
	{
		return featVecSequence.get(index);
	}
	
	public FeatureVector getCurrentFV()
	{
		return featVecSequence.get(state);
	}
	
	public FeatureVectorSequence getFeatureVectorSequence()
	{
		return featVecSequence;
	}
	
	public void addFeatureVector(FeatureVector fv)
	{
		featVecSequence.add(fv);
	}
	
	/**
	 * the index of last processed (assigned/searched) token
	 */
	public int state = -1;
	
	public abstract void incrementState();
	
	public void retSetState()
	{
		state = -1;
	}
	
	public int getState()
	{
		return state;
	}
	
	/**
	 * clear feature vectors, so that the Target assignment can creates its feature vector in beamSearch
	 */
	public void clearFeatureVectors()
	{
		for(int i=0; i<this.featVecSequence.size(); i++)
		{
			this.featVecSequence.sequence.set(i, new FeatureVector());
		}
	}
	
	public void setViolate(boolean violate)
	{
		this.violate = violate;
	}
	
	public boolean getViolate()
	{
		return this.violate;
	}

	public static class ScoreComparator implements Comparator<AbstractAssignment>
	{
		@Override
		public int compare(AbstractAssignment assn1, AbstractAssignment assn2)
		{
			if(Math.abs(assn1.getScore() - assn2.getScore()) < 0.00001)
			{
				return 0;
			}
			if(assn1.getScore() > assn2.getScore())
			{
				return -1;
			}
			else 
			{
				return 1;
			}
		}	
	}
	
	@Override
	public int compareTo(AbstractAssignment assn)
	{
		// NOTE: reverse the order to make descending sort 
		return Double.compare(assn.getScore(), this.getScore());
	}

	/**
	 * define the loss function of two assignments 
	 * primarily for MIRA update for now 
	 * @param abstractAssignment
	 * @return
	 */
	public double loss(AbstractAssignment abstractAssignment)
	{
		return 0;
	}
}
