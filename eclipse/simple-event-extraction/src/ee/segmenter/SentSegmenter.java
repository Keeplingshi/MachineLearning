package ee.segmenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import ICTCLAS.I3S.AC.ICTCLAS50;
import ee.data.in.Sentence;
import ee.util.Encode;
import ee.util.Global;
import ee.util.POS;

public class SentSegmenter {

	private ICTCLAS50 segmenter = new ICTCLAS50();

	/**
	 * Construct a sentence segmenter by using default encode
	 * 
	 * @throws UnsupportedEncodingException
	 *             throws if the encode beyond supports.
	 */
	public SentSegmenter() throws UnsupportedEncodingException {
		if (segmenter.ICTCLAS_Init(Global.PLATFORM.getBytes(Global.DEF_ENCODE
				.toString())) == false) {
			System.err.println("Init Fail!");
			return;
		}
	}

	/**
	 * Import user dictionary
	 * 
	 * @param extra
	 *            path of the user dictionary
	 */
	public void importExtra(String extra) {
		int cs = -1;
		if (Global.DEF_ENCODE == Encode.UTF8)
			cs = 1; // 中科院分词工具的词典编码和待分词文件编码不一样
		segmenter.ICTCLAS_ImportUserDictFile(extra.getBytes(), cs);
	}

	public void close() {
		segmenter.ICTCLAS_Exit();
	}

	/**
	 * Segment sentence contained in a file
	 * 
	 * @param path
	 *            file path
	 * @param fc
	 *            file encode
	 * @param pos
	 *            if using pos
	 * @param dst
	 *            result
	 * @throws FileNotFoundException
	 *             Throws if the path of file is incorrect
	 */
	public void segmentf(String path, Encode fc, POS pos, String dst)
			throws FileNotFoundException {
		if (!new File(path).exists()) {
			throw new FileNotFoundException();
		}
		segmenter.ICTCLAS_FileProcess(path.getBytes(), fc.toInt(), pos.toInt(),
				dst.getBytes());
	}

	/**
	 * Set the POS level
	 * 
	 * @param level
	 *            0 计算所二级标注集, 1 计算所一级标注集,2 北大二级标注集,3 北大一级标注集
	 */
	public void setPOSLevel(int level) {
		segmenter.ICTCLAS_SetPOSmap(level);
	}

	/**
	 * Segment sentence in buffer
	 * 
	 * @param s
	 *            sentence buffer
	 * @param ss
	 *            buffer encode
	 * @param pos
	 *            if using pos
	 * @return StringTokens split by whitespace
	 * @throws UnsupportedEncodingException
	 *             Throws if the encode beyond supports.
	 */
	public String segments(Sentence s, Encode ss, POS pos)
			throws UnsupportedEncodingException {
		byte[] res = segmenter.ICTCLAS_ParagraphProcess(s.getSentence()
				.getBytes(Global.DEF_ENCODE.toString()), ss.toInt(), pos
				.toInt());
		return new String(res, 0, res.length, Global.DEF_ENCODE.toString());
	}

	/**
	 * Segment sentence
	 * 
	 * @param s
	 * @param cs
	 * @param ifPos
	 * @return StringTokens split by whitespace
	 * @throws UnsupportedEncodingException
	 *             Throws if the encode beyond supports.
	 */
	public String[] lsegments(Sentence s, Encode cs, POS pos)
			throws UnsupportedEncodingException {
		byte[] res = segmenter.ICTCLAS_ParagraphProcess(s.getSentence()
				.getBytes(Global.DEF_ENCODE.toString()), cs.toInt(), pos
				.toInt());
		String whole = new String(res, 0, res.length,
				Global.DEF_ENCODE.toString());
		StringTokenizer st = new StringTokenizer(whole);
		String[] sents = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			sents[i] = st.nextToken();
		}
		return sents;
	}

	/**
	 * Test case
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException,
			FileNotFoundException {
		SentSegmenter s = new SentSegmenter();
		s.importExtra("./data/userdict.txt");
		s.segmentf("./data/test.txt", Encode.UTF8, POS.NUSE, "test_result.txt");
		String ss = s.segments(new Sentence("我喜欢打篮球。"), Global.DEF_ENCODE,
				POS.NUSE);
		System.out.println(ss);
		s.close();
		System.out.println("done");
	}
}
