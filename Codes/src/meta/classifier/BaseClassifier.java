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
		
		// �����train��test��
		System.out.println("random: " + Constant.s);
		
		if (data.classAttribute().isNominal()) {
			data.stratify(numFolds);
		}
		data.randomize(Constant.rand);
		
		// �������
		System.out.println("min_support: " + Constant.minSupport);
		System.out.println("recall��ֵ��" + Constant.recall);
		System.out.println("delta: " + Constant.pu_delta);
	}
	
	
	/**
	 * ����û�����pattern�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		// ������������
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			eval.eval(train, test);
		}
		System.out.println("===�����pattern�ķ�����:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * ����fp�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateFP() throws Exception {
		Instances inss = new Instances(data);
		
		Evaluator eval = Evaluator.newEvaluator(this.classifier);
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// ����train_x�ϵ�transaction�ļ�
			TransactionGen.genFpTrainTransaction(train, fold);
			File transFile = null; 
			while (transFile == null) {
				transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			}

			// gen train_x pattern
			PatternGen.genTrain_XFpPatterns(inss, fold);
			
			// load train_x pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		System.out.println("===Fp������:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * ����fp-ig�ķ���Ч��
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
			
			// ����fpig_deltaѡ��pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fpig_delta);
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		
		System.out.println("===Fp-IG������:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
	/**
	 * ����pu�ķ���Ч��
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
			
			// ��train����Ϊ����
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
			// �ֱ�����transaction�ļ���pattern�ļ�
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
				
				// filter, ����nofit��fit��ͬ������pattern
				patternsX = PuFilter.filter(trainC_fit, trainC_nofit, patternsX, Constant.recall);
				
				// ������L_x��pattern�Ĳ���
				for (PuPattern pat : patternsX) {
					if (!union.containsKey(pat.pName())) {
						union.put(pat.pName(), pat);
					}
				}
			}
			List<PuPattern> patterns = new ArrayList<PuPattern>(union.values());
			
			// ����supportU����
			patterns = PuFilter.CalculateAndSortBySuppU(patterns, test);
			
			// ����coverageѡȡpattern
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
			// ͳ��item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// ͳ��item��cover�̶�
			
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
			
			// ͳ��instance coverage
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
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			
			// evaluate
			eval.eval(augTrain, augTest);
		}
		
		// ���instance��item��coverage
		for (String insC : instanceCover) {
			System.out.println(insC);
		}
		for (String itemC : itemCover) {
			System.out.println(itemC);
		}
		System.out.println("===Pu������:===");
		System.out.println(eval.formatEvalRst());
	}
	
	
/*	*//**
	 * ����cp��coverage
	 * @throws Exception
	 *//*
	public void evaluateCP() throws Exception {
		Instances inss = new Instances(data);
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			
		
			// ����train_1�ϵ�transaction�ļ�
			TransactionGen.genCpTrainTransaction(train, fold);
			// ��train_1����closed frequent pattern 
			File transFile = new File(Constant.CP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}

			// load L1�����ɵ�patterns
			List<PuPattern> patterns1 = PatternGen.genTrain_XCpPatterns(inss, fold);
			//*********************************************************************************************
			final int itemCoverTimesDelta = 1;
			// ͳ��item coverage]
			List<AttrValEntry> itemList = new ArrayList<AttrValEntry>(ItemLoader.loadItemsByReverse(inss).values());
			Map<Integer, Integer> itemCountMap = new HashMap<Integer, Integer>();	// ͳ��item��cover�̶�
			
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
