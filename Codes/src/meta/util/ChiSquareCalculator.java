package meta.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.Pattern;
import meta.entity.PuPattern;
import weka.core.Instance;
import weka.core.Instances;

public class ChiSquareCalculator {
	
	private static class FitCounter {
		public int fit;
		public int noFit;
	}
	
	public static void cal(Instances D, List<? extends Pattern> patterns) {
		int numInstances = D.numInstances();
		Map<Double, List<Instance>>	map = mapInstancesByClass(D);
		for (Pattern pattern : patterns) {
			double chi = 0;
			FitCounter patFitCount = count(D, pattern);
			
			for (List<Instance> clsInstances : map.values()) {
				FitCounter clsFitCount = count(clsInstances, pattern);
				int clsTotal = clsInstances.size();
				chi += (Math.pow(clsFitCount.fit, 2) / clsTotal / patFitCount.fit
						+ Math.pow(clsFitCount.noFit, 2) / clsTotal / patFitCount.noFit);
			}
			chi = (chi-1) * numInstances;
			pattern.setChi(chi);
		}
//		// sort
//		Collections.sort(patterns, new Comparator<Pattern>() {
//			@Override
//			public int compare(Pattern o1, Pattern o2) {
//				return Double.compare(o1.getChi(), o2.getChi());
//			}
//		});
	}
	
	
	public static void cal4PerClass(Instances D, List<? extends Pattern> patterns) {
		Map<Double, List<Instance>> map = mapInstancesByClass(D);
//		for (List<Instance> list : map.values()) {
//			System.out.println("class  size = "  + list.size());
//		}
		int numInstances = D.numInstances();
		for (Pattern pattern : patterns) {
			// global support 
			FitCounter fc = count(D, pattern);
			pattern.setgSupport(fc.fit);
			
			for (Double cls : map.keySet()) {
				double chi = 0;
				
				List<Instance> clsInstances = map.get(cls);
				List<Instance> otherClsInstances = new ArrayList<>();
				for (Double cls2 : map.keySet()) {
					if (cls2 != cls) {
						otherClsInstances.addAll(map.get(cls2));
					}
				}
				
				FitCounter  clsFc = count(clsInstances, pattern);
				FitCounter otherClsFc = count(otherClsInstances, pattern);
				
				double a = clsFc.fit;
				double b = otherClsFc.fit;
				double c = clsFc.noFit;
				double d = otherClsFc.noFit;
				
				chi  = numInstances * Math.pow((a*d-b*c), 2) / (a+b) / (c+d) / (a+c) / (b+d);
//				if (chi == 0) {
//					System.out.println("eee");
//				}
				pattern.chi4PerClass.put(cls, chi);
				pattern.supp4PerClass.put(cls, a/clsInstances.size());
			}
		}
	}
	
	
	
	private static FitCounter count(Instances D, Pattern pattern) {
		FitCounter fc = new FitCounter();
		int numInstances = D.numInstances();
		for (int i = 0; i < numInstances; i++) {
			Instance ins = D.instance(i);
			if (pattern.isFit(ins)) {
				fc.fit++;
			} else {
				fc.noFit++;
			}
		}
		assert (fc.fit+fc.noFit == numInstances);	// assert condition
		return fc;
	}
	private static FitCounter count(List<Instance> clsInstances, Pattern pattern) {
		FitCounter fc = new FitCounter();
		int numInstances = clsInstances.size();
		for (Instance ins : clsInstances) {
			if (pattern.isFit(ins)) {
				fc.fit++;
			} else {
				fc.noFit++;
			}
		}
		assert (fc.fit+fc.noFit == numInstances);
		return fc;
	}
	
	
	private static Map<Double, List<Instance>> mapInstancesByClass(Instances D) {
		Map<Double, List<Instance>> map = new HashMap<Double, List<Instance>>();
		int numInstances = D.numInstances();
		for (int i = 0; i < numInstances; i++) {
			Instance ins = D.instance(i);
			double cls = ins.classValue();
			if (map.containsKey(cls)) {
				map.get(cls).add(ins);
			} else {
				List<Instance> clsInstances = new ArrayList<Instance>();
				clsInstances.add(ins);
				map.put(cls, clsInstances);
			}
		}
		return map;
	}
	
	
}
