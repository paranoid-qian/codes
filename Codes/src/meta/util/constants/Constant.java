package meta.util.constants;

import meta.util.constants.table.BreastCancerTable;
import meta.util.constants.table.DbTable;

public interface Constant {
	
	/* db table */
	public static final String DB_NAME = "breast_cancer";
	public static final DbTable DB_TABLE = new BreastCancerTable();
	
	/* arff */
	public static final String DATASET_ARFF = "E:\\weka\\dataset\\uci-20070111\\nominal\\breast-cancer.arff";
	
	/* attribute value pair (item) file open path */
	public static final String ITEMS_FOLDER = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\attr-pairs\\";
	
	/* transaction file output path */
	public static final String TRANSACTION_FILE = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\transaction.txt";
	
	/* pattern file path */
	public static final String PATTRN_FILE = "E:\\weka\\dataset\\0-1-dataset\\breast-cancer\\pattern.txt";
	
	/* others */ 
	public static final String DOT = ".";
	public static final String SP = "|";
	public static final String ITEM_FILE_POSTFIX = "_pairs.txt";
}
