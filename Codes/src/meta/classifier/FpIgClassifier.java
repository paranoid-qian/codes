package meta.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.gen.PatternGen;
import meta.gen.TrainTestGen;
import meta.transaction.TransactionAug;
import meta.util.ChiSquareCalculator;
import meta.util.IgCalculator;
import meta.util.constants.Constant;
import meta.util.loader.PatternLoader;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This classifier is for HongChen's method
 * @author paranoid.Q
 * @date Apr 29, 2015 2:04:52 PM
 */
public class FpIgClassifier implements IClassifier {

	private EvalResource resource;
	
	public FpIgClassifier(EvalResource resource) {
		this.resource = resource;
	}
	
	@Override
	public void evaluate() throws Exception {
		Instances inss = new Instances(resource.getInstances());
		Evaluator eval = Evaluator.newEvaluator(resource.getClassifier(), inss);
		int numFolds = resource.getNumFolds();
		for (int fold = 0; fold < numFolds; fold++) {
//			Instances[] tt = TrainTestGen.genTrainTest(resource.getTrainRatio(), inss, fold);
//			Instances train = tt[0];
//			Instances test = tt[1];
			
			Instances train = TrainTestGen.genTrain(inss, numFolds, fold);
			Instances test = TrainTestGen.genTest(inss, numFolds, fold);
			
			// gen train_x pattern
			PatternGen.genTrain_Foldx_FpPatterns(inss, fold);
			
			// load train_x pattern��ֱ������FP������pattern���ɣ�
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			
			/*
			 * ���ط�������ӡIG�Ϳ���(ȫ��IG)
			 *--------------------------------BEGIN------------------------------------ 
			 */
			if (Constant.debug_fp_ig) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					IgCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getIg());
					}
					
					System.out.println("--------------------------");
				}
			}
			if (Constant.debug_fp_chi) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
//					ChiSquareCalculator.cal(inss, patterns);
//					for (Pattern pattern : patterns) {
//						System.out.println(pattern.getChi());
//					}
					ChiSquareCalculator.cal4PerClass(inss, patterns);
					for (Pattern pattern : patterns) {
//						TreeMap<Double, Double> chi4PerClass = pattern.chi4PerClass;
//						for (Double chi : chi4PerClass.values()) {
//							System.out.print(chi + "\t");
//						}
						TreeMap<Double, Integer> supp4PerClass = pattern.supp4PerClass;
						for (Integer supp : supp4PerClass.values()) {
							System.out.print(supp + "\t");
						}
						System.out.println();
					}
					System.out.println("--------------------------");
				}
			}
			/*
			 *---------------------------------END-------------------------------------
			 */
			
			
			// initialization
			IgFilter.calRelevance(train, patterns);
			IgFilter.sortByGain(patterns);
//			System.out.println("=====");
//			for (Pattern pattern : patterns) {
//				System.out.println(pattern.pId() + "|" + pattern.getGain() + "|" + pattern.getRevelance());
//			}
//			System.out.println("=====");
			
			// ����fpig_deltaѡ��pattern
			//System.out.println("***");
			patterns  = filter(train, patterns, Constant.fpig_delta);
			//System.out.println("***");
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.evalV2(augTrain, augTest);
			
			/*
			 * ���ط�������ӡIG�Ϳ���(ȫ��IG)
			 *--------------------------------BEGIN------------------------------------ 
			 */
			if (Constant.debug_fp_afterFiltered) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					IgCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getIg());
					}
					System.out.println("--------------------------");
				}
			}
			if (Constant.debug_fp_chi_afterFiltered) {
				if (fold == Constant.debug_fold) {
					System.out.println("--------------------------");
					ChiSquareCalculator.cal(inss, patterns);
					for (Pattern pattern : patterns) {
						System.out.println(pattern.getChi());
					}
					System.out.println("--------------------------");
				}
			}
			/*
			 *---------------------------------END-------------------------------------
			 */
			
		}
		System.out.println("-------------------------------------------");
		System.out.println("fp fs patterns:");
		System.out.println("avg\t" + eval.getAvgRstString());
		System.out.println("max\t" + eval.getMaxRstString());
		System.out.println("-------------------------------------------");
		
	}
	
	private List<Pattern> filter(Instances train, List<Pattern> list, int coverage_delta) {
		List<Pattern> result = new ArrayList<>();
		Set<Instance> coveredSet = new HashSet<>();
		
		int cover = 0;			// train������instance��cover�����ٴ���
		int numSelected = 0;	// ��ѡ���pattern����
		
		int numInstances = train.numInstances();
		int numPatterns = list.size();
		while(true) {
			if (numSelected >= numPatterns) {
				//System.out.println("selected all");
				break;	// selected all patterns
			}
			if (cover >= coverage_delta) {
				break;
			}
			Pattern selected = list.remove(list.size()-1);
			//System.out.println(selected.pId() + "|" + selected.getGain());
			result.add(selected);
			for (Pattern pattern : list) {
				IgFilter.updateGain(pattern, selected, train);
			}
			IgFilter.sortByGain(list);
			
			numSelected++;
			for (Instance instance : selected.getCoveredSet()) {
				if (coveredSet.size() == numInstances) {
					coveredSet.clear();
					cover++;
				}
				coveredSet.add(instance);
			}
		}
		return result;
	}
	
}
