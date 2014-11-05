package meta.pattern.globalEval;

import java.util.Map;

import meta.entity.AttrValEntry;
import meta.util.loader.InstanceLoader;
import meta.util.loader.ItemLoader;
import weka.core.Instance;
import weka.core.Instances;

public class GlobalFeatureEval {
	
	private static Instances inss;
	private static Map<Integer, Integer> featSupportMap; 
	
	/**
	 * 计算单独每个item的支持度情况
	 */
	public static void genGlobalFeatureSupport() throws Exception {
		inss = InstanceLoader.loadInstances();
		Map<String, Map<String, AttrValEntry>> itemMap = ItemLoader.loadItems(inss);
		
		for (int i = 0; i < inss.numInstances(); i++) {
			Instance ins = inss.instance(i);
			for (int j = 0; j < inss.numAttributes()-1; j++) {
				int itemId = itemMap.get(ins.attribute(j).name()).get(ins.stringValue(j)).getId();
				if (featSupportMap.containsKey(itemId)) {
					featSupportMap.put(itemId, featSupportMap.get(itemId)+1);
				} else {
					featSupportMap.put(itemId, 1);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			GlobalFeatureEval.genGlobalFeatureSupport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
