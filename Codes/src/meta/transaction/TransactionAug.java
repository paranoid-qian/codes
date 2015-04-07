package meta.transaction;

import java.io.IOException;
import java.util.List;

import meta.entity.Pattern;
import meta.util.constants.Constant;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class TransactionAug {
	
	
	private static FastVector fv = new FastVector();
	static {
		fv.addElement(Constant.FIT);
		fv.addElement(Constant.NO_FIT);
	}
	
	/**
	 * 扩展pattern到instances中
	 * @param patterns
	 * @param instances
	 * @return
	 * @throws IOException 
	 */
	public static Instances augmentDataset(List<? extends Pattern> patterns, Instances instances) throws IOException {
		Instances copy = new Instances(instances);
		
		int newAttrStart = copy.numAttributes() - 1;
		
		// 插入新attributes（即pattern）
		for (Pattern pattern : patterns) {
			copy.insertAttributeAt(new Attribute(pattern.pName(), fv), copy.numAttributes()-1);
		}
		// 插入新的attributes values
		for (int i = 0; i < copy.numInstances(); i++) {
			
			Instance ins = copy.instance(i);
			int index = newAttrStart;
			for (Pattern pattern : patterns) {
				String isFit = pattern.pValue(ins);
				ins.setValue(index++, isFit);
			}
		}
		return copy;
	}
	
}
