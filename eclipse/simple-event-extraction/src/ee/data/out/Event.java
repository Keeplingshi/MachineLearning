package ee.data.out;

import java.util.ArrayList;
import java.util.List;

public class Event {
	/**
	 * Event template
	 */
	private SemanticFrame template;
	/**
	 * Event tuple list
	 */
	private List<Tuple> events;

	public Event(SemanticFrame t, List<Tuple> el) {
		this.template = t;
		this.events = el;
	}

	public SemanticFrame getTemplate() {
		return template;
	}

	public List<Tuple> getTopEvent(int number) {
		return new ArrayList<Tuple>(events).subList(0, number);
	}

	public List<Tuple> getEvents() {
		return events;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(template.toString());
		sb.append("\n");
		for (Tuple t : events) {
			sb.append(t.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
