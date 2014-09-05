package snap_amazon_meta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {
	
	public static void processGroup_SalesRank() {
		String[] itemAttrs = {"group"};
		// ����filename
		String fileName = "group.salesrank_step("+ Constant.rankStep +").dat";
		try {
			List<String> list = Arrays.asList(itemAttrs);
			
			//��ȡattr����ȡֵ
			List<List<String>> attrsValues = AttrValueUtil.getAttrsValues(DbUtil.ITEM_TABLE, list); // һ��attr��Ӧһ��inner list
			List<String> salesValues = Aggregator.genSalesRankValues(DbUtil.ITEM_TABLE, "salesrank", Constant.minRank, Constant.maxRank, Constant.rankStep);
			attrsValues.add(salesValues);
			
			// ���
			List<String> attrValueComposition = Aggregator.compositeAttrValue(attrsValues); // ��Ϻ���["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
			
			Aggregator.agg(attrValueComposition, DbUtil.ITEM_TABLE, fileName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void processGroup() {
		String[] itemAttrs = {"group"};
		// ����filename
		String fileName = "group.dat";
		try {
			List<String> list = Arrays.asList(itemAttrs);
			//��ȡattr����ȡֵ
			List<List<String>> attrsValues = AttrValueUtil.getAttrsValues(DbUtil.ITEM_TABLE, list); // һ��attr��Ӧһ��inner list
			// ���
			List<String> attrValueComposition = Aggregator.compositeAttrValue(attrsValues); // ��Ϻ���["`item`.`attr1`='1' AND `attr2`='1'", "`attr1`='2' AND `attr2`='3', ..."]
			Aggregator.agg(attrValueComposition, DbUtil.ITEM_TABLE, fileName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	// test
	public static void main(String[] args) {
		
		processGroup_SalesRank();
		
	}
	
}