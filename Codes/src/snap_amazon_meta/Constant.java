package snap_amazon_meta;

public interface Constant {
	
	// import related
	public static final String SOURCE = "E:\\斯坦福snap project数据集\\amazon-meta.txt\\sample.txt";
	public static final int IGNORE = 7; // 头7行（描述性信息和id=0的product）忽略
	
	
	// serialize related
	public static final String SERI_PATH = "E:\\斯坦福snap project数据集\\amazon-meta.txt\\";
	public static final String SERI_POSTFIX = ".serialize.dat";
	
	
	// aggregator
	public static final String AGGOUTPUT_FOLDER = "E:\\斯坦福snap project数据集\\result\\";
	
	
	//others 
	public static final String DOT = ".";
}
