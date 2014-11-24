package meta.entity;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;

import meta.util.constants.Constant;


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
	
	/* pattern item ids */
	private String pItemIds = null;
	
	/* pattern information gain value, default 0.0 */
	private double ig = 0;
	
	/* global support value */
	private int support;
	
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
	
	public String pItems() {
		if (this.pItemIds == null) {
			resolve();
		}
		return pItemIds;
	}
	
	
	public String pValue(Instance ins) {
		for (AttrValEntry entry : entryList) {
			// if one entry doesn't fit, return NO_FIT
			if (!ins.stringValue(AttributeIndexs.get(entry.getAttr())).equals(entry.getVal())) {
				return Constant.NO_FIT;
			}
		}
		return Constant.FIT;
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
	
	/*
	 * resolve pattern attr name and value
	 */
	private void resolve() {
		StringBuilder name = new StringBuilder("");
		StringBuilder items = new StringBuilder();
		for (AttrValEntry entry : entryList) {
			name.append(entry.getAttr() + "=" + entry.getVal() + "&");
			items.append(entry.getId() + " ");
		}
		name.replace(name.length()-1, name.length(), "");
		items.replace(items.length()-1, items.length(), "");
		this.pAttr = name.toString();
		this.pItemIds = items.toString();
	}
}
