package meta.transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TransactionFormulator {
	
	private static final String TRANSACTION_DEST = "E:\\斯坦福snap project数据集\\transaction\\transaction.txt";
	private static final String ITEMS_DEST = "E:\\斯坦福snap project数据集\\transaction\\transaction-items2.txt";
	private static BufferedReader bReader = null;
	private static BufferedWriter bWriter = null;
	static {
		try {
			bReader = new BufferedReader(new FileReader(TRANSACTION_DEST));
			bWriter = new BufferedWriter(new FileWriter(ITEMS_DEST, true)); // append mode
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	
	
	public static void calculateItems() throws IOException {
		String line = null;
		do {
			line = bReader.readLine();
			if (line == null || line.equals("") || line.equals(" ")) {
				break;
			}
			
			String[] sp = line.split(" ");
			for (String item : sp) {
				bWriter.write(item);
				bWriter.newLine();
			}
		} while (true);
		
		bWriter.flush();
	}
	
	public static void main(String[] args) {
		try {
			calculateItems();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public static void calculateItems_0() throws IOException {
		String line = null;
		do {
			line = bReader.readLine();
			if (line == null || line.equals("") || line.equals(" ")) {
				break;
			}
			
			String[] sp = line.split(" ");
			for (String item : sp) {
				if (map.containsKey(item)) {
					map.put(item, map.get(item) + 1);
				} else {
					map.put(item, 1);
				}
			}
		} while (true);
		for (Entry<String, Integer> en : map.entrySet()) {
			bWriter.write(en.getKey());
			bWriter.write(" ");
			bWriter.write(Integer.toString(en.getValue()));
			bWriter.newLine();
		}
		bWriter.flush();
	}*/
}
