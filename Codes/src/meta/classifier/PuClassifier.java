package meta.classifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.Item;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.PuFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
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
		
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);				// evaluator for PAT_PU
		Evaluator eval4PatAll = Evaluator.newEvaluator(resource.getClassifier(), inss);			// evaluator for PAT_ALL
		Evaluator eval4SingleFeatures = Evaluator.newEvaluator(resource.getClassifier(), inss);	// evaluator for single_features
		
		
		int numFolds = resource.getNumFolds();
		for (int fold = 0; fold < numFolds; fold++) {
			Instances train = TrainTestGen.genTrain(inss, numFolds, fold);
			Instances test = TrainTestGen.genTest(inss, numFolds, fold);
			
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
			
			
			/*
			 * 对比测度：pattern all，不经过filter过程 
			 *--------------------------------BEGIN------------------------------------ 
			 */
			//System.out.println("Pat_ALL:" + numPatterns);
			Instances augTrain = TransactionAug.augmentDatasetV2(patterns, train);
			Instances augTest = TransactionAug.augmentDatasetV2(patterns, test);
			eval4PatAll.evalV2(augTrain, augTest);
			/*
			 *---------------------------------END-------------------------------------
			 */
			
			
			// filter by instance coverage
			patterns = PuFilter.filterByInstanceCoverageV2(test, patterns, Constant.instance_coverage);
			//System.out.println("Filtered: " + patterns.size() + "/" + numPatterns);
			
			
			/*
			 * 对比测度：single feature & fs
			 *--------------------------------BEGIN------------------------------------ 
			 */
			List<Item> singleItems = extractItems(patterns);
			augTrain = TransactionAug.augmentDataset4SingleFeatures(singleItems, train);
			augTest = TransactionAug.augmentDataset4SingleFeatures(singleItems, test);
			eval4SingleFeatures.evalV2(augTrain, augTest);
			
			/*
			 *---------------------------------END-------------------------------------
			 */
			
			
			if (Constant.deubg_pattern_filterd) {
				System.out.println("pattern的总数：" + numPatterns);
				System.out.println("过滤后pattern：" + patterns.size());
				// print patterns
				System.out.println("fold-" + fold + ":");
				for (PuPattern pattern : patterns) {
					System.out.println(pattern.getFromClass() + "|" + pattern.pId() + "|" + pattern.getDx() + "|" + pattern.getSuppL1() + "|" + pattern.getIdf());
				}
			}
			
			// 增广instance
			augTrain = TransactionAug.augmentDatasetV2(patterns, train);
			augTest = TransactionAug.augmentDatasetV2(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
			
		}
		//System.out.println(eval.printWeightedEvalRst());
		//System.out.println(eval.printConfusionMatrix());
		System.out.println("-------------------------------------------");
		System.out.println("Pu patterns:");
		System.out.println("avg" + eval.getAvgRstString());
		System.out.println("max" + eval.getMaxRstString());
		System.out.println("-------------------------------------------");
		
		System.out.println("-------------------------------------------");
		System.out.println("Pu single features:");
		System.out.println("avg" + eval4SingleFeatures.getAvgRstString());
		System.out.println("max" + eval4SingleFeatures.getMaxRstString());
		System.out.println("-------------------------------------------");
		
		System.out.println("-------------------------------------------");
		System.out.println("Pu patterns all:");
		System.out.println("avg" + eval4PatAll.getAvgRstString());
		System.out.println("max" + eval4PatAll.getMaxRstString());
		System.out.println("-------------------------------------------");
		
	}
	
	/*
	 * map instances according to their class
	 * @return map<classVal, List<Instance>>
	 */
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
	
	/*
	 * calculate Dx value
	 */
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
	
	/*
	 * @param 经过pu过滤的pattern
	 * @return 所有pattern的item（去重）
	 */
	private List<Item> extractItems(List<PuPattern> patterns) {
		Map<Integer, Item> map = new HashMap<>();
		for (Pattern pattern : patterns) {
			for (Item item : pattern.entrys()) {
				map.putIfAbsent(item.getId(), item);
			}
		}
		return new ArrayList<>(map.values());
	}
	
}
