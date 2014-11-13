package meta.util.constants;

import java.util.Random;

public interface Constant {
	
	public static final String DATASET = "credit";
	
	public static final int minSupport = 50;
	public static final double recall = 0.6;
	public static final int delta = 1;
	
	
	/* 调试用，开启后输出具体的每折pattern */
	public static final boolean debug_pu_pattern = false;
	public static final boolean debug_origin_summary = false;
	
	/* 随机种子 */
	public static final long s = System.currentTimeMillis();
	public static final Random rand = new Random(s); // 1415678605768L/1415678962610L
	
	/* pu trans and pattern */
	public static final String PU_TRAIN_L1_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\transL1";
	public static final String PU_TRAIN_L0_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\transL0";
	public static final String PU_TRAIN_L0_PATTERN_FILE_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\patternL0";
	public static final String PU_TRAIN_L1_PATTERN_FILE_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\patternL1";
	//public static final String TEST_TRANSACTION_PATTERNS_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\pu\\test\\";
	
	public static final String FOLD_PATH = "\\fold_";
	public static final String TYPE_POSTFIX = ".txt";
	
	/* fp trans and pattern */
	public static final String FP_TRAIN_TRANSACTION_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fp\\trans";
	public static final String FP_TRAIN_PATTERN_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\fp\\pattern";
	
	//===============================================================================================================================
	
	
	/* arff 文件*/
	public static final String DATASET_ARFF = "E:\\weka\\dataset\\"+ DATASET +"\\"+ DATASET +".arff";
	
	/* attribute value pair (item) file open path */
	public static final String ITEMS_FOLDER = "E:\\weka\\dataset\\"+ DATASET +"\\items\\";
	public static final String ITEM_FILE_POSTFIX = "_pairs.txt";
	
	/* transaction file output path */
	//public static final String TRANSACTION_FILE = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\transaction.txt";
	
	
	/* augment values */
	public static final String FIT = "fit";
	public static final String NO_FIT = "no-fit";
	
	public static final String ARFF_POSTFIX = ".arff";
	
	
	/* others */ 
	public static final String DOT = ".";
	public static final String SP = "|";
	public static final String ADD = "+";
	
}
