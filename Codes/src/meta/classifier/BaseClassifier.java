package meta.classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.Pattern;
import meta.entity.PuPattern;
import meta.evaluator.Evaluator;
import meta.pattern.FitJudger;
import meta.pattern.IgFilter;
import meta.pattern.PatternGen;
import meta.pattern.PuFilter;
import meta.transaction.TransactionAug;
import meta.transaction.TransactionGen;
import meta.util.constants.Constant;
import meta.util.loader.ItemLoader;
import meta.util.loader.PatternLoader;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class BaseClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = Constant.numFolds;
	
	public BaseClassifier(Classifier classifier) {
		
		super();
		this.classifier = classifier;
		
		// 随机化train和test集
		System.out.println("random: " + Constant.s);
		
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		data.randomize(Constant.rand);
		
		// 输出参数
		System.out.println("min_support: " + Constant.minSupport);
		System.out.println("recall阈值：" + Constant.recall);
		System.out.println("delta: " + Constant.pu_delta);
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
		}
		System.out.println("===不添加pattern的分类结果:===");
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
		
			// 生成train_x上的transaction文件
			TransactionGen.genFpTrainTransaction(train, fold);
			File transFile = null; 
			while (transFile == null) {
				transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			}

			// gen train_x pattern
			PatternGen.genTrain_XFpPatterns(inss, fold);
			
			// load train_x pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		System.out.println("===Fp分类结果:===");
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
		
			// load train_x pattern
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
		
		System.out.println("===Fp-IG分类结果:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * 评估pu的分类效果
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		
		String[] itemCover = new String[10];
		String[] instanceCover = new String[10];
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			
			// 对train划分为多类
			Map<Double, List<Instance>> trainC_XMap = new HashMap<Double, List<Instance>>();
			for (int j = 0; j < train.numInstances(); j++) {
				Instance ins = inss.instance(j);
				if (trainC_XMap.containsKey(ins.classValue())) {
					trainC_XMap.get(ins.classValue()).add(ins);
				} else {
					List<Instance> trainC_X = new ArrayList<Instance>();
					trainC_X.add(ins);
					trainC_XMap.put(ins.classValue(), trainC_X);
				}
			}
			
			Map<String, PuPattern> union = new HashMap<String, PuPattern>();
			// 分别生成transaction文件和pattern文件
			for (Double classVal : trainC_XMap.keySet()) {
				List<Instance> trainC_fit = trainC_XMap.get(classVal);
				List<Instance> trainC_nofit = new ArrayList<Instance>();
				for (Double j : trainC_XMap.keySet()) {
					if (j.compareTo(classVal) != 0) {
						trainC_nofit.addAll(trainC_XMap.get(j));
					}
				}
				
				TransactionGen.genL_X_PuTransactionFile(trainC_fit, fold, classVal);
				File transFile = null;
				while (transFile == null) {
					transFile = new File(Constant.PU_TRAIN_LX_TRANSACTION_FOLDER + fold + Constant.TRANS_PATH + classVal + Constant.TYPE_POSTFIX);
				}
				
				PatternGen.genTrain_XPuPatterns(inss ,fold, classVal);
				List<PuPattern> patternsX = PatternLoader.loadTrain_L_FoldX_PuPatterns(inss, fold, classVal);
				
				// filter, 根据nofit和fit共同来过滤pattern
				patternsX = PuFilter.filter(trainC_fit, trainC_nofit, patternsX, Constant.recall);
				
				// 求所有L_x上pattern的并集
				for (PuPattern pat : patternsX) {
					if (!union.containsKey(pat.pName())) {
						union.put(pat.pName(), pat);
					}
				}
			}
			List<PuPattern> patterns = new ArrayList<PuPattern>(union.values());
			
			// 根据supportU排序
			patterns = PuFilter.CalculateAndSortBySuppU(patterns, test);
			
			// 根据coverage选取pattern
			patterns = PuFilter.filterByCoverage(train, patterns, Constant.pu_delta);
			
//			if (Constant.debug_pu_pattern) {
//				System.out.println("-------------------------");
//				System.out.println("pattern | suppL1 | suppL0 | suppU");
//				for (PuPattern pattern : patterns) {
//					System.out.println(pattern.pItems() + " | " + pattern.getSuppL1() + " | " + pattern.getSuppL0() + " | " + pattern.getSuppU());
//				}
//			}
			//*********************************************************************************************
			final int itemCoverTimesDelta = 1;
			// 统计item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// 统计item的cover程度
			
			int itemTotal = itemList.size();
			for (PuPattern pattern : patterns) {
				for (AttrValEntry entry : pattern.entrys()) {
					if (!itemCountMap.containsKey(entry.getId())) {
						itemCountMap.put(entry.getId(), 1);
					} else {
						itemCountMap.put(entry.getId(), itemCountMap.get(entry.getId())+1);
					}
				}
			}
			int coverage = 0;
			for (AttrValEntry entry : itemList) {
				if (!itemCountMap.containsKey(entry.getId())) {
					continue;
				}
				int count = itemCountMap.get(entry.getId());
				if (count >= itemCoverTimesDelta) {
					coverage++;
				}
			}
			itemCover[fold] = "item coverage: " + coverage + " (" + ((double)coverage/itemTotal) + ")";
			
			// 统计instance coverage
			final int insCoverTimesDelta = 1;
			coverage = 0;
			for (int i = 0; i < train.numInstances(); i++) {
				int coverNPattern = 0;
				Instance instance = train.instance(i);
				for (PuPattern pattern : patterns) {
					if (FitJudger.isFit(instance, pattern)) {
						coverNPattern++;
					}
				}
				if (coverNPattern >= insCoverTimesDelta) {
					coverage++;
				}
				
			}
			instanceCover[fold] = "instance coverage(fold_"+ fold +"): " + coverage + " (" + ((double)coverage/train.numInstances()) + ")";
			
			// 增广instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		
		// 输出instance和item的coverage
		for (String insC : instanceCover) {
			System.out.println(insC);
		}
		for (String itemC : itemCover) {
			System.out.println(itemC);
		}
		System.out.println("===Pu分类结果:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
/*	*//**
	 * 计算cp的coverage
	 * @throws Exception
	 *//*
	public void evaluateCP() throws Exception {
		Instances inss = new Instances(data);
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			
		
			// 生成train_1上的transaction文件
			TransactionGen.genCpTrainTransaction(train, fold);
			// 在train_1上挖closed frequent pattern 
			File transFile = new File(Constant.CP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("第"+ fold +"折的trainsL1 文件生成！");
					break;
				}
			}

			// load L1上生成的patterns
			List<PuPattern> patterns1 = PatternGen.genTrain_XCpPatterns(inss, fold);
			//*********************************************************************************************
			final int itemCoverTimesDelta = 1;
			// 统计item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// 统计item的cover程度
			
			int itemTotal = itemList.size();
			for (PuPattern pattern : patterns1) {
				for (AttrValEntry entry : pattern.entrys()) {
					if (!itemCountMap.containsKey(entry.getId())) {
						itemCountMap.put(entry.getId(), 1);
					} else {
						itemCountMap.put(entry.getId(), itemCountMap.get(entry.getId())+1);
					}
				}
			}
			int coverage = 0;
			for (AttrValEntry entry : itemList) {
				if (!itemCountMap.containsKey(entry.getId())) {
					continue;
				}
				int count = itemCountMap.get(entry.getId());
				if (count >= itemCoverTimesDelta) {
					coverage++;
				}
			}
			System.out.println("item coverage: " + coverage + " (" + ((double)coverage/itemTotal) + ")");
			//*********************************************************************************************
		}
	}*/
	
	
	
}
