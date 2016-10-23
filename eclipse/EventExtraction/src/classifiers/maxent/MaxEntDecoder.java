package classifiers.maxent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.mallet.classify.Classifier;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;

public class MaxEntDecoder 
{
	// the model
	Classifier classifier;
	// the name of this classifier
	String label;
	
	public MaxEntDecoder(File modelFile, String name) throws IOException
	{
		// read model
		ObjectInputStream oos = new ObjectInputStream(new FileInputStream(modelFile));
	    try
		{
			classifier = (Classifier) oos.readObject();
			oos.close();
		    this.label = name;
		} 
	    catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * given a feature vector, in the form of:
	 * [data ...] should like: feature:value
	 * @param featureVector
	 * @return
	 * @throws FileNotFoundException
	 */
	public String decodeOnFeatureVector(String featureVector) 
	{
		InputStreamReader inputReader = new InputStreamReader(new ByteArrayInputStream(featureVector.getBytes()));
        CsvIterator reader =
            new CsvIterator(inputReader,
                            "(\\w+)\\s+(\\S+)\\s+(.*)",
                            3, 2, 1);  // (data, label, name) field indices               

        InstanceList instances = new InstanceList(classifier.getInstancePipe());
        instances.addThruPipe(reader);
        
        // there is only one instance encoded in the featureVector
    	Instance instance = instances.get(0);
        Labeling labeling = classifier.classify(instance).getLabeling();

        String best = labeling.getBestLabel().toString();
        return best;
    }
	
	public void decodeOnFeatureTable(File featureTable) throws FileNotFoundException 
	{
        CsvIterator reader =
            new CsvIterator(new FileReader(featureTable),
                            "(\\w+)\\s+(\\S+)\\s+(.*)",
                            3, 2, 1);  // (data, label, name) field indices               

        InstanceList instances = new InstanceList(classifier.getInstancePipe());
        instances.addThruPipe(reader);

        // get the predicted labeling                                           
        for(int i=0; i<instances.size(); i++) 
        {
        	Instance instance = instances.get(i);
            Labeling labeling = classifier.classify(instance).getLabeling();
            
            if(!labeling.getBestLabel().equals(instance.getTarget()))
            {
            	System.out.println("incorrect");
            }
            
            System.out.println(instance.getName());
            System.out.println(labeling.getBestLabel());
            for (int rank = 0; rank < labeling.numLocations(); rank++)
            {
                System.out.print(labeling.getLabelAtRank(rank) + ":" +
                                 labeling.getValueAtRank(rank) + " ");
            }
            System.out.println();
        }
    }
	
	static public void main(String[] args) throws IOException, ClassNotFoundException
	{
		Pattern p = Pattern.compile("(\\w+)\\s+(\\S+)\\s+(.*)");
		Matcher matcher = p.matcher("Relation4 Part-Whole-> HeadLemma12:texas_brownsville:1");
		if(matcher.matches())
		{
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
		}
		
		MaxEntDecoder trainer = new MaxEntDecoder(new File("/Users/qli/maxent_relation"), "relationClassifier");
		trainer.decodeOnFeatureTable(new File("Temp/FeatureTableRelation"));
	}
}
