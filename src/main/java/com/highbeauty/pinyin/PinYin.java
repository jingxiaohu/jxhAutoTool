package com.highbeauty.pinyin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PinYin {

	private static PinYin py = null;
	private static Properties p = null;

	private PinYin() {
		try {
			if (p == null) {
				InputStream is = getClass().getResourceAsStream("/pinyin.properties");
				InputStreamReader reader = new InputStreamReader(is, "UTF-8");
				p = new Properties();
				p.load(reader);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getPy(String s) {
		if(p == null || py == null){
			py = new PinYin();
		}
		List<String> ret = new ArrayList<String>();
		int len = s.length();
		for (int n = 0; n < len; n++) {
			String k = s.substring(n, n + 1);
			String v = (String) p.get(k);
			v = v == null ? k : v;
			ret.add(v);
		}
		return ret;
	}


	public static List<String> getShortPy(String s){
		List<String> r = getPy(s);

		List<String> ret = new ArrayList<String>();
		for (String v : r) {
			String v1 = v.substring(0, 1);
			ret.add(v1);
		}
		return ret;
	}


	public static String getPinYin(String s){
		if(p == null || py == null){
			py = new PinYin();
		}
		StringBuffer ret = new StringBuffer();
		int len = s.length();
		for (int n = 0; n < len; n++) {
			String k = s.substring(n, n + 1);
			String v = (String) p.get(k);
			if(v == null)
				ret.append(k);
			else{
				if(ret.length() > 0 &&ret.charAt(ret.length() - 1) !=  ' '){
					ret.append(' ');
				}
				ret.append(v);
			}
		}
		return ret.toString();
	}

	public static String getShortPinYin(String s){
		if(p == null || py == null){
			py = new PinYin();
		}
		StringBuffer ret = new StringBuffer();
		int len = s.length();
		for (int n = 0; n < len; n++) {
			String k = s.substring(n, n + 1);
			String v = (String) p.get(k);
			if(v == null)
				ret.append(k);
			else{
				v = v.substring(0, 1);
				ret.append(v);
			}
		}
		return ret.toString();
	}

	public static void main(String[] args) {
		List<String> z = PinYin.getPy("中文从类继承的字段");

		System.out.println(z);

		z = PinYin.getShortPy("中文从类继承的字段");
		System.out.println(z);

		String s = PinYin.getPinYin("中文从类继承的字段");
		System.out.println(s);

		s = PinYin.getShortPinYin("中文从类继承的字段");
		System.out.println(s);

	}
}
