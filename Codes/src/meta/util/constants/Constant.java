package meta.util.constants;

import meta.util.constants.table.BreastCancerTable;
import meta.util.constants.table.DbTable;

public interface Constant {
	
	/* db table */
	public static final String DB_NAME = "credit";
	public static final DbTable DB_TABLE = new BreastCancerTable();
	
	/* arff */
	public static final String DATASET_ARFF = "E:\\weka\\dataset\\0-1-dataset\\credit\\credit-g.arff";
	
	/* attribute value pair (item) file open path */
	public static final String ITEMS_FOLDER = "E:\\weka\\dataset\\0-1-dataset\\credit\\items\\";
	
	/* transaction file output path */
	public static final String TRANSACTION_FILE = "E:\\weka\\dataset\\0-1-dataset\\credit\\transaction.txt";
	
	/* train transaction file output path */
	public static final String TRAIN_TRANSACTION_FILE_PREFIX = "E:\\weka\\dataset\\0-1-dataset\\credit\\cosineTrans\\train_trans_fold_";
	public static final String TEST_TRANSACTION_FILE_PREFIX = "E:\\weka\\dataset\\0-1-dataset\\credit\\cosineTrans\\test_trans_fold_";
	public static final String TRAIN_TRANSACTION_PATTERNS_FOLDER = "E:\\weka\\dataset\\0-1-dataset\\credit\\cosineTrans\\patterns\\";
	
	/* pattern file path */
	public static final String FP_PATTRN_FILE = "E:\\weka\\dataset\\0-1-dataset\\credit\\fp\\fp_pattern.txt";
	public static final String FP_PATTERN_FOLEDR = "E:\\weka\\dataset\\0-1-dataset\\credit\\fp\\";
	
	/* cosine pattern file path */
	public static final String COSINE_PATTERN_FILE = "E:\\weka\\dataset\\0-1-dataset\\credit\\cosine\\cosine_pattern.txt";
	public static final String COSINE_PATTERN_FOLEDR = "E:\\weka\\dataset\\0-1-dataset\\credit\\cosine\\";
	
	/* augment values */
	public static final String FIT = "fit";
	public static final String NO_FIT = "no-fit";
	
	public static final String ARFF_POSTFIX = ".arff";
	
	/* topk patterns */
	public static final int TOP_K = 1;
	
	/* others */ 
	public static final String DOT = ".";
	public static final String SP = "|";
	public static final String ADD = "+";
	public static final String ITEM_FILE_POSTFIX = "_pairs.txt";
	
}
