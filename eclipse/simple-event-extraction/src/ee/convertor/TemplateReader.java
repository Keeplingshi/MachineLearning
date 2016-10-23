package ee.convertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ee.data.out.SemanticFrame;

/**
 * Read the title of the template
 */
public class TemplateReader {
	/**
	 * Construct the title of the template from file
	 * 
	 * @param f
	 *            File path of the template title
	 * @return Template
	 * @throws IOException
	 *             throws if the path of the file is incorrect
	 */
	public SemanticFrame reader(String f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(f)));
		String subjName = br.readLine();
		String predName = br.readLine();
		String objName = br.readLine();
		String dateName = null;
		if (br.ready()) {
			dateName = br.readLine();
		}
		SemanticFrame t;
		if (dateName == null)
			t = new SemanticFrame(subjName, predName, objName);
		else
			t = new SemanticFrame(subjName, predName, objName, dateName);
		br.close();
		return t;
	}
}
