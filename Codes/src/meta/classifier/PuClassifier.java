package meta.classifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.PuFilter;
import meta.gen.PatternGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instance;
import weka.core.Instances;

public class PuClassifier implements IClassifier {
	
	private EvalResource resource;
	
	public PuClassifier(EvalResource resource) {
		this.resource = resource;
	}
	
	@Override
	public void evaluate() throws Exception {
		Instances inss = new Instances(resource.getInstances());
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);
		int numFolds = resource.getNumFolds();
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
//			Instances[] trainTest = TrainTestGen.genTrainTest(trainRatio, inss, fold);
//			Instances train = trainTest[0];
//			Instances test = trainTest[1];
			
			// map train instances to l_x
			Map<Double, List<Instance>> map = mapInstancesByClass(train);
			
			// union of patterns for storage
			Map<String, PuPattern> union = new HashMap<>();
			
			for (Double classVal : map.keySet()) {
				// get L_1 instances
				List<Instance> instanceListL_x = map.get(classVal);
				
				// gen L_1 patterns
				PatternGen.genPuPatterns4Fold4L_x(instanceListL_x, fold, classVal);
				List<PuPattern> patterns4Fold4L_x = PatternLoader.loadPuPatterns4Fold4L_x(inss, fold, classVal);
				
				// calculate D(x)
				caluateDx(patterns4Fold4L_x, instanceListL_x, inss);
				
				// union
				for (PuPattern puPattern : patterns4Fold4L_x) {
					if (!union.containsKey(puPattern.pName())) {
						union.put(puPattern.pName(), puPattern);
					}
				}
			}
			
			// sort by D(x) value
			List<PuPattern> patterns = new ArrayList<>(union.values());
			patterns.sort(new Comparator<PuPattern>() {
				@Override // 从高到低排序
				public int compare(PuPattern p1, PuPattern p2) {
					if (p1.getDx() > p2.getDx()) { 
						return -1;
					} else if (p1.getDx() < p2.getDx()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			
			if (Constant.debug_pattern_dx) {
				// print patterns
				for (PuPattern pattern : patterns) {
					System.out.println(pattern.getFromClass() + "|" + pattern.pId() + "|" + pattern.getDx() + "|" + pattern.getSuppL1() + "|" + pattern.getIdf());
				}
			}
			
			int numPatterns = patterns.size();
			
			// filter by instance coverage
			patterns = PuFilter.filterByInstanceCoverageV2(test, patterns, Constant.instance_coverage);
			
			if (Constant.deubg_pattern_filterd) {
				System.out.println("pattern的总数：" + numPatterns);
				System.out.println("过滤后pattern：" + patterns.size());
				// print patterns
				System.out.println("fold-" + fold + ":");
				for (PuPattern pattern : patterns) {
					//System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
					System.out.println(pattern.getFromClass() + "|" + pattern.pId() + "|" + pattern.getDx() + "|" + pattern.getSuppL1() + "|" + pattern.getIdf());
				}
			}
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
			
		}
		//System.out.println(eval.printWeightedEvalRst());
		//System.out.println(eval.printConfusionMatrix());
		System.out.println(eval.getAvgRstString());
		System.out.println(eval.getMaxRstString());
	}
	
	private Map<Double, List<Instance>> mapInstancesByClass(Instances train) {
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
	
	private void caluateDx(List<PuPattern> patterns4Fold4L_x, List<Instance> instanceListL_x, Instances inss) {
		// suppD
		for (int i = 0; i < inss.numInstances(); i++) {
			Instance ins = inss.instance(i);
			for (PuPattern pattern : patterns4Fold4L_x) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppD();
				}
			}
		}
		// suppL1
		for (Instance ins : instanceListL_x) {
			for (PuPattern pattern : patterns4Fold4L_x) {
				if (pattern.isFit(ins)) {
					pattern.incrSuppL1();
				}
			}
		}
		// dx
		int dSize = inss.numInstances();
		int lSize = instanceListL_x.size();
		for (PuPattern pattern : patterns4Fold4L_x) {
			double dx = pattern.getSuppL1() * Math.log(dSize/pattern.getSuppD()) / Math.log(2);
			pattern.setDx(dx);
			pattern.setIdf(Math.log(dSize/pattern.getSuppD()) / Math.log(2));
		}
	}
	
}
