package commons;

public class Alphabets implements java.io.Serializable
{
	private static final long serialVersionUID = 5986378682696423133L;

	// the alphabet of node labels (trigger labels)
	public Alphabet nodeTargetAlphabet = new Alphabet();	
	
	// the alphabet of the label for each edge (trigger-->argument link), shared by the whole application
	public Alphabet edgeTargetAlphabet = new Alphabet();
	
	// the alphabet of features, shared by the whole application
	public Alphabet featureAlphabet = new Alphabet();
	
	// only keep the entity label alphabet
	public Alphabet entityLabelAlphabet = new Alphabet();
	
	// only keep the argumentRole alphabet
	public Alphabet triggerLabelAlphabet = new Alphabet();
	
	// only keep the trigger label alphabet
	public Alphabet argumentRoleAlphabet = new Alphabet();
	
	// only keep the relation type alphabet
	public Alphabet relationTypeAlphabet = new Alphabet();
	
	public Alphabets()
	{
		;
	}
}
