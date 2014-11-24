package meta.classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.entity.Pattern;
import meta.entity.PuPattern;
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
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import wlsvm.WLSVM;

public class PuClassifier extends AbstractClassifier{
	
	private Classifier classifier;
	private int numFolds = Constant.numFolds;
	
	public PuClassifier(Classifier classifier) {
		
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
		System.out.println("delta: " + Constant.delta);
	}
	
	
	/**
	 * ����û�����pattern�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateOrigin() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0, 0};
		double[] recall = {0, 0, 0};
		double[] fMeasure = {0, 0, 0};
		
		// ������������
		Evaluation eval = new Evaluation(inss);
		for (int i = 0; i < numFolds; i++) {
			Instances test = inss.trainCV(numFolds, i);
			Instances train = inss.testCV(numFolds, i);
			// evaluate
			try {
				Classifier cls = Classifier.makeCopy(classifier);
				cls.buildClassifier(train);
				eval.evaluateModel(cls, test);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			precision[2] += eval.weightedPrecision();
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			recall[2] += eval.weightedRecall();
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
			fMeasure[2] += eval.weightedFMeasure();
		}
		
		if (Constant.debug_origin_summary) {
			System.out.println(eval.toClassDetailsString());
		}
		
		System.out.println("\n\nPrecision\t\tRecall\t\tF-Measure");
		System.out.println("===================================================");
		System.out.println("�����pattern�ķ�����:");
		//System.out.println("precision\t recall\t fMeasure");
		//System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
		//System.out.println(Utils.doubleToString(precision[1]/numFolds, 7 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
		System.out.println(Utils.doubleToString(precision[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[2]/numFolds, 7, 4));
		
	}
	
	
	/**
	 * ����fp�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateFP() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// ����train_1�ϵ�transaction�ļ�
			TransactionGen.genFpTrainTransaction(train, fold);
			// ��train_1����closed frequent pattern 
			File transFile = new File(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}

			// load train�����ɵ�pattern
			List<Pattern> patterns = PatternGen.genTrain_XFpPatterns(inss, fold);
			
			//*******************pattern all**********************************//
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			// pat_all����
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			precision[0] += eval.weightedPrecision();
			recall[0] += eval.weightedRecall();
			fMeasure[0] += eval.weightedFMeasure();
			
		}
		System.out.println("===================================================");
		System.out.println("Fp������:");
		System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
	}
	
	
	/**
	 * ����fp�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluateFPIG() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0};
		double[] recall = {0, 0};
		double[] fMeasure = {0, 0};
		
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
		
			// load train�����ɵ�pattern
			List<Pattern> patterns = PatternLoader.loadTrain_FoldX_FpPatterns(inss, fold);
			
			//*******************pattern fs**********************************//
			// ����ig����patterns
			patterns = IgFilter.calculateAndSortByIg(train, patterns);
			// ����deltaѡ��pattern
			patterns  = IgFilter.filterByCoverage(train, patterns, Constant.fp_delta);
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns, train);
			Instances augTest = TransactionAug.augmentDataset(patterns, test);
			// pat_fs����
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			precision[1] += eval.weightedPrecision();
			recall[1] += eval.weightedRecall();
			fMeasure[1] += eval.weightedFMeasure();
			
		}
		
		System.out.println("===================================================");
		System.out.println("Fp_fs������:");
		System.out.println(Utils.doubleToString(precision[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
	}
	
	
	/**
	 * ����pu�ķ���Ч��
	 * @throws Exception
	 */
	public void evaluatePu() throws Exception {
		Instances inss = new Instances(data);
		
		double[] precision = {0, 0, 0};
		double[] recall = {0, 0, 0};
		double[] fMeasure = {0, 0, 0};
		
		String[] itemCover = new String[10];
		String[] instanceCover = new String[10];
		
		for (int fold = 0; fold < numFolds; fold++) {
			Instances test = inss.trainCV(numFolds, fold);	// 90%
			Instances train = inss.testCV(numFolds, fold); 	// 10%
			
			// ��train����0/1����
			List<Instance> trainC_0 = new ArrayList<Instance>();
			List<Instance> trainC_1 = new ArrayList<Instance>();
			for (int j = 0; j < train.numInstances(); j++) {
				Instance ins = train.instance(j);
				if (ins.classValue() == 0.0) {
					trainC_0.add(ins);
				} else {
					trainC_1.add(ins);
				}
				
			}
			
			// ����train_1��train_0�ϵķֱ�����transaction�ļ�
			TransactionGen.genL_X_PuTransactionFile(trainC_1, fold, 1);
			TransactionGen.genL_X_PuTransactionFile(trainC_0, fold, 0);

			// ��train_1����closed frequent pattern 
			File transFile = new File(Constant.PU_TRAIN_L1_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}
			List<PuPattern> patterns1 = PatternGen.genTrain_XPuPatterns(inss ,fold, 1);
			
			// ��train_1����closed frequent pattern 
			transFile = new File(Constant.PU_TRAIN_L0_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX);
			while (true) {
				if (transFile.exists()) {
					//System.out.println("��"+ fold +"�۵�trainsL1 �ļ����ɣ�");
					break;
				}
			}
			List<PuPattern> patterns0 = PatternGen.genTrain_XPuPatterns(inss ,fold, 0);
			
			/*// ����pattern��suppL0��suppL1
			for (Instance ins : trainC_1) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1��instance����pattern��suppL1++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL1();		// L1��instance����pattern��suppL1++
					}
				}
			}
			for (Instance ins : trainC_0) {
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0��instance����pattern��suppL0++
					}
				}
				for (PuPattern pattern : patterns0) {
					if (FitJudger.isFit(ins, pattern)) {
						pattern.incrSuppL0();		// L0��instance����pattern��suppL0++
					}
				}
			}*/
			
			// filter
			patterns1 = PuFilter.filter1(trainC_1, trainC_0, patterns1, test, Constant.recall);
			patterns0 = PuFilter.filter0(trainC_1, trainC_0, patterns0, test, Constant.recall);
			// ��L0pattern��L1pattern�Ĳ���
			Map<String, PuPattern> union = new HashMap<String, PuPattern>();
			for (PuPattern pat : patterns1) {
				union.put(pat.pName(), pat);
			}
			for (PuPattern pat : patterns0) {
				if (!union.containsKey(pat.pName())) {
					union.put(pat.pName(), pat);
				}
			}
			patterns1 = new ArrayList<PuPattern>(union.values());
			
			/*for (PuPattern puPattern : patterns1) {
				System.out.println(puPattern.pItems());
			}*/
			
			// ����supportU����
			patterns1 = PuFilter.CalculateAndSortBySuppU(patterns1, test);
			
			// ����coverageѡȡpattern
			patterns1 = PuFilter.filterByCoverage(train, patterns1, Constant.delta);
			
			if (Constant.debug_pu_pattern) {
				System.out.println("-------------------------");
				System.out.println("pattern | suppL1 | suppL0 | suppU");
				for (PuPattern pattern : patterns1) {
					System.out.println(pattern.pItems() + " | " + pattern.getSuppL1() + " | " + pattern.getSuppL0() + " | " + pattern.getSuppU());
				}
			}
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
			itemCover[fold] = "item coverage: " + coverage + " (" + ((double)coverage/itemTotal) + ")";
			
			// ͳ��instance coverage
			final int insCoverTimesDelta = 1;
			coverage = 0;
			for (int i = 0; i < train.numInstances(); i++) {
				int coverNPattern = 0;
				Instance instance = train.instance(i);
				for (PuPattern pattern : patterns1) {
					if (FitJudger.isFit(instance, pattern)) {
						coverNPattern++;
					}
				}
				if (coverNPattern >= insCoverTimesDelta) {
					coverage++;
				}
				
			}
			instanceCover[fold] = "instance coverage(fold_"+ fold +"): " + coverage + " (" + ((double)coverage/train.numInstances()) + ")";
	//*********************************************************************************************
			
			// ����instance
			Instances augTrain = TransactionAug.augmentDataset(patterns1, train);
			Instances augTest = TransactionAug.augmentDataset(patterns1, test);
			
			// ����
			Evaluation eval = new Evaluation(augTrain);
			Classifier cls = Classifier.makeCopy(this.classifier);
			cls.buildClassifier(augTrain);
			eval.evaluateModel(cls, augTest);
			
			precision[0] += eval.precision(0);
			precision[1] += eval.precision(1);
			precision[2] += eval.weightedPrecision();
			
			recall[0] += eval.recall(0);
			recall[1] += eval.recall(1);
			recall[2] += eval.weightedRecall();
			
			fMeasure[0] += eval.fMeasure(0);
			fMeasure[1] += eval.fMeasure(1);
			fMeasure[2] += eval.weightedFMeasure();
		}
		
		// ���instance��item��coverage
		for (String insC : instanceCover) {
			System.out.println(insC);
		}
		for (String itemC : itemCover) {
			System.out.println(itemC);
		}
		
		System.out.println("===================================================");
		System.out.println("Pu������:");
		//System.out.println("precision\t recall\t fMeasure");
		//System.out.println(Utils.doubleToString(precision[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[0]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[0]/numFolds, 7, 4));
		//System.out.println(Utils.doubleToString(precision[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[1]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[1]/numFolds, 7, 4));
		System.out.println(Utils.doubleToString(precision[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(recall[2]/numFolds, 7, 4) + "\t\t" + Utils.doubleToString(fMeasure[2]/numFolds, 7, 4));
	}
	
	
	/**
	 * ����cp��coverage
	 * @throws Exception
	 */
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
	}
	
	public static void main(String[] args) {
		PuClassifier eval = new PuClassifier(new WLSVM());
		try {
			eval.evaluateOrigin();
			eval.evaluateFP();
			eval.evaluateFPIG();
			eval.evaluatePu();
			//c45.evaluateCP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
