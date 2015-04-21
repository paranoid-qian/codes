package meta.entity;


public class PuPattern extends Pattern {
	
	// pattern��L1�ϵ�supportֵ
	private int suppL1 = 0;
	// pattern��L-L1�ϵ�supportֵ
	private int suppLOther = 0;
	// pattern��U�ϵ�supportֵ
	private int suppD = 0;
	
	private int coveredU = 0;
	
	// D(x)ֵ
	private double dx = 0;
	private double nL1 = 0;
	private double idf = 0;

	
	// pattern�������
	private double fromClass = 0;
	
	
	// increment����
	public void incrSuppD() {
		this.suppD++;
	}
	public void incrSuppL1() {
		this.suppL1++;
	}
	public void incrSuppOther() {
		this.suppLOther++;
	}
	public void incrCoveredU() {
		this.coveredU++;
	}
	
	// reset����
	public void resetSupp(){
		this.suppL1 = 0;
		this.suppLOther = 0;
		this.suppD = 0;
	}
	
	
	public int getSuppD() {
		return suppD;
	}

	public void setSuppD(int suppD) {
		this.suppD = suppD;
	}
	
	public int getSuppL1() {
		return suppL1;
	}

	public void setSuppL1(int suppL1) {
		this.suppL1 = suppL1;
	}
	public double getDx() {
		return dx;
	}
	public void setDx(double dx) {
		this.dx = dx;
	}
	public int getSuppLOther() {
		return suppLOther;
	}
	public void setSuppLOther(int suppLOther) {
		this.suppLOther = suppLOther;
	}
	public int getCoveredU() {
		return coveredU;
	}
	public void setCoveredU(int coveredU) {
		this.coveredU = coveredU;
	}
	public double getnL1() {
		return nL1;
	}
	public void setnL1(double nL1) {
		this.nL1 = nL1;
	}
	public double getIdf() {
		return idf;
	}
	public void setIdf(double idf) {
		this.idf = idf;
	}
	public double getFromClass() {
		return fromClass;
	}
	public void setFromClass(double fromClass) {
		this.fromClass = fromClass;
	}

}
