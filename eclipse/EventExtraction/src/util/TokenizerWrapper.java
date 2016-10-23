package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * this is a thin wrapper of sentence detector and tokenizer of OpenNLP
 * @author z0034d5z
 *
 */
public class TokenizerWrapper 
{
	public static TokenizerWrapper tokenizerWrapper;
	
	public static TokenizerWrapper getTokenizer() throws InvalidFormatException, IOException
	{
		if(tokenizerWrapper == null)
		{
			tokenizerWrapper = new TokenizerWrapper(new File("data/en-token.bin"));
		}
		return tokenizerWrapper;
	}
	
	TokenizerME tokenizer;
	
	TokenizerWrapper(File model_File) throws InvalidFormatException, IOException
	{
		InputStream modelIn = new FileInputStream(model_File);
		TokenizerModel model = new TokenizerModel(modelIn);
		modelIn.close();	
		tokenizer = new TokenizerME(model);
	}
	
	/**
	 * the tokens
	 * @param sent the tokens
	 * @return the tags for each token
	 */
	public String[] tokenize(String text)
	{
		return tokenizer.tokenize(text);
	}
	
	/**
	 * the span, in which we can know the charactor offset of each token
	 * @param text
	 * @return
	 */
	public util.Span[] tokenizeSpan(String text)
	{
		Span[] spans = tokenizer.tokenizePos(text);
		util.Span[] ret = new util.Span[spans.length];
		int i=0;
		for(Span span : spans)
		{
			util.Span cunySpan = new util.Span(span.getStart(), span.getEnd() - 1);
			ret[i++] = cunySpan;
		}
		return ret;
	}

	
	public static void main(String[] args) throws InvalidFormatException, IOException
	{
		String text = "Troops from the U.S. Army's 101st Airborne Division went to the site on Friday, finding a number of large drums buried in bunkers.";
		String[] sents = TokenizerWrapper.getTokenizer().tokenize(text);
		for(String sent : sents)
		{
			System.out.println(sent);
		}
		
	}
}
