package ee.data.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ee.util.Utils;

/**
 * Corpus(A collection of documents)
 */
public class Corpus {
	private String corpus;
	private List<Document> documents;

	/**
	 * Construct corpus from a folder containing all the documents.
	 * 
	 * @param folder
	 *            path of source file
	 */
	public Corpus(String folder) {
		File fd = new File(folder);
		if (!fd.isDirectory()) {
			System.err.println(folder + " is not a folder");
			return;
		}
		documents = new ArrayList<Document>();
		for (String s : fd.list()) {
			try {
				String doc = Utils.readFileFromPath(s);
				Document d = new Document(doc);
				documents.add(d);
			} catch (IOException e) {
				System.err.println("Can't read file: " + s);
			}
		}
	}

	/**
	 * A summary description of the corpus
	 */
	public void summary() {

	}

	public String getCorpus() {
		return corpus;
	}

	public List<Document> getDocuments() {
		return documents;
	}
}
