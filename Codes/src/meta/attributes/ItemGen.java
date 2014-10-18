package meta.attributes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import meta.util.DbUtil;
import meta.util.constants.Constant;


/**
 * 生成attr-val对，并把对写入到文件中
 * @author paranoid
 *
 */
public class ItemGen {
	
	private static int incrementId = 0;
	private static BufferedWriter bWriter = null;
	private static PreparedStatement ps = null;
	
	/**
	 * 离散属性的分析处理，形成value-id对
	 * @param table
	 * @param attr
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void genDiscreteAttrPairs(String table, String attr) throws SQLException, IOException {
		bWriter = new BufferedWriter(new FileWriter(new File(Constant.ITEMS_FOLDER + attr + Constant.ITEM_FILE_POSTFIX), true)); // append mode
		
		Connection connection = DbUtil.openConn(Constant.DB_NAME);
		String sql = "SELECT `"+ attr +"` FROM `"+ table +"` WHERE `"+ attr+ "` is not null GROUP BY `"+ attr +"`;";
		ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if (rs.first()) {
			List<String> list = new ArrayList<String>();
			do {
				String val = rs.getString(attr);
				if (val.equals("?")) {
					continue;
				}
				list.add(val);
			} while (rs.next());
			for (int i = 0; i < list.size(); i++) {
				bWriter.write(list.get(i) + Constant.SP + incrementId++);
				bWriter.newLine();
			}
			
		}
		System.out.println("success write attr-val-id tuples for " + attr);
		System.out.println("next id = " + incrementId);
		bWriter.flush();
		
		bWriter.close();
		ps.close();
		DbUtil.closeConn(connection);
	}
	
	
	public static void main(String[] args) {
		try {
			for (String attr : Constant.DB_TABLE.columns()) {
				genDiscreteAttrPairs(Constant.DB_TABLE.tableName(), attr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
