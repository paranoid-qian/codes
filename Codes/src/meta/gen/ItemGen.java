package meta.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import meta.util.constants.Constant;
import weka.core.Instances;
import weka.core.Attribute;


/**
 * 生成attribute-value对，并把对写入到文件中
 * @author paranoid
 *
 */
public class ItemGen {
	
	// item id
	private static int incrementId = 0;
	
	/**
	 * Every attribute stores in one file named attr_pairs.txt.
	 * in every txt file, one line stores a corresponding value and its incrmentId
	 * e.g:	gender_pairs.txt
	 * 		male|0
	 * 		female|1
	 * @param inss
	 * @return item count
	 * @throws Exception
	 */
	public static int genItems(Instances inss) throws Exception {
		// 遍历所有attributes
		int numAttributes = inss.numAttributes();
		for (int i = 0; i < numAttributes; i++) {
			Attribute attribute = inss.attribute(i);
			String attrName = attribute.name();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(Constant.ITEMS_FOLDER + attrName + Constant.ITEM_FILE_POSTFIX))); // append mode
			StringBuilder sb = new StringBuilder();
			int numVals = attribute.numValues();
			for (int j = 0; j < numVals; j++) {
				String attrVal = attribute.value(j);
				sb.append(attrVal + Constant.SP + incrementId++ + "\n");
			}
			bWriter.write(sb.toString());
			bWriter.flush();
			bWriter.close();
		}
		return incrementId;
	}

}
