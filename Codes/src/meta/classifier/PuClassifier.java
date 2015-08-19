package meta.classifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sun.istack.internal.Nullable;

import meta.entity.Item;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.PuFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
import meta.transaction.TransactionAug;
import meta.util.ChiSquareCalculator;
import meta.util.IgCalculator;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instance;
import weka.core.Instances;

public class PuClassifier implements IClassifier {
	
	private EvalResource resource;
	
	public PuClassifier( EvalResource resource) {
		this.resource = resource;
	}
	@Override
	public void evaluate() throws Exception {
		Instances inss = new Instances(resource.getInstances());
		
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);				// evaluator for PAT_PU
		Evaluator eval4PatAll = Evaluator.newEvaluator(resource.getClassifier(), inss);			// evaluator for PAT_ALL
		Evaluator eval4SingleFeatures = Evaluator.newEvaluator(resource.getClassifier(), inss);	// evaluator for single_features
		
		
		int numFolds = resource.getNumFolds();
		// 全局pattern
//		Map<Double, List<Instance>> inssMap = mapInstancesByClass(inss);
//		for (Double classVal : inssMap.keySet()) {
//			PatternGen.genPuPatterns4Fold4L_x(inssMap.get(classVal), -1, classVal);
//		}
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances[] tt = TrainTestGen.genTrainTest(resource.getTrainRatio(), inss, fold);
			Instances train = tt[0];
			Instances test = tt[1];
//			Instances train = TrainTestGen.genTrain(inss, numFolds, fold);
//			Instances test = TrainTestGen.genTest(inss, numFolds, fold);
			
			// map train instances to l_x
			Map<Double, List<Instance>> map = mapInstancesByClass(train);
			
			// union of patterns for storage
			Map<String, PuPattern> union = new HashMap<>();
			
			for (Double classVal : map.keySet()) {
				// get L_x instances
				List<Instance> instanceListL_x = map.get(classVal);
				
				// gen L_x patterns
				PatternGen.genPuPatterns4Fold4L_x(instanceListL_x, fold, classVal);
				List<PuPattern> patterns4Fold4L_x = PatternLoader.loadPuPatterns4Fold4L_x(inss, fold, classVal);
//				// load 全局pattern
//				List<PuPattern> patterns4Fold4L_x = PatternLoader.loadPuPatterns4Fold4L_x(inss, -1, classVal);
				
				// calculate D(x)
				caluateDx(patterns4Fold4L_x, instanceListL_x, inss);
				
				// union
				for (PuPattern puPattern : patterns4Fold4L_x) {
					if (!union.containsKey(puPattern.pName())) {
						union.put(puPattern.pName(), puPattern);
					}
				}
			}
			
			List<PuPattern> patterns = new ArrayList<>(union.values());
			
			
			 
			/*
			 * 因素分析：打印IG和卡方
			 *--------------------------------BEGIN------------------------------------ 
			 */
			if (Constant.debug_one_class_ig) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					IgCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getIg());
					}
					System.out.println("--------------------------");
				}
			}
			if (Constant.debug_one_class_chi) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					ChiSquareCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getChi());
					}
					ChiSquareCalculator.cal4PerClass(inss, patterns);
					for (PuPattern pattern : patterns) {
//						TreeMap<Double, Double> chi4PerClass = pattern.chi4PerClass;
//						for (Double chi : chi4PerClass.values()) {
//							System.out.print(chi + "\t");
//						}
//						TreeMap<Double, Double> supp4PerClass = pattern.supp4PerClass;
//						for (Double supp : supp4PerClass.values()) {
//							System.out.print(supp + "\t");
//						}
						//System.out.println(pattern.getgSupport());
						//System.out.println();
					}
					System.out.println("--------------------------");
				}
			}
			/*
			 *---------------------------------END-------------------------------------
			 */
			
			
			// sort by D(x) value
			
			patterns.sort(new Comparator<PuPattern>() {
				@Override // 从高到低排序
				public int compare(PuPattern p1, PuPattern p2) {
					return Double.compare(p2.getDx(), p1.getDx());
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
			
			
			 
			/*
			 * 因素分析：打印IG和卡方(全局)
			 *--------------------------------BEGIN------------------------------------ 
			 */
			if (Constant.debug_one_class_ig_afterFilter) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					IgCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getIg());
					}
					System.out.println("--------------------------");
				}
			}
			if (Constant.debug_one_class_chi_afterFilter) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					ChiSquareCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getChi());
					}
					ChiSquareCalculator.cal4PerClass(inss, patterns);
					for (PuPattern pattern : patterns) {
//						TreeMap<Double, Double> chi4PerClass = pattern.chi4PerClass;
//						for (Double chi : chi4PerClass.values()) {
//							System.out.print(chi + "\t");
//						}
//						TreeMap<Double, Double> supp4PerClass = pattern.supp4PerClass;
//						for (Double supp : supp4PerClass.values()) {
//							System.out.print(supp + "\t");
//						}
						//System.out.println(pattern.getgSupport());
						//System.out.println();
					}
					System.out.println("--------------------------");
				}
			}
			/*
			 *---------------------------------END-------------------------------------
			 */
			
			
			
		}
		//System.out.println(eval.printWeightedEvalRst());
		//System.out.println(eval.printConfusionMatrix());
		System.out.println("-------------------------------------------");
		System.out.println("Pu single features:");
		System.out.println("avg\t" + eval4SingleFeatures.getAvgRstString());
		System.out.println("max\t" + eval4SingleFeatures.getMaxRstString());
		System.out.println("-------------------------------------------");
		System.out.println("-------------------------------------------");
		System.out.println("Pu patterns all:");
		System.out.println("avg\t" + eval4PatAll.getAvgRstString());
		System.out.println("max\t" + eval4PatAll.getMaxRstString());
		System.out.println("-------------------------------------------");
		System.out.println("-------------------------------------------");
		System.out.println("Pu patterns:");
		System.out.println("avg\t" + eval.getAvgRstString());
		System.out.println("max\t" + eval.getMaxRstString());
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
