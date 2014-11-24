package meta.classifier;

import weka.core.Instances;

public class InstancesSplitter {
	
	
	public static Instances splitCV(Instances inss, int percentage, int fold) {
		Instances train = null;
		
		
		return train;
	}
	
	
	private static void copyInstances(Instances dest, Instances src, int from, int num) {
		for (int i = from; i < num; i++) {
			dest.add(src.instance(i));
		}
	}
	
}
