package meta.util.loader;

import java.io.File;

import meta.util.constants.Constant;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class InstanceLoader {
	
	/**
	 * Load instances
	 * @return 
	 * @throws Exception 
	 */
	public static Instances loadInstances() throws Exception {
		// initial load
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(Constant.DATASET_ARFF));
		Instances newInss = loader.getDataSet();
		newInss.setClassIndex(newInss.numAttributes()-1);
		
		// Discretize number attributes
		Discretize filter = new Discretize();
		filter.setInputFormat(newInss);
		newInss = Filter.useFilter(newInss, filter);
		
		return newInss;
	}
	
	/**
	 * Load instances
	 * @return 
	 * @throws Exception 
	 */
	public static Instances loadInstances(String path) throws Exception {
		// initial load
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(path));
		Instances newInss = loader.getDataSet();
		newInss.setClassIndex(newInss.numAttributes()-1);
		
//		// Discretize number attributes
//		Discretize filter = new Discretize();
//		filter.setInputFormat(newInss);
//		newInss = Filter.useFilter(newInss, filter);
		
		return newInss;
	}
	
}
