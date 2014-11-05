package meta.pattern.globalEval;

import java.util.List;

import meta.entity.CosinePattern;
import meta.entity.Pattern;
import meta.util.loader.InstanceLoader;
import meta.util.loader.PatternLoader;
import weka.core.Instances;

public class AbstractGlobalEval {
	
	
	protected Instances data;
	protected List<Pattern> pats;	
	protected List<CosinePattern> cosinePats;
	
	
	public AbstractGlobalEval() {
		try {
			data = InstanceLoader.loadInstances();
			pats = PatternLoader.loadPattern(data);
			cosinePats = PatternLoader.loadCosinePattern(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
