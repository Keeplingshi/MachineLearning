package ee.data.in;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ee.util.Utils;

/**
 * Sentence(A collection of words or characters)
 */
public class Sentence {
	private String sentence;
	private List<Word> words;

	/**
	 * Construct a sentence from buffer
	 * 
	 * @param sent
	 *            The sentence
	 */
	public Sentence(String sent) {
		this.sentence = sent;
	}

	/**
	 * Construct a sentence from file
	 * 
	 * @param f
	 *            The file containing sentence
	 * @throws IOException
	 *             Throws if the file path is incorrect
	 */
	public Sentence(File f) throws IOException {
		this(Utils.readFile(f));
	}

	/**
	 * A summary description of the sentence
	 */
	public void summary() {

	}
	
	public String getSentence() {
		return sentence;
	}

	public List<Word> getWords() {
		return words;
	}

	public String toString() {
		return sentence;
	}
}
