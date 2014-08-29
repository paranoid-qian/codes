package snap_amazon_meta;

public interface Constant {
	
	// import related
	public static final String SOURCE = "E:\\斯坦福snap project数据集\\amazon-meta.txt\\amazon-meta.txt";
	public static final String ITEM_DEST = "E:\\斯坦福snap project数据集\\item.txt";
	public static final String CAT_DEST = "E:\\斯坦福snap project数据集\\cat.txt";
	public static final String REVIEW_DEST = "E:\\斯坦福snap project数据集\\review.txt";
	public static final int IGNORE = 7; // 头7行（描述性信息和id=0的product）忽略
	
	
	// serialize related
	public static final String SERI_PATH = "E:\\斯坦福snap project数据集\\seri\\";
	public static final String SERI_POSTFIX = ".serialize.dat";
	
	
	// aggregator
	public static final String AGGOUTPUT_FOLDER = "E:\\斯坦福snap project数据集\\result\\";
	
	
	// salesrank
	public static final int maxRank = 3798351;
	public static final int minRank = -1;
	public static final int rankStep = 10000;
	
	
	//others 
	public static final String DOT = ".";
	public static final String SP = "|";
}
