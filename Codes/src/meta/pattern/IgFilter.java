package meta.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;

import meta.entity.Pattern;
import meta.util.constants.Constant;

public class IgFilter {
	
	/**
	 * 根据pattern的ig降序排列
	 * @param patterns
	 * @param train
	 */
	public static List<Pattern> calculateAndSortByIg(Instances train, List<Pattern> patterns) {
		// 计算pattern的ig值，一折的fold作为全局的信息
		double info = infoOfTrain(train);
		
		List<Pattern> rst = new ArrayList<Pattern>();
		for (Pattern pattern : patterns) {
			double gain = info - infoOfPattern(train, pattern);
			pattern.setIg(gain);
			rst.add(pattern);
		}
		
		// 根据ig降序排列
		Collections.sort(rst, new Comparator<Pattern>() {
			@Override
			public int compare(Pattern arg0, Pattern arg1) {
				return (int) (arg1.getIg() - arg0.getIg());
			}
		});
		
		return rst;
	}
	
	
	/**
	 * 根据trainL中cover的instances的情况，挑选pattern作为最终的feature
	 * @param train
	 * @param patterns
	 * @param delta
	 * @return
	 */
	public static List<Pattern> filterByCoverage(Instances train, List<Pattern> patterns, int delta) {
		List<Pattern> rst = new ArrayList<Pattern>();
		
		Map<Instance, Integer> coverageCountMap = new HashMap<Instance, Integer>();
		for (int i=0; i<train.numInstances(); i++) {
			Instance ins = train.instance(i);
			coverageCountMap.put(ins, 0);
		}
		int min = 0;
		
		int index = 0;
		int total = patterns.size();
		while(true) {
			if (index >= total) {
				//System.out.println("pattern全部取完了");
				break;
			}
			if (min >= delta) {
				//System.out.println("all instances满足阈值delta了");
				//System.out.println(index);
				break;
			}
			Pattern p = patterns.get(index);
			rst.add(p);
			index++;
			
			// 统计所有instance的cover程度
			// 更新min值
			int curMin = Integer.MAX_VALUE;
			for (int i=0; i<train.numInstances(); i++) {
				Instance ins = train.instance(i);
				if (FitJudger.isFit(ins, p)) {
					int newCount = coverageCountMap.get(ins) + 1;
					coverageCountMap.put(ins, newCount);
				}
				if (coverageCountMap.get(ins) < curMin) {
					curMin = coverageCountMap.get(ins);
				}
			}
			min = curMin;
		}
		
		if (Constant.debug_fp_ig) {
			System.out.println("过滤pattern比例：" + rst.size()+ "/" + patterns.size());
		}
		
		return rst;
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
		return (-count_c1/total*Math.log(count_c1/total)/Math.log(2) - count_c0/total*Math.log(count_c0/total)/Math.log(2));
	}
		
	private static double infoOfPattern(Instances train, Pattern pattern) {
		// 区分C-1和C-0两类的instances
		List<Instance> fitInstances = new ArrayList<Instance>();
		List<Instance> nofitInstances = new ArrayList<Instance>();
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
			if (FitJudger.isFit(ins, pattern)) {
				fitInstances.add(ins);
			} else {
				nofitInstances.add(ins);
			}
		}
		double total = train.numInstances();
		double count_c1 = fitInstances.size();
		double count_c0 = nofitInstances.size();
		
		return count_c1/total*infoOfPartialTrain(fitInstances) + count_c0/total*infoOfPartialTrain(nofitInstances);
	}
	
	private static double infoOfPartialTrain(List<Instance> patrialInstances) {
		double total = patrialInstances.size();
		double count_c1 = 0;
		for (int i = 0; i < patrialInstances.size(); i++) {
			Instance ins = patrialInstances.get(i);
			if (ins.classValue() == 0.0) {
				count_c1++;
			}
		}
		double count_c0 = total - count_c1;
		return (-count_c1/total*Math.log(count_c1/total)/Math.log(2) - count_c0/total*Math.log(count_c0/total)/Math.log(2));
	}
}
