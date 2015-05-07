package meta.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



import java.util.Map;
import java.util.Set;

import meta.entity.Pattern;
import meta.entity.PuPattern;
import weka.core.Instance;
import weka.core.Instances;

public class IgCalculator {
	
	public static void cal(Instances D, List<? extends Pattern> patterns) {
		
		double info = info(D);
		for (Pattern pattern : patterns) {
			double infoOfPattern = infoOfPattern(D, pattern);
			double ig = info - infoOfPattern;
			pattern.setIg(ig);
			if (ig < 0) {
				pattern.setIg(0);
			}
		}
		// sort ascending 
		sort(patterns);
	}
	
	public static void calPerClassIg(Instances D, List<PuPattern> patterns) {
		Map<Double, List<Instance>> map = mapInstancesByClass(D);
		int numInstances = D.numInstances();
		for (PuPattern pattern : patterns) {
			double cls = pattern.getFromClass();
			
			for (int i = 0; i < numInstances; i++) {
				Instance ins = D.instance(i);
				if (pattern.isFit(ins)) {
					if (ins.classValue() == cls) {
						pattern.clsFitCount++;
					} else {
						pattern.otherClsFitCount++;
					}
				}
			}
			int clsNumInstances = map.get(cls).size();
			pattern.clsFitCount = pattern.clsFitCount / clsNumInstances;
			pattern.otherClsFitCount = pattern.otherClsFitCount / (numInstances - clsNumInstances);
		}
		Collections.sort(patterns, new Comparator<PuPattern>() {
			@Override
			public int compare(PuPattern o1, PuPattern o2) {
				return Double.compare(o1.clsFitCount, o2.clsFitCount);
			}
		});
		
	}
	
	
	private static void sort(List<? extends Pattern> patterns) {
		// sort ascending 
		Collections.sort(patterns, new Comparator<Pattern>() {
			@Override
			public int compare(Pattern o1, Pattern o2) {
				return Double.compare(o1.getIg(), o2.getIg());
			}
		});
	}
	
	
	
	private static double info(Instances D) {
		double numInstances = D.numInstances();
		Map<Double, List<Instance>> map = mapInstancesByClass(D);
		double result = 0;
		for (List<Instance> D_class_x : map.values()) {
			int count_D_class_x = D_class_x.size();
			if (count_D_class_x != 0) {
				result += -count_D_class_x/numInstances * Math.log(count_D_class_x/numInstances) / Math.log(2);
			}
		}
		return result;
		
	}
	private static double infoOfPattern(Instances D, Pattern pattern) {
		// 划分fit和no-fit两个分区, 因为对pattern而言，只有两个分区
		Set<Instance> fitInstances = new HashSet<Instance>();
		Set<Instance> nofitInstances = new HashSet<Instance>();
		for (int i = 0; i < D.numInstances(); i++) {
			Instance ins = D.instance(i);
			if (pattern.isFit(ins)) {
				fitInstances.add(ins);
			} else {
				nofitInstances.add(ins);
			}
		}
		
		//pattern.setCoveredSet(new HashSet<>(fitInstances));
		double total = D.numInstances();
		double count_D1 = fitInstances.size();
		double count_D0 = nofitInstances.size();
		
		assert (total == count_D0 + count_D1);
		
		return count_D1/total*infoOfD_class_x(fitInstances) + count_D0/total*infoOfD_class_x(nofitInstances);
	}
	
	private static double infoOfD_class_x(Set<Instance> D_class_X) {
		double total = D_class_X.size();
		Map<Double, List<Instance>> map = mapInstancesByClass(D_class_X);
		
		// count_c_x=0时，导致log求值为NaN，因此这里需要去掉count_c_x为0的情形
		// 舍弃为0的项
		double result = 0;
		for (List<Instance> list : map.values()) {
			int count_c_x = list.size();
			if (count_c_x != 0) {
				result += -count_c_x/total*Math.log(count_c_x/total)/Math.log(2);
			}
		}
		return result;
	}
	
	
	private static Map<Double, List<Instance>> mapInstancesByClass(Instances D) {
		Map<Double, List<Instance>> map = new HashMap<Double, List<Instance>>();
		for (int i = 0; i < D.numInstances(); i++) {
			Instance ins = D.instance(i);
			double cls = ins.classValue();
			if (map.containsKey(cls)) {
				map.get(cls).add(ins);
			} else {
				List<Instance> D_class_x = new ArrayList<Instance>();
				D_class_x.add(ins);
				map.put(cls, D_class_x);
			}
		}
		return map;
	} 
	
	
	/*
	 * map instances according to their class
	 * @return map<classVal, List<Instance>>
	 */
	private static Map<Double, List<Instance>> mapInstancesByClass(Set<Instance> instances) {
		Map<Double, List<Instance>> map = new HashMap<>();
		for (Instance ins : instances) {
			if (map.containsKey(ins.classValue())) {
				map.get(ins.classValue()).add(ins);
			} else {
				List<Instance> l_x = new ArrayList<>();
				l_x.add(ins);
				map.put(ins.classValue(), l_x);
			}
		}
		return map;
	}
}
