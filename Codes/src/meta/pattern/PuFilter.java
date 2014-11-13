package meta.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.PuPattern;
import weka.core.Instance;
import weka.core.Instances;

public class PuFilter {
	
	
	/**
	 * ����L1�����ɵ�pattern
	 * @param trainC_1
	 * @param trainC_0
	 * @param patterns
	 * @param test
	 * @param recall
	 * @return
	 */
	public static List<PuPattern> filter1(List<Instance> trainC_1, List<Instance> trainC_0, List<PuPattern> patterns, Instances test, double recall) {
		
		// ��ѡ����recall��pattern��Ϊ��ѡpattern
		List<PuPattern> filteredPatterns = filterByRecall1(trainC_1, trainC_0, patterns, recall);
		//filteredPatterns = CalculateAndSortBySuppU(filteredPatterns, test);
		// ͳ��ֵ
		int trainCount = trainC_0.size() + trainC_1.size();
		/*System.out.println("recall��ֵ:" + recall + "(" + recall*trainCount + ")");
		System.out.println("L0:" + trainC_0.size());
		System.out.println("L1:" + trainC_1.size());*/
		return filteredPatterns;
	}
	
	public static List<PuPattern> filter0(List<Instance> trainC_1, List<Instance> trainC_0, List<PuPattern> patterns, Instances test, double recall) {
		
		// ��ѡ����recall��pattern��Ϊ��ѡpattern
		List<PuPattern> filteredPatterns = filterByRecall0(trainC_1, trainC_0, patterns, recall);
		//filteredPatterns = CalculateAndSortBySuppU(filteredPatterns, test);
		/*for (PuPattern pattern : filteredPatterns) {
			System.out.println(pattern.pItems() + " | " + pattern.getSuppL1() + " | " + pattern.getSuppL0() + " | " + pattern.getSuppU());
		}*/
		return filteredPatterns;
	}
	
	
	/**
	 * ����recall���˳���ѡL1�ϵ�pattern
	 * @param train
	 * @param recall
	 * @return
	 */
	private static List<PuPattern> filterByRecall1(List<Instance> trainC_1, List<Instance> trainC_0, List<PuPattern> patterns, double recall) {
		List<PuPattern> filteredPatterns = new ArrayList<PuPattern>();
		
		int trainCount = trainC_0.size() + trainC_1.size();
		
		int L0_count = trainC_0.size();
		
		// ����pattern
		for (PuPattern pattern : patterns) {
			if (pattern.getSuppL1()+L0_count-pattern.getSuppL0() >= recall*trainCount) {
				filteredPatterns.add(pattern);
			}
		}
		
		return filteredPatterns;
	}
	
	/**
	 * ����recall���˳���ѡL0�ϵ�pattern
	 * @param train
	 * @param recall
	 * @return
	 */
	private static List<PuPattern> filterByRecall0(List<Instance> trainC_1, List<Instance> trainC_0, List<PuPattern> patterns, double recall) {
		List<PuPattern> filteredPatterns = new ArrayList<PuPattern>();
		
		int trainCount = trainC_0.size() + trainC_1.size();
		
		int L1_count = trainC_1.size();
		
		// ����pattern
		for (PuPattern pattern : patterns) {
			if (pattern.getSuppL0()+L1_count-pattern.getSuppL1() >= recall*trainCount) {
				filteredPatterns.add(pattern);
			}
		}
		return filteredPatterns;
	}
	
	/**
	 * ����suppU����pattern
	 * @param filteredPatterns
	 * @param test
	 * @return
	 */
	public static List<PuPattern> CalculateAndSortBySuppU(List<PuPattern> filteredPatterns, Instances test) {
		// ����pattern��U�ϵ�supportֵ��д��pattern��suppU�ֶ�
		for (int i = 0; i < test.numInstances(); i++) {
			Instance ins = test.instance(i);
			for (PuPattern pattern : filteredPatterns) {
				if (FitJudger.isFit(ins, pattern)) {
					pattern.incrSuppU();
				}
			}
		}
		// ����suppU��������
		Collections.sort(filteredPatterns, new Comparator<PuPattern>() {
			@Override
			public int compare(PuPattern p0, PuPattern p1) {
				return p0.getSuppU() - p1.getSuppU();
			}
		});
		
		return filteredPatterns;
	}
	
	
	/**
	 * ����trainL��cover��instances���������ѡpattern��Ϊ���յ�feature
	 * @param train
	 * @param patterns
	 * @param delta
	 * @return
	 */
	public static List<PuPattern> filterByCoverage(Instances train, List<PuPattern> patterns, int delta) {
		List<PuPattern> rst = new ArrayList<PuPattern>();
		
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
				//System.out.println("patternȫ��ȡ����");
				break;
			}
			if (min >= delta) {
				//System.out.println("all instances������ֵdelta��");
				//System.out.println(index);
				break;
			}
			PuPattern p = patterns.get(index);
			rst.add(p);
			index++;
			
			// ͳ������instance��cover�̶�
			// ����minֵ
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
		return rst;
	}
	
	
}
