package meta.transaction;

import java.util.List;

import meta.entity.Pattern;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class TransactionAug {
	
	
	/**
	 * augment dataset according to filtered patterns
	 * @param patterns
	 * @param instances
	 * @return
	 */
	public static Instances augmentDataset(List<Pattern> patterns, Instances instances) {
		Instances copy = new Instances(instances);
		
		int newAttrStart = copy.numAttributes() - 1;
		
		// insert new attributes(patterns)
		for (Pattern pattern : patterns) {
			copy.insertAttributeAt(new Attribute(pattern.pName()), copy.numAttributes()-1);
		}
		// insert corresponding attribute values(pattern values)
		for (int i = 0; i < copy.numInstances(); i++) {
			Instance ins = copy.instance(i);
			int index = newAttrStart;
			for (Pattern pattern : patterns) {
				System.out.println(pattern.pName() + "||" + pattern.pValue());
				ins.setValue(index++, pattern.pValue());
			}
		}
		return copy;
	}
	
}
