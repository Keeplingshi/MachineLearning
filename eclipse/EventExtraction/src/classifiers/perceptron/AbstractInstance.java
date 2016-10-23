package classifiers.perceptron;

public class AbstractInstance
{
	// the gold standard assignment
	// if it's a training instance
	public AbstractAssignment target;

	// the active target prefix that created in search
	public AbstractAssignment target_prefix;
}
