package meta.filter;

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
	 * ����L_x�����ɵ�pattern
	 * @param trainC_1
	 * @param trainC_0
	 * @param patterns
	 * @param test
	 * @param recall
	 * @return
	 */
	public static List<PuPattern> filter(List<Instance> trainCX, List<Instance> trainC_X, List<PuPattern> patterns, double recall) {
		
		// ��ѡ����recall��pattern��Ϊ��ѡpattern
		List<PuPattern> filteredPatterns = filterByRecall(trainCX, trainC_X, patterns, recall);
		//filteredPatterns = CalculateAndSortBySuppU(filteredPatterns, test);
		// ͳ��ֵ
		int trainCount = trainCX.size() + trainC_X.size();
		/*System.out.println("recall��ֵ:" + recall + "(" + recall*trainCount + ")");
		System.out.println("L0:" + trainC_0.size());
		System.out.println("L1:" + trainC_1.size());*/
		return filteredPatterns;
	}
	
	
	/**
	 * ����recall���˳���ѡLX�ϵ�pattern
	 * @param train
	 * @param recall
	 * @return
	 */
	private static List<PuPattern> filterByRecall(List<Instance> trainCX, List<Instance> trainC_X, List<PuPattern> patterns, double recall) {
		List<PuPattern> filteredPatterns = new ArrayList<PuPattern>();
		
		int trainCount = trainCX.size() + trainC_X.size();
		
		int L_X_count = trainC_X.size();
		
		// ����pattern
		for (PuPattern pattern : patterns) {
			if (pattern.getSuppL1()+L_X_count-pattern.getSuppL0() >= recall*trainCount) {
				filteredPatterns.add(pattern);
			}
		}
		
		return filteredPatterns;
	}
	
	/**
	 * ����trainL��cover��instances���������ѡpattern��Ϊ���յ�feature
	 * @param train
	 * @param patterns
	 * @param delta
	 * @return
	 */
	public static List<PuPattern> filterByCoverage(Instances train, Instances test, List<PuPattern> patterns, int delta) {
		List<PuPattern> rst = new ArrayList<PuPattern>();
		// ����pattern��U�ϵ�supportֵ��д��pattern��suppU�ֶ�
		for (int i = 0; i < test.numInstances(); i++) {
			Instance ins = test.instance(i);
			for (PuPattern pattern : patterns) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppU();
				}
			}
		}
		// ����suppU��������
		Collections.sort(patterns, new Comparator<PuPattern>() {
			@Override
			public int compare(PuPattern p0, PuPattern p1) {
				return p0.getSuppU() - p1.getSuppU();
			}
		});
		
		// ͳ��coverage��ѡpattern
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
	}
	
	
}
