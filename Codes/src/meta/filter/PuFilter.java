package meta.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.Item;
import meta.entity.PuPattern;
import meta.util.constants.Constant;
import meta.util.loader.ItemLoader;
import weka.core.Instance;
import weka.core.Instances;

public class PuFilter {
	
	/**
	 * 过滤L_x上生成的pattern
	 * @param trainC_1
	 * @param trainC_0
	 * @param patterns
	 * @param test
	 * @param recall
	 * @return
	 *//*
	public static List<PuPattern> filter(List<Instance> trainCX, List<Instance> trainC_X, List<PuPattern> patterns, double recall) {
		
		// 挑选超过recall的pattern作为候选pattern
		List<PuPattern> filteredPatterns = filterByRecall(trainCX, trainC_X, patterns, recall);
		//filteredPatterns = CalculateAndSortBySuppU(filteredPatterns, test);
		// 统计值
		int trainCount = trainCX.size() + trainC_X.size();
		System.out.println("recall阈值:" + recall + "(" + recall*trainCount + ")");
		System.out.println("L0:" + trainC_0.size());
		System.out.println("L1:" + trainC_1.size());
		return filteredPatterns;
	}*/
	
	
	/**
	 * 根据recall过滤出候选LX上的pattern
	 * @param train
	 * @param recall
	 * @return
	 *//*
	private static List<PuPattern> filterByRecall(List<Instance> trainCX, List<Instance> trainC_X, List<PuPattern> patterns, double recall) {
		List<PuPattern> filteredPatterns = new ArrayList<PuPattern>();
		
		int trainCount = trainCX.size() + trainC_X.size();
		
		int L_X_count = trainC_X.size();
		
		// 过滤pattern
		for (PuPattern pattern : patterns) {
			if (pattern.getSuppL1()+L_X_count-pattern.getSuppL0() >= recall*trainCount) {
				filteredPatterns.add(pattern);
			}
		}
		
		return filteredPatterns;
	}*/
	
	public static List<PuPattern> filterByInstanceCoverage(Instances test, List<PuPattern> patterns, double coverage) {
		
		return patterns;
	}
	
	public static List<PuPattern> filterByItemCoverage(Instances inss, List<PuPattern> inputPatterns, double coverage) throws IOException {
		List<Item> itemList = new ArrayList<>(ItemLoader.loadItemsByReverse(inss).values());	// 全局item
		
		List<PuPattern> filteredPatterns = new ArrayList<>();	// output result list
		Map<Integer, Integer> itemCountMap = new HashMap<>();	// map to count items' coverage
		double threshold = itemList.size() * coverage;
		int coveredItems = 0;
		for (PuPattern pattern : inputPatterns) {
			if ((double)coveredItems >= threshold) {	// if satisfy, break
				if (Constant.debug_item_coverage) {
					System.out.println("Item coverage: " + coveredItems + "/" + itemList.size());
				}
				break;
			}
			// count item coverage
			for (Item item : pattern.entrys()) {
				if (!itemCountMap.containsKey(item.getId())) {	
					coveredItems++;
				}
			}
			filteredPatterns.add(pattern);
		}
		return filteredPatterns;
	}
	
	
	/**
	 * 根据trainL中cover的instances的情况，挑选pattern作为最终的feature
	 * @param train
	 * @param patterns
	 * @param delta
	 * @return
	 *//*
	public static List<PuPattern> filterByCoverage(Instances train, Instances test, List<PuPattern> patterns, int delta) {
		List<PuPattern> rst = new ArrayList<PuPattern>();
		// 计算pattern在U上的support值并写到pattern的suppU字段
		for (int i = 0; i < test.numInstances(); i++) {
			Instance ins = test.instance(i);
			for (PuPattern pattern : patterns) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppU();
				}
			}
		}
		// 按照suppU降序排列
		Collections.sort(patterns, new Comparator<PuPattern>() {
			@Override
			public int compare(PuPattern p0, PuPattern p1) {
				return p0.getSuppU() - p1.getSuppU();
			}
		});
		
		// 统计coverage挑选pattern
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
			PuPattern p = patterns.get(index);
			rst.add(p);
			index++;
			
			// 统计所有instance的cover程度
			// 更新min值
			int curMin = Integer.MAX_VALUE;
			for (int i=0; i<train.numInstances(); i++) {
				Instance ins = train.instance(i);
				if (p.isFit(ins)) {
					int newCount = coverageCountMap.get(ins) + 1;
					coverageCountMap.put(ins, newCount);
				}
				if (coverageCountMap.get(ins) < curMin) {
					curMin = coverageCountMap.get(ins);
				}
			}
			min = curMin;
		}
		return rst;
	}*/
	
	
}
