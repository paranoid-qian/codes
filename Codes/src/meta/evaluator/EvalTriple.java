package meta.evaluator;

public class EvalTriple {
	
	private double precision;
	private double recall;
	private double fMeasure;
	
	public EvalTriple(double precision, double recall) {
		this.precision = precision;
		this.recall = recall;
		this.fMeasure = 2 * precision * recall / (precision + recall);
	}
	
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public double getfMeasure() {
		return fMeasure;
	}
	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
	
	
	
}
