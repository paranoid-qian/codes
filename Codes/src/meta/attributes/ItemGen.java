package meta.attributes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;


/**
 * ����attr-val�ԣ����Ѷ�д�뵽�ļ���
 * @author paranoid
 *
 */
public class ItemGen {
	
	private static int incrementId = 0;
	private static BufferedWriter bWriter = null;
	private static Instances inss;
	
	/**
	 * ��ɢ���Եķ��������γ�value-id��
	 * @throws Exception
	 */
	public static void genItems() throws Exception {
		inss = InstanceLoader.loadInstances();
		
		Map<String, Map<String, Integer>> itemsMap = new HashMap<String, Map<String,Integer>>();
		// ����dataset�����е�instances
		for (int i = 0; i < inss.numInstances(); i++) {
			Instance ins = inss.instance(i);
			
			// ������instance��attributes-values
			for (int j = 0; j < inss.numAttributes()-1; j++) {
				String attr = inss.attribute(j).name();
				String val = ins.stringValue(j);
				
				if (!itemsMap.containsKey(attr)) {
					Map<String, Integer> valIdMap = new HashMap<String, Integer>();
					valIdMap.put(val, incrementId++);
					itemsMap.put(attr, valIdMap);
				} else {
					Map<String, Integer> valIdMap = itemsMap.get(attr);
					if (!valIdMap.containsKey(val)) {
						valIdMap.put(val, incrementId++);
					}
				}
			}
		}
		
		
		// output items
		BufferedWriter allBWriter = new BufferedWriter(new FileWriter(new File(Constant.ITEMS_FOLDER + "_ALL" + Constant.ITEM_FILE_POSTFIX), true)); // append mode
		for (String attr : itemsMap.keySet()) {
			bWriter = new BufferedWriter(new FileWriter(new File(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX), true)); // append mode
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			for (Entry<String, Integer> entry : itemsMap.get(attr).entrySet()) {
				sb.append(entry.getKey() + Constant.SP + entry.getValue() + "\n");
				sb2.append(attr + Constant.SP + entry.getKey() + Constant.SP + entry.getValue() + "\n");
			}
			bWriter.write(sb.toString());
			bWriter.flush();
			bWriter.close();
			
			// write all to one file
			allBWriter.write(sb2.toString());
			allBWriter.flush();
		}
		allBWriter.close();
		//System.out.println(dataset);
	}
	
	
	public static void main(String[] args) {
		try {
			genItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
