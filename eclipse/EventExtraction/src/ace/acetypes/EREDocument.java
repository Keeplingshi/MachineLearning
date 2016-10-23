package ace.acetypes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import util.Span;


/**
 * Convert DEFT ERE corpus to ACE format
 * @author qli
 *
 */
public class EREDocument extends AceDocument
{

	public EREDocument(String textFileName, String xmlfileName)
			throws IOException
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
				
				// analysize the content
				analyzeDocument (textFileName, xmlfileName);
			} 
			catch (ParserConfigurationException e)
			{
				e.printStackTrace();
			} 
			catch (SAXException e)
			{
				e.printStackTrace();
			}
		}  
	}
	
	private void analyzeDocument (String textFileName, String APFfileName)
		    throws SAXException, IOException {
			Document xmlDoc = builder.parse(APFfileName);
			fileTextWithXML = readDocument(textFileName);
			fileText = eraseXML(fileTextWithXML);
			readXMLdocument (xmlDoc, fileText);
	}

	private void readXMLdocument(Document xmlDoc, String fileText)
	{
		NodeList sourceFileElements = xmlDoc.getElementsByTagName("deft_ere");
		Element sourceFileElement = (Element) sourceFileElements.item(0);
		docID = sourceFileElement.getAttribute("docid");
		sourceType = sourceFileElement.getAttribute("source_type");

		NodeList entityElements = xmlDoc.getElementsByTagName("entity");
		for (int i=0; i<entityElements.getLength(); i++) {
			Element entityElement = (Element) entityElements.item(i);
			AceEntity entity = CreateAceEntity (entityElement, fileText);
			addEntity(entity);
		}
		NodeList relationElements = xmlDoc.getElementsByTagName("relation");
		for (int i=0; i<relationElements.getLength(); i++) {
			Element relationElement = (Element) relationElements.item(i);
			AceRelation relation = CreateAceRelation (relationElement, this, fileText);
			addRelation(relation);
		}
		NodeList eventElements = xmlDoc.getElementsByTagName("event");
		for (int i=0; i<eventElements.getLength(); i++) {
			Element eventElement = (Element) eventElements.item(i);
			AceEvent event = CreateAceEvent(eventElement, this, fileText);
			addEvent(event);
		}
	}

	/**
	 * givne an Element in ERE format, create a event
	 * @param eventElement
	 * @param ereDocument
	 * @param fileText
	 * @return
	 */
	private AceEvent CreateAceEvent(Element eventElement,
			EREDocument ereDocument, String fileText)
	{
		AceEvent ret = new AceEvent();
		ret.id = eventElement.getAttribute("id");
		ret.type = eventElement.getAttribute("type");
		ret.subtype = eventElement.getAttribute("subtype");
	
		NodeList mentionElements = eventElement.getElementsByTagName("event_mention");
		for (int j=0; j<mentionElements.getLength(); j++) {
			Element mentionElement = (Element) mentionElements.item(j);
			AceEventMention mention = CreateAceEventMention(mentionElement);
			ret.addMention(mention);
		}
		
		return ret;
	}

	private AceEventMention CreateAceEventMention(Element mentionElement)
	{
		AceEventMention ret = new AceEventMention();
		ret.id = mentionElement.getAttribute("id");
		ret.confidence = 0.0f;	// Qi: to avoid empty string exception
		
		NodeList anchors = mentionElement.getElementsByTagName("trigger");
		Element anchorElement = (Element) anchors.item(0);
		int start = Integer.parseInt(anchorElement.getAttribute("offset"));
		if(this.docID.contains("fbis_eng"))
		{
			start = convertOffset4OSC(start);
		}
		int end = Integer.parseInt(anchorElement.getAttribute("length"));
		end = start + end - 1;
		ret.anchorExtent = new Span(start, end);
		ret.anchorText = fileText.substring(ret.anchorExtent.start(), ret.anchorExtent.end()+1);
		NodeList arguments = mentionElement.getElementsByTagName("arg");
		for (int j=0; j<arguments.getLength(); j++) {
			Element argumentElement = (Element) arguments.item(j);
			AceEventMentionArgument argument = CreateAceEventMentionArgument(argumentElement);
			ret.addArgument(argument);
		}
		NodeList places = mentionElement.getElementsByTagName("place");
		for (int j=0; j<places.getLength(); j++) {
			Element placeElement = (Element) places.item(j);
			AceEventMentionArgument argument = CreatePlaceArgument(placeElement);
			ret.addArgument(argument);
		}
		ret.extent = ret.anchorExtent;
		ret.text = ret.anchorText;
		return ret;
	}

	private AceEventMentionArgument CreatePlaceArgument(Element placeElement)
	{
		AceEventMentionArgument ret = new AceEventMentionArgument();
		ret.role = "Place";
		String mentionid = placeElement.getAttribute("entity_mention_id");
		ret.value = this.findMention(mentionid);
		ret.confidence = 0.0; // Qi: to avoid empty string exception
		return ret;
	}

	private AceEventMentionArgument CreateAceEventMentionArgument(Element argumentElement)
	{
		AceEventMentionArgument ret = new AceEventMentionArgument();
		ret.role = argumentElement.getAttribute("type");
		String mentionid = argumentElement.getAttribute("entity_mention_id");
		ret.value = this.findMention(mentionid);
		ret.confidence = 0.0; // Qi: to avoid empty string exception
		return ret;
	}

	/**
	 * given an Element in ERE format, create a relation
	 * @param relationElement
	 * @param ereDocument
	 * @param fileText
	 * @return
	 */
	private AceRelation CreateAceRelation(Element relationElement,
			EREDocument ereDocument, String fileText)
	{
		AceRelation ret = new AceRelation();
		ret.id = relationElement.getAttribute("id");
		ret.type = relationElement.getAttribute("type");
		ret.subtype = relationElement.getAttribute("subtype");
		
		NodeList mentionElements = relationElement.getElementsByTagName("relation_mention");
		for (int j=0; j<mentionElements.getLength(); j++) {
			Element mentionElement = (Element) mentionElements.item(j);
			AceRelationMention mention = CreateAceRelationMention(mentionElement);
			ret.addMention(mention);
		}
		
		ret.arg1 = (AceEntity) ret.mentions.get(0).arg1.getParent();
		ret.arg2 = (AceEntity) ret.mentions.get(0).arg2.getParent();
		return ret;
	}

	private AceRelationMention CreateAceRelationMention(Element mentionElement)
	{
		AceRelationMention ret = new AceRelationMention();
		ret.id = mentionElement.getAttribute("ID");
		// ret.text = fileText.substring(extent.start(), extent.end()+1);
		// get arguments
		NodeList arguments = mentionElement.getElementsByTagName("arg");
		for (int j=0; j<arguments.getLength(); j++) {
			Element argument = (Element) arguments.item(j);
			String mentionid = argument.getAttribute("entity_mention_id");
			String role = "Arg-1";
			if(j > 0)
			{
				role = "Arg-2";
			}
			if (role.equals("Arg-1")) {
				ret.arg1 = this.findEntityMention(mentionid);
			} else if (role.equals("Arg-2")) {
				ret.arg2 = this.findEntityMention(mentionid);
			} else if (AceRelation.timeRoles.contains(role)) {
			// ignore time roles at present
			} else {
				System.err.println ("*** invalid ROLE for relation mention");
			}
		}
		int start = ret.arg1.head.compareTo(ret.arg2.head) < 0 ? 
				ret.arg1.head.start() : ret.arg2.head.start();
		int end = ret.arg1.head.compareTo(ret.arg2.head) < 0 ? 
				ret.arg2.head.end() : ret.arg1.head.end();
		ret.extent = new Span(start, end);
		ret.text = fileText.substring(ret.extent.start(), ret.extent.end()+1);
		return ret;
	}

	/**
	 * given an Element in ERE format, create a relation
	 * @param entityElement
	 * @param fileText
	 * @return
	 */
	private AceEntity CreateAceEntity(Element entityElement, String fileText)
	{
		AceEntity ret = new AceEntity();
		ret.id = entityElement.getAttribute("id");
		ret.type = entityElement.getAttribute("type");
		
		NodeList mentionElements = entityElement
				.getElementsByTagName("entity_mention");
		for (int j = 0; j < mentionElements.getLength(); j++) {
			Element mentionElement = (Element) mentionElements.item(j);
			AceEntityMention mention = CreateAceEntityMention(mentionElement);
			ret.addMention(mention);
		}
		// sort mentions by end of head -- earlier mention first
		for (int i = 0; i < ret.mentions.size() - 1; i++) {
			for (int j = i + 1; j < ret.mentions.size(); j++) {
				AceEntityMention meni = (AceEntityMention) ret.mentions.get(i);
				AceEntityMention menj = (AceEntityMention) ret.mentions.get(j);
				if (meni.head.end() > menj.head.end()) {
					ret.mentions.set(i, menj);
					ret.mentions.set(j, meni);
				}
			}
		}
		return ret;
	}

	private AceEntityMention CreateAceEntityMention(Element mentionElement)
	{
		AceEntityMention ret = new AceEntityMention();
		ret.id = mentionElement.getAttribute("id");
		ret.type = mentionElement.getAttribute("noun_type");
		if(ret.type.equals("NA"))
		{
			ret.type = "NAM";
		}
		int start = Integer.parseInt(mentionElement.getAttribute("offset"));
		if(this.docID.contains("fbis_eng"))
		{
			start = convertOffset4OSC(start);
		}
		int end = Integer.parseInt(mentionElement.getAttribute("length"));
		end = start + end - 1;
		ret.head = new Span(start, end);
		ret.headText = fileText.substring(ret.head.start(), ret.head.end()+1);
		ret.extent = ret.head;
		ret.text = ret.headText;
		return ret;
	}

	/**
	 * in OSC data format, each line is counted as two characters (LF+CR)
	 * we need convert it to normal
	 * @param fileText
	 * @param offset
	 * @return
	 */
	private int convertOffset4OSC(int offset)
	{
		int ret = 0;
		// maintains the adjusted position of each new line
		List<Integer> newlines = new ArrayList<Integer>();
		for(int i=0; i<this.fileText.length(); i++)
		{
			if(this.fileText.charAt(i) == '\n')
			{
				newlines.add(i + newlines.size() + 2);
			}
		}
		
		for(int i=0; i<newlines.size(); i++)
		{
			if(offset < newlines.get(i))
			{
				ret = offset - i;
				return ret;
			}
		}
		// offset is in last line
		ret = offset - newlines.size();
		return ret;
	}
	
	public static void main (String[] args) throws Exception 
	{
		// Qi: test the ERE document object
		String dir = "/Users/qli/Data/ERE-LDC2013E64/LDC2013E64_DEFT_Phase_1_ERE_Annotation_R3/truecase/ere_truecase/";
		String xmlFile = dir + "PROXY_XIN_ENG_20070326.0234.ere.xml";
		String textFile = dir + "PROXY_XIN_ENG_20070326.0234.txt";
	
		AceDocument doc = new EREDocument(textFile, xmlFile);
		doc.write(new PrintWriter(System.out));
	}
}
