package meta.util.loader;

import meta.entity.AttributeIndexs;
import weka.core.Instances;

public class AttributeLoader {
	
	public static void loadAttrIndexs(Instances inss) {
		for (int i = 0; i < inss.numAttributes(); i++) {
			AttributeIndexs.add(inss.attribute(i).name(), i);
		}
	}
}
