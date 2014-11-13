package meta.entity;


public class PuPattern extends Pattern {
	
	// pattern在标记的test数据集L1上的support值
	private int suppL1 = 0;
	
	// pattern在标记的test数据集L1上的support值
	private int suppL0 = 0;
	
	// pattern在未标记的test数据集U上的support值
	private int suppU = 0;

	
	public void incrSuppU() {
		this.suppU++;
	}
	public void incrSuppL0(){
		this.suppL0++;
	}
	public void incrSuppL1() {
		this.suppL1++;
	}
	
	public void resetSupp(){
		this.suppL0=0;
		this.suppL1=0;
		this.suppU=0;
	}
	
	
	public int getSuppU() {
		return suppU;
	}

	public void setSuppU(int suppU) {
		this.suppU = suppU;
	}
	
	public int getSuppL1() {
		return suppL1;
	}

	public void setSuppL1(int suppL1) {
		this.suppL1 = suppL1;
	}

	public int getSuppL0() {
		return suppL0;
	}

	public void setSuppL0(int suppL0) {
		this.suppL0 = suppL0;
	}

}
