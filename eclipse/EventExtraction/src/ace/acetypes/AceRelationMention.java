// -*- tab-width: 4 -*-
//Title:        JET
//Copyright:    2005
//Author:       Ralph Grishman
//Description:  A Java-based Information Extraction Toolkil
//              (ACE extensions)

package ace.acetypes;

import java.io.*;

import org.w3c.dom.*;

import util.Span;



/**
 *  an Ace relation mention, with information from the ACE key.
 */

public class AceRelationMention extends AceMention{

	public boolean equals(Object obj)
	{
		if(obj instanceof AceRelationMention)
		{
			AceRelationMention mention = (AceRelationMention) obj;
			if(this.arg1.equals(mention.arg1) && this.arg2.equals(mention.arg2) 
					&& this.getType().equals(mention.getType()) && this.getSubType().equals(mention.getSubType()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  arg1:  an AceEntityMention
	 */
	public AceEntityMention arg1;
	/**
	 *  arg2:  an AceEntityMention
	 */
	public AceEntityMention arg2;
	/**
	 *  relation:  the AceRelation of which this is a mention
	 */
	public AceRelation relation;
	/**
	 *  our confidence in the presence of this relation mention
	 */
	public double confidence = 1.0;

	AceRelationMention()
	{
		; // default constructor, do nothing
	}
	
	public AceRelationMention(String id, AceEntityMention arg1, AceEntityMention arg2, String fileText, Span extent)
	{
		this.id = id;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.extent = extent;
		text = fileText.substring(extent.start(), extent.end()+1);
	}
	
	/**
	 *  create an AceEntityMention from the information in the APF file.
	 *  @param mentionElement the XML element from the APF file containing
	 *                       information about this mention
	 *  @param acedoc        the AceDocument to which this relation mention
	 *                       belongs
	 */
	public AceRelationMention (Element mentionElement, AceDocument acedoc, String fileText) {
				id = mentionElement.getAttribute("ID");
				confidence = 0; //Qi:modified Double.parseDouble(mentionElement.getAttribute("p"));
				if (AceDocument.ace2005) {
					// get arguments (2005 format)
					NodeList extents = mentionElement.getElementsByTagName("extent");
					Element extentElement = (Element) extents.item(0);
					extent = AceEntityMention.decodeCharseq(extentElement);
					text = fileText.substring(extent.start(), extent.end()+1);
					NodeList arguments = mentionElement.getElementsByTagName("relation_mention_argument");
					for (int j=0; j<arguments.getLength(); j++) {
						Element argument = (Element) arguments.item(j);
						String mentionid = argument.getAttribute("REFID");
						String role = argument.getAttribute("ROLE");
						if (role.equals("Arg-1")) {
							arg1 = acedoc.findEntityMention(mentionid);
						} else if (role.equals("Arg-2")) {
							arg2 = acedoc.findEntityMention(mentionid);
						} else if (AceRelation.timeRoles.contains(role)) {
						// ignore time roles at present
						} else {
							System.err.println ("*** invalid ROLE for relation mention");
						}
					}
				} else {
					// Qi: get ldc extent
					NodeList extents = mentionElement.getElementsByTagName("ldc_extent");
					Element extentElement = (Element) extents.item(0);
					extent = AceEntityMention.decodeCharseq(extentElement);
					text = fileText.substring(extent.start(), extent.end()+1);
					// get arguments (2004 format)
					NodeList arguments = mentionElement.getElementsByTagName("rel_mention_arg");
					for (int j=0; j<arguments.getLength(); j++) {
						Element argument = (Element) arguments.item(j);
						String mentionid = argument.getAttribute(
							AceDocument.ace2004 ? "ENTITYMENTIONID" : "MENTIONID");
						String argnum = argument.getAttribute("ARGNUM");
						if (argnum.equals("1")) {
							arg1 = acedoc.findEntityMention(mentionid);
						} else if (argnum.equals("2")) {
							arg2 = acedoc.findEntityMention(mentionid);
						} else {
							System.err.println ("*** invalid ARGNUM for relation");
						}
					}
				}
		}

		public void write (PrintWriter w) {
			w.print  ("      <relation_mention ID=\"" + id + "\"");
			// LDCLEXICALCONDITION is not scored but is required for validation prior to 2005
			if (!AceDocument.ace2005)
				w.print (" LDCLEXICALCONDITION=\"Formulaic\"");
			w.println(">");
			if(AceDocument.ace2004)
				w.println("      <ldc_extent>");
			else
				w.println("      <extent>");
			AceEntityMention.writeCharseq (w, extent, text);
			if(AceDocument.ace2004)
				w.println("      </ldc_extent>");
			else
				w.println("      </extent>");
			writeMentionArg (w, 1, arg1);
			writeMentionArg (w, 2, arg2);
			w.println("      </relation_mention>");
		}

		private void writeMentionArg (PrintWriter w, int argnum, AceEntityMention arg) {
			if (AceDocument.ace2005) {
				w.println("        <relation_mention_argument REFID=\"" + arg.id +
			          "\" ROLE=\"Arg-" + argnum + "\">");
			} else {
				String keyword = AceDocument.ace2004 ? "ENTITYMENTIONID" : "MENTIONID";
				w.println("        <rel_mention_arg " + keyword + "=\"" + arg.id +
				          "\" ARGNUM=\"" + argnum + "\">");
			}
			w.println("          <extent>");
			w.print  ("            <charseq START=\"" + arg.extent.start() +
			          "\" END=\"" + arg.extent.end() + "\">");
			String arg_text = arg.text;
			arg_text = arg_text.replaceAll("&", "&amp;");
			arg_text = arg_text.replaceAll("<", "&lt;");
			arg_text = arg_text.replaceAll(">", "&gt;");
			arg_text = arg_text.replaceAll("\"", "&quot;");
			arg_text = arg_text.replaceAll("'", "&apos;");
			w.print  (arg_text);
			w.println(            "</charseq>");
			w.println("          </extent>");
			if (AceDocument.ace2005) {
				w.println("        </relation_mention_argument>");
			} else {
				w.println("        </rel_mention_arg>");
			}
		}

		/**
		 *  returns a String representation of the mention, consisting of
		 *  the type and subtype of the relation, and the text of the argument
		 *  mentions.
		 */

		public String toString () {
			return relation.type + ":" + relation.subtype +
			       "(" + arg1.text + ", " + arg2.text + ")";
		}

		@Override
		public AceEventArgumentValue getParent()
		{
			return relation;
		}

		@Override
		public String getType()
		{
			return relation.type;
		}
		
		public String getSubType()
		{
			return relation.subtype;
		}

}
