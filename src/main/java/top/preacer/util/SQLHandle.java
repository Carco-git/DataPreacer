package top.preacer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLHandle {
	public static void main(String[] args) {
		System.out.println(canonicalSQL("select  * \n from   db1"));
	}
	  public static String canonicalSQL(String str) {
			String result = "";
			if (str != null) {
				Pattern p = Pattern.compile("sql");//如(\r?\n(\\s*\r?\n)+)
				Matcher m = p.matcher(str);
				result = m.replaceAll("\r\n");//多个换行变成一个换行
				result = result.replaceAll( "\\s+", " " );//多个空格变为一个
			}
			return result;
		}
}
