package meta.entity;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import meta.util.constants.Constant;


/**
 * Frequent close pattern definition
 * @author paranoid
 *
 */
public class Pattern {
	
	/* pattern, 1 pat per line */
	private List<AttrValEntry> entryList;
	/* pattern information gain value, default 0.0 */
	private double ig;
	/* global support value */
	private int support;
	/* pattern attribute and value */
	private String pAttr;
	
	public Pattern() {
		entryList = new ArrayList<AttrValEntry>();
		this.ig = 0;
		this.support = 0;
		this.pAttr = null;
	}
	
	/**
	 * Return pattern name
	 * @return
	 */
	public String pName() {
		if (this.pAttr == null) {
			resolve();
		}
		return pAttr;
	}
	
	/**
	 * 判断instance是否满足pattern
	 * @param ins
	 * @return
	 */
	public boolean isFit(Instance ins) {
		for (AttrValEntry entry : this.entrys()) {
			int id = AttributeIndexs.get(entry.getAttr());
			String v = ins.stringValue(id);
			if (!v.equals(entry.getVal())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 返回pattern对某个instance的值 (1 or 0)
	 * @param ins
	 * @return
	 */
	public String pValue(Instance ins) {
		return isFit(ins) ? Constant.FIT : Constant.NO_FIT;
	}
	
	public void addEntry(AttrValEntry entry) {
		entryList.add(entry);
	}

	public List<AttrValEntry> entrys() {
		return this.entryList;
	}
	
	public void setIg(double ig) {
		this.ig = ig>0 ? ig : 0;
	}
	
	public double getIg() {
		return this.ig;
	}
	
	public void setSupport(int support) {
		this.support = support;
	}
	
	public int getSupport() {
		return this.support;
	}
	
	// Resolve pattern attribute name
	private void resolve() {
		StringBuilder name = new StringBuilder("");
		for (AttrValEntry entry : entryList) {
			name.append(entry.getAttr() + "=" + entry.getVal() + "&");
		}
		name.replace(name.length()-1, name.length(), "");
		this.pAttr = name.toString();
	}
	
}
