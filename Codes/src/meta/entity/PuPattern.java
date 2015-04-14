package meta.entity;


public class PuPattern extends Pattern {
	
	// pattern在L1上的support值
	private int suppL1 = 0;
	// pattern在L-L1上的support值
	private int suppLOther = 0;
	// pattern在U上的support值
	private int suppD = 0;
	
	// D(x)值
	private double dx = 0;

	
	// increment操作
	public void incrSuppD() {
		this.suppD++;
	}
	public void incrSuppL1() {
		this.suppL1++;
	}
	public void incrSuppOther() {
		this.suppLOther++;
	}
	
	// reset操作
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

}
