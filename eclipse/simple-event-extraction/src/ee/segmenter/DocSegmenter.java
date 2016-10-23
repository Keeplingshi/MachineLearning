package ee.segmenter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ee.data.in.Document;
import ee.data.in.Sentence;

/**
 * Segment document
 */
public class DocSegmenter {

	public static List<Sentence> segment(Document document) {
		return segment(document, "。");
	}

	public static List<Sentence> segment(Document document, String del) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		Pattern p = Pattern.compile(".*?" + del);
		Matcher m = p.matcher(document.getDocument());
		while (m.find()) {
			Sentence s = new Sentence(m.group());
			sentences.add(s);
		}
		return sentences;
	}

	/**
	 * Test case
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Document doc = new Document("随后温总理就离开了舟曲县城，预计温总理今天下午"
				+ "就回到北京。以上就是今天上午的最新动态。");
		List<Sentence> sentences = DocSegmenter.segment(doc);

		System.out.println(sentences);
	}
}
