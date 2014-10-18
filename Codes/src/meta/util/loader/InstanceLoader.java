package meta.util.loader;

import java.io.File;
import java.io.IOException;

import meta.attributes.AttributeIndexs;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class InstanceLoader {

	/**
	 * load instances
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Instances loadInstances(String path) throws IOException {
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(path));
		Instances newInss = loader.getDataSet();
		newInss.setClassIndex(newInss.numAttributes()-1);
		
		loadAttrIndexs(newInss); // load attribute indexs for latter usage
		
		return newInss;
	}
	
	private static void loadAttrIndexs(Instances inss) {
		for (int i = 0; i < inss.numAttributes(); i++) {
			AttributeIndexs.add(inss.attribute(i).name(), i);
		}
	}
}
