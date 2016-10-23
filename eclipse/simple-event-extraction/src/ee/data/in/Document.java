package ee.data.in;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ee.util.Utils;

/**
 * Document(A collection of sentences)
 */
public class Document {
	private String document;
	private List<Sentence> sentences;

	/**
	 * Construct a document from a buffer
	 * 
	 * @param doc
	 *            document content
	 */
	public Document(String doc) {
		this.document = doc;
	}

	/**
	 * Construct a document from a file
	 * 
	 * @param f
	 *            document file
	 * @throws IOException
	 *             Throws if the path of the file is incorrect
	 */
	public Document(File f) throws IOException {
		this(Utils.readFile(f));
	}

	/**
	 * A summary description of the document
	 */
	public void summary() {

	}

	public String getDocument() {
		return document;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

}
