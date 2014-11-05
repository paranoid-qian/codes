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
	private static BufferedWriter bWriter = null;
	private static Instances inss;
	
	/**
	 * generate global transactions
	 * @throws Exception 
	 */
	public static void genTransaction() throws Exception {
		inss = InstanceLoader.loadInstances();
		if (itemMap == null) {
			itemMap = ItemLoader.loadItems(inss);
		}
		bWriter = new BufferedWriter(new FileWriter(Constant.TRANSACTION_FILE, true)); // append mode
		
		// generate transactions
		for (int i = 0; i < inss.numInstances(); i++) {
			StringBuffer sb = new StringBuffer();
			Instance ins = inss.instance(i);
			for (int j = 0; j < inss.numAttributes()-1; j++) {
				String attr = ins.attribute(j).name();
				String val = ins.stringValue(j);
				/*if (itemMap.get(attr) == null) {
					System.err.println("bug");
				}*/
				if (itemMap.get(attr).containsKey(val)) {
					sb.append(itemMap.get(attr).get(val).getId() + " ");
				}
			}
			bWriter.write(sb.toString());
			bWriter.newLine();
			bWriter.flush();
			
		}
		bWriter.close();
	}
	
	/**
	 * generate training transactions
	 * @param train
	 * @param flag
	 * @param fold
	 * @throws Exception
	 */
	public static void genTrainTransaction(List<Instance> train, int flag, int fold) throws Exception {
		inss = InstanceLoader.loadInstances();
		if (itemMap == null) {
			itemMap = ItemLoader.loadItems(inss);
		}
		
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(Constant.TRAIN_TRANSACTION_FILE_PREFIX+fold+"_c"+flag, true)); // append mode
		
		for (Instance instance : train) {
			int numAttributes = instance.numAttributes();
			StringBuffer sb = new StringBuffer();
			boolean effective = true; // 记录是否含有missing数据
			for (int i = 0; i < numAttributes-1; i++) {
				String attrName = instance.attribute(i).name();
				String attrVal = instance.stringValue(i);
				if (attrVal.equals("?")) {
					effective = false;
					break;
				}
				AttrValEntry e = itemMap.get(attrName).get(attrVal);
				int itemId = e.getId();
				sb.append(itemId + " ");
			}
			if (effective) {
				sb.replace(sb.length()-1, sb.length(), "");
				bWriter.write(sb.toString());
				bWriter.newLine();
			}
		}
		bWriter.flush();
		bWriter.close();
	}
	
	
	public static void main(String[] args) {
		try {
			genTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
