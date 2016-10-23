package ee.data.out;

public class Tuple {
	/**
	 * subject of a sentence
	 */
	public String subj;
	/**
	 * object of a sentence
	 */
	public String obj;
	/**
	 * predicate of a sentence
	 */
	public String pred;
	public Date date;

	public Tuple() {
		super();
	}

	public Tuple(String subj, String pred, String obj) {
		this();
		this.subj = subj;
		this.pred = pred;
		this.obj = obj;
	}

	public Tuple(String subj, String pred, String obj, Date date) {
		this(subj, pred, obj);
		this.date = date;
	}

	/**
	 * If the tuple if legal
	 * 
	 * @return A tuple is legal if the predicate of the sentence exists and at
	 *         least one senmatic roles of the predicate(i.e. object and
	 *         subject) exists
	 */
	public boolean valid() {
		return pred != null && (subj != null || obj != null);
	}

	public String toString() {
		return "(" + subj + ", " + pred + ", " + obj + ")";
	}

}
