package com.highbeauty.sql.spring.builder;

public class StrEx {
	public static String upperFirst(String s) {
		int len = s.length();
		if (len <= 0)
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append(s.substring(0, 1).toUpperCase());
		sb.append(s.substring(1, len));

		return sb.toString();
	}
	
	public static String pkg2Path(String pkg){
		return pkg.replaceAll("\\.", "/");
	}
	
	public static void main(String[] args) throws Exception{
		String s = "cn.yahoo.games";
		
		System.out.println(pkg2Path(s));
	}
}
