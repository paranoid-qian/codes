package meta.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weka.core.Instance;
import weka.core.Instances;
import meta.entity.Pattern;

public class IgFilter {
	
	/**
	 * 计算relevance (用ig)
	 * @param patterns
	 * @param train
	 */
	public static void calRelevance(Instances train, List<Pattern> patterns) {
		// 计算pattern的ig值，一折的fold作为全局的信息
		double info = infoOfTrain(train);
		for (Pattern pattern : patterns) {
			double iGain = info - infoOfPattern(train, pattern);
			pattern.setRevelance(iGain);
			pattern.setGain(iGain);		// initial value of gain, when no pattern was already selected
		}
	}
	
	/**
	 * 根据gain升序排列
	 * @param patterns
	 */
	public static void sortByGain(List<Pattern> patterns) {
		Collections.sort(patterns, new Comparator<Pattern>() {
			@Override
			public int compare(Pattern arg0, Pattern arg1) {
				return Double.compare(arg0.getGain(), arg1.getGain());
			}
		});
	}
	
	/**
	 * update gain of a pattern in P according to the new selected pattern.
	 * @param pattern
	 * @param selected
	 * @param train
	 * @return true if updated, false if last GAIN value is kept
	 */
	public static boolean updateGain(Pattern pattern, Pattern selected, Instances train) {
		// 每个pattern的cover情况已经在计算ig的时候统计得出了，后面不需要重复计算
		Set<Instance> covered1 = new HashSet<>(pattern.getCoveredSet());
		Set<Instance> covered2 = new HashSet<>(selected.getCoveredSet());
		
		int p1 = covered1.size();
		int p2 = covered2.size();
		covered1.retainAll(covered2);	// 没有覆盖equals方法！
		int p12 = covered1.size();
//		if (p12 != 0) {
//			System.out.println(pattern.pId());
//			System.out.println(selected.pId());
//		}
		double r = ((double)p12) / (p1 + p2 - p12) 
				* Math.min(selected.getRevelance(), pattern.getRevelance());
		double gain = pattern.getRevelance() - r;
		if (gain < pattern.getGain()) {
			pattern.setGain(gain);
			//System.out.println("updated: " + pattern.pId());
			return true;
		}
		return false;
	}
	
	private static double infoOfTrain(Instances train) {
		double total = train.numInstances();
		Map<Double, List<Instance>> map = mapInstancesByClass(train);
		double result = 0;
		for (List<Instance> list : map.values()) {
			int count_c_x = list.size();
			if (count_c_x != 0) {
				result += (-count_c_x/total*Math.log(count_c_x/total)/Math.log(2));
			}
		}
		return result;
	}
		
	private static double infoOfPattern(Instances train, Pattern pattern) {
		// 划分fit和no-fit两个分区, 因为对pattern而言，只有两个分区
		Set<Instance> fitInstances = new HashSet<Instance>();
		Set<Instance> nofitInstances = new HashSet<Instance>();
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
			if (pattern.isFit(ins)) {
				fitInstances.add(ins);
			} else {
				nofitInstances.add(ins);
			}
		}
		pattern.setCoveredSet(new HashSet<>(fitInstances));
		double total = train.numInstances();
		double count_D1 = fitInstances.size();
		double count_D0 = nofitInstances.size();
		
		return count_D1/total*infoOfPartialTrain(fitInstances) + count_D0/total*infoOfPartialTrain(nofitInstances);
	}
	
	private static double infoOfPartialTrain(Set<Instance> partialInstances) {
		double total = partialInstances.size();
		Map<Double, List<Instance>> map = mapInstancesByClass(partialInstances);
		
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
	
	
	/*
	 * map instances according to their class
	 * @return map<classVal, List<Instance>>
	 */
	private static Map<Double, List<Instance>> mapInstancesByClass(Instances train) {
		Map<Double, List<Instance>> map = new HashMap<>();
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
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
