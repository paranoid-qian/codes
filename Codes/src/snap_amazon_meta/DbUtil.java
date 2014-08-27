package snap_amazon_meta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
	
	public static final String CONN_URL = "jdbc:mysql://localhost:3306/snap_amazon_meta?useUnicode=true&amp;characterEncoding=UTF-8";
	public static final String USERNAME = "root";
	public static final String PASSWORD	= "root";
	
	public static Connection openConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("success load mysql driver");
			Connection connection = DriverManager.getConnection(CONN_URL, USERNAME, PASSWORD);
			return connection;
		} catch (Exception e) {
			System.out.println("failed load mysql driver");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void closeConn(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
