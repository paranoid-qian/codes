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
	
	// pattern, 1 pat per line
	private List<Item> itemsList;
	// pattern information gain value, default 0.0
	private double ig;
	// global support value
	private int support;
	// pattern attribute and value 
	private String pName;
	// item ids
	private String pId;
	
	
	private int coveredU;
	public void incrCoveredU() {
		this.coveredU++;
	}
	
	public Pattern() {
		itemsList = new ArrayList<Item>();
		this.ig = 0;
		this.support = 0;
		this.pName = null;
	}
	
	/**
	 * Return pattern name
	 * @return
	 */
	public String pName() {
		if (this.pName == null) {
			resolve();
		}
		return pName;
	}
	
	public String pId() {
		if (this.pId == null) {
			resolve();
		}
		return pId;
	}
	
	/**
	 * 判断instance是否满足pattern
	 * @param ins
	 * @return
	 */
	public boolean isFit(Instance ins) {
		for (Item entry : this.entrys()) {
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
	
	public void addEntry(Item entry) {
		itemsList.add(entry);
	}

	public List<Item> entrys() {
		return this.itemsList;
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
		StringBuilder id = new StringBuilder("");
		for (Item entry : itemsList) {
			name.append(entry.getAttr() + "=" + entry.getVal() + "&");
			id.append(entry.getId() + " ");
		}
		name.replace(name.length()-1, name.length(), "");
		this.pName = name.toString();
		this.pId = id.toString();
	}

	public int getCoveredU() {
		return coveredU;
	}

	public void setCoveredU(int coveredU) {
		this.coveredU = coveredU;
	}

	
	
}
