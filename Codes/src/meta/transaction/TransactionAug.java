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
	 * augment dataset according to filtered patterns
	 * @param patterns
	 * @param instances
	 * @return
	 * @throws IOException 
	 */
	public static Instances augmentDataset(List<Pattern> patterns, Instances instances) throws IOException {
		Instances copy = new Instances(instances);
		
		int newAttrStart = copy.numAttributes() - 1;
		
		// insert new attributes(patterns)
		for (Pattern pattern : patterns) {
			copy.insertAttributeAt(new Attribute(pattern.pName(), fv), copy.numAttributes()-1);
		}
		// insert corresponding attribute values(pattern values)
		for (int i = 0; i < copy.numInstances(); i++) {
			Instance ins = copy.instance(i);
			int index = newAttrStart;
			for (Pattern pattern : patterns) {
				ins.setValue(index++, pattern.pValue(ins));
			}
		}
		return copy;
	}
	
}
