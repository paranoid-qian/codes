package meta.transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import meta.entity.AttrValEntry;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import meta.util.loader.ItemLoader;
import weka.core.Instance;
import weka.core.Instances;

public class TransactionGen {
	
	private static Map<String, Map<String, AttrValEntry>> itemMap = null;
	private static Instances inss;
	
	/**
	 * updated 11.7
	 * @param trainC_X
	 * @param fold
	 * @param c_1ORc_0
	 * @throws Exception
	 */
	public static void genL_X_PuTransactionFile(List<Instance> trainC_X, int fold, int c_1ORc_0) throws Exception {
		inss = InstanceLoader.loadInstances();
		if (itemMap == null) {
			itemMap = ItemLoader.loadItems(inss);
		}
		String prefix = Constant.PU_TRAIN_L0_TRANSACTION_FOLDER;
		if (c_1ORc_0 == 1) {
			prefix = Constant.PU_TRAIN_L1_TRANSACTION_FOLDER;
		}
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(prefix + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX)); 
		
		for (Instance instance : trainC_X) {
			int numAttributes = instance.numAttributes();
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < numAttributes-1; i++) {
				String attrName = instance.attribute(i).name();
				String attrVal = instance.stringValue(i);
				AttrValEntry e = itemMap.get(attrName).get(attrVal);
				if (e != null) {
					int itemId = e.getId();
					sb.append(itemId + " ");
				}
			}
			sb.replace(sb.length()-1, sb.length(), "");
			bWriter.write(sb.toString());
			bWriter.newLine();
		}
		bWriter.flush();
		bWriter.close();
	}
	
	/**
	 * generate training transactions
	 * @param train
	 * @param flag
	 * @param fold
	 * @throws Exception
	 */
	public static void genFpTrainTransaction(Instances train, int fold) throws Exception {
		inss = InstanceLoader.loadInstances();
		if (itemMap == null) {
			itemMap = ItemLoader.loadItems(inss);
		}
		
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.FP_TRAIN_TRANSACTION_FOLDER + Constant.FOLD_PATH + fold + Constant.TYPE_POSTFIX)); 
		
		for (int i=0; i< train.numInstances(); i++) {
			Instance instance = train.instance(i);
			
			int numAttributes = instance.numAttributes();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < numAttributes-1; j++) {
				String attrName = instance.attribute(j).name();
				String attrVal = instance.stringValue(j);
				AttrValEntry e = itemMap.get(attrName).get(attrVal);
				if (e != null) {
					int itemId = e.getId();
					sb.append(itemId + " ");
				}
			}
			sb.replace(sb.length()-1, sb.length(), "");
			bWriter.write(sb.toString());
			bWriter.newLine();
		}
		bWriter.flush();
		bWriter.close();
	}
	
}
