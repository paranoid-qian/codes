package meta.classifier;


/**
 * SingleFeatureClassifier depends on PuClassifier's filtered patterns' items,
 * so, this class instance MUST be called after PuClassifier instance
 * @author paranoid.Q
 * @date Apr 27, 2015 4:12:03 PM
 */
public class SingleFeatureClassifier implements IClassifier {
	
	private EvalResource resource;
	
	public SingleFeatureClassifier(EvalResource resource) {
	    this.resource = resource;
	}
	
	@Override
	public void evaluate() throws Exception {
	    
	}
	
	
}
