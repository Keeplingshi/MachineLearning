package ee.data.out;

/**
 * Semantic frames of the event
 */
public class SemanticFrame {
	/**
	 * Default date title of semantic frame
	 */
	private final String DEF_DATE_TITLE = "日期";
	/**
	 * A special tuple used to represent the title of the semantic frame
	 */
	private Tuple sframeTitle;
	/**
	 * The date title of the semantic frame
	 */
	private String dateTitle;

	/**
	 * Construct a tuple, using default date title.
	 * 
	 * @param subjTitle
	 *            Title of the subject
	 * @param predTitle
	 *            Title of the predicate
	 * @param objTitle
	 *            Title of the object
	 */
	public SemanticFrame(String subjTitle, String predTitle, String objTitle) {
		super();
		sframeTitle = new Tuple();
		this.sframeTitle.subj = subjTitle;
		this.sframeTitle.pred = predTitle;
		this.sframeTitle.obj = objTitle;
		this.dateTitle = DEF_DATE_TITLE;
	}

	/**
	 * Construct a tuple
	 * 
	 * @param subjTitle
	 *            Title of the subject
	 * @param predTitle
	 *            Title of the predicate
	 * @param objName
	 *            Title of the object
	 * @param dateName
	 *            Title of the date
	 */
	public SemanticFrame(String subjTitle, String predTitle, String objName,
			String dateName) {
		this(subjTitle, predTitle, objName);
		this.dateTitle = dateName;
	}

	public String toString() {
		return "(" + getSubjTitle() + ", " + getPredTitle() + ", "
				+ getObjTitle() + ")";
	}

	public String getSubjTitle() {
		return this.sframeTitle.subj;
	}

	public String getObjTitle() {
		return this.sframeTitle.obj;
	}

	public String getPredTitle() {
		return this.sframeTitle.pred;
	}

	public String getDateTitle() {
		return dateTitle;
	}
}
