package soc_poker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static final String src = "E:\\˹̹��snap project���ݼ�\\soc-pokec���ݼ�\\soc-pokec-profiles.txt\\soc-pokec-profiles.txt";
	
	
	public static void main(String[] args) {
		BufferedReader bReader = null;
		
		try {
			bReader = new BufferedReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		String line = null;
		while (i<50) {
			try {
				line = bReader.readLine();
				System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
		
	}
	
}

