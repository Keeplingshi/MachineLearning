package ee.util;

import java.util.Properties;

public abstract class Global {
	/**
	 * Segment tools' win32 path
	 */
	public static final String WIN32 = "./src/ee/segmenter/win32/";
	/**
	 * Segment tools' win64 path
	 */
	public static final String WIN64 = "./src/ee/segmenter/win64/";
	/**
	 * User defined extra information
	 */
	public static final String USER_EXTRA = "./data/userdict.txt";
	/**
	 * System default encode
	 */
	public static final Encode DEF_ENCODE = Encode.UTF8;

	/**
	 * User's plat form
	 */
	public static final String PLATFORM;

	static {
		Properties pros = System.getProperties();
		String arch = pros.get("os.arch").toString();
		if (arch.equals("x86"))
			PLATFORM = WIN32;
		else if (arch.equals("x64"))
			PLATFORM = WIN64;
		else
			PLATFORM = "NOT_SUPPORT";
	}
}
