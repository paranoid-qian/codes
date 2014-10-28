package meta.entity;

public class CosinePattern extends Pattern {
	
	/* pattern information */
	private Pattern pattern;
	
	/* pattern cosine value */
	private double cosine;
	
	/**
	 * constructor
	 */
	public CosinePattern() {
		
	}
	
	public void setCosine(double cosine) {
		this.cosine = cosine;
	}
	public double getCosine() {
		return this.cosine;
	}
	
}
