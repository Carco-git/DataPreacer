package top.preacer;


import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		List<String> a = new ArrayList<String>();
		a.add("a");
		a.add(null);
		a.add("\\null");
		System.out.println(a);
	}
}
