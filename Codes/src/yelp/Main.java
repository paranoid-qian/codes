package yelp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	public static final String src = "E:\\yelpÊý¾Ý¼¯\\yelp_dataset_challenge_academic_dataset\\yelp_dataset_challenge_academic_dataset";
	
	
	public static void main(String[] args) {
		BufferedReader bReader = null;
		
		try {
			bReader = new BufferedReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (bReader != null) {
			try {
				String line = bReader.readLine();
				line = bReader.readLine();
				JSONObject jo = new JSONObject(line);
				System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
