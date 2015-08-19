package meta.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

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
	// pattern attribute and value 
	private String pName;
	// item ids
	private String pId;
	
	
	/* 
	 * For HongChen's method
	 */
	private double relevance;	// using pattern ig as the value
	private double gain;		// pattern gain
	
	
	// for one-class analysis, can not be changed
	private double ig;
	private double chi;
	
	// global support value
	private int gSupport;
	
	//public double chi_one_class;
	public TreeMap<Double, Double> chi4PerClass = new TreeMap<>(new Comparator<Double>() {
		@Override
		public int compare(Double o1, Double o2) {
			return Double.compare(o1, o2);
		}
	});
	
	// per class support
	public TreeMap<Double, Double> supp4PerClass = new TreeMap<>(new Comparator<Double>() {
		@Override
		public int compare(Double o1, Double o2) {
			return Double.compare(o1, o2);
		}
	});
	
	private Set<Instance> coveredSet;
	
	private int coveredU;
	public void incrCoveredU() {
		this.coveredU++;
	}
	
	public Pattern() {
		itemsList = new ArrayList<Item>();
		this.relevance = 0;
		this.pName = null;
		this.coveredSet = new HashSet<>();
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
	
	public void setRevelance(double revelance) {
		this.relevance = revelance;
	}
	
	public double getRevelance() {
		return this.relevance;
	}
	
	
	// Resolve pattern attribute name
	private void resolve() {
		StringBuilder name = new StringBuilder();
		StringBuilder id = new StringBuilder();
		for (Item item : itemsList) {
			name.append(item.toString() + Constant.AND);
			id.append(item.getId() + Constant.SPACE);
		}
		name.replace(name.length()-1, name.length(), Constant.NULL);
		this.pName = name.toString();
		this.pId = id.toString();
	}

	public int getCoveredU() {
		return coveredU;
	}

	public void setCoveredU(int coveredU) {
		this.coveredU = coveredU;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public Set<Instance> getCoveredSet() {
		return coveredSet;
	}

	public void setCoveredSet(Set<Instance> coveredSet) {
		this.coveredSet = coveredSet;
	}

	public double getIg() {
		return ig;
	}

	public void setIg(double ig) {
		this.ig = ig;
	}

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) {
		this.chi = chi;
	}

	public int getgSupport() {
		return gSupport;
	}

	public void setgSupport(int gSupport) {
		this.gSupport = gSupport;
	}

	
	
}
