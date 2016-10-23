package event.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Controller;
import util.Span;
import util.TypeConstraints;
import util.graph.DependencyGraph;

import commons.Alphabets;
import commons.Sentence;
import commons.Sentence.Sent_Attribute;

import classifiers.perceptron.AbstractInstance;

import ace.acetypes.*;

import event.perceptron.featureGenerator.NodeFeatureGenerator;

/**
 * This is a basic object of the learning algrithm
 * it represents a sentence, including text features target assignments, beam in searching etc.
 * 
 * For the text features, it should contain feature vectors for each token in the sentence
 * and the original rich representation of the sentence, e.g. dependency parse tree etc.
 * 
 * For the (target) assignment, it should encode two types of assignment:
 * (1) label assignment for each token: refers to the event trigger classification 
 * (2) assignment for any sub-structure of the sentence, e.g. one assignment indicats that 
 * the second token is argument of the first trigger
 * 
 * Given the first type of assigment, it should be able to get features for the learning algorithm, e.g. token feature vector X assignment
 * similarly, given the second type of assignment, it should be able to get features like: text features assoicated with tokens X assignment
 * Finally, on top of the assignment, it should be able to get arbitrary features, e.g. count how many "triggers" accur in this sentence
 * @author che
 *
 */
public class SentenceInstance extends AbstractInstance
{
	public boolean learnable = false;

	public Alphabets alphabets;

	// the settings of the whole perceptron
	public Controller controller;

	// the text of the original doc
	public String allText;

	public String docID;

	Sentence sent;

	/**
	 * the list of argument candidates (values/entities/timex)
	 */
	public List<AceMention> eventArgCandidates = new ArrayList<AceMention>();

	/**
	 * the list of event mentions 
	 */
	public List<AceEventMention> eventMentions;

	/**
	 * a sequence of token, each token is a vector of features
	 * this is useful for the beam search 
	 */
	Map<InstanceAnnotations, Object> textFeaturesMap = new HashMap<InstanceAnnotations, Object>();

	static public enum InstanceAnnotations
	{
		Token_FEATURE_MAPs, // list->map<key,value> token feature maps, each map contains basic text features for a token
		DepGraph, // dependency: Collection<TypedDependency> or other kind of data structure
		TOKEN_SPANS, // List<Span>: the spans of each token in this sent
		POSTAGS, // POS tags
		NodeTextFeatureVectors, // node feature Vectors
		EdgeTextFeatureVectors, // edge feature Vectors
		ParseTree // parse tree
	}

	public Sentence getSent()
	{
		return this.sent;
	}

	public Object get(InstanceAnnotations key)
	{
		return textFeaturesMap.get(key);
	}

	public Span[] getTokenSpans()
	{
		return (Span[]) textFeaturesMap.get(InstanceAnnotations.TOKEN_SPANS);
	}

	public String[] getPosTags()
	{
		return (String[]) textFeaturesMap.get(InstanceAnnotations.POSTAGS);
	}

	public List<Map<Class<?>, Object>> getTokenFeatureMaps()
	{
		return (List<Map<Class<?>, Object>>) textFeaturesMap
				.get(InstanceAnnotations.Token_FEATURE_MAPs);
	}

	public SentenceInstance(Alphabets alphabets, Controller controller,
			boolean learnable)
	{
		this.alphabets = alphabets;
		this.controller = controller;
		this.learnable = learnable;
	}

	/**
	 * use sentence instance to initialize the training instance
	 * the SentenceInstance object can also be initialized by a file
	 * @param sent
	 */
	public SentenceInstance(Sentence sent, Alphabets alphabets,
			Controller controller, boolean learnable)
	{
		this(alphabets, controller, learnable);

		// set the text of the doc
		this.allText = sent.doc.allText;
		this.docID = sent.doc.docID;
		this.sent = sent;

		// fill in entity information
		this.eventArgCandidates.addAll(sent.entityMentions);
		this.eventArgCandidates.addAll(sent.valueMentions);
		this.eventArgCandidates.addAll(sent.timexMentions);

		// sort event Arg candidates by order of offsets
		Collections.sort(this.eventArgCandidates, new Comparator<AceMention>()
		{
			@Override
			public int compare(AceMention arg0, AceMention arg1)
			{
				int begin0 = arg0.extent.start();
				int begin1 = arg1.extent.start();
				if (arg0 instanceof AceEntityMention)
				{
					begin0 = ((AceEntityMention) arg0).head.start();
				}
				if (arg1 instanceof AceEntityMention)
				{
					begin1 = ((AceEntityMention) arg1).head.start();
				}
				if (begin0 > begin1)
				{
					return 1;
				}
				else if (begin0 == begin1)
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}
		});

		// fill in token text feature maps
		this.textFeaturesMap.put(InstanceAnnotations.Token_FEATURE_MAPs,
				sent.get(Sent_Attribute.Token_FEATURE_MAPs));

		// fill in Annotations map with dependency paths, later we can even fill in parse tree etc.
		DependencyGraph graph = (DependencyGraph) sent
				.get(Sent_Attribute.DepGraph);
		this.textFeaturesMap.put(InstanceAnnotations.DepGraph, graph);

		// fill in parse tree
		this.textFeaturesMap.put(InstanceAnnotations.ParseTree,
				sent.get(Sent_Attribute.ParseTree));

		// fill in tokens and pos tags
		this.textFeaturesMap.put(InstanceAnnotations.TOKEN_SPANS,
				sent.get(Sent_Attribute.TOKEN_SPANS));
		this.textFeaturesMap.put(InstanceAnnotations.POSTAGS,
				sent.get(Sent_Attribute.POSTAGS));

		// get node text feature vectors
		List<List<String>> tokenFeatVectors = NodeFeatureGenerator
				.get_node_text_features(this);
		this.textFeaturesMap.put(InstanceAnnotations.NodeTextFeatureVectors,
				tokenFeatVectors);

		// get edge text feature vectors, this vectors is built up in the lasy fashion, when it's needed, it's filled
		List<List<List<String>>> edgeFeatVectors = new ArrayList<List<List<String>>>();
		for (int i = 0; i < size(); i++)
		{
			List<List<String>> featuresForNode = new ArrayList<List<String>>();
			edgeFeatVectors.add(featuresForNode);
			for (int j = 0; j < eventArgCandidates.size(); j++)
			{
				featuresForNode.add(null);
			}
		}
		this.textFeaturesMap.put(InstanceAnnotations.EdgeTextFeatureVectors,
				edgeFeatVectors);

		// add event ground-truth
		eventMentions = new ArrayList<AceEventMention>();
		eventMentions.addAll(sent.eventMentions);

		// add target as gold-standard assignment
		this.target = new SentenceAssignment(this);
	}

