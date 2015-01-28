package meta.util.constants;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import wlsvm.WLSVM;

public interface Constant {
	
	public static final String DATASET ="car";
	
	public static final Classifier CLASSIFIER = new WLSVM();
	
	public static final int minSupport = 60;
	public static final double recall = 0.4;
	public static final int delta = 1;
	public static final int fp_delta = 1;
	
	//����
	public static final int numFolds = 10;
	
	// cp
	public static final double minCosine = 0.2;
	public static final double minCosineSupport = 0;
	
	/* �����ã���������������ÿ��pattern */
	public static final boolean debug_pu_pattern = false;
	public static final boolean debug_origin_summary = false;
	public static final boolean debug_cmd_pu = false;
	public static final boolean debug_fp_ig = false;
	
	/* ������� */
	public static final long s = System.currentTimeMillis();
	public static final Random rand = new Random(s); // 1415678605768L/1415678962610L
	
	/* pu trans and pattern */
	public static final String PU_TRAIN_LX_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\fold_";
	public static final String PU_TRAIN_LX_PATTERN_FILE_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\fold_";
	//public static final String TEST_TRANSACTION_PATTERNS_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\test\\";
	
	public static final String TRANS_PATH = "\\transL_";
	public static final String PATS_PATH = "\\patternL_";
	
	public static final String FOLD_PATH = "\\fold_";
	public static final String TYPE_POSTFIX = ".txt";
	
	/* fp trans and pattern */
	public static final String FP_TRAIN_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fp\\trans";
	public static final String FP_TRAIN_PATTERN_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fp\\pattern";
	
	/* fp ig trans and pattern */
	public static final String FP_IG_TRAIN_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fpig\\trans";
	public static final String FP_IG_TRAIN_PATTERN_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fpig\\pattern";
	
	public static final String CP_TRAIN_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\cp\\trans";
	public static final String CP_TRAIN_PATTERN_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\cp\\pattern";
	
	//===============================================================================================================================
	
	
	/* arff �ļ�*/
	public static final String DATASET_ARFF = "E:\\weka\\dataset\\"+ DATASET +"\\"+ DATASET +".arff";
	
	/* attribute value pair (item) file open path */
	public static final String ITEMS_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\items\\";
	public static final String ITEM_FILE_POSTFIX = "_pairs.txt";
	
	/* transaction file output path */
	//public static final String TRANSACTION_FILE = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\transaction.txt";
	
	
	/* augment values */
	public static final String FIT = "1";
	public static final String NO_FIT = "0";
	
	public static final String ARFF_POSTFIX = ".arff";
	
	
	/* others */ 
	public static final String DOT = ".";
	public static final String SP = "|";
	public static final String ADD = "+";
	
}
