package arena;

import java.util.List;

public class ValueGetter {

	private String value;
	private List<String> ls;

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setLValue(List<String> ls) {
		this.ls = ls;
	}

	public List<String> getLValue() {
		return ls;
	}

}