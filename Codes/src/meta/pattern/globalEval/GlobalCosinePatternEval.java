package meta.pattern.globalEval;

import java.io.IOException;
import java.util.List;

import weka.core.Instances;
import meta.entity.CosinePattern;
import meta.entity.Pattern;
import meta.pattern.IgFilter;
import meta.transaction.TransactionAug;

public class GlobalCosinePatternEval extends AbstractGlobalEval {

	
	public GlobalCosinePatternEval() {
		super();
	}
	
	/**
	 * for理论推导，在全局dataset上计算出cosine pattern的information gain值，并降序排列输出
	 */
	public void getGlobalSortedPatterns() {
		Instances inss = new Instances(data);
		
		// filter pattern
		List<Pattern> augPatterns = null;
		try {
			 augPatterns = IgFilter.filter(inss, inss, cosinePats, 8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			inss = TransactionAug.augmentDataset(augPatterns, inss);
			//System.out.println(inss);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*for (Pattern pattern : augPatterns) {
			System.out.println(pattern.pName() + "\t\t\t" + pattern.getGlobalSupport() + "\t\t\t" + ((CosinePattern)pattern).getCosine() + "\t\t\t" + pattern.getIg());
		}*/
	}
	
	
	public static void main(String[] args) {
		GlobalCosinePatternEval gcpEval = new GlobalCosinePatternEval();
		gcpEval.getGlobalSortedPatterns();
	}
}
