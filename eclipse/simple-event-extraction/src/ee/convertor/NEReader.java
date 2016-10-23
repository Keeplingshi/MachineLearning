package ee.convertor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ee.util.Utils;

/**
 * 获取命名实体
 */
public class NEReader {

	/**
	 * Read Named Entity(NE) list from file
	 * 
	 * @param f
	 *            The path of the file
	 * @return a list containing NE
	 * @throws IOException
	 *             throws if the file path is incorrect
	 */
	public List<String> read(String f) throws IOException {
		String content = Utils.readFileFromPath(f);
		StringTokenizer st = new StringTokenizer(content, "\n");
		List<String> nel = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			nel.add(st.nextToken());
		}

		return nel;
	}

}
