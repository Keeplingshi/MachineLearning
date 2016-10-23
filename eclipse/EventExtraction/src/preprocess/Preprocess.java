package preprocess;

import java.io.*;
import java.util.*;

/**
 * 
 * @author chenyadong
 */
public class Preprocess
{

	
	/**
	 * generator file list. format: bc/timexnorm/filename
	 */
	public static void filelist() throws Exception
	{
		File aceDir = new File("./data/ACE2005-TrainingData-V5.0/English");
		File[] dirs = aceDir.listFiles();
		StringBuilder sb = new StringBuilder();
		for (File dir : dirs)
		{
			String dirName = dir.getName();
			String fileListName = aceDir + "/" +dirName + "/FileList";
			BufferedReader br = new BufferedReader(new FileReader(fileListName));
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				if (line.startsWith("# DOCID"))
				{
					continue;
				}
				String[] strs = line.split("\t");
				if (strs.length == 3) 
				{
					sb.append(dirName + "/timex2norm/" +strs[0] + "\n");
				}
			}
			br.close();
		}
		
		FileWriter fw = new FileWriter("./tmp/filelist.txt");
		fw.write(sb.toString());
		fw.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		filelist();
		System.exit(0);
	}

}
