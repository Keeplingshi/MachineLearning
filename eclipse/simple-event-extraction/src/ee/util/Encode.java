package ee.util;

public enum Encode {
	UNKNOWN(0), ASCII(1), GB2312(2), GBK(2), GB10380(2), UTF8(3), BIG5(4);

	private int value;

	Encode(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

}
