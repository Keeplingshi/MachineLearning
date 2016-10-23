package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameNet
{
	/**
	 * only kept the frames that are frequent in the training data
	 * (frequecy >= 10)
	 */
	static List<String> WHITE_LIST = Arrays.asList(new String[]{
		"Abandonment",
		"Appeal",
		"Arranging",
		"Arrest",
		"Arriving",
		"Assemble",
		"Attack",
		"Becoming",
		"Becoming_a_member",
		"Behind_the_scenes",
		"Being_named",
		"Birth",
		"Board_vehicle",
		"Body_movement",
		"Bringing",
		"Causation",
		"Cause_change_of_position_on_a_scale",
		"Cause_harm",
		"Cause_impact",
		"Cause_motion",
		"Cause_to_end",
		"Change_of_leadership",
		"Change_position_on_a_scale",
		"Choosing",
		"Come_together",
		"Commerce_collect",
		"Commerce_pay",
		"Commerce_sell",
		"Compatibility",
		"Conquering",
		"Contacting",
		"Death",
		"Departing",
		"Destroying",
		"Disembarking",
		"Experience_bodily_harm",
		"Experiencer_obj",
		"Firing",
		"Forming_relationships",
		"Getting",
		"Giving",
		"Grasp",
		"Hit_or_miss",
		"Hit_target",
		"Impact",
		"Imposing_obligation",
		"Ingest_substance",
		"Intentionally_create",
		"Invading",
		"Judgment_communication",
		"Killing",
		"Leadership",
		"Locative_relation",
		"Make_acquaintance",
		"Meet_specifications",
		"Meet_with_response",
		"Motion",
		"Notification_of_charges",
		"Offenses",
		"Operate_vehicle",
		"Part_orientational",
		"Path_shape",
		"Quarreling",
		"Quitting",
		"Range",
		"Rape",
		"Reasoning",
		"Relational_natural_features",
		"Removing",
		"Request",
		"Response",
		"Ride_vehicle",
		"Self_motion",
		"Sentencing",
		"Shoot_projectiles",
		"Social_event",
		"Sounds",
		"Statement",
		"Terrorism",
		"Text",
		"Travel",
		"Traversing",
		"Trial",
		"Use_firearm",
		"Verdict",
		"Visiting",
	});
	
	
	static private FrameNet frames = null;
	
	static public FrameNet getSingleton()
	{
		if(frames == null)
		{
			try
			{
				File dict_path = new File("data/frameDicts"); 			
				frames = new FrameNet(dict_path);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
		}
		return frames;
	}
	
	// lemma#pos --> frames
	Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public FrameNet(File dict_path) throws FileNotFoundException,IOException 
	{
		initializeDict(dict_path);
	}

	
	/**
	 * read the dictionary to memory data structure
	 * @param dictFile
	 */
	protected void initializeDict(File dictFile) 
	{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dictFile));
		
			String line = "";
			while((line = reader.readLine()) != null)
			{
				// convert tokens into lowercase
				line = line.trim();
				String[] fields = line.split("\\s");
				if(fields.length < 3)
				{
					continue;
				}
				String frameName = fields[0];
				String lemma = fields[1];
				String pos = fields[2];
				String key = lemma + "#" + pos;
				List<String> names = map.get(key);
				if(WHITE_LIST.contains(frameName))
				{
					if(names == null)
					{
						names = new ArrayList<String>();
						map.put(key, names);
					}
					names.add(frameName);
				}
			}
			reader.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public List<String> getFrameIDs(String lemma, String pos)
	{
		pos = "" + pos.charAt(0);
		String key = lemma + "#" + pos;
		return map.get(key);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		File dict_path = new File("data/frameDicts"); 
		
		FrameNet dicts = new FrameNet(dict_path);
		System.out.println(dicts.getFrameIDs("get", "V"));
		System.out.println(dicts.getFrameIDs("transport", "V"));
		System.out.println(dicts.getFrameIDs("stab", "V"));
		System.out.println(dicts.getFrameIDs("infiltrate", "V"));
		System.out.println(dicts.getFrameIDs("cart", "V"));
		System.out.println(dicts.getFrameIDs("torture", "V"));
	}
}
