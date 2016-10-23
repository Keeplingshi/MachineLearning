package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * this is a simple function to convert truecasing results from 
 * Stanford CoreNLP toolkit to texts
 * @author qli
 *
 */
public class ConvertTrueCasing
{
	
	
	private static void readTrueCasing(File file, List<Span> spans,
			List<String> words)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try
		{
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			NodeList nodelist = doc.getElementsByTagName("token");
			for(int i=0; i<nodelist.getLength(); i++)
			{
				Node node = nodelist.item(i);
				NodeList children = node.getChildNodes();
				Span span = new Span(0, 0);
				String word = "";
				String truecase = "";
				for(int j=0; j<children.getLength(); j++)
				{
					Node subnode = children.item(j);
					if(subnode.getNodeName().equalsIgnoreCase("CharacterOffsetBegin"))
					{
						span.start = Integer.parseInt(subnode.getTextContent());
					}
					else if(subnode.getNodeName().equalsIgnoreCase("CharacterOffsetEnd"))
					{
						span.end = Integer.parseInt(subnode.getTextContent()) - 1;
					}
					else if(subnode.getNodeName().equalsIgnoreCase("TrueCaseText"))
					{
						word = subnode.getTextContent();
					}
					else if(subnode.getNodeName().equalsIgnoreCase("TrueCase"))
					{
						truecase = subnode.getTextContent();
					}
				}
				if(truecase.equals("O") || truecase.equals("LOWER"))
				{
					continue;
				}
				
				spans.add(span);
				words.add(word);
			}
		} 
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} 
		catch (SAXException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
	}
	
	public static void convert(File src, File cased, File tgt)
	{
		List<Span> spans = new ArrayList<Span>();
		List<String> words = new ArrayList<String>();
		// get truecase information
		readTrueCasing(cased, spans, words);
		// get original text
		try
		{
			String text = FileUtils.readFileToString(src);
			PrintStream out = new PrintStream(tgt);
			for(int i=0; i<text.length(); i++)
			{
				String word = null;
				for(int j=0; j<spans.size(); j++)
				{
					Span span = spans.get(j);
					if(span.start <= i && span.end >= i)
					{
						word = "" + words.get(j).charAt(i - span.start);
						break;
					}
				}
				if(word != null)
				{
					out.print(word);
				}
				else
				{
					// make sure by default is lowercase 
					out.print(Character.toLowerCase(text.charAt(i)));
				}
			}
			out.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	static public void main(String[] args) throws IOException
	{
		File filelist = new File(args[0]);
		List<String> files = FileUtils.readLines(filelist);
		for(String file : files)
		{
			File src = new File(file);
			File cased = new File(file + ".xml");
			File tgt = new File(file + ".truecase");
			convert(src, cased, tgt);
		}
	}
}
