package meta.pattern.globalEval;

import java.io.IOException;
import java.util.List;

import meta.entity.CosinePattern;
import meta.entity.Pattern;
import meta.util.constants.Constant;
import meta.util.loader.InstanceLoader;
import meta.util.loader.PatternLoader;

import weka.core.Instances;

public class AbstractGlobalEval {
	
	
	protected Instances data;
	protected List<Pattern> pats;	
	protected List<CosinePattern> cosinePats;
	
	
	public AbstractGlobalEval() {
		try {
			data = InstanceLoader.loadInstances(Constant.DATASET_ARFF);
			pats = PatternLoader.loadPattern();
			cosinePats = PatternLoader.loadCosinePattern();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
