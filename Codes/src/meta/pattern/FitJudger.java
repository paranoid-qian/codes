package meta.pattern;

import meta.entity.AttrValEntry;
import meta.entity.AttributeIndexs;
import meta.entity.Pattern;
import weka.core.Instance;

public class FitJudger {
	
	/**
	 * 判断一个instance是否满足一个pattern
	 * @param ins
	 * @param pattern
	 * @return
	 */
	public static boolean isFit(Instance ins, Pattern pattern) {
		for (AttrValEntry entry : pattern.entrys()) {
			int id = AttributeIndexs.get(entry.getAttr());
			/*if (id < 0) {
				System.out.println("bug");
			}*/
			String v = ins.stringValue(id);
			if (!v.equals(entry.getVal())) {
				return false;
			}
		}
		return true;
	}
	
}
