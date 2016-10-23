package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class ParseFrameNet
{
	protected static DocumentBuilder builder = null;
	
	static public void readFrame(File dir, File outFile)
	{
		// build the builder
		if (builder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			try
			{
				builder = factory.newDocumentBuilder();
				
				// Qi: make it igoring TDT
				builder.setEntityResolver(new EntityResolver() {
			        @Override
			        public InputSource resolveEntity(String publicId, String systemId)
			                throws SAXException, IOException {
			                return new InputSource(new StringReader(""));
			        }
			    });
				
				PrintStream out = new PrintStream(outFile);
				for(File file : dir.listFiles())
				{
					if(file.getName().endsWith(".xml"))
					{
						// analysize the content
						analyzeDocument(file, out);
					}
				}
				out.close();
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
	}

	static private void analyzeDocument(File file, PrintStream out) throws SAXException, IOException
	{
		Document xmlDoc = builder.parse(file);
		Element frame = (Element) xmlDoc.getElementsByTagName("frame").item(0);
		String frameName = frame.getAttribute("name");
		NodeList lus = xmlDoc.getElementsByTagName("lexUnit");
		for(int i=0; i<lus.getLength(); i++)
		{
			Element unit = (Element) lus.item(i);
			String pos = unit.getAttribute("POS");
			String lemma = unit.getAttribute("name");
			lemma = lemma.replaceAll("\\.\\w$", "");
			out.println(frameName + "\t" + lemma + "\t" + pos);
			if(lemma.contains("-"))
			{
				lemma = lemma.replace("-", "");
				out.println(frameName + "\t" + lemma + "\t" + pos);
			}
		}
	}
	
	public static void main(String[] args)
	{
		File dir = new File(args[0]);
		File outputDir = new File(args[1]);
		readFrame(dir, outputDir);
	}
}
