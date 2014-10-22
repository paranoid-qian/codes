import java.io.UnsupportedEncodingException;


public class A {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "2001-12-16  cutomer: A11NCO6YTE4BTJ  rating: 5  votes:   5  helpful:   4";
		byte[] b = s.getBytes("UTF-8");
		for (byte c : b) {
			System.out.print(Integer.toBinaryString((char)c) + " ");
		}
	}
}
