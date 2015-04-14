package meta.util.constants;

import java.util.Random;
public class Constant {
	
	public static final String TOP_FOLDER = "E:\\pulearning_codes_2015\\naivebayes\\";
	public static final String DATASET = "contraceptive";	
	/* 随机种子 */
	public static final long s = System.currentTimeMillis();
	public static final Random rand = new Random(1429013465476L); // 1415678605768L/1415678962610L
	
	
	// 折数 
	public static final int numFolds = 5;
	// 挖CPF时：min_support
	public static final int minSupport = 30;	
	
	// pattern至少要包含几个item
	public static final int itemMinCount = 2;
	public static final int puItemMinCount = 3;
	
	// item coverage
	public static final double item_coverage = 1;
	
	
	//public static final double recall = 0.4;	// 过滤CFP：recall
	public static final int pu_delta = 1;		// U过滤时：pu_delta
	public static final int fpig_delta = 1;		// fp-ig方法的delta
	
	
	/* instance coverage */
	public static final double instance_coverage = 0.90;
	public static final int coverage_delta = 1;	// 至少有coverage_delta次coverage，才算一个instance被coverage
	
	public static final boolean debug_pattern_dx = false;	// 开启后输出pattern和相应的dx
	public static final boolean deubg_pattern_filterd = false;	// 开启后输出pattern（过滤后）
	public static final boolean debug_item_coverage = false;	// 开启后输出item_coverage值
	
	
//	// cp
//	public static final double minCosine = 0.2;
//	public static final double minCosineSupport = 0;
	
	/* DEBUG */
	
	
	public static final boolean debug_pu_pattern = false;		/* 开启后输出具体的pu方法的每折pattern */
	public static final boolean debug_origin_summary = false;	/* 开启后输出eval的summary */
	public static final boolean debug_cmd_pu = false;			/* 开启后输出挖掘CFP时的cmd命令 */
	public static final boolean debug_fp_ig = false;			/* 开启后输出具体的fp-ig方法的每折pattern */
	
	/* pu trans and pattern */
	public static final String PU_TRAIN_LX_TRANSACTION_FOLDER = TOP_FOLDER + DATASET +"\\pu\\fold_";
	public static final String PU_TRAIN_LX_PATTERN_FILE_FOLDER = TOP_FOLDER + DATASET +"\\pu\\fold_";
	
	public static final String TRANS_PATH = "\\transL_";
	public static final String PATS_PATH = "\\patternL_";
	
	public static final String FOLD_PATH = "\\fold_";
	public static final String TYPE_POSTFIX = ".txt";
	
	/* fp trans and pattern */
	public static final String FP_TRAIN_TRANSACTION_FOLDER = TOP_FOLDER+ DATASET +"\\fp\\trans";
	public static final String FP_TRAIN_PATTERN_FOLDER = TOP_FOLDER+ DATASET +"\\fp\\pattern";
	
	/* fp ig trans and pattern，可以直接利用fp的*/
	public static final String FP_IG_TRAIN_TRANSACTION_FOLDER = FP_TRAIN_TRANSACTION_FOLDER; //TOP_FOLDER+ DATASET +"\\fpig\\trans";
	public static final String FP_IG_TRAIN_PATTERN_FOLDER = FP_TRAIN_PATTERN_FOLDER; //TOP_FOLDER+ DATASET +"\\fpig\\pattern";
	
//	public static final String CP_TRAIN_TRANSACTION_FOLDER = TOP_FOLDER + DATASET +"\\cp\\trans";
//	public static final String CP_TRAIN_PATTERN_FOLDER = TOP_FOLDER + DATASET +"\\cp\\pattern";
	
	//===============================================================================================================================
	
	
	/* arff 文件*/
	public static final String DATASET_ARFF = TOP_FOLDER +"..\\"+ DATASET +".arff";
	
	/* attribute value pair (item) file open path */
	public static final String ITEMS_FOLDER = TOP_FOLDER + DATASET +"\\items\\";
	public static final String ITEM_FILE_POSTFIX = "_pairs.txt";
	
	/* augment values */
	public static final String FIT = "1";
	public static final String NO_FIT = "0";
	
	public static final String ARFF_POSTFIX = ".arff";
	
	/* others constant */ 
	public static final String DOT = ".";
	public static final String SP = "|";
	public static final String ADD = "+";
	
}
