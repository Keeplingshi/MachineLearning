package ee.util;

public enum POS {
	NUSE(0), USE(1);

	private int value;

	POS(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}
}
