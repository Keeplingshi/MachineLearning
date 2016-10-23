package ee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class Utils {

	public static String readFile(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuffer sb = new StringBuffer();
		while (br.ready()) {
			sb.append(br.readLine());
		}
		br.close();
		return sb.toString();
	}

	public static String readFileFromPath(String path) throws IOException {
		return readFile(new File(path));
	}

}
