package snap_amazon_meta;

public interface Constant {
	
	// import related
	public static final String SOURCE = "E:\\˹̹��snap project���ݼ�\\amazon-meta.txt\\amazon-meta.txt";
	public static final String ITEM_DEST = "E:\\˹̹��snap project���ݼ�\\item.txt";
	public static final String CAT_DEST = "E:\\˹̹��snap project���ݼ�\\cat.txt";
	public static final String REVIEW_DEST = "E:\\˹̹��snap project���ݼ�\\review.txt";
	public static final int IGNORE = 7; // ͷ7�У���������Ϣ��id=0��product������
	
	
	// serialize related
	public static final String SERI_PATH = "E:\\˹̹��snap project���ݼ�\\seri\\";
	public static final String SERI_POSTFIX = ".serialize.dat";
	
	
	// aggregator
	public static final String AGGOUTPUT_FOLDER = "E:\\˹̹��snap project���ݼ�\\result\\";
	
	
	// salesrank
	public static final int maxRank = 3798351;
	public static final int minRank = -1;
	public static final int rankStep = 10000;
	
	
	//others 
	public static final String DOT = ".";
	public static final String SP = "|";
}
