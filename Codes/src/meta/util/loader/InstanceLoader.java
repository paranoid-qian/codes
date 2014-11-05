package meta.util.loader;

import java.io.File;

import meta.entity.AttributeIndexs;
import meta.util.constants.Constant;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class InstanceLoader {
	
	private static Instances inss = null;
	
	/**
	 * load instances
	 * @param path
	 * @return
	 * @throws Exception 
	 */
	public static Instances loadInstances() throws Exception {
		if (inss != null) {
			return inss;
		}
		
		// initial load
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(Constant.DATASET_ARFF));
		Instances newInss = loader.getDataSet();
		newInss.setClassIndex(newInss.numAttributes()-1);
		
		loadAttrIndexs(newInss); // load attribute indexs for latter usage
		
		// Discretize number attributes
		Discretize filter = new Discretize();
		filter.setInputFormat(newInss);
		newInss = Filter.useFilter(newInss, filter);
		
		inss = newInss;
		return newInss;
	}
	
	private static void loadAttrIndexs(Instances inss) {
		for (int i = 0; i < inss.numAttributes(); i++) {
			AttributeIndexs.add(inss.attribute(i).name(), i);
		}
	}
}