	/**
	 * the size of the sentence
	 * @return
	 */
	public int size()
	{
		return this.getTokenSpans().length;
	}

	/**
	 * given a SentenceAssignment, convert the results as List of AceEventMentions
	 * @param assn
	 * @return
	 */
	public List<AceEvent> getEvents(SentenceAssignment assn, String id,
			String fileText)
	{
		List<AceEvent> ret = new ArrayList<AceEvent>();
		for (int i = 0; i < assn.nodeAssignment.size(); i++)
		{
			Integer trigger_label = assn.nodeAssignment.get(i);
			String label = (String) this.alphabets.nodeTargetAlphabet
					.lookupObject(trigger_label);
			if (label != null
					&& !label.equals(SentenceAssignment.Default_Trigger_Label))
			{
				// only put event subtype and id
				AceEvent event = new AceEvent(id,
						TypeConstraints.eventTypeMap.get(label), label);

				// not NON
				Span trigger_span = this.getTokenSpans()[i];
				String mention_id = id + "-1";
				AceEventMention mention = new AceEventMention(mention_id,
						trigger_span, fileText, null);

				// set extent of the event mention
				Span[] tokenSpans = (Span[]) this
						.get(InstanceAnnotations.TOKEN_SPANS);
				int extent_start = tokenSpans[0].start();
				int extent_end = tokenSpans[tokenSpans.length - 1].end();
				Span extent = new Span(extent_start, extent_end);
				mention.extent = extent;
				mention.text = extent.getCoveredText(fileText);

				// find all arguments
				Map<Integer, Integer> edgeMap = assn.edgeAssignment.get(i);
				if (edgeMap != null)
				{
					for (Integer arg_index : edgeMap.keySet())
					{
						Integer role_index = edgeMap.get(arg_index);
						String role = (String) this.alphabets.edgeTargetAlphabet
								.lookupObject(role_index);
						if (role != null
								&& !role.equals(SentenceAssignment.Default_Argument_Label))
						{
							AceEventMentionArgument argument = new AceEventMentionArgument(
									this.eventArgCandidates.get(arg_index),
									role, mention);
							mention.addArgument(argument);
						}
					}
				}
				event.addMention(mention);
				ret.add(event);
			}
		}

		return ret;
	}

	/**
	 * check if the assignment is correct up to current assn.getState()
	 * @param assn
	 * @return
	 */
	public boolean violateGoldStandard(SentenceAssignment assn)
	{
		// if there isn't "target" in this, that means this is not for learning
		if (target == null)
		{
			return false;
		}
		return !assn
				.equals((SentenceAssignment) this.target, this.target.state);
	}

	/**
	 * compare a set of assignments with gold standard 
	 * @param beam
	 * @return true if violation false if not violation 
	 */
	public boolean violateGoldStandard(List<SentenceAssignment> beam)
	{
		for (SentenceAssignment assn : beam)
		{
			if (assn.equals((SentenceAssignment) this.target, assn.state))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the assignment is correct up to current assn.getState(), 
	 * but only consider k-th argument labeling (labeling for k-th entity)
	 * @param beam
	 * @param argNum: the number of entity
	 * @return
	 */
	public boolean violateGoldStandard(List<SentenceAssignment> beam, int argNum)
	{
		for (SentenceAssignment assn : beam)
		{
			if (assn.equals((SentenceAssignment) this.target, assn.state,
					argNum))
			{
				return false;
			}
		}
		return true;
	}

}
