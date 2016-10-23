package cyd.frame.sim;

import java.io.*;
import java.util.*;
import java.text.*;
import edu.sussex.nlp.jws.*;

// 'TestExamples': how to use Java WordNet::Similarity
// David Hope, 2008
public class Similarity
{
	private static Similarity sim = null;
	
	public static Similarity getSingleton() throws Exception
	{
		if (sim == null)
		{
			sim = new Similarity("F:\\chenyadong\\软件\\WordNet");
		}
		return sim;
	}
	
	static Lin lin = null;
	
	public Similarity(String wordnet_path) throws Exception
	{
		JWS ws = new JWS(wordnet_path, "2.1");
		lin = ws.getLin();
	}
	
	public double get_sim(String a, String b, String pos) throws Exception
	{
		return lin.max(a, b, pos);
	}
	
	public static void main(String[] args) throws Exception
	{
		String m = "attack";
		String n = "strike";
		String pos = "v";
		double a = Similarity.getSingleton().get_sim(m, n, pos);
		double b = Similarity.getSingleton().get_sim("work", "strike", "v");
		
		System.out.println(a);
		System.out.println(b);
	}
}
