package meta.classifier;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jdk.nashorn.internal.ir.TernaryNode;
import sun.tools.jar.resources.jar;
import meta.entity.Item;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.filter.IgFilter;
import meta.filter.PatternFilter;
import meta.filter.PuFilter;
import meta.gen.PatternGen;
import meta.transaction.TransactionAug;
import meta.util.constants.Constant;
import meta.util.loader.ItemLoader;
import meta.util.loader.PatternLoader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class BaseClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = Constant.numFolds;
	
	private List<Integer> naiveErrorList = new ArrayList<>();
	private List<Integer> puErrorList = new ArrayList();
	
	
	public BaseClassifier(Classifier classifier) {
		
		super();
		this.classifier = classifier;
		data.randomize(Constant.rand);
		// 随机化train和test集
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		
		// 输出参数
		System.out.println("random\t" + Constant.s);
		System.out.println("numFolds\t" + Constant.numFolds);
		System.out.println("itemMaxCount\t" + Constant.itemMaxCount);
		System.out.println("puItemMaxCount\t" + Constant.puItemMaxCount);
		System.out.println("min_support\t" + Constant.minSupport);
		System.out.println("puMin_support\t" + Constant.puMinSupport);
		System.out.println("instance coverage\t" + Constant.instance_coverage);
	}
	
	
	/**
	 * 评估没有添加pattern的分类效果
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		// 评估分类性能
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			eval.eval(train, test);
			
			// eval for instance error
			//eval.eval4Instances(train, test, naiveErrorList);
		}
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * 评估fp的分类效果
	 * @throws Exception
	 */
	public void evaluateFP() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// gen train_x pattern
			PatternGen.genTrain_XFpPatterns(train, fold);
			
			// load train_x pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// 计算cover instance程度
			for (Pattern pattern : patterns) {
				for (int i = 0; i < test.numInstances(); i++) {
					if (pattern.isFit(test.instance(i))) {
						pattern.incrCoveredU();
					}
				}
				System.out.println("fold-" + fold + ": " + pattern.pId() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
			}
			
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * 评估fp-ig的分类效果
	 * @throws Exception
	 */
	public void evaluateFPIG() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// load train_x pattern（直接利用FP产生的pattern即可）
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// sort patterns according to IG value
			patterns = IgFilter.calculateAndSortByIg(train, patterns);
			
			// 根据fpig_delta选择pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fpig_delta);
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * 评估pu的分类效果
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			
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
				calDx(patterns4Fold4L_x, instanceListL_x, inss);
				
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
					System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx());
				}
			}
			
			// filter by instance coverage
			//patterns = PuFilter.filterByItemCoverage(inss, patterns, Constant.item_coverage);
			patterns = PuFilter.filterByInstanceCoverage(test, patterns, Constant.instance_coverage);
			
			if (Constant.deubg_pattern_filterd) {
				// print patterns
				for (PuPattern pattern : patterns) {
					System.out.println("fold-" + fold + ": " + pattern.pId() + " || D(x)=" + pattern.getDx() + " || coveredU=" + pattern.getCoveredU() + "/" + test.numInstances());
				}
			}
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
			
			// eval for dive analysis
			//eval.eval4Instances(augTrain, augTest, puErrorList);
			
		}
		System.out.println(eval.formatEvalRst());
		System.out.println("\n----------------------------");
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
	
	private void calDx(List<PuPattern> patterns4Fold4L_x, List<Instance> instanceListL_x, Instances inss) {
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
			double dx = pattern.getSuppL1() * Math.log(dSize/pattern.getSuppD()) / lSize;
			pattern.setDx(dx);
		}
	}

	
	public void printErrorDiff() {
		System.out.println("naive classifier errors: ");
		for (Integer i : naiveErrorList) {
			System.out.print(i + ",");
		}
		System.out.println("\n---------------------------");
		System.out.println("pu classifier errors: ");
		for (Integer i : puErrorList) {
			System.out.print(i + ",");
		}
		System.out.println("\n---------------------------");
	}

}
