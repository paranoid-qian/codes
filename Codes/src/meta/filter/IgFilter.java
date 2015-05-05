package meta.filter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
		covered1.retainAll(covered2);
		int p12 = covered1.size();
		if (p1 + p2 - p12 == 0) {
			System.out.println(pattern.pId());
			System.out.println(selected.pId());
		}
		double gain = pattern.getRevelance() - p12 / (p1 + p2 - p12) 
				* Math.min(selected.getRevelance(), pattern.getRevelance());
		if (gain < pattern.getGain()) {
			pattern.setGain(gain);
			return true;
		}
		return false;
	}
	
	private static double infoOfTrain(Instances train) {
		double total = train.numInstances();
		double count_c1 = 0;
		for (int i = 0; i < total; i++) {
			Instance ins = train.instance(i);
			if (ins.classValue() == 0.0) {
				count_c1++;
			}
		}
		double count_c0 = total - count_c1;
		if (count_c0 != 0 && count_c1 != 0) {
			return ( -count_c1/total*Math.log(count_c1/total)/Math.log(2) - count_c0/total*Math.log(count_c0/total)/Math.log(2) );
		} else if (count_c0 == 0) {
			return ( -count_c1/total*Math.log(count_c1/total)/Math.log(2) );
		} else {
			return ( -count_c0/total*Math.log(count_c0/total)/Math.log(2));
		}
	}
		
	private static double infoOfPattern(Instances train, Pattern pattern) {
		// 区分C-1和C-0两类的instances
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
		double count_c1 = fitInstances.size();
		double count_c0 = nofitInstances.size();
		
		return count_c1/total*infoOfPartialTrain(fitInstances) + count_c0/total*infoOfPartialTrain(nofitInstances);
	}
	
	private static double infoOfPartialTrain(Set<Instance> partrialInstances) {
		double total = partrialInstances.size();
		double count_c1 = 0;
		for (Instance ins : partrialInstances) {
			if (ins.classValue() == 0.0) {
				count_c1++;
			}
		}
		double count_c0 = total - count_c1;
		// count_c1或count_c0为0时，导致log求值为NaN，因此这里需要分别处理
		// 舍弃为0的项
		if (count_c0 != 0 && count_c1 != 0) {
			return ( -count_c1/total*Math.log(count_c1/total)/Math.log(2) - count_c0/total*Math.log(count_c0/total)/Math.log(2) );
		} else if (count_c0 == 0) {
			return ( -count_c1/total*Math.log(count_c1/total)/Math.log(2) );
		} else {
			return ( -count_c0/total*Math.log(count_c0/total)/Math.log(2));
		}
	}
}
