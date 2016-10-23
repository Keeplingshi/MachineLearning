package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.io.FileUtils;

public class Controller implements java.io.Serializable
{
	private static final long serialVersionUID = -2757316522836157583L;
	
	public Integer beamSize = 5;
	public Integer maxIterNum = 30;
	public Boolean avgArguments = true;
	// skip the features of NON argument link assignment
	public Boolean skipNonArgument = true;
	// use global feature
	public Boolean useGlobalFeature = true;
	// order of trigger labeling 0/1 0 stands for unigram, 1 stands for bigram
	public Integer order = 1;
	// define the feature cutoff threshold
	// default 0: no cutoff
	public Integer cutoff = 0; 
	
	// event specific arguments

	public Boolean skipNonEventSent = true; 
	// whether use crossSent inference 
	public Boolean crossSent = false;
	// whether use BeamSearchCluster or BeamSearchClusterSeq
	public Boolean crossSentReranking = false;
	public Boolean addNeverSeenFeatures = true;
	// the type of evaluator during training 1 is EvaluatorLoose, 0 is evaluatorFinal
	public Integer evaluatorType = 0;
	
	// relation specific arguments
	// determine whether remove DISC as in Chan & Roth 2011 or not
	public Boolean removeDISC = true;
	// determine the granularity of relation 0: directed subtype 1: directed type (coarse)
	// 2: undirected type (coarse-grained, and don't distinguish argument order)
	// default value is currently set as 1
	public Integer relationGranularity = 1;
	// define the weight for relation features
	// the loss function of perceptron is 
	// (1 - \lamda) \Phi(x,y)_{entity} \times w + \lambda \Phi(x,y)_{relation} \times w
	// - (1 - \lamda) \Phi(x,z)_{entity} \times w + \lambda \Phi(x,z)_{relation} \times w
	public Double lambda = 0.5;
	// the argument is true is we only consider entity mention in the perceptron
	// which means relation is not modeled in the perceptron
	public Boolean entityOnly = false;
	public Boolean dictInMentionBaseline = true;  
	
	// for switching between 2004 and 2005 data format
	public String year = "2004";
	public Boolean relationOnly = false;

	// for the trick that most related work in relation
	// like Sun et al. 2011. Mention pair that are not separated by 
	// no more than 3 other 
	// mentions are considered relation candidate, by default this is disabled
	public Integer mention_distance = Integer.MAX_VALUE;
	
	// if true, ignore the links of the structures in the first full pass
	// of the training data
	public Boolean head_start = false;  
	public Boolean relationEventFeature = true;
	public Boolean miraUpdate = false;
	// the number k in k-best mira, default value should be 1
	public Integer miraK = 1; 
	// 0: f1-loss, 1: counting-loss, 2: 0-1 loss, 3: f1-relatexed loss
	public Integer loss = 0; 
	
	public Boolean useFrameNet = true;
	
	public Integer getMention_distance()
	{
		if(mention_distance == null)
		{
			mention_distance = Integer.MAX_VALUE;
		}
		return mention_distance;
	}
	
	public Controller()
	{
		;
	}
	
	public void readFromFile(File fileName)
	{
		String content;
		try
		{
			content = FileUtils.readFileToString(fileName);
			this.setValueFromArguments(content.split("\\s+"));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * set the values according to command line arguments
	 * @param line
	 */
	public void setValueFromArguments(String[] arguments)
	{
		for(String arg : arguments)
		{
			String[] colns = arg.split("=");
			if(colns.length != 2)
			{
				System.out.println("Malform argument: " + arg);
				System.exit(-1);
			}
			Field[] fields = this.getClass().getFields();
			for(Field field : fields)
			{
				if(field.getName().equalsIgnoreCase(colns[0]))
				{
					// matched an arg
					try
					{
						Object value;
						if(field.getGenericType().equals(Integer.class))
						{
							value = Integer.valueOf(colns[1]);
						}
						else if(field.getGenericType().equals(Double.class))
						{
							value = Double.valueOf(colns[1]);
						}
						else if(field.getGenericType().equals(Boolean.class))
						{
							value = Boolean.valueOf(colns[1]);
						}
						else
						{
							// default type is string
							value = colns[1];
						}
						field.set(this, value);
					} 
					catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					} 
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		Field[] fields = this.getClass().getFields();
	    for(Field field : fields) {
	        try
			{
	        	Object value = field.get(this);
				String name = field.getName();
		        ret.append(" " + name + "=" + value.toString());
			} 
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} 
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
	        
	    }
		return ret.toString().trim();
	}
	
	public static void main(String[] args)
	{
		Controller controller = new Controller();
		String comm = "beamSize=20 year=2005";
		controller.setValueFromArguments(comm.split("\\s+"));
		System.out.println(controller);
		controller.setValueFromArguments(controller.toString().split("\\s+"));
		System.out.println(controller);
	}
}
