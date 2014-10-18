package meta.entity;

import java.util.ArrayList;
import java.util.List;


/**
 * frequent close pattern definition
 * @author paranoid
 *
 */
public class Pattern {
	
	/* pattern, 1 pat per line */
	private List<AttrValEntry> entryList;
	
	/* pattern attr and value */
	private String pAttr = null;
	private String pVal = null;
	
	
	/* pattern information gain value, default 0.0 */
	private double ig = 0;
	
	
	public Pattern() {
		entryList = new ArrayList<AttrValEntry>();
	}
	
	public void addEntry(AttrValEntry entry) {
		entryList.add(entry);
	}

	public List<AttrValEntry> entrys() {
		return this.entryList;
	}
	
	public String pName() {
		if (this.pAttr == null) {
			resolve();
		}
		return pAttr;
	}
	
	public String pValue() {
		if (this.pVal == null) {
			resolve();
		}
		return pVal;
	}
	
	
	public void setIg(double ig) {
		this.ig = ig>0 ? ig : 0;
	}
	
	public double getIg() {
		return this.ig;
	}
	
	/*
	 * resolve pattern attr name and value
	 */
	private void resolve() {
		StringBuilder name = new StringBuilder("p:");
		StringBuilder value = new StringBuilder();
		for (AttrValEntry entry : entryList) {
			name.append(entry.getAttr() + "+");
			value.append(entry.getVal() + "+");
		}
		name.replace(name.length()-1, name.length(), "");
		value.replace(value.length()-1, value.length(), "");
		
		this.pAttr = name.toString();
		this.pVal = value.toString();
	}
}
