package ee.model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import ee.convertor.Pair;
import ee.data.in.Document;
import ee.data.in.Sentence;
import ee.data.out.Event;
import ee.data.out.SemanticFrame;
import ee.data.out.Tuple;
import ee.segmenter.DocSegmenter;
import ee.segmenter.SentSegmenter;
import ee.util.Global;
import ee.util.POS;

public class TupleModel {

	/**
	 * User dictionary
	 */
	private List<String> interest;
	/**
	 * Semantic frame
	 */
	private SemanticFrame sframe;
	/**
	 * Event trigger
	 */
	private List<String> trigger;
	/**
	 * Sentence segmenter
	 */
	private SentSegmenter segmenter;

	/**
	 * Trained Language model path
	 */
	private static final String lanModel = "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz";
	/**
	 * Lexical parser
	 */
	private LexicalizedParser lp;

	/**
	 * Model status
	 */
	public static String status;

	/**
	 * Construct a tuple model, using user dictionary and trigger to extract
	 * event from corpus
	 * 
	 * @param interest
	 *            user dictionary
	 * @param trigger
	 *            trigger words
	 * @param sframe
	 *            semantic frames
	 * @throws UnsupportedEncodingException
	 */
	public TupleModel(List<String> interest, List<String> trigger,
			SemanticFrame sframe) throws UnsupportedEncodingException {
		loadModel();
		this.segmenter = new SentSegmenter();
		this.interest = interest;
		this.sframe = sframe;
		this.trigger = trigger;
		extInterest();
	}

	/**
	 * Configure the segmenter
	 * 
	 * @param extra
	 *            extra user's interests. e.g.: NE
	 */
	public void configSegmenter(String extra) {
		this.segmenter.importExtra(extra);
	}

	/**
	 * Load parser
	 */
	private void loadModel() {
		lp = LexicalizedParser.loadModel(lanModel);
	}

	/**
	 * Get the name of the denpendency node
	 * 
	 * @param tgn
	 *            TreeGraphNode
	 * @return Name of the node
	 */
	private String getNodeName(TreeGraphNode tgn) {
		Pair p;
		try {
			p = cut(tgn.toString());
			return p.f;
		} catch (Exception e) {
			System.err.println("Can't get the name of the node" + tgn);
			return "";
		}

	}

	/**
	 * Cut a string to a pair
	 * 
	 * @param s
	 *            String to be cut
	 * @return Pair
	 * @throws Exception
	 *             throws if the number of sub-parts of the source isn't 2.
	 */
	private Pair cut(String s) throws Exception {
		StringTokenizer st = new StringTokenizer(s, "-");
		if (st.countTokens() != 2)
			throw new Exception("Can't cut: The number of subpart isn't 2");
		Pair p = new Pair();
		p.f = st.nextToken();
		p.s = st.nextToken();
		return p;
	}

	/**
	 * Extend the user dictionary
	 * 
	 * @return extended user dictionary
	 */
	private List<String> extInterest() {
		for (String s : trigger) {
			interest.add(s);
		}
		return interest;
	}

	/**
	 * To judge if the content is interested according to the user dictionary
	 * 
	 * @return true if the content is interested
	 */
	private boolean interested(String[] sent) {
		for (String s : sent) {
			if (interest.contains(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extract events from sentences
	 * 
	 * @param sl
	 *            A list containing sentences.
	 * @return Event object
	 * @throws UnsupportedEncodingException
	 *             throws if the Encoding beyond supports.
	 */
	public Event extractEvent(List<Sentence> sl)
			throws UnsupportedEncodingException {
		return new Event(sframe, analysisl(sl));
	}

	/**
	 * Analyze events of the sentences
	 * 
	 * @param sl
	 *            A list containing sentences
	 * @return A list containing tuples describing events
	 * @throws UnsupportedEncodingException
	 */
	public List<Tuple> analysisl(List<Sentence> sl)
			throws UnsupportedEncodingException {
		List<Tuple> tl = new ArrayList<Tuple>();
		for (Sentence s : sl) {
			Tuple t = analysis(s);
			if (t != null)
				tl.add(t);
		}
		return tl;
	}

	/**
	 * Analyze event from a sentence
	 * 
	 * @param s
	 *            A sentence
	 * @return Event tuple, ** a sentence containing a event now.
	 * @throws UnsupportedEncodingException
	 */
	public Tuple analysis(Sentence s) throws UnsupportedEncodingException {
		// segment
		SentSegmenter seg = new SentSegmenter();
		String[] sent = seg.lsegments(s, Global.DEF_ENCODE, POS.NUSE);

		// parse
		List<HasWord> sentence = new ArrayList<HasWord>();
		for (String word : sent) {
			sentence.add(new Word(word));
		}

		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		Tree parse = lp.apply(sentence);
		// parse.pennPrint();
		// System.out.println(parse.taggedYield());

		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);

		// extract
		if (interested(sent)) {
			try {
				return extract(tdl);
			} catch (Exception e) {
				System.err.println("Event extraction error.");
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Extract events from a list containing dependency relation of sentences
	 * 
	 * @param tdcl
	 *            dependency of a sentence
	 * @return a list containing tuples describing events
	 * @throws Exception
	 *             throws if
	 */
	public List<Tuple> extractl(List<Collection<TypedDependency>> tdcl)
			throws Exception {
		List<Tuple> l = new ArrayList<Tuple>();
		for (Collection<TypedDependency> tdc : tdcl) {
			Tuple t = extract(tdc);
			if (t != null)
				l.add(t);
		}
		return l;
	}

	/**
	 * Extract a tuple from the dependency of a sentence
	 * 
	 * @param tdc
	 *            dependency of a sentence
	 * @return A tuple
	 */
	public Tuple extract(Collection<TypedDependency> tdc) {
		Tuple t = new Tuple();
		for (Iterator<TypedDependency> iter = tdc.iterator(); iter.hasNext();) {
			TypedDependency td = iter.next();

			if (td.reln().toString().equals("dobj")) {
				t.pred = getNodeName(td.gov());
				t.obj = getNodeName(td.dep());
			}
			if (td.reln().toString().equals("nsubj")) {
				t.subj = getNodeName(td.dep());
			}
			// System.out.println(td);
		}

		if (t.valid())
			return t;
		else
			return null;
	}

	/**
	 * Test case
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<String> interest = new ArrayList<String>();
		interest.add("北京");
		List<String> trigger = new ArrayList<String>();
		trigger.add("回到");
		SemanticFrame t = new SemanticFrame("主语", "谓语", "宾语");

		TupleModel m = new TupleModel(interest, trigger, t);
		// User defined extra phase, including NE ...
		m.configSegmenter(Global.USER_EXTRA);

		Document doc = new Document("随后温总理就离开了舟曲县城，预计温总理今天"
				+ "下午就回到北京。以上就是今天上午的最新动态。");

		List<Sentence> sentences = DocSegmenter.segment(doc);

		System.out.println("原文:");
		System.out.println(sentences);
		System.out.println("事件:");
		Event e = m.extractEvent(sentences);
		System.out.println(e.getTemplate());
		System.out.println(e.getEvents());
	}
}
