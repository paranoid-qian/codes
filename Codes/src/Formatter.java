import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.sound.sampled.Line;

import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;


public class Formatter {
		
	public static void main(String[] args) {
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("E:\\斯坦福snap project数据集\\amazon-meta.txt\\amazon-meta.txt"));
			String line = null;
			
			int count = 0;
			BufferedWriter writer = new BufferedWriter(new FileWriter(new java.io.File("F:\\out9.csv"), true)); // append
			
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			while (true) {
				line = reader.readLine();
//				if(line == null || line.equals("") || line.equals(" "))
//					break;
				
				//line = reader.readLine();
				/*String items[] = line.split(",");
				String id = items[0];
				long stamp = Long.parseLong(items[1]);
				String zone = items[2];
				
				
				
				Date date = new Date(stamp * 1000);
				
				format.setTimeZone(TimeZone.getTimeZone(zone));
				String formated = format.format(date);
				
				System.out.println(count++);
				
				writer.write(id + "," + formated);
				writer.newLine();
				writer.flush();*/
				
				System.out.println(line);
				count++;
				
			}
			
			//System.out.println("处理记录数：" + count);
//			reader.close();
//			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date = new Date(1329186625L * 1000);
		format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String formated = format.format(date);
		System.out.println(formated);*/
		
	}
}
