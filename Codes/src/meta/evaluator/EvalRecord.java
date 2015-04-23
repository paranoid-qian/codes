package meta.evaluator;

import java.util.ArrayList;
import java.util.List;

public class EvalRecord {
	
	private List<EvalTriple> record;
	private EvalTriple max;
	
	
	public EvalRecord() {
		this.record = new ArrayList<EvalTriple>();
	}
	
	/**
	 * output the triple with max f-measure
	 * @return EvalTriple
	 */
	public EvalTriple getMaxTriple() {
		return max;
	}
	
	public EvalTriple getAvgTriple() {
		double precision = 0;
		double recall = 0;
		double fMeasure = 0;
		for (EvalTriple evalTriple : record) {
			precision += evalTriple.getPrecision();
			recall += evalTriple.getRecall();
			fMeasure += evalTriple.getfMeasure();
		}
		int numFolds = record.size();
		precision /= numFolds;
		recall /= numFolds;
		fMeasure /= numFolds;
		
		assert(fMeasure == 2*precision*recall/(precision+recall));
		
		return new EvalTriple(precision, recall);
	}
	
	/**
	 * add evaluation triple
	 * @param precision
	 * @param recall
	 */
	public void addEval(double precision, double recall) {
		EvalTriple newTriple = new EvalTriple(precision, recall);
		this.record.add(newTriple);
		if (max == null || newTriple.getfMeasure() > max.getfMeasure()) {
			max = newTriple;
		}
	}

	
	
}
