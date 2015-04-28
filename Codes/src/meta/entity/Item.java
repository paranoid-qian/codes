package meta.entity;

import weka.core.Instance;
import meta.util.constants.Constant;

/**
 * attribute-value entry
 * @author paranoid
 *
 */
public class Item {
	
	/* entry id */
	private int id;
	
	/* attribute name */
	private String attr;
	
	/* attribute value */
	private String val;
	
	
	public Item(int id, String attr, String val) {
		this.setId(id);
		this.setAttr(attr);
		this.setVal(val);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return this.attr + Constant.EQUAL + this.val;
	}
	
	public String iValue(Instance ins) {
		String v = ins.stringValue(id);
		if (!v.equals(val)) {
			return Constant.NO_FIT;
		} else {
			return Constant.FIT;
		}
	}
	
}
