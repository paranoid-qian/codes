package meta.attributes;

public class EdgeAttrPair {
	
	public static final String HYPHEN = "_";
	
	
	private String attr;
	
	private String val1;
	private String val2;
	
	
	public EdgeAttrPair(String attr, String val1, String val2) {
		setAttr(attr);
		setVal1(val1);
		setVal2(val2);
	}
	
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getVal1() {
		return val1;
	}
	public void setVal1(String val1) {
		this.val1 = val1;
	}
	public String getVal2() {
		return val2;
	}
	public void setVal2(String val2) {
		this.val2 = val2;
	}
	
	
	public static String key(String val1, String val2) {
		return val1 + HYPHEN + val2;
	}
	
	
}
