import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class A {
	
	public static void main(String[] args) {
		String s = "2001-12-16  cutomer: A11NCO6YTE4BTJ  rating: 5  votes:   5  helpful:   4";
		String id = s.trim().split(" +")[4];
		System.out.println(id.trim());
		// 0, 2, 4, 6, 8
	}
}
